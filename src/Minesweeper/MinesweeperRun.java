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

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Date: Jan 6, 2017
 * Author: Edward Xie 6
 * File: Minesweeper.java
 * Purpose:
 */
public class MinesweeperRun {

    private static MinesweeperGame game;
    private static final int DEF_WIDTH=30, DEF_HEIGHT=16, DEF_NUM_MINES=99, DEF_TILE_SIZE=62;

    /*static {
        String localPath=System.getProperty("user.dir");
        String lwjglDllPath=localPath+"\\lib\\lwjgl64.dll";
        String openALDllPath=localPath+"\\lib\\OpenAL64.dll";
        System.load(lwjglDllPath);
        System.load(openALDllPath);

        try {
            PrintWriter e=new PrintWriter(System.currentTimeMillis()+".txt");
            e.write(lwjglDllPath+"\n");
            e.write(openALDllPath+"\n");
            e.write("Called MinesweeperRun static block");
            e.close();
        } catch(IOException ex) {
            Logger.getLogger(SoundSource.class.getName()).log(Level.SEVERE,null,ex);
        }
    }*/

    public static void main(String[] args) {//starts game
        game=new MinesweeperGame(DEF_TILE_SIZE,DEF_WIDTH,DEF_HEIGHT,DEF_NUM_MINES);
    }
}
