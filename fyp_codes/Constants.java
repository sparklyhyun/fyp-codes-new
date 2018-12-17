package fyp_codes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

//total number of qc = 4


public class Constants {
	public static final int MAX_X = 4; 	//bay size (for 1) ?
	public static final int MAX_Y = 10;	//bay height?
	public static final int BAYSIZE = MAX_X * MAX_Y; 
	
	public static final int HALF_Y = MAX_Y/2;	//half is unloading, half is loading 
	
	public static final int QC_X = 12; // 3 bays here 
	
	public static final int TOTAL_Y = 20; // 2 qc, top and bottom 
	public static final int TOTAL_X = 24; // 2 qc, left and right
	public static final int TOTAL_SIZE = TOTAL_Y * TOTAL_X; 
	
	public static final int NUM_QC_X = 2;
	public static final int NUM_QC_Y = 2; 
	
	public static final int AGV = 8; //number of AGV
	
	public static final int CELL_SIZE = 20; 
	public static final Color COLOR_LOADING = Color.BLUE;
	public static final Color COLOR_LOADING_ASSIGNED = Color.CYAN;	//when assigned a vehicle
	public static final Color COLOR_UNLOADING = Color.GREEN;
	public static final Color COLOR_UNLOADING_ASSIGNED = Color.YELLOW;	//when assgined a vehicle
	public static final Color COLOR_COMPLETE = Color.WHITE; 
	public static final Color COLOR_WAITING = Color.ORANGE;
	
	public static final int SLEEP = 500; 
	
	public static int TOTALDELAY = 0; 
	public static int TOTALTIME = 0;
	
	public static boolean allComplete = false; 
	
	//can i put timer here? would it work? 
	/*
	public static JLabel lblDelaytime_counter = new JLabel("delay_time_1");
	public static JLabel lblTotaltime_counter = new JLabel("total_time_1");
	*/ 
	//does this work like this? 
	static class DelayComp{
		public static JLabel lblDelaytime_counter = new JLabel("delayTime Counter");
		public static JLabel lblTotaltime_counter = new JLabel("totalTime Counter");
		
		
		
		public DelayComp(){
			lblTotaltime_counter.setVerticalAlignment(SwingConstants.BOTTOM);
			lblTotaltime_counter.setFont(new Font("Arial", Font.PLAIN, 12));
			lblTotaltime_counter.setBounds(363, 370, 67, 15);
			
			lblDelaytime_counter.setVerticalAlignment(SwingConstants.BOTTOM);
			lblDelaytime_counter.setFont(new Font("Arial", Font.PLAIN, 12));
			lblDelaytime_counter.setBounds(525, 370, 69, 15);
			
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
			lblDelaytime_counter.setText(Integer.toString(Constants.TOTALDELAY));
		}
		
		public void updateTotalTimer(){
			lblTotaltime_counter.setText(Integer.toString(Constants.TOTALTIME));
		}
	}
	
	public static DelayComp TIMERS = new DelayComp(); 
}
