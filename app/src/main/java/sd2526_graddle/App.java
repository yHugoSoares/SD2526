package sd2526_graddle;


public class App {
    public static void main(String[] args) throws Exception {
        // Delegate to the existing server main
        // If Main is in the default package and placed at app/src/main/java/Main.java:
        Main.main(args);
    }

    // Provide a local Main class so code compiles when there is no external Main class available.
    public static class Main {
        public static void main(String[] args) throws Exception {
            // TODO: replace with actual server startup logic or remove this stub if an external Main class is provided.
            System.err.println("Main.main invoked but no external Main class was found.");
        }
    }
}
