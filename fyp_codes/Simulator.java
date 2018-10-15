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
		
		
		//just testing greedy class
		Greedy g = new Greedy(joblist, agvList); 
		g.startGreedy();
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
		_frame.setSize(new Dimension(400, 450));	//window size 400(width) by 400(height)  
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
		JButton btn_greedy_algo = new JButton("Greedy Algorithm");
		btn_greedy_algo.setFont(new Font("Calbri", Font.BOLD, 12));
		btn_greedy_algo.setFocusPainted(false);
		//add event 
		
		
		_buttons.add(btn_greedy_algo);
		
		//other search to be added later 
	}

}
