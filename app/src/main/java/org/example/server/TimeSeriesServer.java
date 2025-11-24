package org.example.server;

import org.example.domain.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * Servidor principal de séries temporais
 * Gerencia múltiplas séries temporais e autenticação de clientes
 * Usa ReentrantReadWriteLock para sincronização eficiente
 */
public class TimeSeriesServer {
    private static final int DEFAULT_PORT = 5000;
    private static final int THREAD_POOL_SIZE = 10;
    
    private final int port;
    private final int maxDays;
    private final int maxSeriesInMemory;
    private volatile int currentDay;
    
    // Sincronização de séries temporais
    private final ReentrantReadWriteLock seriesLock = new ReentrantReadWriteLock();
    private final Map<Integer, TimeSeries> timeSeriesMap = new LinkedHashMap<>();
    private final TimeSeries currentSeries;
    
    // Autenticação
    private final ReentrantReadWriteLock authLock = new ReentrantReadWriteLock();
    private final Map<String, User> users = new ConcurrentHashMap<>();
    
    // Persistência
    private final File dataDir;
    private final File usersFile;
    
    // Thread pool para aceitar conexões
    private final ExecutorService connectionExecutor;
    private volatile boolean running = false;
    
    // Notificações
    private final NotificationManager notificationManager = new NotificationManager();
    
    public TimeSeriesServer(int port, int maxDays, int maxSeriesInMemory) {
        this.port = port;
        this.maxDays = maxDays;
        this.maxSeriesInMemory = maxSeriesInMemory;
        this.currentDay = 0;
        this.currentSeries = new TimeSeries(0);
        this.dataDir = new File("data");
        this.usersFile = new File(dataDir, "users.dat");
        loadUsers();
        this.connectionExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        
        // Adicionar série do dia 0
        seriesLock.writeLock().lock();
        try {
            timeSeriesMap.put(0, currentSeries);
        } finally {
            seriesLock.writeLock().unlock();
        }
    }

    private synchronized void persistUsers() {
        try {
            dataDir.mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(usersFile))) {
                for (Map.Entry<String, User> e : users.entrySet()) {
                    pw.println(e.getKey() + ":" + e.getValue().getPasswordHash());
                }
            }
        } catch (IOException ex) {
            System.err.println("Erro ao persistir utilizadores: " + ex.getMessage());
        }
    }

    // Load users from disk into users map
    private synchronized void loadUsers() {
        if (!usersFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    users.put(parts[0], new User(parts[0], parts[1]));
                }
            }
        } catch (IOException ex) {
            System.err.println("Erro ao carregar utilizadores: " + ex.getMessage());
        }
    }
    
    public void start() throws IOException {
        running = true;
        dataDir.mkdirs();
        
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado na porta " + port);
        
        try {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                connectionExecutor.execute(new ClientHandler(this, clientSocket));
            }
        } finally {
            serverSocket.close();
            connectionExecutor.shutdown();
        }
    }
    
    /**
     * Registra novo utilizador
     */
    public boolean registerUser(String username, String password) {
        authLock.writeLock().lock();
        try {
            if (users.containsKey(username)) {
                return false;
            }
            String passwordHash = User.hashPassword(password);
            users.put(username, new User(username, passwordHash));
            persistUsers();
            return true;
        } finally {
            authLock.writeLock().unlock();
        }
    }
    
    /**
     * Autentica utilizador
     */
    public boolean authenticateUser(String username, String password) {
        authLock.readLock().lock();
        try {
            User user = users.get(username);
            if (user == null) return false;
            String passwordHash = User.hashPassword(password);
            return user.getPasswordHash().equals(passwordHash);
        } finally {
            authLock.readLock().unlock();
        }
    }
    
    /**
     * Adiciona evento ao dia atual e notifica listeners
     */
    public void addEvent(String productName, long quantity, double price) {
        Event event = new Event(productName, quantity, price, System.currentTimeMillis());
        currentSeries.addEvent(event);
        
        notificationManager.recordSale(productName);
    }
    
    /**
     * Avança para próximo dia e reseta notificações
     */
    public void nextDay() {
        seriesLock.writeLock().lock();
        try {
            currentDay++;
            notificationManager.reset();  // Reset de notificações para novo dia
            
            TimeSeries newSeries = new TimeSeries(currentDay);
            timeSeriesMap.put(currentDay, newSeries);
            
            if (timeSeriesMap.size() > maxSeriesInMemory) {
                Integer oldestDay = timeSeriesMap.keySet().iterator().next();
                TimeSeries oldSeries = timeSeriesMap.remove(oldestDay);
                
                try {
                    persistSeries(oldestDay, oldSeries);
                } catch (IOException e) {
                    System.err.println("Erro ao persistir série: " + e.getMessage());
                }
            }
        } finally {
            seriesLock.writeLock().unlock();
        }
    }
    
    /**
     * Obtém série temporal para um dia específico
     */
    public TimeSeries getTimeSeries(int day) {
        seriesLock.readLock().lock();
        try {
            if (timeSeriesMap.containsKey(day)) {
                return timeSeriesMap.get(day);
            }
        } finally {
            seriesLock.readLock().unlock();
        }
        
        // Tentar carregar do disco
        try {
            return loadSeries(day);
        } catch (IOException e) {
            System.err.println("Erro ao carregar série: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Persiste série temporal para disco
     */
    private void persistSeries(int day, TimeSeries series) throws IOException {
        File file = new File(dataDir, "series_" + day + ".dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeInt(day);
            List<Event> events = series.getAllEvents();
            oos.writeInt(events.size());
            for (Event event : events) {
                oos.writeObject(event);
            }
        }
    }
    
    /**
     * Carrega série temporal do disco
     */
    private TimeSeries loadSeries(int day) throws IOException {
        File file = new File(dataDir, "series_" + day + ".dat");
        if (!file.exists()) return null;
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            int loadedDay = ois.readInt();
            TimeSeries series = new TimeSeries(loadedDay);
            int eventCount = ois.readInt();
            
            for (int i = 0; i < eventCount; i++) {
                Event event = (Event) ois.readObject();
                series.addEvent(event);
            }
            
            return series;
        } catch (ClassNotFoundException e) {
            throw new IOException("Erro ao desserializar eventos", e);
        }
    }
    
    public int getCurrentDay() { return currentDay; }
    public int getMaxDays() { return maxDays; }
    public TimeSeries getCurrentSeries() { return currentSeries; }
    public NotificationManager getNotificationManager() { return notificationManager; }
}
