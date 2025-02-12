package Dynatac.Protocol;

public interface IDynatacProtocolMaster {
	
	public interface IDynatacProtocolMasterSuscriptor {
		abstract public void remoteEvent (int eventId, int data);
	}
	
	abstract public void setOnEvent (IDynatacProtocolMasterSuscriptor subscriptor);
	
	abstract public void sendCommand (int aCommand, int data);
}
