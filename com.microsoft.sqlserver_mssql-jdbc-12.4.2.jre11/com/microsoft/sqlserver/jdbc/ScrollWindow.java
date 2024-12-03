/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.RowType;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.microsoft.sqlserver.jdbc.TDSReaderMark;
import java.io.Serializable;
import java.util.logging.Level;

final class ScrollWindow
implements Serializable {
    private static final long serialVersionUID = 3028807583846251111L;
    private transient TDSReaderMark[] rowMark;
    private boolean[] updatedRow;
    private boolean[] deletedRow;
    private RowType[] rowType;
    private int size = 0;
    private int maxRows = 0;
    private int currentRow;

    final int getMaxRows() {
        return this.maxRows;
    }

    final int getRow() {
        return this.currentRow;
    }

    ScrollWindow(int size) {
        this.setSize(size);
        this.reset();
    }

    private void setSize(int size) {
        assert (this.size != size);
        this.size = size;
        this.maxRows = size;
        this.rowMark = new TDSReaderMark[size];
        this.updatedRow = new boolean[size];
        this.deletedRow = new boolean[size];
        this.rowType = new RowType[size];
        for (int i = 0; i < size; ++i) {
            this.rowType[i] = RowType.UNKNOWN;
        }
    }

    final void clear() {
        for (int i = 0; i < this.rowMark.length; ++i) {
            this.rowMark[i] = null;
            this.updatedRow[i] = false;
            this.deletedRow[i] = false;
            this.rowType[i] = RowType.UNKNOWN;
        }
        assert (this.size > 0);
        this.maxRows = this.size;
        this.reset();
    }

    final void reset() {
        this.currentRow = 0;
    }

    final void resize(int newSize) {
        assert (newSize > 0);
        if (newSize != this.size) {
            this.setSize(newSize);
        }
    }

    final String logCursorState() {
        return " currentRow:" + this.currentRow + " maxRows:" + this.maxRows;
    }

    final boolean next(SQLServerResultSet rs) throws SQLServerException {
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(rs.toString() + this.logCursorState());
        }
        assert (0 <= this.currentRow && this.currentRow <= this.maxRows + 1);
        if (this.maxRows + 1 == this.currentRow) {
            return false;
        }
        if (this.currentRow >= 1) {
            this.updatedRow[this.currentRow - 1] = rs.getUpdatedCurrentRow();
            this.deletedRow[this.currentRow - 1] = rs.getDeletedCurrentRow();
            this.rowType[this.currentRow - 1] = rs.getCurrentRowType();
        }
        ++this.currentRow;
        if (this.maxRows + 1 == this.currentRow) {
            rs.fetchBufferNext();
            return false;
        }
        if (null != this.rowMark[this.currentRow - 1]) {
            rs.fetchBufferReset(this.rowMark[this.currentRow - 1]);
            rs.setCurrentRowType(this.rowType[this.currentRow - 1]);
            rs.setUpdatedCurrentRow(this.updatedRow[this.currentRow - 1]);
            rs.setDeletedCurrentRow(this.deletedRow[this.currentRow - 1]);
            return true;
        }
        if (rs.fetchBufferNext()) {
            this.rowMark[this.currentRow - 1] = rs.fetchBufferMark();
            this.rowType[this.currentRow - 1] = rs.getCurrentRowType();
            if (SQLServerResultSet.logger.isLoggable(Level.FINEST)) {
                SQLServerResultSet.logger.finest(rs.toString() + " Set mark " + this.rowMark[this.currentRow - 1] + " for row " + this.currentRow + " of type " + this.rowType[this.currentRow - 1]);
            }
            return true;
        }
        this.maxRows = this.currentRow - 1;
        return false;
    }

    final void previous(SQLServerResultSet rs) throws SQLServerException {
        if (SQLServerResultSet.logger.isLoggable(Level.FINER)) {
            SQLServerResultSet.logger.finer(rs.toString() + this.logCursorState());
        }
        assert (0 <= this.currentRow && this.currentRow <= this.maxRows + 1);
        if (0 == this.currentRow) {
            return;
        }
        if (this.currentRow <= this.maxRows) {
            assert (this.currentRow >= 1);
            this.updatedRow[this.currentRow - 1] = rs.getUpdatedCurrentRow();
            this.deletedRow[this.currentRow - 1] = rs.getDeletedCurrentRow();
            this.rowType[this.currentRow - 1] = rs.getCurrentRowType();
        }
        --this.currentRow;
        if (0 == this.currentRow) {
            return;
        }
        assert (null != this.rowMark[this.currentRow - 1]);
        rs.fetchBufferReset(this.rowMark[this.currentRow - 1]);
        rs.setCurrentRowType(this.rowType[this.currentRow - 1]);
        rs.setUpdatedCurrentRow(this.updatedRow[this.currentRow - 1]);
        rs.setDeletedCurrentRow(this.deletedRow[this.currentRow - 1]);
    }
}

