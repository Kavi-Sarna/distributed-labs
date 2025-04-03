import java.math.BigInteger;
import java.rmi.Naming;

/**
 * BumperClient connects to the Bumper service and calls
 * the bump() method 10,000 times, then displays statistics.
 */
public class BumperClient {

    public static void main(String[] args) {
        try {
            // Look up the remote object from the RMI registry
            Bumper bumper = (Bumper) Naming.lookup("//localhost/bumper");
            System.out.println("Connected to Bumper service");

            // Initialize counters
            BigInteger ctr = BigInteger.ZERO;
            BigInteger n = new BigInteger("10000");

            // Start timing
            long start = System.currentTimeMillis();

            // Loop until ctr equals n
            while (ctr.compareTo(n) < 0) {

                // Increment local counter
                ctr = ctr.add(BigInteger.ONE);

                // Call remote bump method
                bumper.bump();
            }

            // End timing
            long stop = System.currentTimeMillis();

            // Get final value from server
            BigInteger finalValue = bumper.get();

            // Calculate elapsed time in seconds
            double elapsedTime = (stop - start) / 1000.0;

            // Display results
            System.out.println("Final server counter value: " + finalValue);
            System.out.println("Time taken to call bump() 10,000 times: " + elapsedTime + " seconds");

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
