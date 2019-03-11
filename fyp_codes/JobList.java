package fyp_codes;

import java.util.*;
import javax.swing.*;
import java.awt.*;

//list of jobs to be completed
public class JobList extends JPanel{
	public final Job[][] jobs = new Job[Constants.TOTAL_Y][Constants.TOTAL_X];
	
	public JobList(){
	
		/*
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.QC_X; j++){
				jobs[i][j] = new Job(i,j, false); //true - loading, false - unloading 
				System.out.println("job y: " + i + "job x: " + j + " created");
			}
		}*/
		
		//creating the full job list 
		createJobLists(); 
		
		//fixed test cases for testing 
		//tests(3); 

		calcTotalJobNum(); 
		
		//print jobs
		//printJobs(jobs); 
		
		reset(); 
		System.out.println("job list done");
	}
	
	public void tests(int x){
		/*
		switch(x){
		case 1: testCases(Constants.testCaseCost1); break; 
		case 2: testCases(Constants.testCaseCost2); break;
		case 3: testCases(Constants.testCaseCost3); break;
		case 4: testCases(Constants.testCaseCost4); break; 
		default: testCases(Constants.testCaseCost4); break; 
		}*/
		
		switch(x){
		case 1: //all at random cost
			testCases(Constants.testCaseNew1Start0, Constants.testCaseNew1Start1, Constants.testCaseNew1End0, Constants.testCaseNew1End1);
			break; 
		case 2:	//qc1 higher cost, others low cost
			testCases(Constants.testCaseNew1Start0, Constants.testCaseNew2Start1, Constants.testCaseNew1End0, Constants.testCaseNew2End1);
			break; 
		case 3: // qc1 lower cost, others at random 
			testCases(Constants.testCaseNew1Start0, Constants.testCaseNew3Start1, Constants.testCaseNew1End0, Constants.testCaseNew3End1);
			break; 
		default: break; 
		}
		
		
	}
	
	public void createJobLists(){
		//createFullList(); 
		//firstQcHigherCost(); 
		//firstQcLowerCost(); 
		//firstQcLoading(); 
		//allRandomCost(); 
		
		//updated ones with new coordinates (try with random one first) 
		//createFullList(); 
		//firstQcHigherCost();
		firstQcLowerCost(); 
		
	}

	
	public void testCases(int[][] costs){
		for(int j=0; j<Constants.TOTAL_X; j++){
			for(int i=0; i<5; i++){
				jobs[i][j] = new Job(i,j, false); //true - loading, false - unloading 
				jobs[i][j].setCost(costs[i][j]);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=5; i<10; i++){
				jobs[i][j] = new Job(i,j, true); //true - loading, false - unloading 
				jobs[i][j].setCost(costs[i][j]);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=10; i<15; i++){
				jobs[i][j] = new Job(i,j, false); //true - loading, false - unloading 
				jobs[i][j].setCost(costs[i][j]);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=15; i<20; i++){
				jobs[i][j] = new Job(i,j, true); //true - loading, false - unloading 
				jobs[i][j].setCost(costs[i][j]);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
		}
	}
	
	public void testCases(int[][] start0, int[][] start1, int[][] end0, int[][] end1){
		for(int j=0; j<Constants.TOTAL_X; j++){
			for(int i=0; i<5; i++){
				jobs[i][j] = new Job(i,j, false); //true - loading, false - unloading 
				jobs[i][j].setStartPos(start0[i][j], start1[i][j]);
				jobs[i][j].setEndPos(end0[i][j], end1[i][j]);
				jobs[i][j].calcTotalCost();
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=5; i<10; i++){
				jobs[i][j] = new Job(i,j, true); //true - loading, false - unloading 
				jobs[i][j].setStartPos(start0[i][j], start1[i][j]);
				jobs[i][j].setEndPos(end0[i][j], end1[i][j]);
				jobs[i][j].calcTotalCost();
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=10; i<15; i++){
				jobs[i][j] = new Job(i,j, false); //true - loading, false - unloading 
				jobs[i][j].setStartPos(start0[i][j], start1[i][j]);
				jobs[i][j].setEndPos(end0[i][j], end1[i][j]);
				jobs[i][j].calcTotalCost();
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=15; i<20; i++){
				jobs[i][j] = new Job(i,j, true); //true - loading, false - unloading 
				jobs[i][j].setStartPos(start0[i][j], start1[i][j]);
				jobs[i][j].setEndPos(end0[i][j], end1[i][j]);
				jobs[i][j].calcTotalCost();
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
		}
	}
	
	public void printJobs(Job[][] lists){
		System.out.println("print test cases");
		for(int i=0; i<Constants.TOTAL_Y; i++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				System.out.print(lists[i][j].getTotalCost()+ " ");
			}
			System.out.println(" ");
		}
	}
	
	public void createFullList(){
		/*
		for(int j=0; j<Constants.TOTAL_X; j++){
			for(int i=0; i<5; i++){
				jobs[i][j] = new Job(i,j, false); //true - loading, false - unloading 
				jobs[i][j].initCost(1);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=5; i<10; i++){
				jobs[i][j] = new Job(i,j, true); //true - loading, false - unloading 
				jobs[i][j].initCost(1);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=10; i<15; i++){
				jobs[i][j] = new Job(i,j, false); //true - loading, false - unloading 
				jobs[i][j].initCost(1);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=15; i<20; i++){
				jobs[i][j] = new Job(i,j, true); //true - loading, false - unloading 
				jobs[i][j].initCost(1);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
		}*/
		
		for(int j=0; j<Constants.TOTAL_X; j++){
			for(int i=0; i<5; i++){
				jobs[i][j] = new Job(i,j, false); //true - loading, false - unloading 
				jobs[i][j].initCost2(2);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=5; i<10; i++){
				jobs[i][j] = new Job(i,j, true); //true - loading, false - unloading 
				jobs[i][j].initCost2(2);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=10; i<15; i++){
				jobs[i][j] = new Job(i,j, false); //true - loading, false - unloading 
				jobs[i][j].initCost2(2);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=15; i<20; i++){
				jobs[i][j] = new Job(i,j, true); //true - loading, false - unloading 
				jobs[i][j].initCost2(2);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
		}
	}
	
	public void firstQcHigherCost(){
		//qc 1
		/*
		for(int i=0; i<Constants.TOTAL_X; i++){
			if(i<Constants.QC_X){ //1st qc 
				for(int j=0; j<5; j++){
					jobs[j][i] = new Job(j,i, false); 
					jobs[j][i].initCost(0);
				}
				for(int k=5;k<10; k++ ){
					jobs[k][i] = new Job(k,i, true);
					jobs[k][i].initCost(0);
				}
			}else{
				for(int j=0; j<5; j++){
					jobs[j][i] = new Job(j,i, false); 
					jobs[j][i].initCost(1);
				}
				for(int k=5;k<10; k++ ){
					jobs[k][i] = new Job(k,i, true);
					jobs[k][i].initCost(1);
				}
			}
		}
		
		for(int i=0; i<Constants.TOTAL_X; i++){
			for(int j=10; j<15; j++){
				jobs[j][i] = new Job(j, i, false);
				jobs[j][i].initCost(1);
			}
			for(int j=15; j<20; j++){
				jobs[j][i] = new Job(j, i, true);
				jobs[j][i].initCost(1);
			}
		}
		*/
		
		for(int i=0; i<Constants.TOTAL_X; i++){
			if(i<Constants.QC_X){ //1st qc 
				for(int j=0; j<5; j++){
					jobs[j][i] = new Job(j,i, false); 
					jobs[j][i].initCost2(1);
				}
				for(int k=5;k<10; k++ ){
					jobs[k][i] = new Job(k,i, true);
					jobs[k][i].initCost2(1);
				}
			}else{
				for(int j=0; j<5; j++){
					jobs[j][i] = new Job(j,i, false); 
					jobs[j][i].initCost2(0);
				}
				for(int k=5;k<10; k++ ){
					jobs[k][i] = new Job(k,i, true);
					jobs[k][i].initCost2(0);
				}
			}
		}
		
		for(int i=0; i<Constants.TOTAL_X; i++){
			for(int j=10; j<15; j++){
				jobs[j][i] = new Job(j, i, false);
				jobs[j][i].initCost2(0);
			}
			for(int j=15; j<20; j++){
				jobs[j][i] = new Job(j, i, true);
				jobs[j][i].initCost2(0);
			}
		}
		

	}
	
	public void firstQcLowerCost(){
		/*
		for(int i=0; i<Constants.TOTAL_X; i++){
			if(i<Constants.QC_X){ //1st qc 
				for(int j=0; j<5; j++){
					jobs[j][i] = new Job(j,i, false); 
					jobs[j][i].initCost(2);
				}
				for(int k=5;k<10; k++ ){
					jobs[k][i] = new Job(k,i, true);
					jobs[k][i].initCost(2);
				}
			}else{
				for(int j=0; j<5; j++){
					jobs[j][i] = new Job(j,i, false); 
					jobs[j][i].initCost(1);
				}
				for(int k=5;k<10; k++ ){
					jobs[k][i] = new Job(k,i, true);
					jobs[k][i].initCost(1);
				}
			}
		}
		
		for(int i=0; i<Constants.TOTAL_X; i++){
			for(int j=10; j<15; j++){
				jobs[j][i] = new Job(j, i, false);
				jobs[j][i].initCost(1);
			}
			for(int j=15; j<20; j++){
				jobs[j][i] = new Job(j, i, true);
				jobs[j][i].initCost(1);
			}
		}*/
		
		for(int i=0; i<Constants.TOTAL_X; i++){
			if(i<Constants.QC_X){ //1st qc 
				for(int j=0; j<5; j++){
					jobs[j][i] = new Job(j,i, false); 
					jobs[j][i].initCost2(0);
				}
				for(int k=5;k<10; k++ ){
					jobs[k][i] = new Job(k,i, true);
					jobs[k][i].initCost2(0);
				}
			}else{
				for(int j=0; j<5; j++){
					jobs[j][i] = new Job(j,i, false); 
					jobs[j][i].initCost2(2);
				}
				for(int k=5;k<10; k++ ){
					jobs[k][i] = new Job(k,i, true);
					jobs[k][i].initCost2(2);
				}
			}
		}
		
		for(int i=0; i<Constants.TOTAL_X; i++){
			for(int j=10; j<15; j++){
				jobs[j][i] = new Job(j, i, false);
				jobs[j][i].initCost2(2);
			}
			for(int j=15; j<20; j++){
				jobs[j][i] = new Job(j, i, true);
				jobs[j][i].initCost2(2);
			}
		}
		
	}
	
	public void firstQcLoading(){
		for(int i=0; i<Constants.TOTAL_X; i++){
			if(i<Constants.QC_X){ //1st qc 
				for(int j=0; j<5; j++){
					jobs[j][i] = new Job(j,i, true); 
					jobs[j][i].initCost(0);
				}
				for(int k=5;k<10; k++ ){
					jobs[k][i] = new Job(k,i, true);
					jobs[k][i].initCost(0);
				}
			}else{
				for(int j=0; j<5; j++){
					jobs[j][i] = new Job(j,i, false); 
					jobs[j][i].initCost(1);
				}
				for(int k=5;k<10; k++ ){
					jobs[k][i] = new Job(k,i, true);
					jobs[k][i].initCost(1);
				}
			}
		}
		
		for(int i=0; i<Constants.TOTAL_X; i++){
			for(int j=10; j<15; j++){
				jobs[j][i] = new Job(j, i, false);
				jobs[j][i].initCost(1);
			}
			for(int j=15; j<20; j++){
				jobs[j][i] = new Job(j, i, true);
				jobs[j][i].initCost(1);
			}
		}	
	}
	
	public void allRandomCost(){
		for(int j=0; j<Constants.TOTAL_X; j++){
			for(int i=0; i<5; i++){
				jobs[i][j] = new Job(i,j, false); //true - loading, false - unloading 
				jobs[i][j].initCost(3);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=5; i<10; i++){
				jobs[i][j] = new Job(i,j, true); //true - loading, false - unloading 
				jobs[i][j].initCost(3);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=10; i<15; i++){
				jobs[i][j] = new Job(i,j, false); //true - loading, false - unloading 
				jobs[i][j].initCost(3);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
			for(int i=15; i<20; i++){
				jobs[i][j] = new Job(i,j, true); //true - loading, false - unloading 
				jobs[i][j].initCost(3);
				//System.out.println("job y: " + i + "job x: " + j + " created");
			}
		}
	}
	
	/*
	public JobList(int y, int x, JobList fullList){
		//for splitjob, hence set true 
		this.splitJobList = true; 
		
		int k = 0; //Constants.QC_X;
		int l = 0; //Constants.MAX_Y; 
		
		//System.out.println("\nspllit list index: " + y + ", " + x);
		
		for(int j=x*Constants.QC_X; j<(x+1)*Constants.QC_X; j++){
			//System.out.println("x value: " + j);
			for(int i=y*(Constants.MAX_Y); i<(y+1)*(Constants.MAX_Y); i++){
				//System.out.println("y value: " + i);
				splitJobs[l][k] = fullList.getJob(i, j); 
				splitJobs[l][k].setSplitX(k);
				splitJobs[l][k].setSplitY(l);
				//testing index
				//System.out.println("split job y: " + splitJobs[l][k].getY() + ", x: " + splitJobs[l][k].getY() + ", fullList y: " + fullList.getJob(i, j).getY() + ", x: " + fullList.getJob(i, j).getX());
				
				l++;
			}
			k++; 
			l = 0; 
		}
	}*/ 
	
	public void reset(){
		
		for(int i=0; i<Constants.TOTAL_Y; i++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				jobs[i][j].setIncomplete();
				jobs[i][j].setVisited(false);
				jobs[i][j].setNotAssigned();
				jobs[i][j].setIsWaiting(false);
			}
		}
		/*
		if(splitJobList == true){
			for(int i=0; i<Constants.MAX_Y; i++){
				for(int j=0; j<Constants.QC_X; j++){
					splitJobs[i][j].setIncomplete();
					splitJobs[i][j].setNotvisited();
				}
			}
		}else{
			for(int i=0; i<Constants.TOTAL_Y; i++){
				for(int j=0; j<Constants.TOTAL_X; j++){
					jobs[i][j].setIncomplete();
					jobs[i][j].setNotvisited();
				}
			}
		}*/
	}
	
	public void calcTotalJobNum(){
		for(int i=0; i<Constants.TOTAL_Y; i++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				if(jobs[i][j].getTotalCost()>0){
					Constants.TOTAL_JOB_NO++; 
				}else{
					jobs[i][j].setComplete();
				}
			}
		}
		System.out.println("total job no: " + Constants.TOTAL_JOB_NO);
	}
	
	public void printStartEndPt(){
		System.out.println("start position 0");
		for(int i=0; i<Constants.TOTAL_Y; i++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				System.out.print(jobs[i][j].getStartPos()[0] + ",");
			}
			System.out.println(" "); 
		}
		
		System.out.println("start posision 1");
		for(int i=0; i<Constants.TOTAL_Y; i++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				System.out.print(jobs[i][j].getStartPos()[1] + ",");
			}
			System.out.println(" "); 
		}
		
		
		System.out.println("end position 0");
		for(int i=0; i<Constants.TOTAL_Y; i++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				System.out.print(jobs[i][j].getEndPos()[0] + ",");
			}
			System.out.println(" "); 
		}
		
		System.out.println("end position 1");
		for(int i=0; i<Constants.TOTAL_Y; i++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				System.out.print(jobs[i][j].getEndPos()[1] + ",");
			}
			System.out.println(" "); 
		}
	}
	
	public boolean isLoading(int y, int x){
		/*
		if(splitJobList == true){	
			return splitJobs[y][x].getLoading();
		}*/
		return jobs[y][x].getLoading();
	}
	
	public boolean isVisited(int y, int x){
		/*if(splitJobList == true){
			return splitJobs[y][x].getVisited();
		}*/
		return jobs[y][x].getVisited();
	}
	
	public boolean isAssigned(int y, int x){
		/*if(splitJobList == true){
			return splitJobs[y][x].getAssigned();
		}*/
		return jobs[y][x].getAssigned();
	}
	
	public boolean isComplet(int y, int x){

		return jobs[y][x].getComplete(); 
	}
	
	public boolean isComplete(int y, int x){
		return jobs[y][x].getComplete(); 
	}
	
	public boolean isWaiting(int y, int x){
		/*if(splitJobList == true){
			return splitJobs[y][x].getIsWaiting();
		}*/
		return jobs[y][x].getIsWaiting();
	}
	
	public Job getJob(int y, int x){
		/*if(splitJobList == true){
			return  splitJobs[y][x]; 
		}*/
		return jobs[y][x];
		
	}
	
	public boolean isAgvWait(int y, int x){
		return jobs[y][x].getAgvWait(); 
	}
	
	public void clearJobList(){
		for(int i=0; i<jobs.length; i++){
			Arrays.fill(jobs[i], null);
		}
	}
	
	//GUI
	public void paintComponent(Graphics g){
		super.paintComponent(g);	//idk why doesnt import??? 
		//GuiCell[][] guiCells = new GuiCell[Constants.MAX_Y][Constants.MAX_X];
		GuiCell[][] guiCells = new GuiCell[Constants.TOTAL_Y][Constants.TOTAL_X];
		for(int i=0; i<Constants.TOTAL_Y; i++){
			//for(int j=0; j<Constants.MAX_X; j++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				guiCells[i][j] = new GuiCell(j*Constants.CELL_SIZE, i*Constants.CELL_SIZE, Constants.CELL_SIZE);
			}
		}
		
		//System.out.println("paint simulator");
		
		
		//cell colours 
		for(int i=0; i<Constants.TOTAL_Y; i++){
			//for(int j=0; j<Constants.MAX_X; j++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				Color cellColor = null; 
				
				//for repaint when complete
				if(isComplete(i,j)){
					cellColor = Constants.COLOR_COMPLETE;
				}else if(isWaiting(i,j)){
					cellColor = Constants.COLOR_WAITING;
				}else if(isAgvWait(i,j)){
					cellColor = Constants.COLOR_WAITAGV; 
				}
				
				else{
					if(isLoading(i,j)){
						if(isAssigned(i,j) && isWaiting(i,j)){
							cellColor = Constants.COLOR_WAITAGV; 
						}
						else if(isAssigned(i,j) && !isWaiting(i,j)){
							cellColor = Constants.COLOR_LOADING_ASSIGNED;
						}
						else{
							cellColor = Constants.COLOR_LOADING;
						}
					}else if(!isLoading(i,j)){
						if(!isAssigned(i,j) && isWaiting(i,j)){
							cellColor = Constants.COLOR_WAITAGV; 
						}
						else if(isAssigned(i,j) && !isWaiting(i,j)){
							cellColor = Constants.COLOR_UNLOADING_ASSIGNED;	
						}
						else{
							cellColor = Constants.COLOR_UNLOADING;
						}
					}
				}
				g.setColor(cellColor);
				
				//modify here to make a space between the qc lol 
				g.fillRect(guiCells[Constants.TOTAL_Y-i-1][j].x, guiCells[Constants.TOTAL_Y-i-1][j].y, guiCells[Constants.TOTAL_Y-i-1][j].cellSize, guiCells[Constants.TOTAL_Y-i-1][j].cellSize);
			}
			
			
		}

	}
	
	private class GuiCell{
		public final int x;
		public final int y; 
		public final int cellSize; 
		
		//change later! 
		public GuiCell(int borderX, int borderY, int borderSize){
			this.x = borderX + 2; 			//2 = outline
			this.y = 410 - borderY - 1; 	//400 = map y (not sure if pixels)
			this.cellSize = borderSize - 4;	//4 = outline *2 
		}
		
	}

}
