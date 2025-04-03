import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * BumperImpl is the servant class that implements the Bumper interface.
 * It extends UnicastRemoteObject to enable remote object functionality.
 */
public class BumperImpl extends UnicastRemoteObject implements Bumper {

    // The BigInteger that is modified by calls to bump()
    private BigInteger counter;

    /**
     * Constructor initializes the counter to zero.
     * @throws RemoteException if a remote communication error occurs
     */
    public BumperImpl() throws RemoteException {
        super();
        counter = BigInteger.ZERO;
    }

    /**
     * Implementation of bump() method.
     * Adds 1 to the BigInteger counter each time it's called.
     * @return true on successful completion
     * @throws RemoteException if a remote communication error occurs
     */
    public boolean bump() throws RemoteException {
        counter = counter.add(BigInteger.ONE);
        return true;
    }

    /**
     * Implementation of get() method.
     * Returns the current value of the BigInteger counter.
     * @return the current BigInteger value
     * @throws RemoteException if a remote communication error occurs
     */
    public BigInteger get() throws RemoteException {
        return counter;
    }
}
