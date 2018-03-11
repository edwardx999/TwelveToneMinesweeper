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

import javax.swing.border.Border;

/**
 *
 * @author xieedwa
 * @param <OnBoard> the type of piece that goes on the board
 */
public class Board<OnBoard> {

//    static final Color DEFAULT_COLOR1=new Color(255,228,196);
//    static final Color DEFAULT_COLOR2=new Color(205,133,63);
//    static final Color DEFAULT_HIGHLIGHT_COLOR=new Color(171,205,239);
    TileButton<OnBoard>[][] board;//board of buttons [x][y]

    //constructor
    public Board(int width,int height) {
        board=new TileButton[width][height];
        initBoard();
    }

    public TileButton<OnBoard>[][] getBoard() {//returns board array
        return board;
    }

    public TileButton<OnBoard> getTile(int x,int y) {//gets a tile
        return board[x][y];
    }

    public void initBoard() {//initializes board
        for(int c=0;c<board.length;++c)
            for(int r=0;r<board[0].length;++r)
                board[c][r]=new TileButton();
    }

    public void setTileEntity(int x,int y,OnBoard entity) {//changes entity on board
        board[x][y].setEntity(entity);
    }

    public void setTileBorder(int x,int y,Border border) {//changes the border of a tile
        board[x][y].setBorder(border);
    }

    public void setAllBorders(Border border) {//changes all borders of buttons
        for(int r=0;r<board.length;++r)
            for(int c=0;c<board[0].length;++c)
                board[r][c].setBorder(border);
    }
}
