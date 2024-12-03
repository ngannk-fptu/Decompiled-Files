/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ICell;
import org.apache.poi.xwpf.usermodel.TableRowHeightRule;
import org.apache.poi.xwpf.usermodel.XWPFSDTCell;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHeightRule;

public class XWPFTableRow {
    private final CTRow ctRow;
    private final XWPFTable table;
    private List<XWPFTableCell> tableCells;

    public XWPFTableRow(CTRow row, XWPFTable table) {
        this.table = table;
        this.ctRow = row;
        this.getTableCells();
    }

    @Internal
    public CTRow getCtRow() {
        return this.ctRow;
    }

    public XWPFTableCell createCell() {
        XWPFTableCell tableCell = new XWPFTableCell(this.ctRow.addNewTc(), this, this.table.getBody());
        this.ensureBlockLevelElement(tableCell);
        this.tableCells.add(tableCell);
        return tableCell;
    }

    public XWPFTableCell getCell(int pos) {
        if (pos >= 0 && pos < this.ctRow.sizeOfTcArray()) {
            return this.getTableCells().get(pos);
        }
        return null;
    }

    public void removeCell(int pos) {
        if (pos >= 0 && pos < this.ctRow.sizeOfTcArray()) {
            this.tableCells.remove(pos);
            this.ctRow.removeTc(pos);
        }
    }

    public XWPFTableCell addNewTableCell() {
        CTTc cell = this.ctRow.addNewTc();
        XWPFTableCell tableCell = new XWPFTableCell(cell, this, this.table.getBody());
        this.ensureBlockLevelElement(tableCell);
        this.tableCells.add(tableCell);
        return tableCell;
    }

    private void ensureBlockLevelElement(XWPFTableCell tableCell) {
        if (tableCell.getParagraphs().isEmpty()) {
            tableCell.addParagraph();
        }
    }

    public int getHeight() {
        CTTrPr properties = this.getTrPr();
        return properties.sizeOfTrHeightArray() == 0 ? 0 : (int)Units.toDXA(POIXMLUnits.parseLength(properties.getTrHeightArray(0).xgetVal()));
    }

    public void setHeight(int height) {
        CTTrPr properties = this.getTrPr();
        CTHeight h = properties.sizeOfTrHeightArray() == 0 ? properties.addNewTrHeight() : properties.getTrHeightArray(0);
        h.setVal(new BigInteger(Integer.toString(height)));
    }

    public TableRowHeightRule getHeightRule() {
        CTTrPr properties = this.getTrPr();
        return properties.sizeOfTrHeightArray() == 0 ? TableRowHeightRule.AUTO : TableRowHeightRule.valueOf(properties.getTrHeightArray(0).getHRule().intValue());
    }

    public void setHeightRule(TableRowHeightRule heightRule) {
        CTTrPr properties = this.getTrPr();
        CTHeight h = properties.sizeOfTrHeightArray() == 0 ? properties.addNewTrHeight() : properties.getTrHeightArray(0);
        h.setHRule(STHeightRule.Enum.forInt(heightRule.getValue()));
    }

    private CTTrPr getTrPr() {
        return this.ctRow.isSetTrPr() ? this.ctRow.getTrPr() : this.ctRow.addNewTrPr();
    }

    public XWPFTable getTable() {
        return this.table;
    }

    public List<ICell> getTableICells() {
        ArrayList<ICell> cells = new ArrayList<ICell>();
        try (XmlCursor cursor = this.ctRow.newCursor();){
            cursor.selectPath("./*");
            while (cursor.toNextSelection()) {
                XmlObject o = cursor.getObject();
                if (o instanceof CTTc) {
                    cells.add(new XWPFTableCell((CTTc)o, this, this.table.getBody()));
                    continue;
                }
                if (!(o instanceof CTSdtCell)) continue;
                cells.add(new XWPFSDTCell((CTSdtCell)o, this, this.table.getBody()));
            }
        }
        return cells;
    }

    public List<XWPFTableCell> getTableCells() {
        if (this.tableCells == null) {
            ArrayList<XWPFTableCell> cells = new ArrayList<XWPFTableCell>();
            for (CTTc tableCell : this.ctRow.getTcArray()) {
                cells.add(new XWPFTableCell(tableCell, this, this.table.getBody()));
            }
            this.tableCells = cells;
        }
        return this.tableCells;
    }

    public XWPFTableCell getTableCell(CTTc cell) {
        for (XWPFTableCell tableCell : this.tableCells) {
            if (tableCell.getCTTc() != cell) continue;
            return tableCell;
        }
        return null;
    }

    public boolean isCantSplitRow() {
        CTTrPr trpr;
        boolean isCant = false;
        if (this.ctRow.isSetTrPr() && (trpr = this.getTrPr()).sizeOfCantSplitArray() > 0) {
            CTOnOff onoff = trpr.getCantSplitArray(0);
            isCant = !onoff.isSetVal() || POIXMLUnits.parseOnOff(onoff.xgetVal());
        }
        return isCant;
    }

    public void setCantSplitRow(boolean split) {
        CTTrPr trpr = this.getTrPr();
        CTOnOff onoff = trpr.sizeOfCantSplitArray() > 0 ? trpr.getCantSplitArray(0) : trpr.addNewCantSplit();
        onoff.setVal(split ? STOnOff1.ON : STOnOff1.OFF);
    }

    public boolean isRepeatHeader() {
        boolean repeat = false;
        for (XWPFTableRow row : this.table.getRows()) {
            repeat = row.getRepeat();
            if (row != this && repeat) continue;
            break;
        }
        return repeat;
    }

    private boolean getRepeat() {
        CTTrPr trpr;
        boolean repeat = false;
        if (this.ctRow.isSetTrPr() && (trpr = this.getTrPr()).sizeOfTblHeaderArray() > 0) {
            CTOnOff rpt = trpr.getTblHeaderArray(0);
            repeat = !rpt.isSetVal() || POIXMLUnits.parseOnOff(rpt.xgetVal());
        }
        return repeat;
    }

    public void setRepeatHeader(boolean repeat) {
        CTTrPr trpr = this.getTrPr();
        CTOnOff onoff = trpr.sizeOfTblHeaderArray() > 0 ? trpr.getTblHeaderArray(0) : trpr.addNewTblHeader();
        onoff.setVal(repeat ? STOnOff1.ON : STOnOff1.OFF);
    }
}

