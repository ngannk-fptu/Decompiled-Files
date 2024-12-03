/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.newtable;

import java.awt.Rectangle;
import java.util.List;
import java.util.Set;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.Length;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.layout.CollapsedBorderSide;
import org.xhtmlrenderer.layout.FloatManager;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.newtable.CollapsedBorderValue;
import org.xhtmlrenderer.newtable.TableBox;
import org.xhtmlrenderer.newtable.TableColumn;
import org.xhtmlrenderer.newtable.TableRowBox;
import org.xhtmlrenderer.newtable.TableSectionBox;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.ContentLimit;
import org.xhtmlrenderer.render.ContentLimitContainer;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;

public class TableCellBox
extends BlockBox {
    public static final TableCellBox SPANNING_CELL = new TableCellBox();
    private int _row;
    private int _col;
    private TableBox _table;
    private TableSectionBox _section;
    private BorderPropertySet _collapsedLayoutBorder;
    private BorderPropertySet _collapsedPaintingBorder;
    private CollapsedBorderValue _collapsedBorderTop;
    private CollapsedBorderValue _collapsedBorderRight;
    private CollapsedBorderValue _collapsedBorderBottom;
    private CollapsedBorderValue _collapsedBorderLeft;
    private static final int[] BORDER_PRIORITIES = new int[IdentValue.getIdentCount()];
    private static final int BCELL = 10;
    private static final int BROW = 9;
    private static final int BROWGROUP = 8;
    private static final int BCOL = 7;
    private static final int BTABLE = 6;

    @Override
    public BlockBox copyOf() {
        TableCellBox result = new TableCellBox();
        result.setStyle(this.getStyle());
        result.setElement(this.getElement());
        return result;
    }

    @Override
    public BorderPropertySet getBorder(CssContext cssCtx) {
        if (this.getTable().getStyle().isCollapseBorders()) {
            return this._collapsedLayoutBorder == null ? BorderPropertySet.EMPTY_BORDER : this._collapsedLayoutBorder;
        }
        return super.getBorder(cssCtx);
    }

    public void calcCollapsedBorder(CssContext c) {
        CollapsedBorderValue top = this.collapsedTopBorder(c);
        CollapsedBorderValue right = this.collapsedRightBorder(c);
        CollapsedBorderValue bottom = this.collapsedBottomBorder(c);
        CollapsedBorderValue left = this.collapsedLeftBorder(c);
        this._collapsedPaintingBorder = new BorderPropertySet(top, right, bottom, left);
        top.setWidth((top.width() + 1) / 2);
        right.setWidth(right.width() / 2);
        bottom.setWidth(bottom.width() / 2);
        left.setWidth((left.width() + 1) / 2);
        this._collapsedLayoutBorder = new BorderPropertySet(top, right, bottom, left);
        this._collapsedBorderTop = top;
        this._collapsedBorderRight = right;
        this._collapsedBorderBottom = bottom;
        this._collapsedBorderLeft = left;
    }

    public int getCol() {
        return this._col;
    }

    public void setCol(int col) {
        this._col = col;
    }

    public int getRow() {
        return this._row;
    }

    public void setRow(int row) {
        this._row = row;
    }

    @Override
    public void layout(LayoutContext c) {
        super.layout(c);
    }

    public TableBox getTable() {
        if (this._table == null) {
            this._table = (TableBox)this.getParent().getParent().getParent();
        }
        return this._table;
    }

    protected TableSectionBox getSection() {
        if (this._section == null) {
            this._section = (TableSectionBox)this.getParent().getParent();
        }
        return this._section;
    }

    public Length getOuterStyleWidth(CssContext c) {
        Length result = this.getStyle().asLength(c, CSSName.WIDTH);
        if (result.isVariable() || result.isPercent()) {
            return result;
        }
        int bordersAndPadding = 0;
        BorderPropertySet border = this.getBorder(c);
        bordersAndPadding += (int)border.left() + (int)border.right();
        RectPropertySet padding = this.getPadding(c);
        result.setValue(result.value() + (long)(bordersAndPadding += (int)padding.left() + (int)padding.right()));
        return result;
    }

    public Length getOuterStyleOrColWidth(CssContext c) {
        Length result = this.getOuterStyleWidth(c);
        if (this.getStyle().getColSpan() > 1 || !result.isVariable()) {
            return result;
        }
        TableColumn col = this.getTable().colElement(this.getCol());
        if (col != null) {
            result = col.getStyle().asLength(c, CSSName.WIDTH);
        }
        return result;
    }

    public void setLayoutWidth(LayoutContext c, int width) {
        this.calcDimensions(c);
        this.setContentWidth(width - this.getLeftMBP() - this.getRightMBP());
    }

    @Override
    public boolean isAutoHeight() {
        return this.getStyle().isAutoHeight() || !this.getStyle().hasAbsoluteUnit(CSSName.HEIGHT);
    }

    @Override
    public int calcBaseline(LayoutContext c) {
        int result = super.calcBaseline(c);
        if (result != Integer.MIN_VALUE) {
            return result;
        }
        Rectangle contentArea = this.getContentAreaEdge(this.getAbsX(), this.getAbsY(), c);
        return (int)contentArea.getY();
    }

    public int calcBlockBaseline(LayoutContext c) {
        return super.calcBaseline(c);
    }

    public void moveContent(LayoutContext c, final int deltaY) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            Box b = this.getChild(i);
            b.setY(b.getY() + deltaY);
        }
        this.getPersistentBFC().getFloatManager().performFloatOperation(new FloatManager.FloatOperation(){

            @Override
            public void operate(Box floater) {
                floater.setY(floater.getY() + deltaY);
            }
        });
        this.calcChildLocations();
    }

    public boolean isPageBreaksChange(LayoutContext c, int posDeltaY) {
        if (!c.isPageBreaksAllowed()) {
            return false;
        }
        PageBox page = c.getRootLayer().getFirstPage(c, this);
        int bottomEdge = this.getAbsY() + this.getChildrenHeight();
        return page != null && (bottomEdge >= page.getBottom() - c.getExtraSpaceBottom() || bottomEdge + posDeltaY >= page.getBottom() - c.getExtraSpaceBottom());
    }

    public IdentValue getVerticalAlign() {
        IdentValue val = this.getStyle().getIdent(CSSName.VERTICAL_ALIGN);
        if (val == IdentValue.TOP || val == IdentValue.MIDDLE || val == IdentValue.BOTTOM) {
            return val;
        }
        return IdentValue.BASELINE;
    }

    private boolean isPaintBackgroundsAndBorders() {
        boolean showEmpty = this.getStyle().isShowEmptyCells();
        return showEmpty || this.getChildrenContentType() != 4;
    }

    @Override
    public void paintBackground(RenderingContext c) {
        Rectangle bounds;
        if (this.isPaintBackgroundsAndBorders() && this.getStyle().isVisible() && (bounds = c.isPrint() && this.getTable().getStyle().isPaginateTable() ? this.getContentLimitedBorderEdge(c) : this.getPaintingBorderEdge(c)) != null) {
            this.paintBackgroundStack(c, bounds);
        }
    }

    private void paintBackgroundStack(RenderingContext c, Rectangle bounds) {
        BorderPropertySet border = this.getStyle().getBorder(c);
        TableColumn column = this.getTable().colElement(this.getCol());
        if (column != null) {
            c.getOutputDevice().paintBackground(c, column.getStyle(), bounds, this.getTable().getColumnBounds(c, this.getCol()), border);
        }
        Box row = this.getParent();
        Box section = row.getParent();
        CalculatedStyle tableStyle = this.getTable().getStyle();
        CalculatedStyle sectionStyle = section.getStyle();
        Rectangle imageContainer = section.getPaintingBorderEdge(c);
        imageContainer.y += tableStyle.getBorderVSpacing(c);
        imageContainer.height -= tableStyle.getBorderVSpacing(c);
        imageContainer.x += tableStyle.getBorderHSpacing(c);
        imageContainer.width -= 2 * tableStyle.getBorderHSpacing(c);
        c.getOutputDevice().paintBackground(c, sectionStyle, bounds, imageContainer, sectionStyle.getBorder(c));
        CalculatedStyle rowStyle = row.getStyle();
        imageContainer = row.getPaintingBorderEdge(c);
        imageContainer.x += tableStyle.getBorderHSpacing(c);
        imageContainer.width -= 2 * tableStyle.getBorderHSpacing(c);
        c.getOutputDevice().paintBackground(c, rowStyle, bounds, imageContainer, rowStyle.getBorder(c));
        c.getOutputDevice().paintBackground(c, this.getStyle(), bounds, this.getPaintingBorderEdge(c), border);
    }

    @Override
    public void paintBorder(RenderingContext c) {
        if (this.isPaintBackgroundsAndBorders() && !this.hasCollapsedPaintingBorder()) {
            if (c.isPrint() && this.getTable().getStyle().isPaginateTable() && this.getStyle().isVisible()) {
                Rectangle bounds = this.getContentLimitedBorderEdge(c);
                if (bounds != null) {
                    c.getOutputDevice().paintBorder(c, this.getStyle(), bounds, this.getBorderSides());
                }
            } else {
                super.paintBorder(c);
            }
        }
    }

    public void paintCollapsedBorder(RenderingContext c, int side) {
        c.getOutputDevice().paintCollapsedBorder(c, this.getCollapsedPaintingBorder(), this.getCollapsedBorderBounds(c), side);
    }

    private Rectangle getContentLimitedBorderEdge(RenderingContext c) {
        ContentLimit limit;
        Rectangle result = this.getPaintingBorderEdge(c);
        TableSectionBox section = this.getSection();
        if (section.isHeader() || section.isFooter()) {
            return result;
        }
        ContentLimitContainer contentLimitContainer = ((TableRowBox)this.getParent()).getContentLimitContainer();
        ContentLimit contentLimit = limit = contentLimitContainer != null ? contentLimitContainer.getContentLimit(c.getPageNo()) : null;
        if (limit == null) {
            return null;
        }
        if (limit.getTop() == -1 || limit.getBottom() == -1) {
            return result;
        }
        int top = c.getPageNo() == contentLimitContainer.getInitialPageNo() ? result.y : limit.getTop() - ((TableRowBox)this.getParent()).getExtraSpaceTop();
        int bottom = c.getPageNo() == contentLimitContainer.getLastPageNo() ? result.y + result.height : limit.getBottom() + ((TableRowBox)this.getParent()).getExtraSpaceBottom();
        result.y = top;
        result.height = bottom - top;
        return result;
    }

    @Override
    public Rectangle getChildrenClipEdge(RenderingContext c) {
        Rectangle bounds;
        if (c.isPrint() && this.getTable().getStyle().isPaginateTable() && (bounds = this.getContentLimitedBorderEdge(c)) != null) {
            BorderPropertySet border = this.getBorder(c);
            RectPropertySet padding = this.getPadding(c);
            bounds.y += (int)border.top() + (int)padding.top();
            bounds.height -= (int)border.height() + (int)padding.height();
            return bounds;
        }
        return super.getChildrenClipEdge(c);
    }

    @Override
    protected boolean isFixedWidthAdvisoryOnly() {
        return this.getTable().getStyle().isIdent(CSSName.TABLE_LAYOUT, IdentValue.AUTO);
    }

    @Override
    protected boolean isSkipWhenCollapsingMargins() {
        return true;
    }

    public static CollapsedBorderValue compareBorders(CollapsedBorderValue border1, CollapsedBorderValue border2, boolean returnNullOnEqual) {
        if (!border2.defined()) {
            return border1;
        }
        if (!border1.defined()) {
            return border2;
        }
        if (border1.style() == IdentValue.HIDDEN) {
            return border1;
        }
        if (border2.style() == IdentValue.HIDDEN) {
            return border2;
        }
        if (border2.style() == IdentValue.NONE) {
            return border1;
        }
        if (border1.style() == IdentValue.NONE) {
            return border2;
        }
        if (border1.width() != border2.width()) {
            return border1.width() > border2.width() ? border1 : border2;
        }
        if (border1.style() != border2.style()) {
            return BORDER_PRIORITIES[border1.style().FS_ID] > BORDER_PRIORITIES[border2.style().FS_ID] ? border1 : border2;
        }
        if (returnNullOnEqual && border1.precedence() == border2.precedence()) {
            return null;
        }
        return border1.precedence() >= border2.precedence() ? border1 : border2;
    }

    private static CollapsedBorderValue compareBorders(CollapsedBorderValue border1, CollapsedBorderValue border2) {
        return TableCellBox.compareBorders(border1, border2, false);
    }

    private CollapsedBorderValue collapsedLeftBorder(CssContext c) {
        TableColumn colElt;
        BorderPropertySet border = this.getStyle().getBorder(c);
        CollapsedBorderValue result = CollapsedBorderValue.borderLeft(border, 10);
        TableCellBox prevCell = this.getTable().cellLeft(this);
        if (prevCell != null) {
            if ((result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderRight(prevCell.getStyle().getBorder(c), 10))).hidden()) {
                return result;
            }
        } else if (this.getCol() == 0) {
            if ((result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderLeft(this.getParent().getStyle().getBorder(c), 9))).hidden()) {
                return result;
            }
            if ((result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderLeft(this.getSection().getStyle().getBorder(c), 8))).hidden()) {
                return result;
            }
        }
        if ((colElt = this.getTable().colElement(this.getCol())) != null && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderLeft(colElt.getStyle().getBorder(c), 7))).hidden()) {
            return result;
        }
        if (this.getCol() > 0 && (colElt = this.getTable().colElement(this.getCol() - 1)) != null && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderRight(colElt.getStyle().getBorder(c), 7))).hidden()) {
            return result;
        }
        if (this.getCol() == 0 && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderLeft(this.getTable().getStyle().getBorder(c), 6))).hidden()) {
            return result;
        }
        return result;
    }

    private CollapsedBorderValue collapsedRightBorder(CssContext c) {
        TableColumn colElt;
        TableBox tableElt = this.getTable();
        boolean inLastColumn = false;
        int effCol = tableElt.colToEffCol(this.getCol() + this.getStyle().getColSpan() - 1);
        if (effCol == tableElt.numEffCols() - 1) {
            inLastColumn = true;
        }
        CollapsedBorderValue result = CollapsedBorderValue.borderRight(this.getStyle().getBorder(c), 10);
        if (!inLastColumn) {
            TableCellBox nextCell = tableElt.cellRight(this);
            if (nextCell != null && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderLeft(nextCell.getStyle().getBorder(c), 10))).hidden()) {
                return result;
            }
        } else {
            if ((result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderRight(this.getParent().getStyle().getBorder(c), 9))).hidden()) {
                return result;
            }
            if ((result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderRight(this.getSection().getStyle().getBorder(c), 8))).hidden()) {
                return result;
            }
        }
        if ((colElt = this.getTable().colElement(this.getCol() + this.getStyle().getColSpan() - 1)) != null && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderRight(colElt.getStyle().getBorder(c), 7))).hidden()) {
            return result;
        }
        if (!inLastColumn ? (colElt = tableElt.colElement(this.getCol() + this.getStyle().getColSpan())) != null && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderLeft(colElt.getStyle().getBorder(c), 7))).hidden() : (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderRight(tableElt.getStyle().getBorder(c), 6))).hidden()) {
            return result;
        }
        return result;
    }

    private CollapsedBorderValue collapsedTopBorder(CssContext c) {
        CollapsedBorderValue result = CollapsedBorderValue.borderTop(this.getStyle().getBorder(c), 10);
        TableCellBox prevCell = this.getTable().cellAbove(this);
        if (prevCell != null && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderBottom(prevCell.getStyle().getBorder(c), 10))).hidden()) {
            return result;
        }
        if ((result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderTop(this.getParent().getStyle().getBorder(c), 9))).hidden()) {
            return result;
        }
        if (prevCell != null) {
            TableRowBox prevRow = null;
            prevRow = prevCell.getSection() == this.getSection() ? (TableRowBox)this.getParent().getPreviousSibling() : prevCell.getSection().getLastRow();
            if (prevRow != null && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderBottom(prevRow.getStyle().getBorder(c), 9))).hidden()) {
                return result;
            }
        }
        TableSectionBox currSection = this.getSection();
        if (this.getRow() == 0) {
            if ((result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderTop(currSection.getStyle().getBorder(c), 8))).hidden()) {
                return result;
            }
            currSection = this.getTable().sectionAbove(currSection, false);
            if (currSection != null && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderBottom(currSection.getStyle().getBorder(c), 8))).hidden()) {
                return result;
            }
        }
        if (currSection == null) {
            TableColumn colElt = this.getTable().colElement(this.getCol());
            if (colElt != null && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderTop(colElt.getStyle().getBorder(c), 7))).hidden()) {
                return result;
            }
            if ((result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderTop(this.getTable().getStyle().getBorder(c), 6))).hidden()) {
                return result;
            }
        }
        return result;
    }

    private CollapsedBorderValue collapsedBottomBorder(CssContext c) {
        CollapsedBorderValue result = CollapsedBorderValue.borderBottom(this.getStyle().getBorder(c), 10);
        TableCellBox nextCell = this.getTable().cellBelow(this);
        if (nextCell != null && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderTop(nextCell.getStyle().getBorder(c), 10))).hidden()) {
            return result;
        }
        if ((result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderBottom(this.getParent().getStyle().getBorder(c), 9))).hidden()) {
            return result;
        }
        if (nextCell != null && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderTop(nextCell.getParent().getStyle().getBorder(c), 9))).hidden()) {
            return result;
        }
        TableSectionBox currSection = this.getSection();
        if (this.getRow() + this.getStyle().getRowSpan() >= currSection.numRows()) {
            if ((result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderBottom(currSection.getStyle().getBorder(c), 8))).hidden()) {
                return result;
            }
            currSection = this.getTable().sectionBelow(currSection, false);
            if (currSection != null && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderTop(currSection.getStyle().getBorder(c), 8))).hidden()) {
                return result;
            }
        }
        if (currSection == null) {
            TableColumn colElt = this.getTable().colElement(this.getCol());
            if (colElt != null && (result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderBottom(colElt.getStyle().getBorder(c), 7))).hidden()) {
                return result;
            }
            if ((result = TableCellBox.compareBorders(result, CollapsedBorderValue.borderBottom(this.getTable().getStyle().getBorder(c), 6))).hidden()) {
                return result;
            }
        }
        return result;
    }

    private Rectangle getCollapsedBorderBounds(CssContext c) {
        BorderPropertySet border = this.getCollapsedPaintingBorder();
        Rectangle bounds = this.getPaintingBorderEdge(c);
        bounds.x -= (int)border.left() / 2;
        bounds.y -= (int)border.top() / 2;
        bounds.width += (int)border.left() / 2 + ((int)border.right() + 1) / 2;
        bounds.height += (int)border.top() / 2 + ((int)border.bottom() + 1) / 2;
        return bounds;
    }

    @Override
    public Rectangle getPaintingClipEdge(CssContext c) {
        if (this.hasCollapsedPaintingBorder()) {
            return this.getCollapsedBorderBounds(c);
        }
        return super.getPaintingClipEdge(c);
    }

    public boolean hasCollapsedPaintingBorder() {
        return this._collapsedPaintingBorder != null;
    }

    protected BorderPropertySet getCollapsedPaintingBorder() {
        return this._collapsedPaintingBorder;
    }

    public CollapsedBorderValue getCollapsedBorderBottom() {
        return this._collapsedBorderBottom;
    }

    public CollapsedBorderValue getCollapsedBorderLeft() {
        return this._collapsedBorderLeft;
    }

    public CollapsedBorderValue getCollapsedBorderRight() {
        return this._collapsedBorderRight;
    }

    public CollapsedBorderValue getCollapsedBorderTop() {
        return this._collapsedBorderTop;
    }

    public void addCollapsedBorders(Set all, List borders) {
        if (this._collapsedBorderTop.exists() && !all.contains(this._collapsedBorderTop)) {
            all.add(this._collapsedBorderTop);
            borders.add(new CollapsedBorderSide(this, 1));
        }
        if (this._collapsedBorderRight.exists() && !all.contains(this._collapsedBorderRight)) {
            all.add(this._collapsedBorderRight);
            borders.add(new CollapsedBorderSide(this, 8));
        }
        if (this._collapsedBorderBottom.exists() && !all.contains(this._collapsedBorderBottom)) {
            all.add(this._collapsedBorderBottom);
            borders.add(new CollapsedBorderSide(this, 4));
        }
        if (this._collapsedBorderLeft.exists() && !all.contains(this._collapsedBorderLeft)) {
            all.add(this._collapsedBorderLeft);
            borders.add(new CollapsedBorderSide(this, 2));
        }
    }

    @Override
    protected int getCSSHeight(CssContext c) {
        if (this.getStyle().isAutoHeight()) {
            return -1;
        }
        int result = (int)this.getStyle().getFloatPropertyProportionalWidth(CSSName.HEIGHT, this.getContainingBlock().getContentWidth(), c);
        BorderPropertySet border = this.getBorder(c);
        result -= (int)border.top() + (int)border.bottom();
        RectPropertySet padding = this.getPadding(c);
        return (result -= (int)padding.top() + (int)padding.bottom()) >= 0 ? result : -1;
    }

    @Override
    protected boolean isAllowHeightToShrink() {
        return false;
    }

    @Override
    public boolean isNeedsClipOnPaint(RenderingContext c) {
        boolean result = super.isNeedsClipOnPaint(c);
        if (result) {
            return result;
        }
        ContentLimitContainer contentLimitContainer = ((TableRowBox)this.getParent()).getContentLimitContainer();
        if (contentLimitContainer == null) {
            return false;
        }
        return c.isPrint() && this.getTable().getStyle().isPaginateTable() && contentLimitContainer.isContainsMultiplePages();
    }

    static {
        TableCellBox.BORDER_PRIORITIES[IdentValue.DOUBLE.FS_ID] = 1;
        TableCellBox.BORDER_PRIORITIES[IdentValue.SOLID.FS_ID] = 2;
        TableCellBox.BORDER_PRIORITIES[IdentValue.DASHED.FS_ID] = 3;
        TableCellBox.BORDER_PRIORITIES[IdentValue.DOTTED.FS_ID] = 4;
        TableCellBox.BORDER_PRIORITIES[IdentValue.RIDGE.FS_ID] = 5;
        TableCellBox.BORDER_PRIORITIES[IdentValue.OUTSET.FS_ID] = 6;
        TableCellBox.BORDER_PRIORITIES[IdentValue.GROOVE.FS_ID] = 7;
        TableCellBox.BORDER_PRIORITIES[IdentValue.INSET.FS_ID] = 8;
    }
}

