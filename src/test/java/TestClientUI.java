import client.TimeSeriesClient;
import java.util.*;

/**
 * Interface de teste e demonstração
 * Permite interagir com servidor para pequenos testes
 */
public class TestClientUI {
    private TimeSeriesClient client;
    private Scanner scanner;
    
    public TestClientUI(String host, int port) {
        this.client = new TimeSeriesClient(host, port);
        this.scanner = new Scanner(System.in);
    }
    
    public void start() throws Exception {
        client.connect();
        System.out.println("Conectado ao servidor");
        
        boolean running = true;
        while (running) {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1. Registar");
            System.out.println("2. Login");
            System.out.println("3. Adicionar evento");
            System.out.println("4. Próximo dia");
            System.out.println("5. Consultar quantidade");
            System.out.println("6. Consultar volume");
            System.out.println("7. Consultar preços");
            System.out.println("8. Listar eventos");
            System.out.println("9. Sair");
            System.out.print("Escolha: ");
            
            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            try {
                switch (option) {
                    case 1 -> handleRegister();
                    case 2 -> handleLogin();
                    case 3 -> handleAddEvent();
                    case 4 -> handleNextDay();
                    case 5 -> handleGetQuantity();
                    case 6 -> handleGetVolume();
                    case 7 -> handleGetPriceStats();
                    case 8 -> handleGetEvents();
                    case 9 -> running = false;
                    default -> System.out.println("Opção inválida");
                }
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
            }
        }
        
        client.disconnect();
    }
    
    private void handleRegister() throws Exception {
        System.out.print("Utilizador: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        if (client.register(username, password)) {
            System.out.println("Registo bem-sucedido!");
        } else {
            System.out.println("Erro ao registar");
        }
    }
    
    private void handleLogin() throws Exception {
        System.out.print("Utilizador: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        if (client.login(username, password)) {
            System.out.println("Login bem-sucedido!");
        } else {
            System.out.println("Erro ao fazer login");
        }
    }
    
    private void handleAddEvent() throws Exception {
        System.out.print("Produto: ");
        String product = scanner.nextLine();
        System.out.print("Quantidade: ");
        long quantity = scanner.nextLong();
        System.out.print("Preço: ");
        double price = scanner.nextDouble();
        
        client.addEvent(product, quantity, price);
        System.out.println("Evento adicionado!");
    }
    
    private void handleNextDay() throws Exception {
        int day = client.nextDay();
        System.out.println("Dia atual: " + day);
    }
    
    private void handleGetQuantity() throws Exception {
        System.out.print("Produto: ");
        String product = scanner.nextLine();
        System.out.print("Dias (lookback): ");
        int days = scanner.nextInt();
        
        long quantity = client.getQuantity(product, days);
        System.out.println("Quantidade total: " + quantity);
    }
    
    private void handleGetVolume() throws Exception {
        System.out.print("Produto: ");
        String product = scanner.nextLine();
        System.out.print("Dias (lookback): ");
        int days = scanner.nextInt();
        
        double volume = client.getVolume(product, days);
        System.out.printf("Volume total: %.2f€\n", volume);
    }
    
    private void handleGetPriceStats() throws Exception {
        System.out.print("Produto: ");
        String product = scanner.nextLine();
        System.out.print("Dias (lookback): ");
        int days = scanner.nextInt();
        
        TimeSeriesClient.PriceStats stats = client.getPriceStats(product, days);
        System.out.printf("Preço médio: %.2f€\n", stats.average);
        System.out.printf("Preço máximo: %.2f€\n", stats.maximum);
    }
    
    private void handleGetEvents() throws Exception {
        System.out.print("Dia (offset): ");
        int dayOffset = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("Produtos (separados por vírgula): ");
        String[] products = scanner.nextLine().split(",");
        Set<String> productSet = new HashSet<>(Arrays.asList(products));
        
        List<TimeSeriesClient.EventRecord> events = client.getEvents(dayOffset, productSet);
        System.out.println("\nEventos encontrados:");
        for (TimeSeriesClient.EventRecord event : events) {
            System.out.printf("  %s: %d unidades a %.2f€\n", 
                            event.productName, event.quantity, event.price);
        }
    }
    
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 5000;
        
        if (args.length > 0) host = args[0];
        if (args.length > 1) port = Integer.parseInt(args[1]);
        
        TestClientUI ui = new TestClientUI(host, port);
        ui.start();
    }
}
