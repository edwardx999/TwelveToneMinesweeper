/*
Copyright(C) 2017 Edward Xie

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package Minesweeper;

import org.lwjgl.openal.AL;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.*;

/**
 * @author xieedwa
 */
public class MinesweeperFrame extends JFrame {

	//Instance Variables
	private Board<MineTile> board;//board of mines
	private ResetButton reset;//reset button
	private JTextField minesLeft, time;//text fields for number of mines and time
	private JPanel boardPanel;//panel for buttons
	private JMenuBar menuBar;//menu bar
	private JMenu options;
	private JMenuItem gameOptions;
	private JMenuItem exit;
	private JMenuItem sound;
	public final OptionsFrame optionsFrame;//frame to change game options
	boolean fullscreen=true;


	//Constructor
	public MinesweeperFrame(ActionListener al,WindowListener wl) {
		addWindowListener(wl);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initMenu(al);
		setTitle("Minesweeper by EX");
		setIconImage(new ImageIcon("images/icon.png").getImage());
		setUndecorated(fullscreen);
		optionsFrame=new OptionsFrame(this,al);
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("images/cursorsquare.png").getImage(),new Point(this.getX(),this.getY()),""));
	}

	/*public MinesweeperFrame(int tileSize,int width,int height,MouseListener ml,ActionListener al) {
				Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		initBoard(width,height);//initializes board of a given height and width
		initFrame(tileSize,width,height,ml);//initializes frames and buttons
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initMenu(al);
		setTitle("Minesweeper");
		optionsFrame=new OptionsFrame(this,al);
		setLocationRelativeTo(null);
		setVisible(true);
	}*/
	public void initBoard(int width,int height) {//makes buttons exist
		board=new Board<>(width,height);
	}

	public void initFrame(int tileSize,int hOffset,int vOffset,int width,int height,MouseListener ml,boolean fullscreen) {

		//System.out.println(tileSize);
		boardPanel=new JPanel();
		boardPanel.setLayout(null);
		initBoard(width,height);
		for(int r=0;r<height;++r)//adding buttons to panel
			for(int c=0;c<width;++c) {
				board.getTile(c,r).setTileX(c);
				board.getTile(c,r).setTileY(r);
				board.getTile(c,r).setBounds(c*tileSize+hOffset,r*tileSize+vOffset,tileSize,tileSize);
				board.getTile(c,r).setBackground(new Color(100,160,220));
				board.getTile(c,r).addMouseListener(ml);
				boardPanel.add(board.getTile(c,r));
			}

		reset=new ResetButton();//adding reset button
		reset.setBounds((width-1)*tileSize/2+hOffset,(tileSize)*height+vOffset,tileSize,tileSize);
		reset.addMouseListener(ml);
		boardPanel.add(reset);

		minesLeft=new JTextField();//adding text field for mines left
		minesLeft.setBounds((width-4)*tileSize/2+hOffset,(tileSize)*height+vOffset,3*tileSize/2,tileSize);
		minesLeft.setEditable(false);
		minesLeft.setForeground(Color.RED);
		minesLeft.setBackground(Color.BLACK);
		minesLeft.setFont(new Font("Digital-7 Mono",Font.PLAIN,tileSize));
		minesLeft.setHorizontalAlignment(JTextField.RIGHT);
		boardPanel.add(minesLeft);

		time=new JTextField();//adding text field for timer
		time.setBounds((width+1)*tileSize/2+hOffset,(tileSize)*height+vOffset,3*tileSize/2,tileSize);
		time.setEditable(false);
		time.setForeground(Color.RED);
		time.setBackground(Color.BLACK);
		time.setFont(new Font("Digital-7 Mono",Font.PLAIN,tileSize));
		boardPanel.add(time);

		//boardPanel.setBounds(0,0,width*tileSize,height*tileSize);//sizing and adding panel
		board.setAllBorders(BorderFactory.createLineBorder(new Color(50,50,50),1));
		boardPanel.setSize(width*tileSize,(height+1)*tileSize);
		this.setContentPane(boardPanel);
		TileButton botRig=board.getTile(width-1,height-1);
		//System.out.println(botRig.getX());
		//System.out.println(botRig.getY());
		//setSize(new Dimension(botRig.getX()+2*tileSize+2,board.getTile(0,height-1).getY()+5*tileSize+30));
		if(fullscreen)
			setSize(Toolkit.getDefaultToolkit().getScreenSize());
		else setSize(new Dimension(width*tileSize+6,(height+1)*tileSize+59));
		//change to grid bag layout
		setResizable(false);
		setLocationRelativeTo(null);
	}

	private void initMenu(ActionListener al) {//creates and adds menu items
		menuBar=new JMenuBar();

		options=new JMenu("Game");
		options.setMnemonic(KeyEvent.VK_G);

		gameOptions=new JMenuItem("Game Options");
		gameOptions.addActionListener(al);
		options.add(gameOptions);

		sound=new JMenuItem("Sound Toggle");
		sound.addActionListener(al);
		options.add(sound);

		exit=new JMenuItem("Exit");
		exit.addActionListener((ActionEvent e)->{
			try {
				AL.destroy();
			} catch(Throwable err) {
				try {
					PrintWriter eee=new PrintWriter(System.currentTimeMillis()+".txt");
					eee.write(err.getMessage()+"\n");
					eee.close();
				} catch(IOException errrrr) {
				}
			}
			System.exit(0);
		});
		options.add(exit);
		menuBar.add(options);
		setJMenuBar(menuBar);
	}

	public Board<MineTile> getBoard() {//returns the board used
		return board;
	}

	public void setBoard(Board board) {//allows changing the board
		this.board=board;
	}

	public void showTime(long time) {//writes time to text field
		String show;
		if(time>=999) show="999";
		else {
			show=Long.toString(time);
			show="00".substring(0,3-show.length())+show;
		}
		this.time.setText(show);
	}

	public void showNumMines(int numMines) {//writes number of mines left to field
		String show;
		if(numMines>=999) show="999";
		else {
			show=Long.toString(numMines);
			show="00".substring(0,3-show.length())+show;
		}
		this.minesLeft.setText(show);
	}

	public void setResetIcon(ImageIcon face) {//changes the icon of the reset button
		reset.setIcon(face);
	}

	public void showOptionsFrame() {
		optionsFrame.setLocationRelativeTo(this);
		optionsFrame.setVisible(true);
	}
}

class ResetButton extends JButton {//used to distinguish buttons in logic

}

class OptionsFrame extends JFrame {//options menu

	private JLabel heightLBL;
	public JTextField heightTF;
	private JLabel numMinesLBL;
	public JTextField numMinesTF;
	private JButton okBUT;
	private JLabel widthLBL;
	public JTextField widthTF;
	private JLabel volumeLBL;
	public JTextField volumeTF;

	public OptionsFrame(JFrame center,ActionListener al) {//aligns frame with gui
		setLocationRelativeTo(center);
		setUndecorated(true);
		initFrame(al);
	}

	public void initFrame(ActionListener al) {//initializes options frame

		numMinesLBL=new JLabel();
		widthLBL=new JLabel();
		heightLBL=new JLabel();
		numMinesTF=new JTextField();
		widthTF=new JTextField();
		heightTF=new JTextField();
		okBUT=new JButton();
		volumeLBL=new JLabel("Volume");
		volumeTF=new JTextField();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		numMinesLBL.setText("Number of Mines:");

		widthLBL.setText("Width:");

		heightLBL.setText("Height:");

		numMinesTF.setText("10");

		widthTF.setText("9");

		heightTF.setText("9");

		okBUT.setText("OK");
		okBUT.addActionListener(al);

		GroupLayout layout=new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
										.addComponent(okBUT)
										.addComponent(heightLBL)
										.addComponent(numMinesLBL)
										.addComponent(widthLBL)
										.addComponent(volumeLBL))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING,false)
										.addComponent(numMinesTF,GroupLayout.DEFAULT_SIZE,50,Short.MAX_VALUE)
										.addComponent(widthTF)
										.addComponent(heightTF)
										.addComponent(volumeTF))
								.addContainerGap(GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE))
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(numMinesLBL)
										.addComponent(numMinesTF,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(widthLBL)
										.addComponent(widthTF,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(heightLBL)
										.addComponent(heightTF,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(volumeLBL)
										.addComponent(volumeTF,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(okBUT)
								.addContainerGap(GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE))
		);

		pack();
	}

	//these methods return the game options from fields: number of mines, width of board, height of board
	public int getNumMines() {
		return Integer.parseInt(numMinesTF.getText());
	}

	public int getMWidth() {
		return Integer.parseInt(widthTF.getText());
	}

	public int getMHeight() {
		return Integer.parseInt(heightTF.getText());
	}
}
