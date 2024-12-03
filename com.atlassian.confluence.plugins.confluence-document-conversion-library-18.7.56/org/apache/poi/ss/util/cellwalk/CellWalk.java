/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.util.cellwalk;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.cellwalk.CellHandler;
import org.apache.poi.ss.util.cellwalk.CellWalkContext;

public class CellWalk {
    private final Sheet sheet;
    private final CellRangeAddress range;
    private boolean traverseEmptyCells;

    public CellWalk(Sheet sheet, CellRangeAddress range) {
        this.sheet = sheet;
        this.range = range;
        this.traverseEmptyCells = false;
    }

    public boolean isTraverseEmptyCells() {
        return this.traverseEmptyCells;
    }

    public void setTraverseEmptyCells(boolean traverseEmptyCells) {
        this.traverseEmptyCells = traverseEmptyCells;
    }

    public void traverse(CellHandler handler) {
        int firstRow = this.range.getFirstRow();
        int lastRow = this.range.getLastRow();
        int firstColumn = this.range.getFirstColumn();
        int lastColumn = this.range.getLastColumn();
        int width = lastColumn - firstColumn + 1;
        SimpleCellWalkContext ctx = new SimpleCellWalkContext();
        ctx.rowNumber = firstRow;
        while (ctx.rowNumber <= lastRow) {
            Row currentRow = this.sheet.getRow(ctx.rowNumber);
            if (currentRow != null) {
                ctx.colNumber = firstColumn;
                while (ctx.colNumber <= lastColumn) {
                    Cell currentCell = currentRow.getCell(ctx.colNumber);
                    if (currentCell != null && (!this.isEmpty(currentCell) || this.traverseEmptyCells)) {
                        long rowSize = Math.multiplyExact((long)Math.subtractExact(ctx.rowNumber, firstRow), (long)width);
                        ctx.ordinalNumber = Math.addExact(rowSize, (long)(ctx.colNumber - firstColumn + 1));
                        handler.onCell(currentCell, ctx);
                    }
                    ++ctx.colNumber;
                }
            }
            ++ctx.rowNumber;
        }
    }

    private boolean isEmpty(Cell cell) {
        return cell.getCellType() == CellType.BLANK;
    }

    private static class SimpleCellWalkContext
    implements CellWalkContext {
        private long ordinalNumber;
        private int rowNumber;
        private int colNumber;

        private SimpleCellWalkContext() {
        }

        @Override
        public long getOrdinalNumber() {
            return this.ordinalNumber;
        }

        @Override
        public int getRowNumber() {
            return this.rowNumber;
        }

        @Override
        public int getColumnNumber() {
            return this.colNumber;
        }
    }
}

