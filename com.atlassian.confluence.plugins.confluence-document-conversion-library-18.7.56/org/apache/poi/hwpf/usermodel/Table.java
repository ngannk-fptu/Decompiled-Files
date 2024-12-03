/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.util.ArrayList;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.TableRow;

public final class Table
extends Range {
    private ArrayList<TableRow> _rows;
    private boolean _rowsFound;
    private int _tableLevel;

    Table(int startIdxInclusive, int endIdxExclusive, Range parent, int levelNum) {
        super(startIdxInclusive, endIdxExclusive, parent);
        this._tableLevel = levelNum;
        this.initRows();
    }

    public TableRow getRow(int index) {
        this.initRows();
        return this._rows.get(index);
    }

    public int getTableLevel() {
        return this._tableLevel;
    }

    private void initRows() {
        if (this._rowsFound) {
            return;
        }
        this._rows = new ArrayList();
        int rowStart = 0;
        int numParagraphs = this.numParagraphs();
        for (int rowEnd = 0; rowEnd < numParagraphs; ++rowEnd) {
            Paragraph startRowP = this.getParagraph(rowStart);
            Paragraph endRowP = this.getParagraph(rowEnd);
            if (!endRowP.isTableRowEnd() || endRowP.getTableLevel() != this._tableLevel) continue;
            this._rows.add(new TableRow(startRowP.getStartOffset(), endRowP.getEndOffset(), this, this._tableLevel));
            rowStart = rowEnd;
        }
        this._rowsFound = true;
    }

    public int numRows() {
        this.initRows();
        return this._rows.size();
    }

    @Override
    protected void reset() {
        this._rowsFound = false;
    }

    public int type() {
        return 5;
    }
}

