package fyp_codes;

import java.util.*;
import javax.swing.*;
import java.awt.*;

//list of jobs to be completed
public class JobList {
	public final Job[][] jobs = new Job[Constants.MAX_Y][Constants.MAX_X];
	//private Agv agv = null; 
	
	public JobList(){
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_Y; j++){
				jobs[i][j] = new Job(i,j); 
				
				//only test loading task for now
				jobs[i][j].setLoading();
			}
		}
	}
	
	public boolean isLoading(int y, int x){
		return jobs[y][x].getLoading();
	}
	
	public boolean isVisited(int y, int x){
		return jobs[y][x].getVisited();
	}
	
	public boolean isComplete(int y, int x){
		return jobs[y][x].getComplete(); 
	}
	
	//GUI
	public void paintComponent(Graphics g){
		super.paintComponent(g);	//idk why doesnt import??? 
		GuiCell[][] guiCells = new GuiCell[Constants.MAX_X][Constants.MAX_Y];
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				guiCells[i][j] = new GuiCell(j*Constants.CELL_SIZE, i*Constants.CELL_SIZE, Constants.CELL_SIZE);
			}
		}
		
		//cell colours 
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_Y; j++){
				Color cellColor; 
				if(isLoading(i,j)){
					cellColor = Constants.COLOR_LOADING;
				}else if(!isLoading(i,j)){
					cellColor = Constants.COLOR_UNLOADING;
				}else if(isComplete(i,j)){
					cellColor = Constants.COLOR_COMPLETE; 
				}
				g.setColor(cellColor);
				g.fillRect(guiCells[i][j].x, guiCells[i][j].y, guiCells[i][j].cellSize, guiCells[i][j].cellSize);
			}
			
			
		}

	}
	
	private class GuiCell{
		public final int x;
		public final int y; 
		public final int cellSize; 
		
		public GuiCell(int borderX, int borderY, int borderSize){
			this.x = borderX + 2; //2 = outline
			this.y = 400 - borderY - 2; 	//400 = map y (not sure if pixels)
			this.cellSize = borderSize - 4;	//4 = outline *2 
		}
		
	}

}
