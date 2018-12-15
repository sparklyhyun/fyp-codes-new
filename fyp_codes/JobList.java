package fyp_codes;

import java.util.*;
import javax.swing.*;
import java.awt.*;

//list of jobs to be completed
public class JobList extends JPanel{
	//public final Job[][] jobs = new Job[Constants.MAX_Y][Constants.MAX_X];
	public final Job[][] jobs = new Job[Constants.MAX_Y][Constants.TOTAL_X];
	//private Agv agv = null; 
	
	public JobList(){
		//int jIndex = 0;
		for(int i=0; i<Constants.MAX_Y; i++){
			//for(int j=0; j<Constants.MAX_X; j++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				jobs[i][j] = new Job(i,j, false); //true - loading, false - unloading 
				System.out.println("job y: " + i + "job x: " + j + " created");
				//jIndex++; 
			}
		}
		System.out.println("job list done");
	}
	
	public void reset(){
		for(int i=0; i<Constants.MAX_Y; i++){
			//for(int j=0; j<Constants.MAX_X; j++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				jobs[i][j].setIncomplete();
				jobs[i][j].setNotvisited();
			}
		}
	}
	
	public boolean isLoading(int y, int x){
		return jobs[y][x].getLoading();
	}
	
	public boolean isVisited(int y, int x){
		return jobs[y][x].getVisited();
	}
	
	public boolean isAssigned(int y, int x){
		return jobs[y][x].getAssigned();
	}
	
	public boolean isComplete(int y, int x){
		return jobs[y][x].getComplete(); 
	}
	
	public boolean isWaiting(int y, int x){
		return jobs[y][x].getIsWaiting();
	}
	
	public Job getJob(int y, int x){
		return jobs[y][x]; 
	}
	
	//GUI
	public void paintComponent(Graphics g){
		super.paintComponent(g);	//idk why doesnt import??? 
		//GuiCell[][] guiCells = new GuiCell[Constants.MAX_Y][Constants.MAX_X];
		GuiCell[][] guiCells = new GuiCell[Constants.MAX_Y][Constants.TOTAL_X];
		for(int i=0; i<Constants.MAX_Y; i++){
			//for(int j=0; j<Constants.MAX_X; j++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				guiCells[i][j] = new GuiCell(j*Constants.CELL_SIZE, i*Constants.CELL_SIZE, Constants.CELL_SIZE);
			}
		}
		
		//System.out.println("paint simulator");
		
		
		//cell colours 
		for(int i=0; i<Constants.MAX_Y; i++){
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
				g.fillRect(guiCells[Constants.MAX_Y-i-1][j].x, guiCells[Constants.MAX_Y-i-1][j].y, guiCells[Constants.MAX_Y-i-1][j].cellSize, guiCells[Constants.MAX_Y-i-1][j].cellSize);
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
			this.y = 300 - borderY - 2; 	//400 = map y (not sure if pixels)
			this.cellSize = borderSize - 4;	//4 = outline *2 
		}
		
	}

}
