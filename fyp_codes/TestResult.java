package fyp_codes;

public class TestResult {
	private int tTime;
	private int tDelay; 
	private int tTravel;
	private float aDelay; 
	private float aTravel; 
	
	public TestResult(int tTime, int tDelay, int tTravel){
		this.tTime = tTime;
		this.tDelay = tDelay;
		this.tTravel = tTravel;

		aDelay = (float)tDelay / Constants.NUM_QC; 
		aTravel = (float)tDelay / Constants.AGV; 
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
	
	public float getAvgDelay(){
		return aDelay; 
	}
	
	public float getAgvTravel(){
		return aTravel; 
	}
	
}
