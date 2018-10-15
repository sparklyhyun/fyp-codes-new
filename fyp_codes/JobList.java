package fyp_codes;

import java.util.*;
import javax.swing.*;
import java.awt.*;

//list of jobs to be completed
public class JobList extends JPanel{
	public final Job[][] jobs = new Job[Constants.MAX_Y][Constants.MAX_X];
	//private Agv agv = null; 
	
	public JobList(){
		int jIndex = 0;
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				jobs[i][j] = new Job(i,j); 
				//only test loading task for now
				jobs[i][j].setLoading();
				//jobs[i][j].setIndex(jIndex);
			}
		}
		System.out.println("job list done");
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
	
	public Job getJob(int y, int x){
		return jobs[y][x]; 
	}
	
	//GUI
	public void paintComponent(Graphics g){
		super.paintComponent(g);	//idk why doesnt import??? 
		GuiCell[][] guiCells = new GuiCell[Constants.MAX_Y][Constants.MAX_X];
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				guiCells[i][j] = new GuiCell(j*Constants.CELL_SIZE, i*Constants.CELL_SIZE, Constants.CELL_SIZE);
			}
		}
		
		System.out.println("populating simulator");
		
		
		//cell colours 
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				Color cellColor = null; 
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
		
		//change later! 
		public GuiCell(int borderX, int borderY, int borderSize){
			this.x = borderX + 2; 			//2 = outline
			this.y = 300 - borderY - 2; 	//400 = map y (not sure if pixels)
			this.cellSize = borderSize - 4;	//4 = outline *2 
		}
		
	}

}
