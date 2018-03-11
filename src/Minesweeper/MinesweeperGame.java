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

import org.lwjgl.openal.AL10;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.Timer;

/**
 * Date: Jan 6, 2017
 * Author: Edward Xie 6
 * File: MinesweeperGame.java
 * Purpose:
 */
public class MinesweeperGame implements MouseListener, ActionListener, WindowListener {

	private static final Random rand=new Random();
	private boolean running, firstClick=true;//whether game is running, whether you are on the first click
	private int width, height, numMines, tileSize, numCleared, flagsMade;
	//width of board, height of board, total number of mines, pixel size of tiles,cleared tiles needed to win, number of tiles cleared, number of flags made

	private final MinesweeperFrame frame;//frame of game
	private final Timer timer;
	private long time;
	boolean sound=true;
	final boolean soundAllowed;

	//    private Thread timer;
//
//    class Timer extends Thread {
//        //thread used for timer
//
//        long startTime;
//
//        @Override
//        public void run() {
//            startTime=System.currentTimeMillis();;//resets time when game starts
//            while(isRunning())
//                frame.showTime((System.currentTimeMillis()-startTime)/1000);//outputs time when game is running
//        }
//    }
	//all the pictures
	private ImageIcon mine, ded, hap, sup, flg, win, wrong;
	private ImageIcon[] numPics;
	private TwelveTonePlayer ttp;

	//constructor
	public MinesweeperGame(int tileSize,int width,int height,int numMines) {
		frame=new MinesweeperFrame(this,this);
		timer=new Timer(1000,this);
		timer.setInitialDelay(0);
		boolean soundAllowed;
		try {
			ttp=new TwelveTonePlayer();
			soundAllowed=true;
		} catch(MalformedURLException|UnsatisfiedLinkError ex) {
			System.out.println(ex);
			soundAllowed=false;
		}
		this.soundAllowed=soundAllowed;
		initializer(width,height,numMines);
	}

	public void initializer(int width,int height,int numMines) {
		if(!checkCond(width,height,numMines))
			throw new IllegalArgumentException();
		setNumMines(numMines);//calculation of game properties
		setWidth(width);
		setHeight(height);
		int hOffset=0, vOffset=0;
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		int tileSize1=(int)((screenSize.getWidth()-6)/width);
		int tileSize2=(int)((screenSize.getHeight()-24)/(height+1));
		if(tileSize1>tileSize2) {
			setTileSize(tileSize2);
			hOffset=(int)((screenSize.getWidth()-tileSize*width)/2);
		} else {
			setTileSize(tileSize1);
			vOffset=(int)((screenSize.getHeight()-24-tileSize*(height+1))/2);
		}

		fixIconSizes();
		frame.initFrame(tileSize,hOffset,vOffset,width,height,this,true);//initializing frame
		fixIconSizes();//makes icons fit buttons
		frame.setBoard(initMines(frame.getBoard()));//initializes mines
		frame.setResetIcon(hap);
		frame.showTime(999);
		frame.setVisible(true);
		restartGame();//starts the game
	}

	public void setNumMines(int numMines) {//changes number of mines
		this.numMines=numMines;
	}

	public void setWidth(int width) {//changes minewidth
		this.width=width;
	}

	public void setHeight(int height) {//changes mine height
		this.height=height;
	}

	public void setTileSize(int tileSize) {
		if(tileSize<5) throw new IllegalArgumentException();
		this.tileSize=tileSize;
	}

	@Override
	public void actionPerformed(ActionEvent e) {//for game options menu

		if(e.getSource() instanceof Timer)
			if(running)
				frame.showTime(time++);
			else {
				timer.stop();
				time=0;
			}
		if(e.getSource() instanceof JMenuItem)//puts current state in options frame
			if(((JMenuItem)e.getSource()).getText().equals("Game Options")) {
				frame.optionsFrame.heightTF.setText(Integer.toString(height));
				frame.optionsFrame.widthTF.setText(Integer.toString(width));
				frame.optionsFrame.numMinesTF.setText(Integer.toString(numMines));
				frame.showOptionsFrame();
			} else /*if(((JMenuItem)e.getSource()).getText().equals("Sound Toggle"))*/
				sound=!sound;

		if(e.getSource() instanceof JButton) {
			try {
				int newWidth=frame.optionsFrame.getMWidth();//gets new options
				int newHeight=frame.optionsFrame.getMHeight();
				int newNumMines=frame.optionsFrame.getNumMines();
				if(checkCond(newWidth,newHeight,newNumMines))
					if(newWidth!=width||newHeight!=height) {
						frame.setVisible(false);
						initializer(newWidth,newHeight,newNumMines);//remake game for new options
						setGameState(false);
					} else
						setNumMines(newNumMines);
			} catch(IllegalArgumentException err) {//if options are bad
			}
			try {
				float newVolume=Integer.parseInt(frame.optionsFrame.volumeTF.getText())/100.0f;
				if(newVolume>2) newVolume=2;
				AL10.alListenerf(AL10.AL_GAIN,newVolume);
			} catch(IllegalArgumentException err) {//if options are bad
			}
			frame.optionsFrame.setVisible(false);
		}
	}

	private Board<MineTile> initMines(Board<MineTile> board) {//initializes mines on board
		for(int r=0;r<height;++r)
			for(int c=0;c<width;++c)
				board.getTile(c,r).setEntity(new MineTile(0));
		return board;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {//changes reset button to surprise face when clicking on mine
		if(e.getSource() instanceof TileButton&&e.getButton()==MouseEvent.BUTTON1&&running)
			frame.setResetIcon(sup);
	}

	@Override
	public void mouseReleased(MouseEvent e) {//game actions

		if(e.getButton()==MouseEvent.BUTTON1) {//registers left click
			if(e.getSource() instanceof TileButton)//on mine
				tileLeftClick((TileButton<MineTile>)e.getSource(),true);
			if(e.getSource() instanceof ResetButton) {//restarts the game when reset button is clicked
				System.out.println("Reset");
				restartGame();
				//if settings have changed redraw frame;
			}

		} else if(e.getButton()==MouseEvent.BUTTON3)//registers right click
			if(e.getSource() instanceof TileButton) {
				if(!running) return;
				TileButton<MineTile> src=(TileButton<MineTile>)e.getSource();
				if(!src.getEntity().isRevealed()) {//flag toggling
					playSound();
					if(src.getEntity().isFlagged()) {
						--flagsMade;
						src.setIcon(null);
						src.getEntity().setFlagged(false);
					} else {
						++flagsMade;
						src.setIcon(flg);
						src.getEntity().setFlagged(true);
					}
					frame.showNumMines(numMines-flagsMade);
				}
			}
	}

	public static boolean checkCond(int width,int height,int numMines) {
		if(width<10||width>50||height<10||height>30) return false;
		if(numMines<10) return false;
		return width*height-numMines>=10;
	}

	private void tileLeftClick(TileButton<MineTile> src,boolean clicked) {//src of click, and whether or not it was clicked by the player
		if(!running&&!firstClick) return;
		frame.setResetIcon(hap);
		int x=src.getTileX();
		int y=src.getTileY();
		if(firstClick) {
			resetBoard(x,y);//makes game when game starts so that you always start on blank tile
			resetGame();
			frame.showTime(0);
			timer.restart();
		}
		MineTile tile=src.getEntity();
		if(tile.isFlagged()) return;//prevents clicking flagged mine
		if(!tile.isRevealed()) {//clicking on hidden tile
			if(clicked) playSound();
			if(tile.getNumberOfMines()==0){//will flood click empty tiles
				long time=System.nanoTime();
				floodClick(src.getTileX(),src.getTileY());
				//System.out.println(System.nanoTime()-time);
			}
			else {
				if(tile.getNumberOfMines()==-1) endGame();//game on over on mine click
				else ++numCleared;//win progess
				src.getEntity().setRevealed(true);//reveals mine
				src.setIcon(tile.getImage());
			}
		} else if(clicked)//clicking on a revealed tile with all flags completed clicks all adjacent unrevealed tiles
			if(numberOfFlagsAdjacent(x,y,frame.getBoard())==frame.getBoard().getTile(x,y).getEntity().getNumberOfMines()) {
				int m=numCleared;
				for(int dx=-1;dx<=1;++dx)
					for(int dy=-1;dy<=1;++dy)
						if(x+dx>=0&&x+dx<width&&y+dy>=0&&y+dy<height)
							tileLeftClick(frame.getBoard().getTile(x+dx,y+dy),false);
				if(numCleared!=m) playSound();
			}
		if(checkWin()) gameWon();
	}

	private void playSound() {
		if(sound&&soundAllowed)
			ttp.playSound();
	}

	private void floodClick(int x,int y) {//algorithm to click on all bordering empty tiles
		/*
		Pseudocode for scanline
			reveal all 8 around first
			scan right and
		 */
		TileButton<MineTile> currentTile;
		ArrayList<Range> ranges=new ArrayList();
		//start scan to left
		int xL=x;
		while(xL>=0) {
			currentTile=frame.getBoard().getTile(xL,y);
			{
				revealTile(xL,y);
				++numCleared;
				//revealTile(xL,y-1);
				//revealTile(xL,y+1);
			}
			if(0!=currentTile.getEntity().getNumberOfMines())
				break;
			--xL;
		}
		//start scan to right
		int xR=x+1;
		while(xR<width) {
			currentTile=frame.getBoard().getTile(xR,y);
			{
				revealTile(xR,y);
				++numCleared;
				//revealTile(xR,y-1);
				//revealTile(xR,y+1);
			}

			if(0!=currentTile.getEntity().getNumberOfMines())
				break;
			++xR;
		}

		ranges.add(new Range(xL,xR,y));

		for(int xLU=xL;xLU<=xR;++xLU){
			xLU=floodClick0(xLU,xR,y+1,1,ranges);
		}
		for(int xLD=xL;xLD<=xR;++xLD){
			xLD=floodClick0(xLD,xR,y-1,-1,ranges);
		}

		for(Range range:ranges){
			for(int i=range.start;i<=range.end;++i){
				revealTile(i,range.y+1);
				revealTile(i,range.y-1);
				numCleared+=2;
			}
		}
		//scan above
	}

	private static class Range{
		public int start;
		public int end;
		public int y;

		public Range(int start,int end,int y) {
			this.start=start;
			this.end=end;
			this.y=y;
		}
	}

	private int floodClick0(int origXL,int origXR,int y,int direction,ArrayList<Range> ranges) {//idea: do normal 8 way flood fill and add ranges to range stack
		//System.out.println(origXL+" "+origXR+" "+y);
		if(y<0||y>=height)
			return origXR;
		if(origXL<0||origXL>=width)
			return origXL;
		TileButton<MineTile> currentTile=frame.getBoard().getTile(origXL,y);
		if(0!=currentTile.getEntity().getNumberOfMines()||currentTile.getEntity().isRevealed()){
			return origXL;
		}

		int xL=origXL;
		while(xL>=0) {
			currentTile=frame.getBoard().getTile(xL,y);
			{
				revealTile(xL,y);
			}
			if(0!=currentTile.getEntity().getNumberOfMines())
				break;
			--xL;
		}

		int xR=origXL;
		while(xR<width-1) {
			currentTile=frame.getBoard().getTile(xR,y);
			{
				revealTile(xR,y);
			}
			if(0!=currentTile.getEntity().getNumberOfMines())
				break;
			++xR;
		}

		//scan in opposite direction
		int xLLL;
		int xLL=xLLL=xL;
		while(xL<origXL-1) {
			xL=floodClick0(xL,origXL,y-direction,-direction,ranges);
			++xL;
		}
		++origXR;
		while(origXR<=xR) {
			origXR=floodClick0(origXR,xR,y-direction,-direction,ranges);
			++origXR;
		}

		//scan in same direction
		while(xLL<=xR) {
			xLL=floodClick0(xLL,xR,y+direction,direction,ranges);
			++xLL;
		}
		ranges.add(new Range(xLLL,xR,y));
		return xR;
	}

	private void revealTile(TileButton<MineTile> tile) {
		tile.getEntity().setRevealed(true);
		tile.setIcon(tile.getEntity().getImage());
	}

	private void revealTile(int x,int y) {
		if(x<0||x>=width||y<0||y>=height)
			return;
		revealTile(frame.getBoard().getTile(x,y));
	}

	private boolean checkWin() {//win if all non mines cleared
		//return numCleared==width*height-numMines;
		Board<MineTile> board=frame.getBoard();
		for(int x=0;x<width;++x){
			for(int y=0;y<height;++y){
				MineTile tile=board.getTile(x,y).getEntity();
				if(tile.getNumberOfMines()!=-1&&!tile.isRevealed()){
					return false;
				}
			}
		}
		return true;
	}

	private void gameWon() {//end game
		System.out.println("Game Won!");
		setGameState(false);
		frame.setResetIcon(win);
		finishFlags();
		if(soundAllowed) {
			ttp.playCMaj7();
			ttp.saveRow();
		}
	}

	private Board<MineTile> placeMines(int banx,int bany,Board<MineTile> board) {//places mines on board
		int minesCreated=0;
		while(minesCreated<numMines) {
			int x=(int)(width*Math.random());
			int y=(int)(height*Math.random());
			if(board.getTile(x,y).getEntity().getNumberOfMines()!=-1&&(Math.abs(x-banx)>1||Math.abs(y-bany)>1)) {//mines cannot be next to first click
				board.getTile(x,y).getEntity().setNumberOfMines(-1);
				board.getTile(x,y).getEntity().setImage(mine);
				++minesCreated;
			}
		}
		return board;
	}

	private Board<MineTile> assignMineNumbers(Board<MineTile> board) {//assigns mine number to board
		for(int r=0;r<height;++r)
			for(int c=0;c<width;++c) {
				int minesAdj=numberOfMinesAdjacent(c,r,board);
				if(minesAdj!=-1) {
					board.getTile(c,r).getEntity().setNumberOfMines(minesAdj);
					board.getTile(c,r).getEntity().setImage(numPics[minesAdj]);
				}
			}
		return board;
	}

	private int numberOfMinesAdjacent(int x,int y,Board<MineTile> board) {//calculates mines adjecent to a tile
		if(board.getTile(x,y).getEntity().getNumberOfMines()==-1) return -1;
		int minesNext=0;
		for(int dx=-1;dx<=1;++dx)
			for(int dy=-1;dy<=1;++dy)
				if(x+dx>=0&&x+dx<width&&y+dy>=0&&y+dy<height&&(dx!=0||dy!=0))
					if(board.getTile(x+dx,y+dy).getEntity().getNumberOfMines()==-1)
						++minesNext;
		return minesNext;
	}

	private int numberOfFlagsAdjacent(int x,int y,Board<MineTile> board) {//calculates flags adjecent to a tile
		int flagsNext=0;
		for(int dx=-1;dx<=1;++dx)
			for(int dy=-1;dy<=1;++dy)
				if(x+dx>=0&&x+dx<width&&y+dy>=0&&y+dy<height&&(dx!=0||dy!=0))
					if(board.getTile(x+dx,y+dy).getEntity().isFlagged())
						++flagsNext;
		return flagsNext;
	}

	private Board<MineTile> hideAllTiles(Board<MineTile> board) {//hides all tiles
		for(int r=0;r<height;++r)
			for(int c=0;c<width;++c) {
				board.getTile(c,r).getEntity().setRevealed(false);
				board.getTile(c,r).setIcon(null);
			}
		return board;
	}

	private void fixIconSizes() {//makes icons fit on buttons
		mine=new ImageIcon("images/mine.png");
		ded=new ImageIcon("images/ded.png");
		hap=new ImageIcon("images/hap.png");
		sup=new ImageIcon("images/sup.png");
		flg=new ImageIcon("images/flg.png");
		win=new ImageIcon("images/win.png");
		wrong=new ImageIcon("images/wrong.png");
		numPics=new ImageIcon[]{
				new ImageIcon("images/zer.png"),
				new ImageIcon("images/one.png"),
				new ImageIcon("images/two.png"),
				new ImageIcon("images/thr.png"),
				new ImageIcon("images/fou.png"),
				new ImageIcon("images/fiv.png"),
				new ImageIcon("images/six.png"),
				new ImageIcon("images/sev.png"),
				new ImageIcon("images/eig.png")};
		mine=new ImageIcon(mine.getImage().getScaledInstance(tileSize,tileSize,Image.SCALE_SMOOTH));
		for(int i=0;i<9;++i)
			numPics[i]=new ImageIcon(numPics[i].getImage().getScaledInstance(tileSize,tileSize,Image.SCALE_SMOOTH));
		ded=new ImageIcon(ded.getImage().getScaledInstance(tileSize,tileSize,Image.SCALE_SMOOTH));
		hap=new ImageIcon(hap.getImage().getScaledInstance(tileSize,tileSize,Image.SCALE_SMOOTH));
		sup=new ImageIcon(sup.getImage().getScaledInstance(tileSize,tileSize,Image.SCALE_SMOOTH));
		flg=new ImageIcon(flg.getImage().getScaledInstance(tileSize,tileSize,Image.SCALE_SMOOTH));
		win=new ImageIcon(win.getImage().getScaledInstance(tileSize,tileSize,Image.SCALE_SMOOTH));
		wrong=new ImageIcon(wrong.getImage().getScaledInstance(tileSize,tileSize,Image.SCALE_SMOOTH));

	}

	//    private void loadSounds() {
//        ArrayList<String> noteNames=new ArrayList(Arrays.asList(new String[]{"a","as","b","c","cs","d","ds","e","f","fs","g","gs"}));
//        highTones=new ArrayList<>();
//        lowTones=new ArrayList<>();
//        midTones=new ArrayList<>();
//        noteNames=scrambleArray(noteNames);
////        String localPath=System.getProperty("user.dir");
////        System.out.println(localPath);
////        localPath=localPath.replaceAll("\\","/");
//        AudioClip toAdd;
//        try {
//            for(String s:noteNames) {
//                System.out.println(getSystemResource("tones/"+s+"2.wav"));
//                highTones.add(newAudioClip(getSystemResource("tones/"+s+"2.wav")));
//                System.out.println("\\tones\\"+s+"l.wav");
//                lowTones.add(newAudioClip(getSystemResource("tones/"+s+"l.wav")));
//                System.out.println("\\tones\\"+s+"1.wav");
//                midTones.add(newAudioClip(getSystemResource("tones/"+s+"1.wav")));
//            }
//        } catch(NullPointerException e) {
//            System.out.println(e.getClass());
//            System.exit(1);
//        }
//    }
//    private ArrayList scrambleArray(ArrayList array) {
//        ArrayList temp=new ArrayList<>();
//        for(Object o:array)
//            temp.add(o);
//        ArrayList toRet=new ArrayList();
//        while(temp.size()>0) {
//            int swit=rand.nextInt(temp.size());
//            toRet.add(temp.get(swit));
//            temp.remove(swit);
//        }
//        return toRet;
//    }
	private void setGameState(boolean running) {//changes whether game is running
		this.running=running;
	}

	private void endGame() {//game over actions
		System.out.println("Game Over!");
		setGameState(false);
		showMistakes();
		frame.setResetIcon(ded);
		if(soundAllowed)
			ttp.saveRow();
	}

	private void showMistakes() {//reveals all mines and wrongly placed flags
		for(int c=0;c<width;++c)
			for(int r=0;r<height;++r) {
				MineTile temp=frame.getBoard().getTile(c,r).getEntity();
				if(temp.isFlagged()&&temp.getNumberOfMines()!=-1)
					frame.getBoard().getTile(c,r).setIcon(wrong);
				else if(temp.getNumberOfMines()==-1)
					frame.getBoard().getTile(c,r).setIcon(mine);
			}
	}

	private void finishFlags() {//flags all mines on win
		for(int c=0;c<width;++c)
			for(int r=0;r<height;++r)
				if(frame.getBoard().getTile(c,r).getEntity().getNumberOfMines()==-1)
					frame.getBoard().getTile(c,r).setIcon(flg);
		frame.showNumMines(0);
	}

	private void restartGame() {//restarts the game
		numCleared=0;
		firstClick=true;
		setGameState(false);
		frame.showNumMines(numMines);
		frame.setBoard(hideAllTiles(frame.getBoard()));
		frame.setResetIcon(hap);
		frame.showTime(999);
		if(soundAllowed)
			ttp.reset();
		//        frame.repaint();
	}

	private void resetBoard(int banx,int bany) {//remakes the board with mines and numbers after first click
		frame.setBoard(initMines(frame.getBoard()));
		frame.setBoard(placeMines(banx,bany,frame.getBoard()));
		frame.setBoard(assignMineNumbers(frame.getBoard()));
		frame.setResetIcon(hap);
	}

	private void resetGame() {//resets game state
		flagsMade=0;
		setGameState(true);
		firstClick=false;
	}

	public boolean isRunning() {//used to tell timer whether game is running
		return running;
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		killALData();
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	private void killALData() {
		if(soundAllowed)
			ttp.kill();
	}
}
