package fyp_codes;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JButton;
import java.awt.CardLayout;
import javax.swing.border.BevelBorder;

import fyp_codes.Simulator.CalcTime;
import javax.swing.JSplitPane;

public class SimulatorNew {

	private JFrame frame;
	
	private static JobList joblist; 
	
	//testing
	private static JobList testList1;
	private static JobList testList2; 
	private static JobList testList3;
	private static JobList testList4; 
	
	private static ArrayList<Agv> agvList = new ArrayList<>();
	private static ArrayList<SplitJobList> splitJobListArr = new ArrayList<>();
	
	private static int totalTime = 0;
	private static int totalDelay = 0;
	
	public static void main(String[] args) {
		
		resetTimers();
		
		
		joblist = new JobList(); 
		
		//testing
		testList1 = joblist; 
		testList2 = joblist; 
		testList3 = joblist; 
		testList4 = joblist; 
		//////
		
		joblist.setBorder(null);
		seeJobList(joblist); 
		joblist.setLayout(new CardLayout(0, 0));
		
		//testing
		testList1.setBorder(null);
		testList1.setLayout(new CardLayout(0, 0));
		testList2.setBorder(null);
		testList2.setLayout(new CardLayout(0, 0));
		testList3.setBorder(null);
		testList3.setLayout(new CardLayout(0, 0));
		testList4.setBorder(null);
		testList4.setLayout(new CardLayout(0, 0));
		/////////
		
		System.out.println("agv done");
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimulatorNew window = new SimulatorNew();
					window.frame.setVisible(true);
					
					CalcTime totalTimer = new CalcTime("totalTimer"); 
					
					totalTimer.start();
					
					int numQcY = Constants.TOTAL_X / Constants.QC_X; 
					int numQcX = Constants.TOTAL_Y / Constants.MAX_Y; 
					String qcName; 
					
					for(int i=0; i<numQcY; i++){
						for(int j=0; j<numQcX; j++){
							
							SplitJobList splitJobList = new SplitJobList(i, j, joblist); 
							//seeSplitJobList(splitJobList); 
							splitJobListArr.add(splitJobList); 
							
							qcName = "qc" + i + j; 
							
							Greedy g = new Greedy(joblist, splitJobList, qcName); 
							
							System.out.println("splitting job is done\n");
							
							g.start(); 
							
						}
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(Constants.allComplete){
					System.out.println("---------------------------greedy complete=========");
					System.out.println("total delay: " + Constants.TOTALDELAY);
					System.out.println("total time: " + Constants.TOTALTIME);
					
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SimulatorNew() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 600, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//timers initialized here
		JPanel topPanel_timers = new JPanel();
		frame.getContentPane().add(topPanel_timers, BorderLayout.NORTH);
		
		JLabel lbl_total_time = new JLabel("Total Time: ");
		lbl_total_time.setFont(new Font("Arial", Font.BOLD, 12));
		topPanel_timers.add(lbl_total_time);
		
		JLabel lbl_total_time_counter = new JLabel("total_time_counter");
		lbl_total_time_counter.setFont(new Font("Arial", Font.PLAIN, 12));
		topPanel_timers.add(lbl_total_time_counter);
		
		JLabel lbl_total_delay = new JLabel("Total Delay: ");
		lbl_total_delay.setFont(new Font("Arial", Font.BOLD, 12));
		topPanel_timers.add(lbl_total_delay);
		
		JLabel lbl_total_delay_counter = new JLabel("total_delay_counter");
		lbl_total_delay_counter.setFont(new Font("Arial", Font.PLAIN, 12));
		topPanel_timers.add(lbl_total_delay_counter);
		
		//buttons initialized here
		JPanel bottomPanel_buttons = new JPanel();
		frame.getContentPane().add(bottomPanel_buttons, BorderLayout.SOUTH);
		
		JButton btn_reset = new JButton("Reset");
		btn_reset.setFont(new Font("Arial", Font.PLAIN, 12));
		bottomPanel_buttons.add(btn_reset);
		
		JButton btn_restart = new JButton("Restart");
		btn_restart.setFont(new Font("Arial", Font.PLAIN, 12));
		bottomPanel_buttons.add(btn_restart);
		
		//here, the cardlayout and the tasklist!! 
		JPanel middlePanel_tasks = new JPanel();
		frame.getContentPane().add(middlePanel_tasks, BorderLayout.CENTER);
		middlePanel_tasks.setLayout(new CardLayout(0, 0));
		
		
		//split pane here 
		
		JSplitPane splitPane_leftRight = new JSplitPane();
		splitPane_leftRight.setDividerLocation(300);
		middlePanel_tasks.add(splitPane_leftRight, "leftRightSplit");
		
		JSplitPane splitPane_vertical_left = new JSplitPane();
		splitPane_vertical_left.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_vertical_left.setDividerLocation(200);
		splitPane_leftRight.setLeftComponent(splitPane_vertical_left);
		
		JPanel panel_3 = new JPanel();
		splitPane_vertical_left.setRightComponent(panel_3);
		panel_3.setLayout(new CardLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		splitPane_vertical_left.setLeftComponent(panel_1);
		panel_1.setLayout(new CardLayout(0, 0));
		
		JSplitPane splitPane_vertical_right = new JSplitPane();
		splitPane_vertical_right.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_vertical_right.setDividerLocation(200);
		splitPane_leftRight.setRightComponent(splitPane_vertical_right);
		
		JPanel panel_2 = new JPanel();
		splitPane_vertical_right.setLeftComponent(panel_2);
		panel_2.setLayout(new CardLayout(0, 0));
		
		JPanel panel_4 = new JPanel();
		splitPane_vertical_right.setRightComponent(panel_4);
		panel_4.setLayout(new CardLayout(0, 0));
		
		//initTasks(middlePanel_tasks); 
		
		
		
		initTasks(panel_1, testList1); 
		initTasks(panel_2, testList2); 
		initTasks(panel_3, testList3); 
		initTasks(panel_4, testList4); 
		
		
	}
	
	private void initTasks(JPanel jpanel, JobList j){
		//problem lies hereeeeeeeeeeeeeeeeeeeeeeeeeee
		jpanel.add(joblist, "TASK_LIST"); 
		CardLayout c = (CardLayout) (jpanel.getLayout());
		c.show(jpanel, "TASK_LIST");
	}
	
	public static void resetTimers(){
		Constants.TOTALDELAY = 0;
		Constants.TOTALTIME = 0;
	}
	
	public static void seeJobList(JobList jl){
		int mulBays = Constants.QC_X / Constants.MAX_X; 
		for(int k=0; k<mulBays; k++){
			for(int i=0; i<Constants.MAX_Y; i++){
				for(int j=k*Constants.MAX_X; j<(k+1)*Constants.MAX_X; j++){
					System.out.print(jl.getJob(i, j).getTotalCost()+ " ");
				}
				System.out.println(" ");
			}
			
		}
		
	}
	
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
