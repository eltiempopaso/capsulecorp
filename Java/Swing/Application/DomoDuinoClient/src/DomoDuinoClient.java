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

public class DomoDuinoClient extends JFrame implements IDynatacProtocolMasterSuscriptor{

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
					DomoDuinoClient frame = new DomoDuinoClient();
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
		// Server information
		//
		int localPort = 9090;
		int serverPort = 9090;
		String serverIp = "192.168.1.7";

		

		// Create Serial and Socket buses
		//
		IDynatacBus [] busesList = 
			{
				/* new DynatacBusServerSocket(localPort), */
				new DynatacBusClientSocket (serverIp, serverPort)
			};
		

		// Create master dynatac protocol
		//
		IDynatacProtocolMaster dynatacMaster = new DynatacProtocol(
				new DynatacBusBridge(busesList)
				);
		dynatacMaster.setOnEvent(this);

		
		return dynatacMaster;
	}
	
	public DomoDuinoClient() throws IOException {
		
		String ipAddress = DynatacBusServerSocket.getIpAddress();
		
		final IDynatacProtocolMaster master = configureMaster();
		System.out.println("My address is: " + ipAddress);
		
				
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
