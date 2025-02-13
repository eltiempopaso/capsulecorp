package Dynatac.Bus;

import java.net.Inet4Address;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
//import java.net.SocketException;
import java.net.UnknownHostException;

import Dynatac.Bus.IDynatacBus.IDynatacBusListener;

/**
 * 
 * @author elotro
 *
 */
public class DynatacBusServerSocket extends DynatacBusCommon implements IDynatacBusListener,Runnable {

	/**
	 * 
	 * @author elotro
	 *
	 */
	private class InternalMiniServer extends DynatacBusBase implements Runnable {
		/**
		* Constructor
		*/
		/**
		 * Dynatac Bus serving on a socket
		 * 
		 * @param aSocket
		 */
		public InternalMiniServer (Socket aSocket) {
			socket_ = aSocket;
			
			// Open the streams
			//
			try {
				streamInitializations (socket_.getInputStream(), socket_.getOutputStream());

				// Start the new thread
				// 
				new Thread (this).start();

			} catch (IOException e) {
				System.err.println(e.toString());
				setStatus (DYNATAC_BUS_STATUS_STARTING_FAILED);
			}
		}

		/**
		 * Internal thread, used to read data
		 * 
		 */
		public void run() {
			while (!Thread.currentThread().isInterrupted())
			{
				// if not data ready means the client has closed its connection
				//
				if (!dataReady())
				{
					break;
				}
			}
			
			close();
		}
		
		// Ending method
		// 
		protected void close() {
			try {
				socket_.close();
				myConnectedClients_.removeBus(this);
			} catch (IOException e) {
				System.err.println(e.toString());
				setStatus (DYNATAC_BUS_STATUS_ENDING_FAILED);
			}			
		}

		
		/**
		 * Internal class variables
		 */
		private Socket socket_ = null;
	}

	/**
	* Constructor
	*/
	public DynatacBusServerSocket (int aPort) {
		myPort_ = aPort;
		myConnectedClients_ = new DynatacBusBridge();
		myConnectedClients_.installListener(this);
		
		new Thread (this).start();
	}
	
	/**
	 * write data to all connections
	 * 
	 */
	public void write(String data) {
		myConnectedClients_.write(data);
	}

	/**
	 * Starts listening a socket
	 * 
	 * @throws IOException
	 */
	private void waitForConnections() throws IOException
	{
		Socket aSocket = listener_.accept();
		
		InternalMiniServer miniServer = new InternalMiniServer(aSocket);
		
		myConnectedClients_.addBus(miniServer);
	}
	
	public void dataAvailable(String data, IDynatacBus bus) {
		notifyListeners (data);
	}

	public void onStatusChange(int busStatus) {
		setStatus (busStatus, true);
	}
	

	/**
	 * Listener thread
	 */
	public void run() {
		initialize ();
	
		while (!Thread.currentThread().isInterrupted())
		{
			try {
				waitForConnections ();
			} catch (IOException e1) {
				System.err.println( e1.toString() );
				setStatus (DYNATAC_BUS_STATUS_CONNECTION_PROBLEM);
			}
		}
		
		//close();
	}

	/**
	 * Common method to initialize
	 */
	protected void initialize() {
		try {
			listener_ = new ServerSocket(myPort_);
		} catch (IOException e) {
			System.err.println(e.toString());
			setStatus (DYNATAC_BUS_STATUS_STARTING_FAILED);
		}
	}
	
	/**
	 * Common method to end up
	 */
	protected void close() {
	}
	

	/**
	 * Static methods
	 * @throws UnknownHostException 
	 */
	static public String getIpAddress () throws UnknownHostException
	{	
		return Inet4Address.getLocalHost().getHostAddress();
	}
	
	/**
	 * Internal class variables
	 */
	private int 		 myPort_;	
	private ServerSocket listener_;
	
	private DynatacBusBridge myConnectedClients_;

}
