import java.util.Random;
import java.util.concurrent.Semaphore;

public abstract class UserlandProcess implements Runnable {

	boolean quantumExpired;		// amount of run time a program gets (will be true when run time has expired)
	public Thread thread = new Thread(this);		// thread
	public Semaphore semaphore = new Semaphore(0);	// semaphore
	
	public static byte[] physicalMemory = new byte[1024*1024];	// physical memory (1024 pages where each page has 1024 bytes
	public static int[][] TLB = {{-1,-1},{-1,-1}};				// TLB for virtual to physical page
	
	public static String[] assignMemory = new String[1024];		// assigns memory to the name of a process
	
	
	public UserlandProcess() {
		thread.start();
		
	}
	
	
	// acquire the semaphore, then call main
	public void run() {

		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		
		main();
	}
	
	
	
	// called to indicate that process' quantum has expired
	public void requestStop() {
		quantumExpired=true;
	}
	
	//*****
	// indicates if the semaphore is 0
	public boolean isStopped()   {
		
		// if there is a thread is currently acquiring the semaphore, the semaphore will be 0 and return true
		if (semaphore.availablePermits() == 0) {
			return true;
		}
		
		else {
			return false;
		}
		
	}
	
	// true when the java thread is not alive
	public boolean isDone() {
		return !thread.isAlive();
	}
	
	// releases (increments) the semaphore, allowing this thread to run
	public void start() {

		semaphore.release();
	}
	
	// acquires (decrements) the semaphore, stopping the thread from running
	public void stop() {
		
		
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// if the boolean is true, set the boolean to false and call OS.switchProcess()
	public void cooperate()   {
		if(quantumExpired==true) {
			quantumExpired=false;
			OS.switchProcess();
		}
		

	}
	
	// reads memory at page pageNum, then within the page at pageOffset
	public byte Read(int address) {
		
		int virtualPageNum = address/1024;		// virtual page number
		int physicalPageNum = -1;				// physical page number
		int pageOffset = -1;					// where in the page we are
		int addressOfMemory = -1;				// actual address in memory
		
		
		
		// checks if virtual address is in either of the stored virtual addresses
		if(TLB[0][0]==virtualPageNum) {
			physicalPageNum = TLB[1][0];
		}
		else if (TLB[0][1]==virtualPageNum) {
			physicalPageNum = TLB[1][1];
		}
		
		// else TLB doesn't have given virtual page -> physical page mapping 
		else {
			// calls OS.GetMapping and stores new random entry in TLB
			OS.GetMapping(virtualPageNum);
			
			
			
			
			// re performs checks again to see if there is a new virtual -> physical mapping

			// checks if virtual address is in either of the stored virtual addresses
			if(TLB[0][0]==virtualPageNum) {
				physicalPageNum = TLB[1][0];
			}
			else if (TLB[0][1]==virtualPageNum) {
				physicalPageNum = TLB[1][1];
			}
		}
		
		// stop process
		if(physicalPageNum==-1) {
			requestStop();
		}
		
		// calculates the address in memory
		pageOffset = address%1024;
		addressOfMemory = (physicalPageNum*1024)+pageOffset;
		
		// returns byte in addressOfMemory
		return physicalMemory[addressOfMemory];
	}
	
	
	
	
	
	
	// writes data at page pageNum, then within the page at pageOffset
	public void Write(int address, byte value) {

		int virtualPageNum = address/1024;		// virtual page number
		int physicalPageNum = -1;				// physical page number
		int pageOffset = -1;					// where in the page we are
		int addressOfMemory = -1;				// actual address in memory
		
		
		
		// checks if virtual address is in either of the stored virtual addresses
		if(TLB[0][0]==virtualPageNum) {
			physicalPageNum = TLB[1][0];
		}
		else if (TLB[0][1]==virtualPageNum) {
			physicalPageNum = TLB[1][1];
		}
		
		// else TLB doesn't have given virtual page -> physical page mapping 
		else {
			// calls OS.GetMapping and stores new random entry in TLB
			OS.GetMapping(virtualPageNum);
			
			
			// re performs checks again to see if there is a new virtual -> physical mapping

			// checks if virtual address is in either of the stored virtual addresses
			if(TLB[0][0]==virtualPageNum) {
				physicalPageNum = TLB[1][0];
			}
			else if (TLB[0][1]==virtualPageNum) {
				physicalPageNum = TLB[1][1];
			}
		}
		
		// stop process
		if(physicalPageNum==-1) {
			requestStop();
		}
		
		// calculates the address in memory
		pageOffset = address%1024;
		addressOfMemory = (physicalPageNum*1024)+pageOffset;
		
		// writes value to addressOfMemory in physicalMemory
		physicalMemory[addressOfMemory] = value;
	}
	
	
	
	// abstract main method to be overwritten
	public abstract void main();
	
	
}
