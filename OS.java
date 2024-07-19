import java.util.ArrayList;
import java.util.Random;


public class OS{

	private static Kernal kernal;		// instance to Kernal class
	static ArrayList<Object> functionParam = new ArrayList<>();;		// ArrayList for parameters of a given function
	static Object returnValue;			// return value for function
	
	enum CallType{CreateProcess, SwitchProcess, SleepProcess, 
		Open, Close, Read, Seek, Write, SendMessage, WaitForMessage,
		GetPid, GetPidByName, GetMapping, AllocateMemory, FreeMemory};		// enum for what function to call
	static CallType currentCall;		// instance of CallType
	
	
	// creates a new process
	public static int CreateProcess(UserlandProcess up)   {
		functionParam.clear();		// resets the parameters
		functionParam.add(new PCB(up, PCB.Priority.interactive));  	// adds function to parameter list
		currentCall = CallType.CreateProcess;	// sets the current call
		
		// give middle priority
		// switch to the kernal
		// releases the semaphore, if its scheduler has a process currently running, stop it
		kernal.start();			
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			kernal.getScheduler().getCurrentlyRunning().stop();
		}
		
		// (for startup) if there is not already a process running, wait until there is
		while(kernal.getScheduler().getCurrentlyRunning()==null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int newReturnVal = (int) returnValue;			// cast and return the return value
		return newReturnVal;
	}
	
	
	public static int CreateProcess(UserlandProcess up, PCB.Priority priority){
		functionParam.clear();		// resets the parameters
		functionParam.add(new PCB(up, priority));  	// adds function to parameter list
		currentCall = CallType.CreateProcess;	// sets the current call
		
		// give middle priority
		
		
		// switch to the kernal
		// releases the semaphore, if its scheduler has a process currently running, stop it
		kernal.start();			
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			kernal.getScheduler().getCurrentlyRunning().stop();
		}
		
		// (for startup) if there is not already a process running, wait until there is
		while(kernal.getScheduler().getCurrentlyRunning()==null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int newReturnVal = (int) returnValue;			// cast and return the return value
		return newReturnVal;
		
	}
	
	// creates kernal() and calls CreateProcess on the current process as well as an idle process
	public static void Startup(UserlandProcess init)   {
		kernal = new Kernal();
		IdleProcess idle = new IdleProcess();
		CreateProcess(init);
		CreateProcess(idle, PCB.Priority.background);
	}
	
	// stops currently running process and switches to the new process
	public static void switchProcess()   {
		functionParam.clear();					// clear all parameters
		currentCall = CallType.SwitchProcess;	// current call set to switchprocess
		
		// switch to the kernal
		// releases the semaphore, if its scheduler has a process currently running, stop it
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
	}
	
	// calls sleep in kernal which calls sleep in scheduler
	public static void Sleep(int milliseconds) {
		functionParam.clear();		// resets the parameters
		functionParam.add(milliseconds);  	// adds function to parameter list
		currentCall = CallType.SleepProcess;	// sets the current call
		
		// switch to the kernal
		// releases the semaphore, if its scheduler has a process currently running, stop it
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
		
		// (for startup) if there is not already a process running, wait until there is
		while(kernal.getScheduler().getCurrentlyRunning()==null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		returnValue = null;			// cast and return the return value
		
		
		
		//kernal.Sleep();
	}
	
	public static void testPrint(String printBack) {
		//System.out.println(printBack);
	}
	
	// clears function param, adds parameters, and changes currentCall to Open
	public static void Open(String s) {
		functionParam.clear();
		functionParam.add(s);
		OS.currentCall = CallType.Open;
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
		
	}

	// clears function param, adds parameters, and changes currentCall to Close
	public static void Close(int id) {
		functionParam.clear();
		functionParam.add(id);
		OS.currentCall = CallType.Close;
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
	}

	// clears function param, adds parameters, and changes currentCall to Read
	public static void Read(int id, int size) {
		functionParam.clear();
		functionParam.add(id);
		functionParam.add(size);
		OS.currentCall = CallType.Read;
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
	}
	
	// clears function param, adds parameters, and changes currentCall to Seek
	public static void Seek(int id, int to) {
		functionParam.clear();
		functionParam.add(id);
		functionParam.add(to);
		OS.currentCall = CallType.Seek;
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
	}

	// clears function param, adds parameters, and changes currentCall to Write
	public static void Write(int id, byte[] data) {
		functionParam.clear();
		functionParam.add(id);
		functionParam.add(data);
		OS.currentCall = CallType.Write;
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
	}
	

	// clears function param, adds parameters, and changes currentCall to SendMessage
	public static void SendMessage(kernalMessage km) {
		functionParam.clear();
		functionParam.add(km);
		OS.currentCall = CallType.SendMessage;
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
	}
	
	// clears function param, and changes currentCall to WaitForMessage
	public static void WaitForMessage() {
		functionParam.clear();
		OS.currentCall = CallType.WaitForMessage;
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
	}
	
	
	

	/// clears function param, and changes currentCall to GetPid
	public static void GetPid() {
		functionParam.clear();
		OS.currentCall = CallType.GetPid;
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
	}
	
	// clears function param, adds parameters, and changes currentCall to GetPidByName
	public static void GetPidByName(String nameOfProcess) {
		functionParam.clear();
		functionParam.add(nameOfProcess);
		OS.currentCall = CallType.GetPidByName;
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
	}
	
	// takes a virtual page as a parameter, and saves its physical page counterpart in OS.returnValue
	public static void GetMapping(int virtualPageNumber) {
		functionParam.clear();
		functionParam.add(virtualPageNumber);
		OS.currentCall = CallType.GetMapping;
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
	}
	
	
	public static void AllocateMemory(int size) {
		functionParam.clear();
		functionParam.add(size);
		OS.currentCall = CallType.AllocateMemory;
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
	}
	
	public static void FreeMemory(int pointer, int size) {
		functionParam.clear();
		functionParam.add(pointer);
		functionParam.add(size);
		OS.currentCall = CallType.FreeMemory;
		PCB crp = kernal.getScheduler().getCurrentlyRunning();
		kernal.start();		
		if(kernal.getScheduler().getCurrentlyRunning() != null) {
			crp.stop();
		}
	}
	
	
	
	
	
	
	
}
