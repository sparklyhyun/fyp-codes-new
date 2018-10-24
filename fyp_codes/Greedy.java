package fyp_codes;

import java.util.*;


public class Greedy {
	public static JobList jobList;
	public static ArrayList<Agv> agvList;	//kind of idle list. 
	private static ArrayList<Job> q_jobs = new ArrayList<>(); 
	Lock l = new Lock(); 
	private static boolean complete = false; 
	private static int totalDelay = 0;
	private static int totalCostEverything = 0; 
	private static int jobNo =0 ; 
	
	public Greedy(JobList j, ArrayList<Agv> agvL){
		this.jobList = j; 
		this.agvList = agvL; 
	}
	
	public void startGreedy1(){	//generic greedy algorithm, start from first row, then move onto the next row
		//cost = just total cost
		long startTime = System.nanoTime();  //to see the performance
		
		Job[] sortArray = new Job[Constants.MAX_X];	//for sorting purpose 
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				sortArray[j] = jobList.getJob(i, j);
				totalCostEverything += jobList.getJob(i, j).getTotalCost();
			}
			sortArray = sortDecending(sortArray);
			for(int k=0; k<Constants.MAX_X; k++){
				q_jobs.add(sortArray[k]);
			}
		}
		
		//q_jobs.get(q_jobs.size()-1).setLastJob();
		
		long endtime = System.nanoTime()-startTime;
		System.out.println("time taken for scheduling: " + endtime);
		
		showJobSeq(); 
		
		long startTime2 = System.currentTimeMillis();
		showExecution();		
		
		
		System.out.println("all jobs ended---------------------------------" );
		
		
	}
	
	public void startGreedy2(){	//1 item lookahead
		long startTime = System.nanoTime();  //to see the performance
		
		Job[] sortArray = new Job[Constants.MAX_X];	//for sorting purpose 
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				sortArray[j] = jobList.getJob(i, j);
			}
			sortArray = sortDecending(sortArray);
			
			for(int k=0; k<Constants.MAX_X; k++){
				//new step, check next item and add the next item in front if it has higher cost
				int y = sortArray[k].getY();
				int nexty = y+1;
				int x = sortArray[k].getX();
				int count = 0; 
				
				if(nexty<Constants.MAX_Y && count < 3){
					Job nextJob = jobList.getJob(nexty, x); 
					if(jobList.getJob(y, x).getVisited()==false){
						//compare, when setting visited, set in the joblist
						int nextCost = nextJob.getTotalCost();
						if(nextCost > sortArray[k].getTotalCost()){
							//add next task into the queue first 
							q_jobs.add(nextJob);
							jobList.getJob(nexty, x).setVisited();
							
							count++; 
						}
						q_jobs.add(sortArray[k]);
					}
				}else{
					if(jobList.getJob(y, x).getVisited()==false){
						q_jobs.add(sortArray[k]);
					}
				}
				
			}
		}
		long endtime = System.nanoTime()-startTime;
		System.out.println("time taken for scheduling: " + endtime);
		
		showJobSeq(); 
		
		long startTime2 = System.currentTimeMillis();
		showExecution();
		
		while(true){
			//System.out.println("\t\t\t\t\t\t\t\t\t\t" + jobNo);

			if(jobNo >= 40){
				System.out.println("-------------------all jobs complete---------------");
				break;
			}
			try {
				Thread.sleep(Constants.SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//System.out.println("-------------------all jobs complete---------------");
		long endtime2 = System.currentTimeMillis() - startTime2; 
		System.out.println("time taken until job completion: " + endtime2);
	}
	
	
	public Job[] sortDecending(Job[] arr){	//add high cost first
		//simple bubble sort 
		for(int i=Constants.MAX_X-1; i>0; i--){
			for(int j=0; j<i; j++){
				if(arr[j].getTotalCost()<arr[j+1].getTotalCost()){
					Job temp = arr[j];
					arr[j] = arr[j+1];
					arr[j+1] = temp; 
				}
				
			}
		}
		return arr; 
	}
		
	public void updateSimulator(){
			
		for(int k=0; k<q_jobs.size(); k++){
			jobList.repaint();
			q_jobs.get(k).setComplete();
				
			//wait for 1 second 
			try {
				Thread.sleep(Constants.SLEEP);
				totalDelay++;
				totalCostEverything++; 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void showExecution(){
		ArrayList<AtomicJob> atomicJobList = new ArrayList<>(); 
		//wait if agv list is empty
		while(q_jobs.isEmpty()==false){
			//System.out.println("\t\t\t\t\t is job empty: " + q_jobs.isEmpty());
			//wait until there is idle agv
			while(true){
				if(agvList.isEmpty()==false){
					try {
						Thread.sleep(Constants.SLEEP);
						totalDelay++;
						totalCostEverything++; 
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
							
					System.out.println("agvList size: " + agvList.size());
					Agv idleAgv = agvList.get(0);
					//System.out.println("agv removed from the waiting queue: " + idleAgv.getAgvNum());
					agvList.remove(0);	//agv not idle anymore 
							
					Job j = q_jobs.get(0);
					q_jobs.remove(0); 	//remove the first job in the queue 
					String threadName = Integer.toString(j.getY()) + Integer.toString(j.getX()); //set name 
					//System.out.println("job i,j: " + threadName);
							
					AtomicJob a = new AtomicJob(j, threadName, idleAgv);
					atomicJobList.add(a);
					a.start(); 
							
					//agv should be added the queue when the job completes! 
					//agvList.add(idleAgv);
					//System.out.println("agv added to the queue, new queue length" + agvList.size());
							
					break;
				}
				System.out.println("agv not available, waiting.....");
				
				try {
					Thread.sleep(Constants.SLEEP);
					totalDelay++;
					totalCostEverything++; 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
		//join to track children threads end time
		/*
		for(int i=0; i<atomicJobList.size(); i++){
			try {
				atomicJobList.get(i).getThread().join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}*/	
		
		
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
	}
	

	public void showJobSeq(){
		for(int i=0; i<q_jobs.size(); i++){
			System.out.println("job " + i + ", i = " + q_jobs.get(i).getY() + ", j = " + q_jobs.get(i).getX() 
					+ ", cost = " + q_jobs.get(i).getTotalCost());
		}
	}
	
	public int idealStart(Job prev, Job curr){	//start from index 2
		int newIdealStart = prev.getTotalCost(); //ideal start time is when prev just finishes drop off 
		return 0; 
	}
	
	class Lock{
		private AtomicJob aj; 
		private boolean completion; 
		
		public void lock(AtomicJob aj, boolean complete){
			this.aj = aj;
			if(complete){
				System.out.println("qc locked, completion on the way");
				aj.completeTask();
				
				try {
					Thread.sleep(Constants.SLEEP);
					totalDelay++;
					totalCostEverything++; 
					System.out.println("qc released");
					System.out.println("completed jobs: " + jobNo);
					jobNo+= 1;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				System.out.println("qc locked, asignment on the way");
				aj.getJob().setAssigned();
				try {
					//System.out.println("wait for qc release");
					Thread.sleep(Constants.SLEEP);
					totalDelay++;
					totalCostEverything++; 
					System.out.println("qc released");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		
	}
	
	//create thread class
	
	class AtomicJob implements Runnable{
		Job j; 
		private Thread t; 
		private String name; 
		private Agv agv;
		
		public AtomicJob(Job j, String name, Agv agv){	//update this, add shared resource 
			this.j = j; 
			this.name = name;
			//System.out.println("thread name: " + this.name);
		}
		
		@Override
		public void run() {	
			//System.out.println("run start");
			
			traveling(agv);
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			//t.interrupt();	//end of the thread
			// TODO Auto-generated method stub
			
		}
		
		public void start(){
			//System.out.println("starting thread " + name);
			if(t==null){
				t = new Thread(this, name);
				t.start();
				
			}
		}
		
		public Job getJob(){
			return j; 
		}
		
		public Thread getThread(){
			return t; 
		}
		
		public void traveling(Agv agv){
			//set job assigned first
			j.setAssigned();
		
			System.out.println("job " + j.getY() + ", " + j.getX()+ " on agv");
			
			jobList.repaint();
			
			//here, delay for traveling. Hold the agv. 
			int c = j.getTotalCost();
			// wait for total cost
			try {
				System.out.println("sleep for: " + c + " units");
				Thread.sleep(Constants.SLEEP * c);
				totalDelay += c;
				totalCostEverything += c; 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//if the previous job (column) not complete, wait
			if(j.getY()-1 >= 0){
				Job prev = jobList.getJob(j.getY()-1, j.getX());
				while(prev.getComplete() == false){
					System.out.println("waiting for previous job to finish.....");
					System.out.println("prev i, j: " + prev.getY() + ", " + prev.getX()); 
					try {
						Thread.sleep(Constants.SLEEP);
						totalDelay++;
						totalCostEverything++; 
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			synchronized(l){
				agvList.add(agv);
				l.lock(this, true);
			}
			
			
		}
		
		public void completeTask(){				
			j.setComplete();
			System.out.println("agv added to the queue, new queue length: " + agvList.size());
			jobList.getJob(j.getY(), j.getX()).setComplete();
			jobList.repaint();
			//occupied = false; 
			System.out.println("job " + j.getY() + ", " + j.getX()+ " completed");
			
			
			//remove this? 
			
				
		}
		
	}
	
	
	
	
}
