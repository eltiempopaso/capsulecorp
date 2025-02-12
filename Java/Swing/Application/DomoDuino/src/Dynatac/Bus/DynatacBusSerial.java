package Dynatac.Bus;

import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener;
import Dynatac.Bus.DynatacBusBase;


//import java.util.Enumeration;
import java.util.List;


import java.util.ArrayList;

public class DynatacBusSerial extends DynatacBusBase implements SerialPortEventListener {
	
	/**
	 * Configuration class 
	 */
	public static class DynatacBusSerial_ConfigInfo {
		public DynatacBusSerial_ConfigInfo()
		{
			TIME_OUT  = 2000;
			DATA_RATE = 9600;			
		}
		
		/** Milliseconds to block while waiting for port open */
		public int TIME_OUT;
		
		/** Default bits per second for COM port. */
		public int DATA_RATE;
	}
	
	public DynatacBusSerial (String port, DynatacBusSerial_ConfigInfo info)
	{
		myPort_   = port;
		timeout_  = info.TIME_OUT;
		dataRate_ = info.DATA_RATE;
		
		initialize();
		
		System.out.println("SerialManager created with port" + myPort_);
	}
		
	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public void serialEvent(SerialPortEvent oEvent) {
		
		// 
		//
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			
			// Call parent protected method
			//
			dataReady();
		}
		// else {
		
		// Ignore all the other eventTypes, but you should consider the other ones.
		//
		
		// }
	}
	
	/**
	 * Scans for available serial ports
	 * 
	 * @return a list of valid serial ports
	 */
	public static List<String> scanPorts () {
		
		// Return variable
		//
		List<String> names = new ArrayList<String>();
	
		@SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();

		// Go through all elements and add the detected ports
		//
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = portEnum.nextElement();

			String portName = currPortId.getName();

			names.add (portName);
		}

		return names;
	}
	
	
	/**
	 * Public methods, all static because it is a singleton 
	 * 
	 */
	protected void initialize() {
		
		// Variables
		//
		CommPortIdentifier portId = null;
		//Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		@SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();

		// Look for com port
		//
		System.out.println ("Trying to open port " + myPort_);
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = portEnum.nextElement();
			if (currPortId.getName().equals(myPort_))
			{
				portId = currPortId;
				System.out.println("COM port found: "+myPort_);
				break;
			}
		}

		// Raise an error if no com port is available
		//
		if (portId == null) {
			System.err.println("Could not find COM port.");
		}

		// Open port if available
		//
		try {
			// Open serial port, and use class name for the appName.
			//
			serialPort_ = (SerialPort) portId.open(this.getClass().getName(), timeout_);

			// Set port parameters
			//
			serialPort_.setSerialPortParams(dataRate_,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// Open the streams
			//
			streamInitializations (serialPort_.getInputStream(), serialPort_.getOutputStream());

			// Add event listeners
			//
			serialPort_.addEventListener(this);
			serialPort_.notifyOnDataAvailable(true);
			
		} catch (Exception e) {
			System.err.println(e.toString());
			setStatus (DYNATAC_BUS_STATUS_STARTING_FAILED);
		} 
	}
	
	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	protected synchronized void close() {
		
		System.out.println("Closing serial port.");;
		
		// Remove listeners
		//
		if (serialPort_ != null) {
			serialPort_.removeEventListener();
			serialPort_.close();
		}
	}
	
	/**
	 * Internal class variables
	 */
	private String 		myPort_;
	private int 		timeout_;
	private int 		dataRate_;
	private SerialPort 	serialPort_;
}
