
public class ping extends UserlandProcess{

	@Override
	public void main() {
		

		
		// gets pid for process pong
		OS.GetPidByName("pong");
		int pongPid = (int)OS.returnValue;
		
		
		int message = 0;
		
		while(true) {
			

			
			// gets pid for current process ping
			OS.GetPid();
			int pingPid = (int)OS.returnValue;
			
		
			
			// creates kernal message with ping as sender and pong as target
			kernalMessage sendPing = new kernalMessage(pingPid, pongPid, message, "punane".getBytes());
			
			// sends message
			OS.SendMessage(new kernalMessage(sendPing));
			
			// prints out message
			System.out.println("PING: from: " + pingPid + " to: " + pongPid + " num: " + message);
			

			OS.Sleep(50);
			
			// waits for message
			OS.WaitForMessage();
			kernalMessage response = (kernalMessage)OS.returnValue;
			
			if(response != null) {
				// prints out message
				System.out.println("PING: from: " + pingPid + " to: " + pongPid + " num: " + message);
				message = response.getMessage();
				
			}
			
			
			
			
			
			
		}
	}

}
