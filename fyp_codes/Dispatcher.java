package fyp_codes;

/*
 * this is the class that does scheduling 
 * decide which agv is assigned to which qc at which time point 
 * */

import java.awt.List;
import java.util.*;
import javax.swing.*;

import fyp_codes.Greedy.AtomicJob;
import fyp_codes.Greedy.Lock;

import java.util.concurrent.*;	

public class Dispatcher {
	private JobList jobList;	
	//the split list. to change the joblist, get full list x and y from the split list 
	private SplitJobList splitJobList; 

	private ArrayList<Agv> agvList = new ArrayList<>();	//kind of idle list. 
	private ArrayList<Job> q_jobs = new ArrayList<>(); 
	private Lock l = new Lock(); 
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
	
	//private static ArrayList<SplitJobList> splitJobListArr = new ArrayList<>();
	//private static HashMap<String, Integer> totalQcCost = new HashMap<>();
	
	private static ArrayList<ArrayList<Job>> q_jobsList = new ArrayList<ArrayList<Job>>(); 
	//private static HashMap<Integer, Integer> totalQcCost = new HashMap<>(); 
	
	//arraylist index is the same as the qc index and the total cost index!!!
	private static int[] totalQcCost = new int[Constants.NUM_QC]; 
	
	//arraylist to store the order of jobs 
	private static ArrayList<Job> jobOrder = new ArrayList<>(); 
	
	private String name; 
	
	//private static int[] jobCompleted = new int[Constants.NUM_QC]; 
	private static int[][] completeJobsBay; 
	//minus minus until 0, then move to next bay**********************************************
	
	public Dispatcher(JobList j){
		this.jobList = j; 
		
		for(int k=0; k<Constants.AGV; k++){
			Agv agv = new Agv(k); 
			agvList.add(agv); 
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
		while(jobOrder.isEmpty() == false){
			//agvlist empty
			while(agvList.isEmpty() == true){
				System.out.println("agv not available, waiting.....");
				
				try {
					Thread.sleep(Constants.SLEEP);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			Agv idleAgv = agvList.get(0);
			agvList.remove(0);	//agv not idle anymore 
			
			Job j = jobOrder.get(0);
			jobOrder.remove(0); 	//remove the first job in the queue 	\
			
			
			//check if need to wait for bay
			//waitingbay;
			
			String threadName = Integer.toString(j.getY()) + Integer.toString(j.getX()); //set name 
			
			boolean qcWait = false;
			
			//make this more intricate 
			if(j.getLoading() == false){
				qcWait = true; 
			}
			
			AtomicJob a = new AtomicJob(j, threadName, idleAgv, qcWait);
			jobNo_created++; 
			
			atomicJobList.add(a);
			
			a.start(); 
			
			try {
				Thread.sleep(Constants.SLEEP);
				//Constants.TOTALTIME++; 
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
	*/ 

	public void waitingBay(){
		//need to change this to use the complete job array
		//dd
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
			jobList.repaint();
			//System.out.println("agv: " + this.agv.getAgvNum() + "qcWait: " + qcWait);
			
			//empty agv list, need to wait for agv
			if(qcWait == true){//waiting for the agv
				//jobList.getJob(j.getY(), j.getX()).setIsWaiting(true);
				jobList.getJob(j.getY(), j.getX()).setIsWaiting(true);
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

			//jobList.getJob(j.getY(), j.getX()).setIsWaiting(false);
			jobList.getJob(j.getY(), j.getX()).setIsWaiting(false);
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
				j.setIsWaiting(false);
				jobList.repaint();
			}
			
			
		}
		
		public void completeTask(){ //this one is for unloading 				
			
			j.setComplete();
			jobList.repaint();
			
			//int y = splitJobList.getSplitListY();
			int y = j.getSplitY(); 
			
			int nexty = j.getY()+1;
			
			
			//figure out what this function is for lollll 
			
			//lets remove this and see what happens
			
			/*
			if(nexty+1 <= (y+1)*Constants.MAX_Y){
				//System.out.println("----------------next one waiting: "+ nexty + ", " + j.getX() + ", " + jobList.getJob(nexty, j.getX()).getIsWaiting());
				//not even reaching this stage somehow.....
				if(jobList.getJob(nexty, j.getX()).getIsWaiting() == true){
					System.out.println("job y: " + nexty + ", x: " + j.getX() + ", no longer waiting........");
					jobList.getJob(nexty, j.getX()).setIsWaiting(false);	//next job no longer waiting
					jobList.repaint();
				}
			}*/ 
			
			System.out.println("agv added to the queue, new queue length: " + agvList.size());
			
			//jobList.getJob(j.getY(), j.getX()).setComplete();
			//jobList.getJob(j.getSplitY(), j.getSplitX()); 
			
			jobList.repaint();
			System.out.println("job " + j.getY() + ", " + j.getX()+ " completed");
			
			
			
		}
		
		@SuppressWarnings("deprecation")
		public void paused(){
			t.suspend();
		}
	}
}
