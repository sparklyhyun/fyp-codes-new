package fyp_codes;

import java.util.*; 

public class DispatcherTest2 {
	private JobList jobList;	 

	private ArrayList<Agv> agvList = new ArrayList<>();	//kind of idle list. 
	private ArrayList<Job> q_jobs = new ArrayList<>(); 
	
	//arraylist to store the order of jobs 
	private static ArrayList<Job> jobOrder = new ArrayList<>();
	//private static ArrayList<ArrayList<Job>> eventJob = new ArrayList<>(); 
	
	//event priority queue here
	private static ArrayList<ArrayList<Event>> eventOrder = new ArrayList<>(); 
	
	private static ArrayList<ArrayList<Job>> q_jobsList = new ArrayList<ArrayList<Job>>();  
	private static int[] totalQcCost = new int[Constants.NUM_QC]; 
	
	
	
	public DispatcherTest2(JobList j, int x){
		
		switch(x){
		case 1: // single simulation
			singleSimulation(j); 
			break; 
		case 2:
			multipleSimulation(j); 
			break; 
		default: break; 
		}
		//singleSimulation(j); 
		
		
		/*
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
		
		initDispatcher(); 
		
		//create event order
		//initEventList(); 
		
		startDispatching(); 
		
		//create multiple testing! 
		*/ 
	}
	
	public void singleSimulation( JobList j){
		jobList = j; 
		
		//add agv 
		for(int i=0; i<Constants.AGV; i++){
			agvList.add(new Agv()); 
		}
		
		for(int i=0; i<Constants.NUM_QC; i++){
			eventOrder.add(new ArrayList<Event>()); 
		}
		
		/*
		for(int i=0; i<Constants.NUM_QC; i++){
			eventJob.add(new ArrayList<Job>()); 
		}*/
		
		initDispatcher(); 
		
		//create event order
		//initEventList(); 
		
		startDispatching(); 
		
		//create multiple testing! 
		
		System.out.println("dispatching ended! ");
	}
	
	public void multipleSimulation(JobList j){
		jobList = j; 
	}
	
	public void initArrLists(){
		for(int i=0; i<Constants.AGV; i++){
			agvList.add(new Agv()); 
		}
		
		for(int i=0; i<Constants.NUM_QC; i++){
			eventOrder.add(new ArrayList<Event>()); 
		}
	}
	
	public void resetDispatcher(JobList j){
		//need to reset all the timings
		Constants.TOTALTIME = 0;
		Constants.TOTALDELAY = 0;
		Constants.TRAVELTIME = 0; 
		
		//reset jobs completed
		Constants.jobsCompleted = 0; 
		
		//reset bay 
		Constants.allComplete = 0;
		
		//reset total job no
		Constants.TOTAL_JOB_NO = Constants.TOTAL_SIZE; 
		
		//reset crane used
		for(int i=0; i<Constants.NUM_QC; i++){
			Constants.CRANEUSED[i] = 0; 
		}
		
		//reset all jobs <- create new joblist?? 
		jobList = j; 
		
		//reset agv location
		agvList.clear();
		for(int i=0; i<Constants.AGV; i++){
			agvList.add(new Agv()); 
			//agvList.get(i).resetAgvLocation(); 
			//agvList.get(i).setIdle(true);
		}
		
		//empty event order list
		for(int i=0; i<Constants.NUM_QC; i++){
			eventOrder.clear();
		}
		
		//create a new one??
		eventOrder = new ArrayList<>(); 
		for(int i=0; i<Constants.NUM_QC; i++){
			eventOrder.add(new ArrayList<Event>()); 
		}
		
		jobOrder.clear();
		
		//
		
		
	}
	
	public void initDispatcher(){
		//sort jobs
		sortJobs(); 
		
		//job order
		dispatchOrder();
	}
	
	public void sortJobs(){
		Sort sort = new Sort(jobList); 
		q_jobsList = sort.getJobListsSorted(); 
		totalQcCost = sort.getTotalCost(); 
		Constants.WAITBAY = sort.getCompleteBayList(); 
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
		
		
		//check every 100 units??
		int completeSoFar = 0; 
		
		System.out.println(" \t\t\t\t inside here, jobsCompleted: " + Constants.jobsCompleted + " total job no: " + Constants.TOTAL_JOB_NO);
		
		System.out.println("job order: ");
		for(int i=0; i<jobOrder.size(); i++){
			System.out.print("(" + jobOrder.get(i).getY() + ", " + jobOrder.get(i).getX() + ") , ");
		}
		System.out.println(" ");
		
		while(Constants.jobsCompleted < Constants.TOTAL_JOB_NO){
			
			if(Constants.TOTALTIME % 200 == 0 && Constants.TOTALTIME > 0){
				if(completeSoFar < Constants.jobsCompleted){
					completeSoFar = Constants.jobsCompleted; 
				}else{
					System.out.println("Some job is stuck, terminating....");
					Constants.BUGDETECTED = true; 
					
					jobOrder.clear();
					eventOrder.clear();
					agvList.clear();
					
					System.out.println("event order empty?: " + eventOrder.isEmpty());
					System.out.println("job order empty?: " + jobOrder.isEmpty());

					break; 
				}
			}
			
			if(Constants.BUGDETECTED){
				System.out.println("some job is stuck, terminating.....");
				Constants.TOTALTIME = 0; 
				Constants.BUGDETECTED = true; 
				break; 
			}
			//check if agv idle (if idle, need to add delay times...) 
			noAgv = 0; 
			//System.out.println(" \t\t\t\t inside here2, jobsCompleted: " + Constants.jobsCompleted + " total job no: " + Constants.TOTAL_JOB_NO);
			
			if(jobOrder.size()>0){
				System.out.println(" \t\t\t\t\t\t inside here3");
				for(int i=0; i<Constants.AGV; i++){
					//System.out.println("\t\t\t agv idle?: " + agvList.get(i).getIdle() );
					if(agvList.get(i).getIdle()){
						
						System.out.println("\t\t inside here 4");
						agvList.get(i).setIdle(false);
						idleAgv = agvList.get(i); 
						
						j = jobOrder.get(0); 
						//System.out.println("job removed from job order: " + j.getY() + ", " + j.getX());
						//eventJob.get(j.getQcIndex()).add(j); 
						jobOrder.remove(0); 
						
						idleAgv.setAgvWaitTime(j);	
						j.setAgv(idleAgv);
						
						eventOrder.get(j.getQcIndex()).add(new Event(j, Constants.TOTALTIME, Constants.TRAVEL,j.getLoading(), jobList));	//create 1 travelling event
						//System.out.println("event created: " + j.getY() + ", " + j.getX());
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
								//System.out.println("finish state job: " + eventOrder.get(i).get(k).getJob().getY() + ", " + eventOrder.get(i).get(k).getJob().getX());
								eventOrder.get(i).get(k).changeState();
								
								
								if(!eventOrder.get(i).get(k).getJob().getLoading()){
									eventOrder.get(i).remove(k);
								}else{
									/*
									System.out.println("finish state job state changed: " + eventOrder.get(i).get(k).getJob().getY() + ", " + eventOrder.get(i).get(k).getJob().getX() +
											"event Type: " + eventOrder.get(i).get(k).getEventType());
									*/
									if(eventOrder.get(i).get(k).getEventType() == 1){
										eventOrder.get(i).remove(k);
									}
								}
								
								/*
								for(int l=0; l<Constants.NUM_QC; l++){
									Collections.sort(eventOrder.get(l), new EventCompare());
								}*/
								//eventOrder.get(i).remove(k); // this has to be handled..... 
								
								/*
								for(int m=0; m<Constants.NUM_QC; m++){
									System.out.println("Removed qc: " + m);
									for(int n=0; n<eventOrder.get(m).size(); n++){
										System.out.print(" (" + eventOrder.get(m).get(n).getJob().getY()+ ", " + eventOrder.get(m).get(n).getJob().getX() + "), ");
									}
									System.out.println(" ");
								}*/
								
								k = 0;
								continue; 
								
							}else{ //if travelling or delay or baywait 
								eventOrder.get(i).get(k).changeState();
								
								for(int l=0; l<Constants.NUM_QC; l++){
									Collections.sort(eventOrder.get(l), new EventCompare());
								}
								
								/*
								System.out.println("job state changed: " + eventOrder.get(i).get(k).getJob().getY() + ", " + eventOrder.get(i).get(k).getJob().getX() +
										"event Type: " + eventOrder.get(i).get(k).getEventType());
								*/
								
								//print arraylist
								
								/*
								for(int m=0; m<1; m++){
									System.out.println("qc: " + m);
									for(int n=0; n<eventOrder.get(m).size(); n++){
										System.out.print(" (" + eventOrder.get(m).get(n).getJob().getY()+ ", " + eventOrder.get(m).get(n).getJob().getX() + "), ");
									}
									System.out.println(" ");
								}*/
								
								k = 0;
								continue; 
							}
							
						}
						
						/*
						for(int l=0; l<Constants.NUM_QC; l++){
							Collections.sort(eventOrder.get(l), new EventCompare());
						}*/
						
						k++; 
					}
				}
			}
			
			
			//see event job, if any event needs to be created. 
			//for(int i=0)
			
			
			//add delay if qc not used for more than 1 unit 
			for(int i=0; i<Constants.CRANEUSED.length; i++){
				if(Constants.CRANEUSED[i] < Constants.TOTALTIME-1){
					noAgv++; 
				}
			}
			
			
			jobList.repaint(); 
			//wait one unit 
			try {
				Thread.sleep(Constants.SLEEP);
				Constants.TOTALDELAY += noAgv; 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("current time: " + Constants.TOTALTIME);
		
		}
	}
	
	
	class EventCompare implements Comparator<Event>{

		@Override
		public int compare(Event a, Event b) {
			// TODO Auto-generated method stub
			Integer ae = a.getTime(); 
			Integer be = b.getTime();
			
			if( ae == be){
				Integer ai = a.getJob().getJobIndex(); 
				Integer bi = b.getJob().getJobIndex();
				
				return Integer.compare(ai, bi);
			}
			
			return ae.compareTo(be); 
		}
	}
}
