import java.time.Clock;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {

	
	private Timer timer;								// timer for delay
	private PCB pcb;		// currently running process
	public int pid;			// pid
	public static Clock clock = Clock.systemDefaultZone();
	
	private LinkedList<PCB> realTime;		// real time processes (top priority)
	private LinkedList<PCB> interactive;	// interactive processes (2nd priority)
	private LinkedList<PCB> background;		// background processes (3rd priority)
	private LinkedList<ClockDelay> sleepList;	// sleeping processes go in here until woken back up
	private LinkedList<pidAndPCB> waitForMessageList;	// processes that are waiting for messages
	
	
	public HashMap<Integer, PCB> pidToPcbMap = new HashMap<>();
	
	// constructor
	public Scheduler() {
		
		// initializes
		realTime = new LinkedList<>();
		interactive = new LinkedList<>();
		background = new LinkedList<>();
		sleepList = new LinkedList<>();
		waitForMessageList = new LinkedList<>();
		timer = new Timer();
		pid = 0;	
		
		// TimerTask to interrupt current process
		TimerTask interrupt = new TimerTask() {
			@Override
			public void run() {
				if(pcb != null) {
					pcb.requestStop();
				}
			
			}
		};
		// timer that interrupts the process ever 250 milliseconds
		timer.scheduleAtFixedRate(interrupt,0,250);
	}
	
	
	// adds a process to its correct priority linked list, if there is no process currently running than switch process
	public int CreateProcess(PCB up) {
		// maps PCB's process' to PID, is used for in GetPidByName()
		pidToPcbMap.put(pid, up);
		
		whichPriority(up.priorityType).add(up);
		if(pcb==null) {
			SwitchProcess();
		}
		pid++;
		return pid;
		
	}
	
	
	public void SwitchProcess() {
		
		// clears TLb every time process is switched
		UserlandProcess.TLB[0][0]=-1;
		UserlandProcess.TLB[0][1]=-1;
		UserlandProcess.TLB[1][0]=-1;
		UserlandProcess.TLB[1][1]=-1;
		
		// checks sleeping processes
		wakeUpProcess();
		
		
		if(pcb==null);	// if process is null, do nothing
		
		// if there is a process currently running 
		else if(!pcb.isDone()) {
			OS.testPrint(pcb.getUp() + " " + whichPriority(pcb.priorityType));
			// add process to end of the list
			whichPriority(pcb.priorityType).addLast(pcb);
		}
		// empties the array of ID's in PCB if the process ends
		else if(pcb.isDone()){
			// maps PCB's process' name to PID, is used for in GetPidByName()
			pidToPcbMap.put(pid, pcb);
			pcb.emtpyIdArray();
		}
		
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// sets process in linked lists first index to current process
		substituteProcess();
		
		
	
	}
	
	// adds the milliseconds wanted to sleep to the current clock
	public void Sleep(int milliseconds) {
		// total clock time including the milliseconds to wait
		long clockMilli = (long)clock.millis() + milliseconds;
		ClockDelay clockDelay = new ClockDelay(clockMilli, pcb);	// creates new clockDelay object
		whichPriority(pcb.priorityType).remove(pcb);				// removes pcb from priority queue
		sleepList.add(clockDelay);	// adds process to sleeping list
		substituteProcess();	// switches process
		
	}
	

	// switches to a "random" PCB
	public void substituteProcess() {
		pcb = randomProcess().removeFirst();
	}
	
	
	// creates a new class to hold the sleeping process along with its time to wait until running
	private class ClockDelay{
		long clockMilli;
		PCB pcb;
		
		// constructor
		public ClockDelay(long clockMilli, PCB pcb) {
			this.clockMilli = clockMilli;
			this.pcb = pcb;
		}
		
		// getter for clockMilli
		public long getclockMilli() {
			return clockMilli;
		}
		
		// gets pcb
		public PCB getPCB() {
			return pcb;
		}
	}
	
	// method that is used to add to the correct list when the matching priority is sent through
	public LinkedList<PCB> whichPriority(PCB.Priority priority){
		
		switch(priority) {
		case realTime:
			return realTime;
		case interactive:
			return interactive;
		case background:
			return background;
			
		default:
			System.out.print("Error: Error in whichPriority()");
			break;
		}
		return background;
		
	}
	
	public LinkedList<PCB> randomProcess(){
		
		Random rand = new Random();	// instance of random class
		int randomInt = rand.nextInt(10);
		
		// if there is a process in realTime
		if(!realTime.isEmpty()){
			// if the random number is 0-5 return realTime processes
			if(randomInt<=5) return realTime;
			
			// instead if random number is 6-8 check if there is an interactive process
			// and if so return it
			else if(randomInt<9&&!interactive.isEmpty()) {
				return interactive;
			}
			
			// instead if random number is 9 check if there is a background process
			// and if so return it
			else if (randomInt==9&&!background.isEmpty()) {
				return background;
			}
			
			else {
				return realTime;
			}
		} 
		
		// if realTime is empty, but interactive is not, 3/4 interactive and 1/4 background
		else if(!interactive.isEmpty()){
			randomInt = rand.nextInt(4);
			// if the random number is 0-3 return interactive processes
			if(randomInt<=3) return interactive;
			
			// instead if random number is 4 check if there is a background process
			// and if so return it
			else if (randomInt==4&&!background.isEmpty()) {
				return background;
			}
			
			else {
				return interactive;
			}
		}
		
		else if(!background.isEmpty()){
			return background;
		}
		
		else {
			System.out.println("error(): no more processes to run in any priority");
		}
		return background;
		
	}
	
	// returns currently running PCB
	public PCB getCurrentlyRunning() {
		return pcb;
	}
	
	
	// wakes up a process
	public void wakeUpProcess() {

		// loop over sleepers to regularly check sleepers to see if they can be woken
		for (int i=0; i<sleepList.size(); i++) {
			// if the clock plus the millisecond delay is less than the clocks current time,
			// then the process can be added back to its respective list
			if(sleepList.get(i).getclockMilli() <= clock.millis()) {
				OS.testPrint("sleeplist element: "+sleepList.get(i).pcb.toString());
				OS.testPrint("sleeplist size: "+sleepList.size());
				whichPriority(sleepList.get(i).pcb.priorityType).addLast(sleepList.remove(i).pcb);
			}
		}
	}
	
	// returns current process' pid
	public int GetPid() {
		return pid;
	}
	
	
	// returns the pid based on the name of a process
	public int GetPidByName(String nameOfProcess) {
		
		// loops through hashmap and returns pid if the name given is one of the processes
		for(Map.Entry<Integer, PCB> entry : pidToPcbMap.entrySet()) {
			if(entry.getValue().GetName().equals(nameOfProcess)) {
				return entry.getKey();
			}
		}	
		// will only return if given name is not a process
		System.out.println("Error Scheduler GetPidByName(): Name entered is not a valid process.");
		return -1;
	}
	
	
	
	
	// if a process is waiting for a message, deschedule it and add it to waitForMessageList
	public void WaitForMessageProcess(){
		waitForMessageList.add(getCurrentPidandPCB());	// adds pidAndPCB object to waitForMessageList		
		whichPriority(pcb.priorityType).remove(pcb);	// removes pcb from priority queue
		substituteProcess();							// switches process
		
	}
	
	
	// checks processes that are waiting for a message to see if they have gotten one
	public void reprioritizeMessageProcess() {
		// loop over waitForMessageList to remove the currently running process from the waitForMessageList and put it back in its correct priority
		for (int i=0; i<waitForMessageList.size(); i++) {
			if(waitForMessageList.get(i).pcb.equals(pcb)) {
				whichPriority(waitForMessageList.get(i).pcb.priorityType).addLast(waitForMessageList.remove(i).pcb);
			}
		}
		
	}
	
	// returns physical page from virtual page index
	public VirtualToPhysicalMapping GetMapping(int virtualPageNumber) {
		return pcb.getMapping(virtualPageNumber);
	}
	
	
	// gets a random process, looks for a free page. if none, try another process until a free page is found
	public VirtualToPhysicalMapping getRandomProcess() {
		
		
		int index = -1;			// index into the 100 virtual page array
		Random rand;			// random object to be used to get random process
		int randomInt = -1;
		int randomPhysicalPage;
		VirtualToPhysicalMapping virtualToPhysical = new VirtualToPhysicalMapping();
		
		PCB[] currentProccesses = new PCB[pidToPcbMap.values().size()];
		pidToPcbMap.values().toArray(currentProccesses);
		
		// while process is
		for(int i=0; i<currentProccesses.length; i++) {
			
			// creates a random index into the currently running processes
			rand = new Random();	
			randomInt = rand.nextInt(currentProccesses.length);
			
			// loops through process' 100 virtual page array to look for a physical page
			for(int j=0; j<100; j++) {
				virtualToPhysical.physicalPageNum = currentProccesses[randomInt].getMapping(j).physicalPageNum;
				if(virtualToPhysical.physicalPageNum!=-1) {
					index = j;
					break;
				}
			}
		}
		
		// sets victims disk page to its physical page, then changes its physical page to -1
		currentProccesses[randomInt].pushIntoDisk(index, virtualToPhysical.physicalPageNum);
		
		// sets current procces' physical page at "index" to victims old physical page
		pcb.setPhysicalPage(index, virtualToPhysical.physicalPageNum);
		
		// returns mapping for virtual to physical pages
		return virtualToPhysical;
	}
	
	
	
	
	
	
	
	// uses GetName() to get the currently running pcb and its pid
	public pidAndPCB getCurrentPidandPCB(){
		// finds the matching pcb and creates a pidAndPCB object from it
		for(Map.Entry<Integer, PCB> entry : pidToPcbMap.entrySet()) {
			if(entry.getValue().GetName().equals(pcb.GetName())) {
				return new pidAndPCB(entry.getKey(), entry.getValue());
			}
		}
		
		return new pidAndPCB();
		
	}
	
	// new data structure to hold a pcb and its pid
	private class pidAndPCB{
		int pid;
		PCB pcb;
		
		public pidAndPCB(int pid, PCB pcb) {
			this.pid = pid;
			this.pcb = pcb;
		}
		
		// constructor for failures
		public pidAndPCB() {
			System.out.println("Error Scheduler getCurrentPidAndPCB(): current pcb is not in pidToPcbMap.");
			this.pid = -1;
		}
		
		
	}
	
	
	
	
}
