/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.util.ArrayList;

public class TileBlock {
    int occX;
    int occY;
    int occW;
    int occH;
    int xOff;
    int yOff;
    int w;
    int h;
    int benefit;
    boolean[] occupied;

    TileBlock(int occX, int occY, int occW, int occH, boolean[] occupied, int xOff, int yOff, int w, int h) {
        this.occX = occX;
        this.occY = occY;
        this.occW = occW;
        this.occH = occH;
        this.xOff = xOff;
        this.yOff = yOff;
        this.w = w;
        this.h = h;
        this.occupied = occupied;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                if (occupied[x + xOff + occW * (y + yOff)]) continue;
                ++this.benefit;
            }
        }
    }

    public String toString() {
        String ret = "";
        for (int y = 0; y < this.occH; ++y) {
            for (int x = 0; x < this.occW + 1; ++x) {
                ret = x == this.xOff || x == this.xOff + this.w ? (y == this.yOff || y == this.yOff + this.h - 1 ? ret + "+" : (y > this.yOff && y < this.yOff + this.h - 1 ? ret + "|" : ret + " ")) : (y == this.yOff && x > this.xOff && x < this.xOff + this.w ? ret + "-" : (y == this.yOff + this.h - 1 && x > this.xOff && x < this.xOff + this.w ? ret + "_" : ret + " "));
                if (x == this.occW) continue;
                ret = this.occupied[x + y * this.occW] ? ret + "*" : ret + ".";
            }
            ret = ret + "\n";
        }
        return ret;
    }

    int getXLoc() {
        return this.occX + this.xOff;
    }

    int getYLoc() {
        return this.occY + this.yOff;
    }

    int getWidth() {
        return this.w;
    }

    int getHeight() {
        return this.h;
    }

    int getBenefit() {
        return this.benefit;
    }

    int getWork() {
        return this.w * this.h + 1;
    }

    static int getWork(TileBlock[] blocks) {
        int ret = 0;
        for (TileBlock block : blocks) {
            ret += block.getWork();
        }
        return ret;
    }

    TileBlock[] getBestSplit() {
        if (this.simplify()) {
            return null;
        }
        if (this.benefit == this.w * this.h) {
            return new TileBlock[]{this};
        }
        return this.splitOneGo();
    }

    public TileBlock[] splitOneGo() {
        boolean[] filled = (boolean[])this.occupied.clone();
        ArrayList<TileBlock> items = new ArrayList<TileBlock>();
        for (int y = this.yOff; y < this.yOff + this.h; ++y) {
            for (int x = this.xOff; x < this.xOff + this.w; ++x) {
                if (filled[x + y * this.occW]) continue;
                int cw = this.xOff + this.w - x;
                for (int cx = x; cx < x + cw; ++cx) {
                    if (filled[cx + y * this.occW]) {
                        cw = cx - x;
                        continue;
                    }
                    filled[cx + y * this.occW] = true;
                }
                int ch = 1;
                for (int cy = y + 1; cy < this.yOff + this.h; ++cy) {
                    int cx;
                    for (cx = x; cx < x + cw && !filled[cx + cy * this.occW]; ++cx) {
                    }
                    if (cx != x + cw) break;
                    for (cx = x; cx < x + cw; ++cx) {
                        filled[cx + cy * this.occW] = true;
                    }
                    ++ch;
                }
                items.add(new TileBlock(this.occX, this.occY, this.occW, this.occH, this.occupied, x, y, cw, ch));
                x += cw - 1;
            }
        }
        TileBlock[] ret = new TileBlock[items.size()];
        items.toArray(ret);
        return ret;
    }

    public boolean simplify() {
        int y;
        int x;
        int x2;
        int y2;
        boolean[] workOccupied = this.occupied;
        for (y2 = 0; y2 < this.h; ++y2) {
            for (x2 = 0; x2 < this.w && workOccupied[x2 + this.xOff + this.occW * (y2 + this.yOff)]; ++x2) {
            }
            if (x2 != this.w) break;
            ++this.yOff;
            --y2;
            --this.h;
        }
        if (this.h == 0) {
            return true;
        }
        for (y2 = this.h - 1; y2 >= 0; --y2) {
            for (x2 = 0; x2 < this.w && workOccupied[x2 + this.xOff + this.occW * (y2 + this.yOff)]; ++x2) {
            }
            if (x2 != this.w) break;
            --this.h;
        }
        for (x = 0; x < this.w; ++x) {
            for (y = 0; y < this.h && workOccupied[x + this.xOff + this.occW * (y + this.yOff)]; ++y) {
            }
            if (y != this.h) break;
            ++this.xOff;
            --x;
            --this.w;
        }
        for (x = this.w - 1; x >= 0; --x) {
            for (y = 0; y < this.h && workOccupied[x + this.xOff + this.occW * (y + this.yOff)]; ++y) {
            }
            if (y != this.h) break;
            --this.w;
        }
        return false;
    }
}

