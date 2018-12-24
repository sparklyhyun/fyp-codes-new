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
import java.util.concurrent.*;	//use TimeUnit 
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
	
	
	//public static DelayComp bothTimers = new DelayComp(); 
	
	public static void main(String[] args){
		
		resetTimers();
		
		joblist = new JobList(); 
		seeJobList(joblist); 
		
		/*
		for(int i=0; i<Constants.AGV; i++){
			Agv agv = new Agv(i); 
			agvList.add(agv); 
		}
		System.out.println("agv done");
		*/
		viewSimulator();
		
		int numQcY = Constants.TOTAL_X / Constants.QC_X; 
		int numQcX = Constants.TOTAL_Y / Constants.MAX_Y; 
		//int numQcX = 1;
		//int numQcY = 1;
		String qcName; 
		
		for(int i=0; i<numQcY; i++){
			for(int j=0; j<numQcX; j++){
				
				/*
				ArrayList<Agv> splitAgvList = new ArrayList<>(); 
				
				for(int k=0; k<Constants.AGV; k++){
					Agv agv = new Agv(k); 
					splitAgvList.add(agv); 
				}
				*/
				
				SplitJobList splitJobList = new SplitJobList(i, j, joblist); 
				seeSplitJobList(splitJobList); 
				splitJobListArr.add(splitJobList); 
				
				//System.out.printf("Lol\n");
				//System.out.println(splitJobList.getJob(0, 0).getX());
				qcName = "qc" + i + j; 
				
				
				
				//Greedy g = new Greedy(joblist, splitJobList, splitAgvList, qcName);
				Greedy g = new Greedy(joblist, splitJobList, qcName); 
				
				System.out.println("splitting job is done\n");
				
				g.start(); 
				
				/*
				for(int k=numQcY*Constants.MAX_Y; k<(numQcY+1)*Constants.MAX_Y; k++){
					System.out.println("k value: " + k);
					for(int l=numQcX*Constants.QC_X; l<(numQcX+1)*Constants.QC_X; l++){
						System.out.println("l value: " + l);
						splitJobList = new JobList(k, l, joblist); 
						Greedy g = new Greedy(splitJobList, agvList);
						g.start(); 
					}
				}*/
				
				
				
				
			}
		}
		
		
		initTasks();
		
		
		
		////////////////////////////
		//Greedy g = new Greedy(joblist, agvList);
		
		joblist.setLayout(null);
		/*
		for(int i=0; i<splitJobListArr.size(); i++){
			splitJobListArr.get(i).setLayout(null);
		}*/
		
		//testing!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		/*
		for(int i=0; i<q_greedy.size(); i++){
			q_greedy.get(i).start();
		}*/
		
		//testing the simulator 
		//g.testSimulator();
		
		//updating the timer test
		CalcTime totalTimer = new CalcTime("totalTimer"); 
		totalTimer.start();
		
		//test simple greedy
		//g.startGreedy1();
		//g.startGreedy2();
		//g.startGreedyUnloading2();
		
		//g.startMergedGreedy();
				
		if(Constants.allComplete){
			System.out.println("---------------------------greedy complete=========");
			System.out.println("total delay: " + Constants.TOTALDELAY);
			System.out.println("total time: " + Constants.TOTALTIME);
			
		}
		

	}
	
	public static void resetTimers(){
		Constants.TOTALDELAY = 0;
		Constants.TOTALTIME = 0;
	}
	
	public static void seeJobList(JobList jl){
		System.out.println("see entire job list");
		for(int i=0; i<Constants.TOTAL_Y; i++){
			for(int j=0; j<Constants.TOTAL_X; j++){
				System.out.print(jl.getJob(i, j).getTotalCost()+ " ");
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
		//greedy search
		JButton btn_greedy_algo = new JButton("Greedy");
		btn_greedy_algo.setFont(new Font("Calbri", Font.BOLD, 12));
		btn_greedy_algo.setFocusPainted(false);
		//add event 
		
		
		_buttons.add(btn_greedy_algo);
		
		//greedy search 2
		JButton btn_greedy_algo_2 = new JButton("Greedy 2");
		btn_greedy_algo_2.setFont(new Font("Calbri", Font.BOLD, 12));
		btn_greedy_algo_2.setFocusPainted(false);
				
		_buttons.add(btn_greedy_algo_2); 
		
		//reset button 
		JButton btn_reset = new JButton("Reset");
		btn_reset.setFont(new Font("Calbri", Font.BOLD, 12));
		btn_reset.setFocusPainted(false);
		
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
			while(Constants.allComplete == false){
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
}

