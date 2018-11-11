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
		
		//long startTime2 = System.currentTimeMillis();
		showExecution();		
		
		
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
		
		//start = l*max_X
		//end = (l+1)*max_x
		
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
		}
		
		/*
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
		}*/
		
		//long endtime = System.nanoTime()-startTime;
		//System.out.println("time taken for scheduling: " + endtime);
		
		showJobSeq(); 
		
		//long startTime2 = System.currentTimeMillis();
		showExecution();
		
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
			//wait until there is idle agv
			
			//if jobno created = 40, wait until jobno completed = 40 or 80 or 120 (basically multiply by bayno
			
			if((jobNo_created == 40) || (jobNo_created == 80)){
				if((jobNo == 40) || (jobNo == 80)){
					System.out.println("move onto the next bay......................................");
				}else{
					if(jobNo_created == 40){
						while(jobNo < 40){
							try {
								System.out.println("wait until jobs completed...............................");
								Thread.sleep(Constants.SLEEP);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					
					if(jobNo_created == 80){
						while(jobNo < 80){
							try {
								System.out.println("wait until jobs completed...............................");
								Thread.sleep(Constants.SLEEP);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					
				}
			}
			
			
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
							
					AtomicJob a = new AtomicJob(j, threadName, idleAgv);
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

		
		/*
		for(int i=0; i<mulBays; i++){
			//number of jobs per bay
			int bayJobNo = Constants.MAX_X * Constants.MAX_Y;
			for(int k=0; k<bayJobNo ; k++){
				//wait until baycomplete = true; 
				while(true){
					if(jobNo == i*bayJobNo){
						bayComplete = true; 
					}
					if(bayComplete == true){
						System.out.println("starting bay number " + i + " ===========================================");
						bayComplete = false; 
						break;
					}
					try {
						Thread.sleep(Constants.SLEEP);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				//wait until there is idle agv
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
								
						AtomicJob a = new AtomicJob(j, threadName, idleAgv);
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
		*/
		
		
		
		

		
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
	/*
	
	public int idealStart(Job prev, Job curr){	//start from index 2
		int newIdealStart = prev.getTotalCost(); //ideal start time is when prev just finishes drop off 
		return 0; 
	}*/
	
	
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
		
	}
	
	
	class AtomicJob implements Runnable{
		Job j; 
		private Thread t; 
		private String name; 
		private Agv agv;
		
		public AtomicJob(Job j, String name, Agv agv){	
			this.j = j; 
			this.name = name;
		}
		
		@Override
		public void run() {	

			traveling(agv);
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		
		public void traveling(Agv agv){
			j.setAssigned();
		
			System.out.println("job " + j.getY() + ", " + j.getX()+ " on agv");
			
			jobList.repaint();
			
			//here, delay for traveling. Hold the agv. 
			int c = j.getTotalCost();
			System.out.println("sleep for: " + c + " units");
			
			/*
			try {
				Thread.sleep(Constants.SLEEP * c);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			
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
						Thread.sleep(Constants.SLEEP);
						
						updateDelayTimer(); 
						
						System.out.println("\t\t\t\t\t\t\t\t\t\t total delay: " + Constants.TOTALDELAY);
						//Constants.TOTALTIME++; 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Constants.TOTALDELAY++;	//For now, adds one more at the end. 
					
				}

				
			}
			synchronized(l){
				agvList.add(agv);
				l.lock(this, true);
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
		
	}
	
	
	
	
}
