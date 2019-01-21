package fyp_codes;

/*
 * this is the class that does scheduling 
 * decide which agv is assigned to which qc at which time point 
 * */

import java.awt.List;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import fyp_codes.Greedy.AtomicJob;
import fyp_codes.Greedy.Lock;

import java.util.concurrent.*;	

public class Dispatcher {
	private JobList jobList;	
	//the split list. to change the joblist, get full list x and y from the split list 
	private SplitJobList splitJobList; 

	private ArrayList<Agv> agvList = new ArrayList<>();	//kind of idle list. 
	private ArrayList<Job> q_jobs = new ArrayList<>(); 
	//private Lock l = new Lock(); 
	private Lock[] lockArr = new Lock[Constants.NUM_QC];
	
	private boolean greedyComplete = false; 
	private int jobNo = 0; //for completed jobs 
	private int jobNo_created = 0; 
	
	//wait until bay is complete. then move on to the next bay.
	private boolean bayComplete = false; 
	
	//to store the job that is not waiting anymore...
	private ArrayList<Job> waitingJob = new ArrayList<>();

	private boolean qcWaiting = true; // false - no need to wait for qc 
	
	//to pause the execution
	ArrayList<AtomicJob> atomicJobList = new ArrayList<>(); 
	
	private static ArrayList<ArrayList<Job>> q_jobsList = new ArrayList<ArrayList<Job>>();  
	
	//arraylist index is the same as the qc index and the total cost index!!!
	private static int[] totalQcCost = new int[Constants.NUM_QC]; 
	
	//arraylist to store the order of jobs 
	private static ArrayList<Job> jobOrder = new ArrayList<>(); 
	
	private String name; 
	
	//private static int[] jobCompleted = new int[Constants.NUM_QC]; 
	private static int[][] completeJobsBay; 
	//minus minus until 0, then move to next bay**********************************************
	
	private static int incompleteQc = Constants.NUM_QC; 
	
	private static int[][] jobsCreated = new int[Constants.NUM_QC][Constants.NUM_BAY];
	
	private static ArrayList<ArrayList<Job>> bayWait = new ArrayList<>(); //number of jobs waiting for the bay 
	
	//private static int[] bayWait = new int[Constants.NUM_QC]; //to keep track of whether to wait in front or not 
	
	private static boolean bayWaitBool = false; 
	
	//Semaphore sem; //need 1 for each qc. can I do an array of semaphores??
	//Semaphore[] sem = new Semaphore[Constants.NUM_QC]; //well looks like I can! 
	
	public Dispatcher(JobList j){
		this.jobList = j; 
		
		for(int k=0; k<Constants.AGV; k++){
			Agv agv = new Agv(k); 
			agvList.add(agv); 
		}
		/*
		for(int i=0; i<Constants.NUM_QC; i++){
			sem[i] = new Semaphore(1); //1 semaphore each qc? 
		}*/ 
		
		/*
		for(int i=0; i<Constants.NUM_QC; i++){
			Queue<Job> q_job = new LinkedList<>(); 
			bayWait.add(q_job); 
		}*/
		
		for(int i=0; i<Constants.NUM_QC; i++){
			ArrayList<Job> waitJobs = new ArrayList<>();
			bayWait.add(waitJobs); 
		}
		
		//initialize lock 
		for(int i=0; i<Constants.NUM_QC; i++){
			lockArr[i] = new Lock(); 
			//bayWait[i] = 0; //initializa bayWait
		}
		
		
		sortJobs(); 
		
		dispatchOrder(); 
		
		startDispatching(); 
	}
	
	//sort split jobs here
	public void sortJobs(){
		Sort sort = new Sort(jobList); 
		q_jobsList = sort.getJobListsSorted(); 
		totalQcCost = sort.getTotalCost(); 
		completeJobsBay = sort.getCompleteBayList(); 
		//jobsCreated = sort.getCompleteBayList(); 
		
		for(int i=0; i<Constants.NUM_QC; i++){
			jobsCreated[i] = Arrays.copyOf(completeJobsBay[i], completeJobsBay[i].length); 
		}
	}
	
	//now the dispatching algorithms.
	/* 1. start with simple greedy. higher cost dispatch agv first
	 * 2. the order of jobs will be 
	 * */
	
	public void dispatchOrder(){
		simpleGreedy(); 
	}
	
	//dispatch order baseline - simple greedy. The highest cost dispatch agv first. 
	public void simpleGreedy(){
		//the total cost of all the qc
		int totalSum = 0;
		for(int i=0; i<totalQcCost.length; i++){
			totalSum+= totalQcCost[i]; 
		}
		
		
		for(int i=0; i<totalQcCost.length; i++){
			System.out.println("qc index: " + i + " , cost: " + totalQcCost[i]);
		}
		
		System.out.println("total sum: " + totalSum);
		
		Job j;
		ArrayList<Job> jarr; 
		
		while(totalSum>0){
			int max = 0; 
			int maxIndex = 0; 
			
			for(int i=0; i<totalQcCost.length; i++){
				if(totalQcCost[i] > max){
					System.out.println("total cost: " + totalQcCost[i]); 
					max = totalQcCost[i];
					maxIndex = i; 
				}
			}
			
			System.out.println("simple greedy, max index = " + maxIndex);
			//System.out.print("q_jobsList size = " + q_jobsList.size());
			
			jarr = q_jobsList.get(maxIndex);
			
			System.out.println("jarr.size =  " + jarr.size());
			
			j = jarr.get(0); 
			
			jobOrder.add(j);
			q_jobsList.get(maxIndex).remove(0); 
			totalQcCost[maxIndex] -= j.getTotalCost(); 
			totalSum -= j.getTotalCost();
			System.out.println("total sum after removing: " + totalSum);
		}
	}
	
	public void startDispatching(){
		int prevQcIndex = -1; 
		
		while(jobOrder.isEmpty() == false){
			//agvlist empty
			while(agvList.isEmpty() == true){
				System.out.println("agv not available, waiting.....");
				
				try {
					Thread.sleep(Constants.SLEEP);
					Constants.TOTALDELAY += incompleteQc; 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			Agv idleAgv = agvList.get(0);
			agvList.remove(0);	//agv not idle anymore 
			
			Job j = jobOrder.get(0);
			jobOrder.remove(0); 	//remove the first job in the queue 	\
			
			String threadName = Integer.toString(j.getY()) + Integer.toString(j.getX()); //set name 
			
			boolean qcWait = false;
			
			//make this more intricate 
			if(j.getLoading() == false){ //if unloading job 
				qcWait = true; 
				
				//if previous one has the same index 
				/*
				if(bayWait[j.getQcIndex()]>0){
					bayWait[j.getQcIndex()]++; 
				}
				if((bayWait[j.getQcIndex()]>0) && (prevQcIndex == j.getQcIndex())){
					qcWait = true; 
				}*/
				
			}
			
			//set previous qc index to determine whether to put the delay in front or not (for unloading) 
			prevQcIndex = j.getQcIndex(); 
			
			AtomicJob a = new AtomicJob(j, threadName, idleAgv, qcWait);
			jobNo_created++; 
			
			atomicJobList.add(a);
			
			a.start(); 
			
			
			
			//add the delay for all other qcs 
			Constants.TOTALDELAY += incompleteQc-1; 
			System.out.println("incomplete qc = " + incompleteQc);
			
			try {
				Thread.sleep(Constants.SLEEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	/*
	
	public void startMergedGreedy(){
		for(int l=0; l<numBays; l++){
			while(q_jobs.isEmpty() == false){
				if(q_jobs.size()>20){
					showExecutionUnloadingMerged();	//need to edit wait for bays part 
				}else{
					showExecutionLoadingMerged(); 
				}
			}
			
			waitingBay();
			jobNo = 0; 
			
		}
		
		Constants.allComplete++; 
		System.out.println("Thread ended, thread name: " + name + "\n");
	}
	
	*/ 
		
	/*
	public void showExecutionLoadingMerged(){
		//ArrayList<AtomicJob> atomicJobList = new ArrayList<>();
		while(q_jobs.size()>0){
			while(agvList.isEmpty() == true){
				System.out.println("agv not available, waiting.....");
				
				try {
					Thread.sleep(Constants.SLEEP);
					//Constants.TOTALTIME++; 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			System.out.println("agvList size: " + agvList.size());
			
			
			//2nd check, if agv really available
			if(agvList.isEmpty() == true){
				continue; 
			}
			
			
			//before assinging agv, acquire sem
			
			try {
				sem.acquire();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Agv idleAgv = agvList.get(0);
			agvList.remove(0);	//agv not idle anymore 
			
			Job j = q_jobs.get(0);
			q_jobs.remove(0); 	//remove the first job in the queue 					
			
			String threadName = Integer.toString(j.getY()) + Integer.toString(j.getX()); //set name 
					
			AtomicJob a = new AtomicJob(j, threadName, idleAgv, false);
			jobNo_created++; 
			
			atomicJobList.add(a);
			
			//System.out.println("qc thread: " + name + ", job y: " + a.getJob().getY() + ", x: " + a.getJob().getX());
			
			a.start(); 
			
			try {
				Thread.sleep(Constants.SLEEP);
				//Constants.TOTALTIME++; 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public void showExecutionUnloadingMerged(){
		//the merged one 
		//ArrayList<AtomicJob> atomicJobList = new ArrayList<>(); 
		
		while(q_jobs.size()>20){
			System.out.println("agv list empty: " + agvList.isEmpty());
			
			while(agvList.isEmpty()==true){
				System.out.println("agv not available, waiting.....");
				
				try {
					Thread.sleep(Constants.SLEEP);
					//Constants.TOTALTIME++; 
					Constants.TOTALDELAY++;
					updateDelayTimer();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				qcWaiting = false; 
				break;
			}
			
			
			Agv idleAgv = agvList.get(0);
			agvList.remove(0);	//agv not idle anymore 
			
			Job j = q_jobs.get(0);
			q_jobs.remove(0); 	//remove the first job in the queue 	
						
			String threadName = Integer.toString(j.getY()) + Integer.toString(j.getX()); //set name 
					
			AtomicJob a = new AtomicJob(j, threadName, idleAgv, qcWaiting);
			jobNo_created++; 
			
			atomicJobList.add(a);
			
			
			System.out.println("qc thread: " + name + ", job y: " + a.getJob().getY() + ", x: " + a.getJob().getX());
			a.start(); 
			
			qcWaiting = true; 
			
			try {
				Thread.sleep(Constants.SLEEP);
				//Constants.TOTALTIME++; 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			}
		}

	public void waitingBay(){
		while(jobNo < 40){
			System.out.println("waiting for the bay to complete all loading jobs...............");
			try {
				Thread.sleep(Constants.SLEEP);
				updateTotalTimer();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
*/

	public void showJobSeq(){
		for(int i=0; i<q_jobs.size(); i++){
			System.out.println("job " + i + ", i = " + q_jobs.get(i).getY() + ", j = " + q_jobs.get(i).getX() 
					+ ", cost = " + q_jobs.get(i).getTotalCost());
		}
	}
	
	public boolean getGreedyComplete(){
		return greedyComplete; 
	}
	
	//update the timers 
	
	public void updateDelayTimer(){
		Constants.TIMERS.updateDelayTimer();
	}
	
	public void updateTotalTimer(){
		Constants.TIMERS.updateTotalTimer();
	}
	
	//test if the split job list is working
	public void seeSplitJobList(JobList jl){
		//System.out.println(jl);
		
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.QC_X; j++){
				System.out.print("splitjob retrieved, y: " + jl.getJob(i, j).getY() + ", x: " + jl.getJob(i, j).getX());
			}
		}
		
		
	}
		
	
	class Lock{
		private AtomicJob aj; 
		//private boolean completion; 
		
		public void lock(AtomicJob aj, boolean complete){
			this.aj = aj;
			if(complete){
				aj.completeTask();
				try {
					Thread.sleep(Constants.SLEEP);
					//System.out.println("completed jobs: " + jobNo);
					//jobNo+= 1;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				aj.getJob().setAssigned();
				try {
					Thread.sleep(Constants.SLEEP);
					System.out.println("qc released");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		public void unloadWaitLock(AtomicJob aj){
			this.aj = aj;
			//assign waiting false 
			aj.notWaiting();
		}
	}
	
	
	class AtomicJob implements Runnable{
		Job j; 
		private Thread t; 
		private String name; 
		private Agv agv;
		private boolean qcWait;	//true- unloading not first, false- unloading first/ loading. true - need to set delay 1 unit before  
		
		public AtomicJob(Job j, String name, Agv agv, boolean qcWait ){	
			this.j = j; 
			this.name = name;
			this.qcWait = qcWait; 
		}
		
		@Override
		public void run() {	
			jobsCreated[j.getQcIndex()][j.getBayIndex()]--; 
			if(jobsCreated[j.getQcIndex()][2]<1){
				incompleteQc--; 
			}
			
			if(j.getLoading() == true){
				travelingLoading(agv);
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}else{		
				travelingUnloading(agv); 
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
			
			
			
		}
		
		public void start(){
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
		
		public void setAgv(Agv agv){
			this.agv = agv; 
		}
		
		public Agv getAgv(){
			return agv; 
		}
		
		public void travelingLoading(Agv agv){
			
			j.setAssigned();
			j.setIsWaiting(false);
			
			jobList.repaint();
			System.out.println("job " + j.getY() + ", " + j.getX()+ " on agv");

			
			//here, delay for traveling. Hold the agv. 
			int c = j.getTotalCost();
			System.out.println("sleep for: " + c + " units");
			
			while(c > 0){
				c--;
				try {
					Thread.sleep(Constants.SLEEP);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			//if the previous job (column) not complete, wait
			if(j.getY()-1 >= 0){
				Job prev = jobList.getJob(j.getY()-1, j.getX());
				if(prev.getLoading() == true && prev.getComplete() == false){
					System.out.println("previous job unfinished. waiting.............................." + (j.getY()-1) + ", " + j.getX());
					jobList.getJob(j.getY(), j.getX()).setIsWaiting(true);
					jobList.repaint();
				}
				
				while(prev.getComplete() == false){
					try {
						updateDelayTimer();
						Thread.sleep(Constants.SLEEP);
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Constants.TOTALDELAY++;	//For now, adds one more at the end. 
					
				}

				
			}
			
			//do i put it here? (no but it has to be together with complete) 
			/*
			jobList.getJob(j.getY(), j.getX()).setIsWaiting(false);
			jobList.repaint();
			*/
			
			
			waitForBay(); 
			
			int qcIndex = j.getQcIndex();
			Lock l = lockArr[qcIndex]; 
			
			//complete the job
			synchronized(l){
				agvList.add(agv);
				
				//only release sem after agv is added back
				//sem.release();
				
				l.lock(this, true);
			}
			
			System.out.println("complete jobs bay: " + j.getQcIndex()+ ", " + j.getBayIndex() + ", jobs left: " + completeJobsBay[j.getQcIndex()][j.getBayIndex()]);
			completeJobsBay[j.getQcIndex()][j.getBayIndex()]--; 
			

		}
		
		public void travelingUnloading(Agv agv){
			waitForBay();
			
			j.setAssigned();
			//jobList.repaint();
			//System.out.println("agv: " + this.agv.getAgvNum() + "qcWait: " + qcWait);
			
			//empty agv list, need to wait for agv
			if((qcWait == true) || (bayWait[j.getQcIndex()]>0)){//waiting for the agv
				j.setIsWaiting(true);
				
				jobList.repaint();
				
				//System.out.println("job " + j.getY() + ", " + j.getX()+ " null agv, waiting for agv");
				System.out.println("job " + j.getY() + ", " + j.getX()+ " null agv, waiting for agv");
				
				try {
					Constants.TOTALDELAY++;
					//updateDelayTimer();
					Thread.sleep(Constants.SLEEP);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(j.getIsWaiting()){
				int qcIndex = j.getQcIndex(); 
				Lock l = lockArr[qcIndex]; 
				synchronized(l){
					l.unloadWaitLock(this);
				}
			}
			
			
			j.setTravelling(true);
			jobList.repaint();
			
			//here, delay for traveling. Hold the agv. 
			int c = j.getBeforeTravelCost();
			System.out.println("before travelling, sleep for: " + c + " units");
			
			while(c > 0){
				c--;
				try {
					Thread.sleep(Constants.SLEEP);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//job completion can happen at the same time
			completeTask();
			
			System.out.println("complete jobs bay: " + j.getQcIndex()+ ", " + j.getBayIndex() + ", jobs left: " + completeJobsBay[j.getQcIndex()][j.getBayIndex()]);
			completeJobsBay[j.getQcIndex()][j.getBayIndex()]--; 
			
			agvList.add(agv);
			
			//release sem only after agv is added back
			//sem.release();
			
			System.out.println("agv added back..........................................." );
			System.out.println("new agv list size: " + agvList.size());
			jobNo+= 1;
		}
		
		
		public void waitForBay(){
			//so... is the bay no count greater than 0, wait until 0
			int qcIndex = j.getQcIndex();
			int bayIndex = j.getBayIndex(); 
			
			if(bayIndex > 0){
				if(j.getLoading() == false){
					if(completeJobsBay[qcIndex][bayIndex-1] > 0){
						bayWait[qcIndex]++; 
						System.out.println("baywait index: " + qcIndex + ", baywait value: " + bayWait[qcIndex]);
					}
				}
				while(completeJobsBay[qcIndex][bayIndex-1] > 0){
					//wait
					j.setIsWaiting(true);
					jobList.repaint();
					try {
						Constants.TOTALDELAY++;;
						Thread.sleep(Constants.SLEEP);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(j.getLoading() == false){
					if(this.qcWait == false){
						Lock l = lockArr[qcIndex]; 
						synchronized(l){
							l.unloadWaitLock(this);
						}
					}
				}else{
					j.setIsWaiting(false);
					jobList.repaint();
				}
			}
			
			
		}
		
		public void notWaiting(){
			//changed here!! to solve the wait-don't wait- wait blinking problem 
			j.setIsWaiting(false);
			j.setAssigned();
			jobList.repaint();
			try {
				Thread.sleep(Constants.SLEEP);
				if(bayWait[j.getQcIndex()]>0){
					bayWait[j.getQcIndex()]--; 
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		public void completeTask(){ //this one is for unloading 				
			
			j.setComplete();
			jobList.repaint();
			
			//int y = splitJobList.getSplitListY();
			int y = j.getSplitY();
			
			int nexty = j.getY()+1;
			
			
			//System.out.println("max y: " + Constants.MAX_Y);
			//System.out.println("splity: " + y);
			//System.out.println("nexty: " + nexty + ", limit: "+(y+1)*Constants.MAX_Y+", condition: " + (nexty < (y+1)*Constants.MAX_Y));
			
			///problem is here. the index stops at nexty index out of range
			if(j.getLoading()){
				if(nexty < (y+1)*Constants.MAX_Y){
					System.out.println("----------------next one waiting: "+ nexty + ", " + j.getX() + ", " + jobList.getJob(nexty, j.getX()).getIsWaiting());
					//not even reaching this stage somehow.....
					if(jobList.getJob(nexty, j.getX()).getIsWaiting() == true){
						System.out.println("job y: " + nexty + ", x: " + j.getX() + ", no longer waiting........");
						jobList.getJob(nexty, j.getX()).setIsWaiting(false);	//next job no longer waiting
						jobList.repaint();
					}
				}
			}
			
			
			System.out.println("agv added to the queue, new queue length: " + agvList.size());
			
			//jobList.getJob(j.getY(), j.getX()).setComplete();
			//jobList.getJob(j.getSplitY(), j.getSplitX()); 
			
			jobList.repaint();
			System.out.println("job " + j.getY() + ", " + j.getX()+ " completed");
			Constants.jobsCompleted++; 
			
		}

		
		@SuppressWarnings("deprecation")
		public void paused(){
			t.suspend();
		}
	}
}
