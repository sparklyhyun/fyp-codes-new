package fyp_codes;

public class Event {
	private Job job; 
	private int time; //time of occurence of this event 
	private int eventType; // 0 - travel, 1 - release, 2 - delay, 3- baywait, 4 - prevwait?? 
	private JobList jobList; 
	
	private boolean delayed = false; 
	private boolean prevWait = false; 

	private boolean loading = false; //true - loading, false - unloading 
	
	// loading jobs
	public Event(Job a, int time, int eventType, boolean loading, JobList joblist){
		job = a; 
		this.jobList = joblist; 
		if(!job.getAgvWait()){
			job.setAgvWait(true);
		}
		
		System.out.println("job agvwait?: " + job.getY() + " , " + job.getX() + " = " + job.getAgvWait());
		
		if(a.getAgvWait()){
			this.time = time + a.getAgvWaitTime(); 
		}
		
		System.out.println("job calculated time: " + job.getY() + ", " +  job.getX() + " = " + this.time); 
		
		this.eventType = eventType;
		this.loading = loading; 

	}

	
	public int getTime(){
		return time; 
	}
	
	public int getEventType(){
		return eventType; 
	}
	
	public Job getJob(){
		return job; 
	}
	
	public void changeState(){
		//System.out.println("change state job: " + job.getY() + ", " + job.getX() + " = " + eventType);
		
			if(loading){
				System.out.println("Current job : " + job.getY()+ ", " + job.getX() + " , Current eventType : " + eventType);
				//System.out.println("Current eventType : " + eventType);
				switch(eventType){
				case 0:	//travel ended. then update time.
					//need to check for delay in front 
					System.out.println("job finished travelling: " + job.getY() + ", " + job.getX());
					job.setAgvWait(false);
					job.setAssigned();
					time = job.getTotalCost() + Constants.TOTALTIME; 
					eventType = Constants.RELEASE; 
					break; 
					
				case 1:	//job finished
					System.out.println("job: " + job.getY() + ", " + job.getX() + " time: " + time + " crane used: " + Constants.CRANEUSED[job.getQcIndex()] );
					
					// if this was delayed before
					if(delayed){
						System.out.println("job finished completion: " + job.getY() + ", " + job.getX());
						job.setIsWaiting(false);
						job.setComplete();
						job.getAgv().setIdle(true);
						job.getAgv().setAgvLocation(job.getEndPos());
						
						Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;//next free time is +2 after this  
						Constants.jobsCompleted++; 
						
						//also add for baywait
						Constants.WAITBAY[job.getQcIndex()][job.getBayIndex()]++; 
						break; 
					}
					
					
					//check baywait (after this state change, just break) 
					//check baywait (if the bay index is greater than 0
					if(job.getBayIndex() > 0){//check if previous bay waiting. 
						if(Constants.WAITBAY[job.getQcIndex()][job.getBayIndex()] < Constants.BAYSIZE){
							eventType = Constants.BAYWAIT; 
							job.setIsWaiting(true);
							time++; 
							break; 
						}
					}
					
					//check prev job 
					
					int prevY = job.getY()-1; 
					
					int minY;
					if(job.getQcIndex()<2){
						minY = 0; 
					}else{
						minY = Constants.MAX_Y; 
					}
					
					if(prevY >= minY && jobList.getJob(prevY, job.getX()).getLoading()){
						if(!jobList.getJob(prevY, job.getX()).getComplete()){
							//if prev job is not complete 
							job.setIsWaiting(true);
							eventType = Constants.PREVWAIT; 
							time++; 
							break; 
						}
					}
					
					//check consecutive 
					if(time <= Constants.CRANEUSED[job.getQcIndex()]){	//consecutive, need to change to delay 
						eventType = Constants.DELAY; 
						job.setIsWaiting(true);
						time = Math.max(time, Constants.CRANEUSED[job.getQcIndex()]+1); 
						Constants.CRANEUSED[job.getQcIndex()]+= 1; 
						System.out.println("loading consecutive job: " + job.getY() + ", " + job.getX() + " = " + time + " , event type: " + eventType);
						
					}else{ //not consecutive, thus change to complete 
						System.out.println("job finished completion: " + job.getY() + ", " + job.getX());
						job.setIsWaiting(false);
						job.setComplete();
						job.getAgv().setIdle(true);
						job.getAgv().setAgvLocation(job.getEndPos());
						
						Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;//next free time is +2 after this  
						Constants.jobsCompleted++; 
						
						//also add for baywait
						Constants.WAITBAY[job.getQcIndex()][job.getBayIndex()]++; 
					}
					break; 
				case 2: //delay <- not going into delay. why??? 
					
					System.out.println("delay job: " + job.getY() + ", " + job.getX());
					job.setIsWaiting(false);
					eventType = Constants.RELEASE; 
					delayed = true; 
					//Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;
					//no need time, as change state will change to finish job 

					break;
				case 3: //baywait 
					//if baywait ends, then go to release. then reset 
					if(Constants.WAITBAY[job.getQcIndex()][job.getBayIndex()] >= Constants.BAYSIZE){
						//check if delay is needed here too. 
						if(time <= Constants.CRANEUSED[job.getQcIndex()]){	//consecutive, need to change to delay 
							eventType = Constants.DELAY; 
							job.setIsWaiting(true);
							time = Math.max(time, Constants.CRANEUSED[job.getQcIndex()]+1); 
							Constants.CRANEUSED[job.getQcIndex()]+= 1; 
							System.out.println("loading consecutive job: " + job.getY() + ", " + job.getX() + " = " + time + " , event type: " + eventType);
							
						}else{ //not consecutive, thus change to complete 
							//this is else condition for the delay 
							job.setIsWaiting(false);
							time = job.getTotalCost() + Constants.TOTALTIME; 
							eventType = Constants.RELEASE; 
							Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;
						}
					}else{
						time = time++; 
					}
					break; 
					
				case 4: //prevwait 
					int prevY2 = job.getY()-1; 
					
					if(jobList.getJob(prevY2, job.getX()).getAgvWait() || !jobList.getJob(prevY2, job.getX()).getAssigned() || jobList.getJob(prevY2, job.getX()).getIsWaiting()){
						//if prev job waiting for agv, or is waiting for qc, or is not assigned at all
						eventType = Constants.PREVWAIT; 
						time++; 
					}else{
						job.setIsWaiting(false);
						
						//check consecutive 
						if(time <= Constants.CRANEUSED[job.getQcIndex()]){	//consecutive, need to change to delay 
							eventType = Constants.DELAY; 
							job.setIsWaiting(true);
							time = Math.max(time, Constants.CRANEUSED[job.getQcIndex()]+1); 
							Constants.CRANEUSED[job.getQcIndex()]+= 1; 
							System.out.println("loading consecutive job: " + job.getY() + ", " + job.getX() + " = " + time + " , event type: " + eventType);
							
						}else{ //not consecutive, thus change to complete 
							System.out.println("job finished completion: " + job.getY() + ", " + job.getX());
							job.setIsWaiting(false);
							eventType = Constants.RELEASE; 
							Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;//next free time is +2 after this  
						}
					}
					
					break; 
				default: break; 
				}
				
			}else{///////////////////////////////////////////////////////////////////////////////////////////////////////
				//this, need to differentiate by loading & unloading 
				switch(eventType){
				case 0:	//travel ended. then update time.
					//need to check for delay in front 
					System.out.println("job finished travelling: " + job.getY() + ", " + job.getX());
					job.setAgvWait(false);
					
					//check baywait (if the bay index is greater than 0
					if(job.getBayIndex() > 0){//check if previous bay waiting. 
						if(Constants.WAITBAY[job.getQcIndex()][job.getBayIndex()] < Constants.BAYSIZE){
							eventType = Constants.BAYWAIT; 
							job.setIsWaiting(true);
							time++; 
							break; 
						}
					}
					
					//check prevJob (if prev job not complete, wait (also break here) )
					int prevY = job.getY()-1; 
					
					int minY;
					if(job.getQcIndex()<2){
						minY = 0; 
					}else{
						minY = Constants.MAX_Y; 
					}

					if(prevY >= minY && !jobList.getJob(prevY, job.getX()).getLoading()){
						if(jobList.getJob(prevY, job.getX()).getAgvWait() || !jobList.getJob(prevY, job.getX()).getAssigned() || jobList.getJob(prevY, job.getX()).getIsWaiting()){
							//if prev job waiting for agv, or is waiting for qc, or is not assigned at all
							job.setIsWaiting(true);
							eventType = Constants.PREVWAIT; 
							time++; 
							break; 
						}
					}
					
					
					System.out.println("job: " + job.getY() + ", " + job.getX() + " time: " + time + " crane used: " + Constants.CRANEUSED[job.getQcIndex()] );

					//check consecutive 
					if(time <= Constants.CRANEUSED[job.getQcIndex()]){	//consecutive, need to change to delay 
						System.out.println("consecutive job, need to wait: " + job.getY() + ", " + job.getX());
						eventType = Constants.DELAY; 
						job.setIsWaiting(true);
						time = Math.max(time, Constants.CRANEUSED[job.getQcIndex()]+1); 
						Constants.CRANEUSED[job.getQcIndex()]+= 1; 
						
					}else{ //not consecutive, thus change to complete 
						job.setAssigned();
						time = job.getTotalCost() + Constants.TOTALTIME; 
						eventType = Constants.RELEASE; 
						Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;//next free time is +2 after this  
						System.out.println("job finished travelling: " + job.getY() + ", " + job.getX() + " new crane time: " + Constants.CRANEUSED[job.getQcIndex()]);
					}
					break; 
				case 1:	//job finished 
					System.out.println("job finished completion: " + job.getY() + ", " + job.getX());
					job.setComplete();
					job.getAgv().setIdle(true);
					job.getAgv().setAgvLocation(job.getEndPos());
					Constants.jobsCompleted++; 
					//also add for baywait
					Constants.WAITBAY[job.getQcIndex()][job.getBayIndex()]++; 
					break; 
				case 2: //delay
					job.setAssigned();
					job.setIsWaiting(false);
					eventType = Constants.RELEASE; 
					Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;
					time = job.getTotalCost() + Constants.TOTALTIME; 
					break;
				case 3: //baywait 
					//if baywait ends, then go to release. then reset 
					if(Constants.WAITBAY[job.getQcIndex()][job.getBayIndex()-1] >= Constants.BAYSIZE){
						//check if delay is needed here too. 
						if(time <= Constants.CRANEUSED[job.getQcIndex()]){	//consecutive, need to change to delay 
							System.out.println("consecutive job, need to wait: " + job.getY() + ", " + job.getX());
							eventType = Constants.DELAY; 
							job.setIsWaiting(true);
							time = Math.max(time, Constants.CRANEUSED[job.getQcIndex()]+1); 
							Constants.CRANEUSED[job.getQcIndex()]+= 1; 
							
						}else{ //not consecutive, thus change to complete 
							job.setAssigned();
							job.setIsWaiting(false);
							time = job.getTotalCost() + Constants.TOTALTIME; 
							eventType = Constants.RELEASE; 
							Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;//next free time is +2 after this  
							System.out.println("job finished travelling: " + job.getY() + ", " + job.getX() + " new crane time: " + Constants.CRANEUSED[job.getQcIndex()]);
						}
					}else{
						time = time++; 
					}
					
					break; 
				case 4: //prevwait 
					
					int prevY2 = job.getY()-1; 
					
					if(jobList.getJob(prevY2, job.getX()).getAgvWait() || !jobList.getJob(prevY2, job.getX()).getAssigned() || jobList.getJob(prevY2, job.getX()).getIsWaiting()){
						//if prev job waiting for agv, or is waiting for qc, or is not assigned at all
						eventType = Constants.PREVWAIT; 
						time++; 
					}else{
						//prev job is done  
						job.setIsWaiting(false);
						
						if(time <= Constants.CRANEUSED[job.getQcIndex()]){	//consecutive, need to change to delay 
							System.out.println("consecutive job, need to wait: " + job.getY() + ", " + job.getX());
							eventType = Constants.DELAY; 
							job.setIsWaiting(true);
							time = Math.max(time, Constants.CRANEUSED[job.getQcIndex()]+1); 
							Constants.CRANEUSED[job.getQcIndex()]+= 1; 
							
						}else{ //not consecutive, thus change to complete 
							job.setAssigned();
							time = job.getTotalCost() + Constants.TOTALTIME; 
							eventType = Constants.RELEASE; 
							Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;//next free time is +2 after this  
							System.out.println("job finished travelling: " + job.getY() + ", " + job.getX() + " new crane time: " + Constants.CRANEUSED[job.getQcIndex()]);
						}
						
					}
					
					
					break; 
				default: break; 
				}
			}

			//baywait 
			//previous job! 
		
		

	}


}
