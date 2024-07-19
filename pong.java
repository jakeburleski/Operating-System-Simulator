
public class pong extends UserlandProcess{

	@Override
	public void main() {
		
		int messagePong;
		
		
		while(true) {

			// gets pid for process ping
			OS.GetPidByName("ping");
			int pingPid = (int)OS.returnValue;
			
			// gets pid for current process pong
			OS.GetPid();
			int pongPid = (int)OS.returnValue;
			

			// waits for message
			OS.WaitForMessage();
			kernalMessage response = (kernalMessage)OS.returnValue;
			
			if(response != null) {
				
				messagePong = response.getMessage();
				
				// prints out message
				System.out.println("PONG: from: " + pongPid + " to: " + pingPid + " " + "num: " + messagePong);
				
				int nextMessage = messagePong+1;
				
				kernalMessage sendPong = new kernalMessage(pongPid, pingPid, nextMessage, "punane".getBytes());
				OS.SendMessage(new kernalMessage(sendPong));
				
				OS.Sleep(50);
			}
			
			// creates kernal message with pong as sender and ping as target
		
			
			// sends message
			
			
			
			
		}
		
		
	}

}
