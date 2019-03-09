package fyp_codes;

public class QuayCrane{
	private int index; 
	private int lastUsed = 0; 
	
	public QuayCrane(int i){
		index = i; 
	}
	
	public void setLastUsed(int lu){
		lastUsed = lu;  
	}
	
	public int getLastUsed(){
		return lastUsed; 
	}
}
