package fyp_codes;

import java.util.Random;

public class Job {
	private final int x;	//no. of bay (column)
	private final int y;	//tier 		(row) 
	private int index; //priority 
	private boolean	loading = false;	//for unloading task, false. for loading task, true
	private boolean visited = false; 	//if visited = true, else = false
	private boolean complete = false; 	//if job complete = true, else = false
	private boolean assigned = false; 	//if the load on the agv = true 
	private int travel; 	//time taken to travel to the pick up point + time taken to pickup 
	//private final int pCost = 1; 	//time taken to execute the task (pickup)
	private final int dCost = 1;	//time takent to drop off 
	private int tCost; 				//actual end time 
	
	private int idealEnd = 0; 		//idealEnd == previous.endtime, or can be later than prev.endtime 
	//private int idealStart = 0; 	//idealStart = prev.idealEnd 
	private int buffer = 0; 		//buffer = prev.idealEnd - travel, later sort according to buffer 
	private boolean lastJob = false; 
	private boolean isWaiting = false; 
	
	private Agv agv; 

	
	public Job(int y, int x, int index){
		this.x = x;
		this.y = y;
		this.index = index; 
		initCost();
	}
	
	public void initCost(){
		Random rand = new Random();
		int randomCost = rand.nextInt(10)+1; 	// cost ranges from 1 to 10 
		this.travel = randomCost; 
		//System.out.println("job i, j: " + y+ ", " +x+ ", cost: " + randomCost);
		setTotalCost(); 
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getIndex(){
		return index; 
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
	
	public void setComplete(){
		complete = true;
	}
	
	public void setIncomplete(){
		complete = false; 
	}
	
	public void setTotalCost(){	//previous total cost
		tCost = travel + dCost;
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
	
	
	
}
