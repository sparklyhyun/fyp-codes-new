package fyp_codes;

public class Event {
	private Job job; 
	private int time; //time of occurence of this event 
	private int eventType; // 0 - travel, 1 - release, 2 - delay 

	private boolean loading = false; //true - loading, false - unloading 
	
	// loading jobs
	public Event(Job a, int time, int eventType, boolean loading){
		job = a; 
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
					
					//check baywait (after this state change, just break) 
					
					//check consecutive 
					if(time <= Constants.CRANEUSED[job.getQcIndex()]){	//consecutive, need to change to delay 
						eventType = Constants.DELAY; 
						job.setIsWaiting(true);
						time = Math.max(time, Constants.CRANEUSED[job.getQcIndex()]+1); 
						Constants.CRANEUSED[job.getQcIndex()]+= 1; 
						System.out.println("loading consecutive job: " + job.getY() + ", " + job.getX() + " = " + time);
						
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
				case 2: //delay
					System.out.println("delay job: " + job.getY() + ", " + job.getX());
					job.setAssigned();
					job.setIsWaiting(false);
					job.setComplete();
					eventType = Constants.RELEASE; 
					//Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;
					//no need time, as change state will change to finish job 

					break;
				case 3: //baywait 
					//if baywait ends, then go to release. then reset 
					if(Constants.WAITBAY[job.getQcIndex()][job.getBayIndex()] >= Constants.BAYSIZE){
						
						//check if delay is needed here too. 
						
						
						//this is else condition for the delay 
						job.setIsWaiting(false);
						time = job.getTotalCost() + Constants.TOTALTIME; 
						eventType = Constants.RELEASE; 
						Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;
					}else{
						time = time++; 
					}
					
					break; 
				default: break; 
				}
				
			}else{
				//this, need to differentiate by loading & unloading 
				switch(eventType){
				case 0:	//travel ended. then update time.
					//need to check for delay in front 
					System.out.println("job finished travelling: " + job.getY() + ", " + job.getX());
					job.setAgvWait(false);
					
					//check baywait 
					
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
					if(Constants.WAITBAY[job.getQcIndex()][job.getBayIndex()] >= Constants.BAYSIZE){
						
						//check if delay is needed here too. 
						
						job.setIsWaiting(false);
						time = job.getTotalCost() + Constants.TOTALTIME; 
						eventType = Constants.RELEASE; 
						Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;
					}else{
						time = time++; 
					}
					
					break; 
				default: break; 
				}
			}

			//baywait 
			//previous job! 
		
		

	}


}
