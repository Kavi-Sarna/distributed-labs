import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * BumperServer creates and registers the Bumper service
 * with the RMI registry.
 */
public class BumperServer {

    public static void main(String[] args) {
        try {
            // Create the service implementation
            BumperImpl bumpService = new BumperImpl();

            // Start the RMI registry on the default port (1099)
            try {
                LocateRegistry.createRegistry(1099);
                System.out.println("RMI registry created.");

            } catch (Exception e) {
                System.out.println("RMI registry already exists.");
            }

            // Bind the remote object's stub in the registry with the name "bumper"
            Naming.rebind("//localhost/bumper", bumpService);

            System.out.println("Bumper service registered. Ready to serve requests...");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
