import java.util.Random;
import java.util.concurrent.Semaphore;

public class Kernal implements Runnable, Device{

	Scheduler scheduler;	// Scheduler member
	Thread thread;			// thread
	Semaphore semaphore;	// semaphore
	VirtualFileSystem vfs;	// vfs object
	boolean[] pagesInUse = new boolean[1024];	// tracks if pages are in use (all start as false: not in use)
	int swapFile;
	int pageTracker;
	
	// getter for scheduler
	public Scheduler getScheduler() {
		return this.scheduler;
	}
	
	// constructor
	public Kernal() {
		semaphore = new Semaphore(0);		// allows 0 threads to be running on start
		thread = new Thread(this);			// initializes thread
		scheduler = new Scheduler();		// initializes scheduler
		thread.start();						// starts the thread
		vfs = new VirtualFileSystem();		// initializes vfs
		swapFile = vfs.Open("file swap.txt");	// creates a swap file via vfs
		pageTracker = 0;
	}
	

	// releases (increments) the semaphore, allowing this thread to run
	public void start() {
		semaphore.release();
	}
	

	// acquire the semaphore, then call main
	public void run() {
		
		// infinite loop that attempts to decrement the semaphore then either creates a new process or switches process
		while(true) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			// call the function that implements them
			switch (OS.currentCall){
				
			case CreateProcess:
				OS.returnValue = null;
				OS.returnValue = scheduler.CreateProcess((PCB)OS.functionParam.get(0));
				break;
			case SwitchProcess:
				scheduler.SwitchProcess();
				break;
			case SleepProcess:
				Sleep();
				break;
			case Open:
				OS.returnValue = null;
				OS.returnValue = Open((String)OS.functionParam.get(0));
				break;
			case Close:
				Close((int)OS.functionParam.get(0));
				break;
			case Read:
				OS.returnValue = null;
				OS.returnValue = Read((int)OS.functionParam.get(0), (int)OS.functionParam.get(1));
				break;
			case Seek:
				Seek((int)OS.functionParam.get(0), (int)OS.functionParam.get(1));
				break;
			case Write:
				OS.returnValue = null;
				OS.returnValue = Write((int)OS.functionParam.get(0), (byte[])OS.functionParam.get(1));
				break;
			case SendMessage:
				OS.returnValue = null;
				SendMessage((kernalMessage)OS.functionParam.get(0));
				break;
			case WaitForMessage:
				OS.returnValue = null;
				OS.returnValue = WaitForMessage();
				break;
			case GetPid:
				OS.returnValue = null;
				OS.returnValue = GetPid();
				break;
			case GetPidByName:
				OS.returnValue = null;
				OS.returnValue = GetPidByName((String)OS.functionParam.get(0));
				break;
			case GetMapping:
				OS.returnValue = null;
				GetMapping((int)OS.functionParam.get(0));
				break;
			case AllocateMemory:
				OS.returnValue = null;
				OS.returnValue = AllocateMemory((int)OS.functionParam.get(0));
				break;
			case FreeMemory:
				OS.returnValue = null;
				OS.returnValue = FreeMemory((int)OS.functionParam.get(0), (int)OS.functionParam.get(1));
				break;
			}
			
			scheduler.getCurrentlyRunning().run();	// starts whichever process resulted from create or switch process
		}
	}
	
	// calls sleep in scheduler
	public void Sleep() {	
		if(OS.functionParam.get(0) instanceof Integer) {
			scheduler.Sleep((Integer)OS.functionParam.get(0));
		}
	}

	// finds an empty entry in scheduler's PCB's array, and calls vfs.open, if either array is full or vfs.open fails, return -1 
	@Override
	public int Open(String s) {
		// finds an empty entry in scheduler's PCB's array and returns -1 if full
		int emptyEntry = scheduler.getCurrentlyRunning().getEmptyIdEntry();
		if(emptyEntry==-1) {
			System.out.println("Error Kernal Open(): currently running PCB's array is full");
			return emptyEntry;
		}
		
		// calls vfs' open and returns -1 if it fails
		int vfsEmptyEntry = vfs.Open(s);
		if(vfsEmptyEntry==-1) {
			System.out.println("Error Kernal Open(): VFS open()");
			return vfsEmptyEntry;
		}
		
		// put the id from vfs into the scheduler's PCB's array
		scheduler.getCurrentlyRunning().setIdEntry(emptyEntry, vfsEmptyEntry);
		
		// returns the empty entry in scheduler's PCB's array if no errors occur
		return emptyEntry;
		
	}
	// Empties scheduler's PCB's array id index
	@Override
	public void Close(int id) {
		
		int entryPCB = scheduler.getCurrentlyRunning().getIdEntry(id);
		vfs.Close(entryPCB);
		scheduler.getCurrentlyRunning().setIdEntry(entryPCB, -1);
		
	}

	// gets the object thats in the scheduler's PCB's array by the index (id) and calls read on vfs
	@Override
	public byte[] Read(int id, int size) {
		int entryPCB = scheduler.getCurrentlyRunning().getIdEntry(id);
		return vfs.Read(entryPCB, size);
		
		
	}
	
	// gets the object thats in the scheduler's PCB's array by the index (id) and calls seek on vfs
	@Override
	public void Seek(int id, int to) {
		int entryPCB = scheduler.getCurrentlyRunning().getIdEntry(id);
		vfs.Seek(entryPCB, to);
		
	}

	// gets the object thats in the scheduler's PCB's array by the index (id) and calls write on vfs
	@Override
	public int Write(int id, byte[] data) {
		
		int entryPCB = scheduler.getCurrentlyRunning().getIdEntry(id);
		return vfs.Write(entryPCB, data);
	}
	
	

	// returns current process' pid by calling schedulers GetPid()
	public int GetPid() {
		return scheduler.GetPid();
	}
	
	// returns the pid based on the name of a process by calling schedulers GetPidByName()
	public int GetPidByName(String nameOfProcess) {
		return scheduler.GetPidByName(nameOfProcess);
	}
	
	
	// sends message to another process
	public void SendMessage(kernalMessage km){
		kernalMessage copyKM = new kernalMessage(km);
		copyKM.senderPID = scheduler.GetPid();
		
		// if the target PCB does exist, add KernalMessage to the targets KernalMessage queue
		if(scheduler.pidToPcbMap.get(copyKM.targetPID)!=null) {
			scheduler.pidToPcbMap.get(copyKM.targetPID).messageQueue.add(copyKM);
		}
		
		// if the currently running process gets a message, reprioritize it
		if(WaitForMessage().message!=-1) {
			scheduler.reprioritizeMessageProcess();
		}
		
	}
	
	// process waits to get a message from another process
	public kernalMessage WaitForMessage(){
		
		// if the currently running process has a message in the queue, take it off the list and return it
		if(!scheduler.getCurrentlyRunning().messageQueue.isEmpty()) {
			return scheduler.getCurrentlyRunning().messageQueue.remove();
		}
		
		else {
			scheduler.WaitForMessageProcess();
			return new kernalMessage();
		}
	}
	
	// seek to the offset then read or write
	
	/*	call getmapping, if no free space get a random process' page and write their page to fake file
	 * call seek bc when you call it you will start reading from that seeked offset
	 * 
	 */
	
	// returns physical page from virtual page index
	public void GetMapping(int virtualPageNumber) {

		// physical page number
		VirtualToPhysicalMapping virtualToPhysical = scheduler.GetMapping(virtualPageNumber);
		// if physical page = -1, check in pagesInUse array to see if any pages are not in use
		if(virtualToPhysical.physicalPageNum==-1) {
			for(int i=0; i<pagesInUse.length; i++) {
				if(pagesInUse[i]==false) {
					pagesInUse[i]=true;
					virtualToPhysical.physicalPageNum = i;
					break;
				}
			}
			// if there are no free pages, perform a page swap to free a page
			if(virtualToPhysical.physicalPageNum == -1) {
					// calls getRandomProcess
					virtualToPhysical = scheduler.getRandomProcess();
					// writes the physical page of the random process into the swap file and increments pageTracker
					swapFile = vfs.Write(pageTracker,new byte[virtualToPhysical.physicalPageNum*1024]);
					pageTracker++;
			}
		}
		
		// loads old data in
		if(virtualToPhysical.diskPageNum!=-1) {
			virtualToPhysical.physicalPageNum = virtualToPhysical.diskPageNum;
		}
		
		
		int virtualPageNum = virtualPageNumber;
		
		Random random = new Random();				// creates a random number either 0 or 1
		int randomTlbEntry = random.nextInt(2);
		
		// puts virtual page and its physical page into random TLB entry
		UserlandProcess.TLB[0][randomTlbEntry] = virtualPageNum;
		UserlandProcess.TLB[1][randomTlbEntry] = virtualToPhysical.physicalPageNum;
	}
	
	
	// allocates "size" memory for process and returns start of virtual address
	public int AllocateMemory(int size) {
		
		int pagesNeeded = size/1024;						// pages needed (size/1024)
		int[] physicalToVirtualPage = new int[pagesNeeded];	// will be used to store physical values in PCB array
		
		// if size is not a multiple of 1024, return failure
		if(size%1024 != 0) {
			System.out.println("Error Kernal AllocateMemory(): size isn't a multiple of 1024");
			return -1;
		}
		
		
		int counter=0;	// counter to fill array
		
		// for the length of the boolean array
		for(int i=0; i<pagesInUse.length; i++) {
			
			// if there is an unused page, mark it as used and assign the process to the page
			if(pagesInUse[i]==false) {
				pagesInUse[i] = true;
				UserlandProcess.assignMemory[i] = scheduler.getCurrentlyRunning().GetName();
				
				// fill the array with the physical pages the process is using
				if(counter==pagesNeeded) break;
				physicalToVirtualPage[counter] = i;
				counter++;
				
			}
		}
		
		// maps physical pages to virtual and returns start of that virtual address
		return (1024*(scheduler.getCurrentlyRunning().storePhysicalPage(physicalToVirtualPage)));
	}
	
	// frees memory
	public boolean FreeMemory(int pointer, int size) {
		
		int pagesToBeFreed = size/1024;		// pages to be freed (size/1024)
		int pageToStartAt = pointer/1024;	// where to start freeing memory
		
		
		// if size or pointer is not a multiple of 1024, return failure
		if(size%1024!=0 || pointer%1024!=0) {
			System.out.println("Error Kernal FreeMemory(): pointer or size isn't a multiple of 1024");
			return false;
		}
		
		
		
		
		
		scheduler.getCurrentlyRunning().FreeMemory(pageToStartAt, pagesToBeFreed);
		return true;
		
		
		
	}
	
	
	
}
