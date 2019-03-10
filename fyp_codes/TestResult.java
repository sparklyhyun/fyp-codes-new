package fyp_codes;

public class TestResult {
	private int tTime;
	private int tDelay; 
	private int tTravel;
	private int aDelay; 
	private int aTravel; 
	
	public TestResult(int tTime, int tDelay, int tTravel, int aDelay, int aTravel){
		this.tTime = tTime;
		this.tDelay = tDelay;
		this.tTravel = tTravel;
		this.aDelay = aDelay;
		this.aTravel = aTravel; 
	}
	
	public int getTotalTime(){
		return tTime; 
	}
	
	public int getTotalDelay(){
		return tDelay; 
	}
	
	public int getTotalTravel(){
		return tTravel;
	}
	
	public int getAvgDelay(){
		return aDelay; 
	}
	
	public int getAgvTravel(){
		return aTravel; 
	}
	
}
