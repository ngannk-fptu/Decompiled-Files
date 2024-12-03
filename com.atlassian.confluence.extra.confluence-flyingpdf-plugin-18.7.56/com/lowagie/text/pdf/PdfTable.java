/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Cell;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Row;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfCell;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfTable
extends Rectangle {
    private int columns;
    private ArrayList<PdfCell> headercells;
    private ArrayList<PdfCell> cells;
    protected Table table;
    protected float[] positions;

    PdfTable(Table table, float left, float right, float top) {
        super(left, top, right, top);
        this.table = table;
        table.complete();
        this.cloneNonPositionParameters(table);
        this.columns = table.getColumns();
        this.positions = table.getWidths(left, right - left);
        this.setLeft(this.positions[0]);
        this.setRight(this.positions[this.positions.length - 1]);
        this.headercells = new ArrayList();
        this.cells = new ArrayList();
        this.updateRowAdditionsInternal();
    }

    void updateRowAdditions() {
        this.table.complete();
        this.updateRowAdditionsInternal();
        this.table.deleteAllRows();
    }

    private void updateRowAdditionsInternal() {
        PdfCell currentCell;
        int prevRows = this.rows();
        int rowNumber = 0;
        int groupNumber = 0;
        int firstDataRow = this.table.getLastHeaderRow() + 1;
        ArrayList<PdfCell> newCells = new ArrayList<PdfCell>();
        int rows = this.table.size() + 1;
        float[] offsets = new float[rows];
        for (int i = 0; i < rows; ++i) {
            offsets[i] = this.getBottom();
        }
        Iterator rowIterator = this.table.iterator();
        while (rowIterator.hasNext()) {
            boolean groupChange = false;
            Row row = (Row)rowIterator.next();
            if (row.isEmpty()) {
                if (rowNumber < rows - 1 && offsets[rowNumber + 1] > offsets[rowNumber]) {
                    offsets[rowNumber + 1] = offsets[rowNumber];
                }
            } else {
                for (int i = 0; i < row.getColumns(); ++i) {
                    Cell cell;
                    block13: {
                        cell = (Cell)row.getCell(i);
                        if (cell == null) continue;
                        currentCell = new PdfCell(cell, rowNumber + prevRows, this.positions[i], this.positions[i + cell.getColspan()], offsets[rowNumber], this.cellspacing(), this.cellpadding());
                        if (rowNumber < firstDataRow) {
                            currentCell.setHeader();
                            this.headercells.add(currentCell);
                            if (!this.table.isNotAddedYet()) continue;
                        }
                        try {
                            if (offsets[rowNumber] - currentCell.getHeight() - this.cellpadding() < offsets[rowNumber + currentCell.rowspan()]) {
                                offsets[rowNumber + currentCell.rowspan()] = offsets[rowNumber] - currentCell.getHeight() - this.cellpadding();
                            }
                        }
                        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                            if (!(offsets[rowNumber] - currentCell.getHeight() < offsets[rows - 1])) break block13;
                            offsets[rows - 1] = offsets[rowNumber] - currentCell.getHeight();
                        }
                    }
                    currentCell.setGroupNumber(groupNumber);
                    groupChange |= cell.getGroupChange();
                    newCells.add(currentCell);
                }
            }
            ++rowNumber;
            if (!groupChange) continue;
            ++groupNumber;
        }
        int n = newCells.size();
        for (Object e : newCells) {
            currentCell = (PdfCell)e;
            try {
                currentCell.setBottom(offsets[currentCell.rownumber() - prevRows + currentCell.rowspan()]);
            }
            catch (ArrayIndexOutOfBoundsException aioobe) {
                currentCell.setBottom(offsets[rows - 1]);
            }
        }
        this.cells.addAll(newCells);
        this.setBottom(offsets[rows - 1]);
    }

    int rows() {
        return this.cells.isEmpty() ? 0 : this.cells.get(this.cells.size() - 1).rownumber() + 1;
    }

    @Override
    public int type() {
        return 22;
    }

    ArrayList<PdfCell> getHeaderCells() {
        return this.headercells;
    }

    boolean hasHeader() {
        return !this.headercells.isEmpty();
    }

    ArrayList<PdfCell> getCells() {
        return this.cells;
    }

    int columns() {
        return this.columns;
    }

    final float cellpadding() {
        return this.table.getPadding();
    }

    final float cellspacing() {
        return this.table.getSpacing();
    }

    public final boolean hasToFitPageTable() {
        return this.table.isTableFitsPage();
    }

    public final boolean hasToFitPageCells() {
        return this.table.isCellsFitPage();
    }

    public float getOffset() {
        return this.table.getOffset();
    }
}

