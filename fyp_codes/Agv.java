package fyp_codes;

import java.util.*;

public class Agv {
	private int agvNum;
	private boolean idle = true; 	//if true, agv idle. else, false. Initial state = idle 
	private ArrayList<Job> taskList = new ArrayList<>(); //see if i need this later 
	private boolean atQc = false; 	// true if agv is at the same qc as the next job 
	
	private int[] currCoord = new int[2]; 
	private int agvWaitTime; 	//this is actually the travel time 
	
	public Agv(){//int i){
		//this.agvNum = i; 
		//initAgvLocation(); 
		
		//agvs start from the same location 
		currCoord[0] = 0;
		currCoord[1] = 0; 
	}
	
	public void initAgvLocation(){
		//randomly initialize initial location! 
		Random rand = new Random(); 
		currCoord[0] = rand.nextInt(2); 		// index from 0 to 1	//0 - qc, 1 - yc 
		currCoord[1] = rand.nextInt(4); 		// index from 0 to 3
	}
	
	public void resetAgvLocation(){
		//for multiple testing! 
		currCoord[0] = 0;
		currCoord[1] = 0; 
	}
	
	public void setIdle(boolean b){
		this.idle = b;
	}
	
	public boolean getIdle(){
		return idle; 
	}
	
	public int getAgvNum(){
		return agvNum; 
	}
	
	public int getAgvWaitTime(){
		return agvWaitTime; 
	}
	
	public void setAgvWaitTime(Job j){
		int[] jobPos = j.getStartPos(); 

		int diff = Math.abs(currCoord[1] - jobPos[1]); 
		if(currCoord[0] == 0){	//if agv at QC
			if(currCoord[1] == jobPos[1]){
				//agv at the same qc as the job
				//need to wait for qc to pick up the container 
				agvWaitTime = 0; 
				atQc = true; 
			}else{
				agvWaitTime = diff; 
			}
		}else{
			agvWaitTime = diff + Constants.VERT_COST + Constants.HOR_COST + Constants.TURN_COST*2; 
		}
		
		
		
	}
	
	public void setAgvLocation(int[] loc){
		this.currCoord = loc;
	}
	
	public int[] getAgvLocation(){
		return currCoord; 
	}

	public boolean getAtQc(){
		return atQc; 
	}
	
}
