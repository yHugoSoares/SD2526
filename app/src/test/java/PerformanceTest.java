// import client.TimeSeriesClient;
// import java.util.*;
// import java.util.concurrent.*;

// /**
//  * Testes de desempenho e escalabilidade
//  */
// public class PerformanceTest {
    
//     /**
//      * Teste 1: Escalabilidade com múltiplos clientes
//      */
//     public static void testScalability() throws Exception {
//         System.out.println("\n=== Teste de Escalabilidade ===");
        
//         int[] clientCounts = {1, 5, 10, 20};
        
//         for (int numClients : clientCounts) {
//             System.out.println("\nTestando com " + numClients + " clientes...");
            
//             ExecutorService executor = Executors.newFixedThreadPool(numClients);
//             long startTime = System.currentTimeMillis();
            
//             for (int i = 0; i < numClients; i++) {
//                 final int clientId = i;
//                 executor.submit(() -> {
//                     try {
//                         TimeSeriesClient client = new TimeSeriesClient("localhost", 5000);
//                         client.connect();
//                         client.login("user1", "password1");
                        
//                         for (int j = 0; j < 100; j++) {
//                             client.addEvent("Product" + (j % 10), 1 + random(5), 10 + random(20));
//                         }
                        
//                         client.disconnect();
//                     } catch (Exception e) {
//                         System.err.println("Erro cliente " + clientId + ": " + e.getMessage());
//                     }
//                 });
//             }
            
//             executor.shutdown();
//             executor.awaitTermination(5, TimeUnit.MINUTES);
            
//             long duration = System.currentTimeMillis() - startTime;
//             System.out.printf("Tempo total: %.2f segundos\n", duration / 1000.0);
//         }
//     }
    
//     /**
//      * Teste 2: Leitura vs Escrita
//      */
//     public static void testReadWriteRatio() throws Exception {
//         System.out.println("\n=== Teste de Proporção Leitura/Escrita ===");
        
//         TimeSeriesClient client = new TimeSeriesClient("localhost", 5000);
//         client.connect();
//         client.login("user1", "password1");
        
//         // Adicionar alguns dados
//         for (int i = 0; i < 1000; i++) {
//             client.addEvent("ProductA", 5, 15.5);
//         }
        
//         // Teste de leitura
//         long startRead = System.currentTimeMillis();
//         for (int i = 0; i < 1000; i++) {
//             client.getQuantity("ProductA", 1);
//         }
//         long readTime = System.currentTimeMillis() - startRead;
        
//         // Teste de escrita
//         long startWrite = System.currentTimeMillis();
//         for (int i = 0; i < 1000; i++) {
//             client.addEvent("ProductB", 1, 10.0);
//         }
//         long writeTime = System.currentTimeMillis() - startWrite;
        
//         System.out.printf("Tempo de leitura (1000 ops): %.2f ms\n", readTime);
//         System.out.printf("Tempo de escrita (1000 ops): %.2f ms\n", writeTime);
//         System.out.printf("Proporção (R/W): %.2f\n", (double) readTime / writeTime);
        
//         client.disconnect();
//     }
    
//     /**
//      * Teste 3: Robustez (cliente não consome respostas)
//      */
//     public static void testRobustness() throws Exception {
//         System.out.println("\n=== Teste de Robustez ===");
        
//         // Criar cliente que não processa todas as respostas
//         TimeSeriesClient slowClient = new TimeSeriesClient("localhost", 5000);
//         slowClient.connect();
//         slowClient.login("user1", "password1");
        
//         // Enviar múltiplos pedidos sem ler respostas
//         System.out.println("Enviando pedidos sem ler respostas...");
//         for (int i = 0; i < 10; i++) {
//             slowClient.addEvent("ProductC", 1, 20.0);
//             Thread.sleep(100);
//         }
        
//         System.out.println("Servidor continuou a funcionar (sem deadlock)");
//         slowClient.disconnect();
//     }
    
//     private static int random(int max) {
//         return new Random().nextInt(max);
//     }
    
//     public static void main(String[] args) throws Exception {
//         try {
//             testScalability();
//             testReadWriteRatio();
//             testRobustness();
            
//             System.out.println("\n=== Todos os testes completados ===");
//         } catch (Exception e) {
//             System.err.println("Erro nos testes: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }
// }
