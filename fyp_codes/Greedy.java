package fyp_codes;

import java.awt.List;
import java.util.*;

import javax.swing.*;


public class Greedy implements Runnable{
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
	
	//to store half half 
	//private static ArrayList<Job> q_unloading = new ArrayList<>(); 
	//private static ArrayList<Job> q_loading = new ArrayList<>(); 
	
	//making this runnable 
	private Thread t; 
	private String name; 
	
	@Override
	//make it a runnable 
	public void run() {
		// TODO Auto-generated method stub
		
		startMergedGreedy(); 
	}	
	
	public void start(){
		if(t==null){
			t = new Thread(this, name);
			t.start();
			
		}
	}
	
	public Greedy(JobList j, SplitJobList sj , /*ArrayList<Agv> agvL,*/ String name){
		this.jobList = j; 
		this.splitJobList = sj; 

		for(int k=0; k<Constants.AGV; k++){
			Agv agv = new Agv(k); 
			agvList.add(agv); 
		}
		
		this.name = name; 
		
		//show agvList (works fine)
		/*
		for(int i=0; i<agvList.size(); i++){
			System.out.println("agvList test: " + agvList.get(i).getAgvNum());
		}
		*/ 
		
		//System.out.println("greedy name: " + name);
		//System.out.println("see joblist ");
		//seeSplitJobList(jobList); //done fixed
		
	}
	
	public void startMergedGreedy(){
		
		Job[] sortArray = new Job[Constants.MAX_X];	//for sorting purpose 
		int numBays = Constants.QC_X / Constants.MAX_X; 
		int numHalf = Constants.MAX_Y / 2; //top 5 unloading, bottom 5 loading 

		for(int l=0; l<numBays; l++){
			//sort 1 bay at a time. update q_jobs accordingly 
			
			//sort unloading first
			sortUnloading(l, sortArray);
			
			//sort loading
			sortLoading(l, sortArray); 
			while(q_jobs.isEmpty() == false){
				if(q_jobs.size()>20){
					showExecutionUnloadingMerged();	//need to edit wait for bays part 
				}else{
					showExecutionLoadingMerged(); 
				}
			}
			
			//wait for bay 
			//unloading can only start when loading is finished. 
			waitingBay();
			jobNo = 0; 
			
		}
		
		Constants.allComplete = true; 
		System.out.println("Thread ended, thread name: " + name + "\n");
	}
	
	public void sortUnloading(int bayNo, Job[] sortArray){
		int arr = 0;
		for(int i=0; i<Constants.HALF_Y; i++){
			for(int j=bayNo*Constants.MAX_X; j<(bayNo+1)*Constants.MAX_X; j++){
				//sortArray[arr] = jobList.getJob(i, j);
				sortArray[arr] = splitJobList.getJob(i, j); 
				arr++;
			}
			sortArray = sortDescending(sortArray);
			
			for(int k=0; k<Constants.MAX_X; k++){
				q_jobs.add(sortArray[k]);
			}
			arr = 0; 
		}
	}
	
	public void sortLoading(int bayNo, Job[] sortArray){
		int arr = 0;
		
		int splity, splitx; 
		
		for(int i=Constants.HALF_Y; i<Constants.MAX_Y; i++){
			
			for(int j=bayNo*Constants.MAX_X; j<(bayNo+1)*Constants.MAX_X; j++){
				//sortArray[arr] = jobList.getJob(i, j);
				sortArray[arr] = splitJobList.getJob(i, j); 
				arr++;
			}
			sortArray = sortDescending(sortArray);
			
			for(int k=0; k<Constants.MAX_X; k++){
				//new step, check next item and add the next item in front if it has higher cost
				//fullListX and fullListY already obtaned here 
				int y = sortArray[k].getY();
				int nexty = y+1;
				int x = sortArray[k].getX();
				int count = 0; 
				
				int fullListX, fullListY;
				
				if(nexty<Constants.MAX_Y && count < Constants.AGV-1){
					Job nextJob = jobList.getJob(nexty, x); 
					//Job nextJob = splitJobList.getJob(nexty, x); 
					if(jobList.getJob(y, x).getVisited()==false){
					//if(splitJobList.getJob(y, x).getVisited() == false){
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
					//System.out.printf("y: %d x: %d\n", y, x);
					//change y and x to split job
					
					if(jobList.getJob(y, x).getVisited() == false){
					//if(splitJobList.getJob(y, x).getVisited() == false){
						
						q_jobs.add(sortArray[k]);
					}
				}
				
			}
			arr = 0; 
		}
	
	}
	
	
	public void startGreedy1(){	//generic greedy algorithm, start from first row, then move onto the next row
		//used for unloading 
		
		Job[] sortArray = new Job[Constants.MAX_X];	//for sorting purpose 
		int mulBays = Constants.QC_X / Constants.MAX_X; 
		
		for(int l=0; l<mulBays; l++){
			for(int i=0; i<Constants.HALF_Y; i++){
				int arr = 0;
				for(int j=l*Constants.MAX_X; j<(l+1)*Constants.MAX_X; j++){
					sortArray[arr] = jobList.getJob(i, j);
					arr++;
				}
				sortArray = sortDescending(sortArray);
				
				for(int k=0; k<Constants.MAX_X; k++){
					q_jobs.add(sortArray[k]);
					
					
				}
			}
		}
		
		
		showJobSeq(); 

		//showExecution();				
		
		/*
		showExecutionUnloading();
		
		while(true){
			try {
				Thread.sleep(Constants.SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(jobNo >= (Constants.MAX_Y * Constants.QC_X)){
				Constants.allComplete = true; 
				System.out.println("-------------------all jobs complete---------------");
				break;
			}
			
		}
		*/
		
	}
	
	
	public void startGreedy2(){	//1 item lookahead
		//for loading 
		
		Job[] sortArray = new Job[Constants.MAX_X];	//for sorting purpose 
		int mulBays = Constants.QC_X / Constants.MAX_X; 
		
		for(int l=0; l<mulBays; l++){
			for(int i=0; i<Constants.MAX_Y; i++){
				int arr = 0;
				for(int j=l*Constants.MAX_X; j<(l+1)*Constants.MAX_X; j++){
					sortArray[arr] = jobList.getJob(i, j);
					arr++;
				}
				sortArray = sortDescending(sortArray);
				
				for(int k=0; k<Constants.MAX_X; k++){
					//new step, check next item and add the next item in front if it has higher cost
					int y = sortArray[k].getY();
					int nexty = y+1;
					int x = sortArray[k].getX();
					int count = 0; 
					
					if(nexty<Constants.MAX_Y && count < Constants.AGV-1){
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
		}		

		showJobSeq(); 
		
		showExecution();
		
		while(true){
			try {
				Thread.sleep(Constants.SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(jobNo >= (Constants.MAX_Y * Constants.QC_X)){
				Constants.allComplete = true; 
				System.out.println("-------------------all jobs complete---------------");
				break;
			}
			
		}
		
	}
	
	/*
	public void startGreedyUnloading2(){
		Job[] sortArray = new Job[Constants.MAX_X];	//for sorting purpose 
		int mulBays = Constants.QC_X / Constants.MAX_X; 
		
		//for sorting purpose
		Job emptyJob = new Job(100,100,true);
		emptyJob.setTotalCost(0);
		
		int arrX = 0;
		
		for(int l=0; l<mulBays; l++){
			
			//check 4 items at a time, not necessarily a row 
			for(int i=l*Constants.MAX_X; i<(l+1)*Constants.MAX_X; i++){
				
				//first, put top row into sorting array
				for(int arr=0; arr<Constants.MAX_X; arr++){
					sortArray[arr] = jobList.getJob(0, i);
					i++;
				}
				sortArray = sortDecending(sortArray);
				
				//put the first item in the job queue
				q_jobs.add(sortArray[0]);
				
				System.out.println("sorted first job....................................");
				
				
				for(int j=0; j<Constants.BAYSIZE-1; j++){
					//then, check add the job that is below that added job into the queue 
					int addedJobY = q_jobs.get(q_jobs.size()-1).getY();
					int addedJobX = q_jobs.get(q_jobs.size()-1).getX(); 
					
					System.out.println("next job y: " + (addedJobY+1) +" .....................");
					System.out.println((addedJobY+1 < Constants.MAX_Y));
					
					if(addedJobY+1 < Constants.MAX_Y){
						sortArray[0] = jobList.getJob(addedJobY+1, addedJobX); 
					}else{
						sortArray[0] = emptyJob; 
					}	
					
					if(q_jobs.get(q_jobs.size()-1).getY() ){

						sortDescending2(sortArray);
						q_jobs.add(sortArray[0]);
					}else{
						sortArray = sortDecending(sortArray);
						q_jobs.add(sortArray[0]);
					}
					 

					

					
				}
			}
			
			
			
		}
		
		showJobSeq(); 
		
		showExecutionUnloading();
		
		while(true){
			try {
				Thread.sleep(Constants.SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(jobNo >= (Constants.MAX_Y * Constants.QC_X)){
				Constants.allComplete = true; 
				System.out.println("-------------------all jobs complete---------------");
				break;
			}
			
		}
	}*/
	
	public Job[] sortDescending(Job[] arr){	//add high cost first
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
	
	public Job[] sortDescending2(Job[] arr){//for unloading
		for(int i=Constants.MAX_X-1; i>0; i--){
			for(int j=0; j<i; j++){
				if(arr[j].getY() > arr[j+1].getY()){
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
				//Constants.TOTALTIME++; 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
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
	
	public void showExecution(){
		//ArrayList<AtomicJob> atomicJobList = new ArrayList<>(); 
		
		//for multiple bays
		int mulBays = Constants.QC_X / Constants.MAX_X;
		bayComplete = true; //start with true
		
		//wait if agv list is empty
		while(q_jobs.isEmpty()==false){
			
			while(true){
				if(agvList.isEmpty()==false){
					try {
						Thread.sleep(Constants.SLEEP);
						//Constants.TOTALTIME++; 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
							
					System.out.println("agvList size: " + agvList.size());
					Agv idleAgv = agvList.get(0);
					agvList.remove(0);	//agv not idle anymore 
					
					Job j = q_jobs.get(0);
					q_jobs.remove(0); 	//remove the first job in the queue 					
					
					String threadName = Integer.toString(j.getY()) + Integer.toString(j.getX()); //set name 
							
					AtomicJob a = new AtomicJob(j, threadName, idleAgv, false);
					jobNo_created++; 
					
					atomicJobList.add(a);
					
					a.start(); //it feels like i need to re-do everything........
							
					break;
				}
				
				System.out.println("agv not available, waiting.....");
				
				try {
					Thread.sleep(Constants.SLEEP);
					//Constants.TOTALTIME++; 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
			
			//now the agv list is not working 
			System.out.println("agvList size: " + agvList.size());
			if(agvList.isEmpty()== true){
				continue; 
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
		
	
	
	public void showExecutionUnloading(){
		//ArrayList<AtomicJob> atomicJobList = new ArrayList<>(); 
		
		try {
			Thread.sleep(Constants.SLEEP);
			//Constants.TOTALTIME++; 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//for multiple bays
		int mulBays = Constants.QC_X / Constants.MAX_X;
		bayComplete = true; //start with true
		
		while(q_jobs.isEmpty()==false){
			
			while(true){
				if(agvList.isEmpty()==false){
							
					System.out.println("agvList size: " + agvList.size());
					Agv idleAgv = agvList.get(0);
					agvList.remove(0);	//agv not idle anymore 
					
					Job j = q_jobs.get(0);
					q_jobs.remove(0); 	//remove the first job in the queue 					
					
					String threadName = Integer.toString(j.getY()) + Integer.toString(j.getX()); //set name 
							
					AtomicJob a = new AtomicJob(j, threadName, idleAgv, qcWaiting);
					jobNo_created++; 
					
					atomicJobList.add(a);
					
					a.start(); 
					
					qcWaiting = true; 
					
					try {
						Thread.sleep(Constants.SLEEP);
						//Constants.TOTALTIME++; 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					break;
				}
				
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
			}

		}

		
	}

	public void waitingBay(){
		while(jobNo < 40){
			System.out.println("waiting for the bay to complete all loading jobs...............");
			try {
				Thread.sleep(Constants.SLEEP);
				//Constants.TOTALTIME++; 
				//Constants.TOTALDELAY++;
				updateTotalTimer();
			} catch (InterruptedException e) {
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
	
	@SuppressWarnings("deprecation")
	public void pauseGreedy(){
		AtomicJob j; 
		for(int i=0; i<atomicJobList.size(); i++){
			j = atomicJobList.get(i);
			j.paused();
		}
		t.suspend();
		
	}
	
	
	class Lock{
		private AtomicJob aj; 
		//private boolean completion; 
		
		public void lock(AtomicJob aj, boolean complete){
			this.aj = aj;
			if(complete){
				//System.out.println("qc locked, completion on the way");
				aj.completeTask();
				try {
					Thread.sleep(Constants.SLEEP);
					
					//try out
					//jobList.getJob(aj.getJob().getY(), aj.getJob().getX()).setIsWaiting(true);
					//jobList.repaint();
					
					//Constants.TOTALTIME++; 
					//System.out.println("qc released");
					System.out.println("completed jobs: " + jobNo);
					jobNo+= 1;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				//System.out.println("qc locked, asignment on the way");
				aj.getJob().setAssigned();
				try {
					//System.out.println("wait for qc release");
					Thread.sleep(Constants.SLEEP);
					//Constants.TOTALTIME++; 
					System.out.println("qc released");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		
		public void unloadingLock(AtomicJob aj, boolean unload){
			//put this in show execution 
			this.aj = aj; 
			
			//handle setWaiting 
			
			int i=0;
			while(i<waitingJob.size()){
				Job j = waitingJob.get(i);
				Job prev = jobList.getJob(j.getY()-1, j.getX());
				if(j.getIsWaiting() == true && prev.getTravelling() == true){
					j.setIsWaiting(false);
					j.setTravelling(true);
					//jobList.getJob(j.getY(), j.getX()).setIsWaiting(false);
					//jobList.getJob(j.getY(), j.getX()).setTravelling(true);
					jobList.repaint();
					try {
						Thread.sleep(Constants.SLEEP);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				i++;
			}
			
			//handle setassigned 
			if(unload){
				System.out.println("qc locked, completion on the way");
				aj.unloadComplete();
				
				try {
					Thread.sleep(Constants.SLEEP);
					System.out.println("qc released");
					System.out.println("unloaded job: " + jobNo);
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
					//Constants.TOTALTIME++; 
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
			//waitForBay(); 
			
			//complete the job
			synchronized(l){
				agvList.add(agv);
				l.lock(this, true);
			}

		}
		
		public void travelingUnloading(Agv agv){

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
			
			//waitForBay(); 
			
			//job completion can happen at the same time
			completeTask();
			agvList.add(agv);
			System.out.println("agv added back..........................................." );
			System.out.println("new agv list size: " + agvList.size());
			jobNo+= 1;
		}
		
		public void waitForBay(){
			int mulBays = Constants.QC_X / Constants.MAX_X;
			int baySize = Constants.BAYSIZE;
			ArrayList<Integer> jobCompletedArr = new ArrayList<>(); 
			ArrayList<Integer> maxX = new ArrayList<>(); 
			
			//calculate starting index of job per bay
			for(int i=0; i<mulBays-1; i++){
				jobCompletedArr.add(baySize + baySize*i);
				maxX.add(Constants.MAX_X + Constants.MAX_X*i);
			}
			
			for(int i=0; i<jobCompletedArr.size(); i++){
				if(jobNo_created >= jobCompletedArr.get(i) && j.getX()>maxX.get(i)-1){
					//System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
					//int nexty = j.getY()+1; //only considers next row. need to consider the next bay. job index - 0 to 39, 40 to 80 
					
					if(jobNo<=jobCompletedArr.get(i)){
						System.out.print("\t\t\t\t\t wait job x: " + j.getX());
						while(jobNo<jobCompletedArr.get(i)){
							if(j.getLoading()==false){
								break; 
							}
							System.out.println("\t\t\t\t\twait until jobs completed...............................");
							//jobList.getJob(nexty, j.getX()).setIsWaiting(true);
							jobList.getJob(j.getY(), j.getX()).setIsWaiting(true);
							jobList.repaint();
							try {
								Thread.sleep(Constants.SLEEP);
								Constants.TOTALDELAY++;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						System.out.println("\t\t\t\t\t\tmove onto the next bay......................................");
					}
					
				//jobList.getJob(nexty, j.getX()).setIsWaiting(false);
				jobList.getJob(j.getY(), j.getX()).setIsWaiting(false);
				jobList.repaint();
			}
			
		}
		}
		
		public void completeTask(){				
			
			j.setComplete();
			
			int y = splitJobList.getSplitListY();
			
			int nexty = j.getY()+1;
			
			if(nexty+1 <= (y+1)*Constants.MAX_Y){
				System.out.println("----------------next one waiting: "+ nexty + ", " + j.getX() + ", " + jobList.getJob(nexty, j.getX()).getIsWaiting());
				//not even reaching this stage somehow.....
				if(jobList.getJob(nexty, j.getX()).getIsWaiting() == true){
					System.out.println("job y: " + nexty + ", x: " + j.getX() + ", no longer waiting........");
					jobList.getJob(nexty, j.getX()).setIsWaiting(false);	//next job no longer waiting
					//jobList.repaint();
				}
			}
			
			System.out.println("agv added to the queue, new queue length: " + agvList.size());
			
			//jobList.getJob(j.getY(), j.getX()).setComplete();
			jobList.getJob(j.getSplitY(), j.getSplitX()); 
			
			jobList.repaint();
			System.out.println("job " + j.getY() + ", " + j.getX()+ " completed");
			

		}
		
		public void unloadComplete(){
			j.setTravelling(true);
			jobList.repaint();
			
			if(j.getY()+1 < Constants.MAX_Y){
				int nexty = j.getY()+1;
				if(jobList.getJob(nexty, j.getX()).getIsWaiting() == true){
					jobList.getJob(nexty, j.getX()).setIsWaiting(false);	//next job no longer waiting 
					//jobList.repaint();
				}
			}
			System.out.println("job " + j.getY() + ", " + j.getX()+ " just unloaded");
			jobList.repaint();
		}
		
		
		@SuppressWarnings("deprecation")
		public void paused(){
			t.suspend();
		}
	}


	
	
	
}
