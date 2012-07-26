import java.awt.Color;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.swing.*;
//import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
enum Player { NONE, X, O }
enum Mode	{ BEGINNER, TWO, EXPERT }

public class TicTacToe implements ActionListener, MouseListener, MenuListener, Runnable {
	
	private Timer timer = null;
	private JFrame window = new JFrame("Tic Tac Toe");
	private JPanel topPanel = new JPanel();
	private JPanel gamePanel = new JPanel();
	private JPanel bottomPanel = new JPanel();
	private GridBagLayout gameGrid = new GridBagLayout();
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
	private Mode mode = Mode.BEGINNER;
	private JDialog winnerDialog = new JDialog();
	private JLabel winnerLabel = new JLabel("Winner!");
	private JButton winnerButton = new JButton("OK");
	private Vector<JPanel> remainingTiles = new Vector<JPanel>();
	private Image xim = null;
	private Image oim = null;
	//private LineBorder border = new LineBorder(Color.BLACK, 3);
	private static int[][][] wins = {
		{{1,1,1}, {0,0,0}, {0,0,0}},
		{{0,0,0}, {1,1,1}, {0,0,0}},
		{{0,0,0}, {0,0,0}, {1,1,1}},
		{{1,0,0}, {1,0,0}, {1,0,0}},
		{{0,1,0}, {0,1,0}, {0,1,0}},
		{{0,0,1}, {0,0,1}, {0,0,1}},
		{{1,0,0}, {0,1,0}, {0,0,1}},
		{{0,0,1}, {0,1,0}, {1,0,0}},
		{{0,0,0}, {0,0,0}, {0,0,0}}
	};
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
		GridBagConstraints c = new GridBagConstraints();
		gamePanel.setLayout(gameGrid);
		gamePanel.setBackground(Color.GRAY);
		// --- Fill in tiles with actual JPanels ---
		for(int i = 0; i < gameTile.length; i++) {
			for(int j = 0; j < gameTile[0].length; j++) {
				gameTile[i][j] = new JPanel();
				gameTile[i][j].addMouseListener(this);
				players.put(gameTile[i][j], Player.NONE);
				gameTile[i][j].setPreferredSize(new Dimension(200, 200));
				gameTile[i][j].setMinimumSize(new Dimension(200, 200));
				gameTile[i][j].setMaximumSize(new Dimension(200, 200));
				c.gridx = j;
				c.gridy = i;
				c.gridwidth = 1;
		        c.gridheight = 1;
		        //gameTile[i][j].setBorder(border);
				gameGrid.setConstraints(gameTile[i][j], c);
				gamePanel.add(gameTile[i][j]);
				remainingTiles.add(gameTile[i][j]);
			}
		}
		infoText.setEditable(false);
		topPanel.setLayout(new GridLayout(1, 3));
		topPanel.add(resetButton);
		resetButton.addActionListener(this);
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
		window.setSize(900,700);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		winnerDialog.setVisible(false);
		winnerDialog.setSize(150,100);
		winnerDialog.getContentPane().add(winnerLabel, "North");
		winnerDialog.getContentPane().add(winnerButton, "South");
		winnerButton.addActionListener(this);
		
		//Get image resources
		try {
			xim = ImageIO.read(new File("x.png"));
			oim = ImageIO.read(new File("o.png"));
		}
		catch (IOException e) {
			System.out.println("Couldn't get image resources for game!" + e);
		}
		
		timer = new Timer(100, this);
		timer.start();
	}	
	
	private void drawAllTiles() {
		for(int i = 0; i < gameTile.length; i++) {
			for(int j = 0; j < gameTile[0].length; j++) {
				setTile(gameTile[i][j], players.get(gameTile[i][j]));
				//gameTile[i][j].setBorder(border);
			}
		}
	}
	
	private boolean checkForWin() {
		int[][] checkarr = new int[3][3];
		for(int i = 0; i < gameTile.length; i++) {
			for(int j = 0; j < gameTile[0].length; j++) {
				if(players.get(gameTile[i][j]) == currentPlayer) {
					checkarr[i][j] = 1;
				}
				else {
					checkarr[i][j] = 0;
				}
			}
		}
		for(int[][] win : wins) {
			if(arrmatch(win, checkarr)) {
				declareWinner();
				return true;
			}
		}
		return false;
	}
	
	private void printstate() {
		System.out.println("GAME STATE:");
		String out = "";
		for(int i = 0; i < gameTile.length; i++) {
			for(int j = 0; j < gameTile[0].length; j++) {
				if(players.get(gameTile[i][j]) == currentPlayer) {
					out += "1 ";
				}
				else {
					out += "0 ";
				}
			}
			out += "\r\n";
		}
		System.out.println(out);
	}
	
	private boolean arrmatch(int[][] a, int[][] b) {
		int out = 0;
		for(int i = 0; i < gameTile.length; i++) {
			for(int j = 0; j < gameTile[0].length; j++) {
				if((a[i][j] == b[i][j]) && (a[i][j] == 1)) {
					out += 1;
				}
			}
		}
		if(out == 3)
			return true;
		else
			return false;
	}
	
	private void reset() {
		remainingTiles = new Vector<JPanel>();
		for(int i = 0; i < gameTile.length; i++) {
			for(int j = 0; j < gameTile[0].length; j++) {
				players.put(gameTile[i][j], Player.NONE);
				remainingTiles.add(gameTile[i][j]);
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
			g.drawImage(xim, 0, 0, tile);
			break;
		case O:
			g.drawImage(oim, 0, 0, tile);
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
	
	private void declareWinner() {
		if(currentPlayer == Player.X) {
			winnerLabel.setText("X PLAYER WINS!");
		}
		else if(currentPlayer == Player.O) {
			winnerLabel.setText("O PLAYER WINS!");
		}
		else {
			winnerLabel.setText("SOMEONE WON!");
		}
		winnerDialog.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		JPanel clickedPanel = (JPanel)me.getSource();
		if(!remainingTiles.contains(clickedPanel)) {
			return;
		}
		players.put(clickedPanel, currentPlayer);
		remainingTiles.remove(clickedPanel);
		if(checkForWin()) {
			return;
		}
		swapPlayers();
		new Thread(this).start();
	}

	@Override
	public void mouseEntered(MouseEvent me) {
	}

	@Override
	public void mouseExited(MouseEvent me) {
	}

	@Override
	public void mousePressed(MouseEvent me) {
		
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		if(ae.getSource() == timer) {
			drawAllTiles();
			return;
		}
		if(ae.getSource() == resetButton) {
			printstate();
			reset();
			infoText.setText("Game reset.");
		}
		if(ae.getSource() == beginner) {
			reset();
			mode = Mode.BEGINNER;
			infoText.setText("Game reset, now in beginner mode.");
			return;
		}
		if(ae.getSource() == twoPlayer) {
			reset();
			mode = Mode.TWO;
			infoText.setText("Game reset, now in two player mode. Take turns selecting squares. X goes first.");
			return;
		}
		if(ae.getSource() == expert) {
			mode = Mode.EXPERT;
			reset();
			infoText.setText("Game reset, now in expert mode.");
			return;
		}
		if(ae.getSource() == winnerButton) {
			winnerDialog.setVisible(false);
			reset();
			infoText.setText("Game reset due to win.");
			return;
		}
	}

	@Override
	public void menuCanceled(MenuEvent arg0) {
		
	}

	@Override
	public void menuDeselected(MenuEvent arg0) {
		System.out.println("Deselected");
		timer.start();
	}

	@Override
	public void menuSelected(MenuEvent arg0) {
		timer.stop();
	}

	@Override
	public void run() {
		switch(mode){
		case TWO:
			return;
		case BEGINNER:
			System.out.println("Beginner");
			if(remainingTiles.size() == 0) {
				return;
			}
			Random r = new Random();
			JPanel tmp = remainingTiles.get(r.nextInt(remainingTiles.size()));
			remainingTiles.remove(tmp);
			players.put(tmp, currentPlayer);
			checkForWin();
			swapPlayers();
			return;
		case EXPERT:
			return;
		default:
			return;
		}
		
	}
}

class RefreshingPicturePanel extends JPanel
{
private Image im;
	  
public RefreshingPicturePanel(Image imageToShow) {
	im = imageToShow;  
  }
	  
@Override
public void paint(Graphics g) {
	g.drawImage(im,0,0,this);
  }

}
