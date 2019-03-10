package fyp_codes;

import java.util.*; 

public class DispatcherTest2 {
	private JobList jobList;	 

	private ArrayList<Agv> agvList = new ArrayList<>();	//kind of idle list. 
	private ArrayList<Job> q_jobs = new ArrayList<>(); 
	
	//arraylist to store the order of jobs 
	private static ArrayList<Job> jobOrder = new ArrayList<>();
	private static ArrayList<ArrayList<Job>> eventJob = new ArrayList<>(); 
	
	//event priority queue here
	private static ArrayList<ArrayList<Event>> eventOrder = new ArrayList<>(); 
	
	private static ArrayList<ArrayList<Job>> q_jobsList = new ArrayList<ArrayList<Job>>();  
	private static int[] totalQcCost = new int[Constants.NUM_QC]; 
	private static int[][] completeJobsBay; 
	
	
	
	public DispatcherTest2(JobList j){
		jobList = j; 
		
		//add agv 
		for(int i=0; i<Constants.AGV; i++){
			agvList.add(new Agv()); 
		}
		
		for(int i=0; i<Constants.NUM_QC; i++){
			eventOrder.add(new ArrayList<Event>()); 
		}
		
		for(int i=0; i<Constants.NUM_QC; i++){
			eventJob.add(new ArrayList<Job>()); 
		}
		//sort jobs
		sortJobs(); 
		
		//job order
		dispatchOrder();
		for(int i=0; i<jobOrder.size(); i++){
			System.out.println("job: " + jobOrder.get(i).getY() + ", " + jobOrder.get(i).getX());
		}
		
		//create event order
		//initEventList(); 
		
		startDispatching(); 
	}
	
	public void sortJobs(){
		Sort sort = new Sort(jobList); 
		q_jobsList = sort.getJobListsSorted(); 
		totalQcCost = sort.getTotalCost(); 
		completeJobsBay = sort.getCompleteBayList(); 
		//jobsCreated = sort.getCompleteBayList(); 

	}
	
	public void dispatchOrder(){
		simpleGreedyTotalCost(); 
	}
	
	public void simpleGreedyTotalCost(){
		//the total cost of all the qc
		int totalSum = 0;
		for(int i=0; i<totalQcCost.length; i++){
			totalSum+= totalQcCost[i]; 
		}
		
		/*
		for(int i=0; i<totalQcCost.length; i++){
			System.out.println("qc index: " + i + " , cost: " + totalQcCost[i]);
		}*/
		
		System.out.println("total sum: " + totalSum);
		
		Job j;
		ArrayList<Job> jarr; 
		int index = 0; 
		while(totalSum>0){
			int max = 0; 
			int maxIndex = 0; 
			
			for(int i=0; i<totalQcCost.length; i++){
				if(totalQcCost[i] > max){
					//System.out.println("total cost: " + totalQcCost[i]); 
					max = totalQcCost[i];
					maxIndex = i; 
				}
			}
			
			/*
			System.out.println("the maxindex joblist size: " + q_jobsList.get(maxIndex).size());
			System.out.println("the total qc cost remainig: " + totalQcCost[maxIndex]);
			*/ 
			
			j = q_jobsList.get(maxIndex).get(0); 
			j.setJobIndex(index);
			
			jobOrder.add(j);
			q_jobsList.get(maxIndex).remove(0); 
			totalQcCost[maxIndex] -= j.getTotalCost(); 
			totalSum -= j.getTotalCost();
			index++; 
			//System.out.println("total sum after removing: " + totalSum);
		}
		
	}
	
	/*
	public void initEventList(){
		for(int i=0; i<jobOrder.size(); i++){
			eventOrder.add(new Event(jobOrder.get(i), 0, 0, jobOrder.get(i).getLoading()));
		}
	}
	*/
	
	public void startDispatching(){
		int noAgv; 	//need to calculate delay due to no agv available <- this, i will deal with it later! 
		Agv idleAgv; 
		Job j; 
		while(Constants.jobsCompleted < Constants.TOTAL_JOB_NO){
			//check if agv idle (if idle, need to add delay times...) 
			noAgv = 0; 
			if(jobOrder.size()>0){
				for(int i=0; i<Constants.AGV; i++){
					if(agvList.get(i).getIdle()){
						agvList.get(i).setIdle(false);
						idleAgv = agvList.get(i); 
						
						j = jobOrder.get(0); 
						System.out.println("job removed from job order: " + j.getY() + ", " + j.getX());
						//eventJob.get(j.getQcIndex()).add(j); 
						jobOrder.remove(0); 
						
						idleAgv.setAgvWaitTime(j);	
						j.setAgv(idleAgv);
						
						eventOrder.get(j.getQcIndex()).add(new Event(j, Constants.TOTALTIME, Constants.TRAVEL,j.getLoading()));	//create 1 travelling event
						System.out.println("event created: " + j.getY() + ", " + j.getX());
						break; 
					}
				}
			}
			
			//sort the events according to occurrence time 
			for(int i=0; i<Constants.NUM_QC; i++){
				Collections.sort(eventOrder.get(i), new EventCompare());
			}
			
			//then, check if any event ended 
			for(int i=0; i<Constants.NUM_QC; i++){
				if(!eventOrder.get(i).isEmpty()){
					int k = 0; 
					while(k<eventOrder.get(i).size()){
						if(eventOrder.get(i).get(k).getTime() == Constants.TOTALTIME){
							if(eventOrder.get(i).get(k).getEventType() == 1){	//if released, remove from the queue 
								eventOrder.get(i).get(k).changeState();
								eventOrder.get(i).remove(k); // this has to be handled..... 
								k = 0;
								continue; 
								
							}else if(eventOrder.get(i).get(k).getEventType() == 0){ //if travelling or delay.
								eventOrder.get(i).get(k).changeState();
								for(int l=0; l<Constants.NUM_QC; l++){
									Collections.sort(eventOrder.get(l), new EventCompare());
								}
								k = 0;
								continue; 
							}else if(eventOrder.get(i).get(k).getEventType() == 2){	//this is for delay jobs 
								eventOrder.get(i).get(k).changeState();
								for(int l=0; l<Constants.NUM_QC; l++){
									Collections.sort(eventOrder.get(l), new EventCompare());
								}
								k = 0;
								continue; 
							}
							
						}
						k++; 
					}
				}
			}
			
			
			//see event job, if any event needs to be created. 
			//for(int i=0)
			
			
			jobList.repaint(); 
			//wait one unit 
			try {
				Thread.sleep(Constants.SLEEP);
				Constants.TOTALDELAY += noAgv; 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//System.out.println("current time: " + Constants.TOTALTIME);
		
		}
	}
	
	class EventCompare implements Comparator<Event>{

		@Override
		public int compare(Event a, Event b) {
			// TODO Auto-generated method stub
			Integer ae = a.getTime(); 
			Integer be = b.getTime();
			return ae.compareTo(be); 
		}
	}
}
