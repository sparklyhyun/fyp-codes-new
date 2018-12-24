package fyp_codes;

public class SplitJobList {
	//just to contain jobs from the full job list 
	
	public final Job[][] splitJobs = new Job[Constants.MAX_Y][Constants.QC_X]; 
	
	public SplitJobList(int y, int x, JobList fullList){
		//for splitjob, hence set true 
		
		int k = 0; //Constants.QC_X;
		int l = 0; //Constants.MAX_Y; 
		
		for(int j=x*Constants.QC_X; j<(x+1)*Constants.QC_X; j++){
			//System.out.println("x value: " + j);
			for(int i=y*(Constants.MAX_Y); i<(y+1)*(Constants.MAX_Y); i++){
				splitJobs[l][k] = fullList.getJob(i, j); 
				splitJobs[l][k].setSplitX(k);
				splitJobs[l][k].setSplitY(l);
				l++;
			}
			k++; 
			l = 0; 
		}
	}
	
	public void reset(){
		for(int i=0; i<Constants.TOTAL_Y; i++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				splitJobs[i][j].setIncomplete();
				splitJobs[i][j].setNotvisited();
			}
		}
	}
	
	public boolean isLoading(int y, int x){
		return splitJobs[y][x].getLoading();
	}
	
	public boolean isVisited(int y, int x){
		return splitJobs[y][x].getVisited();
	}
	
	public boolean isAssigned(int y, int x){

		return splitJobs[y][x].getAssigned();
	}
	
	public boolean isComplete(int y, int x){

		return splitJobs[y][x].getComplete(); 
	}
	
	public boolean isWaiting(int y, int x){

		return splitJobs[y][x].getIsWaiting();
	}
	
	public Job getJob(int y, int x){

		return  splitJobs[y][x]; 
		
	}
}
