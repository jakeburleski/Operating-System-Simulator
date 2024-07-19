
public class HelloWorld extends UserlandProcess{

	@Override
	public void main() {

		while(true) {
		
			
			
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			OS.AllocateMemory(1024);
			int x = (int)OS.returnValue;
			
			System.out.println("allocating memory at: " + x);
			
			Write(x,(byte)120);
			System.out.println("Reading from memory " + x + ", byte stored:" + Read(x));
			
			
			// un comment to free memory every time memory is allocated
			//OS.FreeMemory(x, 1024);
			
			
			//System.out.println("Hello World");
		
			cooperate();
			
			//OS.Sleep(100);
			
		}
		
	}
}

