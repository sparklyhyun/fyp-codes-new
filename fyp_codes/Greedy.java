package fyp_codes;

import java.util.*;

public class Greedy {
	public final JobList jobList;
	public final ArrayList<Agv> agvList;	//kind of idle list. 
	private ArrayList<Job> q_jobs = new ArrayList<>(); 
	
	public Greedy(JobList j, ArrayList<Agv> agvL){
		this.jobList = j; 
		this.agvList = agvL; 
	}
	
	public void startGreedy(){
		//put the 1st column into a queue (test first) 
		for(int i=0; i<Constants.MAX_Y; i++){
			q_jobs.add(jobList.getJob(i, 0)); 
		}
		
		//print array elements 
		System.out.println("test queue elements");
		for(int j=0; j<q_jobs.size(); j++){
			System.out.println("job y: " + q_jobs.get(j).getY() + " job x: " +q_jobs.get(j).getX());
			//for 1 column, x should be the same 
		}
		
		//test simulator repaint
		for(int k=0; k<q_jobs.size(); k++){
			q_jobs.get(k).setComplete();
			
			//wait for 1 second 
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			jobList.repaint();
		}
		
		
		
		//continue with the next column, then the next column - more time saving (crane need not move much) 
		/*
		 * pop the first item in the queue
		 * 	for remaining items,
		 * 		calculate the end time & end travel time
		 * 		current.end time = next.end travel time 
		 *
		 *Then, calculate the ideal start time for each items
		 *		ideal start time = this.end travel time - travel time 
		 *		(ideal start time == buffer time before start) 
		 *
		 *Then, assign in the ascending order of the buffer time 
		 * 
		 * */
		
		//do it with all the columns? i think i need to compare lol 
		
	}
	
	/* I need to show
	 * - agv assignment
	 * - agv distance from the crane
	 */
	
	public int idealStart(Job prev, Job curr){	//start from index 2
		int newIdealStart = prev.getTotalCost(); //ideal start time is when prev just finishes drop off 
		return 0; 
	}
	
}
