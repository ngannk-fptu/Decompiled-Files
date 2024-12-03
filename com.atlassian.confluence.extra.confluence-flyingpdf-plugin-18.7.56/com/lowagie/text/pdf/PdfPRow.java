/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;
import java.awt.Color;

public class PdfPRow {
    public static final float BOTTOM_LIMIT = -1.07374182E9f;
    public static final float RIGHT_LIMIT = 20000.0f;
    protected PdfPCell[] cells;
    protected float[] widths;
    protected float[] extraHeights;
    protected float maxHeight = 0.0f;
    protected boolean calculated = false;
    private int[] canvasesPos;

    public PdfPRow(PdfPCell[] cells) {
        this.cells = cells;
        this.widths = new float[cells.length];
        this.initExtraHeights();
    }

    public PdfPRow(PdfPRow row) {
        this.maxHeight = row.maxHeight;
        this.calculated = row.calculated;
        this.cells = new PdfPCell[row.cells.length];
        for (int k = 0; k < this.cells.length; ++k) {
            if (row.cells[k] == null) continue;
            this.cells[k] = new PdfPCell(row.cells[k]);
        }
        this.widths = new float[this.cells.length];
        System.arraycopy(row.widths, 0, this.widths, 0, this.cells.length);
        this.initExtraHeights();
    }

    public boolean setWidths(float[] widths) {
        if (widths.length != this.cells.length) {
            return false;
        }
        System.arraycopy(widths, 0, this.widths, 0, this.cells.length);
        float total = 0.0f;
        this.calculated = false;
        for (int k = 0; k < widths.length; ++k) {
            PdfPCell cell = this.cells[k];
            if (cell == null) {
                total += widths[k];
                continue;
            }
            cell.setLeft(total);
            int last = k + cell.getColspan();
            while (k < last) {
                total += widths[k];
                ++k;
            }
            --k;
            cell.setRight(total);
            cell.setTop(0.0f);
        }
        return true;
    }

    public void initExtraHeights() {
        this.extraHeights = new float[this.cells.length];
        for (int i = 0; i < this.extraHeights.length; ++i) {
            this.extraHeights[i] = 0.0f;
        }
    }

    public void setExtraHeight(int cell, float height) {
        if (cell < 0 || cell >= this.cells.length) {
            return;
        }
        this.extraHeights[cell] = height;
    }

    public float calculateHeights() {
        this.maxHeight = 0.0f;
        for (PdfPCell cell : this.cells) {
            float height = 0.0f;
            if (cell == null || !((height = cell.getMaxHeight()) > this.maxHeight) || cell.getRowspan() != 1) continue;
            this.maxHeight = height;
        }
        this.calculated = true;
        return this.maxHeight;
    }

    public void writeBorderAndBackground(float xPos, float yPos, float currentMaxHeight, PdfPCell cell, PdfContentByte[] canvases) {
        Color background = cell.getBackgroundColor();
        if (background != null || cell.hasBorders()) {
            float right = cell.getRight() + xPos;
            float top = cell.getTop() + yPos;
            float left = cell.getLeft() + xPos;
            float bottom = top - currentMaxHeight;
            if (background != null) {
                PdfContentByte backgr = canvases[1];
                backgr.setColorFill(background);
                backgr.rectangle(left, bottom, right - left, top - bottom);
                backgr.fill();
            }
            if (cell.hasBorders()) {
                Rectangle newRect = new Rectangle(left, bottom, right, top);
                newRect.cloneNonPositionParameters(cell);
                newRect.setBackgroundColor(null);
                PdfContentByte lineCanvas = canvases[2];
                lineCanvas.rectangle(newRect);
            }
        }
    }

    protected void saveAndRotateCanvases(PdfContentByte[] canvases, float a, float b, float c, float d, float e, float f) {
        int last = 4;
        if (this.canvasesPos == null) {
            this.canvasesPos = new int[last * 2];
        }
        for (int k = 0; k < last; ++k) {
            ByteBuffer bb = canvases[k].getInternalBuffer();
            this.canvasesPos[k * 2] = bb.size();
            canvases[k].saveState();
            canvases[k].concatCTM(a, b, c, d, e, f);
            this.canvasesPos[k * 2 + 1] = bb.size();
        }
    }

    protected void restoreCanvases(PdfContentByte[] canvases) {
        int last = 4;
        for (int k = 0; k < last; ++k) {
            ByteBuffer bb = canvases[k].getInternalBuffer();
            int p1 = bb.size();
            canvases[k].restoreState();
            if (p1 != this.canvasesPos[k * 2 + 1]) continue;
            bb.setSize(this.canvasesPos[k * 2]);
        }
    }

    public static float setColumn(ColumnText ct, float left, float bottom, float right, float top) {
        if (left > right) {
            right = left;
        }
        if (bottom > top) {
            top = bottom;
        }
        ct.setSimpleColumn(left, bottom, right, top);
        return top;
    }

    public void writeCells(int colStart, int colEnd, float xPos, float yPos, PdfContentByte[] canvases) {
        int newStart;
        if (!this.calculated) {
            this.calculateHeights();
        }
        colEnd = colEnd < 0 ? this.cells.length : Math.min(colEnd, this.cells.length);
        if (colStart < 0) {
            colStart = 0;
        }
        if (colStart >= colEnd) {
            return;
        }
        for (newStart = colStart; newStart >= 0 && this.cells[newStart] == null; --newStart) {
            if (newStart <= 0) continue;
            xPos -= this.widths[newStart - 1];
        }
        if (newStart < 0) {
            newStart = 0;
        }
        if (this.cells[newStart] != null) {
            xPos -= this.cells[newStart].getLeft();
        }
        for (int k = newStart; k < colEnd; ++k) {
            PdfPCellEvent evt;
            PdfPCell cell = this.cells[k];
            if (cell == null) continue;
            float currentMaxHeight = this.maxHeight + this.extraHeights[k];
            this.writeBorderAndBackground(xPos, yPos, currentMaxHeight, cell, canvases);
            Image img = cell.getImage();
            float tly = cell.getTop() + yPos - cell.getEffectivePaddingTop();
            if (cell.getHeight() <= currentMaxHeight) {
                switch (cell.getVerticalAlignment()) {
                    case 6: {
                        tly = cell.getTop() + yPos - currentMaxHeight + cell.getHeight() - cell.getEffectivePaddingTop();
                        break;
                    }
                    case 5: {
                        tly = cell.getTop() + yPos + (cell.getHeight() - currentMaxHeight) / 2.0f - cell.getEffectivePaddingTop();
                        break;
                    }
                }
            }
            if (img != null) {
                if (cell.getRotation() != 0) {
                    img = Image.getInstance(img);
                    img.setRotation(img.getImageRotation() + (float)((double)cell.getRotation() * Math.PI / 180.0));
                }
                boolean vf = false;
                if (cell.getHeight() > currentMaxHeight) {
                    img.scalePercent(100.0f);
                    float scale = (currentMaxHeight - cell.getEffectivePaddingTop() - cell.getEffectivePaddingBottom()) / img.getScaledHeight();
                    img.scalePercent(scale * 100.0f);
                    vf = true;
                }
                float left = cell.getLeft() + xPos + cell.getEffectivePaddingLeft();
                if (vf) {
                    switch (cell.getHorizontalAlignment()) {
                        case 1: {
                            left = xPos + (cell.getLeft() + cell.getEffectivePaddingLeft() + cell.getRight() - cell.getEffectivePaddingRight() - img.getScaledWidth()) / 2.0f;
                            break;
                        }
                        case 2: {
                            left = xPos + cell.getRight() - cell.getEffectivePaddingRight() - img.getScaledWidth();
                            break;
                        }
                    }
                    tly = cell.getTop() + yPos - cell.getEffectivePaddingTop();
                }
                img.setAbsolutePosition(left, tly - img.getScaledHeight());
                try {
                    canvases[3].addImage(img);
                }
                catch (DocumentException e) {
                    throw new ExceptionConverter(e);
                }
            }
            if (cell.getRotation() == 90 || cell.getRotation() == 270) {
                float netWidth = currentMaxHeight - cell.getEffectivePaddingTop() - cell.getEffectivePaddingBottom();
                float netHeight = cell.getWidth() - cell.getEffectivePaddingLeft() - cell.getEffectivePaddingRight();
                ColumnText ct = ColumnText.duplicate(cell.getColumn());
                ct.setCanvases(canvases);
                ct.setSimpleColumn(0.0f, 0.0f, netWidth + 0.001f, -netHeight);
                try {
                    ct.go(true);
                }
                catch (DocumentException e) {
                    throw new ExceptionConverter(e);
                }
                float calcHeight = -ct.getYLine();
                if (netWidth <= 0.0f || netHeight <= 0.0f) {
                    calcHeight = 0.0f;
                }
                if (calcHeight > 0.0f) {
                    float pivotX;
                    float pivotY;
                    if (cell.isUseDescender()) {
                        calcHeight -= ct.getDescender();
                    }
                    ct = ColumnText.duplicate(cell.getColumn());
                    ct.setCanvases(canvases);
                    ct.setSimpleColumn(-0.003f, -0.001f, netWidth + 0.003f, calcHeight);
                    if (cell.getRotation() == 90) {
                        pivotY = cell.getTop() + yPos - currentMaxHeight + cell.getEffectivePaddingBottom();
                        switch (cell.getVerticalAlignment()) {
                            case 6: {
                                pivotX = cell.getLeft() + xPos + cell.getWidth() - cell.getEffectivePaddingRight();
                                break;
                            }
                            case 5: {
                                pivotX = cell.getLeft() + xPos + (cell.getWidth() + cell.getEffectivePaddingLeft() - cell.getEffectivePaddingRight() + calcHeight) / 2.0f;
                                break;
                            }
                            default: {
                                pivotX = cell.getLeft() + xPos + cell.getEffectivePaddingLeft() + calcHeight;
                            }
                        }
                        this.saveAndRotateCanvases(canvases, 0.0f, 1.0f, -1.0f, 0.0f, pivotX, pivotY);
                    } else {
                        pivotY = cell.getTop() + yPos - cell.getEffectivePaddingTop();
                        switch (cell.getVerticalAlignment()) {
                            case 6: {
                                pivotX = cell.getLeft() + xPos + cell.getEffectivePaddingLeft();
                                break;
                            }
                            case 5: {
                                pivotX = cell.getLeft() + xPos + (cell.getWidth() + cell.getEffectivePaddingLeft() - cell.getEffectivePaddingRight() - calcHeight) / 2.0f;
                                break;
                            }
                            default: {
                                pivotX = cell.getLeft() + xPos + cell.getWidth() - cell.getEffectivePaddingRight() - calcHeight;
                            }
                        }
                        this.saveAndRotateCanvases(canvases, 0.0f, -1.0f, 1.0f, 0.0f, pivotX, pivotY);
                    }
                    try {
                        ct.go();
                    }
                    catch (DocumentException e) {
                        throw new ExceptionConverter(e);
                    }
                    finally {
                        this.restoreCanvases(canvases);
                    }
                }
            } else {
                float fixedHeight = cell.getFixedHeight();
                float rightLimit = cell.getRight() + xPos - cell.getEffectivePaddingRight();
                float leftLimit = cell.getLeft() + xPos + cell.getEffectivePaddingLeft();
                if (cell.isNoWrap()) {
                    switch (cell.getHorizontalAlignment()) {
                        case 1: {
                            rightLimit += 10000.0f;
                            leftLimit -= 10000.0f;
                            break;
                        }
                        case 2: {
                            if (cell.getRotation() == 180) {
                                rightLimit += 20000.0f;
                                break;
                            }
                            leftLimit -= 20000.0f;
                            break;
                        }
                        default: {
                            if (cell.getRotation() == 180) {
                                leftLimit -= 20000.0f;
                                break;
                            }
                            rightLimit += 20000.0f;
                        }
                    }
                }
                ColumnText ct = ColumnText.duplicate(cell.getColumn());
                ct.setCanvases(canvases);
                float bry = tly - (currentMaxHeight - cell.getEffectivePaddingTop() - cell.getEffectivePaddingBottom());
                if (fixedHeight > 0.0f && cell.getHeight() > currentMaxHeight) {
                    tly = cell.getTop() + yPos - cell.getEffectivePaddingTop();
                    bry = cell.getTop() + yPos - currentMaxHeight + cell.getEffectivePaddingBottom();
                }
                if ((tly > bry || ct.zeroHeightElement()) && leftLimit < rightLimit) {
                    ct.setSimpleColumn(leftLimit, bry - 0.001f, rightLimit, tly);
                    if (cell.getRotation() == 180) {
                        float shx = leftLimit + rightLimit;
                        float shy = yPos + yPos - currentMaxHeight + cell.getEffectivePaddingBottom() - cell.getEffectivePaddingTop();
                        this.saveAndRotateCanvases(canvases, -1.0f, 0.0f, 0.0f, -1.0f, shx, shy);
                    }
                    try {
                        ct.go();
                    }
                    catch (DocumentException e) {
                        throw new ExceptionConverter(e);
                    }
                    finally {
                        if (cell.getRotation() == 180) {
                            this.restoreCanvases(canvases);
                        }
                    }
                }
            }
            if ((evt = cell.getCellEvent()) == null) continue;
            Rectangle rect = new Rectangle(cell.getLeft() + xPos, cell.getTop() + yPos - currentMaxHeight, cell.getRight() + xPos, cell.getTop() + yPos);
            evt.cellLayout(cell, rect, canvases);
        }
    }

    public boolean isCalculated() {
        return this.calculated;
    }

    public float getMaxHeights() {
        if (this.calculated) {
            return this.maxHeight;
        }
        return this.calculateHeights();
    }

    public void setMaxHeights(float maxHeight) {
        this.maxHeight = maxHeight;
    }

    float[] getEventWidth(float xPos) {
        int n = 0;
        for (PdfPCell cell1 : this.cells) {
            if (cell1 == null) continue;
            ++n;
        }
        float[] width = new float[n + 1];
        n = 0;
        width[n++] = xPos;
        for (PdfPCell cell : this.cells) {
            if (cell == null) continue;
            width[n] = width[n - 1] + cell.getWidth();
            ++n;
        }
        return width;
    }

    public PdfPRow splitRow(PdfPTable table, int rowIndex, float new_height) {
        int k;
        PdfPCell[] newCells = new PdfPCell[this.cells.length];
        float[] fixHs = new float[this.cells.length];
        float[] minHs = new float[this.cells.length];
        boolean allEmpty = true;
        for (k = 0; k < this.cells.length; ++k) {
            float newHeight = new_height;
            PdfPCell cell = this.cells[k];
            if (cell == null) {
                int index = rowIndex;
                if (!table.rowSpanAbove(index, k)) continue;
                newHeight += table.getRowHeight(index);
                while (table.rowSpanAbove(--index, k)) {
                    newHeight += table.getRowHeight(index);
                }
                PdfPRow row = table.getRow(index);
                if (row == null || row.getCells()[k] == null) continue;
                newCells[k] = new PdfPCell(row.getCells()[k]);
                newCells[k].consumeHeight(newHeight);
                newCells[k].setRowspan(row.getCells()[k].getRowspan() - rowIndex + index);
                allEmpty = false;
                continue;
            }
            fixHs[k] = cell.getFixedHeight();
            minHs[k] = cell.getMinimumHeight();
            Image img = cell.getImage();
            PdfPCell newCell = new PdfPCell(cell);
            if (img != null) {
                if (newHeight > cell.getEffectivePaddingBottom() + cell.getEffectivePaddingTop() + 2.0f) {
                    newCell.setPhrase(null);
                    allEmpty = false;
                }
            } else {
                boolean thisEmpty;
                int status;
                float y;
                ColumnText ct = ColumnText.duplicate(cell.getColumn());
                float left = cell.getLeft() + cell.getEffectivePaddingLeft();
                float top = cell.getTop() - cell.getEffectivePaddingTop();
                float bottom = top + cell.getEffectivePaddingBottom() - newHeight;
                float right = cell.getRight() - cell.getEffectivePaddingRight();
                switch (cell.getRotation()) {
                    case 90: 
                    case 270: {
                        y = PdfPRow.setColumn(ct, bottom, left, top, right);
                        break;
                    }
                    default: {
                        y = PdfPRow.setColumn(ct, left, bottom, cell.isNoWrap() ? 20000.0f : right, top);
                    }
                }
                try {
                    status = ct.go(true);
                }
                catch (DocumentException e) {
                    throw new ExceptionConverter(e);
                }
                boolean bl = thisEmpty = ct.getYLine() == y;
                if (thisEmpty) {
                    newCell.setColumn(ColumnText.duplicate(cell.getColumn()));
                    ct.setFilledWidth(0.0f);
                } else if ((status & 1) == 0) {
                    newCell.setColumn(ct);
                    ct.setFilledWidth(0.0f);
                } else {
                    newCell.setPhrase(null);
                }
                allEmpty = allEmpty && thisEmpty;
            }
            newCells[k] = newCell;
            cell.setFixedHeight(newHeight);
        }
        if (allEmpty) {
            for (k = 0; k < this.cells.length; ++k) {
                PdfPCell cell = this.cells[k];
                if (cell == null) continue;
                if (fixHs[k] > 0.0f) {
                    cell.setFixedHeight(fixHs[k]);
                    continue;
                }
                cell.setMinimumHeight(minHs[k]);
            }
            return null;
        }
        this.calculateHeights();
        PdfPRow split = new PdfPRow(newCells);
        split.widths = (float[])this.widths.clone();
        split.calculateHeights();
        return split;
    }

    public PdfPCell[] getCells() {
        return this.cells;
    }
}

