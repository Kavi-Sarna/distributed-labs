import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Bumper interface defines the remote methods that can be called
 * by clients on the Bumper service.
 */
public interface Bumper extends Remote {
    /**
     * Adds 1 to a BigInteger held by the service.
     * @return true on successful completion
     * @throws RemoteException if a remote communication error occurs
     */
    public boolean bump() throws RemoteException;
    
    /**
     * Returns the current value of the BigInteger held by the service.
     * @return the current BigInteger value
     * @throws RemoteException if a remote communication error occurs
     */
    public BigInteger get() throws RemoteException;
}
