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
		System.out.println("change state job: " + job.getY() + ", " + job.getX() + " = " + eventType);
			if(loading){
				
			}else{
				//this, need to differentiate by loading & unloading 
				switch(eventType){
				case 0:	//travel. then update time.
					//need to check for delay in front 
					System.out.println("job finished travelling: " + job.getY() + ", " + job.getX());
					job.setAgvWait(false);
					
					if(time <= Constants.CRANEUSED[job.getQcIndex()]){
						eventType = Constants.DELAY; 
						job.setIsWaiting(true);
						time = Math.max(time, Constants.CRANEUSED[job.getQcIndex()]); 
						Constants.CRANEUSED[job.getQcIndex()]+= 1; 
						
					}else{
						job.setAssigned();
						time = job.getTotalCost() + Constants.TOTALTIME; 
						eventType = Constants.RELEASE; 
						Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;	//next free time is +2 after this  
						System.out.println("job finished travelling: " + job.getY() + ", " + job.getX() + " new time: " + time);
					}
					
					break; 
				case 1:	//job finished 
					System.out.println("job finished completion: " + job.getY() + ", " + job.getX());
					job.setComplete();
					job.getAgv().setIdle(true);
					job.getAgv().setAgvLocation(job.getEndPos());
					Constants.jobsCompleted++; 
					break; 
				case 2: //delay
					if(time >= Constants.CRANEUSED[job.getQcIndex()]){
						job.setIsWaiting(false);
						job.setAssigned();
						eventType = Constants.RELEASE; 
						Constants.CRANEUSED[job.getQcIndex()] = Constants.TOTALTIME + 1;
					}else{
						time = Math.max(time, Constants.CRANEUSED[job.getQcIndex()]); 
						Constants.CRANEUSED[job.getQcIndex()]+= 1; 
					}
					break; 
				default: break; 
				}
			}

			
		
		

	}


}
