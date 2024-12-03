/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.newtable;

import java.util.ArrayList;
import java.util.List;
import org.xhtmlrenderer.newtable.TableCellBox;

public class RowData {
    private List _row = new ArrayList();

    public List getRow() {
        return this._row;
    }

    public void extendToColumnCount(int columnCount) {
        while (this._row.size() < columnCount) {
            this._row.add(null);
        }
    }

    public void splitColumn(int pos) {
        TableCellBox current = (TableCellBox)this._row.get(pos);
        this._row.add(pos + 1, current == null ? null : TableCellBox.SPANNING_CELL);
    }
}

