public class kernalMessage {
	
	int senderPID;	// sender pid
	int targetPID;	// receiver pid
	int message;	// "what" this message is
	byte[] data;	// can be whatever the applications wants it to be
	
	
	// constructor to be used as a fail
	public kernalMessage() {
		this.message = -1;
	}
	
	// constructor with data
	public kernalMessage(int senderPID, int targetPID, int message, byte[] data) {
		this.senderPID = senderPID;
		this.targetPID = targetPID;
		this.message = message;
		this.data = data;
	}
	
	
	
	// copy constructor
	public kernalMessage(kernalMessage message) {
		this.senderPID = message.senderPID;
		this.targetPID = message.targetPID;
		this.message = message.message;
		this.data = message.data;
	}
	
	// ToString
	@Override
	public String toString() {
		return "SenderPID: " + senderPID + "\ntargetPID: " + targetPID + "\nmessage: " + message + "\ndata: " + data.toString();
	}
	
	public int getMessage() {
		return this.message;
	}
}
