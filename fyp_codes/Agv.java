package fyp_codes;

import java.util.*;

public class Agv {
	private int agvNum;
	private boolean idle = true; 	//if true, agv idle. else, false. Initial state = idle 
	private ArrayList<Job> taskList = new ArrayList<>(); //see if i need this later 
	
	private int[] currCoord = new int[2]; 
	private int agvWaitTime; 
	
	public Agv(int i){
		this.agvNum = i; 
		initAgvLocation(); 
	}
	
	public void initAgvLocation(){
		//randomly initialize initial location! 
		Random rand = new Random(); 
		currCoord[0] = rand.nextInt(2); 		// index from 0 to 1	//0 - qc, 1 - yc 
		currCoord[1] = rand.nextInt(4); 		// index from 0 to 3
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
	
	public void taskExecute(){
		//TimeUnit.MILLISECONDS.sleep(100); 
		//1 cost unit  = 100 milliseconds 
		//System.out.println("task executed" + task id );
	}
	
	public int getAgvWaitTime(){
		return agvWaitTime; 
	}
	
	public void setAgvWaitTime(Job j){
		int[] jobPos = j.getStartPos(); 

		int diff = Math.abs(currCoord[1] - jobPos[1]); 
		if(currCoord[0] == 0){	//if agv at QC
			agvWaitTime = diff; 
		}else{
			agvWaitTime = diff + Constants.VERT_COST + Constants.HOR_COST + Constants.TURN_COST*2; 
		}
		
	}
	
	public void setAgvLocation(int[] loc){
		this.currCoord = loc;
		//agv end point is always at the yc. need to be updated all the time 
		
	}

	
}
