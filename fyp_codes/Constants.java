package fyp_codes;

import java.util.HashMap; 

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

//total number of qc = 4


public class Constants {
	public static final int MAX_X = 4; 	//bay size (for 1) ?
	public static final int MAX_Y = 10;	//bay height?
	public static final int BAYSIZE = MAX_X * MAX_Y; 
	public static int TOTAL_JOB_NO = 0; 
	
	public static final int HALF_Y = MAX_Y/2;	//half is unloading, half is loading 
	
	public static final int QC_X = 12; // 3 bays here 
	
	public static final int NUM_BAY = 3; 
	
	public static int TOTAL_Y = 20; // 2 qc, top and bottom 
	public static int TOTAL_X = 24; // 2 qc, left and right
	
	//public static final int TOTAL_X = 48; // 2 qc, left and right
	
	public static final int TOTAL_SIZE = TOTAL_Y * TOTAL_X; 
	
	public static int NUM_QC_X = 2;
	public static int NUM_QC_Y = 2; 
	public static int NUM_QC = Constants.NUM_QC_X * Constants.NUM_QC_Y;
	
	public static int AGV = 12; //number of AGV
	
	public static final int CELL_SIZE = 20; 
	public static final Color COLOR_LOADING = Color.BLUE;
	public static final Color COLOR_LOADING_ASSIGNED = Color.CYAN;	//when assigned a vehicle
	public static final Color COLOR_UNLOADING = Color.GREEN;
	public static final Color COLOR_UNLOADING_ASSIGNED = Color.YELLOW;	//when assgined a vehicle
	public static final Color COLOR_COMPLETE = Color.WHITE; 
	public static final Color COLOR_WAITING = Color.ORANGE;
	public static final Color COLOR_WAITAGV = Color.LIGHT_GRAY; //assigned vehicle, but waiting for agv 
	
	public static final int SLEEP = 20; 
	
	// events here....
	public static final int TRAVEL = 0; 	//agv travel end time
	public static final int RELEASE = 1; 	//agv release time 
	public static final int DELAY = 2; 		//agv delay 
	public static final int BAYWAIT = 3; 	//job waiting for the bay!
	public static final int PREVWAIT = 4; 
	
	public static int[] CRANEUSED = {0,0,0,0}; //the latest crane used time 
	public static int[][] WAITBAY ; 	//jobs completed per bay 
	
	public static boolean BUGDETECTED = false; 
	
	public static int TOTALDELAY = 0; 
	public static int TOTALTIME = 0;
	
	public static int TRAVELTIME = 0; 
	
	public static int allComplete = 0; 
	
	public static int jobsCompleted = 0; 
	
	public static int[] craneCoord = {2, 7, 12, 17}; 
	
	//for cost calculation
	public static final int TURN_COST = 1; 
	public static final int VERT_COST = 5;
	public static final int HOR_COST = 4; 
	
	//does this work like this? 
	static class DelayComp{
		public static JLabel lblDelaytime_counter = new JLabel("delayTime_Counter");
		public static JLabel lblTotaltime_counter = new JLabel("totalTime_Counter");
		
		
		
		public DelayComp(){
			lblTotaltime_counter.setVerticalAlignment(SwingConstants.TOP);
			lblTotaltime_counter.setFont(new Font("Arial", Font.PLAIN, 12));
			lblTotaltime_counter.setBounds(360, 370, 70, 30);
			
			lblDelaytime_counter.setVerticalAlignment(SwingConstants.TOP);
			lblDelaytime_counter.setFont(new Font("Arial", Font.PLAIN, 12));
			lblDelaytime_counter.setBounds(525, 370, 70, 30);
			
			Timer timer = new Timer(SLEEP, new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					updateTotalTimer(); 
					updateDelayTimer(); 
				}
				
			} );
			
			timer.start();
		}
		
		public JLabel getDelayCounter(){
			return lblDelaytime_counter;
		}
		
		public JLabel getTotalCounter(){
			return lblTotaltime_counter; 
		}
		
		public void updateDelayTimer(){
			lblDelaytime_counter.setText(" "+Integer.toString(Constants.TOTALDELAY)+ "         ");
		}
		
		public void updateTotalTimer(){
			lblTotaltime_counter.setText(" " +Integer.toString(Constants.TOTALTIME)+ "         ");
		}
		
		public int getTotalTimerText(){
			String a = lblTotaltime_counter.getText().replaceAll("\\s+",""); 
			return Integer.parseInt(a);  
		}
	}
	
	public static DelayComp TIMERS = new DelayComp(); 
	
	
	//test cases here 	
	//new test case 1 (all random cost) 
	public static final int[][] testCaseStart0 = {	//this is the starting point for all cases. 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
	}; //all random cost
	
	public static final int[][] testCaseEnd0 = {	//this is the end for all test cases
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} 
	}; 
	
	//start y for test case 1 (random cases) ==================================================================
	
	public static final int[][] testCaseNew1Start1 = {
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{12,2,12,12,2,7,7,2,2,17,7,17,7,17,12,7,17,17,12,2,12,17,12,17}, 
			{12,12,12,17,7,7,7,12,12,17,12,12,7,7,7,12,12,12,17,7,12,7,7,12}, 
			{17,7,7,12,12,12,2,12,12,2,17,2,2,12,7,17,17,12,7,17,7,7,7,17}, 
			{12,17,7,7,7,12,7,7,7,12,7,17,12,2,7,12,12,2,17,17,17,12,2,7}, 
			{7,12,2,17,12,17,17,17,7,17,7,12,2,17,12,17,7,7,12,7,17,17,12,7}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{17,12,2,2,7,17,12,17,12,2,2,17,12,12,7,12,7,2,7,2,17,2,2,17}, 
			{17,17,12,17,12,12,7,12,17,7,12,7,7,2,2,17,7,17,2,12,7,7,17,2}, 
			{12,12,7,12,17,7,7,7,17,12,2,12,7,12,12,7,17,17,2,2,7,12,17,7}, 
			{17,2,17,7,17,2,12,17,7,7,7,17,17,2,2,12,17,7,2,12,7,7,17,2}, 
			{7,2,2,2,2,7,2,7,17,2,2,17,7,12,17,12,7,7,17,12,7,17,2,12}
	}; 

	// end y for test case 1 (all random) ==========================================================================
	
	public static final int[][] testCaseNew1End1 = {
			{17,2,2,12,17,2,2,2,2,12,17,12,12,12,2,7,7,7,2,17,2,7,12,7}, 
			{2,2,17,17,12,17,2,12,7,17,7,17,17,7,12,17,2,17,17,7,7,2,12,12}, 
			{2,7,2,12,17,12,12,17,12,7,7,12,2,12,12,7,12,12,7,12,2,17,12,7}, 
			{7,12,2,7,2,12,7,2,2,7,12,12,17,2,12,17,7,2,7,7,12,7,17,12}, 
			{12,12,2,17,12,7,2,7,12,12,12,2,12,17,17,2,12,2,17,7,12,7,17,2}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{12,2,12,7,17,2,12,7,17,12,12,17,17,17,7,2,2,12,2,17,7,2,2,7}, 
			{17,7,17,17,17,2,12,12,17,17,2,17,17,7,7,2,17,12,7,7,12,2,12,12}, 
			{12,7,17,17,2,12,2,2,17,17,17,7,7,2,12,12,7,7,17,7,17,12,17,17}, 
			{17,17,17,2,17,17,17,12,2,12,2,17,17,7,2,2,7,2,17,17,2,17,2,17}, 
			{2,2,2,7,7,2,7,2,12,17,2,2,7,7,7,2,2,2,2,12,2,17,17,2}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}
	};	

	//new test case 2 (qc1 higher cost)
	//start and end index 0 are the same. can re-use the previous one 
	public static final int[][] testCaseNew2Start1 = {
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{17,17,17,17,17,17,17,17,17,17,17,17,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{17,17,17,17,17,17,17,17,17,17,17,17,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{17,17,17,17,17,17,17,17,17,17,17,17,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{17,17,17,17,17,17,17,17,17,17,17,17,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{17,17,17,17,17,17,17,17,17,17,17,17,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}
	};
	
	public static final int[][] testCaseNew2End1 = {
			{17,17,17,17,17,17,17,17,17,17,17,17,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{17,17,17,17,17,17,17,17,17,17,17,17,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{17,17,17,17,17,17,17,17,17,17,17,17,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{17,17,17,17,17,17,17,17,17,17,17,17,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{17,17,17,17,17,17,17,17,17,17,17,17,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17} 
	}; 
	
	//new test case 3 (qc1 lower cost, all others random) 
	public static final int[][] testCaseNew3Start1 = {
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,2,12,17,17,7,12,7,12,12,17,12}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,2,7,7,2,7,17,2,7,12,7,17}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,12,7,12,12,2,2,7,17,7,2}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,12,7,17,12,2,17,7,17,7,17,2,12}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,17,7,17,12,12,7,7,17,17,12,17,7}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{17,12,12,2,12,12,2,7,7,7,17,2,2,7,17,7,17,7,7,2,7,12,2,2}, 
			{17,12,2,17,7,17,12,2,2,7,7,2,17,2,7,2,2,12,2,12,7,7,7,7}, 
			{2,2,7,12,12,12,2,7,7,12,2,12,2,12,17,2,12,2,17,7,7,7,2,7}, 
			{12,17,7,12,7,12,17,12,2,7,12,2,17,7,7,12,7,7,7,17,7,17,17,12}, 
			{2,12,2,17,12,12,2,12,17,17,7,17,12,7,2,2,12,2,7,2,17,12,7,17} 
	};
	
	public static final int[][] testCaseNew3End1 = {
			{2,2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,2,2,2,12,12,17,7,17}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,12,2,2,12,2,17,7,17,17,7,2,12}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,2,12,2,7,7,7,17,2,2,17,2,17}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,12,2,12,7,2,2,17,12,17,12,2,17}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,17,17,17,12,7,17,2,17,12,12,7,12}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7}, 
			{2,17,12,12,2,17,17,2,7,12,2,2,12,2,7,7,7,7,2,7,17,7,12,2}, 
			{12,2,12,17,2,2,7,17,12,2,17,12,7,12,2,12,7,2,2,7,7,17,12,17}, 
			{17,7,2,2,2,17,2,17,2,7,12,12,12,7,17,2,2,2,2,2,2,17,7,2}, 
			{17,2,17,7,2,7,12,2,2,12,12,17,7,2,17,7,2,12,12,2,12,2,12,7}, 
			{7,2,7,12,17,2,7,7,12,2,2,12,17,17,2,2,2,17,17,17,17,12,12,12}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17}, 
			{12,12,12,12,12,12,12,12,12,12,12,12,17,17,17,17,17,17,17,17,17,17,17,17} 
	}; 
	
	
}
