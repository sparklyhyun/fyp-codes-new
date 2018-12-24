package fyp_codes;

import java.util.*;
import javax.swing.*;
import java.awt.*;

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
	
	//do I need to do this here?
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
				}else{
					if(isLoading(i,j)){
						if(isAssigned(i,j)){
							cellColor = Constants.COLOR_LOADING_ASSIGNED;
						}else{
							cellColor = Constants.COLOR_LOADING;
						}
					}else if(!isLoading(i,j)){
						if(isAssigned(i,j)){
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
