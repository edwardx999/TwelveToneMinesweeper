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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

/**
 * Date: Feb 8, 2017
 * Author: Edward Xie 6
 * File: Sounds.java
 * Purpose: File Playing sounds from one source to one listener
 */
public class SoundSource {

	private final int NUM_BUFFERS;
	private final int NUM_SOURCES;
	private static final int REDUN=3;
	//    public static final Random RAND=new Random();
	private int[] redunCount;
	private IntBuffer buffer;
	private IntBuffer source;
	private FloatBuffer sourcePos;
	private FloatBuffer sourceVel;
	private FloatBuffer listenerPos;
	private FloatBuffer listenerVel;
	private FloatBuffer listenerOri;

	static {
		/*String localPath=System.getProperty("user.dir");
		//String dllDir=localPath+"\\lib";
		String lwjglDllPath=localPath+"\\lib\\lwjgl64.dll";
		String openALDllPath=localPath+"\\lib\\OpenAL64.dll";
		System.load(lwjglDllPath);
		System.load(openALDllPath);*/

		try {
			/*PrintWriter e=new PrintWriter(System.currentTimeMillis()+".txt");
			//e.write(lwjglDllPath+"\n");
			//e.write(openALDllPath+"\n");
			e.write("Called SoundSource static block");
			e.close();*/
			if(!AL.isCreated()) {
				AL.create();
			}
		} catch(LWJGLException ex) {
			Logger.getLogger(SoundSource.class.getName()).log(Level.SEVERE,null,ex);
		}
	}

	public SoundSource(String[] soundFiles,float volume) throws MalformedURLException {
		/*try {
			PrintWriter e=new PrintWriter(System.currentTimeMillis()+".txt");
			e.write("Called SoundSource Constructor");
			e.close();
		} catch(Exception err) {
		}*/
		NUM_BUFFERS=soundFiles.length;
		NUM_SOURCES=NUM_BUFFERS;
		redunCount=new int[NUM_BUFFERS];
		for(int i=0;i<NUM_BUFFERS;++i) {
			redunCount[i]=0;
		}
		createBuffers();
		loadALData(soundFiles,volume);
		setListenerValues();
	}

	public void playSound(int which) {
		redunCount[which]=(redunCount[which]+1)%REDUN;
		AL10.alSourcePlay(source.get(which+redunCount[which]*NUM_BUFFERS));
	}

	private void createBuffers() {
		buffer=BufferUtils.createIntBuffer(REDUN*NUM_BUFFERS);
		source=BufferUtils.createIntBuffer(REDUN*NUM_SOURCES);
		sourcePos=(FloatBuffer)BufferUtils.createFloatBuffer(3*REDUN*NUM_SOURCES).rewind();
		sourceVel=(FloatBuffer)BufferUtils.createFloatBuffer(3*REDUN*NUM_SOURCES).rewind();
		listenerPos=(FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[]{0,0,0}).rewind();
		listenerVel=(FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[]{0,0,0}).rewind();
		listenerOri=(FloatBuffer)BufferUtils.createFloatBuffer(6).put(new float[]{0,0,0,0,0,0}).rewind();
	}

	public int loadALData(URL[] soundPaths,float volume) {
		AL10.alGenBuffers(buffer);
		AL10.alGenSources(source);
		if(AL10.alGetError()!=AL10.AL_NO_ERROR||AL10.alGetError()!=AL10.AL_NO_ERROR) {
			return AL10.AL_FALSE;
		}
		WaveData sound;
		int length=soundPaths.length;
		for(int i=0;i<REDUN*length;++i) {
//            System.out.println(soundPaths[i%length]);
			sound=WaveData.create(soundPaths[i%length]);
//            System.out.println(sound);
			AL10.alBufferData(buffer.get(i),sound.format,sound.data,sound.samplerate);
			sound.dispose();
			AL10.alSourcei(source.get(i),AL10.AL_BUFFER,buffer.get(i));
			AL10.alSourcef(source.get(i),AL10.AL_PITCH,1.0f);
			AL10.alSourcef(source.get(i),AL10.AL_GAIN,volume);
			AL10.alSource(source.get(i),AL10.AL_POSITION,sourcePos);
			AL10.alSource(source.get(i),AL10.AL_VELOCITY,sourceVel);
		}

		if(AL10.alGetError()==AL10.AL_NO_ERROR) {
			return AL10.AL_TRUE;
		}
		return AL10.AL_FALSE;
	}

	public int loadALData(String[] soundNames,float volume) throws MalformedURLException {
		URL[] soundPaths=new URL[soundNames.length];
		String localPath=System.getProperty("user.dir");

		for(int i=0;i<soundNames.length;++i) //            System.out.println("file:"+localPath+"\\"+soundNames[i]);
		{
			soundPaths[i]=new URL("file:"+localPath+"\\"+soundNames[i]);
		}
		return loadALData(soundPaths,volume);
	}

	private void setListenerValues() {
		AL10.alListener(AL10.AL_POSITION,listenerPos);
		AL10.alListener(AL10.AL_VELOCITY,listenerVel);
		AL10.alListener(AL10.AL_ORIENTATION,listenerOri);
	}

	public void killALData() {
		AL10.alDeleteSources(source);
		AL10.alDeleteBuffers(buffer);
	}
}
