/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.sprm.SprmBuffer;
import org.apache.poi.hwpf.sprm.TableSprmUncompressor;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableCellDescriptor;
import org.apache.poi.hwpf.usermodel.TableProperties;

public final class TableRow
extends Range {
    private static final Logger LOG = LogManager.getLogger(TableRow.class);
    private static final short SPRM_DXAGAPHALF = -27134;
    private static final short SPRM_DYAROWHEIGHT = -27641;
    private static final short SPRM_FCANTSPLIT = 13315;
    private static final short SPRM_FTABLEHEADER = 13316;
    private static final short SPRM_TJC = 21504;
    private static final char TABLE_CELL_MARK = '\u0007';
    private TableCell[] _cells;
    private boolean _cellsFound;
    int _levelNum;
    private SprmBuffer _papx;
    private TableProperties _tprops;

    public TableRow(int startIdxInclusive, int endIdxExclusive, Table parent, int levelNum) {
        super(startIdxInclusive, endIdxExclusive, parent);
        Paragraph last = this.getParagraph(this.numParagraphs() - 1);
        this._papx = last._papx;
        this._tprops = TableSprmUncompressor.uncompressTAP(this._papx);
        this._levelNum = levelNum;
        this.initCells();
    }

    public boolean cantSplit() {
        return this._tprops.getFCantSplit();
    }

    public BorderCode getBarBorder() {
        throw new UnsupportedOperationException("not applicable for TableRow");
    }

    public BorderCode getBottomBorder() {
        return this._tprops.getBrcBottom();
    }

    public TableCell getCell(int index) {
        this.initCells();
        return this._cells[index];
    }

    public int getGapHalf() {
        return this._tprops.getDxaGapHalf();
    }

    public BorderCode getHorizontalBorder() {
        return this._tprops.getBrcHorizontal();
    }

    public BorderCode getLeftBorder() {
        return this._tprops.getBrcLeft();
    }

    public BorderCode getRightBorder() {
        return this._tprops.getBrcRight();
    }

    public int getRowHeight() {
        return this._tprops.getDyaRowHeight();
    }

    public int getRowJustification() {
        return this._tprops.getJc();
    }

    public BorderCode getTopBorder() {
        return this._tprops.getBrcTop();
    }

    public BorderCode getVerticalBorder() {
        return this._tprops.getBrcVertical();
    }

    private void initCells() {
        TableCell lastCell;
        if (this._cellsFound) {
            return;
        }
        short expectedCellsCount = this._tprops.getItcMac();
        int lastCellStart = 0;
        ArrayList<TableCell> cells = new ArrayList<TableCell>(expectedCellsCount + 1);
        for (int p = 0; p < this.numParagraphs(); ++p) {
            Paragraph paragraph = this.getParagraph(p);
            String s = paragraph.text();
            if ((s.length() <= 0 || s.charAt(s.length() - 1) != '\u0007') && !paragraph.isEmbeddedCellMark() || paragraph.getTableLevel() != this._levelNum) continue;
            TableCellDescriptor tableCellDescriptor = this._tprops.getRgtc() != null && this._tprops.getRgtc().length > cells.size() ? this._tprops.getRgtc()[cells.size()] : new TableCellDescriptor();
            short leftEdge = this._tprops.getRgdxaCenter() != null && this._tprops.getRgdxaCenter().length > cells.size() ? this._tprops.getRgdxaCenter()[cells.size()] : (short)0;
            short rightEdge = this._tprops.getRgdxaCenter() != null && this._tprops.getRgdxaCenter().length > cells.size() + 1 ? this._tprops.getRgdxaCenter()[cells.size() + 1] : (short)0;
            TableCell tableCell = new TableCell(this.getParagraph(lastCellStart).getStartOffset(), this.getParagraph(p).getEndOffset(), this, this._levelNum, tableCellDescriptor, leftEdge, rightEdge - leftEdge);
            cells.add(tableCell);
            lastCellStart = p + 1;
        }
        if (lastCellStart < this.numParagraphs() - 1) {
            TableCellDescriptor tableCellDescriptor = this._tprops.getRgtc() != null && this._tprops.getRgtc().length > cells.size() ? this._tprops.getRgtc()[cells.size()] : new TableCellDescriptor();
            short leftEdge = this._tprops.getRgdxaCenter() != null && this._tprops.getRgdxaCenter().length > cells.size() ? this._tprops.getRgdxaCenter()[cells.size()] : (short)0;
            short rightEdge = this._tprops.getRgdxaCenter() != null && this._tprops.getRgdxaCenter().length > cells.size() + 1 ? this._tprops.getRgdxaCenter()[cells.size() + 1] : (short)0;
            TableCell tableCell = new TableCell(lastCellStart, this.numParagraphs() - 1, this, this._levelNum, tableCellDescriptor, leftEdge, rightEdge - leftEdge);
            cells.add(tableCell);
        }
        if (!cells.isEmpty() && cells.size() != expectedCellsCount && (lastCell = (TableCell)cells.get(cells.size() - 1)).numParagraphs() == 1 && lastCell.getParagraph(0).isTableRowEnd()) {
            cells.remove(cells.size() - 1);
        }
        if (cells.size() != expectedCellsCount) {
            LOG.atWarn().log("Number of found table cells ({}) for table row [{}c; {}c] not equals to stored property value {}", (Object)cells.size(), (Object)Unbox.box(this.getStartOffset()), (Object)Unbox.box(this.getEndOffset()), (Object)Unbox.box(expectedCellsCount));
            this._tprops.setItcMac((short)cells.size());
        }
        this._cells = cells.toArray(new TableCell[0]);
        this._cellsFound = true;
    }

    public boolean isTableHeader() {
        return this._tprops.getFTableHeader();
    }

    public int numCells() {
        this.initCells();
        return this._cells.length;
    }

    @Override
    protected void reset() {
        this._cellsFound = false;
    }

    public void setCantSplit(boolean cantSplit) {
        this._tprops.setFCantSplit(cantSplit);
        this._papx.updateSprm((short)13315, (byte)(cantSplit ? 1 : 0));
    }

    public void setGapHalf(int dxaGapHalf) {
        this._tprops.setDxaGapHalf(dxaGapHalf);
        this._papx.updateSprm((short)-27134, (short)dxaGapHalf);
    }

    public void setRowHeight(int dyaRowHeight) {
        this._tprops.setDyaRowHeight(dyaRowHeight);
        this._papx.updateSprm((short)-27641, (short)dyaRowHeight);
    }

    public void setRowJustification(int jc) {
        this._tprops.setJc((short)jc);
        this._papx.updateSprm((short)21504, (short)jc);
    }

    public void setTableHeader(boolean tableHeader) {
        this._tprops.setFTableHeader(tableHeader);
        this._papx.updateSprm((short)13316, (byte)(tableHeader ? 1 : 0));
    }
}

