/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;
import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherArrayProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherRecordTypes;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFLine;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFShapeContainer;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFTableCell;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.TableShape;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Units;

public final class HSLFTable
extends HSLFGroupShape
implements HSLFShapeContainer,
TableShape<HSLFShape, HSLFTextParagraph> {
    protected static final int BORDERS_ALL = 5;
    protected static final int BORDERS_OUTSIDE = 6;
    protected static final int BORDERS_INSIDE = 7;
    protected static final int BORDERS_NONE = 8;
    protected HSLFTableCell[][] cells;
    private int columnCount = -1;

    protected HSLFTable(int numRows, int numCols) {
        this(numRows, numCols, null);
    }

    protected HSLFTable(int numRows, int numCols, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(parent);
        if (numRows < 1) {
            throw new IllegalArgumentException("The number of rows must be greater than 1");
        }
        if (numCols < 1) {
            throw new IllegalArgumentException("The number of columns must be greater than 1");
        }
        double x = 0.0;
        double y = 0.0;
        double tblWidth = 0.0;
        double tblHeight = 0.0;
        this.cells = new HSLFTableCell[numRows][numCols];
        for (int i = 0; i < this.cells.length; ++i) {
            x = 0.0;
            for (int j = 0; j < this.cells[i].length; ++j) {
                this.cells[i][j] = new HSLFTableCell(this);
                Rectangle2D.Double anchor = new Rectangle2D.Double(x, y, 100.0, 40.0);
                this.cells[i][j].setAnchor(anchor);
                x += 100.0;
            }
            y += 40.0;
        }
        tblWidth = x;
        tblHeight = y;
        this.setExteriorAnchor(new Rectangle2D.Double(0.0, 0.0, tblWidth, tblHeight));
        EscherContainerRecord spCont = (EscherContainerRecord)this.getSpContainer().getChild(0);
        EscherOptRecord opt = new EscherOptRecord();
        opt.setRecordId(EscherRecordTypes.USER_DEFINED.typeID);
        opt.addEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.GROUPSHAPE__TABLEPROPERTIES, 1));
        EscherArrayProperty p = new EscherArrayProperty(EscherPropertyTypes.GROUPSHAPE__TABLEROWPROPERTIES, true, 0);
        p.setSizeOfElements(4);
        p.setNumberOfElementsInArray(numRows);
        p.setNumberOfElementsInMemory(numRows);
        opt.addEscherProperty(p);
        spCont.addChildBefore(opt, EscherRecordTypes.CLIENT_ANCHOR.typeID);
    }

    protected HSLFTable(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    public HSLFTableCell getCell(int row, int col) {
        if (row < 0 || this.cells.length <= row) {
            return null;
        }
        HSLFTableCell[] r = this.cells[row];
        if (r == null || col < 0 || r.length <= col) {
            return null;
        }
        return r[col];
    }

    @Override
    public int getNumberOfColumns() {
        if (this.columnCount == -1) {
            for (HSLFTableCell[] hc : this.cells) {
                if (hc == null) continue;
                this.columnCount = Math.max(this.columnCount, hc.length);
            }
        }
        return this.columnCount;
    }

    @Override
    public int getNumberOfRows() {
        return this.cells.length;
    }

    @Override
    protected void afterInsert(HSLFSheet sh) {
        super.afterInsert(sh);
        HashSet<HSLFLine> lineSet = new HashSet<HSLFLine>();
        HSLFTableCell[][] hSLFTableCellArray = this.cells;
        int n = hSLFTableCellArray.length;
        for (int i = 0; i < n; ++i) {
            HSLFTableCell[] row;
            for (HSLFTableCell c : row = hSLFTableCellArray[i]) {
                this.addShape(c);
                for (HSLFLine bt : new HSLFLine[]{c.borderTop, c.borderRight, c.borderBottom, c.borderLeft}) {
                    if (bt == null) continue;
                    lineSet.add(bt);
                }
            }
        }
        for (HSLFLine l : lineSet) {
            this.addShape(l);
        }
        this.updateRowHeightsProperty();
    }

    private void cellListToArray() {
        ArrayList<HSLFTableCell> htc = new ArrayList<HSLFTableCell>();
        for (HSLFShape h : this.getShapes()) {
            if (!(h instanceof HSLFTableCell)) continue;
            htc.add((HSLFTableCell)h);
        }
        if (htc.isEmpty()) {
            throw new IllegalStateException("HSLFTable without HSLFTableCells");
        }
        TreeSet<Double> colSet = new TreeSet<Double>();
        TreeSet<Double> rowSet = new TreeSet<Double>();
        for (HSLFTableCell sh : htc) {
            Rectangle2D anchor = sh.getAnchor();
            colSet.add(anchor.getX());
            rowSet.add(anchor.getY());
        }
        this.cells = new HSLFTableCell[rowSet.size()][colSet.size()];
        ArrayList<Double> colLst = new ArrayList<Double>(colSet);
        ArrayList<Double> rowLst = new ArrayList<Double>(rowSet);
        for (HSLFTableCell sh : htc) {
            Rectangle2D anchor = sh.getAnchor();
            int row = rowLst.indexOf(anchor.getY());
            int col = colLst.indexOf(anchor.getX());
            assert (row != -1 && col != -1);
            this.cells[row][col] = sh;
            int gridSpan = this.calcSpan(colLst, anchor.getWidth(), col);
            int rowSpan = this.calcSpan(rowLst, anchor.getHeight(), row);
            sh.setGridSpan(gridSpan);
            sh.setRowSpan(rowSpan);
        }
    }

    private int calcSpan(List<Double> spaces, double totalSpace, int idx) {
        int span = 1;
        ListIterator<Double> li = spaces.listIterator(idx);
        double start = li.next();
        while (li.hasNext() && li.next() - start < totalSpace) {
            ++span;
        }
        return span;
    }

    private void fitLinesToCells() {
        ArrayList<LineRect> lines = new ArrayList<LineRect>();
        for (HSLFShape h : this.getShapes()) {
            if (!(h instanceof HSLFLine)) continue;
            lines.add(new LineRect((HSLFLine)h));
        }
        int threshold = 5;
        HSLFTableCell[][] hSLFTableCellArray = this.cells;
        int n = hSLFTableCellArray.length;
        for (int i = 0; i < n; ++i) {
            HSLFTableCell[] tca;
            for (HSLFTableCell tc : tca = hSLFTableCellArray[i]) {
                if (tc == null) continue;
                Rectangle2D cellAnchor = tc.getAnchor();
                double x1 = cellAnchor.getMinX();
                double x2 = cellAnchor.getMaxX();
                double y1 = cellAnchor.getMinY();
                double y2 = cellAnchor.getMaxY();
                LineRect lline = null;
                LineRect tline = null;
                LineRect rline = null;
                LineRect bline = null;
                int lfit = Integer.MAX_VALUE;
                int tfit = Integer.MAX_VALUE;
                int rfit = Integer.MAX_VALUE;
                int bfit = Integer.MAX_VALUE;
                for (LineRect lr : lines) {
                    int bfitx;
                    int rfitx;
                    int tfitx;
                    int lfitx = lr.leftFit(x1, x2, y1, y2);
                    if (lfitx < lfit) {
                        lfit = lfitx;
                        lline = lr;
                    }
                    if ((tfitx = lr.topFit(x1, x2, y1, y2)) < tfit) {
                        tfit = tfitx;
                        tline = lr;
                    }
                    if ((rfitx = lr.rightFit(x1, x2, y1, y2)) < rfit) {
                        rfit = rfitx;
                        rline = lr;
                    }
                    if ((bfitx = lr.bottomFit(x1, x2, y1, y2)) >= bfit) continue;
                    bfit = bfitx;
                    bline = lr;
                }
                if (lfit < 5 && lline != null) {
                    tc.borderLeft = lline.l;
                }
                if (tfit < 5 && tline != null) {
                    tc.borderTop = tline.l;
                }
                if (rfit < 5 && rline != null) {
                    tc.borderRight = rline.l;
                }
                if (bfit >= 5 || bline == null) continue;
                tc.borderBottom = bline.l;
            }
        }
    }

    protected void initTable() {
        this.cellListToArray();
        this.fitLinesToCells();
    }

    @Override
    public void setSheet(HSLFSheet sheet) {
        super.setSheet(sheet);
        if (this.cells == null) {
            this.initTable();
        } else {
            HSLFTableCell[][] hSLFTableCellArray = this.cells;
            int n = hSLFTableCellArray.length;
            for (int i = 0; i < n; ++i) {
                HSLFTableCell[] cols;
                for (HSLFTableCell col : cols = hSLFTableCellArray[i]) {
                    col.setSheet(sheet);
                }
            }
        }
    }

    @Override
    public double getRowHeight(int row) {
        if (row < 0 || row >= this.cells.length) {
            throw new IndexOutOfBoundsException("Row index '" + row + "' is not within range [0-" + (this.cells.length - 1) + "]");
        }
        return this.cells[row][0].getAnchor().getHeight();
    }

    @Override
    public void setRowHeight(int row, double height) {
        if (row < 0 || row >= this.cells.length) {
            throw new IndexOutOfBoundsException("Row index '" + row + "' is not within range [0-" + (this.cells.length - 1) + "]");
        }
        AbstractEscherOptRecord opt = (AbstractEscherOptRecord)this.getEscherChild(EscherRecordTypes.USER_DEFINED);
        EscherArrayProperty p = (EscherArrayProperty)opt.lookup(EscherPropertyTypes.GROUPSHAPE__TABLEROWPROPERTIES);
        byte[] masterBytes = p.getElement(row);
        double currentHeight = Units.masterToPoints(LittleEndian.getInt(masterBytes, 0));
        LittleEndian.putInt(masterBytes, 0, Units.pointsToMaster(height));
        p.setElement(row, masterBytes);
        double dy = height - currentHeight;
        for (int i = row; i < this.cells.length; ++i) {
            for (HSLFTableCell c : this.cells[i]) {
                if (c == null) continue;
                Rectangle2D anchor = c.getAnchor();
                if (i == row) {
                    anchor.setRect(anchor.getX(), anchor.getY(), anchor.getWidth(), height);
                } else {
                    anchor.setRect(anchor.getX(), anchor.getY() + dy, anchor.getWidth(), anchor.getHeight());
                }
                c.setAnchor(anchor);
            }
        }
        Rectangle2D tblanchor = this.getAnchor();
        tblanchor.setRect(tblanchor.getX(), tblanchor.getY(), tblanchor.getWidth(), tblanchor.getHeight() + dy);
        this.setExteriorAnchor(tblanchor);
    }

    @Override
    public double getColumnWidth(int col) {
        if (col < 0 || col >= this.cells[0].length) {
            throw new IllegalArgumentException("Column index '" + col + "' is not within range [0-" + (this.cells[0].length - 1) + "]");
        }
        return this.cells[0][col].getAnchor().getWidth();
    }

    @Override
    public void setColumnWidth(int col, double width) {
        if (col < 0 || col >= this.cells[0].length) {
            throw new IllegalArgumentException("Column index '" + col + "' is not within range [0-" + (this.cells[0].length - 1) + "]");
        }
        double currentWidth = this.cells[0][col].getAnchor().getWidth();
        double dx = width - currentWidth;
        for (HSLFTableCell[] cols : this.cells) {
            Rectangle2D anchor = cols[col].getAnchor();
            anchor.setRect(anchor.getX(), anchor.getY(), width, anchor.getHeight());
            cols[col].setAnchor(anchor);
            if (col >= cols.length - 1) continue;
            for (int j = col + 1; j < cols.length; ++j) {
                anchor = cols[j].getAnchor();
                anchor.setRect(anchor.getX() + dx, anchor.getY(), anchor.getWidth(), anchor.getHeight());
                cols[j].setAnchor(anchor);
            }
        }
        Rectangle2D tblanchor = this.getAnchor();
        tblanchor.setRect(tblanchor.getX(), tblanchor.getY(), tblanchor.getWidth() + dx, tblanchor.getHeight());
        this.setExteriorAnchor(tblanchor);
    }

    protected HSLFTableCell getRelativeCell(HSLFTableCell origin, int row, int col) {
        int thisRow = 0;
        int thisCol = 0;
        boolean found = false;
        block0: for (HSLFTableCell[] tca : this.cells) {
            thisCol = 0;
            for (HSLFTableCell tc : tca) {
                if (tc == origin) {
                    found = true;
                    break block0;
                }
                ++thisCol;
            }
            ++thisRow;
        }
        int otherRow = thisRow + row;
        int otherCol = thisCol + col;
        return found && 0 <= otherRow && otherRow < this.cells.length && 0 <= otherCol && otherCol < this.cells[otherRow].length ? this.cells[otherRow][otherCol] : null;
    }

    @Override
    protected void moveAndScale(Rectangle2D anchorDest) {
        super.moveAndScale(anchorDest);
        this.updateRowHeightsProperty();
    }

    private void updateRowHeightsProperty() {
        AbstractEscherOptRecord opt = (AbstractEscherOptRecord)this.getEscherChild(EscherRecordTypes.USER_DEFINED);
        EscherArrayProperty p = (EscherArrayProperty)opt.lookup(EscherPropertyTypes.GROUPSHAPE__TABLEROWPROPERTIES);
        byte[] val = new byte[4];
        for (int rowIdx = 0; rowIdx < this.cells.length; ++rowIdx) {
            int rowHeight = Units.pointsToMaster(this.cells[rowIdx][0].getAnchor().getHeight());
            LittleEndian.putInt(val, 0, rowHeight);
            p.setElement(rowIdx, val);
        }
    }

    static class LineRect {
        final HSLFLine l;
        final double lx1;
        final double lx2;
        final double ly1;
        final double ly2;

        LineRect(HSLFLine l) {
            this.l = l;
            Rectangle2D r = l.getAnchor();
            this.lx1 = r.getMinX();
            this.lx2 = r.getMaxX();
            this.ly1 = r.getMinY();
            this.ly2 = r.getMaxY();
        }

        int leftFit(double x1, double x2, double y1, double y2) {
            return (int)(Math.abs(x1 - this.lx1) + Math.abs(y1 - this.ly1) + Math.abs(x1 - this.lx2) + Math.abs(y2 - this.ly2));
        }

        int topFit(double x1, double x2, double y1, double y2) {
            return (int)(Math.abs(x1 - this.lx1) + Math.abs(y1 - this.ly1) + Math.abs(x2 - this.lx2) + Math.abs(y1 - this.ly2));
        }

        int rightFit(double x1, double x2, double y1, double y2) {
            return (int)(Math.abs(x2 - this.lx1) + Math.abs(y1 - this.ly1) + Math.abs(x2 - this.lx2) + Math.abs(y2 - this.ly2));
        }

        int bottomFit(double x1, double x2, double y1, double y2) {
            return (int)(Math.abs(x1 - this.lx1) + Math.abs(y2 - this.ly1) + Math.abs(x2 - this.lx2) + Math.abs(y2 - this.ly2));
        }
    }
}

