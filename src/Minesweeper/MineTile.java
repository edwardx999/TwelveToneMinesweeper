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

import javax.swing.ImageIcon;

/**
 *
 * @author xieedwa
 */
public class MineTile {

    private int number;//number of mines adjacent, -1 is a mine
    private ImageIcon image;//image used when revealed
    private boolean revealed;//whether tile is revealed
    private boolean flagged;//whether mine is flagged

    //constructors
    public MineTile(int number,ImageIcon image) {
        this.number=number;
        setImage(image);
    }

    public MineTile(int number) {
        this(number,null);
    }

    public void setImage(ImageIcon image) {//changes image of tile
        this.image=image;
    }

    public int getNumberOfMines() {//returns mine number
        return number;
    }

    public void setNumberOfMines(int number) {//assigns mine number
        this.number=number;
    }

    public ImageIcon getImage() {//returns mine image
        return image;
    }

    public boolean isRevealed() {//returns whether tile has been revealed
        return revealed;
    }

    public void setRevealed(boolean revealed) {//changes reveal state of mine
        this.revealed=revealed;
    }

    public void setFlagged(boolean flagged) {//changes flagged state
        this.flagged=flagged;
    }

    public boolean isFlagged() {//returns whether tile is flagged
        return flagged;
    }
}
