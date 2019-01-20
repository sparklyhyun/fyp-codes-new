package fyp_codes;

/*
 * this is the class that sorts individual qcs.
 * returns a sorted list for the dispatcher
 * */

import java.awt.List;
import java.lang.reflect.Array;
import java.util.*;
import javax.swing.*;

import fyp_codes.Greedy.AtomicJob;
import fyp_codes.Greedy.Lock;

import java.util.concurrent.*;	

public class Sort {
	private JobList jobList;	
	
	//the split list. to change the joblist, get full list x and y from the split list 
	private SplitJobList splitJobList; 

	private ArrayList<Job> q_jobs = new ArrayList<>(); 
	private boolean greedyComplete = false; 
	
	//to pause the execution
	ArrayList<AtomicJob> atomicJobList = new ArrayList<>(); 
	
	public Sort(JobList j, SplitJobList sj , String name, Semaphore sem){
		this.jobList = j; 
		this.splitJobList = sj; 
		
	}
	
	public void sortMerged(){
		
		Job[] sortArray = new Job[Constants.MAX_X];	//for sorting purpose 
		int numBays = Constants.QC_X / Constants.MAX_X; 
		int numHalf = Constants.MAX_Y / 2; //top 5 unloading, bottom 5 loading 

		for(int l=0; l<numBays; l++){
			//sort 1 bay at a time. update q_jobs accordingly 
			//sort unloading first
			sortUnloading(l, sortArray);
			
			//sort loading
			sortLoading(l, sortArray);
		}
	}
	
	public void sortUnloading(int bayNo, Job[] sortArray){
		int arr = 0;
		for(int i=0; i<Constants.HALF_Y; i++){
			for(int j=bayNo*Constants.MAX_X; j<(bayNo+1)*Constants.MAX_X; j++){
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
					if(jobList.getJob(y, x).getVisited() == false){
						q_jobs.add(sortArray[k]);
					}
				}
				
			}
			arr = 0; 
		}
	
	}	
	
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
	
	public boolean getGreedyComplete(){
		return greedyComplete; 
	}
	
	//get job list 
	public ArrayList<Job> getSortedJobList(){
		return q_jobs; 
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
	
	
	
}
