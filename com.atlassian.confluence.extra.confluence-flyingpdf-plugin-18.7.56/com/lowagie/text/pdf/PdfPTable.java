/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Image;
import com.lowagie.text.LargeElement;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPRow;
import com.lowagie.text.pdf.PdfPTableEvent;
import com.lowagie.text.pdf.events.PdfPTableEventForwarder;
import java.util.ArrayList;

public class PdfPTable
implements LargeElement {
    public static final int BASECANVAS = 0;
    public static final int BACKGROUNDCANVAS = 1;
    public static final int LINECANVAS = 2;
    public static final int TEXTCANVAS = 3;
    protected ArrayList<PdfPRow> rows = new ArrayList();
    protected float totalHeight = 0.0f;
    protected PdfPCell[] currentRow;
    protected int currentRowIdx = 0;
    protected PdfPCell defaultCell = new PdfPCell((Phrase)null);
    protected float totalWidth = 0.0f;
    protected float[] relativeWidths;
    protected float[] absoluteWidths;
    protected PdfPTableEvent tableEvent;
    protected int headerRows;
    protected float widthPercentage = 80.0f;
    private int horizontalAlignment = 1;
    private boolean skipFirstHeader = false;
    private boolean skipLastFooter = false;
    protected boolean isColspan = false;
    protected int runDirection = 0;
    private boolean lockedWidth = false;
    private boolean splitRows = true;
    protected float spacingBefore;
    protected float spacingAfter;
    private boolean[] extendLastRow = new boolean[]{false, false};
    private boolean headersInEvent;
    private boolean splitLate = true;
    private boolean keepTogether;
    protected boolean complete = true;
    private int footerRows;
    protected boolean rowCompleted = true;

    protected PdfPTable() {
    }

    public PdfPTable(float[] relativeWidths) {
        if (relativeWidths == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("the.widths.array.in.pdfptable.constructor.can.not.be.null"));
        }
        if (relativeWidths.length == 0) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.widths.array.in.pdfptable.constructor.can.not.have.zero.length"));
        }
        this.relativeWidths = new float[relativeWidths.length];
        System.arraycopy(relativeWidths, 0, this.relativeWidths, 0, relativeWidths.length);
        this.absoluteWidths = new float[relativeWidths.length];
        this.calculateWidths();
        this.currentRow = new PdfPCell[this.absoluteWidths.length];
        this.keepTogether = false;
    }

    public PdfPTable(int numColumns) {
        if (numColumns <= 0) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.number.of.columns.in.pdfptable.constructor.must.be.greater.than.zero"));
        }
        this.relativeWidths = new float[numColumns];
        for (int k = 0; k < numColumns; ++k) {
            this.relativeWidths[k] = 1.0f;
        }
        this.absoluteWidths = new float[this.relativeWidths.length];
        this.calculateWidths();
        this.currentRow = new PdfPCell[this.absoluteWidths.length];
        this.keepTogether = false;
    }

    public PdfPTable(PdfPTable table) {
        int k;
        this.copyFormat(table);
        for (k = 0; k < this.currentRow.length && table.currentRow[k] != null; ++k) {
            this.currentRow[k] = new PdfPCell(table.currentRow[k]);
        }
        for (k = 0; k < table.rows.size(); ++k) {
            PdfPRow row = table.rows.get(k);
            if (row != null) {
                row = new PdfPRow(row);
            }
            this.rows.add(row);
        }
    }

    public static PdfPTable shallowCopy(PdfPTable table) {
        PdfPTable nt = new PdfPTable();
        nt.copyFormat(table);
        return nt;
    }

    protected void copyFormat(PdfPTable sourceTable) {
        this.relativeWidths = new float[sourceTable.getNumberOfColumns()];
        this.absoluteWidths = new float[sourceTable.getNumberOfColumns()];
        System.arraycopy(sourceTable.relativeWidths, 0, this.relativeWidths, 0, this.getNumberOfColumns());
        System.arraycopy(sourceTable.absoluteWidths, 0, this.absoluteWidths, 0, this.getNumberOfColumns());
        this.totalWidth = sourceTable.totalWidth;
        this.totalHeight = sourceTable.totalHeight;
        this.currentRowIdx = 0;
        this.tableEvent = sourceTable.tableEvent;
        this.runDirection = sourceTable.runDirection;
        this.defaultCell = new PdfPCell(sourceTable.defaultCell);
        this.currentRow = new PdfPCell[sourceTable.currentRow.length];
        this.isColspan = sourceTable.isColspan;
        this.splitRows = sourceTable.splitRows;
        this.spacingAfter = sourceTable.spacingAfter;
        this.spacingBefore = sourceTable.spacingBefore;
        this.headerRows = sourceTable.headerRows;
        this.footerRows = sourceTable.footerRows;
        this.lockedWidth = sourceTable.lockedWidth;
        this.extendLastRow = sourceTable.extendLastRow;
        this.headersInEvent = sourceTable.headersInEvent;
        this.widthPercentage = sourceTable.widthPercentage;
        this.splitLate = sourceTable.splitLate;
        this.skipFirstHeader = sourceTable.skipFirstHeader;
        this.skipLastFooter = sourceTable.skipLastFooter;
        this.horizontalAlignment = sourceTable.horizontalAlignment;
        this.keepTogether = sourceTable.keepTogether;
        this.complete = sourceTable.complete;
    }

    public void setWidths(float[] relativeWidths) throws DocumentException {
        if (relativeWidths.length != this.getNumberOfColumns()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("wrong.number.of.columns"));
        }
        this.relativeWidths = new float[relativeWidths.length];
        System.arraycopy(relativeWidths, 0, this.relativeWidths, 0, relativeWidths.length);
        this.absoluteWidths = new float[relativeWidths.length];
        this.totalHeight = 0.0f;
        this.calculateWidths();
        this.calculateHeights(true);
    }

    public void setWidths(int[] relativeWidths) throws DocumentException {
        float[] tb = new float[relativeWidths.length];
        for (int k = 0; k < relativeWidths.length; ++k) {
            tb[k] = relativeWidths[k];
        }
        this.setWidths(tb);
    }

    protected void calculateWidths() {
        int k;
        if (this.totalWidth <= 0.0f) {
            return;
        }
        float total = 0.0f;
        int numCols = this.getNumberOfColumns();
        for (k = 0; k < numCols; ++k) {
            total += this.relativeWidths[k];
        }
        for (k = 0; k < numCols; ++k) {
            this.absoluteWidths[k] = this.totalWidth * this.relativeWidths[k] / total;
        }
    }

    public void setTotalWidth(float totalWidth) {
        if (this.totalWidth == totalWidth) {
            return;
        }
        this.totalWidth = totalWidth;
        this.totalHeight = 0.0f;
        this.calculateWidths();
        this.calculateHeights(true);
    }

    public void setTotalWidth(float[] columnWidth) throws DocumentException {
        if (columnWidth.length != this.getNumberOfColumns()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("wrong.number.of.columns"));
        }
        this.totalWidth = 0.0f;
        for (float v : columnWidth) {
            this.totalWidth += v;
        }
        this.setWidths(columnWidth);
    }

    public void setWidthPercentage(float[] columnWidth, Rectangle pageSize) throws DocumentException {
        if (columnWidth.length != this.getNumberOfColumns()) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("wrong.number.of.columns"));
        }
        float totalWidth = 0.0f;
        for (float v : columnWidth) {
            totalWidth += v;
        }
        this.widthPercentage = totalWidth / (pageSize.getRight() - pageSize.getLeft()) * 100.0f;
        this.setWidths(columnWidth);
    }

    public float getTotalWidth() {
        return this.totalWidth;
    }

    public float calculateHeights(boolean firsttime) {
        if (this.totalWidth <= 0.0f) {
            return 0.0f;
        }
        this.totalHeight = 0.0f;
        for (int k = 0; k < this.rows.size(); ++k) {
            this.totalHeight += this.getRowHeight(k, firsttime);
        }
        return this.totalHeight;
    }

    public void calculateHeightsFast() {
        this.calculateHeights(false);
    }

    public PdfPCell getDefaultCell() {
        return this.defaultCell;
    }

    public void addCell(PdfPCell cell) {
        int rdir;
        this.rowCompleted = false;
        PdfPCell ncell = new PdfPCell(cell);
        int colspan = ncell.getColspan();
        colspan = Math.max(colspan, 1);
        colspan = Math.min(colspan, this.currentRow.length - this.currentRowIdx);
        ncell.setColspan(colspan);
        if (colspan != 1) {
            this.isColspan = true;
        }
        if ((rdir = ncell.getRunDirection()) == 0) {
            ncell.setRunDirection(this.runDirection);
        }
        this.skipColsWithRowspanAbove();
        boolean cellAdded = false;
        if (this.currentRowIdx < this.currentRow.length) {
            this.currentRow[this.currentRowIdx] = ncell;
            this.currentRowIdx += colspan;
            cellAdded = true;
        }
        this.skipColsWithRowspanAbove();
        while (this.currentRowIdx >= this.currentRow.length) {
            int numCols = this.getNumberOfColumns();
            if (this.runDirection == 3) {
                PdfPCell[] rtlRow = new PdfPCell[numCols];
                int rev = this.currentRow.length;
                for (int k = 0; k < this.currentRow.length; ++k) {
                    PdfPCell rcell = this.currentRow[k];
                    int cspan = rcell.getColspan();
                    rtlRow[rev -= cspan] = rcell;
                    k += cspan - 1;
                }
                this.currentRow = rtlRow;
            }
            PdfPRow row = new PdfPRow(this.currentRow);
            if (this.totalWidth > 0.0f) {
                row.setWidths(this.absoluteWidths);
                this.totalHeight += row.getMaxHeights();
            }
            this.rows.add(row);
            this.currentRow = new PdfPCell[numCols];
            this.currentRowIdx = 0;
            this.skipColsWithRowspanAbove();
            this.rowCompleted = true;
        }
        if (!cellAdded) {
            this.currentRow[this.currentRowIdx] = ncell;
            this.currentRowIdx += colspan;
        }
    }

    private void skipColsWithRowspanAbove() {
        int direction = 1;
        if (this.runDirection == 3) {
            direction = -1;
        }
        while (this.rowSpanAbove(this.rows.size(), this.currentRowIdx)) {
            this.currentRowIdx += direction;
        }
    }

    PdfPCell obtainCell(int row, int col) {
        PdfPCell[] cells = this.rows.get(row).getCells();
        for (int i = 0; i < cells.length; ++i) {
            if (cells[i] == null || col < i || col >= i + cells[i].getColspan()) continue;
            return cells[i];
        }
        return null;
    }

    boolean rowSpanAbove(int currRow, int currCol) {
        if (currCol >= this.getNumberOfColumns() || currCol < 0 || currRow < 1) {
            return false;
        }
        int row = currRow - 1;
        PdfPRow aboveRow = this.rows.get(row);
        if (aboveRow == null) {
            return false;
        }
        PdfPCell aboveCell = this.obtainCell(row, currCol);
        while (aboveCell == null && row > 0) {
            if ((aboveRow = this.rows.get(--row)) == null) {
                return false;
            }
            aboveCell = this.obtainCell(row, currCol);
        }
        int distance = currRow - row;
        if (aboveCell == null) {
            int col = currCol - 1;
            aboveCell = this.obtainCell(row, col);
            while (aboveCell == null && row > 0) {
                aboveCell = this.obtainCell(row, --col);
            }
            return aboveCell != null && aboveCell.getRowspan() > distance;
        }
        if (aboveCell.getRowspan() == 1 && distance > 1) {
            int col = currCol - 1;
            aboveRow = this.rows.get(row + 1);
            --distance;
            aboveCell = aboveRow.getCells()[col];
            while (aboveCell == null && col > 0) {
                aboveCell = aboveRow.getCells()[--col];
            }
        }
        return aboveCell != null && aboveCell.getRowspan() > distance;
    }

    public void addCell(String text) {
        this.addCell(new Phrase(text));
    }

    public void addCell(PdfPTable table) {
        this.defaultCell.setTable(table);
        this.addCell(this.defaultCell);
        this.defaultCell.setTable(null);
    }

    public void addCell(Image image) {
        this.defaultCell.setImage(image);
        this.addCell(this.defaultCell);
        this.defaultCell.setImage(null);
    }

    public void addCell(Phrase phrase) {
        this.defaultCell.setPhrase(phrase);
        this.addCell(this.defaultCell);
        this.defaultCell.setPhrase(null);
    }

    public float writeSelectedRows(int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte[] canvases) {
        return this.writeSelectedRows(0, -1, rowStart, rowEnd, xPos, yPos, canvases);
    }

    public float writeSelectedRows(int colStart, int colEnd, int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte[] canvases) {
        if (this.totalWidth <= 0.0f) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("the.table.width.must.be.greater.than.zero"));
        }
        int totalRows = this.rows.size();
        if (rowStart < 0) {
            rowStart = 0;
        }
        if (rowStart >= (rowEnd = rowEnd < 0 ? totalRows : Math.min(rowEnd, totalRows))) {
            return yPos;
        }
        int totalCols = this.getNumberOfColumns();
        colStart = colStart < 0 ? 0 : Math.min(colStart, totalCols);
        colEnd = colEnd < 0 ? totalCols : Math.min(colEnd, totalCols);
        float yPosStart = yPos;
        for (int k = rowStart; k < rowEnd; ++k) {
            PdfPRow row = this.rows.get(k);
            if (row == null) continue;
            row.writeCells(colStart, colEnd, xPos, yPos, canvases);
            yPos -= row.getMaxHeights();
        }
        if (this.tableEvent != null && colStart == 0 && colEnd == totalCols) {
            float[] heights = new float[rowEnd - rowStart + 1];
            heights[0] = yPosStart;
            for (int k = rowStart; k < rowEnd; ++k) {
                PdfPRow row = this.rows.get(k);
                float hr = 0.0f;
                if (row != null) {
                    hr = row.getMaxHeights();
                }
                heights[k - rowStart + 1] = heights[k - rowStart] - hr;
            }
            this.tableEvent.tableLayout(this, this.getEventWidths(xPos, rowStart, rowEnd, this.headersInEvent), heights, this.headersInEvent ? this.headerRows : 0, rowStart, canvases);
        }
        return yPos;
    }

    public float writeSelectedRows(int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte canvas) {
        return this.writeSelectedRows(0, -1, rowStart, rowEnd, xPos, yPos, canvas);
    }

    public float writeSelectedRows(int colStart, int colEnd, int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte canvas) {
        boolean clip;
        int totalCols = this.getNumberOfColumns();
        colStart = colStart < 0 ? 0 : Math.min(colStart, totalCols);
        colEnd = colEnd < 0 ? totalCols : Math.min(colEnd, totalCols);
        boolean bl = clip = colStart != 0 || colEnd != totalCols;
        if (clip) {
            float w = 0.0f;
            for (int k = colStart; k < colEnd; ++k) {
                w += this.absoluteWidths[k];
            }
            canvas.saveState();
            float lx = colStart == 0 ? 10000.0f : 0.0f;
            float rx = colEnd == totalCols ? 10000.0f : 0.0f;
            canvas.rectangle(xPos - lx, -10000.0f, w + lx + rx, 20000.0f);
            canvas.clip();
            canvas.newPath();
        }
        PdfContentByte[] canvases = PdfPTable.beginWritingRows(canvas);
        float y = this.writeSelectedRows(colStart, colEnd, rowStart, rowEnd, xPos, yPos, canvases);
        PdfPTable.endWritingRows(canvases);
        if (clip) {
            canvas.restoreState();
        }
        return y;
    }

    public static PdfContentByte[] beginWritingRows(PdfContentByte canvas) {
        return new PdfContentByte[]{canvas, canvas.getDuplicate(), canvas.getDuplicate(), canvas.getDuplicate()};
    }

    public static void endWritingRows(PdfContentByte[] canvases) {
        PdfContentByte canvas = canvases[0];
        canvas.saveState();
        canvas.add(canvases[1]);
        canvas.restoreState();
        canvas.saveState();
        canvas.setLineCap(2);
        canvas.resetRGBColorStroke();
        canvas.add(canvases[2]);
        canvas.restoreState();
        canvas.add(canvases[3]);
    }

    public int size() {
        return this.rows.size();
    }

    public float getTotalHeight() {
        return this.totalHeight;
    }

    public float getRowHeight(int idx) {
        return this.getRowHeight(idx, false);
    }

    public float getRowHeight(int idx, boolean firsttime) {
        if (this.totalWidth <= 0.0f || idx < 0 || idx >= this.rows.size()) {
            return 0.0f;
        }
        PdfPRow row = this.rows.get(idx);
        if (row == null) {
            return 0.0f;
        }
        if (firsttime) {
            row.setWidths(this.absoluteWidths);
        }
        float height = row.getMaxHeights();
        for (int i = 0; i < this.relativeWidths.length; ++i) {
            if (!this.rowSpanAbove(idx, i)) continue;
            int rs = 1;
            while (this.rowSpanAbove(idx - rs, i)) {
                ++rs;
            }
            PdfPRow tmprow = this.rows.get(idx - rs);
            PdfPCell cell = tmprow.getCells()[i];
            float tmp = 0.0f;
            if (cell != null && cell.getRowspan() == rs + 1) {
                tmp = cell.getMaxHeight();
                while (rs > 0) {
                    tmp -= this.getRowHeight(idx - rs);
                    --rs;
                }
            }
            if (!(tmp > height)) continue;
            height = tmp;
        }
        row.setMaxHeights(height);
        return height;
    }

    public float getRowspanHeight(int rowIndex, int cellIndex) {
        if (this.totalWidth <= 0.0f || rowIndex < 0 || rowIndex >= this.rows.size()) {
            return 0.0f;
        }
        PdfPRow row = this.rows.get(rowIndex);
        if (row == null || cellIndex >= row.getCells().length) {
            return 0.0f;
        }
        PdfPCell cell = row.getCells()[cellIndex];
        if (cell == null) {
            return 0.0f;
        }
        float rowspanHeight = 0.0f;
        for (int j = 0; j < cell.getRowspan(); ++j) {
            rowspanHeight += this.getRowHeight(rowIndex + j);
        }
        return rowspanHeight;
    }

    public float getHeaderHeight() {
        float total = 0.0f;
        int size = Math.min(this.rows.size(), this.headerRows);
        for (int k = 0; k < size; ++k) {
            PdfPRow row = this.rows.get(k);
            if (row == null) continue;
            total += row.getMaxHeights();
        }
        return total;
    }

    public float getFooterHeight() {
        float total = 0.0f;
        int start = Math.max(0, this.headerRows - this.footerRows);
        int size = Math.min(this.rows.size(), this.headerRows);
        for (int k = start; k < size; ++k) {
            PdfPRow row = this.rows.get(k);
            if (row == null) continue;
            total += row.getMaxHeights();
        }
        return total;
    }

    public boolean deleteRow(int rowNumber) {
        PdfPRow row;
        if (rowNumber < 0 || rowNumber >= this.rows.size()) {
            return false;
        }
        if (this.totalWidth > 0.0f && (row = this.rows.get(rowNumber)) != null) {
            this.totalHeight -= row.getMaxHeights();
        }
        this.rows.remove(rowNumber);
        if (rowNumber < this.headerRows) {
            --this.headerRows;
            if (rowNumber >= this.headerRows - this.footerRows) {
                --this.footerRows;
            }
        }
        return true;
    }

    public boolean deleteLastRow() {
        return this.deleteRow(this.rows.size() - 1);
    }

    public void deleteBodyRows() {
        ArrayList<PdfPRow> rows2 = new ArrayList<PdfPRow>();
        for (int k = 0; k < this.headerRows; ++k) {
            rows2.add(this.rows.get(k));
        }
        this.rows = rows2;
        this.totalHeight = 0.0f;
        if (this.totalWidth > 0.0f) {
            this.totalHeight = this.getHeaderHeight();
        }
    }

    public int getNumberOfColumns() {
        return this.relativeWidths.length;
    }

    public int getHeaderRows() {
        return this.headerRows;
    }

    public void setHeaderRows(int headerRows) {
        if (headerRows < 0) {
            headerRows = 0;
        }
        this.headerRows = headerRows;
    }

    @Override
    public ArrayList<Element> getChunks() {
        return new ArrayList<Element>();
    }

    @Override
    public int type() {
        return 23;
    }

    @Override
    public boolean isContent() {
        return true;
    }

    @Override
    public boolean isNestable() {
        return true;
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

    public float getWidthPercentage() {
        return this.widthPercentage;
    }

    public void setWidthPercentage(float widthPercentage) {
        this.widthPercentage = widthPercentage;
    }

    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public PdfPRow getRow(int idx) {
        return this.rows.get(idx);
    }

    public ArrayList<PdfPRow> getRows() {
        return this.rows;
    }

    public ArrayList<PdfPRow> getRows(int start, int end) {
        ArrayList<PdfPRow> list = new ArrayList<PdfPRow>();
        if (start < 0 || end > this.size()) {
            return list;
        }
        PdfPRow firstRow = this.adjustCellsInRow(start, end);
        int colIndex = 0;
        while (colIndex < this.getNumberOfColumns()) {
            int rowIndex = start;
            while (this.rowSpanAbove(rowIndex--, colIndex)) {
                PdfPCell replaceCell;
                PdfPRow row = this.getRow(rowIndex);
                if (row == null || (replaceCell = row.getCells()[colIndex]) == null) continue;
                firstRow.getCells()[colIndex] = new PdfPCell(replaceCell);
                float extra = 0.0f;
                int stop = Math.min(rowIndex + replaceCell.getRowspan(), end);
                for (int j = start + 1; j < stop; ++j) {
                    extra += this.getRowHeight(j);
                }
                firstRow.setExtraHeight(colIndex, extra);
                float diff = this.getRowspanHeight(rowIndex, colIndex) - this.getRowHeight(start) - extra;
                firstRow.getCells()[colIndex].consumeHeight(diff);
            }
            PdfPCell cell = firstRow.getCells()[colIndex];
            if (cell == null) {
                ++colIndex;
                continue;
            }
            colIndex += cell.getColspan();
        }
        list.add(firstRow);
        for (int i = start + 1; i < end; ++i) {
            list.add(this.adjustCellsInRow(i, end));
        }
        return list;
    }

    protected PdfPRow adjustCellsInRow(int start, int end) {
        PdfPRow row = new PdfPRow(this.getRow(start));
        row.initExtraHeights();
        PdfPCell[] cells = row.getCells();
        for (int i = 0; i < cells.length; ++i) {
            PdfPCell cell = cells[i];
            if (cell == null || cell.getRowspan() == 1) continue;
            int stop = Math.min(end, start + cell.getRowspan());
            float extra = 0.0f;
            for (int k = start + 1; k < stop; ++k) {
                extra += this.getRowHeight(k);
            }
            row.setExtraHeight(i, extra);
        }
        return row;
    }

    public void setTableEvent(PdfPTableEvent event) {
        if (event == null) {
            this.tableEvent = null;
        } else if (this.tableEvent == null) {
            this.tableEvent = event;
        } else if (this.tableEvent instanceof PdfPTableEventForwarder) {
            ((PdfPTableEventForwarder)this.tableEvent).addTableEvent(event);
        } else {
            PdfPTableEventForwarder forward = new PdfPTableEventForwarder();
            forward.addTableEvent(this.tableEvent);
            forward.addTableEvent(event);
            this.tableEvent = forward;
        }
    }

    public PdfPTableEvent getTableEvent() {
        return this.tableEvent;
    }

    public float[] getAbsoluteWidths() {
        return this.absoluteWidths;
    }

    float[][] getEventWidths(float xPos, int firstRow, int lastRow, boolean includeHeaders) {
        if (includeHeaders) {
            firstRow = Math.max(firstRow, this.headerRows);
            lastRow = Math.max(lastRow, this.headerRows);
        }
        float[][] widths = new float[(includeHeaders ? this.headerRows : 0) + lastRow - firstRow][];
        if (this.isColspan) {
            int n = 0;
            if (includeHeaders) {
                for (int k = 0; k < this.headerRows; ++k) {
                    PdfPRow row = this.rows.get(k);
                    if (row == null) {
                        ++n;
                        continue;
                    }
                    widths[n++] = row.getEventWidth(xPos);
                }
            }
            while (firstRow < lastRow) {
                PdfPRow row = this.rows.get(firstRow);
                if (row == null) {
                    ++n;
                } else {
                    widths[n++] = row.getEventWidth(xPos);
                }
                ++firstRow;
            }
        } else {
            int k;
            int numCols = this.getNumberOfColumns();
            float[] width = new float[numCols + 1];
            width[0] = xPos;
            for (k = 0; k < numCols; ++k) {
                width[k + 1] = width[k] + this.absoluteWidths[k];
            }
            for (k = 0; k < widths.length; ++k) {
                widths[k] = width;
            }
        }
        return widths;
    }

    public boolean isSkipFirstHeader() {
        return this.skipFirstHeader;
    }

    public boolean isSkipLastFooter() {
        return this.skipLastFooter;
    }

    public void setSkipFirstHeader(boolean skipFirstHeader) {
        this.skipFirstHeader = skipFirstHeader;
    }

    public void setSkipLastFooter(boolean skipLastFooter) {
        this.skipLastFooter = skipLastFooter;
    }

    public void setRunDirection(int runDirection) {
        switch (runDirection) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                this.runDirection = runDirection;
                break;
            }
            default: {
                throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.run.direction.1", runDirection));
            }
        }
    }

    public int getRunDirection() {
        return this.runDirection;
    }

    public boolean isLockedWidth() {
        return this.lockedWidth;
    }

    public void setLockedWidth(boolean lockedWidth) {
        this.lockedWidth = lockedWidth;
    }

    public boolean isSplitRows() {
        return this.splitRows;
    }

    public void setSplitRows(boolean splitRows) {
        this.splitRows = splitRows;
    }

    public void setSpacingBefore(float spacing) {
        this.spacingBefore = spacing;
    }

    public void setSpacingAfter(float spacing) {
        this.spacingAfter = spacing;
    }

    public float spacingBefore() {
        return this.spacingBefore;
    }

    public float spacingAfter() {
        return this.spacingAfter;
    }

    public boolean isExtendLastRow() {
        return this.extendLastRow[0];
    }

    public void setExtendLastRow(boolean extendLastRows) {
        this.extendLastRow[0] = extendLastRows;
        this.extendLastRow[1] = extendLastRows;
    }

    public void setExtendLastRow(boolean extendLastRows, boolean extendFinalRow) {
        this.extendLastRow[0] = extendLastRows;
        this.extendLastRow[1] = extendFinalRow;
    }

    public boolean isExtendLastRow(boolean newPageFollows) {
        if (newPageFollows) {
            return this.extendLastRow[0];
        }
        return this.extendLastRow[1];
    }

    public boolean isHeadersInEvent() {
        return this.headersInEvent;
    }

    public void setHeadersInEvent(boolean headersInEvent) {
        this.headersInEvent = headersInEvent;
    }

    public boolean isSplitLate() {
        return this.splitLate;
    }

    public void setSplitLate(boolean splitLate) {
        this.splitLate = splitLate;
    }

    public void setKeepTogether(boolean keepTogether) {
        this.keepTogether = keepTogether;
    }

    public boolean getKeepTogether() {
        return this.keepTogether;
    }

    public int getFooterRows() {
        return this.footerRows;
    }

    public void setFooterRows(int footerRows) {
        if (footerRows < 0) {
            footerRows = 0;
        }
        this.footerRows = footerRows;
    }

    public void completeRow() {
        while (!this.rowCompleted) {
            this.addCell(this.defaultCell);
        }
    }

    @Override
    public void flushContent() {
        this.deleteBodyRows();
        this.setSkipFirstHeader(true);
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }

    @Override
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}

