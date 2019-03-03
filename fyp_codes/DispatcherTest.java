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
import java.util.concurrent.locks.ReentrantLock;	

public class DispatcherTest {
	private JobList jobList;	
	//the split list. to change the joblist, get full list x and y from the split list 
	private SplitJobList splitJobList; 

	private ArrayList<Agv> agvList = new ArrayList<>();	//kind of idle list. 
	private ArrayList<Job> q_jobs = new ArrayList<>(); 
	//private Lock l = new Lock(); 
	private ReentrantLock[] lockArr = new ReentrantLock[Constants.NUM_QC];
	
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
	private static ArrayList<ArrayList<Agv>> bayWaitAgv = new ArrayList<>(); //store agv assigned to each of the jobs waiting for the bay 
	private static ArrayList<ArrayList<Job>> agvWaitJobs = new ArrayList<>(); // store jobs waiting for agv for pick up 
	
	private static ArrayList<ArrayList<Job>> unloadWait = new ArrayList<>(); //unload jobs waiting for prev ones to be loaded onto agv 
	private static ArrayList<ArrayList<Job>> agvWait = new ArrayList<>(); // agv waiting for jobs 
	
	//private static int[] bayWait = new int[Constants.NUM_QC]; //to keep track of whether to wait in front or not 
	
	private static boolean[] unloadBayWaitBool = {false, false, false, false}; 
	
	//public static int prevWaitEnded = -1; 
	public static int[] prevWaitEnded = {0,0,0,0}; 
	
	public DispatcherTest(JobList j){
		this.jobList = j; 
		
		for(int k=0; k<Constants.AGV; k++){
			Agv agv = new Agv(k); //need to initialize agv location. is it random?? yeah lets say its random first. 
			agvList.add(agv); 
		}
		
		for(int i=0; i<Constants.NUM_QC; i++){
			ArrayList<Job> waitJobs = new ArrayList<>();
			bayWait.add(waitJobs); 
		}
		
		for(int i=0; i<Constants.NUM_QC; i++){
			ArrayList<Job> unloadWaitJobs = new ArrayList<>(); 
			unloadWait.add(unloadWaitJobs); 
		}
		
		for(int i=0; i<Constants.NUM_QC; i++){
			ArrayList<Agv> waitAgv = new ArrayList<>();
			bayWaitAgv.add(waitAgv); 
		}
		
		
		for(int i=0; i<Constants.NUM_QC; i++){
			ArrayList<Job> agvWait = new ArrayList<>();
			agvWaitJobs.add(agvWait); 
		}
		
		
		for(int i=0; i<Constants.NUM_QC; i++){
			ArrayList<Job> agvWaiting = new ArrayList<>(); 
			agvWait.add(agvWaiting); 
		}
		
		//initialize lock 
		for(int i=0; i<Constants.NUM_QC; i++){
			lockArr[i] = new ReentrantLock(); 
			//bayWait[i] = 0; //initializa bayWait
		}
		
		
		
		sortJobs(); 
		
		// for greedy method 
		dispatchOrder(); 
		startDispatching(); 
		
		// for nearest agv method?
		
		
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
	
	public void dispatchOrder(){
		simpleGreedyTotalCost(); 
		//tabuSearch(); 
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
			
			jobOrder.add(j);
			q_jobsList.get(maxIndex).remove(0); 
			totalQcCost[maxIndex] -= j.getTotalCost(); 
			totalSum -= j.getTotalCost();
			//System.out.println("total sum after removing: " + totalSum);
		}
		
	}
	
	public void tabuSearch(){
		ArrayList<Job> initialJobList = new ArrayList<>(); 
		ArrayList<Job> finalJobList = new ArrayList<>(); 
		//need to find the initial solution (simple greedy method) --> need to change, sort 4 at a time 
		
		Job j; 
		
		int totalSum = 0;
		for(int i=0; i<totalQcCost.length; i++){
			totalSum+= totalQcCost[i]; 
		}
		
		for(int i=0; i<totalQcCost.length; i++){
			System.out.println("qc index: " + i + " , cost: " + totalQcCost[i]);
		}
		
		System.out.println("total sum: " + totalSum);
		
		//ArrayList<Job> jarr; 
		//get initial solution 
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
			
			j = q_jobsList.get(maxIndex).get(0);
			
			initialJobList.add(j);
			q_jobsList.get(maxIndex).remove(0); 
			totalQcCost[maxIndex] -= j.getTotalCost(); 
			totalSum -= j.getTotalCost();
			//System.out.println("total sum after removing: " + totalSum);
		}
		

		
		ArrayList<Job> initialPart = new ArrayList<>();  //frame
		ArrayList<Job> neighbourPart = new ArrayList<>(); //neighbouring frame 
		int pt = 0; //pointer to point to the start of the partial frame
		Job temp; 	//for generating neighbouring solution 
		
		while(pt < initialJobList.size()){
			System.out.println("\npt: " + pt);
			//initialize the initial frame and the neighbouring frame 
			for(int i=pt; i< Math.min((pt+Constants.NUM_QC), initialJobList.size()); i++){
				initialPart.add(initialJobList.get(i)); 
				neighbourPart.add(initialJobList.get(i)); 

			}
			
			//generate neighbouring solution
			for(int i=1; i<initialPart.size()-1; i++){
				//swap 
				temp = neighbourPart.get(i+1); 
				neighbourPart.set(i+1, neighbourPart.get(i)); 
				neighbourPart.set(i, temp); 
				
				//then calculate the total makespan of both
				int initialMakespan = calcMakeSpan(initialPart); 
				int neighbourMakespan = calcMakeSpan(neighbourPart);
				
				//if neibhbour makespan is shorter
				if(neighbourMakespan < initialMakespan){
					//replace initial solution with neighbouring solution
					for(int k=0; k<neighbourPart.size(); k++){
						initialPart.set(k, neighbourPart.get(k)); 
					}
					
				}else if(neighbourMakespan == initialMakespan){
					//calculate earliest agv release
					int initialEarliest = calcEarliestAgvRelease(initialPart);
					int neighbourEarliest = calcEarliestAgvRelease(neighbourPart); 
					
					// if neighbour earliest release is shorter
					if(neighbourEarliest < initialEarliest){
						for(int k=0; i<initialPart.size(); i++){
							initialPart.set(k,  neighbourPart.get(k)); 
						}
					}
				}
				
			}

			//update the pointer
			pt += Constants.NUM_QC; 
			
			//update the initial joblist
			for(int i=0; i<initialPart.size(); i++){
				finalJobList.add(initialPart.get(i)); 
			}
			
			//empty the arraylists
			initialPart.clear();
			neighbourPart.clear();
		}
		
		//after done, update job order list
		for(int i=0; i<finalJobList.size(); i++){
			jobOrder.add(finalJobList.get(i)); 
		}
		
		System.out.println("testing tabu search");
		

	}
	
	public int calcMakeSpan(ArrayList<Job> jList){
		int max = 0; 
		
		for(int i=0; i<jList.size(); i++){
			max = Math.max(max, jList.get(i).getTotalCost()+i); 
		}
		
		return max;
	}
	
	public int calcEarliestAgvRelease(ArrayList<Job> jList){
		int min = 100;
		
		for(int i=0; i<jList.size(); i++){
			min = Math.min(min, jList.get(i).getTotalCost()+i); 
		}
		
		return min; 
	}
	
	public void printFrames(ArrayList<Job> frame){
		for(int i=0; i<frame.size(); i++){
			System.out.print("[" + frame.get(i).getY() + ", " + frame.get(i).getX() + "] ,");//("job: " + frame.get(i).getY() + ", " + frame.get(i).getY());
		}
	}
	
	
	
	
	public void startDispatching(){
		int prevQcIndex = -1; 
		ArrayList<AtomicJob> prevJob = new ArrayList<>(); 
		//ArrayList<Integer> prevQc = new ArrayList<>(); 
		boolean emptyAgv = false; 
		
		int[] delayCounter = {2,2,2,2}; 
		
		int addDelay = 0; 
		
		boolean qcWait = true; 
		
		while(jobOrder.isEmpty() == false){
			//check if any bay is waiting
			//agvlist empty
			while(agvList.isEmpty() == true){
				emptyAgv = true; 
				//System.out.println("agv not available, waiting.....");
				addDelay = 0; 
				try {
					Thread.sleep(Constants.SLEEP);
					for(int i=0; i<4; i++){
						//update delay 
						delayCounter[i]++; 
						if(delayCounter[i]>1){
							addDelay++; 
						}
					}
					
					Constants.TOTALDELAY += addDelay++; 
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if(agvList.isEmpty()){ //second buffer, because of synchronization issues 
				continue; 
			}
			
			addDelay = 0; 
			
			//System.out.println("agvlist size before agv remove: " +agvList.size());
			
			
			Agv idleAgv = agvList.get(0); //need to change this part 
			agvList.remove(0);	//agv not idle anymore 
			
			Job j = jobOrder.get(0);
			jobOrder.remove(0); 	//remove the first job in the queue 	
			
			//update delayCounter 
			delayCounter[j.getQcIndex()] = 0; 
			for(int i=0; i<4; i++){
				if(i != j.getQcIndex()){
					delayCounter[i]++; 
				}
			}
			
			String threadName = Integer.toString(j.getY()) + Integer.toString(j.getX()); //set name 
			
			emptyAgv = false;
			
			
			if(j.getLoading() == false){ //if unloading job 
				if(prevQcIndex == j.getQcIndex() && emptyAgv == false){
					qcWait = true; 
				}
				
			}
			
			 
			
			//set previous qc index to determine whether to put the delay in front or not (for unloading) 
			prevQcIndex = j.getQcIndex(); 
			
			//System.out.println("is job created?");
			
			AtomicJob a = new AtomicJob(j, threadName, idleAgv, qcWait);
			
			
			qcWait = false; 
					
			jobNo_created++; 
			

			
			atomicJobList.add(a);


			prevJob.add(a); 
			
			a.start(); 
			
			
			
			//add the delay for all other qcs 
			for(int i=0; i<4; i++){
				if(delayCounter[i] > 1){
					addDelay++;  
				}
			}
			Constants.TOTALDELAY += addDelay; 
			
			
			
			try {
				Thread.sleep(Constants.SLEEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	public void startDispatchingNearest(){    //this i will deal with it later =================================================
		//no need to put the jobs in 1 queue. just need 1 
		int prevQcIndex = -1; 
		ArrayList<AtomicJob> prevJob = new ArrayList<>(); 
		boolean emptyAgv = false; 
		
		int[] delayCounter = {2,2,2,2}; 
		
		int addDelay = 0; 
		
		boolean qcWait = true; 
		
		while(jobOrder.isEmpty() == false){
			
			while(agvList.isEmpty() == true){
				emptyAgv = true; 
				System.out.println("agv not available, waiting.....");
				addDelay = 0; 
				try {
					Thread.sleep(Constants.SLEEP);
					for(int i=0; i<4; i++){
						//update delay 
						delayCounter[i]++; 
						if(delayCounter[i]>1){
							addDelay++; 
						}
					}
					
					Constants.TOTALDELAY += addDelay++; 
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if(agvList.isEmpty()){ //second buffer, because of synchronization issues 
				continue; 
			}
			
			addDelay = 0; 
			
			Agv idleAgv = agvList.get(0); //need to change this part 
			agvList.remove(0);	//agv not idle anymore 

			
			Job j = jobOrder.get(0);
			jobOrder.remove(0); 	//remove the first job in the queue 	
			
			//update delayCounter 
			delayCounter[j.getQcIndex()] = 0; 
			for(int i=0; i<4; i++){
				if(i != j.getQcIndex()){
					delayCounter[i]++; 
				}
			}
			
			String threadName = Integer.toString(j.getY()) + Integer.toString(j.getX()); //set name 
			
			emptyAgv = false;

			
			if(j.getLoading() == false){ //if unloading job 
				if(prevQcIndex == j.getQcIndex() && emptyAgv == false){
					qcWait = true; 
				}
				
			}

			//set previous qc index to determine whether to put the delay in front or not (for unloading) 
			prevQcIndex = j.getQcIndex(); 
			
			AtomicJob a = new AtomicJob(j, threadName, idleAgv, qcWait);
			
			qcWait = false; 

			
			jobNo_created++; 
			
			//System.out.println("number of jobs created: " + jobNo_created);
			
			atomicJobList.add(a);

			prevJob.add(a); 
			
			a.start(); 

			//add the delay for all other qcs 
			for(int i=0; i<4; i++){
				if(delayCounter[i] > 1){
					addDelay++;  
				}
			}
			Constants.TOTALDELAY += addDelay; 

			try {
				Thread.sleep(Constants.SLEEP);
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
		
	
	class ReentrantLock{
		private AtomicJob aj; 
		//private boolean completion; 
		
		public void ReentrantLock(AtomicJob aj, boolean complete){
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
		
		public void unloadAssign(AtomicJob aj){
			//System.out.println("inside unloadAssign!");
			
			this.aj = aj; 
			System.out.println("job is waiting for agv: " + aj.getJob().getY() + ", " + aj.getJob().getX() + " " + aj.getJob().getAgvWait()); 
			if(aj.getJob().getAgvWait()){
				System.out.println("job waiting for agv true: " + aj.getJob().getY() + ", " + aj.getJob().getX());
				aj.agvWaitEnded(); 
			}
			
			aj.unloadNotWait(); 
			/*
			Job j = aj.getJob(); 
			j.setIsWaiting(false);
			//aj.getJob().setAssigned();
			
			//remove first item (doing this in the lock is safer) 
			System.out.println("removing the first job......: " + j.getY() + ", " + j.getX());
			unloadWait.get(j.getQcIndex()).remove(0);
			*/
		}
		
		public void unloadAgvAssign(AtomicJob aj){
			//System.out.println("inside unload agv assign");
			this.aj = aj; 
			aj.agvWaitEnded(); 
		}
	}
	
	
	class AtomicJob implements Runnable{
		Job j; 
		private Thread t; 
		private String name; 
		private Agv agv = new Agv(000);
		private boolean qcWait;	//true- unloading not first, false- unloading first/ loading. true - need to set delay 1 unit before  
		private boolean bayWaited = false; 
		private int bayWaitedTime = 0; // total waiting time waiting for next bay 
		private boolean agvWaited = false; 
		private boolean unloadPrevWait = false; 
		
		public AtomicJob(Job j, String name, Agv agv, boolean qcWait ){	
			this.j = j; 
			this.name = name;
			this.qcWait = qcWait; 
			this.agv = agv; 
			
			j.setCreated(true);	//created set true only when job is created
			
			//System.out.println("job: " + j.getY() +", " + j.getX() + ", agv index: " + agv.getAgvNum()); 
			this.agv.setAgvWaitTime(j);
			if(agv.getAtQc()){ //wait for qc to pick up container if agv already at the same qc (no need to wait for agv) 
				qcWait = true; 
			}else{
				agvWaited = true; //need to wait for agv 
			}
		}
		
		@Override
		public void run() {	
			jobsCreated[j.getQcIndex()][j.getBayIndex()]--; 
			if(jobsCreated[j.getQcIndex()][2]<1){
				incompleteQc--; 
			}
			
			//before start to travel, need to wait for agv to reach.
			//agvWait(); 
			jobList.repaint();
			
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
		
		public void setQcWait(boolean b){
			this.qcWait = b; 
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
		
		public boolean getQcWait(){
			return qcWait; 
		}
		
		public boolean getBayWait(){
			return bayWaited; 
		}
		
		public void agvWait(){
			//this is complete, don't touch this part anymore! 
			int delay = agv.getAgvWaitTime(); 
			j.setAgvWait(true);
			
			jobList.repaint();
			while(delay >=0){
				if(delay == 1){
					prevWaitEnded[j.getQcIndex()] = Constants.TIMERS.getTotalTimerText(); 
				}
				delay--;
				try {
					Thread.sleep(Constants.SLEEP);
					Constants.TRAVELTIME++; 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//j.setAgvWait(false);
			
			
			if(j.getLoading()){
				j.setAgvWait(false);
				return; 
			}
			
			if(!j.getLoading() && !agvWait.get(j.getQcIndex()).contains(j)){
				//System.out.println("agvwaitlist added: " + j.getY() + ", " + j.getX());
				agvWait.get(j.getQcIndex()).add(j); 
				if(!unloadWait.get(j.getQcIndex()).contains(j)){
					unloadWait.get(j.getQcIndex()).add(j); 
					//System.out.println("agvwait here: " +j.getY() + ", " + j.getX());
				}
				
			}
			
			while(!agvWait.get(j.getQcIndex()).isEmpty()){	
				if(!agvWait.get(j.getQcIndex()).isEmpty() && agvWait.get(j.getQcIndex()).get(0) == j){
					ReentrantLock l = lockArr[j.getQcIndex()]; 
					synchronized(l){
						//l.unloadAgvAssign(this);
						l.unloadAssign(this);
					}	
					//prevWaitEnded[j.getQcIndex()] = 1;

					break;
				}
				
				j.setIsWaiting(true);

				try {
					Thread.sleep(Constants.SLEEP);								
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
			//prevWaitEnded[j.getQcIndex()] = 0;
			
			
		}
		
		public void travelingLoading(Agv agv){
			//this part is done. don't touch anymore
			agvWait(); 
			
			j.setAssigned();
			j.setIsWaiting(false);
			
			jobList.repaint();
			
			//here, delay for traveling. Hold the agv. 
			int c = j.getTotalCost();
			//System.out.println("sleep for: " + c + " units");
			
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
					//System.out.println("previous job unfinished. waiting.............................." + (j.getY()-1) + ", " + j.getX());
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
			
			
			waitForBay(); 
			
			int qcIndex = j.getQcIndex();
			ReentrantLock l = lockArr[qcIndex]; 
			
			//complete the job
			synchronized(l){
				l.ReentrantLock(this, true);
			}
			
			agv.setAgvLocation(j.getEndPos());
			
			agvList.add(agv);
			completeJobsBay[j.getQcIndex()][j.getBayIndex()]--; 
			

		}
		
		public void travelingUnloading(Agv agv){
			
			j.setAssigned();
			agvWait(); //boolean 1
			waitForBay(); 
			unloadSharedQc(); //boolean 1
			
			//prevWaitEnded[j.getQcIndex()] = 0; 
			
			j.setTravelling(true);
			jobList.repaint();
			
			//here, delay for traveling. Hold the agv. 
			int c = j.getTotalCost(); 
			//System.out.println("before travelling, sleep for: " + c + " units");
			
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

			completeJobsBay[j.getQcIndex()][j.getBayIndex()]--; 
			
			agv.setAgvLocation(j.getEndPos());
			
			agvList.add(agv);

			/*
			System.out.println("agv added back..........................................." );
			System.out.println("new agv list size: " + agvList.size());
			*/
			
			jobNo+= 1;
		}
		
		
		public void waitForBay(){
			//this, need to change!! 
			int qcIndex = j.getQcIndex();
			int bayIndex = j.getBayIndex(); 
			
			if(bayIndex > 0){
				if(j.getLoading() == false){ //this is for unloading 
					if(completeJobsBay[qcIndex][bayIndex-1] >= 0){
						j.setAgvWait(false);	//this doesn't work well now......
						bayWaited = true; 	//do i still need this since I now have the shared waiting list? 	(i'll think about it) 
						unloadWait.get(qcIndex).add(j); 
						//System.out.println("------------ bayWait added: "+j.getQcIndex() +" , new length: " + bayWait.get(j.getQcIndex()).size());
					}else{
						if(!unloadWait.get(j.getQcIndex()).isEmpty() && !j.getBayWaited()){
							if(!unloadWait.get(j.getQcIndex()).isEmpty() && unloadWait.get(j.getQcIndex()).get(unloadWait.get(j.getQcIndex()).size()-1).getBayWaited()){
								j.setIsWaiting(true);
								/*
								System.out.println("Bay wait items still exist in unloadwait, job: " + j.getY() + ", " + j.getX()+ 
										" added to unloadwait");
								*/
								if(!unloadWait.get(j.getQcIndex()).contains(j)){
									unloadWait.get(j.getQcIndex()).add(j);
									//System.out.println("job: " + j.getY() +", " + j.getX() + " really added to unloadwait");
								}
								jobList.repaint();
							}
						}
					}
				}
				while(completeJobsBay[qcIndex][bayIndex-1] >= 0){
					if(completeJobsBay[qcIndex][bayIndex-1] < 1){
						completeJobsBay[qcIndex][bayIndex-1]--;
					}
					
					//wait
					j.setIsWaiting(true);
					j.setBayWaited(true);
					bayWaitedTime++;
					jobList.repaint();
					try {
						Constants.TOTALDELAY++;
						Thread.sleep(Constants.SLEEP);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				/*
				if(j.getLoading() == false){
					if(unloadWait.get(qcIndex).size()>0){
						while(unloadWait.get(qcIndex).size()>0){	
							//this entire part needs to undergo overhaul
							if(unloadWait.get(qcIndex).size()>0 && unloadWait.get(qcIndex).get(0) == j){
								
								
								Lock l = lockArr[qcIndex]; 
								synchronized(l){
									l.unloadAssign(this);
								}
								//prevWaitEnded[j.getQcIndex()] = 1;
								
								break;
							}

							try {
								Thread.sleep(Constants.SLEEP);								
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
						}
						
					}
				}else{
					j.setIsWaiting(false);
				}*/
				if(j.getLoading()){
					j.setIsWaiting(false);
				}
			}
			jobList.repaint();

		}
		
		public void unloadSharedQc(){// so this is the sharing function//// 

			int qcIndex = j.getQcIndex(); 	
			
			//1. check if job is consecutive! 
			
			//System.out.println("see if consecutive: " + prevWaitEnded[j.getQcIndex()]);
			System.out.println("job: " + j.getY() + ", " + j.getX() + " current time : " + Constants.TIMERS.getTotalTimerText());
			//if(prevWaitEnded[j.getQcIndex()] == 1){ // delay
			if(prevWaitEnded[j.getQcIndex()] == (Constants.TIMERS.getTotalTimerText()-1)){
				if(!unloadWait.get(j.getQcIndex()).contains(j)){
					//qcWait = true; 
					//System.out.println("here 1 added, job: " + j.getY() + ", " + j.getX());
					j.setIsWaiting(true);
					unloadWait.get(j.getQcIndex()).add(j);
					//printArrayList(unloadWait.get(qcIndex)); 
					
					try {
						Thread.sleep(Constants.SLEEP);	
						Constants.TOTALDELAY++; 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					jobList.repaint();
				}
			}
			
			//2. check if there is previous job. 
			int prevY = j.getY()-1; 
			

			int minY, maxY; 
			if(j.getQcIndex()<1){
				minY = 0;
				maxY = Constants.MAX_Y-1; 
			}else{
				minY = Constants.MAX_Y; 
				maxY = Constants.TOTAL_Y-1; 
			}
			
			
			//update this part. This needs changin 
			if(prevY >= minY){
				//System.out.println("here1 job: " + j.getY() +", " + j.getX());
				Job prevJob = jobList.getJob(prevY, j.getX());
				
				System.out.println( "prevjob index: " +prevJob.getY() + ", " + prevJob.getX() +" is prev job waiting for any: " 
						+ prevJob.getIsWaiting() + " , " + prevJob.getAgvWait());
				if(!prevJob.getLoading() && (prevJob.getIsWaiting() || prevJob.getAgvWait() || !prevJob.getAssigned())){
					System.out.println("here, prev job waiting: " + prevJob.getY() + ", " + prevJob.getX() 
					+ " is job already in unloadwait: " + j.getY()+", " + j.getX() + " " + unloadWait.get(qcIndex).contains(j));
					if(!j.getPrevWaiting()){
						System.out.println("set prev waiting boolean true: " + j.getY() + ", " + j.getX());
						j.setIsWaiting(true);
						j.setPrevWaiting(true);
						if(!unloadWait.get(qcIndex).contains(j)){
							unloadWait.get(qcIndex).add(j);
						}
						
					}
					
					//printArrayList(unloadWait.get(qcIndex)); 
					System.out.println("waiting for previous job: " + j.getY() + ", " + j.getX() + " " +
					j.getPrevWaiting());

					jobList.repaint();
				}
			}
			
			//2. check if waiting for any job 
			if(!unloadWait.get(j.getQcIndex()).isEmpty()){
				if(!unloadWait.get(j.getQcIndex()).contains(j) /*&& !j.getBayWaited()*/){
					//System.out.println("qcWait true job : " + j.getY() + ", " + j.getX());
					unloadWait.get(j.getQcIndex()).add(j); 
					j.setIsWaiting(true);
					//qcWait = true; 
					//printArrayList(unloadWait.get(qcIndex)); 
				}
			}
				
			
			
			//4. job waiting, but not at the front row 
			if(j.getIsWaiting()){
				if(j.getPrevWaiting() && !bayWaited){
					//if the prev job ends waiting, then force assign not wait. 
					Job prevJob = jobList.getJob(j.getY()-1, j.getX()); 
					while(prevJob.getAgvWait() || prevJob.getIsWaiting()){
						//wait. 
						try {
							Thread.sleep(Constants.SLEEP);								
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					//after waiting is done, force remove 
					ReentrantLock l = lockArr[qcIndex]; 
					synchronized(l){
						l.unloadAssign(this);
						try {
							Thread.sleep(Constants.SLEEP);								
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					
					
				}else{
					while(unloadWait.get(qcIndex).size() > 0){
						if(unloadWait.get(qcIndex).get(0) == j){
								break;	
						}else if(j.getY()-1 >= 0 && !jobList.getJob(j.getY()-1, j.getX()).getIsWaiting() && 
								(unloadWait.get(qcIndex).get(0).getPrevWaiting() && unloadWait.get(qcIndex).get(0).getY() > j.getY())){
							break; 
						}
				
						try {
							Thread.sleep(Constants.SLEEP);								
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
						
					ReentrantLock l = lockArr[qcIndex]; 
					
					synchronized(l){
						l.unloadAssign(this);
						try {
							Thread.sleep(Constants.SLEEP);								
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}	
					}
				}
				
				
				System.out.println("job: " +j.getY() + ", " + j.getX() + " not waiting anymore!");
				prevWaitEnded[j.getQcIndex()] = Constants.TIMERS.getTotalTimerText()-1; 
		}
			 
		
		
		
		public void notWaiting(){
			//changed here!! to solve the wait-don't wait- wait blinking problem 
			j.setIsWaiting(false);
			j.setAssigned();
			jobList.repaint();
		
			if(bayWaited){
				unloadWait.get(j.getQcIndex()).remove(0);
			}
			
			if(agvWaited){
				agvWaitJobs.get(j.getQcIndex()).remove(0); 
			}
			
			try {
				Thread.sleep(Constants.SLEEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		public void unloadNotWait(){
			
			/*
			if(j.getAgvWait() ){
				j.setAgvWait(false);
				agvWait.get(j.getQcIndex()).remove(0); 
			}*/
			

			j.setIsWaiting(false);
			j.setAssigned();
			jobList.repaint();
			
			//System.out.println("removing the first job......: " + j.getY() + ", " + j.getX());
			unloadWait.get(j.getQcIndex()).remove(j); 
			//prevWaitEnded[j.getQcIndex()] = 1;
			
			//prevWaitEnded[j.getQcIndex()] = Constants.TOTALTIME;
			
			
			//prevWaitEnded[j.getQcIndex()] = Constants.TIMERS.getTotalTimerText()-1; 
			System.out.println( "job: " +j.getY() + ", " + j.getX()+ " after wait finish: " + prevWaitEnded[j.getQcIndex()]);
			
			
		}
		
		
		public void completeTask(){ //this one is for unloading 				
			
			j.setComplete();
			jobList.repaint();
			
			//int y = splitJobList.getSplitListY();
			int y = j.getSplitY();
			
			int nexty = j.getY()+1;
			
			///problem is here. the index stops at nexty index out of range
			if(j.getLoading()){
				if(nexty < (y+1)*Constants.MAX_Y){
					//System.out.println("----------------next one waiting: "+ nexty + ", " + j.getX() + ", " + jobList.getJob(nexty, j.getX()).getIsWaiting());
					if(jobList.getJob(nexty, j.getX()).getIsWaiting() == true){
						//System.out.println("job y: " + nexty + ", x: " + j.getX() + ", no longer waiting........");
						jobList.getJob(nexty, j.getX()).setIsWaiting(false);	//next job no longer waiting
						jobList.repaint();
					}
				}
			}
			
			
			jobList.repaint();
			//System.out.println("job " + j.getY() + ", " + j.getX()+ " completed");
			Constants.jobsCompleted++; 
			
		}
		
		public void waitSetFalseLoading(){
			//set waiting false in order
			int qcIndex = j.getQcIndex(); 
			Job job; 
			for(int i=0; i<bayWait.get(qcIndex).size(); i++){
				job = bayWait.get(qcIndex).get(0);
				//job.
				
			}
		}
		
		public void printArrayList(ArrayList<Job> arr){
			for(int i=0; i<arr.size(); i++){
				System.out.println("job " + i + ": " + arr.get(i).getY() + ", " + arr.get(i).getX());
			}
			
		}
		
		public void agvWaitEnded(){
			j.setAgvWait(false);
			//j.setIsWaiting(false);
			jobList.repaint();
			
			//System.out.println("agv wait removing the first job......: " + j.getY() + ", " + j.getX());
			agvWait.get(j.getQcIndex()).remove(0); 
			System.out.println("new agvlist size: " + agvWait.get(j.getQcIndex()).size());
		}

		
		@SuppressWarnings("deprecation")
		public void paused(){
			t.suspend();
		}
	}
}