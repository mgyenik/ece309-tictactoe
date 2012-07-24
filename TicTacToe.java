import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;
//import javax.swing.border.Border;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
enum Player { NONE, X, O }

public class TicTacToe implements ActionListener, MouseListener, MenuListener{

	private JFrame window = new JFrame("Tic Tac Toe");
	private JPanel topPanel = new JPanel();
	private JPanel gamePanel = new JPanel();
	private JPanel bottomPanel = new JPanel();
	private GridLayout gameGrid = new GridLayout(3, 3);
	private JPanel[][] gameTile = new JPanel[3][3];				//No construction done yet, all elements NULL!
	private JTextField infoText = new JTextField(80);
	private JMenuBar gameMenuBar = new JMenuBar();
	private JMenu gameMenu = new JMenu("Options");
	private JMenuItem beginner = new JMenuItem("Beginner Mode");
	private JMenuItem twoPlayer = new JMenuItem("Two Player Mode");
	private JMenuItem expert = new JMenuItem("Expert Mode");
	private JButton resetButton = new JButton("Reset Game");
	private HashMap<JPanel, Player> players = new HashMap<JPanel, Player>();
	private Player currentPlayer = Player.X;
	//private Border border = BorderFactory.createLineBorder(Color.BLACK);
	public TicTacToe() {
		buildGUI();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TicTacToe();
	}
	
	private void buildGUI() {
		gamePanel.setLayout(gameGrid);
		// --- Fill in tiles with actual JPanels ---
		for(int i = 0; i < gameTile.length; i++) {
			for(int j = 0; j < gameTile[0].length; j++) {
				gameTile[i][j] = new JPanel();
				gameTile[i][j].addMouseListener(this);
				players.put(gameTile[i][j], Player.NONE);
				gamePanel.add(gameTile[i][j]);
			}
		}
		/*
		for(int i = 0; i < players.length; i++) {
			for(int j = 0; j < players[0].length; j++) {
				players[i][j] = Player.NONE;
			}
		}
		*/
		infoText.setEditable(false);
		topPanel.setLayout(new GridLayout(1, 3));
		bottomPanel.add(infoText);
		
		beginner.addActionListener(this);
		twoPlayer.addActionListener(this);
		expert.addActionListener(this);
		
		gameMenu.add(beginner);
		gameMenu.add(twoPlayer);
		gameMenu.add(expert);
		gameMenu.addMenuListener(this);
		gameMenuBar.add(gameMenu);
		window.setJMenuBar(gameMenuBar);
		
		window.getContentPane().add(topPanel, "North");
		window.getContentPane().add(gamePanel, "Center");
		window.getContentPane().add(bottomPanel, "South");
		window.setSize(900,600);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	private void drawAllTiles() {
		for(int i = 0; i < gameTile.length; i++) {
			for(int j = 0; j < gameTile[0].length; j++) {
				setTile(gameTile[i][j], players.get(gameTile[i][j]));
			}
		}
	}
	
	private void setTile(JPanel tile, Player p){
		int w = tile.getSize().width;
		int h = tile.getSize().height;
		Graphics g = tile.getGraphics();
		switch(p) {
		case NONE:
			g.setColor(Color.WHITE);
			g.fillRect(0,0,w,h);
			break;
		case X:
			g.setColor(Color.BLACK);
			g.fillRect(0,0,w,h);
			break;
		case O:
			g.setColor(Color.RED);
			g.fillRect(0,0,w,h);
			break;
		}
	}

	private void swapPlayers() {
		if(currentPlayer == Player.X) {
			currentPlayer = Player.O;
		}
		else if(currentPlayer == Player.O) {
			currentPlayer = Player.X;
		}
		else {
			currentPlayer = Player.NONE;
			System.out.println("something weird happened while swapping players");
		}
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		//int x = me.getX();
		//int y = me.getY();
		JPanel clickedPanel = (JPanel)me.getSource();
		System.out.println("You clicked -> " + clickedPanel);
		players.put(clickedPanel, currentPlayer);
		swapPlayers();
		drawAllTiles();
		
	}

	@Override
	public void mouseEntered(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		if(ae.getSource() == beginner) {
			infoText.setText("Game reset, now in beginner mode.");
			return;
		}
		if(ae.getSource() == twoPlayer) {
			infoText.setText("Game reset, now in two player mode. Take turns selecting squares. X goes first.");
			return;
		}
		if(ae.getSource() == expert) {
			infoText.setText("Game reset, now in expert mode.");
			return;
		}
	}

	@Override
	public void menuCanceled(MenuEvent arg0) {
		//System.out.println("Cancelled");
		
	}

	@Override
	public void menuDeselected(MenuEvent arg0) {
		System.out.println("Deselected");
		drawAllTiles();
	}

	@Override
	public void menuSelected(MenuEvent arg0) {
		//System.out.println("Selected");
		
	}

}