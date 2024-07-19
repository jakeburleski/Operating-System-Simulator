
public class IdleProcess extends UserlandProcess{

	@Override
	public void main() {

		while(true) {
		
	
			
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			cooperate();
			
		}
		
		
	}


	
}
