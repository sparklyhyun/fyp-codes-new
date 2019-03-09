package fyp_codes;

import java.util.Random;

public class Job{
	private final int x;	//no. of bay (column)
	private final int y;	//tier 		(row) 
	//private int index; //priority 
	private boolean	loading = false;	//for unloading task, false. for loading task, true
	private boolean visited = false; 	//if visited = true, else = false
	private boolean created = false; 
	private boolean complete = false; 	//if job complete = true, else = false
	private boolean assigned = false; 	//if the load on the agv = true 
	private boolean travelling = false; 
	private boolean unloadAssigned = false; //this is only for the merged modified greedy 
	
	private boolean bayWaited = false; 
	
	private int travel; 	//time taken to travel to the pick up point + time taken to pickup 
	//private final int pCost = 1; 	//time taken to execute the task (pickup)
	private int dCost = 2;	//time takent to drop off / pick up 
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
	
	private int jobIndex = 0; 
	
	private int qcIndex;
	private int bayIndex; 

	private int[] startPos = new int[2]; 	//y, x , y=0, x = 2, 7, 12, 17 (depends on location) 	//start pick up point 
	private int[] endPos = new int[2]; 	//y, x , y=1, x = 2, 7, 12, 17 (random) 	//end point
	private boolean agvWait = false; 	// waiting for agv
	private boolean prevWaiting = false; 
	
	
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
			randomCost = rand.nextInt((20-15)+1) + 15;	// cost ranges from 15 to 20 
			break;
		case 1:
			randomCost = rand.nextInt((15-10)+1)+10; 	// cost ranges from 10 to 15 
			break;
		case 2:
			randomCost = rand.nextInt((10-5)+1)+5; 		// cost ranges from 5 to 10
			break;
		case 3: 
			randomCost = rand.nextInt((20-5)+1)+5;		//cost ranges from 5 to 20 
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
	
	public void initCost2 (){
		//the one with the new coordinates!
		Random rand = new Random(); 
		//int qcx = rand.nextInt(4); 		// index from 0 to 3
		int ycx = rand.nextInt(4); 		// index from 0 to 3
		
		//start/end depends on loading or unloading 
		if(loading){
			//loading jobs start from yc. assign random for now 
			startPos[0] = 1; 
			startPos[1] = Constants.craneCoord[ycx]; 
			
			//end at qc 
			endPos[0] = 0; 
			if(y < Constants.MAX_Y ){
				if(x < Constants.QC_X){
					endPos[1] = Constants.craneCoord[0]; 
				}else{
					endPos[1] = Constants.craneCoord[1]; 
				}
			}else{
				if(x < Constants.QC_X){
					endPos[1] = Constants.craneCoord[2]; 
				}else{
					endPos[1] = Constants.craneCoord[3]; 
				}
			}
			
			
		}else{
			//unloading jobs start from qc
			startPos[0] = 0; 
			if(y < Constants.MAX_Y ){
				if(x < Constants.QC_X){
					startPos[1] = Constants.craneCoord[0]; 
				}else{
					startPos[1] = Constants.craneCoord[1]; 
				}
			}else{
				if(x < Constants.QC_X){
					startPos[1] = Constants.craneCoord[2]; 
				}else{
					startPos[1] = Constants.craneCoord[3]; 
				}
			}
			
			//end at yc 
			endPos[0] = 1; 
			endPos[1] = Constants.craneCoord[ycx]; 
		}
		
		
		//calculate cost (assume all will travel anti clockwise) 
		calcTotalCost(); 
		
		/*
		System.out.println("job: " + this.y + ", " +this.x + " pick up: " + startPos[0] + ", " + startPos[1]
				+ ", drop off: " + endPos[0] + ", " + endPos[1]+ " cost: " + tCost);
		*/ 
		//use test cases 
		
	}
	
	public void initCost2(int x){
		Random rand = new Random(); 
		int ycx = rand.nextInt(4); 		// index from 0 to 3
		
		//using test cases. how? 
		//if x = 1, test case 1 (1st qc more dist) 
		//if x = 2, test case 2 (1st qc less dist)
		//if x = 3, test case 3 (all random cost) 
		//if x = 0, test case 0 (all qc end point is right in front) 
		
		//cost calculation is the same 
		if(loading){
			//loading, start from yc, end at qc
			startPos[0] = 1; 
			endPos[0] = 0; 
			
			//end at qc 
			if(y < Constants.MAX_Y ){
				if(this.x < Constants.QC_X){
					endPos[1] = Constants.craneCoord[0]; 
				}else{
					endPos[1] = Constants.craneCoord[1]; 
				}
			}else{
				if(this.x < Constants.QC_X){
					endPos[1] = Constants.craneCoord[2]; 
				}else{
					endPos[1] = Constants.craneCoord[3]; 
				}
			}
			
			switch(x){
			case 0:
				//end pt right below 
				startPos[1] = endPos[1]; 
				break;
			case 1:
				//end pt more dist (for qc 1 only) 
				startPos[1] = Constants.craneCoord[3]; 
				break; 
			case 2:
				//end pt random
				startPos[1] = Constants.craneCoord[ycx]; 
				break; 
			default: break; 
			}
			
		}else{
			//unloading, start from qc, end at yc 
			startPos[0] = 0;
			endPos[0] = 1; 
			
			if(y < Constants.MAX_Y ){
				if(this.x < Constants.QC_X){
					startPos[1] = Constants.craneCoord[0]; 
				}else{
					startPos[1] = Constants.craneCoord[1]; 
				}
			}else{
				if(this.x < Constants.QC_X){
					startPos[1] = Constants.craneCoord[2]; 
				}else{
					startPos[1] = Constants.craneCoord[3]; 
				}
			}
			
			switch(x){
			case 0:
				//end pt right below
				endPos[1] = startPos[1]; 
				break;
			case 1:
				//end pt more dist (for qc1 only)
				endPos[1] = Constants.craneCoord[3]; 
				break;
			case 2:
				//end pt random
				endPos[1] = Constants.craneCoord[ycx]; 
				break; 
			default: break; 
			}
		}
		
		calcTotalCost(); 
		
		/*
		System.out.println("job: " + this.y + ", " +this.x + " pick up: " + startPos[0] + ", " + startPos[1]
				+ ", drop off: " + endPos[0] + ", " + endPos[1]+ " cost: " + tCost);
				
		*/ 
	}
	
	public void initStartEndPt(){
		
	}
	
	public void calcTotalCost(){
		int diff = Math.abs(endPos[1] - startPos[1]); 
		tCost = diff + Constants.VERT_COST + Constants.HOR_COST + Constants.TURN_COST*2 + dCost; 
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
	
	public int[] getStartPos(){
		return startPos; 
	}
	
	public int[] getEndPos(){
		return endPos; 
	}


	public void setStartPos(int y, int x){
		startPos[0] = y; 
		startPos[1] = x; 
	}
	
	public void setEndPos(int y, int x){
		endPos[0] = y;
		endPos[1] = x; 
	}
	
	public void setLoading(){
		loading = true; 
	}
	
	public void setVisited(boolean b){
		visited = b;
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
	
	public void setCost(int cost){
		this.travel = cost; 
		setTotalCost(); 
	}
	
	public boolean getAgvWait(){
		return agvWait; 
	}
	
	public void setAgvWait(boolean b){
		this.agvWait = b; 
	}
	
	public void setUnloadAssigned(boolean b){
		this.unloadAssigned = b; 
	}
	
	public boolean getUnloadAssigned(){
		return unloadAssigned; 
	}
	
	public boolean getCreated(){
		return created; 
	}
	
	public void setCreated(boolean b){
		created = b; 
	}
	
	public void setBayWaited(boolean b){
		bayWaited = b;
	}
	
	public boolean getBayWaited(){
		return bayWaited; 
	}
	
	public boolean getPrevWaiting(){
		return prevWaiting; 
	}
	
	public void setPrevWaiting(boolean b){
		prevWaiting = b; 
	}
	
	public void setJobIndex(int i){
		jobIndex = i; 
	}
	
	public int getJobIndex(){
		return jobIndex; 
	}


}
