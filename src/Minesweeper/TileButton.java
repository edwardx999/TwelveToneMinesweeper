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

import javax.swing.JButton;

public class TileButton<OnBoard> extends JButton {

    private OnBoard entity;//what is on the tile, I probably should have just made this a MineButton class
    private int x, y;//x y 
    
    //constructors
    public TileButton() {
        this(null,0,0);
    }

    public TileButton(int x,int y) {
        this(null,x,y);
    }

    public TileButton(OnBoard entity,int x,int y) {
        setEntity(entity);
        setTileX(x);
        setTileY(y);
    }

    public OnBoard getEntity() {//returns what is on the tile
        return entity;
    }

    public void setEntity(OnBoard entity) {//changes what is on the tile
        this.entity=entity;
    }

    public int getTileX() {//returns x of tiles
        return x;
    }

    public int getTileY() {//returns y of tile
        return y;
    }

    public void setTileX(int x) {//changes x of tile
        this.x=x;
    }

    public void setTileY(int y) {//changes y of tile
        this.y=y;
    }
}
