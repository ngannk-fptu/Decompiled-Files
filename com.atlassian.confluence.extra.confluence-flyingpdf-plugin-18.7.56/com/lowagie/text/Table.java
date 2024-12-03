/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.LargeElement;
import com.lowagie.text.Phrase;
import com.lowagie.text.Row;
import com.lowagie.text.SimpleCell;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.TableRectangle;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.alignment.VerticalAlignment;
import com.lowagie.text.alignment.WithHorizontalAlignment;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class Table
extends TableRectangle
implements LargeElement,
WithHorizontalAlignment {
    private int columns;
    private ArrayList<Row> rows = new ArrayList();
    private Point curPosition = new Point(0, 0);
    private Cell defaultCell = new Cell(true);
    private int lastHeaderRow = -1;
    private int alignment = 1;
    private float cellpadding;
    private float cellspacing;
    private float width = 80.0f;
    private boolean locked = false;
    private float[] widths;
    private boolean mTableInserted = false;
    protected boolean autoFillEmptyCells = false;
    boolean tableFitsPage = false;
    boolean cellsFitPage = false;
    float offset = Float.NaN;
    protected boolean convert2pdfptable = false;
    protected boolean notAddedYet = true;
    protected boolean complete = true;

    public Table(int columns) throws BadElementException {
        this(columns, 1);
    }

    public Table(int columns, int rows) throws BadElementException {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.setBorder(15);
        this.setBorderWidth(1.0f);
        this.defaultCell.setBorder(15);
        if (columns <= 0) {
            throw new BadElementException(MessageLocalization.getComposedMessage("a.table.should.have.at.least.1.column"));
        }
        this.columns = columns;
        for (int i = 0; i < rows; ++i) {
            this.rows.add(new Row(columns));
        }
        this.curPosition = new Point(0, 0);
        this.widths = new float[columns];
        float width = 100.0f / (float)columns;
        for (int i = 0; i < columns; ++i) {
            this.widths[i] = width;
        }
    }

    public Table(Table t) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.cloneNonPositionParameters(t);
        this.columns = t.columns;
        this.rows = t.rows;
        this.curPosition = t.curPosition;
        this.defaultCell = t.defaultCell;
        this.lastHeaderRow = t.lastHeaderRow;
        this.alignment = t.alignment;
        this.cellpadding = t.cellpadding;
        this.cellspacing = t.cellspacing;
        this.width = t.width;
        this.widths = t.widths;
        this.autoFillEmptyCells = t.autoFillEmptyCells;
        this.tableFitsPage = t.tableFitsPage;
        this.cellsFitPage = t.cellsFitPage;
        this.offset = t.offset;
        this.convert2pdfptable = t.convert2pdfptable;
    }

    @Override
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch (DocumentException de) {
            return false;
        }
    }

    @Override
    public int type() {
        return 22;
    }

    @Override
    public ArrayList<Element> getChunks() {
        return new ArrayList<Element>();
    }

    @Override
    public boolean isNestable() {
        return true;
    }

    public int getColumns() {
        return this.columns;
    }

    public int size() {
        return this.rows.size();
    }

    public Dimension getDimension() {
        return new Dimension(this.columns, this.size());
    }

    public Cell getDefaultCell() {
        return this.defaultCell;
    }

    public void setDefaultCell(Cell value) {
        this.defaultCell = value;
    }

    public int getLastHeaderRow() {
        return this.lastHeaderRow;
    }

    public void setLastHeaderRow(int value) {
        this.lastHeaderRow = value;
    }

    public int endHeaders() {
        this.lastHeaderRow = this.curPosition.x - 1;
        return this.lastHeaderRow;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int value) {
        this.alignment = value;
    }

    public void setAlignment(String alignment) {
        if ("Left".equalsIgnoreCase(alignment)) {
            this.alignment = 0;
            return;
        }
        if ("right".equalsIgnoreCase(alignment)) {
            this.alignment = 2;
            return;
        }
        this.alignment = 1;
    }

    @Override
    public void setHorizontalAlignment(HorizontalAlignment alignment) {
        if (alignment == null) {
            return;
        }
        this.alignment = alignment.getId();
    }

    public float getPadding() {
        return this.cellpadding;
    }

    public void setPadding(float value) {
        this.cellpadding = value;
    }

    public float getSpacing() {
        return this.cellspacing;
    }

    public void setSpacing(float value) {
        this.cellspacing = value;
    }

    public void setAutoFillEmptyCells(boolean aDoAutoFill) {
        this.autoFillEmptyCells = aDoAutoFill;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public float[] getProportionalWidths() {
        return this.widths;
    }

    public void setWidths(float[] widths) throws BadElementException {
        if (widths.length != this.columns) {
            throw new BadElementException(MessageLocalization.getComposedMessage("wrong.number.of.columns"));
        }
        float hundredPercent = 0.0f;
        for (int i = 0; i < this.columns; ++i) {
            hundredPercent += widths[i];
        }
        this.widths[this.columns - 1] = 100.0f;
        for (int i = 0; i < this.columns - 1; ++i) {
            float width;
            this.widths[i] = width = 100.0f * widths[i] / hundredPercent;
            int n = this.columns - 1;
            this.widths[n] = this.widths[n] - width;
        }
    }

    public void setWidths(int[] widths) throws DocumentException {
        float[] tb = new float[widths.length];
        for (int k = 0; k < widths.length; ++k) {
            tb[k] = widths[k];
        }
        this.setWidths(tb);
    }

    public boolean isTableFitsPage() {
        return this.tableFitsPage;
    }

    public void setTableFitsPage(boolean fitPage) {
        this.tableFitsPage = fitPage;
        if (fitPage) {
            this.setCellsFitPage(true);
        }
    }

    public boolean isCellsFitPage() {
        return this.cellsFitPage;
    }

    public void setCellsFitPage(boolean fitPage) {
        this.cellsFitPage = fitPage;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getOffset() {
        return this.offset;
    }

    public boolean isConvert2pdfptable() {
        return this.convert2pdfptable;
    }

    public void setConvert2pdfptable(boolean convert2pdfptable) {
        this.convert2pdfptable = convert2pdfptable;
    }

    public void addCell(Cell aCell, int row, int column) throws BadElementException {
        this.addCell(aCell, new Point(row, column));
    }

    public void addCell(Cell aCell, Point aLocation) throws BadElementException {
        if (aCell == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("addcell.cell.has.null.value"));
        }
        if (aLocation == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("addcell.point.has.null.value"));
        }
        if (aCell.isTable()) {
            this.insertTable((Table)aCell.getElements().next(), aLocation);
        }
        if (aLocation.x < 0) {
            throw new BadElementException(MessageLocalization.getComposedMessage("row.coordinate.of.location.must.be.gt.eq.0"));
        }
        if (aLocation.y <= 0 && aLocation.y > this.columns) {
            throw new BadElementException(MessageLocalization.getComposedMessage("column.coordinate.of.location.must.be.gt.eq.0.and.lt.nr.of.columns"));
        }
        if (!this.isValidLocation(aCell, aLocation)) {
            throw new BadElementException(MessageLocalization.getComposedMessage("adding.a.cell.at.the.location.1.2.with.a.colspan.of.3.and.a.rowspan.of.4.is.illegal.beyond.boundaries.overlapping", String.valueOf(aLocation.x), String.valueOf(aLocation.y), String.valueOf(aCell.getColspan()), String.valueOf(aCell.getRowspan())));
        }
        if (aCell.getBorder() == -1) {
            aCell.setBorder(this.defaultCell.getBorder());
        }
        aCell.fill();
        this.placeCell(this.rows, aCell, aLocation);
        this.setCurrentLocationToNextValidPosition(aLocation);
    }

    public void addCell(Cell cell) {
        try {
            this.addCell(cell, this.curPosition);
        }
        catch (BadElementException badElementException) {
            // empty catch block
        }
    }

    public void addCell(Phrase content) throws BadElementException {
        this.addCell(content, this.curPosition);
    }

    public void addCell(Phrase content, Point location) throws BadElementException {
        Cell cell = new Cell(content);
        cell.setBorder(this.defaultCell.getBorder());
        cell.setBorderWidth(this.defaultCell.getBorderWidth());
        cell.setBorderColor(this.defaultCell.getBorderColor());
        cell.setBackgroundColor(this.defaultCell.getBackgroundColor());
        Optional<HorizontalAlignment> optionalHorizontalAlignment = HorizontalAlignment.of(this.defaultCell.getHorizontalAlignment());
        cell.setHorizontalAlignment(optionalHorizontalAlignment.orElse(HorizontalAlignment.UNDEFINED));
        Optional<VerticalAlignment> optionalVerticalAlignment = VerticalAlignment.of(this.defaultCell.getVerticalAlignment());
        cell.setVerticalAlignment(optionalVerticalAlignment.orElse(VerticalAlignment.UNDEFINED));
        cell.setColspan(this.defaultCell.getColspan());
        cell.setRowspan(this.defaultCell.getRowspan());
        this.addCell(cell, location);
    }

    public void addCell(String content) throws BadElementException {
        this.addCell(new Phrase(content), this.curPosition);
    }

    public void addCell(String content, Point location) throws BadElementException {
        this.addCell(new Phrase(content), location);
    }

    public void insertTable(Table aTable) {
        if (aTable == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("inserttable.table.has.null.value"));
        }
        this.insertTable(aTable, this.curPosition);
    }

    public void insertTable(Table aTable, int row, int column) {
        if (aTable == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("inserttable.table.has.null.value"));
        }
        this.insertTable(aTable, new Point(row, column));
    }

    public void insertTable(Table aTable, Point aLocation) {
        if (aTable == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("inserttable.table.has.null.value"));
        }
        if (aLocation == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("inserttable.point.has.null.value"));
        }
        this.mTableInserted = true;
        aTable.complete();
        if (aLocation.y > this.columns) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("inserttable.wrong.columnposition.1.of.location.max.eq.2", String.valueOf(aLocation.y), String.valueOf(this.columns)));
        }
        int rowCount = aLocation.x + 1 - this.rows.size();
        if (rowCount > 0) {
            for (int i = 0; i < rowCount; ++i) {
                this.rows.add(new Row(this.columns));
            }
        }
        this.rows.get(aLocation.x).setElement(aTable, aLocation.y);
        this.setCurrentLocationToNextValidPosition(aLocation);
    }

    public void addColumns(int aColumns) {
        int j;
        ArrayList<Row> newRows = new ArrayList<Row>(this.rows.size());
        int newColumns = this.columns + aColumns;
        for (int i = 0; i < this.rows.size(); ++i) {
            Row row = new Row(newColumns);
            for (j = 0; j < this.columns; ++j) {
                row.setElement(this.rows.get(i).getCell(j), j);
            }
            for (j = this.columns; j < newColumns && i < this.curPosition.x; ++j) {
                row.setElement(null, j);
            }
            newRows.add(row);
        }
        float[] newWidths = new float[newColumns];
        System.arraycopy(this.widths, 0, newWidths, 0, this.columns);
        for (j = this.columns; j < newColumns; ++j) {
            newWidths[j] = 0.0f;
        }
        this.columns = newColumns;
        this.widths = newWidths;
        this.rows = newRows;
    }

    public void deleteColumn(int column) throws BadElementException {
        float[] newWidths = new float[--this.columns];
        System.arraycopy(this.widths, 0, newWidths, 0, column);
        System.arraycopy(this.widths, column + 1, newWidths, column, this.columns - column);
        this.setWidths(newWidths);
        System.arraycopy(this.widths, 0, newWidths, 0, this.columns);
        this.widths = newWidths;
        int size = this.rows.size();
        for (int i = 0; i < size; ++i) {
            Row row = this.rows.get(i);
            row.deleteColumn(column);
            this.rows.set(i, row);
        }
        if (column == this.columns) {
            this.curPosition.setLocation(this.curPosition.x + 1, 0);
        }
    }

    public boolean deleteRow(int row) {
        if (row < 0 || row >= this.rows.size()) {
            return false;
        }
        this.rows.remove(row);
        this.curPosition.setLocation(this.curPosition.x - 1, this.curPosition.y);
        return true;
    }

    public void deleteAllRows() {
        this.rows.clear();
        this.rows.add(new Row(this.columns));
        this.curPosition.setLocation(0, 0);
        this.lastHeaderRow = -1;
    }

    public boolean deleteLastRow() {
        return this.deleteRow(this.rows.size() - 1);
    }

    public void complete() {
        if (this.mTableInserted) {
            this.mergeInsertedTables();
            this.mTableInserted = false;
        }
        if (this.autoFillEmptyCells) {
            this.fillEmptyMatrixCells();
        }
    }

    public TableRectangle getElement(int row, int column) {
        return this.rows.get(row).getCell(column);
    }

    private void mergeInsertedTables() {
        Table lDummyTable;
        int i;
        int j;
        int[] lDummyWidths = new int[this.columns];
        float[][] lDummyColumnWidths = new float[this.columns][];
        int[] lDummyHeights = new int[this.rows.size()];
        boolean isTable = false;
        int lTotalRows = 0;
        int lTotalColumns = 0;
        for (j = 0; j < this.columns; ++j) {
            int lNewMaxColumns = 1;
            float[] tmpWidths = null;
            for (i = 0; i < this.rows.size(); ++i) {
                if (!(this.rows.get(i).getCell(j) instanceof Table)) continue;
                isTable = true;
                lDummyTable = (Table)this.rows.get(i).getCell(j);
                if (tmpWidths == null) {
                    tmpWidths = lDummyTable.widths;
                    lNewMaxColumns = tmpWidths.length;
                    continue;
                }
                int cols = lDummyTable.getDimension().width;
                float[] tmpWidthsN = new float[cols * tmpWidths.length];
                float tpW = 0.0f;
                float btW = 0.0f;
                float totW = 0.0f;
                int tpI = 0;
                int btI = 0;
                int totI = 0;
                tpW += tmpWidths[0];
                btW += lDummyTable.widths[0];
                while (tpI < tmpWidths.length && btI < cols) {
                    if (btW > tpW) {
                        tmpWidthsN[totI] = tpW - totW;
                        if (++tpI < tmpWidths.length) {
                            tpW += tmpWidths[tpI];
                        }
                    } else {
                        tmpWidthsN[totI] = btW - totW;
                        ++btI;
                        if ((double)Math.abs(btW - tpW) < 1.0E-4 && ++tpI < tmpWidths.length) {
                            tpW += tmpWidths[tpI];
                        }
                        if (btI < cols) {
                            btW += lDummyTable.widths[btI];
                        }
                    }
                    totW += tmpWidthsN[totI];
                    ++totI;
                }
                tmpWidths = new float[totI];
                System.arraycopy(tmpWidthsN, 0, tmpWidths, 0, totI);
                lNewMaxColumns = totI;
            }
            lDummyColumnWidths[j] = tmpWidths;
            lTotalColumns += lNewMaxColumns;
            lDummyWidths[j] = lNewMaxColumns;
        }
        for (i = 0; i < this.rows.size(); ++i) {
            int lNewMaxRows = 1;
            for (j = 0; j < this.columns; ++j) {
                if (!(this.rows.get(i).getCell(j) instanceof Table)) continue;
                isTable = true;
                lDummyTable = (Table)this.rows.get(i).getCell(j);
                if (lDummyTable.getDimension().height <= lNewMaxRows) continue;
                lNewMaxRows = lDummyTable.getDimension().height;
            }
            lTotalRows += lNewMaxRows;
            lDummyHeights[i] = lNewMaxRows;
        }
        if (lTotalColumns != this.columns || lTotalRows != this.rows.size() || isTable) {
            float[] lNewWidths = new float[lTotalColumns];
            int lDummy = 0;
            for (int tel = 0; tel < this.widths.length; ++tel) {
                if (lDummyWidths[tel] != 1) {
                    for (int tel2 = 0; tel2 < lDummyWidths[tel]; ++tel2) {
                        lNewWidths[lDummy] = this.widths[tel] * lDummyColumnWidths[tel][tel2] / 100.0f;
                        ++lDummy;
                    }
                    continue;
                }
                lNewWidths[lDummy] = this.widths[tel];
                ++lDummy;
            }
            ArrayList<Row> newRows = new ArrayList<Row>(lTotalRows);
            for (i = 0; i < lTotalRows; ++i) {
                newRows.add(new Row(lTotalColumns));
            }
            int lDummyRow = 0;
            for (i = 0; i < this.rows.size(); ++i) {
                int lDummyColumn = 0;
                for (j = 0; j < this.columns; ++j) {
                    if (this.rows.get(i).getCell(j) instanceof Table) {
                        lDummyTable = (Table)this.rows.get(i).getCell(j);
                        int[] colMap = new int[lDummyTable.widths.length + 1];
                        int ct = 0;
                        for (int cb = 0; cb < lDummyTable.widths.length; ++cb) {
                            colMap[cb] = lDummyColumn + ct;
                            float wb = lDummyTable.widths[cb];
                            float wt = 0.0f;
                            while (ct < lDummyWidths[j]) {
                                int n = ct++;
                                if (!((double)Math.abs(wb - (wt += lDummyColumnWidths[j][n])) < 1.0E-4)) continue;
                            }
                        }
                        colMap[cb] = lDummyColumn + ct;
                        for (int k = 0; k < lDummyTable.getDimension().height; ++k) {
                            for (int l = 0; l < lDummyTable.getDimension().width; ++l) {
                                TableRectangle lDummyElement = lDummyTable.getElement(k, l);
                                if (lDummyElement == null) continue;
                                int col = lDummyColumn + l;
                                if (lDummyElement instanceof Cell) {
                                    Cell lDummyC = (Cell)lDummyElement;
                                    col = colMap[l];
                                    int ot = colMap[l + lDummyC.getColspan()];
                                    lDummyC.setColspan(ot - col);
                                }
                                newRows.get(k + lDummyRow).addElement(lDummyElement, col);
                            }
                        }
                    } else {
                        TableRectangle aElement = this.getElement(i, j);
                        if (aElement instanceof Cell) {
                            ((Cell)aElement).setRowspan(((Cell)this.rows.get(i).getCell(j)).getRowspan() + lDummyHeights[i] - 1);
                            ((Cell)aElement).setColspan(((Cell)this.rows.get(i).getCell(j)).getColspan() + lDummyWidths[j] - 1);
                            this.placeCell(newRows, (Cell)aElement, new Point(lDummyRow, lDummyColumn));
                        }
                    }
                    lDummyColumn += lDummyWidths[j];
                }
                lDummyRow += lDummyHeights[i];
            }
            this.columns = lTotalColumns;
            this.rows = newRows;
            this.widths = lNewWidths;
        }
    }

    private void fillEmptyMatrixCells() {
        try {
            for (int i = 0; i < this.rows.size(); ++i) {
                for (int j = 0; j < this.columns; ++j) {
                    if (this.rows.get(i).isReserved(j)) continue;
                    this.addCell(this.defaultCell, new Point(i, j));
                }
            }
        }
        catch (BadElementException bee) {
            throw new ExceptionConverter(bee);
        }
    }

    private boolean isValidLocation(Cell aCell, Point aLocation) {
        if (aLocation.x < this.rows.size()) {
            if (aLocation.y + aCell.getColspan() > this.columns) {
                return false;
            }
            int difx = this.rows.size() - aLocation.x > aCell.getRowspan() ? aCell.getRowspan() : this.rows.size() - aLocation.x;
            int dify = this.columns - aLocation.y > aCell.getColspan() ? aCell.getColspan() : this.columns - aLocation.y;
            for (int i = aLocation.x; i < aLocation.x + difx; ++i) {
                for (int j = aLocation.y; j < aLocation.y + dify; ++j) {
                    if (!this.rows.get(i).isReserved(j)) continue;
                    return false;
                }
            }
        } else {
            return aLocation.y + aCell.getColspan() <= this.columns;
        }
        return true;
    }

    private void assumeTableDefaults(Cell aCell) {
        Optional<Enum> of;
        if (aCell.getBorder() == -1) {
            aCell.setBorder(this.defaultCell.getBorder());
        }
        if (aCell.getBorderWidth() == -1.0f) {
            aCell.setBorderWidth(this.defaultCell.getBorderWidth());
        }
        if (aCell.getBorderColor() == null) {
            aCell.setBorderColor(this.defaultCell.getBorderColor());
        }
        if (aCell.getBackgroundColor() == null) {
            aCell.setBackgroundColor(this.defaultCell.getBackgroundColor());
        }
        if (aCell.getHorizontalAlignment() == -1) {
            of = HorizontalAlignment.of(this.defaultCell.getHorizontalAlignment());
            aCell.setHorizontalAlignment(of.orElse(HorizontalAlignment.UNDEFINED));
        }
        if (aCell.getVerticalAlignment() == -1) {
            of = VerticalAlignment.of(this.defaultCell.getVerticalAlignment());
            aCell.setVerticalAlignment((VerticalAlignment)((Object)of.orElse((HorizontalAlignment)((Object)VerticalAlignment.UNDEFINED))));
        }
    }

    private void placeCell(ArrayList<Row> someRows, Cell aCell, Point aPosition) {
        Row row;
        int i;
        int rowCount = aPosition.x + aCell.getRowspan() - someRows.size();
        this.assumeTableDefaults(aCell);
        if (aPosition.x + aCell.getRowspan() > someRows.size()) {
            for (i = 0; i < rowCount; ++i) {
                row = new Row(this.columns);
                someRows.add(row);
            }
        }
        for (i = aPosition.x + 1; i < aPosition.x + aCell.getRowspan(); ++i) {
            if (someRows.get(i).reserve(aPosition.y, aCell.getColspan())) continue;
            throw new RuntimeException(MessageLocalization.getComposedMessage("addcell.error.in.reserve"));
        }
        row = someRows.get(aPosition.x);
        row.addElement(aCell, aPosition.y);
    }

    private void setCurrentLocationToNextValidPosition(Point aLocation) {
        int i = aLocation.x;
        int j = aLocation.y;
        do {
            if (j + 1 == this.columns) {
                ++i;
                j = 0;
                continue;
            }
            ++j;
        } while (i < this.rows.size() && j < this.columns && this.rows.get(i).isReserved(j));
        this.curPosition = new Point(i, j);
    }

    public float[] getWidths(float left, float totalWidth) {
        float[] w = new float[this.columns + 1];
        float wPercentage = this.locked ? 100.0f * this.width / totalWidth : this.width;
        switch (this.alignment) {
            case 0: {
                w[0] = left;
                break;
            }
            case 2: {
                w[0] = left + totalWidth * (100.0f - wPercentage) / 100.0f;
                break;
            }
            default: {
                w[0] = left + totalWidth * (100.0f - wPercentage) / 200.0f;
            }
        }
        totalWidth = totalWidth * wPercentage / 100.0f;
        for (int i = 1; i < this.columns; ++i) {
            w[i] = w[i - 1] + this.widths[i - 1] * totalWidth / 100.0f;
        }
        w[this.columns] = w[0] + totalWidth;
        return w;
    }

    public Iterator iterator() {
        return this.rows.iterator();
    }

    public PdfPTable createPdfPTable() throws BadElementException {
        if (!this.convert2pdfptable) {
            throw new BadElementException(MessageLocalization.getComposedMessage("no.error.just.an.old.style.table"));
        }
        this.setAutoFillEmptyCells(true);
        this.complete();
        PdfPTable pdfptable = new PdfPTable(this.widths);
        pdfptable.setComplete(this.complete);
        if (this.isNotAddedYet()) {
            pdfptable.setSkipFirstHeader(true);
        }
        SimpleTable t_evt = new SimpleTable();
        t_evt.cloneNonPositionParameters(this);
        t_evt.setCellspacing(this.cellspacing);
        pdfptable.setTableEvent(t_evt);
        pdfptable.setHeaderRows(this.lastHeaderRow + 1);
        pdfptable.setSplitLate(this.cellsFitPage);
        pdfptable.setKeepTogether(this.tableFitsPage);
        if (!Float.isNaN(this.offset)) {
            pdfptable.setSpacingBefore(this.offset);
        }
        pdfptable.setHorizontalAlignment(this.alignment);
        if (this.locked) {
            pdfptable.setTotalWidth(this.width);
            pdfptable.setLockedWidth(true);
        } else {
            pdfptable.setWidthPercentage(this.width);
        }
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            Row row = (Row)iterator.next();
            for (int i = 0; i < row.getColumns(); ++i) {
                PdfPCell pcell;
                TableRectangle cell = row.getCell(i);
                if (cell == null) continue;
                if (cell instanceof Table) {
                    pcell = new PdfPCell(((Table)cell).createPdfPTable());
                } else if (cell instanceof Cell) {
                    pcell = ((Cell)cell).createPdfPCell();
                    pcell.setPadding(this.cellpadding + this.cellspacing / 2.0f);
                    SimpleCell c_evt = new SimpleCell(false);
                    c_evt.cloneNonPositionParameters((Cell)cell);
                    c_evt.setSpacing(this.cellspacing * 2.0f);
                    pcell.setCellEvent(c_evt);
                } else {
                    pcell = new PdfPCell();
                }
                pdfptable.addCell(pcell);
            }
        }
        return pdfptable;
    }

    public boolean isNotAddedYet() {
        return this.notAddedYet;
    }

    public void setNotAddedYet(boolean notAddedYet) {
        this.notAddedYet = notAddedYet;
    }

    @Override
    public void flushContent() {
        this.setNotAddedYet(false);
        ArrayList<Row> headerRows = new ArrayList<Row>();
        for (int i = 0; i < this.getLastHeaderRow() + 1; ++i) {
            headerRows.add(this.rows.get(i));
        }
        this.rows = headerRows;
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }

    @Override
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Cell getDefaultLayout() {
        return this.getDefaultCell();
    }

    public void setDefaultLayout(Cell value) {
        this.defaultCell = value;
    }
}

