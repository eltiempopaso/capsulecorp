import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Dynatac.Bus.DynatacBusBridge;
import Dynatac.Bus.DynatacBusClientSocket;
import Dynatac.Bus.DynatacBusSerial;
import Dynatac.Bus.DynatacBusSerial.DynatacBusSerial_ConfigInfo;
import Dynatac.Bus.DynatacBusServerSocket;
import Dynatac.Bus.IDynatacBus;
import Dynatac.Protocol.DynatacProtocol;
import Dynatac.Protocol.IDynatacProtocolMaster;
import Dynatac.Protocol.IDynatacProtocolMaster.IDynatacProtocolMasterSuscriptor;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.awt.event.ActionEvent;

@SuppressWarnings("unused")
public class DomoDuino extends JFrame implements IDynatacProtocolMasterSuscriptor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DomoDuino frame = new DomoDuino();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	JButton btnToogleLed;
	JLabel labelTemperatura;
	JLabel labelHumedad;
	JLabel labelLuz;
	int currentLedStatus;
	
	private IDynatacProtocolMaster configureMaster ()
	{
		// Get localhost ip address
		//
		/*
		try {
			myIp = DynatacBusServerSocket.getIpAddress();
		} catch (UnknownHostException e) {
			System.err.println("Could not get localhost ip address.");
			return null;
		}*/
		
		// Get serial port where arduino is connecte
		//
		
		List<String> detectedSerialPorts = DynatacBusSerial.scanPorts();
		String aSerialPortName = "";
		System.out.println("Existing serial ports: "+ detectedSerialPorts.size());
		
		for (int i = 0; i < detectedSerialPorts.size() && aSerialPortName == ""; i++)
		{
			String name = detectedSerialPorts.get(i);
			System.out.println("Serial port "+ i + " named: " + name);
			if (
					name.equals("/dev/tty.usbserial-A9007UX1") // Mac OS X
		//	 ||     name.equals("/dev/ttyACM0") // Raspberry Pi
			 ||     name.equals("/dev/ttyUSB0") // Linux
			 ||     name.equals("COM3") // Windows 
			 )
			{
				aSerialPortName = detectedSerialPorts.get(i);
				System.out.println("Found port: "+ aSerialPortName);
			}
		}
		
		if (aSerialPortName == "")
		{
			System.err.println ("ERROR. No serial devices found.");
			return null;
		}
		else
		{
			System.out.println("Using port: "+ aSerialPortName);
			
		}
		
		// Get port where start listening server
		//
		int serverPort = 9090;

		

		// Create Serial and Socket buses
		//
		IDynatacBus [] busesList = 
			{
				new DynatacBusServerSocket(serverPort),
				new DynatacBusSerial (aSerialPortName, new DynatacBusSerial_ConfigInfo())
			};
		

		// Create master dynatac protocol
		//
		IDynatacProtocolMaster dynatacMaster = new DynatacProtocol(
				new DynatacBusBridge(busesList)
				);
		dynatacMaster.setOnEvent(this);

		
		return dynatacMaster;
	}
	
	public DomoDuino() throws IOException {
		
		String ipAddress = DynatacBusServerSocket.getIpAddress();
		
				
		final IDynatacProtocolMaster master = configureMaster();
		System.out.println("My address is: " + ipAddress);
		
		
		// Create Serial and Socket buses
		//
		IDynatacBus [] busesList2 = 
		{
			new DynatacBusClientSocket ("192.168.1.7", 9090),
			new DynatacBusServerSocket(9091)//,
			//new DynatacBusSerial (aSerialPortName, new DynatacBusSerial_ConfigInfo())
		};	
		// Create master dynatac protocol
		//
		/*
		IDynatacProtocolMaster dynatacMaster = new DynatacProtocol(
				new DynatacBusBridge(busesList)
				);
		*/
		
				
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		
		labelTemperatura = new JLabel("1");
		labelHumedad 	 = new JLabel("7");
		labelLuz 		 = new JLabel("9");
		
		btnToogleLed = new JButton("Toogle LED");
		btnToogleLed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentLedStatus = currentLedStatus==0?1:0;
				master.sendCommand(0, currentLedStatus);
			}
		});
		panel.add(btnToogleLed);
		panel.add(labelTemperatura);
		panel.add(labelHumedad);
		panel.add(labelLuz);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Program is started");
	}
	
final int EVENTO_TEMPERATURA 	= 0;
final int EVENTO_LUZ 			= 1;
final int EVENTO_HUMEDAD 		= 2;

	public void remoteEvent(int eventId, int data) {
		if (eventId == EVENTO_TEMPERATURA)
		{
			labelTemperatura.setText("Temp: "+ String.valueOf(data));			
		}
		else if (eventId == EVENTO_LUZ)
		{
			labelLuz.setText("Luz: " + String.valueOf(data));
		}
		else if (eventId == EVENTO_HUMEDAD)
		{
			labelHumedad.setText("Humedad: " + String.valueOf(data));
		}
	}
}
