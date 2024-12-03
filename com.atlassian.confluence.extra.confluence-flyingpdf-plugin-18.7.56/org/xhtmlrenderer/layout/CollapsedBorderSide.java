/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import org.xhtmlrenderer.newtable.CollapsedBorderValue;
import org.xhtmlrenderer.newtable.TableCellBox;

public class CollapsedBorderSide
implements Comparable {
    private TableCellBox _cell;
    private int _side;

    public CollapsedBorderSide(TableCellBox cell, int side) {
        this._side = side;
        this._cell = cell;
    }

    public TableCellBox getCell() {
        return this._cell;
    }

    public void setCell(TableCellBox cell) {
        this._cell = cell;
    }

    public int getSide() {
        return this._side;
    }

    public void setSide(int side) {
        this._side = side;
    }

    public int compareTo(Object obj) {
        CollapsedBorderSide c1 = this;
        CollapsedBorderSide c2 = (CollapsedBorderSide)obj;
        CollapsedBorderValue v1 = null;
        CollapsedBorderValue v2 = null;
        switch (c1._side) {
            case 1: {
                v1 = c1._cell.getCollapsedBorderTop();
                break;
            }
            case 8: {
                v1 = c1._cell.getCollapsedBorderRight();
                break;
            }
            case 4: {
                v1 = c1._cell.getCollapsedBorderBottom();
                break;
            }
            case 2: {
                v1 = c1._cell.getCollapsedBorderLeft();
            }
        }
        switch (c2._side) {
            case 1: {
                v2 = c2._cell.getCollapsedBorderTop();
                break;
            }
            case 8: {
                v2 = c2._cell.getCollapsedBorderRight();
                break;
            }
            case 4: {
                v2 = c2._cell.getCollapsedBorderBottom();
                break;
            }
            case 2: {
                v2 = c2._cell.getCollapsedBorderLeft();
            }
        }
        CollapsedBorderValue result = TableCellBox.compareBorders(v1, v2, true);
        if (result == null) {
            return 0;
        }
        return result == v1 ? 1 : -1;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CollapsedBorderSide)) {
            return false;
        }
        CollapsedBorderSide that = (CollapsedBorderSide)o;
        if (this._side != that._side) {
            return false;
        }
        return this._cell.equals(that._cell);
    }

    public int hashCode() {
        int result = this._cell.hashCode();
        result = 31 * result + this._side;
        return result;
    }
}

