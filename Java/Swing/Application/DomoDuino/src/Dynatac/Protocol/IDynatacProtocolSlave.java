package Dynatac.Protocol;

public interface IDynatacProtocolSlave {
	
	public interface IDynatacProtocolSlaveSuscriptor {
		abstract public void commandAvailable (int commandId, int data);
	}
	
	abstract public void setOnCommand (IDynatacProtocolSlaveSuscriptor subscriptor);
	
	abstract public void sendEvent (int aEvent, int aData);
}
