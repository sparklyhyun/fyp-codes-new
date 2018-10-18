package fyp_codes;

import java.util.*;

public class Greedy {
	public static JobList jobList;
	public static ArrayList<Agv> agvList;	//kind of idle list. 
	private static ArrayList<Job> q_jobs = new ArrayList<>(); 
	
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
		long endtime = System.currentTimeMillis()-startTime;
		System.out.println("time taken for scheduling: " + endtime);
		
		showJobSeq(); 
		showExecution();		
		
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
			
		for(int k=0; k<q_jobs.size(); k++){
			jobList.repaint();
			q_jobs.get(k).setComplete();
				
			//wait for 1 second 
			try {
				Thread.sleep(Constants.SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void showExecution(){
		//wait if agv list is empty
				while(q_jobs.isEmpty()==false){
					//wait until there is idle agv
					while(true){
						if(agvList.isEmpty()==false){
							try {
								Thread.sleep(Constants.SLEEP);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							System.out.println("agv empty: " + agvList.isEmpty()+ ", "+ "agvList size: " + agvList.size());
							Agv idleAgv = agvList.get(0);
							//System.out.println("agv removed from the waiting queue: " + idleAgv.getAgvNum());
							agvList.remove(0);	//agv not idle anymore 
							
							
							Job j = q_jobs.get(0);
							q_jobs.remove(0); 	//remove the first job in the queue 
							String threadName = Integer.toString(j.getY()) + Integer.toString(j.getX()); //set name 
							System.out.println("job i,j: " + threadName);
							
							AtomicJob a = new AtomicJob(j, threadName, idleAgv);
							a.start();
							
							//agv should be added the queue when the job completes! 
							//agvList.add(idleAgv);
							//System.out.println("agv added to the queue, new queue length" + agvList.size());
							
							break;
						}
						System.out.println("agv not available, waiting.....");
						try {
							Thread.sleep(Constants.SLEEP);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
	
	public int idealStart(Job prev, Job curr){	//start from index 2
		int newIdealStart = prev.getTotalCost(); //ideal start time is when prev just finishes drop off 
		return 0; 
	}
	
	//create thread class 
	class AtomicJob implements Runnable{
		Job j; 
		private Thread t; 
		private String name; 
		private Agv agv;
		
		private Lock lock = new Lock(); 
		
		public AtomicJob(Job j, String name, Agv agv){	//update this, add shared resource 
			this.j = j; 
			this.name = name;
			System.out.println("thread name: " + this.name);
			//t.start();
		}
		
		@Override
		public void run() {	
			System.out.println("run start");
			
			traveling(agv);
			t.interrupt();	//end of the thread
			// TODO Auto-generated method stub
		}
		
		public void start(){
			System.out.println("starting thread " + name);
			if(t==null){
				t = new Thread(this, name);
				t.start();
			}
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
				System.out.println("sleep for: " + c + "units");
				Thread.sleep(Constants.SLEEP * c);
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
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			//then assign complete. introduce lock here to lock the qc? <- still doesnt work lol 
			lock.lock();
			j.setComplete();
			jobList.getJob(j.getY(), j.getX()).setComplete();
			lock.unlock();
			
			agvList.add(agv);
			System.out.println("agv added to the queue, new queue length" + agvList.size());
			
			jobList.repaint();
			System.out.println("job " + j.getY() + ", " + j.getX()+ " completed");			
		}
		
	}
	
	class Lock{
		private boolean isLocked = false;
		
		public synchronized void lock(){
			while(isLocked){
				try {
					wait(); 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			isLocked = true; 
		}
		
		public synchronized void unlock(){
			isLocked = false; 
			notify(); 
		}
		
	}
	
	
}
