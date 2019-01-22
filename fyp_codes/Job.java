package fyp_codes;

import java.util.Random;

public class Job {
	private final int x;	//no. of bay (column)
	private final int y;	//tier 		(row) 
	//private int index; //priority 
	private boolean	loading = false;	//for unloading task, false. for loading task, true
	private boolean visited = false; 	//if visited = true, else = false
	private boolean complete = false; 	//if job complete = true, else = false
	private boolean assigned = false; 	//if the load on the agv = true 
	private boolean travelling = false; 
	
	private int travel; 	//time taken to travel to the pick up point + time taken to pickup 
	//private final int pCost = 1; 	//time taken to execute the task (pickup)
	private final int dCost = 1;	//time takent to drop off 
	private int tCost = 0; 				//actual end time 
	
	private int idealEnd = 0; 		//idealEnd == previous.endtime, or can be later than prev.endtime 
	//private int idealStart = 0; 	//idealStart = prev.idealEnd 
	private int buffer = 0; 		//buffer = prev.idealEnd - travel, later sort according to buffer 
	private boolean lastJob = false; 
	private boolean isWaiting = false; 
	
	private int beforeTravel = 0;
	private int afterTravel = 0; 
	
	private int unloadWaitTime = 0; 
	
	private int splitX = 0;
	private int splitY = 0; 
	
	private Agv agv; 
	
	private int qcIndex;
	private int bayIndex; 

	
	public Job(int y, int x , boolean loading){
		this.x = x;
		this.y = y;
		this.loading = loading;
		
	}
	
	public void initCost(int x){	//0 - high cost, 1 - normal cost, 2 - low cost 
		Random rand = new Random(); 
		int randomCost = 0; 
		switch(x){
		case 0:
			randomCost = rand.nextInt((30-20)+1) + 20;	// cost ranges from 15 to 20 
			break;
		case 1:
			randomCost = rand.nextInt((15-10)+1)+10; 	// cost ranges from 10 to 15 
			break;
		case 2:
			randomCost = rand.nextInt((10-5)+1)+5; 		// cost ranges from 5 to 10
			break;
		default: break;
		}
		
		this.travel = randomCost; 
		/*
		Random rand = new Random();
		int randomCost = rand.nextInt(10)+1; 	// cost ranges from 1 to 10 
		this.travel = randomCost; 
		*/
		setTotalCost(); 
	}
	


	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	/*
	public int getIndex(){
		return index; 
	}*/
	
	public boolean getLoading(){
		return loading;
	}
	
	public boolean getVisited(){
		return visited; 
	}
	
	public boolean getComplete(){
		return complete; 
	}
	
	public boolean getAssigned(){
		return assigned; 
	}
	
	public boolean getLastJob(){
		return lastJob; 
	}
	
	public int getTotalCost(){
		return tCost; 
	}
	
	public int getIdealEnd(){
		return idealEnd;
	}
	
	public boolean getTravelling(){
		return travelling; 
	}
	
	public int getSplitX(){
		return splitX;
	}
	
	public int getSplitY(){
		return splitY; 
	}
	
	public void setSplitX(int x){
		this.splitX = x;
	}
	
	public void setSplitY(int y){
		this.splitY = y; 
	}
	/*
	public int getIdealStart(){
		return idealStart; 
	}*/
	
	public int getBuffer(){
		return buffer; 
	}
	
	public Agv getAgv(){
		return agv; 
	}
	
	public boolean getIsWaiting(){
		return isWaiting; 
	}
	
	public int getBeforeTravelCost(){
		return beforeTravel;
	}
	
	public int getAfterTravelCost(){
		return afterTravel; 
	}
	
	public int getUnloadWaitTime(){
		return unloadWaitTime; 
	}
	
	
	/*
	public void setIndex(int i){
		this.index = i;
	}*/
	
	public void setLoading(){
		loading = true; 
	}
	
	public void setVisited(){
		visited = true;
	}
	
	public void setNotvisited(){
		visited = false; 
	}
	
	public void setAssigned(){
		assigned = true; 
	}
	
	public void setNotAssigned(){
		assigned = false; 
	}
	
	public void setComplete(){
		complete = true;
	}
	
	public void setIncomplete(){
		complete = false; 
	}
	
	public void setTotalCost(){	//previous total cost
		tCost = travel + dCost;

		
	}
	
	public void setTotalCost2(int c){
		tCost = c; 
	}
	
	public void setTotalCost(int x){
		tCost = 0; 
	}
	
	public void setLastJob(){
		lastJob = true; 
	}
	
	public void setIsWaiting(boolean b){
		isWaiting = b; 
	}
	
	//check again!!! 
	public void updateTotalCost(Job prev){
		tCost = prev.getIdealEnd() + 1; 
	}
	
	public void setBuffer(int prev){	//previous.end time 
		buffer = prev - travel; 
	}
	
	public void setAgv(Agv agv){
		this.agv = agv; 
	}
	
	public void setTravelling(boolean t){
		this.travelling = t; 
	}
	
	public void addUnloadWatiTime(){
		this.unloadWaitTime += 1; 
	}
	
	public void setQcIndex(int i){
		this.qcIndex = i; 
	}
	
	public int getQcIndex(){
		return qcIndex; 
	}
	
	public void setBayIndex(int i){
		this.bayIndex = i; 
	}
	
	public int getBayIndex(){
		return bayIndex; 
	}
	
	
}
