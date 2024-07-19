import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class FakeFileSystem implements Device{

	

	RandomAccessFile[] randomFileArray;	// array of random access files

	
	// constructor
	public FakeFileSystem() {
		randomFileArray= new RandomAccessFile[10];
	}
	
	// opens the file
	@Override
	public int Open(String s) {

		RandomAccessFile randFile;	// RandomAccessFile object to be added to randomFileArray
		int ID = -1;				// ID
		
		// loops through randomFileArray to see if there is space to add another randomFileArray object
		for(int i=0; i<randomFileArray.length; i++) {
			if(randomFileArray[i]==null) {
				ID = i;
				break;
			}
		}
		
		// if the array if full, print an error and return -1
		if(ID==-1) {
			System.out.println("Error FakeFileSystem Open(): randomFileArray is full.");
			return ID;
		}
		
		// if the file name is not empty and not null, create the RandomAccessFile for read and write
		if(!s.isEmpty() && s!= null) {
			try {
				randFile = new RandomAccessFile(s, "rw");
			} catch (FileNotFoundException e) {
				System.out.println("Error FakeFileSystem Open(): passed file is empty.");
				return -1;
			}
			randomFileArray[ID] = randFile;
		}
		
		
		return ID;
	}

	// closes the random access file in index "id" in the "randomFileArray"
	@Override
	public void Close(int id) {
		

		// if id is not within the index of the randomFileArray, error
		if(id<0 || id>10) {
			System.out.println("Error FakeFileSystem Close(): id is out of bounds");
		}
		
		
		try {
			randomFileArray[id].close();
		} catch (IOException e) {
			System.out.println("Error FakeFileSystem Close(): Error in close.");
			e.printStackTrace();
		}
		
		randomFileArray[id] = null;
		
	}

	// reads "size" bytes in the random access file in index "id" in the "randomFileArray"
	@Override
	public byte[] Read(int id, int size) {

		// if id is not within the index of the randomArray, error
		if(id<0 || id>10) {
			System.out.println("Error FakeFileSystem Read(): id is out of bounds");
			return new byte[]{-1};
		}
			
		byte[] bytesToRead = new byte[size];		// initializes new byte array
		
		// fills the array with random bytes
		try {
			randomFileArray[id].read(bytesToRead);
		} catch (IOException e) {
			System.out.println("Error FakeFileSystem Read(): can't read bytes.");
			return new byte[]{-1};
		}	
		
		return bytesToRead;
		
	}

	// seeks to "int to" on the random access file in index "id" in randomArray
	@Override
	public void Seek(int id, int to) {
		try {
			randomFileArray[id].seek(to);
		} catch (IOException e) {
			System.out.println("Error FakeFileSystem Seek(): can't seek.");
			e.printStackTrace();
		}
		
	}

	// writes "data" to random access file stored in randomFileArray in index "id"
	@Override
	public int Write(int id, byte[] data) {
		
		try {
			randomFileArray[id].write(data);
		} catch (IOException e) {
			System.out.println("Error FakeFileSystem Write(): can't write.");
			return -1;
		}
		
		return 1;
	}

}
