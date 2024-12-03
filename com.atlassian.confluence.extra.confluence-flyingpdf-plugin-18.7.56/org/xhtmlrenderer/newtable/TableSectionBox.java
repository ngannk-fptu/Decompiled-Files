/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.newtable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.newtable.ColumnData;
import org.xhtmlrenderer.newtable.RowData;
import org.xhtmlrenderer.newtable.TableBox;
import org.xhtmlrenderer.newtable.TableCellBox;
import org.xhtmlrenderer.newtable.TableRowBox;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.RenderingContext;

public class TableSectionBox
extends BlockBox {
    private List _grid = new ArrayList();
    private boolean _needCellWidthCalc;
    private boolean _needCellRecalc;
    private boolean _footer;
    private boolean _header;
    private boolean _capturedOriginalAbsY;
    private int _originalAbsY;

    @Override
    public BlockBox copyOf() {
        TableSectionBox result = new TableSectionBox();
        result.setStyle(this.getStyle());
        result.setElement(this.getElement());
        return result;
    }

    public List getGrid() {
        return this._grid;
    }

    public void setGrid(List grid) {
        this._grid = grid;
    }

    public void extendGridToColumnCount(int columnCount) {
        for (RowData row : this._grid) {
            row.extendToColumnCount(columnCount);
        }
    }

    public void splitColumn(int pos) {
        for (RowData row : this._grid) {
            row.splitColumn(pos);
        }
    }

    public void recalcCells(LayoutContext c) {
        int cRow = 0;
        this._grid.clear();
        this.ensureChildren(c);
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            TableRowBox row = (TableRowBox)i.next();
            row.ensureChildren(c);
            Iterator j = row.getChildIterator();
            while (j.hasNext()) {
                TableCellBox cell = (TableCellBox)j.next();
                this.addCell(row, cell, cRow);
            }
            ++cRow;
        }
    }

    public void calcBorders(LayoutContext c) {
        this.ensureChildren(c);
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            TableRowBox row = (TableRowBox)i.next();
            row.ensureChildren(c);
            Iterator j = row.getChildIterator();
            while (j.hasNext()) {
                TableCellBox cell = (TableCellBox)j.next();
                cell.calcCollapsedBorder(c);
            }
        }
    }

    public TableCellBox cellAt(int row, int col) {
        if (row >= this._grid.size()) {
            return null;
        }
        RowData rowData = (RowData)this._grid.get(row);
        if (col >= rowData.getRow().size()) {
            return null;
        }
        return (TableCellBox)rowData.getRow().get(col);
    }

    private void setCellAt(int row, int col, TableCellBox cell) {
        ((RowData)this._grid.get(row)).getRow().set(col, cell);
    }

    private void ensureRows(int numRows) {
        int nCols = this.getTable().numEffCols();
        for (int nRows = this._grid.size(); nRows < numRows; ++nRows) {
            RowData row = new RowData();
            row.extendToColumnCount(nCols);
            this._grid.add(row);
        }
    }

    private TableBox getTable() {
        return (TableBox)this.getParent();
    }

    @Override
    protected void layoutChildren(LayoutContext c, int contentStart) {
        if (this.isNeedCellRecalc()) {
            this.recalcCells(c);
            this.setNeedCellRecalc(false);
        }
        if (this.isNeedCellWidthCalc()) {
            this.setCellWidths(c);
            this.setNeedCellWidthCalc(false);
        }
        super.layoutChildren(c, contentStart);
    }

    private void addCell(TableRowBox row, TableCellBox cell, int cRow) {
        int cCol;
        int rSpan = cell.getStyle().getRowSpan();
        int cSpan = cell.getStyle().getColSpan();
        List columns = this.getTable().getColumns();
        int nCols = columns.size();
        this.ensureRows(cRow + rSpan);
        for (cCol = 0; cCol < nCols && this.cellAt(cRow, cCol) != null; ++cCol) {
        }
        int col = cCol;
        TableCellBox set = cell;
        while (cSpan > 0) {
            while (cCol >= this.getTable().getColumns().size()) {
                this.getTable().appendColumn(1);
            }
            ColumnData cData = (ColumnData)columns.get(cCol);
            if (cSpan < cData.getSpan()) {
                this.getTable().splitColumn(cCol, cSpan);
            }
            cData = (ColumnData)columns.get(cCol);
            int currentSpan = cData.getSpan();
            for (int r = 0; r < rSpan; ++r) {
                if (this.cellAt(cRow + r, cCol) != null) continue;
                this.setCellAt(cRow + r, cCol, set);
            }
            ++cCol;
            cSpan -= currentSpan;
            set = TableCellBox.SPANNING_CELL;
        }
        cell.setRow(cRow);
        cell.setCol(this.getTable().effColToCol(col));
    }

    @Override
    public void reset(LayoutContext c) {
        super.reset(c);
        this._grid.clear();
        this.setNeedCellWidthCalc(true);
        this.setNeedCellRecalc(true);
        this.setCapturedOriginalAbsY(false);
    }

    void setCellWidths(LayoutContext c) {
        int[] columnPos = this.getTable().getColumnPos();
        for (RowData row : this._grid) {
            List cols = row.getRow();
            int hspacing = this.getTable().getStyle().getBorderHSpacing(c);
            for (int j = 0; j < cols.size(); ++j) {
                TableCellBox cell = (TableCellBox)cols.get(j);
                if (cell == null || cell == TableCellBox.SPANNING_CELL) continue;
                int endCol = j;
                for (int cspan = cell.getStyle().getColSpan(); cspan > 0 && endCol < cols.size(); cspan -= this.getTable().spanOfEffCol(endCol), ++endCol) {
                }
                int w = columnPos[endCol] - columnPos[j] - hspacing;
                cell.setLayoutWidth(c, w);
                cell.setX(columnPos[j] + hspacing);
            }
        }
    }

    @Override
    public boolean isAutoHeight() {
        return true;
    }

    public int numRows() {
        return this._grid.size();
    }

    @Override
    protected boolean isSkipWhenCollapsingMargins() {
        return true;
    }

    @Override
    public void paintBorder(RenderingContext c) {
    }

    @Override
    public void paintBackground(RenderingContext c) {
    }

    public TableRowBox getLastRow() {
        if (this.getChildCount() > 0) {
            return (TableRowBox)this.getChild(this.getChildCount() - 1);
        }
        return null;
    }

    boolean isNeedCellWidthCalc() {
        return this._needCellWidthCalc;
    }

    void setNeedCellWidthCalc(boolean needCellWidthCalc) {
        this._needCellWidthCalc = needCellWidthCalc;
    }

    private boolean isNeedCellRecalc() {
        return this._needCellRecalc;
    }

    private void setNeedCellRecalc(boolean needCellRecalc) {
        this._needCellRecalc = needCellRecalc;
    }

    @Override
    public void layout(LayoutContext c, int contentStart) {
        boolean running;
        boolean bl = running = c.isPrint() && (this.isHeader() || this.isFooter()) && this.getTable().getStyle().isPaginateTable();
        if (running) {
            c.setNoPageBreak(c.getNoPageBreak() + 1);
        }
        super.layout(c, contentStart);
        if (running) {
            c.setNoPageBreak(c.getNoPageBreak() - 1);
        }
    }

    public boolean isFooter() {
        return this._footer;
    }

    public void setFooter(boolean footer) {
        this._footer = footer;
    }

    public boolean isHeader() {
        return this._header;
    }

    public void setHeader(boolean header) {
        this._header = header;
    }

    public boolean isCapturedOriginalAbsY() {
        return this._capturedOriginalAbsY;
    }

    public void setCapturedOriginalAbsY(boolean capturedOriginalAbsY) {
        this._capturedOriginalAbsY = capturedOriginalAbsY;
    }

    public int getOriginalAbsY() {
        return this._originalAbsY;
    }

    public void setOriginalAbsY(int originalAbsY) {
        this._originalAbsY = originalAbsY;
    }
}

