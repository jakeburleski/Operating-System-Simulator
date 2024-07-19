import java.util.HashMap;

public class VirtualFileSystem implements Device {

	DeviceMap[] DeviceMapArray = new DeviceMap[10];		// DeviceMap array of Device and its internal array ID(index)
	RandomDevice randomDevice;							// wrapper for RandomDevice object
	FakeFileSystem fakeFile;							// wrapper for FakeFileSystem object
	
	
	// VFS constructor
	public VirtualFileSystem() {
		randomDevice = new RandomDevice();
		fakeFile = new FakeFileSystem();
	}
	
	
	// calls open
	@Override
	public int Open(String s) {
		
		int DeviceMapArrayIndex = -1;
		DeviceMap deviceMap = new DeviceMap(s);	// passes string to DeviceMaps constructor
		
		
		// checks if there is space in the DeviceMapArray and sets DeviceMapArrayIndex to that index, if not DeviceMapArrayIndex stays -1
		for(int i=0; i<DeviceMapArray.length; i++) {
			if(DeviceMapArray[i]==null) {
				DeviceMapArrayIndex = i;
				break;
			}
		}
					
		// if array is full, print out an error and return DeviceMapArrayIndex 
		if(DeviceMapArrayIndex==-1) {
			System.out.println("Error VirtualFileSystem Open(): deviceArray is full.");
			return DeviceMapArrayIndex;
		}
		
		
		
		
		
		// if the devices internal array is full, or runs into an error return -1
		if(deviceMap.getID()==-1) {
			return deviceMap.getID();
		}
		
		// else store new device into array at index DeviceMapArrayIndex
		else {
			DeviceMapArray[DeviceMapArrayIndex] = deviceMap;
			return DeviceMapArrayIndex;
		}
		
		
	}

	// calls devices close
	@Override
	public void Close(int id) {

		// if id is not within the index of the DeviceMapArray, error
		if(id<0 || id>10) {
			System.out.println("Error VirtualFileSystem Close(): id is out of bounds");
		}
		
		DeviceMapArray[id].getDevice().Close(DeviceMapArray[id].getID());
		
		DeviceMapArray[id] = null;
		
	}

	// calls read for the correct device
	@Override
	public byte[] Read(int id, int size) {
		DeviceMap deviceMap = DeviceMapArray[id];	// gets the DeviceMap from the id of the DeviceMapArray
		
		// calls DeviceMaps device, calls its read via the DeviceMaps getID which returns the devices internal array ID
		byte[] vfsRead = deviceMap.getDevice().Read(deviceMap.getID(), size);
		return vfsRead;
	}

	// calls devices seek
	@Override
	public void Seek(int id, int to) {
		DeviceMap deviceMap = DeviceMapArray[id];	// gets the DeviceMap from the id of the DeviceMapArray
		
		// calls DeviceMaps device, calls its seek via the DeviceMaps getID which returns the devices internal array ID
		deviceMap.getDevice().Seek(deviceMap.getID(), to);
		
	}

	// calls devices write
	@Override
	public int Write(int id, byte[] data) {
		DeviceMap deviceMap = DeviceMapArray[id];	// gets the DeviceMap from the id of the DeviceMapArray
		
		// calls DeviceMaps device, calls its write via the DeviceMaps getID which returns the devices internal array ID
		int vfsWrite = deviceMap.getDevice().Write(deviceMap.getID(), data);
		return vfsWrite;
	}
	
	
	
	// private class to store device and its internal ID for its array
	private class DeviceMap{
		
		Device devicePointer;	// a pointer to the device that is constructed in the VFS constructor
		
		int indexOfDevice = -1;		
		/* index(ID) of the Devices internal array
		-1 if an error occurs when calling devices open, otherwise will be the internal
		array index of either the FakeFilySystem object or RandomDevice object
		*/
		
		
		// DeviceMap constructor
		public DeviceMap(String s) {
			String[] splitBySpaces = s.split(" ",2);	// splits the string by spaces
			String nameOfDevice = splitBySpaces[0];		// name of device
			String passToDevice = splitBySpaces[1];		// what will be passed to said device

			// sets the device pointer to the correct type of device
			if(nameOfDevice.toLowerCase().equals("random")){
				devicePointer = randomDevice;
			}
			else if(nameOfDevice.toLowerCase().equals("file")){
				devicePointer = fakeFile;
			}
			// calls the correct device's open depending on the first word of the String passed in the constructor
			// could be -1 when calling devices open
			this.indexOfDevice = devicePointer.Open(passToDevice);
			
			
		}
		

		// gets the device that is initialized
		public Device getDevice() {
			return devicePointer;
		}
		
		
		// returns ID for devices internal array
		public int getID() {
			return indexOfDevice;
		}
	}

}
