/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.newtable;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.newtable.RowData;
import org.xhtmlrenderer.newtable.TableBox;
import org.xhtmlrenderer.newtable.TableCellBox;
import org.xhtmlrenderer.newtable.TableSectionBox;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.ContentLimitContainer;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;

public class TableRowBox
extends BlockBox {
    private int _baseline;
    private boolean _haveBaseline = false;
    private int _heightOverride;
    private ContentLimitContainer _contentLimitContainer;
    private int _extraSpaceTop;
    private int _extraSpaceBottom;

    @Override
    public BlockBox copyOf() {
        TableRowBox result = new TableRowBox();
        result.setStyle(this.getStyle());
        result.setElement(this.getElement());
        return result;
    }

    @Override
    public boolean isAutoHeight() {
        return this.getStyle().isAutoHeight() || !this.getStyle().hasAbsoluteUnit(CSSName.HEIGHT);
    }

    private TableBox getTable() {
        return (TableBox)this.getParent().getParent();
    }

    private TableSectionBox getSection() {
        return (TableSectionBox)this.getParent();
    }

    @Override
    public void layout(LayoutContext c, int contentStart) {
        boolean running = c.isPrint() && this.getTable().getStyle().isPaginateTable();
        int prevExtraTop = 0;
        int prevExtraBottom = 0;
        if (running) {
            prevExtraTop = c.getExtraSpaceTop();
            prevExtraBottom = c.getExtraSpaceBottom();
            this.calcExtraSpaceTop(c);
            this.calcExtraSpaceBottom(c);
            c.setExtraSpaceTop(c.getExtraSpaceTop() + this.getExtraSpaceTop());
            c.setExtraSpaceBottom(c.getExtraSpaceBottom() + this.getExtraSpaceBottom());
        }
        super.layout(c, contentStart);
        if (running) {
            if (this.isShouldMoveToNextPage(c)) {
                if (this.getTable().getFirstBodyRow() == this) {
                    this.getTable().setNeedPageClear(true);
                } else {
                    this.setNeedPageClear(true);
                }
            }
            c.setExtraSpaceTop(prevExtraTop);
            c.setExtraSpaceBottom(prevExtraBottom);
        }
    }

    private boolean isShouldMoveToNextPage(LayoutContext c) {
        PageBox page = c.getRootLayer().getFirstPage(c, this);
        if (this.getAbsY() + this.getHeight() < page.getBottom()) {
            return false;
        }
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            TableCellBox cell = (TableCellBox)i.next();
            int baseline = cell.calcBlockBaseline(c);
            if (baseline == Integer.MIN_VALUE || baseline >= page.getBottom()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void analyzePageBreaks(LayoutContext c, ContentLimitContainer container) {
        if (this.getTable().getStyle().isPaginateTable()) {
            this._contentLimitContainer = new ContentLimitContainer(c, this.getAbsY());
            this._contentLimitContainer.setParent(container);
            if (container != null) {
                container.updateTop(c, this.getAbsY());
                container.updateBottom(c, this.getAbsY() + this.getHeight());
            }
            Iterator i = this.getChildIterator();
            while (i.hasNext()) {
                Box b = (Box)i.next();
                b.analyzePageBreaks(c, this._contentLimitContainer);
            }
            if (container != null && this._contentLimitContainer.isContainsMultiplePages()) {
                this.propagateExtraSpace(c, container, this._contentLimitContainer, this.getExtraSpaceTop(), this.getExtraSpaceBottom());
            }
        } else {
            super.analyzePageBreaks(c, container);
        }
    }

    private void calcExtraSpaceTop(LayoutContext c) {
        int maxBorderAndPadding = 0;
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            TableCellBox cell = (TableCellBox)i.next();
            int borderAndPadding = (int)cell.getPadding(c).top() + (int)cell.getBorder(c).top();
            if (borderAndPadding <= maxBorderAndPadding) continue;
            maxBorderAndPadding = borderAndPadding;
        }
        this._extraSpaceTop = maxBorderAndPadding;
    }

    private void calcExtraSpaceBottom(LayoutContext c) {
        int maxBorderAndPadding = 0;
        int cRow = this.getIndex();
        int totalRows = this.getSection().numRows();
        List grid = this.getSection().getGrid();
        if (grid.size() > 0 && cRow < grid.size()) {
            List row = ((RowData)grid.get(cRow)).getRow();
            for (int cCol = 0; cCol < row.size(); ++cCol) {
                int borderAndPadding;
                TableCellBox cell = (TableCellBox)row.get(cCol);
                if (cell == null || cell == TableCellBox.SPANNING_CELL || cRow < totalRows - 1 && this.getSection().cellAt(cRow + 1, cCol) == cell || (borderAndPadding = (int)cell.getPadding(c).bottom() + (int)cell.getBorder(c).bottom()) <= maxBorderAndPadding) continue;
                maxBorderAndPadding = borderAndPadding;
            }
        }
        this._extraSpaceBottom = maxBorderAndPadding;
    }

    @Override
    protected void layoutChildren(LayoutContext c, int contentStart) {
        this.setState(2);
        this.ensureChildren(c);
        TableSectionBox section = this.getSection();
        if (section.isNeedCellWidthCalc()) {
            section.setCellWidths(c);
            section.setNeedCellWidthCalc(false);
        }
        if (this.getChildrenContentType() != 4) {
            int cCol = 0;
            Iterator i = this.getChildIterator();
            while (i.hasNext()) {
                TableCellBox cell = (TableCellBox)i.next();
                this.layoutCell(c, cell, 0);
                ++cCol;
            }
        }
        this.setState(3);
    }

    private void alignBaselineAlignedCells(LayoutContext c) {
        TableCellBox cell;
        int i;
        int[] baselines = new int[this.getChildCount()];
        int lowest = Integer.MIN_VALUE;
        boolean found = false;
        for (i = 0; i < this.getChildCount(); ++i) {
            int baseline;
            cell = (TableCellBox)this.getChild(i);
            if (cell.getVerticalAlign() != IdentValue.BASELINE) continue;
            baselines[i] = baseline = cell.calcBaseline(c);
            if (baseline > lowest) {
                lowest = baseline;
            }
            found = true;
        }
        if (found) {
            for (i = 0; i < this.getChildCount(); ++i) {
                int deltaY;
                cell = (TableCellBox)this.getChild(i);
                if (cell.getVerticalAlign() != IdentValue.BASELINE || (deltaY = lowest - baselines[i]) == 0) continue;
                if (c.isPrint() && cell.isPageBreaksChange(c, deltaY)) {
                    this.relayoutCell(c, cell, deltaY);
                    continue;
                }
                cell.moveContent(c, deltaY);
                cell.setHeight(cell.getHeight() + deltaY);
            }
            this.setBaseline(lowest - this.getAbsY());
            this.setHaveBaseline(true);
        }
    }

    private boolean alignMiddleAndBottomAlignedCells(LayoutContext c) {
        boolean needRowHeightRecalc = false;
        int cRow = this.getIndex();
        int totalRows = this.getSection().numRows();
        List grid = this.getSection().getGrid();
        if (grid.size() > 0 && cRow < grid.size()) {
            List row = ((RowData)grid.get(cRow)).getRow();
            for (int cCol = 0; cCol < row.size(); ++cCol) {
                int deltaY;
                IdentValue val;
                TableCellBox cell = (TableCellBox)row.get(cCol);
                if (cell == null || cell == TableCellBox.SPANNING_CELL || cRow < totalRows - 1 && this.getSection().cellAt(cRow + 1, cCol) == cell || (val = cell.getVerticalAlign()) != IdentValue.MIDDLE && val != IdentValue.BOTTOM || (deltaY = this.calcMiddleBottomDeltaY(cell, val)) <= 0) continue;
                if (c.isPrint() && cell.isPageBreaksChange(c, deltaY)) {
                    int oldCellHeight = cell.getHeight();
                    this.relayoutCell(c, cell, deltaY);
                    if (oldCellHeight + deltaY == cell.getHeight()) continue;
                    needRowHeightRecalc = true;
                    continue;
                }
                cell.moveContent(c, deltaY);
                cell.setHeight(cell.getHeight() + deltaY);
            }
        }
        return needRowHeightRecalc;
    }

    private int calcMiddleBottomDeltaY(TableCellBox cell, IdentValue verticalAlign) {
        int result = cell.getStyle().getRowSpan() == 1 ? this.getHeight() - cell.getChildrenHeight() : this.getAbsY() + this.getHeight() - (cell.getAbsY() + cell.getChildrenHeight());
        if (verticalAlign == IdentValue.MIDDLE) {
            return result / 2;
        }
        return result;
    }

    @Override
    protected void calcLayoutHeight(LayoutContext c, BorderPropertySet border, RectPropertySet margin, RectPropertySet padding) {
        if (this.getHeightOverride() > 0) {
            this.setHeight(this.getHeightOverride());
        }
        this.alignBaselineAlignedCells(c);
        this.calcRowHeight(c);
        boolean recalcRowHeight = this.alignMiddleAndBottomAlignedCells(c);
        if (recalcRowHeight) {
            this.calcRowHeight(c);
        }
        if (!this.isHaveBaseline()) {
            this.calcDefaultBaseline(c);
        }
        this.setCellHeights(c);
    }

    private void calcRowHeight(CssContext c) {
        int bottom;
        int y1 = this.getAbsY();
        int y2 = this.getHeight() != 0 ? y1 + this.getHeight() : y1;
        if (this.isLastRow() && (bottom = this.getTable().calcFixedHeightRowBottom(c)) > 0 && bottom > y2) {
            y2 = bottom;
        }
        int cRow = this.getIndex();
        int totalRows = this.getSection().numRows();
        List grid = this.getSection().getGrid();
        if (grid.size() > 0 && cRow < grid.size()) {
            List row = ((RowData)grid.get(cRow)).getRow();
            for (int cCol = 0; cCol < row.size(); ++cCol) {
                int bottomCellEdge;
                TableCellBox cell = (TableCellBox)row.get(cCol);
                if (cell == null || cell == TableCellBox.SPANNING_CELL || cRow < totalRows - 1 && this.getSection().cellAt(cRow + 1, cCol) == cell || (bottomCellEdge = cell.getAbsY() + cell.getHeight()) <= y2) continue;
                y2 = bottomCellEdge;
            }
        }
        this.setHeight(y2 - y1);
    }

    private boolean isLastRow() {
        TableSectionBox section;
        TableBox table = this.getTable();
        if (table.sectionBelow(section = this.getSection(), true) == null) {
            return section.getChild(section.getChildCount() - 1) == this;
        }
        return false;
    }

    private void calcDefaultBaseline(LayoutContext c) {
        int lowestCellEdge = 0;
        int cRow = this.getIndex();
        int totalRows = this.getSection().numRows();
        List grid = this.getSection().getGrid();
        if (grid.size() > 0 && cRow < grid.size()) {
            List row = ((RowData)grid.get(cRow)).getRow();
            for (int cCol = 0; cCol < row.size(); ++cCol) {
                TableCellBox cell = (TableCellBox)row.get(cCol);
                if (cell == null || cell == TableCellBox.SPANNING_CELL || cRow < totalRows - 1 && this.getSection().cellAt(cRow + 1, cCol) == cell) continue;
                Rectangle contentArea = cell.getContentAreaEdge(cell.getAbsX(), cell.getAbsY(), c);
                int bottomCellEdge = contentArea.y + contentArea.height;
                if (bottomCellEdge <= lowestCellEdge) continue;
                lowestCellEdge = bottomCellEdge;
            }
        }
        if (lowestCellEdge > 0) {
            this.setBaseline(lowestCellEdge - this.getAbsY());
        }
        this.setHaveBaseline(true);
    }

    private void setCellHeights(LayoutContext c) {
        int cRow = this.getIndex();
        int totalRows = this.getSection().numRows();
        List grid = this.getSection().getGrid();
        if (grid.size() > 0 && cRow < grid.size()) {
            List row = ((RowData)grid.get(cRow)).getRow();
            for (int cCol = 0; cCol < row.size(); ++cCol) {
                TableCellBox cell = (TableCellBox)row.get(cCol);
                if (cell == null || cell == TableCellBox.SPANNING_CELL || cRow < totalRows - 1 && this.getSection().cellAt(cRow + 1, cCol) == cell) continue;
                if (cell.getStyle().getRowSpan() == 1) {
                    cell.setHeight(this.getHeight());
                    continue;
                }
                cell.setHeight(this.getAbsY() + this.getHeight() - cell.getAbsY());
            }
        }
    }

    private void relayoutCell(LayoutContext c, TableCellBox cell, int contentStart) {
        int width = cell.getWidth();
        cell.reset(c);
        cell.setLayoutWidth(c, width);
        this.layoutCell(c, cell, contentStart);
    }

    private void layoutCell(LayoutContext c, TableCellBox cell, int contentStart) {
        cell.initContainingLayer(c);
        cell.calcCanvasLocation();
        cell.layout(c, contentStart);
    }

    @Override
    public void initStaticPos(LayoutContext c, BlockBox parent, int childOffset) {
        this.setX(0);
        TableBox table = this.getTable();
        this.setY(parent.getHeight() + table.getStyle().getBorderVSpacing(c));
        c.translate(0, this.getY() - childOffset);
    }

    public int getBaseline() {
        return this._baseline;
    }

    public void setBaseline(int baseline) {
        this._baseline = baseline;
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

    @Override
    public void reset(LayoutContext c) {
        super.reset(c);
        this.setHaveBaseline(false);
        this.getSection().setNeedCellWidthCalc(true);
        this.setContentLimitContainer(null);
    }

    public boolean isHaveBaseline() {
        return this._haveBaseline;
    }

    public void setHaveBaseline(boolean haveBaseline) {
        this._haveBaseline = haveBaseline;
    }

    @Override
    protected String getExtraBoxDescription() {
        if (this.isHaveBaseline()) {
            return "(baseline=" + this.getBaseline() + ") ";
        }
        return "";
    }

    public int getHeightOverride() {
        return this._heightOverride;
    }

    public void setHeightOverride(int heightOverride) {
        this._heightOverride = heightOverride;
    }

    @Override
    public void exportText(RenderingContext c, Writer writer) throws IOException {
        if (this.getTable().isMarginAreaRoot()) {
            super.exportText(c, writer);
        } else {
            int yPos = this.getAbsY();
            if (yPos >= c.getPage().getBottom() && this.isInDocumentFlow()) {
                this.exportPageBoxText(c, writer, yPos);
            }
            Iterator i = this.getChildIterator();
            while (i.hasNext()) {
                TableCellBox cell = (TableCellBox)i.next();
                StringBuffer buffer = new StringBuffer();
                cell.collectText(c, buffer);
                writer.write(buffer.toString().trim());
                int cSpan = cell.getStyle().getColSpan();
                for (int j = 0; j < cSpan; ++j) {
                    writer.write(9);
                }
            }
            writer.write(LINE_SEPARATOR);
        }
    }

    public ContentLimitContainer getContentLimitContainer() {
        return this._contentLimitContainer;
    }

    public void setContentLimitContainer(ContentLimitContainer contentLimitContainer) {
        this._contentLimitContainer = contentLimitContainer;
    }

    public int getExtraSpaceTop() {
        return this._extraSpaceTop;
    }

    public void setExtraSpaceTop(int extraSpaceTop) {
        this._extraSpaceTop = extraSpaceTop;
    }

    public int getExtraSpaceBottom() {
        return this._extraSpaceBottom;
    }

    public void setExtraSpaceBottom(int extraSpaceBottom) {
        this._extraSpaceBottom = extraSpaceBottom;
    }

    @Override
    public int forcePageBreakBefore(LayoutContext c, IdentValue pageBreakValue, boolean pendingPageName) {
        PageBox page;
        int currentDelta = super.forcePageBreakBefore(c, pageBreakValue, pendingPageName);
        if (c.isPrint() && this.getStyle().isCollapseBorders() && (page = c.getRootLayer().getPage(c, this.getAbsY() + currentDelta)) != null) {
            int spill = 0;
            Iterator i = this.getChildIterator();
            while (i.hasNext()) {
                TableCellBox cell = (TableCellBox)i.next();
                BorderPropertySet collapsed = cell.getCollapsedPaintingBorder();
                if (collapsed == null) continue;
                spill = Math.max(spill, (int)collapsed.top() / 2);
            }
            int borderTop = this.getAbsY() + currentDelta + (int)this.getMargin(c).top() - spill;
            int rowDelta = page.getTop() - borderTop;
            if (rowDelta > 0) {
                this.setY(this.getY() + rowDelta);
                currentDelta += rowDelta;
            }
        }
        return currentDelta;
    }
}

