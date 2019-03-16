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
	
	private static ArrayList<TestResult> results = new ArrayList<>(); 
	
	private static boolean pause = false; 
	

	
	//public static DelayComp bothTimers = new DelayComp(); 
	
	//new one with dispatcher*************************************************************************
	@SuppressWarnings("deprecation")
	public static void main(String[] args){
		//singleTestSimulation(); 
		
		multipleSimulation(); 
		

	}
	
	public static void singleTestSimulation(){
		resetTimers();
		
		joblist = new JobList(); 
		
		viewSimulator();

		
		CalcTime totalTimer = new CalcTime(); 
		Thread t = new Thread(totalTimer); 
		t.start();

		DispatcherTest2 dispatcher = new DispatcherTest2(joblist); 	// 1- single simulation 
		
		
		initTasks();

		
		joblist.setLayout(null);

		
		//updating the timer test
		//tenative, stops after all threads are created, not when all ends 
		
		int wait = 0; 
		while(true){
			System.out.println("jobs completed: " + Constants.jobsCompleted);
			if(Constants.jobsCompleted >= Constants.TOTAL_SIZE){
				totalTimer.shutdown(true); 
				break;
			}
			
			if(wait > 50){
				totalTimer.shutdown(true);
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
		 
	}
	
	public static void multipleSimulation(){
		//do 10 times maybe?
		int k=0;
		
		resetTimers();	//this, no need if i decide to do dispatcher.reset(); 
		joblist = new JobList(); 
		
		_frame = new JFrame();
		_frame.setSize(new Dimension(800, 700));	//window size 400(width) by 400(height)  
		_frame.setResizable(true);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();	//toolkit impt!! 
		
		//set location of the window
		int xPos = dim.width/2 - _frame.getSize().width/2;
		int yPos = dim.height/2 - _frame.getSize().height/2;
		_frame.setLocation(xPos, yPos);
		
		
		//components in the window 
		_tiles = new JPanel(new CardLayout());
		_buttons = new JPanel();

		Container contentPane = _frame.getContentPane();
		
		//contentPane.setLayout(new BorderLayout());
		
		_frame.setLocationRelativeTo(null);
		contentPane.add(_tiles, BorderLayout.CENTER);
		
		//test the one with splitjoblist
		contentPane.add(_buttons, BorderLayout.PAGE_END); 
		((JComponent) contentPane).setBorder(new EmptyBorder(10, 10, 10, 10));
		
		//_tiles.add(_label);
		
		//initialize map layout
		initTasks();
		
		//initialize button layout
		//initButtons();
		
		//initialize timers
		initTimers(); 
		
		//view full display of the application
		_frame.setVisible(true);
		_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		CalcTime totalTimer = new CalcTime(); 
		Thread t = new Thread(totalTimer); 
		t.start();	//need to stop this for a while after every iteration 
		
		DispatcherTest2 dispatcher = new DispatcherTest2(joblist); //2 - multiple simulation  
		// lets try multiple single simulation 
		
		initTasks();	
		
		joblist.setLayout(null);
		
	
		totalTimer.shutdown(true);
		System.out.println("this test case discarded");

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		if(!Constants.BUGDETECTED){
			System.out.println("=========================All completed========================");
			System.out.println("total time taken: " + Constants.TOTALTIME);
			System.out.println("total delay: " + Constants.TOTALDELAY);
			System.out.println("average delay per QC: " + (float)Constants.TOTALDELAY/4.0);
			System.out.println("total agv travel time: " + Constants.TRAVELTIME);
			System.out.println("average agv travel time: " + (float)Constants.TRAVELTIME/4.0); 
			results.add(new TestResult(Constants.TOTALTIME, Constants.TOTALDELAY, Constants.TRAVELTIME)); 
		}else{
			k--; 
		}
		
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		
		
		
		while(k<9){
			Constants.BUGDETECTED = false; 
			
			joblist.clearJobList(); //<- this will start from where it was left at 
			joblist = new JobList();  
			
			totalTimer = new CalcTime(); 
			initTimers(); 
			
			Thread t1 = new Thread(totalTimer);	//need to stop this for a while after every iteration 
			t1.start();

			dispatcher.resetDispatcher(joblist);

			dispatcher.initDispatcher();
			joblist.setLayout(null);
			
			initTasks();
			
			joblist.setLayout(null);
			
			dispatcher.startDispatching();


			totalTimer.shutdown(true);

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("after timer stopped");
			
			if(Constants.BUGDETECTED){
				System.out.println("this test case discarded");

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("k = " + k);
				continue; //don't increment k
			}
			
			System.out.println("=========================All completed========================");
			System.out.println("total time taken: " + Constants.TOTALTIME);
			System.out.println("total delay: " + Constants.TOTALDELAY);
			System.out.println("average delay per QC: " + (float)Constants.TOTALDELAY/4.0);
			System.out.println("total agv travel time: " + Constants.TRAVELTIME);
			System.out.println("average agv travel time: " + (float)Constants.TRAVELTIME/4.0); 
			
			results.add(new TestResult(Constants.TOTALTIME, Constants.TOTALDELAY, Constants.TRAVELTIME)); 
			
			k++; 
			System.out.println("k = " + k);
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		printResultValues(); 
		
	}
	
	
	public static void printResultValues(){
		for(int i=0; i<results.size(); i++){
			System.out.println("Total time: " + results.get(i).getTotalTime() + ", Total delay: " + results.get(i).getTotalDelay() + 
					", Total travel: " + results.get(i).getTotalTravel() + ", Average delay: " + results.get(i).getAvgDelay() +   
					", Average travel: " + results.get(i).getAgvTravel());
		}
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
		_frame.setSize(new Dimension(800, 700));	//window size 400(width) by 400(height)  
		_frame.setResizable(false);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();	//toolkit impt!! 
		
		//set location of the window
		int xPos = dim.width/2 - _frame.getSize().width/2;
		int yPos = dim.height/2 - _frame.getSize().height/2;
		_frame.setLocation(xPos, yPos);
		
		
		//components in the window 
		_tiles = new JPanel(new CardLayout());
		_buttons = new JPanel();

		Container contentPane = _frame.getContentPane();
		
		//contentPane.setLayout(new BorderLayout());
		
		_frame.setLocationRelativeTo(null);
		contentPane.add(_tiles, BorderLayout.CENTER);
		
		//test the one with splitjoblist
		contentPane.add(_buttons, BorderLayout.PAGE_END); 
		((JComponent) contentPane).setBorder(new EmptyBorder(10, 10, 10, 10));
		
		//_tiles.add(_label);
		
		//initialize map layout
		initTasks();
		
		//initialize button layout
		//initButtons();
		
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
		lblTotalTime.setBounds(162, 10, 84, 15);
		joblist.add(lblTotalTime);
		
		joblist.add(Constants.TIMERS.getTotalCounter());
		
		JLabel lblDelayTime = new JLabel("Delay Time: ");
		lblDelayTime.setVerticalAlignment(SwingConstants.BOTTOM);
		lblDelayTime.setFont(new Font("Arial", Font.PLAIN, 12));
		lblDelayTime.setBounds(290, 10, 75, 15);
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
	
	
	///DO THIS???
	private static void initTimersAgain(){
		//joblist.add(lblTotalTime);
		joblist.add(Constants.TIMERS.getTotalCounter());
		
		//joblist.add(lblDelayTime);
		
		joblist.add(Constants.TIMERS.getDelayCounter());
		
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
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		//private Thread t; 
		//private String timerName; 
		private volatile boolean shutdown = false; 
		
		@Override
		public void run() {		
			while(!shutdown){
				try {
					Constants.TOTALTIME++; 
					Thread.sleep(Constants.SLEEP);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
		}
		
		public void shutdown(boolean b){
			shutdown = b; 
		}
		
	}
	
	//lock for agv?
	static class Lock{
		
	} 
}

