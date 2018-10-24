package fyp_codes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.MouseAdapter;	//see what this does
import java.awt.event.MouseEvent;	//see what this does

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;	//use TimeUnit 
public class Simulator {
	private static JFrame _frame = null; //display job list
	private static JPanel _tiles = null; //display individual task
	private static JPanel _buttons = null;	//display buttons <-use later
	//private static JButton button; 
	
	private static JLabel _label = new JLabel("Simulator"); //set title here 
	
	private static JobList joblist; 
	private static ArrayList<Agv> agvList = new ArrayList<>();
	
	private static int agvNo = 4; 	//number of agv
	
	public static void main(String[] args){
		joblist = new JobList(); 
		seeJobList(); 
		
		for(int i=0; i<agvNo; i++){
			Agv agv = new Agv(i); 
			agvList.add(agv); 
		}
		System.out.println("agv done");
		
		viewSimulator();
		
		Greedy g = new Greedy(joblist, agvList); 
		
		//testing the simulator 
		//g.testSimulator();
		
		//test simple greedy
		//g.startGreedy1();
		g.startGreedy2();
		
		
		//System.out.println("-------------------all jobs complete---------------");
	}
	
	public static void seeJobList(){
		for(int i=0; i<Constants.MAX_Y; i++){
			for(int j=0; j<Constants.MAX_X; j++){
				System.out.print(joblist.getJob(i, j).getTotalCost()+ " ");
			}
			System.out.println(" ");
		}
	}
	
	//don't add buttons yet 
	private static void viewSimulator(){
		_frame = new JFrame();
		_frame.setSize(new Dimension(600, 450));	//window size 400(width) by 400(height)  
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
		contentPane.add(_buttons, BorderLayout.PAGE_END); 
		((JComponent) contentPane).setBorder(new EmptyBorder(10, 10, 10, 10));
		
		_tiles.add(_label);
		
		//initialize map layout
		initTasks();
		
		//initialize button layout
		initButtons();
		
		//view full display of the application
		_frame.setVisible(true);
		_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	private static void initTasks(){
		_tiles.add(joblist, "TASK_LIST");
		
		CardLayout c = ((CardLayout)_tiles.getLayout());
		
		c.show(_tiles, "TASK_LIST");
		//not sure if correct or not 
		/*
		CardLayout card = new CardLayout();
		JPanel cards = new JPanel(card);
		cards.add(joblist, "TASK_LIST");
		card.show(cards, "TASK_LIST");
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

}
