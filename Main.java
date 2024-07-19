

public class Main {

	public static void main(String[] args) throws Exception {
		
		// Starts up ping
		OS.Startup(new HelloWorld());
		
		
		
		
		//System.out.print("Between hello and goodbye");
		OS.CreateProcess(new GoodbyeWorld());
		
		//OS.CreateProcess(new GoodAfternoonWorld(), PCB.Priority.realTime);

	}

}
