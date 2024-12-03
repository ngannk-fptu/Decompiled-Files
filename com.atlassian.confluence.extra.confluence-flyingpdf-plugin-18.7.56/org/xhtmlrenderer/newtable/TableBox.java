/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.newtable;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.Length;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.newtable.ColumnData;
import org.xhtmlrenderer.newtable.TableCellBox;
import org.xhtmlrenderer.newtable.TableColumn;
import org.xhtmlrenderer.newtable.TableRowBox;
import org.xhtmlrenderer.newtable.TableSectionBox;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.ContentLimit;
import org.xhtmlrenderer.render.ContentLimitContainer;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.util.ArrayUtil;
import org.xhtmlrenderer.util.XRLog;

public class TableBox
extends BlockBox {
    private final List _columns = new ArrayList();
    private int[] _columnPos;
    private TableLayout _tableLayout;
    private List _styleColumns;
    private int _pageClearance;
    private boolean _marginAreaRoot;
    private ContentLimitContainer _contentLimitContainer;
    private int _extraSpaceTop;
    private int _extraSpaceBottom;

    @Override
    public boolean isMarginAreaRoot() {
        return this._marginAreaRoot;
    }

    public void setMarginAreaRoot(boolean marginAreaRoot) {
        this._marginAreaRoot = marginAreaRoot;
    }

    @Override
    public BlockBox copyOf() {
        TableBox result = new TableBox();
        result.setStyle(this.getStyle());
        result.setElement(this.getElement());
        return result;
    }

    public void addStyleColumn(TableColumn col) {
        if (this._styleColumns == null) {
            this._styleColumns = new ArrayList();
        }
        this._styleColumns.add(col);
    }

    public List getStyleColumns() {
        return this._styleColumns == null ? Collections.EMPTY_LIST : this._styleColumns;
    }

    public int[] getColumnPos() {
        return ArrayUtil.cloneOrEmpty(this._columnPos);
    }

    private void setColumnPos(int[] columnPos) {
        this._columnPos = columnPos;
    }

    public int numEffCols() {
        return this._columns.size();
    }

    public int spanOfEffCol(int effCol) {
        return ((ColumnData)this._columns.get(effCol)).getSpan();
    }

    public int colToEffCol(int col) {
        int i;
        int c = 0;
        for (i = 0; c < col && i < this.numEffCols(); c += this.spanOfEffCol(i), ++i) {
        }
        return i;
    }

    public int effColToCol(int effCol) {
        int c = 0;
        for (int i = 0; i < effCol; ++i) {
            c += this.spanOfEffCol(i);
        }
        return c;
    }

    public void appendColumn(int span) {
        ColumnData data = new ColumnData();
        data.setSpan(span);
        this._columns.add(data);
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            TableSectionBox section = (TableSectionBox)i.next();
            section.extendGridToColumnCount(this._columns.size());
        }
    }

    @Override
    public void setStyle(CalculatedStyle style) {
        super.setStyle(style);
        this._tableLayout = this.isMarginAreaRoot() ? new MarginTableLayout(this) : (this.getStyle().isIdent(CSSName.TABLE_LAYOUT, IdentValue.AUTO) || this.getStyle().isAutoWidth() ? new AutoTableLayout(this) : new FixedTableLayout(this));
    }

    @Override
    public void calcMinMaxWidth(LayoutContext c) {
        if (!this.isMinMaxCalculated()) {
            this.recalcSections(c);
            if (this.getStyle().isCollapseBorders()) {
                this.calcBorders(c);
            }
            this._tableLayout.calcMinMaxWidth(c);
            this.setMinMaxCalculated(true);
        }
    }

    public void splitColumn(int pos, int firstSpan) {
        ColumnData newColumn = new ColumnData();
        newColumn.setSpan(firstSpan);
        this._columns.add(pos, newColumn);
        ColumnData leftOver = (ColumnData)this._columns.get(pos + 1);
        leftOver.setSpan(leftOver.getSpan() - firstSpan);
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            TableSectionBox section = (TableSectionBox)i.next();
            section.splitColumn(pos);
        }
    }

    public int marginsBordersPaddingAndSpacing(CssContext c, boolean ignoreAutoMargins) {
        int result = 0;
        RectPropertySet margin = this.getMargin(c);
        if (!ignoreAutoMargins || !this.getStyle().isAutoLeftMargin()) {
            result += (int)margin.left();
        }
        if (!ignoreAutoMargins || !this.getStyle().isAutoRightMargin()) {
            result += (int)margin.right();
        }
        BorderPropertySet border = this.getBorder(c);
        result += (int)border.left() + (int)border.right();
        if (!this.getStyle().isCollapseBorders()) {
            RectPropertySet padding = this.getPadding(c);
            int hSpacing = this.getStyle().getBorderHSpacing(c);
            result = (int)((float)result + (padding.left() + padding.right() + (float)((this.numEffCols() + 1) * hSpacing)));
        }
        return result;
    }

    public List getColumns() {
        return this._columns;
    }

    private void recalcSections(LayoutContext c) {
        this.ensureChildren(c);
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            TableSectionBox section = (TableSectionBox)i.next();
            section.recalcCells(c);
        }
    }

    private void calcBorders(LayoutContext c) {
        this.ensureChildren(c);
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            TableSectionBox section = (TableSectionBox)i.next();
            section.calcBorders(c);
        }
    }

    @Override
    protected boolean isAllowHeightToShrink() {
        return false;
    }

    @Override
    public void layout(LayoutContext c) {
        this.calcMinMaxWidth(c);
        this.calcDimensions(c);
        this.calcWidth();
        this.calcPageClearance(c);
        if (!this.isAnonymous()) {
            this.setDimensionsCalculated(false);
            this.calcDimensions(c, this.getContentWidth());
        }
        this._tableLayout.layout(c);
        this.setCellWidths(c);
        this.layoutTable(c);
    }

    @Override
    protected void resolveAutoMargins(LayoutContext c, int cssWidth, RectPropertySet padding, BorderPropertySet border) {
        if (this.getMinWidth() <= this.getContentWidth() + this.marginsBordersPaddingAndSpacing(c, true)) {
            super.resolveAutoMargins(c, cssWidth, padding, border);
        } else {
            if (this.getStyle().isAutoLeftMargin()) {
                this.setMarginLeft(c, 0);
            }
            if (this.getStyle().isAutoRightMargin()) {
                this.setMarginRight(c, 0);
            }
        }
    }

    private void layoutTable(LayoutContext c) {
        boolean running = c.isPrint() && this.getStyle().isPaginateTable();
        int prevExtraTop = 0;
        int prevExtraBottom = 0;
        if (running) {
            prevExtraTop = c.getExtraSpaceTop();
            prevExtraBottom = c.getExtraSpaceBottom();
            c.setExtraSpaceTop(c.getExtraSpaceTop() + (int)this.getPadding(c).top() + (int)this.getBorder(c).top() + this.getStyle().getBorderVSpacing(c));
            c.setExtraSpaceBottom(c.getExtraSpaceBottom() + (int)this.getPadding(c).bottom() + (int)this.getBorder(c).bottom() + this.getStyle().getBorderVSpacing(c));
        }
        super.layout(c);
        if (running) {
            if (this.isNeedAnalyzePageBreaks()) {
                this.analyzePageBreaks(c);
                this.setExtraSpaceTop(0);
                this.setExtraSpaceBottom(0);
            } else {
                this.setExtraSpaceTop(c.getExtraSpaceTop() - prevExtraTop);
                this.setExtraSpaceBottom(c.getExtraSpaceBottom() - prevExtraBottom);
            }
            c.setExtraSpaceTop(prevExtraTop);
            c.setExtraSpaceBottom(prevExtraBottom);
        }
    }

    @Override
    protected void layoutChildren(LayoutContext c, int contentStart) {
        boolean running;
        this.ensureChildren(c);
        boolean bl = running = c.isPrint() && this.getStyle().isPaginateTable();
        if (running) {
            int headerHeight = this.layoutRunningHeader(c);
            int footerHeight = this.layoutRunningFooter(c);
            int spacingHeight = footerHeight == 0 ? 0 : this.getStyle().getBorderVSpacing(c);
            PageBox first = c.getRootLayer().getFirstPage(c, this);
            if (this.getAbsY() + this.getTy() + headerHeight + footerHeight + spacingHeight > first.getBottom()) {
                this.setNeedPageClear(true);
            }
        }
        super.layoutChildren(c, contentStart);
    }

    private int layoutRunningHeader(LayoutContext c) {
        TableSectionBox section;
        int result = 0;
        if (this.getChildCount() > 0 && (section = (TableSectionBox)this.getChild(0)).isHeader()) {
            c.setNoPageBreak(c.getNoPageBreak() + 1);
            section.initContainingLayer(c);
            section.layout(c);
            c.setExtraSpaceTop(c.getExtraSpaceTop() + section.getHeight());
            result = section.getHeight();
            section.reset(c);
            c.setNoPageBreak(c.getNoPageBreak() - 1);
        }
        return result;
    }

    private int layoutRunningFooter(LayoutContext c) {
        TableSectionBox section;
        int result = 0;
        if (this.getChildCount() > 0 && (section = (TableSectionBox)this.getChild(this.getChildCount() - 1)).isFooter()) {
            c.setNoPageBreak(c.getNoPageBreak() + 1);
            section.initContainingLayer(c);
            section.layout(c);
            c.setExtraSpaceBottom(c.getExtraSpaceBottom() + section.getHeight() + this.getStyle().getBorderVSpacing(c));
            result = section.getHeight();
            section.reset(c);
            c.setNoPageBreak(c.getNoPageBreak() - 1);
        }
        return result;
    }

    private boolean isNeedAnalyzePageBreaks() {
        for (Box b = this.getParent(); b != null; b = b.getParent()) {
            if (!b.getStyle().isTable() || !b.getStyle().isPaginateTable()) continue;
            return false;
        }
        return true;
    }

    private void analyzePageBreaks(LayoutContext c) {
        this.analyzePageBreaks(c, null);
    }

    @Override
    public void analyzePageBreaks(LayoutContext c, ContentLimitContainer container) {
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
        if (container != null && this._contentLimitContainer.isContainsMultiplePages() && (this.getExtraSpaceTop() > 0 || this.getExtraSpaceBottom() > 0)) {
            this.propagateExtraSpace(c, container, this._contentLimitContainer, this.getExtraSpaceTop(), this.getExtraSpaceBottom());
        }
    }

    @Override
    public void paintBackground(RenderingContext c) {
        if (this._contentLimitContainer == null) {
            super.paintBackground(c);
        } else if (this.getStyle().isVisible()) {
            c.getOutputDevice().paintBackground(c, this.getStyle(), this.getContentLimitedBorderEdge(c), this.getPaintingBorderEdge(c), this.getStyle().getBorder(c));
        }
    }

    @Override
    public void paintBorder(RenderingContext c) {
        if (this._contentLimitContainer == null) {
            super.paintBorder(c);
        } else if (this.getStyle().isVisible()) {
            c.getOutputDevice().paintBorder(c, this.getStyle(), this.getContentLimitedBorderEdge(c), this.getBorderSides());
        }
    }

    private Rectangle getContentLimitedBorderEdge(RenderingContext c) {
        int bottom;
        int top;
        Rectangle result = this.getPaintingBorderEdge(c);
        ContentLimit limit = this._contentLimitContainer.getContentLimit(c.getPageNo());
        if (limit == null) {
            XRLog.layout(Level.WARNING, "No content limit found");
            return result;
        }
        if (limit.getTop() == -1 || limit.getBottom() == -1) {
            return result;
        }
        RectPropertySet padding = this.getPadding(c);
        BorderPropertySet border = this.getBorder(c);
        if (c.getPageNo() == this._contentLimitContainer.getInitialPageNo()) {
            top = result.y;
        } else {
            TableSectionBox section;
            top = limit.getTop() - (int)padding.top() - (int)border.top() - this.getStyle().getBorderVSpacing(c);
            if (this.getChildCount() > 0 && (section = (TableSectionBox)this.getChild(0)).isHeader()) {
                top -= section.getHeight();
            }
        }
        if (c.getPageNo() == this._contentLimitContainer.getLastPageNo()) {
            bottom = result.y + result.height;
        } else {
            TableSectionBox section;
            bottom = limit.getBottom() + (int)padding.bottom() + (int)border.bottom() + this.getStyle().getBorderVSpacing(c);
            if (this.getChildCount() > 0 && (section = (TableSectionBox)this.getChild(this.getChildCount() - 1)).isFooter()) {
                bottom += section.getHeight();
            }
        }
        result.y = top;
        result.height = bottom - top;
        return result;
    }

    public void updateHeaderFooterPosition(RenderingContext c) {
        ContentLimit limit = this._contentLimitContainer.getContentLimit(c.getPageNo());
        if (limit != null) {
            this.updateHeaderPosition(c, limit);
            this.updateFooterPosition(c, limit);
        }
    }

    private void updateHeaderPosition(RenderingContext c, ContentLimit limit) {
        TableSectionBox section;
        if ((limit.getTop() != -1 || c.getPageNo() == this._contentLimitContainer.getInitialPageNo()) && this.getChildCount() > 0 && (section = (TableSectionBox)this.getChild(0)).isHeader()) {
            int newAbsY;
            int diff;
            if (!section.isCapturedOriginalAbsY()) {
                section.setOriginalAbsY(section.getAbsY());
                section.setCapturedOriginalAbsY(true);
            }
            if ((diff = (newAbsY = c.getPageNo() == this._contentLimitContainer.getInitialPageNo() ? section.getOriginalAbsY() : limit.getTop() - this.getStyle().getBorderVSpacing(c) - section.getHeight()) - section.getAbsY()) != 0) {
                section.setY(section.getY() + diff);
                section.calcCanvasLocation();
                section.calcChildLocations();
                section.calcPaintingInfo(c, false);
            }
        }
    }

    private void updateFooterPosition(RenderingContext c, ContentLimit limit) {
        TableSectionBox section;
        if ((limit.getBottom() != -1 || c.getPageNo() == this._contentLimitContainer.getLastPageNo()) && this.getChildCount() > 0 && (section = (TableSectionBox)this.getChild(this.getChildCount() - 1)).isFooter()) {
            int newAbsY;
            int diff;
            if (!section.isCapturedOriginalAbsY()) {
                section.setOriginalAbsY(section.getAbsY());
                section.setCapturedOriginalAbsY(true);
            }
            if ((diff = (newAbsY = c.getPageNo() == this._contentLimitContainer.getLastPageNo() ? section.getOriginalAbsY() : limit.getBottom()) - section.getAbsY()) != 0) {
                section.setY(section.getY() + diff);
                section.calcCanvasLocation();
                section.calcChildLocations();
                section.calcPaintingInfo(c, false);
            }
        }
    }

    private void calcPageClearance(LayoutContext c) {
        TableRowBox row;
        PageBox page;
        if (c.isPrint() && this.getStyle().isCollapseBorders() && (page = c.getRootLayer().getFirstPage(c, this)) != null && (row = this.getFirstRow()) != null) {
            int spill = 0;
            Iterator i = row.getChildIterator();
            while (i.hasNext()) {
                TableCellBox cell = (TableCellBox)i.next();
                BorderPropertySet collapsed = cell.getCollapsedPaintingBorder();
                int tmp = (int)collapsed.top() / 2;
                if (tmp <= spill) continue;
                spill = tmp;
            }
            int borderTop = this.getAbsY() + (int)this.getMargin(c).top() - spill;
            int delta = page.getTop() - borderTop;
            if (delta > 0) {
                this.setY(this.getY() + delta);
                this.setPageClearance(delta);
                this.calcCanvasLocation();
                c.translate(0, delta);
            }
        }
    }

    private void calcWidth() {
        if (this.getMinWidth() > this.getWidth()) {
            this.setContentWidth(this.getContentWidth() + this.getMinWidth() - this.getWidth());
        } else if (this.getStyle().isIdent(CSSName.WIDTH, IdentValue.AUTO) && this.getMaxWidth() < this.getWidth()) {
            this.setContentWidth(this.getContentWidth() - (this.getWidth() - this.getMaxWidth()));
        }
    }

    public TableRowBox getFirstRow() {
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            TableSectionBox section = (TableSectionBox)i.next();
            if (section.getChildCount() <= 0) continue;
            return (TableRowBox)section.getChild(0);
        }
        return null;
    }

    public TableRowBox getFirstBodyRow() {
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            TableSectionBox section = (TableSectionBox)i.next();
            if (section.isHeader() || section.isFooter() || section.getChildCount() <= 0) continue;
            return (TableRowBox)section.getChild(0);
        }
        return null;
    }

    private void setCellWidths(LayoutContext c) {
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            BlockBox box = (BlockBox)i.next();
            if (!box.getStyle().isTableSection()) continue;
            ((TableSectionBox)box).setCellWidths(c);
        }
    }

    @Override
    protected void calcLayoutHeight(LayoutContext c, BorderPropertySet border, RectPropertySet margin, RectPropertySet padding) {
        super.calcLayoutHeight(c, border, margin, padding);
        if (this.getChildCount() > 0) {
            this.setHeight(this.getHeight() + this.getStyle().getBorderVSpacing(c));
        }
    }

    @Override
    public void reset(LayoutContext c) {
        super.reset(c);
        this._contentLimitContainer = null;
        this._tableLayout.reset();
    }

    @Override
    protected int getCSSWidth(CssContext c) {
        if (this.getStyle().isAutoWidth()) {
            return -1;
        }
        int result = (int)this.getStyle().getFloatPropertyProportionalWidth(CSSName.WIDTH, this.getContainingBlock().getContentWidth(), c);
        BorderPropertySet border = this.getBorder(c);
        result -= (int)border.left() + (int)border.right();
        if (!this.getStyle().isCollapseBorders()) {
            RectPropertySet padding = this.getPadding(c);
            result -= (int)padding.left() + (int)padding.right();
        }
        return result >= 0 ? result : -1;
    }

    public TableColumn colElement(int col) {
        List styleColumns = this.getStyleColumns();
        if (styleColumns.size() == 0) {
            return null;
        }
        int cCol = 0;
        for (TableColumn colElem : styleColumns) {
            int span = colElem.getStyle().getColSpan();
            if ((cCol += span) <= col) continue;
            return colElem;
        }
        return null;
    }

    public Rectangle getColumnBounds(CssContext c, int col) {
        int effCol = this.colToEffCol(col);
        int hspacing = this.getStyle().getBorderHSpacing(c);
        int vspacing = this.getStyle().getBorderVSpacing(c);
        Rectangle result = this.getContentAreaEdge(this.getAbsX(), this.getAbsY(), c);
        result.y += vspacing;
        result.height -= vspacing * 2;
        result.x += this._columnPos[effCol] + hspacing;
        return result;
    }

    @Override
    public BorderPropertySet getBorder(CssContext cssCtx) {
        if (this.getStyle().isCollapseBorders()) {
            return BorderPropertySet.EMPTY_BORDER;
        }
        return super.getBorder(cssCtx);
    }

    public int calcFixedHeightRowBottom(CssContext c) {
        int cssHeight;
        if (!this.isAnonymous() && (cssHeight = this.getCSSHeight(c)) != -1) {
            return this.getAbsY() + cssHeight - (int)this.getBorder(c).bottom() - (int)this.getPadding(c).bottom() - this.getStyle().getBorderVSpacing(c);
        }
        return -1;
    }

    @Override
    protected boolean isMayCollapseMarginsWithChildren() {
        return false;
    }

    protected TableSectionBox sectionAbove(TableSectionBox section, boolean skipEmptySections) {
        TableSectionBox prevSection = (TableSectionBox)section.getPreviousSibling();
        if (prevSection == null) {
            return null;
        }
        while (prevSection != null && prevSection.numRows() <= 0 && skipEmptySections) {
            prevSection = (TableSectionBox)prevSection.getPreviousSibling();
        }
        return prevSection;
    }

    protected TableSectionBox sectionBelow(TableSectionBox section, boolean skipEmptySections) {
        TableSectionBox nextSection = (TableSectionBox)section.getNextSibling();
        if (nextSection == null) {
            return null;
        }
        while (nextSection != null && nextSection.numRows() <= 0 && skipEmptySections) {
            nextSection = (TableSectionBox)nextSection.getNextSibling();
        }
        return nextSection;
    }

    protected TableCellBox cellAbove(TableCellBox cell) {
        int r = cell.getRow();
        TableSectionBox section = null;
        int rAbove = 0;
        if (r > 0) {
            section = cell.getSection();
            rAbove = r - 1;
        } else {
            section = this.sectionAbove(cell.getSection(), true);
            if (section != null) {
                rAbove = section.numRows() - 1;
            }
        }
        if (section != null) {
            TableCellBox aboveCell;
            int effCol = this.colToEffCol(cell.getCol());
            while ((aboveCell = section.cellAt(rAbove, effCol)) == TableCellBox.SPANNING_CELL && --effCol >= 0) {
            }
            return aboveCell == TableCellBox.SPANNING_CELL ? null : aboveCell;
        }
        return null;
    }

    protected TableCellBox cellBelow(TableCellBox cell) {
        int r = cell.getRow() + cell.getStyle().getRowSpan() - 1;
        TableSectionBox section = null;
        int rBelow = 0;
        if (r < cell.getSection().numRows() - 1) {
            section = cell.getSection();
            rBelow = r + 1;
        } else {
            section = this.sectionBelow(cell.getSection(), true);
            if (section != null) {
                rBelow = 0;
            }
        }
        if (section != null) {
            TableCellBox belowCell;
            int effCol = this.colToEffCol(cell.getCol());
            while ((belowCell = section.cellAt(rBelow, effCol)) == TableCellBox.SPANNING_CELL && --effCol >= 0) {
            }
            return belowCell == TableCellBox.SPANNING_CELL ? null : belowCell;
        }
        return null;
    }

    protected TableCellBox cellLeft(TableCellBox cell) {
        TableCellBox prevCell;
        TableSectionBox section = cell.getSection();
        int effCol = this.colToEffCol(cell.getCol());
        if (effCol == 0) {
            return null;
        }
        while ((prevCell = section.cellAt(cell.getRow(), effCol - 1)) == TableCellBox.SPANNING_CELL && --effCol >= 0) {
        }
        return prevCell == TableCellBox.SPANNING_CELL ? null : prevCell;
    }

    protected TableCellBox cellRight(TableCellBox cell) {
        int effCol = this.colToEffCol(cell.getCol() + cell.getStyle().getColSpan());
        if (effCol >= this.numEffCols()) {
            return null;
        }
        TableCellBox result = cell.getSection().cellAt(cell.getRow(), effCol);
        return result == TableCellBox.SPANNING_CELL ? null : result;
    }

    @Override
    public int calcInlineBaseline(CssContext c) {
        int result = 0;
        boolean found = false;
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            TableSectionBox section = (TableSectionBox)i.next();
            Iterator j = section.getChildIterator();
            if (!j.hasNext()) continue;
            TableRowBox row = (TableRowBox)j.next();
            found = true;
            result = row.getAbsY() + row.getBaseline() - this.getAbsY();
            break;
        }
        if (!found) {
            result = this.getHeight();
        }
        return result;
    }

    @Override
    protected int getPageClearance() {
        return this._pageClearance;
    }

    protected void setPageClearance(int pageClearance) {
        this._pageClearance = pageClearance;
    }

    public boolean hasContentLimitContainer() {
        return this._contentLimitContainer != null;
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

    private static class AutoTableLayout
    implements TableLayout {
        private final TableBox _table;
        private Layout[] _layoutStruct;
        private List _spanCells;

        public AutoTableLayout(TableBox table) {
            this._table = table;
        }

        @Override
        public void reset() {
            this._layoutStruct = null;
            this._spanCells = null;
        }

        protected Layout[] getLayoutStruct() {
            return this._layoutStruct;
        }

        private void fullRecalc(LayoutContext c) {
            this._layoutStruct = new Layout[this._table.numEffCols()];
            for (int i = 0; i < this._layoutStruct.length; ++i) {
                this._layoutStruct[i] = new Layout();
                this._layoutStruct[i].setMinWidth(this.getMinColWidth());
                this._layoutStruct[i].setMaxWidth(this.getMinColWidth());
            }
            this._spanCells = new ArrayList();
            TableBox table = this._table;
            int nEffCols = table.numEffCols();
            int cCol = 0;
            for (TableColumn col : table.getStyleColumns()) {
                int span = col.getStyle().getColSpan();
                Length w = col.getStyle().asLength(c, CSSName.WIDTH);
                if (w.isVariable() && col.getParent() != null) {
                    w = col.getParent().getStyle().asLength(c, CSSName.WIDTH);
                }
                if (w.isFixed() && w.value() == 0L || w.isPercent() && w.value() == 0L) {
                    w = new Length();
                }
                int cEffCol = table.colToEffCol(cCol);
                if (!w.isVariable() && span == 1 && cEffCol < nEffCols && table.spanOfEffCol(cEffCol) == 1) {
                    this._layoutStruct[cEffCol].setWidth(w);
                    if (w.isFixed() && this._layoutStruct[cEffCol].maxWidth() < w.value()) {
                        this._layoutStruct[cEffCol].setMaxWidth(w.value());
                    }
                }
                cCol += span;
            }
            for (int i = 0; i < nEffCols; ++i) {
                this.recalcColumn(c, i);
            }
        }

        protected int getMinColWidth() {
            return 1;
        }

        private void recalcColumn(LayoutContext c, int effCol) {
            Layout l = this._layoutStruct[effCol];
            Iterator j = this._table.getChildIterator();
            while (j.hasNext()) {
                TableSectionBox section = (TableSectionBox)j.next();
                int numRows = section.numRows();
                for (int i = 0; i < numRows; ++i) {
                    TableCellBox cell = section.cellAt(i, effCol);
                    if (cell == TableCellBox.SPANNING_CELL || cell == null) continue;
                    if (cell.getStyle().getColSpan() == 1) {
                        l.setMinWidth(Math.max(l.minWidth(), (long)this.getMinColWidth()));
                        l.setMaxWidth(Math.max(l.maxWidth(), (long)this.getMinColWidth()));
                        cell.calcMinMaxWidth(c);
                        if ((long)cell.getMinWidth() > l.minWidth()) {
                            l.setMinWidth(cell.getMinWidth());
                        }
                        if ((long)cell.getMaxWidth() > l.maxWidth()) {
                            l.setMaxWidth(cell.getMaxWidth());
                        }
                        Length w = cell.getOuterStyleOrColWidth(c);
                        w.setValue(Math.min(0x3FFFFFFFL, Math.max(0L, w.value())));
                        switch (w.type()) {
                            case 2: {
                                if (w.value() <= 0L || l.width().isPercent()) break;
                                if (l.width().isFixed()) {
                                    if (w.value() > l.width().value()) {
                                        l.width().setValue(w.value());
                                    }
                                } else {
                                    l.setWidth(w);
                                }
                                if (w.value() <= l.maxWidth()) break;
                                l.setMaxWidth(w.value());
                                break;
                            }
                            case 3: {
                                if (w.value() <= 0L || l.width().isPercent() && w.value() <= l.width().value()) break;
                                l.setWidth(w);
                            }
                        }
                        continue;
                    }
                    if (effCol != 0 && section.cellAt(i, effCol - 1) == cell) continue;
                    l.setMinWidth(Math.max(l.minWidth(), (long)this.getMinColWidth()));
                    l.setMaxWidth(Math.max(l.maxWidth(), (long)this.getMinColWidth()));
                    this._spanCells.add(cell);
                }
            }
            l.setMaxWidth(Math.max(l.maxWidth(), l.minWidth()));
        }

        private long calcEffectiveWidth(LayoutContext c) {
            long tMaxWidth = 0L;
            Layout[] layoutStruct = this._layoutStruct;
            int nEffCols = layoutStruct.length;
            int hspacing = this._table.getStyle().getBorderHSpacing(c);
            for (int i = 0; i < nEffCols; ++i) {
                layoutStruct[i].setEffWidth(layoutStruct[i].width());
                layoutStruct[i].setEffMinWidth(layoutStruct[i].minWidth());
                layoutStruct[i].setEffMaxWidth(layoutStruct[i].maxWidth());
            }
            Collections.sort(this._spanCells, new Comparator(){

                public int compare(Object o1, Object o2) {
                    TableCellBox c1 = (TableCellBox)o1;
                    TableCellBox c2 = (TableCellBox)o2;
                    return c1.getStyle().getColSpan() - c2.getStyle().getColSpan();
                }
            });
            for (TableCellBox cell : this._spanCells) {
                long cWidth;
                int pos;
                int col;
                cell.calcMinMaxWidth(c);
                int span = cell.getStyle().getColSpan();
                Length w = cell.getOuterStyleOrColWidth(c);
                if (w.value() == 0L) {
                    w = new Length();
                }
                int lastCol = col = this._table.colToEffCol(cell.getCol());
                int cMinWidth = cell.getMinWidth() + hspacing;
                int cMaxWidth = cell.getMaxWidth() + hspacing;
                int totalPercent = 0;
                int minWidth = 0;
                int maxWidth = 0;
                boolean allColsArePercent = true;
                boolean allColsAreFixed = true;
                boolean haveVariable = false;
                int fixedWidth = 0;
                while (lastCol < nEffCols && span > 0) {
                    switch (layoutStruct[lastCol].width().type()) {
                        case 3: {
                            totalPercent = (int)((long)totalPercent + layoutStruct[lastCol].width().value());
                            allColsAreFixed = false;
                            break;
                        }
                        case 2: {
                            if (layoutStruct[lastCol].width().value() > 0L) {
                                fixedWidth = (int)((long)fixedWidth + layoutStruct[lastCol].width().value());
                                allColsArePercent = false;
                                break;
                            }
                        }
                        case 1: {
                            haveVariable = true;
                        }
                        default: {
                            if (!layoutStruct[lastCol].effWidth().isPercent()) {
                                layoutStruct[lastCol].setEffWidth(new Length());
                                allColsArePercent = false;
                            } else {
                                totalPercent = (int)((long)totalPercent + layoutStruct[lastCol].effWidth().value());
                            }
                            allColsAreFixed = false;
                        }
                    }
                    span -= this._table.spanOfEffCol(lastCol);
                    minWidth = (int)((long)minWidth + layoutStruct[lastCol].effMinWidth());
                    maxWidth = (int)((long)maxWidth + layoutStruct[lastCol].effMaxWidth());
                    ++lastCol;
                    cMinWidth -= hspacing;
                    cMaxWidth -= hspacing;
                }
                if (w.isPercent()) {
                    if ((long)totalPercent > w.value() || allColsArePercent) {
                        w = new Length();
                    } else {
                        int pos2;
                        int spanMax = Math.max(maxWidth, cMaxWidth);
                        tMaxWidth = Math.max(tMaxWidth, (long)(spanMax * 100) / w.value());
                        long percentMissing = w.value() - (long)totalPercent;
                        int totalWidth = 0;
                        for (pos2 = col; pos2 < lastCol; ++pos2) {
                            if (layoutStruct[pos2].width().isPercent()) continue;
                            totalWidth = (int)((long)totalWidth + layoutStruct[pos2].effMaxWidth());
                        }
                        for (pos2 = col; pos2 < lastCol && totalWidth > 0; ++pos2) {
                            if (layoutStruct[pos2].width().isPercent()) continue;
                            long percent = percentMissing * layoutStruct[pos2].effMaxWidth() / (long)totalWidth;
                            totalWidth = (int)((long)totalWidth - layoutStruct[pos2].effMaxWidth());
                            percentMissing -= percent;
                            if (percent > 0L) {
                                layoutStruct[pos2].setEffWidth(new Length(percent, 3));
                                continue;
                            }
                            layoutStruct[pos2].setEffWidth(new Length());
                        }
                    }
                }
                if (cMinWidth > minWidth) {
                    int maxw;
                    if (allColsAreFixed) {
                        for (pos = col; fixedWidth > 0 && pos < lastCol; ++pos) {
                            cWidth = Math.max(layoutStruct[pos].effMinWidth(), (long)cMinWidth * layoutStruct[pos].width().value() / (long)fixedWidth);
                            fixedWidth = (int)((long)fixedWidth - layoutStruct[pos].width().value());
                            cMinWidth = (int)((long)cMinWidth - cWidth);
                            layoutStruct[pos].setEffMinWidth(cWidth);
                        }
                    } else if (allColsArePercent) {
                        maxw = maxWidth;
                        int minw = minWidth;
                        int cminw = cMinWidth;
                        for (int pos3 = col; maxw > 0 && pos3 < lastCol; ++pos3) {
                            if (!layoutStruct[pos3].effWidth().isPercent() || layoutStruct[pos3].effWidth().value() <= 0L || fixedWidth > cMinWidth) continue;
                            long cWidth2 = layoutStruct[pos3].effMinWidth();
                            cWidth2 = Math.max(cWidth2, (long)cminw * layoutStruct[pos3].effWidth().value() / (long)totalPercent);
                            cWidth2 = Math.min(layoutStruct[pos3].effMinWidth() + (long)(cMinWidth - minw), cWidth2);
                            maxw = (int)((long)maxw - layoutStruct[pos3].effMaxWidth());
                            minw = (int)((long)minw - layoutStruct[pos3].effMinWidth());
                            cMinWidth = (int)((long)cMinWidth - cWidth2);
                            layoutStruct[pos3].setEffMinWidth(cWidth2);
                        }
                    } else {
                        int pos4;
                        maxw = maxWidth;
                        int minw = minWidth;
                        for (pos4 = col; maxw > 0 && pos4 < lastCol; ++pos4) {
                            if (!layoutStruct[pos4].width().isFixed() || !haveVariable || fixedWidth > cMinWidth) continue;
                            long cWidth3 = Math.max(layoutStruct[pos4].effMinWidth(), layoutStruct[pos4].width().value());
                            fixedWidth = (int)((long)fixedWidth - layoutStruct[pos4].width().value());
                            minw = (int)((long)minw - layoutStruct[pos4].effMinWidth());
                            maxw = (int)((long)maxw - layoutStruct[pos4].effMaxWidth());
                            cMinWidth = (int)((long)cMinWidth - cWidth3);
                            layoutStruct[pos4].setEffMinWidth(cWidth3);
                        }
                        for (pos4 = col; maxw > 0 && pos4 < lastCol && minw < cMinWidth; ++pos4) {
                            if (layoutStruct[pos4].width().isFixed() && haveVariable && fixedWidth <= cMinWidth) continue;
                            long cWidth4 = Math.max(layoutStruct[pos4].effMinWidth(), (long)cMinWidth * layoutStruct[pos4].effMaxWidth() / (long)maxw);
                            cWidth4 = Math.min(layoutStruct[pos4].effMinWidth() + (long)(cMinWidth - minw), cWidth4);
                            maxw = (int)((long)maxw - layoutStruct[pos4].effMaxWidth());
                            minw = (int)((long)minw - layoutStruct[pos4].effMinWidth());
                            cMinWidth = (int)((long)cMinWidth - cWidth4);
                            layoutStruct[pos4].setEffMinWidth(cWidth4);
                        }
                    }
                }
                if (!w.isPercent()) {
                    if (cMaxWidth <= maxWidth) continue;
                    for (pos = col; maxWidth > 0 && pos < lastCol; ++pos) {
                        cWidth = Math.max(layoutStruct[pos].effMaxWidth(), (long)cMaxWidth * layoutStruct[pos].effMaxWidth() / (long)maxWidth);
                        maxWidth = (int)((long)maxWidth - layoutStruct[pos].effMaxWidth());
                        cMaxWidth = (int)((long)cMaxWidth - cWidth);
                        layoutStruct[pos].setEffMaxWidth(cWidth);
                    }
                    continue;
                }
                for (pos = col; pos < lastCol; ++pos) {
                    layoutStruct[pos].setMaxWidth(Math.max(layoutStruct[pos].maxWidth(), layoutStruct[pos].minWidth()));
                }
            }
            return tMaxWidth;
        }

        private boolean shouldScaleColumns(TableBox table) {
            return true;
        }

        @Override
        public void calcMinMaxWidth(LayoutContext c) {
            TableBox table = this._table;
            this.fullRecalc(c);
            Layout[] layoutStruct = this._layoutStruct;
            long spanMaxWidth = this.calcEffectiveWidth(c);
            long minWidth = 0L;
            long maxWidth = 0L;
            long maxPercent = 0L;
            long maxNonPercent = 0L;
            int remainingPercent = 100;
            for (int i = 0; i < layoutStruct.length; ++i) {
                minWidth += layoutStruct[i].effMinWidth();
                maxWidth += layoutStruct[i].effMaxWidth();
                if (layoutStruct[i].effWidth().isPercent()) {
                    long percent = Math.min(layoutStruct[i].effWidth().value(), (long)remainingPercent);
                    long pw = layoutStruct[i].effMaxWidth() * 100L / Math.max(percent, 1L);
                    remainingPercent = (int)((long)remainingPercent - percent);
                    maxPercent = Math.max(pw, maxPercent);
                    continue;
                }
                maxNonPercent += layoutStruct[i].effMaxWidth();
            }
            if (this.shouldScaleColumns(table)) {
                maxNonPercent = (maxNonPercent * 100L + 50L) / (long)Math.max(remainingPercent, 1);
                maxWidth = Math.max(maxNonPercent, maxWidth);
                maxWidth = Math.max(maxWidth, maxPercent);
            }
            maxWidth = Math.max(maxWidth, spanMaxWidth);
            int bs = table.marginsBordersPaddingAndSpacing(c, true);
            minWidth += (long)bs;
            maxWidth += (long)bs;
            Length tw = table.getStyle().asLength(c, CSSName.WIDTH);
            if (tw.isFixed() && tw.value() > 0L) {
                table.calcDimensions(c);
                int width = table.getContentWidth() + table.marginsBordersPaddingAndSpacing(c, true);
                maxWidth = minWidth = Math.max(minWidth, (long)width);
            }
            table.setMaxWidth((int)Math.min(maxWidth, 0x3FFFFFFFL));
            table.setMinWidth((int)Math.min(minWidth, 0x3FFFFFFFL));
        }

        @Override
        public void layout(LayoutContext c) {
            long w;
            int i;
            int tableWidth;
            TableBox table = this._table;
            int available = tableWidth = table.getWidth() - table.marginsBordersPaddingAndSpacing(c, false);
            int nEffCols = table.numEffCols();
            boolean havePercent = false;
            int numVariable = 0;
            int numFixed = 0;
            int totalVariable = 0;
            int totalFixed = 0;
            int totalPercent = 0;
            int allocVariable = 0;
            Layout[] layoutStruct = this._layoutStruct;
            block5: for (i = 0; i < nEffCols; ++i) {
                long w2 = layoutStruct[i].effMinWidth();
                layoutStruct[i].setCalcWidth(w2);
                available = (int)((long)available - w2);
                Length width = layoutStruct[i].effWidth();
                switch (width.type()) {
                    case 3: {
                        havePercent = true;
                        totalPercent = (int)((long)totalPercent + width.value());
                        continue block5;
                    }
                    case 2: {
                        ++numFixed;
                        totalFixed = (int)((long)totalFixed + layoutStruct[i].effMaxWidth());
                        continue block5;
                    }
                    case 1: {
                        ++numVariable;
                        totalVariable = (int)((long)totalVariable + layoutStruct[i].effMaxWidth());
                        allocVariable = (int)((long)allocVariable + w2);
                    }
                }
            }
            if (available > 0 && havePercent) {
                for (i = 0; i < nEffCols; ++i) {
                    Length width = layoutStruct[i].effWidth();
                    if (!width.isPercent()) continue;
                    w = Math.max(layoutStruct[i].effMinWidth(), width.minWidth(tableWidth));
                    available = (int)((long)available + (layoutStruct[i].calcWidth() - w));
                    layoutStruct[i].setCalcWidth(w);
                }
                if (totalPercent > 100) {
                    int excess = tableWidth * (totalPercent - 100) / 100;
                    for (int i2 = nEffCols - 1; i2 >= 0; --i2) {
                        if (!layoutStruct[i2].effWidth().isPercent()) continue;
                        w = layoutStruct[i2].calcWidth();
                        long reduction = Math.min(w, (long)excess);
                        excess = (int)((long)excess - reduction);
                        long newWidth = Math.max(layoutStruct[i2].effMinWidth(), w - reduction);
                        available = (int)((long)available + (w - newWidth));
                        layoutStruct[i2].setCalcWidth(newWidth);
                    }
                }
            }
            if (available > 0) {
                for (i = 0; i < nEffCols; ++i) {
                    Length width = layoutStruct[i].effWidth();
                    if (!width.isFixed() || width.value() <= layoutStruct[i].calcWidth()) continue;
                    available = (int)((long)available + (layoutStruct[i].calcWidth() - width.value()));
                    layoutStruct[i].setCalcWidth(width.value());
                }
            }
            if (available > 0 && numVariable > 0) {
                available += allocVariable;
                for (i = 0; i < nEffCols; ++i) {
                    Length width = layoutStruct[i].effWidth();
                    if (!width.isVariable() || totalVariable == 0) continue;
                    w = Math.max(layoutStruct[i].calcWidth(), (long)available * layoutStruct[i].effMaxWidth() / (long)totalVariable);
                    available = (int)((long)available - w);
                    totalVariable = (int)((long)totalVariable - layoutStruct[i].effMaxWidth());
                    layoutStruct[i].setCalcWidth(w);
                }
            }
            if (available > 0 && numFixed > 0) {
                for (i = 0; i < nEffCols; ++i) {
                    Length width = layoutStruct[i].effWidth();
                    if (!width.isFixed()) continue;
                    w = (long)available * layoutStruct[i].effMaxWidth() / (long)totalFixed;
                    available = (int)((long)available - w);
                    totalFixed = (int)((long)totalFixed - layoutStruct[i].effMaxWidth());
                    layoutStruct[i].setCalcWidth(layoutStruct[i].calcWidth() + w);
                }
            }
            if (available > 0 && havePercent && totalPercent < 100) {
                for (i = 0; i < nEffCols; ++i) {
                    Length width = layoutStruct[i].effWidth();
                    if (!width.isPercent()) continue;
                    w = (long)available * width.value() / (long)totalPercent;
                    available = (int)((long)available - w);
                    totalPercent = (int)((long)totalPercent - width.value());
                    layoutStruct[i].setCalcWidth(layoutStruct[i].calcWidth() + w);
                    if (available == 0 || totalPercent == 0) break;
                }
            }
            if (available > 0) {
                int total = nEffCols;
                int i3 = nEffCols;
                while (i3-- > 0) {
                    int w3 = available / total;
                    available -= w3;
                    --total;
                    layoutStruct[i3].setCalcWidth(layoutStruct[i3].calcWidth() + (long)w3);
                }
            }
            if (available < 0) {
                long reduce;
                int mw;
                if (available < 0) {
                    int i4;
                    mw = 0;
                    for (i4 = nEffCols - 1; i4 >= 0; --i4) {
                        Length width = layoutStruct[i4].effWidth();
                        if (!width.isVariable()) continue;
                        mw = (int)((long)mw + (layoutStruct[i4].calcWidth() - layoutStruct[i4].effMinWidth()));
                    }
                    for (i4 = nEffCols - 1; i4 >= 0 && mw > 0; --i4) {
                        Length width = layoutStruct[i4].effWidth();
                        if (!width.isVariable()) continue;
                        long minMaxDiff = layoutStruct[i4].calcWidth() - layoutStruct[i4].effMinWidth();
                        reduce = (long)available * minMaxDiff / (long)mw;
                        layoutStruct[i4].setCalcWidth(layoutStruct[i4].calcWidth() + reduce);
                        available = (int)((long)available - reduce);
                        mw = (int)((long)mw - minMaxDiff);
                        if (available >= 0) break;
                    }
                }
                if (available < 0) {
                    int i5;
                    mw = 0;
                    for (i5 = nEffCols - 1; i5 >= 0; --i5) {
                        Length width = layoutStruct[i5].effWidth();
                        if (!width.isFixed()) continue;
                        mw = (int)((long)mw + (layoutStruct[i5].calcWidth() - layoutStruct[i5].effMinWidth()));
                    }
                    for (i5 = nEffCols - 1; i5 >= 0 && mw > 0; --i5) {
                        Length width = layoutStruct[i5].effWidth();
                        if (!width.isFixed()) continue;
                        long minMaxDiff = layoutStruct[i5].calcWidth() - layoutStruct[i5].effMinWidth();
                        reduce = (long)available * minMaxDiff / (long)mw;
                        layoutStruct[i5].setCalcWidth(layoutStruct[i5].calcWidth() + reduce);
                        available = (int)((long)available - reduce);
                        mw = (int)((long)mw - minMaxDiff);
                        if (available >= 0) break;
                    }
                }
                if (available < 0) {
                    int i6;
                    mw = 0;
                    for (i6 = nEffCols - 1; i6 >= 0; --i6) {
                        Length width = layoutStruct[i6].effWidth();
                        if (!width.isPercent()) continue;
                        mw = (int)((long)mw + (layoutStruct[i6].calcWidth() - layoutStruct[i6].effMinWidth()));
                    }
                    for (i6 = nEffCols - 1; i6 >= 0 && mw > 0; --i6) {
                        Length width = layoutStruct[i6].effWidth();
                        if (!width.isPercent()) continue;
                        long minMaxDiff = layoutStruct[i6].calcWidth() - layoutStruct[i6].effMinWidth();
                        reduce = (long)available * minMaxDiff / (long)mw;
                        layoutStruct[i6].setCalcWidth(layoutStruct[i6].calcWidth() + reduce);
                        available = (int)((long)available - reduce);
                        mw = (int)((long)mw - minMaxDiff);
                        if (available >= 0) break;
                    }
                }
            }
            int pos = 0;
            int hspacing = this._table.getStyle().getBorderHSpacing(c);
            int[] columnPos = new int[nEffCols + 1];
            for (int i7 = 0; i7 < nEffCols; ++i7) {
                columnPos[i7] = pos;
                pos = (int)((long)pos + (layoutStruct[i7].calcWidth() + (long)hspacing));
            }
            columnPos[columnPos.length - 1] = pos;
            this._table.setColumnPos(columnPos);
        }

        protected static class Layout {
            private Length _width = new Length();
            private Length _effWidth = new Length();
            private long _minWidth = 1L;
            private long _maxWidth = 1L;
            private long _effMinWidth = 0L;
            private long _effMaxWidth = 0L;
            private long _calcWidth = 0L;

            public Length width() {
                return this._width;
            }

            public void setWidth(Length l) {
                this._width = l;
            }

            public Length effWidth() {
                return this._effWidth;
            }

            public void setEffWidth(Length l) {
                this._effWidth = l;
            }

            public long minWidth() {
                return this._minWidth;
            }

            public void setMinWidth(long i) {
                this._minWidth = i;
            }

            public long maxWidth() {
                return this._maxWidth;
            }

            public void setMaxWidth(long i) {
                this._maxWidth = i;
            }

            public long effMinWidth() {
                return this._effMinWidth;
            }

            public void setEffMinWidth(long i) {
                this._effMinWidth = i;
            }

            public long effMaxWidth() {
                return this._effMaxWidth;
            }

            public void setEffMaxWidth(long i) {
                this._effMaxWidth = i;
            }

            public long calcWidth() {
                return this._calcWidth;
            }

            public void setCalcWidth(long i) {
                this._calcWidth = i;
            }
        }
    }

    private static class FixedTableLayout
    implements TableLayout {
        private final TableBox _table;
        private List _widths;

        public FixedTableLayout(TableBox table) {
            this._table = table;
        }

        @Override
        public void reset() {
            this._widths = null;
        }

        private void initWidths() {
            this._widths = new ArrayList(this._table.numEffCols());
            for (int i = 0; i < this._table.numEffCols(); ++i) {
                this._widths.add(new Length());
            }
        }

        private int calcWidthArray(LayoutContext c) {
            Length w;
            this.initWidths();
            TableBox table = this._table;
            int cCol = 0;
            int nEffCols = table.numEffCols();
            int usedWidth = 0;
            for (TableColumn col : table.getStyleColumns()) {
                int span = col.getStyle().getColSpan();
                w = col.getStyle().asLength(c, CSSName.WIDTH);
                if (w.isVariable() && col.getParent() != null) {
                    w = col.getParent().getStyle().asLength(c, CSSName.WIDTH);
                }
                long effWidth = 0L;
                if (w.isFixed() && w.value() > 0L) {
                    effWidth = w.value();
                    effWidth = Math.min(effWidth, 0x3FFFFFFFL);
                }
                int usedSpan = 0;
                int i = 0;
                while (usedSpan < span) {
                    if (cCol + i >= nEffCols) {
                        table.appendColumn(span - usedSpan);
                        ++nEffCols;
                        this._widths.add(new Length());
                    }
                    int eSpan = table.spanOfEffCol(cCol + i);
                    if ((w.isFixed() || w.isPercent()) && w.value() > 0L) {
                        this._widths.set(cCol + i, new Length(w.value() * (long)eSpan, w.type()));
                        usedWidth = (int)((long)usedWidth + effWidth * (long)eSpan);
                    }
                    usedSpan += eSpan;
                    ++i;
                }
                cCol += i;
            }
            cCol = 0;
            TableRowBox firstRow = this._table.getFirstRow();
            if (firstRow != null) {
                Iterator j = firstRow.getChildIterator();
                while (j.hasNext()) {
                    TableCellBox cell = (TableCellBox)j.next();
                    w = cell.getOuterStyleWidth(c);
                    int span = cell.getStyle().getColSpan();
                    long effWidth = 0L;
                    if (w.isFixed() && w.value() > 0L) {
                        effWidth = w.value();
                    }
                    int usedSpan = 0;
                    int i = 0;
                    while (usedSpan < span) {
                        int eSpan = this._table.spanOfEffCol(cCol + i);
                        Length columnWidth = (Length)this._widths.get(cCol + i);
                        if (columnWidth.isVariable() && !w.isVariable()) {
                            this._widths.set(cCol + i, new Length(w.value() * (long)eSpan, w.type()));
                            usedWidth = (int)((long)usedWidth + effWidth * (long)eSpan);
                        }
                        usedSpan += eSpan;
                        ++i;
                    }
                    cCol += i;
                }
            }
            return usedWidth;
        }

        @Override
        public void calcMinMaxWidth(LayoutContext c) {
            int bs = this._table.marginsBordersPaddingAndSpacing(c, true);
            this._table.calcDimensions(c);
            this._table.setDimensionsCalculated(false);
            int mw = this.calcWidthArray(c) + bs;
            this._table.setMinWidth(Math.max(mw, this._table.getWidth()));
            this._table.setMaxWidth(this._table.getMinWidth());
            boolean haveNonFixed = false;
            for (int i = 0; i < this._widths.size(); ++i) {
                Length w = (Length)this._widths.get(i);
                if (w.isFixed()) continue;
                haveNonFixed = true;
                break;
            }
            if (haveNonFixed) {
                this._table.setMaxWidth(0x3FFFFFFF);
            }
        }

        @Override
        public void layout(LayoutContext c) {
            int i;
            int tableWidth;
            int available = tableWidth = this._table.getWidth() - this._table.marginsBordersPaddingAndSpacing(c, false);
            int nEffCols = this._table.numEffCols();
            long[] calcWidth = new long[nEffCols];
            for (i = 0; i < calcWidth.length; ++i) {
                calcWidth[i] = -1L;
            }
            for (i = 0; i < nEffCols; ++i) {
                Length l = (Length)this._widths.get(i);
                if (!l.isFixed()) continue;
                calcWidth[i] = l.value();
                available = (int)((long)available - l.value());
            }
            if (available > 0) {
                int totalPercent = 0;
                for (int i2 = 0; i2 < nEffCols; ++i2) {
                    Length l = (Length)this._widths.get(i2);
                    if (!l.isPercent()) continue;
                    totalPercent = (int)((long)totalPercent + l.value());
                }
                int base = tableWidth * totalPercent / 100;
                if (base > available) {
                    base = available;
                }
                for (int i3 = 0; available > 0 && i3 < nEffCols; ++i3) {
                    Length l = (Length)this._widths.get(i3);
                    if (!l.isPercent()) continue;
                    long w = (long)base * l.value() / (long)totalPercent;
                    available = (int)((long)available - w);
                    calcWidth[i3] = w;
                }
            }
            if (available > 0) {
                int i4;
                int totalVariable = 0;
                for (i4 = 0; i4 < nEffCols; ++i4) {
                    Length l = (Length)this._widths.get(i4);
                    if (!l.isVariable()) continue;
                    ++totalVariable;
                }
                for (i4 = 0; available > 0 && i4 < nEffCols; ++i4) {
                    Length l = (Length)this._widths.get(i4);
                    if (!l.isVariable()) continue;
                    int w = available / totalVariable;
                    available -= w;
                    calcWidth[i4] = w;
                    --totalVariable;
                }
            }
            for (i = 0; i < nEffCols; ++i) {
                if (calcWidth[i] >= 0L) continue;
                calcWidth[i] = 0L;
            }
            if (available > 0) {
                int total = nEffCols;
                int i5 = nEffCols;
                while (i5-- > 0) {
                    int w = available / total;
                    available -= w;
                    --total;
                    int n = i5;
                    calcWidth[n] = calcWidth[n] + (long)w;
                }
            }
            int pos = 0;
            int hspacing = this._table.getStyle().getBorderHSpacing(c);
            int[] columnPos = new int[nEffCols + 1];
            for (int i6 = 0; i6 < nEffCols; ++i6) {
                columnPos[i6] = pos;
                pos = (int)((long)pos + (calcWidth[i6] + (long)hspacing));
            }
            columnPos[columnPos.length - 1] = pos;
            this._table.setColumnPos(columnPos);
        }
    }

    private static class MarginTableLayout
    extends AutoTableLayout {
        public MarginTableLayout(TableBox table) {
            super(table);
        }

        @Override
        protected int getMinColWidth() {
            return 0;
        }

        @Override
        public void calcMinMaxWidth(LayoutContext c) {
            AutoTableLayout.Layout center;
            super.calcMinMaxWidth(c);
            AutoTableLayout.Layout[] layoutStruct = this.getLayoutStruct();
            if (!(layoutStruct.length != 3 || (center = layoutStruct[1]).width().isVariable() && center.maxWidth() == 0L)) {
                if (layoutStruct[0].minWidth() > layoutStruct[2].minWidth()) {
                    layoutStruct[2] = layoutStruct[0];
                } else if (layoutStruct[2].minWidth() > layoutStruct[0].minWidth()) {
                    layoutStruct[0] = layoutStruct[2];
                } else {
                    AutoTableLayout.Layout l = new AutoTableLayout.Layout();
                    l.setMinWidth(Math.max(layoutStruct[0].minWidth(), layoutStruct[2].minWidth()));
                    l.setEffMinWidth(l.minWidth());
                    l.setMaxWidth(Math.max(layoutStruct[0].maxWidth(), layoutStruct[2].maxWidth()));
                    l.setEffMaxWidth(l.maxWidth());
                    layoutStruct[0] = l;
                    layoutStruct[2] = l;
                }
            }
        }
    }

    private static interface TableLayout {
        public void calcMinMaxWidth(LayoutContext var1);

        public void layout(LayoutContext var1);

        public void reset();
    }
}

