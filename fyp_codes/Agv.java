package fyp_codes;

import java.util.*;

public class Agv {
	private int agvNum;
	private boolean idle = true; 	//if true, agv idle. else, false. Initial state = idle 
	private ArrayList<Job> taskList = new ArrayList<>(); //see if i need this later 
	
	public Agv(int i){
		this.agvNum = i; 
	}
	
	public void setIdle(boolean b){
		this.idle = b;
	}
	
	public boolean getIdle(){
		return idle; 
	}
	
	public void taskExecute(){
		//TimeUnit.MILLISECONDS.sleep(100); 
		//1 cost unit  = 100 milliseconds 
		//System.out.println("task executed" + task id );
	}
	
}
