import server.TimeSeriesServer;

/**
 * Programa servidor principal
 * Inicia servidor de séries temporais com parametrização
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // Parâmetros: port, maxDays, maxSeriesInMemory
        int port = 2345;
        int maxDays = 30;
        int maxSeriesInMemory = 5;
        
        if (args.length > 0) port = Integer.parseInt(args[0]);
        if (args.length > 1) maxDays = Integer.parseInt(args[1]);
        if (args.length > 2) maxSeriesInMemory = Integer.parseInt(args[2]);
        
        TimeSeriesServer server = new TimeSeriesServer(port, maxDays, maxSeriesInMemory);
        
        // Registar utilizadores de teste
        server.registerUser("us/cer1", "password1");
        server.registerUser("user2", "password2");
        server.registerUser("admin", "admin123");
        
        System.out.println("Iniciando servidor...");
        System.out.println("Porta: " + port);
        System.out.println("Dias máximos: " + maxDays);
        System.out.println("Séries em memória: " + maxSeriesInMemory);
        
        server.start();
    }
}
