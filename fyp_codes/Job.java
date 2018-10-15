package fyp_codes;

import java.util.Random;

public class Job {
	private final int x;	//no. of bay
	private final int y;	//tier 
	//private int index; //priority 
	private boolean	loading = false;	//for unloading task, false. for loading task, true
	private boolean visited = false; 	//if visited = true, else = false
	private boolean complete = false; 	//if job complete = true, else = false
	private int travel; 	//time taken to travel to the pick up point
	private final int pCost = 1; 	//time taken to execute the task (pickup)
	private final int dCost = 1;	//time takent to drop off 
	
	public Job(int x, int y){
		this.x = x;
		this.y = y;
		initCost();
	}
	
	public void initCost(){
		Random rand = new Random();
		int randomCost = rand.nextInt(10)+1; 	// cost ranges from 1 to 10 
		this.travel = randomCost; 
		System.out.println("job i, j: " + y+ ", " +x+ ", cost: " + randomCost);
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
	
	public int getTotalCost(){
		return travel + pCost + dCost; 
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
	
	public void setComplete(){
		complete = true;
	}
	
}
