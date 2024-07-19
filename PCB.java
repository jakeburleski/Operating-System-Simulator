import java.util.LinkedList;
import java.util.Random;

public class PCB {

	private UserlandProcess pcbCurrentProcess;						// current running process
	public static int nextpid;										// static next pid
	public int pid;													// current pid
	enum Priority{realTime, interactive, background};				// priority type of a given process
	Priority priorityType;											// priority object
	private int counter = 0;										// counter that is used to demote a process if timeouts 
	private int[] idArray = new int[10];							// array of IDs which gets initialized with -1
	private String name;											// name of currently running process
	LinkedList<kernalMessage> messageQueue = new LinkedList<>();	// message queue for kernal Messages
	private VirtualToPhysicalMapping[] virtualPageArray = new VirtualToPhysicalMapping[100];							// virtual pages are indexes where physical pages are the actual values
	
	
	// PCB constructor
	public PCB(UserlandProcess up, PCB.Priority priority) {
		// initializes the array with -1, which is the indication that nothing has an index
		for(int i=0; i<idArray.length; i++) {
			idArray[i] = -1;
		}
		
		
		this.priorityType = priority;
		this.pcbCurrentProcess = up;
		nextpid = nextpid+1;
		this.pid = nextpid;
		name = pcbCurrentProcess.getClass().getSimpleName();
		
	}
	
	public void emtpyIdArray() {
		// empties the array with -1, which is the indication that nothing has an index
		for(int i=0; i<idArray.length; i++) {
			idArray[i] = -1;
		}
				
	}
	
	public int getIdEntry(int index) {
		return idArray[index];
	}
	
	// finds an empty (-1) entry in the array and returns either the entry or -1 if full
	public int getEmptyIdEntry() {
		int ID = -1;
		
		for(int i=0; i<idArray.length; i++) {
			if(idArray[i]==-1) {
				ID=i;
			}
		}
		return ID;
	}
	
	// puts the second parameter's id into the array for emptyIdEntry index
	public void setIdEntry(int emptyIdEntry, int setID) {
		idArray[emptyIdEntry] = setID;
	}
	
	
	// for testing
	public UserlandProcess getUp() {
		return pcbCurrentProcess;
	}
	
	// loops until current running process is stopped
	public void stop() {
		pcbCurrentProcess.stop();
	}
	
	// calls the current running process isDone(), will return true if the thread(process) is dead
	public boolean isDone() {
		return pcbCurrentProcess.isDone();
	}
	
	// requests ULP to stop, also will demote process if it timeouts more than 5 times
	public void requestStop() {
		
		counter++;		// counter that demotes after more than 5 timeouts in a row
		if(counter>5) {
			if(priorityType.equals(Priority.realTime)) {
				priorityType = Priority.interactive;
			}
			if(priorityType.equals(Priority.interactive)) {
				priorityType = Priority.background;
			}
		}
		
		pcbCurrentProcess.requestStop();
	}
	
	public void run() {
		pcbCurrentProcess.start();
	}
	
	
	// gets name of process
	public String GetName() {
		return name;
	}
	
	// returns physical page from virtual page index
	public VirtualToPhysicalMapping getMapping(int virtualPage) {
		return virtualPageArray[virtualPage];
		
	}
			
		
	
	
	// stores physical pages into virtualPageArray and returns start of virtual address
	public int storePhysicalPage(int[] physicalPages) {
		
		int startOfVirtualAddress = -1;
		
		int counter = physicalPages.length;
		for(int i=0; i<virtualPageArray.length; i++) {
			
			// if there isn't memory being used, look to find continuous pages and mark pages as in use
			if(virtualPageArray[i]==null) {
				
				// when counter = 0 there is enough continuous space
				counter--;
				if(counter==0) {
					startOfVirtualAddress = i;	// gets start of virtual address
					
					// stores all physical pages in continuous order in virtualPageArray
					for(int j=0; j<physicalPages.length; j++) {
						virtualPageArray[i] = new VirtualToPhysicalMapping();
						i++;
					}
					break;
				}
			}
			// reset counter if an assigned page is found
			else if(virtualPageArray[i]!=null) counter=physicalPages.length;
		}
		
		return startOfVirtualAddress;
		
	}
	
	// free memory in virtual page and unassigns memory from process
	public void FreeMemory(int pointer, int size) {
		
		for(int i=pointer; i<(pointer+size); i++) {
			// checks if physical page is -1 before updating
			if(virtualPageArray[i].physicalPageNum!=-1) {
				UserlandProcess.assignMemory[i] = null;
				virtualPageArray[i] = null;
			}	
		}
	}
	
	public int getDisk(int index) {
		return virtualPageArray[index].diskPageNum;
	}
	
	
	// pushes physical page at "index" into disk page, then updates its physical page to -1
	public void pushIntoDisk(int index, int updateDisk) {
		virtualPageArray[index].diskPageNum = updateDisk;
		virtualPageArray[index].physicalPageNum = -1;
	}
	
	// sets physical page at "index"
	public void setPhysicalPage(int index, int physical) {
		virtualPageArray[index].physicalPageNum = physical;
	}
	
	
	
}
