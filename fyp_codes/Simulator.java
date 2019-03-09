package fyp_codes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import fyp_codes.Constants.DelayComp;

import java.awt.*;
import java.awt.event.MouseAdapter;	//see what this does
import java.awt.event.MouseEvent;	//see what this does
import java.awt.event.ActionEvent;	//testing timer implenmentation
import java.awt.event.ActionListener;	//testing timer implementation 

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;	
import java.util.HashMap; 

public class Simulator {
	private static JFrame _frame = null; //display job list
	private static JPanel _tiles = null; //display individual task
	private static JPanel _buttons = null;	//display buttons <-use later
	//private static JButton button; 
	
	private static JLabel _label = new JLabel("Simulator"); //set title here 
	
	//test out the timer here 
	private static JLabel lblDelaytime_counter = new JLabel("delay_time");	
	private static JLabel lblTotaltime_counter = new JLabel("total_time");
	
	private static JobList joblist; 
	private static JobList joblist2; //second joblist for testing!! 
	private static ArrayList<Agv> agvList = new ArrayList<>();
	
	private static int totalTime = 0;
	private static int totalDelay = 0;
	
	private static ArrayList<SplitJobList> splitJobListArr = new ArrayList<>();
	
	//test the one with split job list
	private static JPanel _splitTile1 = null;
	private static JPanel _splitTile2 = null;
	private static JPanel _splitTile3 = null;
	private static JPanel _splitTile4 = null;
	
	private static ArrayList<Greedy> q_greedy = new ArrayList<>(); 
	
	private static boolean pause = false; 
	
	//for shared pool of agvs 
	static Semaphore sem; 

	
	//public static DelayComp bothTimers = new DelayComp(); 
	
	//new one with dispatcher*************************************************************************
	@SuppressWarnings("deprecation")
	public static void main(String[] args){
		resetTimers();
		
		joblist = new JobList(); 
		//joblist.printStartEndPt();
		
		//seeJobList(joblist); 
		
		viewSimulator();
		
		int numQcY = Constants.TOTAL_X / Constants.QC_X; 
		int numQcX = Constants.TOTAL_Y / Constants.MAX_Y; 
		String qcName; 
		
		CalcTime totalTimer = new CalcTime("totalTimer"); 
		totalTimer.start();
		
		//Dispatcher dispatcher = new Dispatcher(joblist); 
		//DispatcherTest dispatcher = new DispatcherTest(joblist); 
		
		DispatcherTest2 dispatcher = new DispatcherTest2(joblist); 
		
		//need dispatcher to run 
		
		initTasks();

		
		joblist.setLayout(null);

		
		//updating the timer test
		//tenative, stops after all threads are created, not when all ends 
		
		int wait = 0; 
		while(true){
			System.out.println("jobs completed: " + Constants.jobsCompleted);
			if(Constants.jobsCompleted >= Constants.TOTAL_SIZE){
				totalTimer.t.stop();
				break;
			}
			
			if(wait > 30){
				totalTimer.t.stop();
				break; 
			}
			
			try {
				Thread.sleep(Constants.SLEEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			wait++; 
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		System.out.println("=========================All completed========================");
		System.out.println("total time taken: " + Constants.TOTALTIME);
		System.out.println("total delay: " + Constants.TOTALDELAY);
		System.out.println("average delay per QC: " + (float)Constants.TOTALDELAY/4.0);
		System.out.println("total agv travel time: " + Constants.TRAVELTIME);
		System.out.println("average agv travel time: " + (float)Constants.TRAVELTIME/4.0); 
		//dispatcher.showCreatedOrder();	//this was correct
		 
		
		/*	
		if(Constants.allComplete >= Constants.NUM_QC){
			System.out.println("---------------------------greedy complete=========");
			System.out.println("total delay: " + Constants.TOTALDELAY);
			System.out.println("total time: " + Constants.TOTALTIME);
			
		}*/ 
		

	}
	
/*
	public static void main(String[] args){
		
		resetTimers();
		
		joblist = new JobList(); 
		seeJobList(joblist); 
		
		//shared pool of agv now *************************************** new version, this is not needed
		
		for(int i=0; i<Constants.AGV; i++){
			Agv agv = new Agv(i); 
			agvList.add(agv); 
		}
		System.out.println("agv done");
		
		//creating semaphore for agvs ********************************* new version, this is not needed. 
		sem = new Semaphore(Constants.AGV); 
		
		viewSimulator();
		
		int numQcY = Constants.TOTAL_X / Constants.QC_X; 
		int numQcX = Constants.TOTAL_Y / Constants.MAX_Y; 
		String qcName; 
		
		for(int i=0; i<numQcY; i++){
			for(int j=0; j<numQcX; j++){
				
				
				SplitJobList splitJobList = new SplitJobList(i, j, joblist); 
				seeSplitJobList(splitJobList); 
				splitJobListArr.add(splitJobList); 

				qcName = "qc" + i + j; 
				

				
				
				//put greedy in queue ***************************************** new version, this part not needed
				
				Greedy g = new Greedy(joblist, splitJobList, agvList, qcName, sem); 
				q_greedy.add(g); 
				
				System.out.println("splitting job is done\n");
				
				g.start(); 
				
				
				
			}
		}
		
		
		initTasks();

		
		joblist.setLayout(null);

		
		//updating the timer test
		CalcTime totalTimer = new CalcTime("totalTimer"); 
		totalTimer.start();
		
				
		if(Constants.allComplete >= Constants.NUM_QC){
			System.out.println("---------------------------greedy complete=========");
			System.out.println("total delay: " + Constants.TOTALDELAY);
			System.out.println("total time: " + Constants.TOTALTIME);
			
		}
		

	}
	*/
	/*
	public static void startJobs(){
		int numQcY = Constants.TOTAL_X / Constants.QC_X; 
		int numQcX = Constants.TOTAL_Y / Constants.MAX_Y; 
		String qcName; 
		
		for(int i=0; i<numQcY; i++){
			for(int j=0; j<numQcX; j++){
				SplitJobList splitJobList = new SplitJobList(i, j, joblist); 
				seeSplitJobList(splitJobList); 
				splitJobListArr.add(splitJobList); 
				
				qcName = "qc" + i + j; 

				Greedy g = new Greedy(joblist, splitJobList, agvList,qcName, sem); 
				
				//put greedy in queue ************************************* new version, this part needs to be changed
				
				q_greedy.add(g); 
				
				System.out.println("splitting job is done\n");
				
				g.start(); 
				
				
			}
		}
	}
	*/
	
	public static void resetTimers(){
		Constants.TOTALDELAY = 0;
		Constants.TOTALTIME = 0;
	}
	
	public static void seeJobList(JobList jl){
		System.out.println("see entire job list");
		for(int i=0; i<Constants.TOTAL_Y; i++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				System.out.print(jl.getJob(i, j).getTotalCost()+ ", ");
			}
			System.out.println(" ");
		}
		
		
		/*
		int mulBays = Constants.QC_X / Constants.MAX_X;
		for(int k=0; k<mulBays; k++){
			for(int i=0; i<Constants.MAX_Y; i++){
				for(int j=k*Constants.MAX_X; j<(k+1)*Constants.MAX_X; j++){
					System.out.print(jl.getJob(i, j).getTotalCost()+ " ");
				}
				System.out.println(" ");
			}
			
		}*/
	
		/*
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				System.out.print(joblist.getJob(i, j).getTotalCost()+ " ");
			}
			System.out.println(" ");
		}*/
		
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
	
	//don't add buttons yet 
	private static void viewSimulator(){
		_frame = new JFrame();
		_frame.setSize(new Dimension(600, 530));	//window size 400(width) by 400(height)  
		_frame.setResizable(true);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();	//toolkit impt!! 
		
		//set location of the window
		int xPos = dim.width/2 - _frame.getSize().width/2;
		int yPos = dim.height/2 - _frame.getSize().height/2;
		_frame.setLocation(xPos, yPos);
		
		
		//components in the window 
		_tiles = new JPanel(new CardLayout());
		_buttons = new JPanel();
		 
		//test the one with split job list
		/*
		_splitTile1 = new JPanel(new CardLayout());
		_splitTile2 = new JPanel(new CardLayout());
		_splitTile3 = new JPanel(new CardLayout());
		_splitTile4 = new JPanel(new CardLayout());
		*/
		
		Container contentPane = _frame.getContentPane();
		
		//contentPane.setLayout(new BorderLayout());
		
		_frame.setLocationRelativeTo(null);
		contentPane.add(_tiles, BorderLayout.CENTER);
		
		//test the one with splitjoblist
		/*
		contentPane.add(_splitTile1, BorderLayout.CENTER);
		contentPane.add(_splitTile2, BorderLayout.CENTER);
		contentPane.add(_splitTile3, BorderLayout.CENTER);
		contentPane.add(_splitTile4, BorderLayout.CENTER);
		*/
		contentPane.add(_buttons, BorderLayout.PAGE_END); 
		((JComponent) contentPane).setBorder(new EmptyBorder(10, 10, 10, 10));
		
		//_tiles.add(_label);
		
		//initialize map layout
		initTasks();
		
		//initialize button layout
		initButtons();
		
		//initialize timers
		initTimers(); 
		
		//view full display of the application
		_frame.setVisible(true);
		_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	private static void initTasks(){
		
		_tiles.add(joblist, "TASK_LIST");
		
		CardLayout c = ((CardLayout)_tiles.getLayout());
		
		c.show(_tiles, "TASK_LIST");
		
		
		//can I do this?
		/*
		_splitTile1.add(splitJobListArr.get(0), "SPLIT_TASK1");
		_splitTile2.add(splitJobListArr.get(0), "SPLIT_TASK1");
		_splitTile3.add(splitJobListArr.get(0), "SPLIT_TASK1");
		_splitTile4.add(splitJobListArr.get(0), "SPLIT_TASK1");
		*/
		
		//not sure if correct or not 
		/*
		CardLayout card = new CardLayout();
		JPanel cards = new JPanel(card);
		cards.add(joblist, "TASK_LIST");
		card.show(cards, "TASK_LIST");
		*/
	}
	
	private static void initTimers(){
		
		
		
		JLabel lblTotalTime = new JLabel("Total Time: ");
		lblTotalTime.setVerticalAlignment(SwingConstants.BOTTOM);
		lblTotalTime.setFont(new Font("Arial", Font.PLAIN, 12));
		lblTotalTime.setBounds(280, 370, 84, 15);
		joblist.add(lblTotalTime);
		
		joblist.add(Constants.TIMERS.getTotalCounter());
		
		JLabel lblDelayTime = new JLabel("Delay Time: ");
		lblDelayTime.setVerticalAlignment(SwingConstants.BOTTOM);
		lblDelayTime.setFont(new Font("Arial", Font.PLAIN, 12));
		lblDelayTime.setBounds(460, 370, 75, 15);
		joblist.add(lblDelayTime);
		
		joblist.add(Constants.TIMERS.getDelayCounter());
		
		//make sure there is space
		/*
		JLabel lblDelaytime_counter_blank = new JLabel("\t\t\t\t");
		JLabel lblTotaltime_counter_blank = new JLabel("\t\t\t\t");
		
		lblTotaltime_counter_blank.setVerticalAlignment(SwingConstants.BOTTOM);
		lblTotaltime_counter_blank.setFont(new Font("Arial", Font.PLAIN, 12));
		lblTotaltime_counter_blank.setBounds(360, 370, 70, 15);
		
		lblDelaytime_counter_blank.setVerticalAlignment(SwingConstants.BOTTOM);
		lblDelaytime_counter_blank.setFont(new Font("Arial", Font.PLAIN, 12));
		lblDelaytime_counter_blank.setBounds(525, 370, 70, 15);
		*/
		
		
		//labels added to constant class
		//JLabel lblTotaltime_counter = new JLabel("total_time");
		/*
		lblTotaltime_counter.setVerticalAlignment(SwingConstants.BOTTOM);
		lblTotaltime_counter.setFont(new Font("Arial", Font.PLAIN, 12));
		lblTotaltime_counter.setBounds(363, 370, 67, 15);
		joblist.add(lblTotaltime_counter);
		*/
		
		//labels added to constant class
		//JLabel lblDelaytime_counter = new JLabel("delay_time");
		/*
		lblDelaytime_counter.setVerticalAlignment(SwingConstants.BOTTOM);
		lblDelaytime_counter.setFont(new Font("Arial", Font.PLAIN, 12));
		lblDelaytime_counter.setBounds(525, 370, 69, 15);
		joblist.add(lblDelaytime_counter);
		*/
		
		//timer (total time)
		/*
		Timer total_timer = new Timer(100, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				lblTotaltime_counter.setText(Integer.toString(totalTime));
			    totalTime++;
			}
			
		});
		
		//timer (delay time)
		Timer delay_timer = new Timer(100, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				lblDelaytime_counter.setText(Integer.toString(totalDelay));
				totalDelay++; 
			}
			
			
		});
		*/
	}
	
	private static void initButtons(){
		_buttons.setLayout(new GridLayout());
		addButtons();
	}
	
	private static void addButtons(){
		//start button 
		JButton btn_start = new JButton("Start");
		btn_start.setFont(new Font("Calbri", Font.BOLD, 12));
		btn_start.setFocusPainted(false);
		
		//add event 
		btn_start.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				//start algorithm
				//startJobs(); 
				Constants.TIMERS.updateTotalTimer();
			}
		});
	
		_buttons.add(btn_start);
		
		//pause button 
		JButton btn_pause = new JButton("Pause");
		btn_pause.setFont(new Font("Calbri", Font.BOLD, 12));
		btn_pause.setFocusPainted(false);
		
		//add event 
		btn_pause.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				System.out.println("-------------------main paused------------------");
				//pause algorithm
				Greedy greedy; 
				pause = true; 
				for(int i=0; i<q_greedy.size(); i++){
					greedy = q_greedy.get(i);
					greedy.pauseGreedy();;
				}
				
				while(pause == true){
					//nothing happens here 
				}
			}
		});
				
		_buttons.add(btn_pause); 
		
		//reset button 
		JButton btn_reset = new JButton("Reset");
		btn_reset.setFont(new Font("Calbri", Font.BOLD, 12));
		btn_reset.setFocusPainted(false);
		
		//add event 
		btn_reset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				//reset algorithm
				joblist.reset();
				resetTimers(); 
				joblist.repaint();
			}
		});
		
		_buttons.add(btn_reset); 
	}
	
	
	//for calculatng total time here omg 
	static class CalcTime implements Runnable {
		private Thread t; 
		private String timerName; 
		
		public CalcTime(String name){
			timerName = name; 
		}
		
		@Override
		public void run() {			
			while(Constants.allComplete < Constants.NUM_QC){
				try {
					Constants.TOTALTIME++; 
					Thread.sleep(Constants.SLEEP);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		public void start(){
			if(t==null){
				t = new Thread(this, timerName);
				t.start();
				
			}
		
		}
		
	}
	
	//lock for agv?
	static class Lock{
		
	} 
}

