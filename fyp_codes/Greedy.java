package fyp_codes;

import java.util.*;

import javax.swing.*;


public class Greedy {
	public static JobList jobList;
	public static ArrayList<Agv> agvList;	//kind of idle list. 
	private static ArrayList<Job> q_jobs = new ArrayList<>(); 
	Lock l = new Lock(); 
	private static boolean greedyComplete = false; 
	private static int jobNo = 0; //for completed jobs 
	private static int jobNo_created = 0; 
	
	//wait until bay is complete. then move on to the next bay.
	private static boolean bayComplete = false; 
	
	//to store the job that is not waiting anymore...
	private static ArrayList<Job> waitingJob = new ArrayList<>();
	
	private static ArrayList<Job> q_waitingAgv = new ArrayList<>(); 
	
	//how many were waiting and not waiting anymore
	private static int finishWaiting = 0; 
	
	private static Agv waitingAgv = new Agv(1000); //agv to replace null value 

	private static boolean qcWaiting = false; // false - no need to wait for qc 
	
	public Greedy(JobList j, ArrayList<Agv> agvL){
		this.jobList = j; 
		this.agvList = agvL; 
	}
	
	public void startGreedy1(){	//generic greedy algorithm, start from first row, then move onto the next row
		
		Job[] sortArray = new Job[Constants.MAX_X];	//for sorting purpose 
		int mulBays = Constants.TOTAL_X / Constants.MAX_X; 
		
		/*
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				sortArray[j] = jobList.getJob(i, j);
				Constants.TOTALTIME += jobList.getJob(i, j).getTotalCost();
			}
			sortArray = sortDecending(sortArray);
			for(int k=0; k<Constants.MAX_X; k++){
				q_jobs.add(sortArray[k]);
			}
		}*/
		
		for(int l=0; l<mulBays; l++){
			for(int i=0; i<Constants.MAX_Y; i++){
				int arr = 0;
				for(int j=l*Constants.MAX_X; j<(l+1)*Constants.MAX_X; j++){
					sortArray[arr] = jobList.getJob(i, j);
					arr++;
				}
				sortArray = sortDecending(sortArray);
				
				for(int k=0; k<Constants.MAX_X; k++){
					q_jobs.add(sortArray[k]);
					
					
				}
			}
		}
		
		
		showJobSeq(); 

		//showExecution();				
		
		showExecutionUnloading();
		
		while(true){
			try {
				Thread.sleep(Constants.SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(jobNo >= (Constants.MAX_Y * Constants.TOTAL_X)){
				Constants.allComplete = true; 
				System.out.println("-------------------all jobs complete---------------");
				break;
			}
			
		}
		
		
	}
	
	
	public void startGreedy2(){	//1 item lookahead
		//long startTime = System.nanoTime();  //to see the performance
		
		Job[] sortArray = new Job[Constants.MAX_X];	//for sorting purpose 
		int mulBays = Constants.TOTAL_X / Constants.MAX_X; 
		
		for(int l=0; l<mulBays; l++){
			for(int i=0; i<Constants.MAX_Y; i++){
				int arr = 0;
				for(int j=l*Constants.MAX_X; j<(l+1)*Constants.MAX_X; j++){
					sortArray[arr] = jobList.getJob(i, j);
					arr++;
				}
				sortArray = sortDecending(sortArray);
				
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
		//showExecutionUnloading();
		
		while(true){
			try {
				Thread.sleep(Constants.SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(jobNo >= (Constants.MAX_Y * Constants.TOTAL_X)){
				Constants.allComplete = true; 
				System.out.println("-------------------all jobs complete---------------");
				break;
			}
			
		}
		
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
	
	public startGreedyUnloading2(){
		
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
	
	public void showExecution(){
		ArrayList<AtomicJob> atomicJobList = new ArrayList<>(); 
		
		//for multiple bays
		int mulBays = Constants.TOTAL_X / Constants.MAX_X;
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
					
					a.start(); 
							
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
	
	public void showExecutionUnloading(){
		ArrayList<AtomicJob> atomicJobList = new ArrayList<>(); 
		
		try {
			Thread.sleep(Constants.SLEEP);
			//Constants.TOTALTIME++; 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//for multiple bays
		int mulBays = Constants.TOTAL_X / Constants.MAX_X;
		bayComplete = true; //start with true
		
		while(q_jobs.isEmpty()==false){
			
			while(true){
				if(agvList.isEmpty()==false){
					/*
					try {
						Thread.sleep(Constants.SLEEP);
						//Constants.TOTALTIME++; 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/
							
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
		
		/*
		//first item, no need waiting time 
		try {
			Thread.sleep(Constants.SLEEP);
			//Constants.TOTALTIME++; 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Job j = q_jobs.get(0);
		q_jobs.remove(0); 	//remove the first job in the queue 
		
		System.out.println("agvList size: " + agvList.size());
		Agv idleAgv = agvList.get(0);
		agvList.remove(0);	//agv not idle anymore 
		
		String threadName = Integer.toString(j.getY()) + Integer.toString(j.getX()); //set name 
		AtomicJob a = new AtomicJob(j, threadName, idleAgv, qcWaiting);	//if waitingAGv wait for agv  
		jobNo_created++; 
		
		System.out.println("first task, no need to wait for qc");
		
		atomicJobList.add(a);
		
		a.start();
		
		qcWaiting = true; 
		
		//wait if agv list is empty
		while(q_jobs.isEmpty()==false){
			try {
				Thread.sleep(Constants.SLEEP);
				//Constants.TOTALTIME++; 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("here.....................................................");
			
			//if agv empty, wait 
			if(agvList.isEmpty() == true){	
				System.out.println("here.....................................................2");
				qcWaiting = true;
				//1st one is on the qc, waiting for the agv 
				j = q_jobs.get(0);
				q_jobs.remove(0); 	//remove the first job in the queue 					
				threadName = Integer.toString(j.getY()) + Integer.toString(j.getX()); //set name 
				a = new AtomicJob(j, threadName, null, qcWaiting);	//if null & qcWaitng == true, wait until agv assigned   
				jobNo_created++; 
				
				System.out.println("waiting task created.............................");
				
				atomicJobList.add(a);
				
				a.start();
				
				while(agvList.isEmpty() == true){
					System.out.println("agvlist empty, waiting for agv...................................");
					try {
						Thread.sleep(Constants.SLEEP);
						//Constants.TOTALTIME++; 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				qcWaiting = false;
				
				
			}
			
			j = q_jobs.get(0);
			q_jobs.remove(0); 	//remove the first job in the queue 	
			
			System.out.println("agvList size: " + agvList.size());
			System.out.println("qc wait" + qcWaiting);
			idleAgv = agvList.get(0);
			//if(agvList.isEmpty() == false) System.out.println("agv num: " + idleAgv.getAgvNum());
			
			agvList.remove(0);	//agv not idle anymore 
			
			threadName = Integer.toString(j.getY()) + Integer.toString(j.getX()); //set name 
			a = new AtomicJob(j, threadName, idleAgv, qcWaiting);	//if null & qcWaitng == true, wait until agv assigned   
			jobNo_created++; 
			
			System.out.println("waiting task created.............................");
			
			atomicJobList.add(a);
			
			a.start();
			
			qcWaiting = true;
			
			

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
	
	class Lock{
		private AtomicJob aj; 
		//private boolean completion; 
		
		public void lock(AtomicJob aj, boolean complete){
			this.aj = aj;
			if(complete){
				System.out.println("qc locked, completion on the way");
				aj.completeTask();
				
				try {
					Thread.sleep(Constants.SLEEP);
					
					//try out
					//jobList.getJob(aj.getJob().getY(), aj.getJob().getX()).setIsWaiting(true);
					//jobList.repaint();
					
					//Constants.TOTALTIME++; 
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
			
			/*
			i=0; 
			while(true){
				if(waitingJob.isEmpty() == true || i>=waitingJob.size()){
					break;
				}
				
				if(waitingJob.get(i).getIsWaiting() == false){
					waitingJob.remove(i); 
				}
				
				i++;
			}
			*/
			
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
		
			System.out.println("job " + j.getY() + ", " + j.getX()+ " on agv");
			
			jobList.repaint();
			
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
				
				if(prev.getComplete() == false){
					System.out.println("previous job unfinished. waiting..............................");
					jobList.getJob(j.getY(), j.getX()).setIsWaiting(true);
					jobList.repaint();
				}
				
				while(prev.getComplete() == false){
					try {
						
						updateDelayTimer();
						Thread.sleep(Constants.SLEEP);
						
						System.out.println("\t\t\t\t\t\t\t\t\t\t total delay: " + Constants.TOTALDELAY);
						//Constants.TOTALTIME++; 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Constants.TOTALDELAY++;	//For now, adds one more at the end. 
					
				}

				
			}

			waitForBay(); 
			
			//complete the job
			synchronized(l){
				agvList.add(agv);
				l.lock(this, true);
			}

		}
		
		public void travelingUnloading(Agv agv){

			j.setAssigned();
			
			//System.out.println("agv: " + this.agv.getAgvNum() + "qcWait: " + qcWait);
			
			//empty agv list, need to wait for agv
			if(qcWait == true){//waiting for the agv
				jobList.getJob(j.getY(), j.getX()).setIsWaiting(true);
				System.out.println("job " + j.getY() + ", " + j.getX()+ " null agv, waiting for agv");
				jobList.repaint();
				try {
					Constants.TOTALDELAY++;
					//updateDelayTimer();
					Thread.sleep(Constants.SLEEP);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			jobList.getJob(j.getY(), j.getX()).setIsWaiting(false);
			jobList.repaint();
			j.setTravelling(true);

			
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
			
			waitForBay(); 
			
			//job completion can happen at the same time
			completeTask();
			agvList.add(agv);
			System.out.println("agv added back..........................................." );
			System.out.println("new agv list size: " + agvList.size());
			jobNo+= 1;
		}
		
		public void waitForBay(){
			int mulBays = Constants.TOTAL_X / Constants.MAX_X;
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
			
			if(j.getY()+1 < Constants.MAX_Y){
				int nexty = j.getY()+1;
				if(jobList.getJob(nexty, j.getX()).getIsWaiting() == true){
					jobList.getJob(nexty, j.getX()).setIsWaiting(false);	//next job no longer waiting 
				}
			}
			
			System.out.println("agv added to the queue, new queue length: " + agvList.size());
			jobList.getJob(j.getY(), j.getX()).setComplete();
			jobList.repaint();
			System.out.println("job " + j.getY() + ", " + j.getX()+ " completed");
			

		}
		
		public void unloadComplete(){
			j.setTravelling(true);
			
			if(j.getY()+1 < Constants.MAX_Y){
				int nexty = j.getY()+1;
				if(jobList.getJob(nexty, j.getX()).getIsWaiting() == true){
					jobList.getJob(nexty, j.getX()).setIsWaiting(false);	//next job no longer waiting 
				}
			}
			System.out.println("job " + j.getY() + ", " + j.getX()+ " just unloaded");
			jobList.repaint();
		}
		
	}
	
	
	
	
}
