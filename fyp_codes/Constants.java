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
	
	public static final int TOTAL_Y = 20; // 2 qc, top and bottom 
	public static final int TOTAL_X = 24; // 2 qc, left and right
	
	//public static final int TOTAL_X = 48; // 2 qc, left and right
	
	public static final int TOTAL_SIZE = TOTAL_Y * TOTAL_X; 
	
	public static final int NUM_QC_X = 2;
	public static final int NUM_QC_Y = 2; 
	public static int NUM_QC = Constants.NUM_QC_X * Constants.NUM_QC_Y;
	
	public static final int AGV = 12; //number of AGV
	
	public static final int CELL_SIZE = 20; 
	public static final Color COLOR_LOADING = Color.BLUE;
	public static final Color COLOR_LOADING_ASSIGNED = Color.CYAN;	//when assigned a vehicle
	public static final Color COLOR_UNLOADING = Color.GREEN;
	public static final Color COLOR_UNLOADING_ASSIGNED = Color.YELLOW;	//when assgined a vehicle
	public static final Color COLOR_COMPLETE = Color.WHITE; 
	public static final Color COLOR_WAITING = Color.ORANGE;
	public static final Color COLOR_WAITAGV = Color.LIGHT_GRAY; //assigned vehicle, but waiting for agv 
	
	public static final int SLEEP = 15; 
	
	// events here....
	public static final int TRAVEL = 0; 	//agv travel end time
	public static final int RELEASE = 1; 	//agv release time 
	public static final int DELAY = 2; 		//agv delay 
	public static final int BAYWAIT = 3; 	//job waiting for the bay!
	public static final int PREVWAIT = 4; 
	//public static final int BAYWAITDELAY = 5; 
	
	
	public static int[] CRANEUSED = {0,0,0,0}; //the latest crane used time 
	//public static int[] CRANEUPDATED = {0,0,0,0}; //if the crane use time was updated....
	//public static int[] CRANEUSEDPREV = {0,0,0,0}; 
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
	
	//can i put timer here? would it work? 
	/*
	public static JLabel lblDelaytime_counter = new JLabel("delay_time_1");
	public static JLabel lblTotaltime_counter = new JLabel("total_time_1");
	*/ 
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
	public static final int[][] testCaseCost1 = {
			{12, 15, 16, 12, 11, 13, 14, 12, 12, 11, 14, 12, 15, 16, 15, 12, 13, 12, 11, 14, 15, 12 ,12, 11},  
			{14, 15, 13, 14, 13, 16, 13, 12, 15, 12, 14, 13, 13, 12, 13, 11, 12, 13, 11, 11, 13, 12, 13, 13},  
			{12, 15, 15, 14, 14, 12, 13, 14, 12, 14, 14, 13, 14, 16, 12, 11, 15, 11, 14, 12, 11, 11, 12, 15},  
			{15, 14, 16, 13, 16, 15, 13, 15, 11, 11, 11, 13, 12, 11, 13, 12, 13, 15, 12, 13, 14, 13, 16, 15}, 
			{16, 12, 12, 14, 11, 14, 11, 11, 13, 16, 15, 14, 11, 13, 15, 12, 16, 16, 15, 13, 15, 12, 16, 13},  
			{14, 12, 12, 12, 13, 15, 11, 16, 11, 13, 13, 15, 15, 15, 16, 12, 11, 16, 11, 11, 14, 12, 11, 11},  
			{12, 16, 11, 13, 16, 11, 14, 11, 13, 13, 13, 13, 14, 13, 14, 15, 15, 11, 16, 14, 14, 12, 15, 12},  
			{16, 14, 13, 11, 11, 14, 13, 11, 15, 11, 12, 13, 14, 11, 13, 16, 12, 12, 12, 12, 12, 11, 14, 14},  
			{12, 15, 13, 11, 11, 14, 14, 15, 16, 16, 12, 15, 16, 12, 12, 15, 15, 15, 15, 12, 13, 16, 16, 15},  
			{15, 13, 14, 11, 15, 14, 13, 14, 11, 14, 11, 11, 11, 11, 16, 11, 13, 13, 16, 14, 11, 13, 12, 11},  
			{16, 14, 16, 11, 16, 16, 11, 13, 16, 13, 13, 11, 13, 14, 13, 15, 11, 12, 16, 16, 11, 15, 14, 12},  
			{16, 16, 16, 13, 13, 13, 12, 15, 14, 16, 14, 16, 15, 12, 16, 11, 13, 15, 16, 13, 13, 12, 11, 14},  
			{14, 13, 12, 15, 11, 12, 16, 12, 11, 11, 11, 12, 13, 15, 11, 16, 12, 13, 15, 15, 11, 16, 12, 13},  
			{12, 16, 12, 12, 14, 13, 12, 11, 11, 16, 14, 16, 16, 11, 16, 12, 14, 12, 15, 15, 15, 11, 12, 12},  
			{15, 14, 12, 16, 16, 14, 12, 15, 16, 12, 11, 11, 11, 12, 12, 14, 16, 14, 14, 14, 16, 14, 15, 14},  
			{13, 15, 14, 13, 16, 16, 14, 12, 16, 14, 11, 15, 11, 12, 12, 14, 14, 16, 16, 13, 14, 13, 14, 15},  
			{14, 14, 12, 13, 16, 16, 15, 12, 15, 16, 12, 11, 16, 13, 13, 16, 12, 14, 12, 11, 16, 16, 13, 13},  
			{12, 11, 15, 13, 12, 12, 16, 13, 11, 13, 14, 12, 13, 11, 11, 12, 16, 11, 14, 11, 13, 14, 11, 11},  
			{16, 11, 12, 13, 11, 12, 15, 16, 14, 13, 14, 15, 13, 15, 13, 15, 15, 12, 16, 14, 11, 15, 11, 12},  
			{16, 11, 11, 15, 12, 14, 15, 14, 15, 11, 13, 16, 15, 11, 12, 15, 13, 12, 14, 11, 15, 11, 14, 11}  
	};
	
	public static final int[][] testCaseCost2 = {
			{20, 20, 16, 21, 20, 20, 20, 16, 17, 16, 18, 19, 12, 16, 14, 16, 12, 14, 13, 15, 15, 15, 13, 15},  
			{16, 20, 16, 17, 21, 17, 21, 20, 16, 18, 18, 16, 15, 13, 14, 14, 16, 15, 13, 14, 13, 11, 13, 15},  
			{17, 19, 21, 21, 18, 20, 16, 21, 21, 19, 19, 16, 15, 16, 16, 12, 13, 11, 11, 11, 12, 13, 11, 15},  
			{18, 21, 20, 16, 20, 18, 20, 20, 20, 16, 19, 18, 13, 14, 13, 15, 16, 13, 16, 15, 15, 11, 13, 11},  
			{19, 21, 18, 17, 20, 17, 20, 16, 21, 17, 19, 18, 11, 14, 11, 12, 16, 15, 12, 11, 11, 11, 11, 16},  
			{18, 16, 21, 19, 16, 19, 19, 20, 18, 17, 19, 19, 15, 13, 15, 14, 14, 16, 14, 13, 15, 15, 16, 16},  
			{16, 16, 19, 16, 17, 16, 21, 19, 21, 17, 19, 21, 13, 15, 12, 13, 13, 11, 12, 13, 12, 15, 16, 11},  
			{20, 20, 16, 17, 18, 18, 21, 20, 18, 19, 16, 19, 16, 16, 16, 14, 12, 16, 13, 11, 16, 16, 12, 11},  
			{18, 20, 18, 20, 19, 17, 21, 20, 16, 17, 20, 21, 15, 14, 14, 15, 11, 15, 11, 14, 15, 11, 11, 14},  
			{16, 16, 17, 19, 17, 17, 21, 16, 21, 17, 19, 17, 14, 12, 13, 13, 15, 12, 16, 12, 12, 16, 13, 13},  
			{15, 14, 13, 12, 13, 11, 15, 14, 11, 16, 12, 14, 12, 16, 13, 14, 15, 13, 14, 12, 14, 14, 15, 12},  
			{16, 14, 16, 13, 13, 12, 13, 16, 14, 15, 14, 13, 12, 15, 11, 14, 12, 12, 15, 11, 15, 16, 16, 15},  
			{14, 13, 11, 12, 15, 15, 16, 13, 14, 13, 13, 13, 16, 16, 16, 12, 12, 15, 11, 11, 15, 15, 16, 13},  
			{14, 15, 13, 11, 12, 15, 11, 12, 13, 12, 14, 13, 15, 15, 16, 14, 12, 14, 14, 13, 14, 15, 14, 11},  
			{14, 12, 13, 11, 13, 12, 15, 16, 14, 11, 12, 11, 15, 11, 14, 11, 15, 16, 15, 11, 11, 14, 11, 16},  
			{11, 14, 16, 16, 12, 14, 14, 14, 11, 11, 15, 15, 14, 16, 13, 13, 16, 12, 13, 15, 15, 15, 14, 11},  
			{16, 12, 14, 11, 16, 11, 12, 12, 16, 12, 16, 11, 12, 12, 14, 13, 16, 16, 11, 15, 15, 16, 13, 11},  
			{14, 14, 13, 16, 16, 16, 16, 14, 11, 11, 14, 14, 11, 13, 13, 15, 14, 12, 14, 13, 11, 11, 12, 16},  
			{13, 16, 16, 16, 16, 16, 11, 14, 12, 14, 16, 13, 11, 16, 13, 15, 12, 16, 12, 14, 15, 16, 13, 12},  
			{15, 12, 14, 12, 14, 13, 14, 13, 13, 15, 12, 15, 14, 13, 12, 11, 12, 11, 13, 12, 14, 14, 12, 15}  
	};
	
	
	public static final int[][] testCaseCost3 = {
			{10, 8, 6, 11, 11, 7, 8, 8, 8, 8, 8, 10, 14, 11, 16, 16, 13, 11, 16, 16, 16, 16, 16, 11},  
			{9, 9, 7, 6, 6, 7, 7, 11, 9, 11, 6, 6, 16, 14, 11, 12, 14, 15, 11, 12, 13, 12, 14, 12},  
			{7, 11, 9, 9, 7, 10, 6, 10, 10, 9, 9, 10, 12, 12, 13, 13, 15, 11, 14, 12, 13, 11, 14, 16},  
			{9, 7, 11, 8, 9, 8, 8, 11, 10, 6, 7, 8, 14, 14, 15, 11, 12, 13, 12, 11, 14, 11, 11, 11},  
			{11, 9, 6, 11, 11, 9, 9, 9, 8, 9, 8, 11, 13, 13, 15, 16, 16, 11, 15, 12, 14, 14, 12, 13},  
			{7, 6, 10, 6, 10, 6, 6, 10, 9, 9, 8, 8, 14, 16, 15, 15, 13, 13, 16, 15, 14, 15, 15, 11},  
			{8, 6, 7, 9, 8, 10, 9, 7, 11, 6, 11, 9, 13, 13, 14, 13, 11, 14, 15, 14, 12, 15, 11, 16},  
			{8, 11, 11, 6, 11, 6, 9, 8, 8, 9, 7, 7, 14, 11, 12, 14, 13, 16, 13, 14, 15, 15, 13, 15},  
			{10, 7, 6, 11, 9, 7, 9, 7, 7, 9, 11, 6, 11, 12, 15, 15, 13, 16, 12, 11, 16, 11, 14, 11},  
			{6, 10, 10, 11, 9, 7, 10, 10, 7, 10, 11, 7, 13, 14, 16, 15, 15, 15, 16, 16, 14, 15, 14, 16},  
			{15, 12, 13, 15, 13, 16, 16, 13, 15, 12, 15, 15, 14, 16, 14, 11, 13, 14, 16, 13, 12, 12, 14, 14},  
			{11, 13, 16, 12, 16, 13, 16, 15, 14, 13, 16, 11, 16, 12, 13, 14, 13, 13, 12, 13, 14, 14, 13, 15},  
			{11, 11, 12, 14, 15, 11, 16, 14, 13, 12, 13, 15, 12, 16, 12, 14, 14, 14, 13, 12, 14, 14, 16, 14},  
			{14, 12, 15, 12, 15, 16, 11, 14, 13, 11, 15, 14, 12, 16, 16, 12, 16, 12, 14, 12, 16, 11, 15, 11},  
			{16, 16, 15, 15, 11, 12, 11, 16, 16, 14, 12, 12, 13, 14, 14, 15, 11, 12, 16, 13, 13, 14, 15, 13},  
			{12, 15, 15, 13, 15, 15, 12, 14, 12, 16, 11, 12, 11, 13, 14, 12, 13, 15, 14, 13, 12, 12, 13, 11},  
			{11, 11, 14, 13, 12, 14, 11, 12, 15, 13, 12, 13, 14, 11, 15, 14, 12, 16, 13, 12, 11, 13, 14, 12},  
			{12, 16, 13, 12, 15, 13, 11, 15, 13, 16, 14, 11, 13, 16, 11, 13, 13, 13, 15, 14, 12, 12, 15, 15},  
			{15, 15, 16, 13, 11, 12, 11, 12, 11, 15, 13, 14, 14, 11, 15, 13, 16, 12, 15, 15, 14, 11, 15, 12},  
			{12, 16, 11, 14, 12, 14, 16, 12, 13, 11, 13, 11, 12, 16, 16, 14, 15, 12, 12, 11, 12, 16, 16, 13}  
	};
	
	public static final int[][] testCaseCost4 = {
			{15, 13, 10, 15, 16, 12, 16, 13, 17, 11, 12, 21, 21, 6, 10, 18, 12, 20, 13, 6, 17, 21, 12, 17},  
			{11, 13, 12, 7, 17, 16, 18, 12, 16, 9, 11, 15, 15, 13, 16, 12, 17, 9, 8, 15, 20, 15, 13, 11},  
			{14, 19, 10, 17, 16, 7, 9, 7, 6, 7, 19, 9, 15, 13, 19, 17, 20, 8, 14, 21, 14, 12, 20, 21},  
			{16, 19, 21, 8, 6, 19, 21, 19, 13, 10, 12, 7, 6, 20, 16, 17, 17, 14, 8, 10, 9, 19, 12, 14},  
			{8, 16, 18, 15, 13, 13, 12, 6, 8, 17, 21, 19, 8, 18, 12, 10, 11, 10, 15, 9, 9, 21, 15, 18},  
			{14, 8, 10, 13, 15, 18, 21, 12, 20, 7, 18, 7, 11, 19, 9, 10, 17, 9, 17, 20, 10, 16, 16, 7},  
			{16, 9, 13, 13, 13, 17, 7, 20, 14, 13, 20, 15, 7, 15, 16, 18, 7, 17, 14, 17, 9, 8, 7, 17},  
			{21, 16, 14, 15, 17, 18, 14, 11, 12, 11, 9, 19, 20, 14, 10, 13, 7, 16, 10, 14, 13, 8, 10, 6},  
			{21, 19, 19, 14, 21, 17, 18, 14, 18, 18, 16, 10, 15, 12, 16, 20, 7, 10, 19, 7, 14, 20, 13, 17},  
			{21, 16, 20, 8, 8, 16, 14, 9, 14, 16, 21, 9, 9, 14, 9, 10, 7, 20, 16, 21, 16, 20, 14, 15},  
			{17, 15, 16, 18, 20, 8, 11, 17, 10, 15, 18, 21, 21, 20, 10, 11, 12, 19, 16, 12, 15, 18, 18, 10},  
			{20, 19, 15, 15, 13, 10, 15, 18, 11, 6, 21, 17, 14, 15, 13, 16, 21, 12, 16, 8, 13, 15, 16, 6},  
			{7, 15, 13, 6, 14, 8, 19, 7, 19, 10, 19, 8, 7, 6, 16, 19, 18, 15, 19, 7, 12, 11, 13, 9},  
			{16, 13, 21, 16, 16, 14, 14, 21, 20, 21, 12, 16, 21, 17, 15, 18, 20, 21, 18, 16, 8, 18, 21, 21},  
			{8, 16, 17, 17, 10, 20, 14, 9, 17, 20, 7, 12, 9, 10, 15, 18, 15, 14, 15, 12, 13, 20, 8, 14},  
			{12, 9, 10, 16, 19, 8, 13, 13, 14, 18, 11, 12, 17, 17, 15, 15, 20, 11, 12, 13, 8, 15, 17, 13},  
			{18, 11, 21, 14, 10, 15, 16, 16, 13, 19, 19, 12, 15, 11, 8, 14, 11, 20, 12, 10, 21, 11, 11, 10},  
			{10, 11, 8, 15, 7, 21, 7, 18, 20, 7, 15, 14, 17, 17, 13, 18, 17, 10, 21, 16, 7, 13, 11, 9},  
			{20, 18, 15, 8, 19, 9, 8, 17, 21, 7, 20, 16, 8, 18, 14, 20, 16, 9, 18, 14, 16, 8, 18, 18},  
			{16, 13, 10, 8, 6, 11, 15, 19, 13, 9, 18, 7, 15, 14, 14, 10, 6, 6, 11, 9, 12, 15, 7, 17}  
	};
	
	
	//new test case 1 (all random cost) 
	public static final int[][] testCaseNew1Start0 = {
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
	
	public static final int[][] testCaseNew1End0 = {
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
	
	/*
	public static final int[][] testCaseCostNew3; //qc2 higher cost
	public static final int[][] testCaseCostNew4; 
	*/ 
	
	
	
}
