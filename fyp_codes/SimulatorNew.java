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

public class SimulatorNew {

	private JFrame frame;
	
	private static JobList joblist; 
	private static ArrayList<Agv> agvList = new ArrayList<>();
	
	private static int totalTime = 0;
	private static int totalDelay = 0;
	
	public static void main(String[] args) {
		
		resetTimers();
		
		joblist = new JobList(); 
		joblist.setBorder(null);
		seeJobList(joblist); 
		joblist.setLayout(new CardLayout(0, 0));
		
		for(int i=0; i<Constants.AGV; i++){
			Agv agv = new Agv(i); 
			agvList.add(agv); 
		}
		System.out.println("agv done");
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimulatorNew window = new SimulatorNew();
					window.frame.setVisible(true);
					
					CalcTime totalTimer = new CalcTime("totalTimer"); 
					
					totalTimer.start();
					
					Greedy g = new Greedy(joblist, agvList);
					g.startMergedGreedy();
					
					
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
		
		middlePanel_tasks.add(joblist, "TASK_LISTS");
		
		//initTasks(middlePanel_tasks); 
	}
	
	private void initTasks(JPanel jpanel){
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
