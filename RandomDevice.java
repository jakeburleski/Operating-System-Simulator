import java.util.Random;

public class RandomDevice implements Device{
	
	Random[] randomArray;	// array of random devices
	
	
	// constructor
	public RandomDevice() {
		randomArray= new Random[10];
	}
	
	// creates new Random device and puts it in an empty spot in the array
	@Override
	public int Open(String s) {
		
		int seed;		// seed for Random object
		Random rand;	// java.util.Random object to be added to randomArray
		int ID = -1;	// ID
		
		// loops through randomArray to see if there is space to add another random object
		for(int i=0; i<randomArray.length; i++) {
			if(randomArray[i]==null) {
				ID = i;
				break;
			}
		}
		
		// if the array if full, print an error and return -1
		if(ID==-1) {
			System.out.println("Error RandomDevice Open(): randomArray is Full.");
			return ID;
		}
		
		// if there is an s that is passed, that becomes the seed for the random object and is stored in the randomArray
		if(!s.isEmpty() && s!= null) {
			seed = Integer.valueOf(s);
			rand = new Random(seed);
			randomArray[ID] = rand;
		}
		
		// if there is no seed provided, use a randomly generated seed and store the random object in the randomArray
		else {
			rand = new Random();
			randomArray[ID] = rand;
		}
		
		return ID;
	}

	// nulls the random device entry
	@Override
	public void Close(int id) {
		// if id is not within the index of the randomArray, error
		if(id<0 || id>10) {
			System.out.println("Error RandomDevice Close(): id is out of bounds");
		}
		
		randomArray[id] = null;
	}

	// create/fill an array with random values
	@Override
	public byte[] Read(int id, int size) {
		
		// if id is not within the index of the randomArray, error
		if(id<0 || id>10) {
			System.out.println("Error RandomDevice Read(): id is out of bounds");
			return new byte[]{-1};
		}
			
		byte[] randomVals = new byte[size];		// initializes new byte array
		randomArray[id].nextBytes(randomVals);	// fills the array with random bytes
		
		return randomVals;
		
		
	}

	// seeks to "int to" on the Random object in index "id" in randomArray
	@Override
	public void Seek(int id, int to) {

		// if id is not within the index of the randomArray, error
		if(id<0 || id>10) {
			System.out.println("Error RandomDevice Read(): id is out of bounds");
		}
			
		byte[] randomVals = new byte[to];		// initializes new byte array
		randomArray[id].nextBytes(randomVals);	// fills the array with random bytes
		
	}

	// method should not be called in RandomDevice
	@Override
	public int Write(int id, byte[] data) {
		return -1;
	}

}
