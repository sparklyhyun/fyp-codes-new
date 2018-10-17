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
	
	public void startGreedy1(){	//generic greedy algorithm, start from first row, then move onto the next row
		//cost = just total cost
		long startTime = System.currentTimeMillis();  //to see the performance
		
		Job[] sortArray = new Job[Constants.MAX_X];	//for sorting purpose 
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				sortArray[j] = jobList.getJob(i, j);
			}
			sortArray = sortDescending(sortArray);
			for(int k=0; k<Constants.MAX_X; k++){
				q_jobs.add(sortArray[k]);
			}
		}
		
		//test assignment paint
		/*
		for(int i=0; i<q_jobs.size(); i++){
			q_jobs.get(i).setAssigned();
			System.out.println("index :" + i);
			jobList.repaint(); 
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		long endtime = System.currentTimeMillis()-startTime;
		System.out.println("dispatching order: ");
		for(int i=0; i<q_jobs.size(); i++){
			System.out.println("index: " + i + ", job: y, x: " + q_jobs.get(i).getY() + ", "
					+ q_jobs.get(i).getX() + ", total cost: " + q_jobs.get(i).getTotalCost());
		}
		
		System.out.println("time taken: " + endtime);
		updateSimulator();
		*/
		
	}
	
	public Job[] sortDescending(Job[] arr){	//add high cost first
		//simple bubble sort 
		for(int i=Constants.MAX_X-1; i>0; i--){
			for(int j=0; j<i; j++){
				if(arr[j].getTotalCost()>arr[j+1].getTotalCost()){
					Job temp = arr[j];
					arr[j] = arr[j+1];
					arr[j+1] = temp; 
				}
				
			}
		}
		return arr; 
	}
	
		public void updateSimulator(){
			//don't update unless:
			// 1. only assign if agv is available
			// 2. only complete if previous job is complete
			
			for(int k=0; k<q_jobs.size(); k++){
				jobList.repaint();
				q_jobs.get(k).setComplete();
				
				//wait for 1 second 
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
	public void testSimulator(){
		//put the 1st column into a queue 
		for(int i=0; i<Constants.MAX_Y; i++){
			q_jobs.add(jobList.getJob(i, 0)); 
		}

		//print array elements 
		System.out.println("test queue elements");
		for(int j=0; j<q_jobs.size(); j++){
			System.out.println("job y: " + q_jobs.get(j).getY() + " job x: " +q_jobs.get(j).getX()
					+ " job cost: " + q_jobs.get(j).getTotalCost());
			//for 1 column, x should be the same 
		}
		
		//test simulator repaint
		updateSimulator();
		
		
		
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
