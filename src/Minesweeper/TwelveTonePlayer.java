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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Date: Feb 9, 2017
 * Author: Edward Xie 6
 * File: TwelveTonePlayer.java
 * Purpose:
 */
public class TwelveTonePlayer {

	private SoundSource lowTones, midTones, highTones;
	private static final ArrayList<Integer> zeroToEleven=new ArrayList<>();

	static {
		//System.load(new File("lib/lwjgl64.dll").getAbsolutePath());
		//System.load(new File("lib/OpenAL64.dll").getAbsolutePath());
		/*try {
			PrintWriter e=new PrintWriter(System.currentTimeMillis()+".txt");
			e.write("Called TwelveTonePlayer static block");
			e.close();
		} catch(Exception err) {
		}*/
		for(int r=0;r<=11;++r) {
			zeroToEleven.add(r);
		}
//        String localPath=System.getProperty("user.dir");
//        String dllPath=localPath+"\\lib\\lwjgl64.dll";
//        System.out.println(dllPath);
//        System.load(dllPath);
	}

	private int[] row;
	private int rowNote=0, lowNote=0;
	private volatile String played;
	private int whichWay;//0 is normal, 1 retrograde, 2 inverted, 3 retrograde and inverted, 4+ is normal
	private static final String[] noteNames={"a","a#","b","c","c#","d","d#","e","f","f#","g","g#"};
	private static final Random rand=new Random();
	boolean failed;

	public TwelveTonePlayer() throws MalformedURLException {
		loadSounds();
		createRow();
	}

	public TwelveTonePlayer(boolean failed) {
		this.failed=failed;
	}

	public void reset() {
		createRow();
		rowNote=0;
		lowNote=0;
		played="";
		for(int i : row)
			played+=noteNames[i]+" ";
	}

	public void playSound() {
		if(!failed) {
			if(rowNote==0) {
				played+="\n";
				whichWay=rand.nextInt(5);
				if(whichWay==1)
					played+="Retrograde\n";
				else if(whichWay==2)
					played+="Inverted\n";
				else if(whichWay==3)
					played+="Inverted and Retrograde\n";
				else
					played+="Normal\n";
			}
			int noteToPlay;

			if(whichWay==1)
				noteToPlay=row[11-rowNote];
			else if(whichWay==2)
				noteToPlay=inv(rowNote);
			else if(whichWay==3)
				noteToPlay=inv(11-rowNote);
			else
				noteToPlay=row[rowNote];
//            System.out.println(noteToPlay);
			highTones.playSound(noteToPlay);
			if(noteToPlay!=3) {
				lowNote=(lowNote+1)%5;
				int lowNoteToPlay=rand.nextInt(12);
				if(lowNote==0)
					lowTones.playSound(lowNoteToPlay);
				int midNoteToPlay=rand.nextInt(12);
				midTones.playSound(midNoteToPlay);
				played+="\tHigh:"+noteNames[noteToPlay]
						+"\tMiddle:"+noteNames[midNoteToPlay]
						+"\tLow:"+((lowNote==0)?(noteNames[lowNoteToPlay]+"\n"):"");
			} else {
				midTones.playSound(2);
				midTones.playSound(3);
				midTones.playSound(7);
				midTones.playSound(10);
				lowTones.playSound(10);
				played+="\tCMaj 7\n";
				// System.out.println("CMaj7");
			}
			if(rand.nextInt(12)!=1)
				rowNote=(rowNote+1)%12;
		}
	}

	public void playCMaj7() {
		//lowTones.playSound(2);
		lowTones.playSound(3);
		lowTones.playSound(7);
		lowTones.playSound(10);
		midTones.playSound(2);
		midTones.playSound(3);
		midTones.playSound(7);
		midTones.playSound(10);
	}

	/**
	 * calculates the inversion from the prime note
	 *
	 * @param count
	 * @return
	 */
	private int inv(int count) {
		int newNum=2*row[0]-row[count];
		if(newNum>11) newNum%=12;
		if(newNum<0) newNum+=12;
		return newNum;
	}

	private void loadSounds() throws MalformedURLException {
		/*try {
			PrintWriter e=new PrintWriter(System.currentTimeMillis()+".txt");
			e.write("Called Load Sounds");
			e.close();
		} catch(Exception err) {
		}*/
		lowTones=new SoundSource(new String[]{
				"tones\\al.wav",
				"tones\\asl.wav",
				"tones\\bl.wav",
				"tones\\cl.wav",
				"tones\\csl.wav",
				"tones\\dl.wav",
				"tones\\dsl.wav",
				"tones\\el.wav",
				"tones\\fl.wav",
				"tones\\fsl.wav",
				"tones\\gl.wav",
				"tones\\gsl.wav"},100f
		);
		midTones=new SoundSource(new String[]{
				"tones\\a1.wav",
				"tones\\as1.wav",
				"tones\\b1.wav",
				"tones\\c1.wav",
				"tones\\cs1.wav",
				"tones\\d1.wav",
				"tones\\ds1.wav",
				"tones\\e1.wav",
				"tones\\f1.wav",
				"tones\\fs1.wav",
				"tones\\g1.wav",
				"tones\\gs1.wav"},155550f
		);
		highTones=new SoundSource(new String[]{
				"tones\\a2.wav",
				"tones\\as2.wav",
				"tones\\b2.wav",
				"tones\\c2.wav",
				"tones\\cs2.wav",
				"tones\\d2.wav",
				"tones\\ds2.wav",
				"tones\\e2.wav",
				"tones\\f2.wav",
				"tones\\fs2.wav",
				"tones\\g2.wav",
				"tones\\gs2.wav"},1f
		);
	}

	public void createRow() {
		ArrayList<Integer> temp=(ArrayList<Integer>)zeroToEleven.clone();
		row=new int[12];
		int pick;
		for(int i=0;i<12;++i) {
			pick=rand.nextInt(temp.size());
			row[i]=temp.get(pick);
			temp.remove(pick);
		}
		/*for(int i:row)
            System.out.println(i);
        System.out.println();*/
	}

	public void saveRow() {
		try {
			String localPath=System.getProperty("user.dir")+"\\";
//            System.out.println();
//            System.out.print(played);
			localPath="C:\\Users\\edwar\\Videos\\";
			SimpleDateFormat sdf=new SimpleDateFormat("MMddyyyy-hhmmss");
			//System.out.println(localPath+sdf.format(Date.from(Instant.now()))+".txt");
			File toSave=new File(localPath+sdf.format(Date.from(Instant.now()))+".txt");
			PrintWriter pw;
			pw=new PrintWriter(new FileWriter(toSave));
			pw.print(played);
			pw.close();
		} catch(IOException ex) {
			//Logger.getLogger(MinesweeperGame.class.getName()).log(Level.SEVERE,null,ex);
		}
	}

	public void kill() {
		midTones.killALData();
		highTones.killALData();
		lowTones.killALData();
	}

}
