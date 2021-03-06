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
	//private SplitJobList splitJobList; 

	public ArrayList<ArrayList<Job>> q_jobsList = new ArrayList<ArrayList<Job>>(); 
	
	private boolean greedyComplete = false; 
	
	//private String name; 
	
	private ArrayList<SplitJobList> splitJobListArr = new ArrayList<>();
	
	//Hashmap to store cost of jobs for each qc
	private HashMap<String, Integer> totalQcCost = new HashMap<>(); 
	
	//to pause the execution
	ArrayList<AtomicJob> atomicJobList = new ArrayList<>(); 
	
	private int[] totalCost = new int[Constants.NUM_QC]; 
	 
	private int[][] completeJobsBay = new int[Constants.NUM_QC][Constants.NUM_BAY]; 
	
	//public Sort(JobList j, SplitJobList sj, String name){
	public Sort(JobList j){
		
		this.jobList = j; 
		//this.splitJobList = sj; 
		
		//initialize the empty array
		for(int i=0; i<Constants.NUM_QC; i++){
			for(int m=0; m<Constants.NUM_BAY; m++){
				completeJobsBay[i][m] = 0; 
			}
		}
		
		//splitting jobs happen inside here
		jobListSplit();
		sortSplitJobLists(); 
		
		/*
		for(int i=0; i<Constants.NUM_QC; i++){
			for(int k=0; k<Constants.NUM_BAY; k++){
				System.out.println("qc index: " + i + ", bay no: " + k + ", number of jobs added: " + completeJobsBay[i][k]);
			}
		}*/
		
	}
	
	public void jobListSplit(){
		int numQcY = Constants.TOTAL_Y / Constants.MAX_Y; 
		int numQcX = Constants.TOTAL_X / Constants.QC_X;
		String qcName; 
		
		int index = 0; 
		
		for(int i=0; i<numQcY; i++){
			for(int j=0; j<numQcX; j++){	
				qcName = "qc" + i + j;
				//System.out.println("qcname: " + qcName);
				SplitJobList splitJobList = new SplitJobList(i, j, jobList, qcName); 
				//seeSplitJobList(splitJobList); 
				setTotalQcCost(splitJobList, index); 
				splitJobListArr.add(splitJobList); 
				index++; 
				//System.out.println("index for total qc cost: " + index);
			}
			
		}
	}
	
	public static void seeSplitJobList(SplitJobList sjl){
		int mulBays = Constants.QC_X / Constants.MAX_X; 
		for(int k=0; k<mulBays; k++){
			for(int i=0; i<Constants.MAX_Y; i++){
				for(int j=k*Constants.MAX_X; j<(k+1)*Constants.MAX_X; j++){
					System.out.print(sjl.getJob(i, j).getTotalCost()+ " ");
				}
				System.out.println(" ");
			}
			
		}
	}
	
	public void setTotalQcCost(SplitJobList sjl, int index){
		int cost = 0; 
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.QC_X; j++){
				cost += sjl.getJob(i, j).getTotalCost();
			}
		}
		
		System.out.println("total cost here: " + cost);
		totalCost[index] = cost; 
	}
	
	public void sortSplitJobLists(){
		// the sorting happens here
		for(int i=0; i<splitJobListArr.size(); i++){
			sortMerged(splitJobListArr.get(i), i);
		}
		
	}
	
	public void sortMerged(SplitJobList sjl, int qcIndex){
		
		ArrayList<Job> q_jobs = new ArrayList<>();
		Job[] sortArray = new Job[Constants.MAX_X];	//for sorting purpose 
		int numBays = Constants.QC_X / Constants.MAX_X; 
		int numHalf = Constants.MAX_Y / 2; //top 5 unloading, bottom 5 loading 

		for(int l=0; l<numBays; l++){

			//sort 1 bay at a time. update q_jobs accordingly 
			//sort unloading first
			//sortUnloading(l, sortArray, sjl, q_jobs, qcIndex);
			//sortUnloadingTop(l, sjl, q_jobs, qcIndex); 
			//test if this works for unloading too
			
			
			//sort loading
			//sortLoading(l, sortArray, sjl, q_jobs, qcIndex);
			//sortLoadingSimple(l, sortArray, sjl, q_jobs, qcIndex);
			
			//merged version of tier by tier sorting 
			//sortMergedTop(l, sjl, q_jobs, qcIndex); 
			sortMergedByTotalCost(l, sjl, q_jobs, qcIndex);
			//sortModifiedMerged(l, sortArray, sjl, q_jobs, qcIndex);

		}
		//add the q_jobs into the q_jobsList
		q_jobsList.add(q_jobs); 
		
		//System.out.println("q_jobs size inside sorted function: " + q_jobsList.size());
	}
	
	public void sortUnloading(int bayNo, Job[] sortArray, SplitJobList sjl, ArrayList<Job> q_jobs, int qcIndex){
		int arr = 0;
		for(int i=0; i<Constants.HALF_Y; i++){
			for(int j=bayNo*Constants.MAX_X; j<(bayNo+1)*Constants.MAX_X; j++){
				sortArray[arr] = sjl.getJob(i, j); 
				sjl.getJob(i, j).setBayIndex(bayNo);
				sjl.getJob(i, j).setQcIndex(qcIndex);
				
				completeJobsBay[sjl.getJob(i, j).getQcIndex()][bayNo]++; 
				//System.out.println("job: " + i + ", " + j+ " qc index: " + sjl.getJob(i, j).getQcIndex()+ ", bay no: " + bayNo );
				arr++;
			}
			sortArray = sortDescending(sortArray);
			
			for(int k=0; k<Constants.MAX_X; k++){
				q_jobs.add(sortArray[k]);
			}
			arr = 0; 
		}
	}
	
	public void sortLoading(int bayNo, Job[] sortArray, SplitJobList sjl, ArrayList<Job> q_jobs, int qcIndex){
		int arr = 0;
		
		//int splity, splitx; 
		for(int i=Constants.HALF_Y; i<Constants.MAX_Y; i++){
			for(int j=bayNo*Constants.MAX_X; j<(bayNo+1)*Constants.MAX_X; j++){
				sortArray[arr] = sjl.getJob(i, j); 
				sjl.getJob(i, j).setBayIndex(bayNo);
				sjl.getJob(i, j).setQcIndex(qcIndex);
				
				completeJobsBay[sjl.getJob(i, j).getQcIndex()][bayNo]++; 
				//System.out.println("job: " + i + ", " + j+ " qc index: " + sjl.getJob(i, j).getQcIndex()+ ", bay no: " + bayNo );
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
				
				//int fullListX, fullListY;
				
				if(nexty<Constants.MAX_Y && count < Constants.AGV-1){
					Job nextJob = jobList.getJob(nexty, x); 
					if(jobList.getJob(y, x).getVisited()==false){
						//compare, when setting visited, set in the joblist
						int nextCost = nextJob.getTotalCost();
						if(nextCost > sortArray[k].getTotalCost()){
							//add next task into the queue first 
							q_jobs.add(nextJob);
							jobList.getJob(nexty, x).setVisited(true);

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
	
	public void sortModifiedMerged(int bayNo, Job[] sortArray, SplitJobList sjl, ArrayList<Job> q_jobs, int qcIndex){
		int arr = 0;
		
		//int splity, splitx; 
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=bayNo*Constants.MAX_X; j<(bayNo+1)*Constants.MAX_X; j++){
				sortArray[arr] = sjl.getJob(i, j); 
				sjl.getJob(i, j).setBayIndex(bayNo);
				sjl.getJob(i, j).setQcIndex(qcIndex);
				
				completeJobsBay[sjl.getJob(i, j).getQcIndex()][bayNo]++; 
				//System.out.println("job: " + i + ", " + j+ " qc index: " + sjl.getJob(i, j).getQcIndex()+ ", bay no: " + bayNo );
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
				
				//int fullListX, fullListY;
				
				if(nexty<Constants.MAX_Y && count < Constants.AGV-1){
					Job nextJob = jobList.getJob(nexty, x); 
					if(jobList.getJob(y, x).getVisited()==false){
						//compare, when setting visited, set in the joblist
						int nextCost = nextJob.getTotalCost();
						if(nextCost > sortArray[k].getTotalCost()){
							//add next task into the queue first 
							q_jobs.add(nextJob);
							jobList.getJob(nexty, x).setVisited(true);

							count++; 
						}
						q_jobs.add(sortArray[k]);
					}
				}else{				
					if(jobList.getJob(y, x).getVisited() == false){
						q_jobs.add(sortArray[k]);
					}
				}
				
				System.out.println("sort modified merged, q_jobs size: " + q_jobs.size());
			}
			arr = 0; 
		}
	}
	
	public void sortLoadingSimple(int bayNo, Job[] sortArray, SplitJobList sjl, ArrayList<Job> q_jobs, int qcIndex){

		int arr = 0; 
		for(int i=Constants.HALF_Y; i<Constants.MAX_Y; i++){
			for(int j=bayNo*Constants.MAX_X; j<(bayNo+1)*Constants.MAX_X; j++){
				sortArray[arr] = sjl.getJob(i, j); 
				sjl.getJob(i, j).setBayIndex(bayNo);
				sjl.getJob(i, j).setQcIndex(qcIndex);
				
				completeJobsBay[sjl.getJob(i, j).getQcIndex()][bayNo]++; 
				//System.out.println("job: " + i + ", " + j+ " qc index: " + sjl.getJob(i, j).getQcIndex()+ ", bay no: " + bayNo );
				arr++;
			}
			sortArray = sortDescending(sortArray);
			
			for(int k=0; k<Constants.MAX_X; k++){
				q_jobs.add(sortArray[k]);
			}
			arr = 0; 
		}
		
	}
	
	public void sortUnloadingTop(int bayNo, SplitJobList sjl, ArrayList<Job> q_jobs, int qcIndex){
		//System.out.println("sort unloading top" );
		
		//first put all the column into arrayList 
		ArrayList<ArrayList<Job>> colJobs = new ArrayList<>(); 

		//problem populating this arrayList -> it was added properly. where is the problem then?? 
		for(int j = bayNo*Constants.MAX_X ; j<(bayNo+1)*Constants.MAX_X; j++){	//col
			//System.out.println("is it inside this loop?? : no");
			ArrayList<Job> columnJob = new ArrayList<>();
			for(int i=0; i<Constants.HALF_Y; i++){
				sjl.getJob(i, j).setBayIndex(bayNo);
				sjl.getJob(i, j).setQcIndex(qcIndex);
				columnJob.add(sjl.getJob(i, j)); 
				completeJobsBay[sjl.getJob(i, j).getQcIndex()][bayNo]++; 
				//System.out.println("job added to coljob: " + sjl.getJob(i, j).getY() + ", " + sjl.getJob(i, j).getX());
				//System.out.println("new coljob index: " +  j + ", size: " + columnJob.size());
			}
			colJobs.add(columnJob);
			//System.out.println("colum jobs new size: " + colJobs.size());
		}
		
		//it was populating correctly
		/*
		System.out.println("print the arrayList"); 
		for(int i=0; i<colJobs.size(); i++){
			System.out.println("index: " + i);
			for(int j=0; j<=colJobs.get(i).size()-1; j++){
				System.out.println("jobs: " + colJobs.get(i).get(j).getY() + ", " + colJobs.get(i).get(j).getX());
			}
		}
		*/ 
		
		
		//then, sort according to top 
		//Job[] sortArray = sortDescendingTop(colJobs); //do i need this? or can i just have void. I think i can just have void 
		
		ArrayList<Job> sortedArray = sortDescendingTop(colJobs, false); 
		
		for(int i=0; i<sortedArray.size(); i++){
			q_jobs.add(sortedArray.get(i)); 
		}
	}
	
	public void sortMergedTop(int bayNo, SplitJobList sjl, ArrayList<Job> q_jobs, int qcIndex){
		ArrayList<ArrayList<Job>> colJobs = new ArrayList<>(); 

		//problem populating this arrayList -> it was added properly. where is the problem then?? 
		for(int j = bayNo*Constants.MAX_X ; j<(bayNo+1)*Constants.MAX_X; j++){	//col
			//System.out.println("is it inside this loop?? : no");
			ArrayList<Job> columnJob = new ArrayList<>();
			for(int i=0; i<Constants.MAX_Y; i++){
				sjl.getJob(i, j).setBayIndex(bayNo);
				sjl.getJob(i, j).setQcIndex(qcIndex);
				columnJob.add(sjl.getJob(i, j)); 
				completeJobsBay[sjl.getJob(i, j).getQcIndex()][bayNo]++; 
				//System.out.println("job added to coljob: " + sjl.getJob(i, j).getY() + ", " + sjl.getJob(i, j).getX());
				//System.out.println("new coljob index: " +  j + ", size: " + columnJob.size());
			}
			colJobs.add(columnJob);
			//System.out.println("colum jobs new size: " + colJobs.size());
		}
		
		//it was populating correctly
		/*
		System.out.println("print the arrayList"); 
		for(int i=0; i<colJobs.size(); i++){
			System.out.println("index: " + i);
			for(int j=0; j<=colJobs.get(i).size()-1; j++){
				System.out.println("jobs: " + colJobs.get(i).get(j).getY() + ", " + colJobs.get(i).get(j).getX());
			}
		}
		*/
		
		
		//then, sort according to top 
		//Job[] sortArray = sortDescendingTop(colJobs); //do i need this? or can i just have void. I think i can just have void 
		
		ArrayList<Job> sortedArray = sortDescendingTop(colJobs, true); //add if loading and unloading 
		
		for(int i=0; i<sortedArray.size(); i++){
			q_jobs.add(sortedArray.get(i)); 
		}
	}
	
	public void sortMergedByTotalCost(int bayNo, SplitJobList sjl, ArrayList<Job> q_jobs, int qcIndex){	//sort according to the total cost remaining 
		ArrayList<ArrayList<Job>> colJobs = new ArrayList<>(); 

		for(int j = bayNo*Constants.MAX_X ; j<(bayNo+1)*Constants.MAX_X; j++){	//col
			//System.out.println("is it inside this loop?? : no");
			ArrayList<Job> columnJob = new ArrayList<>();
			for(int i=0; i<Constants.MAX_Y; i++){
				sjl.getJob(i, j).setBayIndex(bayNo);
				sjl.getJob(i, j).setQcIndex(qcIndex);
				columnJob.add(sjl.getJob(i, j)); 
				completeJobsBay[sjl.getJob(i, j).getQcIndex()][bayNo]++; 
				//System.out.println("job added to coljob: " + sjl.getJob(i, j).getY() + ", " + sjl.getJob(i, j).getX());
				//System.out.println("new coljob index: " +  j + ", size: " + columnJob.size());
			}
			colJobs.add(columnJob);
			//System.out.println("colum jobs new size: " + colJobs.size());
		}


		ArrayList<Job> sortedArray = sortedDescendingByTotalCost(colJobs);
		
		for(int i=0; i<sortedArray.size(); i++){
			q_jobs.add(sortedArray.get(i)); 
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
	
	public ArrayList<Job> sortDescendingTop(ArrayList<ArrayList<Job>> arrList, boolean b){
		//total number of jobs
		int totalNum; 
		if(b){	//the entire column 
			totalNum = Constants.MAX_Y * Constants.MAX_X; 
		}else{	//only the unloading part
			totalNum = Constants.HALF_Y * Constants.MAX_X;
		}
		
		ArrayList<Job> sortedList = new ArrayList<>(); 
		
		//tier by tier, (same method as dispatching) 
		int maxJobIndex = 0; 
		int maxCost = 0; 
		int cost; 
		
		//total number of jobs 
		
		while(totalNum > 0){
			for(int i=0; i<Constants.MAX_X; i++){
				if(arrList.get(i).size() > 0 ){
					cost = arrList.get(i).get(0).getTotalCost();
					//System.out.println("maxcost: " + maxCost + ", cost: " + cost);
					if(cost > maxCost){
						maxJobIndex = i;
						maxCost = cost; 
					}
				}
			}
			sortedList.add(arrList.get(maxJobIndex).get(0));
			arrList.get(maxJobIndex).remove(0); 
			totalNum--; 
			maxCost = 0; 
		}
		
		System.out.println("what is the size of sorted list: " + sortedList.size());
		
		return sortedList; 
	}
	
	public ArrayList<Job> sortedDescendingByTotalCost(ArrayList<ArrayList<Job>> arrList){
		//same method as dispatching! 
		ArrayList<Job> sortedList = new ArrayList<>(); 
		int[] totalSum = {0,0,0,0}; 
		int total = 0; 
		//calculate the total sum for each qc 
		
		for(int i=0; i<arrList.size(); i++){
			for(int j=0; j<arrList.get(0).size(); j++ ){
				totalSum[i] += arrList.get(i).get(j).getTotalCost(); 
				total += arrList.get(i).get(j).getTotalCost(); 
			}
		}
		
		//print to check the total cost
		/*
		for(int i=0; i<totalSum.length; i++){
			System.out.println("qc index: " + i + ", total cost: " + totalSum[i]);
		}
		System.out.println("total sum of all qc: " + total);
		*/
		
		Job j; 
		while(total>0){
			int max = 0;
			int maxIndex = 0; 
			
			for(int i=0; i<totalSum.length; i++){
				if(totalSum[i] > max){
					//System.out.println("total cost: " + totalQcCost[i]); 
					max = totalSum[i];
					maxIndex = i; 
				}
			}
			
			j = arrList.get(maxIndex).get(0);
			sortedList.add(j);
			arrList.get(maxIndex).remove(0); 
			totalSum[maxIndex] -= j.getTotalCost(); 
			total -= j.getTotalCost();
		}
		
		
		
		return sortedList; 
		
	}
	
	public boolean getGreedyComplete(){
		return greedyComplete; 
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
	
	/*
	public ArrayList<SplitJobList> getSplitJobListsSorted(){
		return splitJobListArr; 
	}*/
	
	public ArrayList<ArrayList<Job>> getJobListsSorted(){
		
		System.out.println("arraylist inside sort length: " + q_jobsList.get(0).size()); 
		
		return q_jobsList; 
	}
	
	public HashMap<String, Integer> getTotalQcCost(){
		return totalQcCost; 
	}
	
	public int[] getTotalCost(){
		return totalCost; 
	}
	
	public int[][] getCompleteBayList(){
		return completeJobsBay; 
	}
	
}
