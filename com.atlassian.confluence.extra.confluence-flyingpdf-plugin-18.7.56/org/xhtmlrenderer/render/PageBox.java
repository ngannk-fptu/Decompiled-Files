/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.constants.MarginBoxName;
import org.xhtmlrenderer.css.newmatch.PageInfo;
import org.xhtmlrenderer.css.parser.FSFunction;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.layout.BoxBuilder;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.newtable.TableBox;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.MarginBox;
import org.xhtmlrenderer.render.RenderingContext;

public class PageBox {
    private static final MarginArea[] MARGIN_AREA_DEFS = new MarginArea[]{new TopLeftCorner(), new TopMarginArea(), new TopRightCorner(), new LeftMarginArea(), new RightMarginArea(), new BottomLeftCorner(), new BottomMarginArea(), new BottomRightCorner()};
    private static final int LEADING_TRAILING_SPLIT = 5;
    private CalculatedStyle _style;
    private int _top;
    private int _bottom;
    private int _paintingTop;
    private int _paintingBottom;
    private int _pageNo;
    private int _outerPageWidth;
    private PageDimensions _pageDimensions;
    private PageInfo _pageInfo;
    private MarginAreaContainer[] _marginAreas = new MarginAreaContainer[MARGIN_AREA_DEFS.length];
    private Element _metadata;

    public int getWidth(CssContext cssCtx) {
        this.resolvePageDimensions(cssCtx);
        return this._pageDimensions.getWidth();
    }

    public int getHeight(CssContext cssCtx) {
        this.resolvePageDimensions(cssCtx);
        return this._pageDimensions.getHeight();
    }

    private void resolvePageDimensions(CssContext cssCtx) {
        if (this._pageDimensions == null) {
            CalculatedStyle style = this.getStyle();
            int width = style.isLength(CSSName.FS_PAGE_WIDTH) ? (int)style.getFloatPropertyProportionalTo(CSSName.FS_PAGE_WIDTH, 0.0f, cssCtx) : this.resolveAutoPageWidth(cssCtx);
            int height = style.isLength(CSSName.FS_PAGE_HEIGHT) ? (int)style.getFloatPropertyProportionalTo(CSSName.FS_PAGE_HEIGHT, 0.0f, cssCtx) : this.resolveAutoPageHeight(cssCtx);
            if (style.isIdent(CSSName.FS_PAGE_ORIENTATION, IdentValue.LANDSCAPE)) {
                int temp = width;
                width = height;
                height = temp;
            }
            PageDimensions dim = new PageDimensions();
            dim.setWidth(width);
            dim.setHeight(height);
            this._pageDimensions = dim;
        }
    }

    private boolean isUseLetterSize() {
        Locale l = Locale.getDefault();
        String county = l.getCountry();
        return county.equals("US") || county.equals("CA") || county.equals("MX");
    }

    private int resolveAutoPageWidth(CssContext cssCtx) {
        if (this.isUseLetterSize()) {
            return (int)LengthValue.calcFloatProportionalValue(this.getStyle(), CSSName.FS_PAGE_WIDTH, "8.5in", 8.5f, (short)8, 0.0f, cssCtx);
        }
        return (int)LengthValue.calcFloatProportionalValue(this.getStyle(), CSSName.FS_PAGE_WIDTH, "210mm", 210.0f, (short)7, 0.0f, cssCtx);
    }

    private int resolveAutoPageHeight(CssContext cssCtx) {
        if (this.isUseLetterSize()) {
            return (int)LengthValue.calcFloatProportionalValue(this.getStyle(), CSSName.FS_PAGE_HEIGHT, "11in", 11.0f, (short)8, 0.0f, cssCtx);
        }
        return (int)LengthValue.calcFloatProportionalValue(this.getStyle(), CSSName.FS_PAGE_HEIGHT, "297mm", 297.0f, (short)7, 0.0f, cssCtx);
    }

    public int getContentHeight(CssContext cssCtx) {
        int retval = this.getHeight(cssCtx) - this.getMarginBorderPadding(cssCtx, 3) - this.getMarginBorderPadding(cssCtx, 4);
        if (retval <= 0) {
            throw new IllegalArgumentException("The content height cannot be zero or less.  Check your document margin definition.");
        }
        return retval;
    }

    public int getContentWidth(CssContext cssCtx) {
        int retval = this.getWidth(cssCtx) - this.getMarginBorderPadding(cssCtx, 1) - this.getMarginBorderPadding(cssCtx, 2);
        if (retval <= 0) {
            throw new IllegalArgumentException("The content width cannot be zero or less.  Check your document margin definition.");
        }
        return retval;
    }

    public CalculatedStyle getStyle() {
        return this._style;
    }

    public void setStyle(CalculatedStyle style) {
        this._style = style;
    }

    public int getBottom() {
        return this._bottom;
    }

    public int getTop() {
        return this._top;
    }

    public void setTopAndBottom(CssContext cssCtx, int top) {
        this._top = top;
        this._bottom = top + this.getContentHeight(cssCtx);
    }

    public int getPaintingBottom() {
        return this._paintingBottom;
    }

    public void setPaintingBottom(int paintingBottom) {
        this._paintingBottom = paintingBottom;
    }

    public int getPaintingTop() {
        return this._paintingTop;
    }

    public void setPaintingTop(int paintingTop) {
        this._paintingTop = paintingTop;
    }

    public Rectangle getScreenPaintingBounds(CssContext cssCtx, int additionalClearance) {
        return new Rectangle(additionalClearance, this.getPaintingTop(), this.getWidth(cssCtx), this.getPaintingBottom() - this.getPaintingTop());
    }

    public Rectangle getPrintPaintingBounds(CssContext cssCtx) {
        return new Rectangle(0, 0, this.getWidth(cssCtx), this.getHeight(cssCtx));
    }

    public Rectangle getPagedViewClippingBounds(CssContext cssCtx, int additionalClearance) {
        Rectangle result = new Rectangle(additionalClearance + this.getMarginBorderPadding(cssCtx, 1), this.getPaintingTop() + this.getMarginBorderPadding(cssCtx, 3), this.getContentWidth(cssCtx), this.getContentHeight(cssCtx));
        return result;
    }

    public Rectangle getPrintClippingBounds(CssContext cssCtx) {
        Rectangle result = new Rectangle(this.getMarginBorderPadding(cssCtx, 1), this.getMarginBorderPadding(cssCtx, 3), this.getContentWidth(cssCtx), this.getContentHeight(cssCtx));
        --result.height;
        return result;
    }

    public RectPropertySet getMargin(CssContext cssCtx) {
        return this.getStyle().getMarginRect(this._outerPageWidth, cssCtx);
    }

    private Rectangle getBorderEdge(int left, int top, CssContext cssCtx) {
        RectPropertySet margin = this.getMargin(cssCtx);
        Rectangle result = new Rectangle(left + (int)margin.left(), top + (int)margin.top(), this.getWidth(cssCtx) - (int)margin.left() - (int)margin.right(), this.getHeight(cssCtx) - (int)margin.top() - (int)margin.bottom());
        return result;
    }

    public void paintBorder(RenderingContext c, int additionalClearance, short mode) {
        int top = 0;
        if (mode == 1) {
            top = this.getPaintingTop();
        }
        c.getOutputDevice().paintBorder(c, this.getStyle(), this.getBorderEdge(additionalClearance, top, c), 15);
    }

    public void paintBackground(RenderingContext c, int additionalClearance, short mode) {
        Rectangle bounds = mode == 1 ? this.getScreenPaintingBounds(c, additionalClearance) : this.getPrintPaintingBounds(c);
        c.getOutputDevice().paintBackground(c, this.getStyle(), bounds, bounds, this.getStyle().getBorder(c));
    }

    public void paintMarginAreas(RenderingContext c, int additionalClearance, short mode) {
        for (int i = 0; i < MARGIN_AREA_DEFS.length; ++i) {
            MarginAreaContainer container = this._marginAreas[i];
            if (container == null) continue;
            TableBox table = this._marginAreas[i].getTable();
            Point p = container.getArea().getPaintingPosition(c, this, additionalClearance, mode);
            c.getOutputDevice().translate(p.x, p.y);
            table.getLayer().paint(c);
            c.getOutputDevice().translate(-p.x, -p.y);
        }
    }

    public int getPageNo() {
        return this._pageNo;
    }

    public void setPageNo(int pageNo) {
        this._pageNo = pageNo;
    }

    public int getOuterPageWidth() {
        return this._outerPageWidth;
    }

    public void setOuterPageWidth(int containingBlockWidth) {
        this._outerPageWidth = containingBlockWidth;
    }

    public int getMarginBorderPadding(CssContext cssCtx, int which) {
        return this.getStyle().getMarginBorderPadding(cssCtx, this.getOuterPageWidth(), which);
    }

    public PageInfo getPageInfo() {
        return this._pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this._pageInfo = pageInfo;
    }

    public Element getMetadata() {
        return this._metadata;
    }

    public void layout(LayoutContext c) {
        c.setPage(this);
        this.retrievePageMetadata(c);
        this.layoutMarginAreas(c);
    }

    private void retrievePageMetadata(LayoutContext c) {
        List props = this.getPageInfo().getXMPPropertyList();
        if (props != null && props.size() > 0) {
            for (PropertyDeclaration decl : props) {
                BlockBox metadata;
                FSFunction func;
                PropertyValue funcVal;
                if (decl.getCSSName() != CSSName.CONTENT) continue;
                PropertyValue value = (PropertyValue)decl.getValue();
                List values = value.getValues();
                if (values.size() != 1 || (funcVal = (PropertyValue)values.get(0)).getPropertyValueType() != 7 || !BoxBuilder.isElementFunction(func = funcVal.getFunction()) || (metadata = BoxBuilder.getRunningBlock(c, funcVal)) == null) break;
                this._metadata = metadata.getElement();
                break;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void layoutMarginAreas(LayoutContext c) {
        RectPropertySet margin = this.getMargin(c);
        for (int i = 0; i < MARGIN_AREA_DEFS.length; ++i) {
            MarginArea area = MARGIN_AREA_DEFS[i];
            Dimension dim = area.getLayoutDimension(c, this, margin);
            TableBox table = BoxBuilder.createMarginTable(c, this._pageInfo, area.getMarginBoxNames(), (int)dim.getHeight(), area.getDirection());
            if (table == null) continue;
            table.setContainingBlock(new MarginBox(new Rectangle((int)dim.getWidth(), (int)dim.getHeight())));
            try {
                c.setNoPageBreak(1);
                c.reInit(false);
                c.pushLayer(table);
                c.getRootLayer().addPage(c);
                table.layout(c);
                c.popLayer();
            }
            finally {
                c.setNoPageBreak(0);
            }
            this._marginAreas[i] = new MarginAreaContainer(area, table);
        }
    }

    public boolean isLeftPage() {
        return this._pageNo % 2 != 0;
    }

    public boolean isRightPage() {
        return this._pageNo % 2 == 0;
    }

    public void exportLeadingText(RenderingContext c, Writer writer) throws IOException {
        for (int i = 0; i < 5; ++i) {
            MarginAreaContainer container = this._marginAreas[i];
            if (container == null) continue;
            container.getTable().exportText(c, writer);
        }
    }

    public void exportTrailingText(RenderingContext c, Writer writer) throws IOException {
        for (int i = 5; i < this._marginAreas.length; ++i) {
            MarginAreaContainer container = this._marginAreas[i];
            if (container == null) continue;
            container.getTable().exportText(c, writer);
        }
    }

    private static class BottomMarginArea
    extends MarginArea {
        public BottomMarginArea() {
            super(new MarginBoxName[]{MarginBoxName.BOTTOM_LEFT, MarginBoxName.BOTTOM_CENTER, MarginBoxName.BOTTOM_RIGHT});
        }

        @Override
        public Dimension getLayoutDimension(CssContext c, PageBox page, RectPropertySet margin) {
            return new Dimension(page.getContentWidth(c), (int)margin.bottom());
        }

        @Override
        public Point getPaintingPosition(RenderingContext c, PageBox page, int additionalClearance, short mode) {
            int top;
            int left = additionalClearance + (int)page.getMargin(c).left();
            if (mode == 1) {
                top = page.getPaintingBottom() - (int)page.getMargin(c).bottom();
            } else if (mode == 2) {
                top = page.getHeight(c) - (int)page.getMargin(c).bottom();
            } else {
                throw new IllegalArgumentException("Illegal mode");
            }
            return new Point(left, top);
        }
    }

    private static class TopMarginArea
    extends MarginArea {
        public TopMarginArea() {
            super(new MarginBoxName[]{MarginBoxName.TOP_LEFT, MarginBoxName.TOP_CENTER, MarginBoxName.TOP_RIGHT});
        }

        @Override
        public Dimension getLayoutDimension(CssContext c, PageBox page, RectPropertySet margin) {
            return new Dimension(page.getContentWidth(c), (int)margin.top());
        }

        @Override
        public Point getPaintingPosition(RenderingContext c, PageBox page, int additionalClearance, short mode) {
            int top;
            int left = additionalClearance + (int)page.getMargin(c).left();
            if (mode == 1) {
                top = page.getPaintingTop();
            } else if (mode == 2) {
                top = 0;
            } else {
                throw new IllegalArgumentException("Illegal mode");
            }
            return new Point(left, top);
        }
    }

    private static class RightMarginArea
    extends MarginArea {
        public RightMarginArea() {
            super(new MarginBoxName[]{MarginBoxName.RIGHT_TOP, MarginBoxName.RIGHT_MIDDLE, MarginBoxName.RIGHT_BOTTOM});
        }

        @Override
        public Dimension getLayoutDimension(CssContext c, PageBox page, RectPropertySet margin) {
            return new Dimension((int)margin.left(), page.getContentHeight(c));
        }

        @Override
        public Point getPaintingPosition(RenderingContext c, PageBox page, int additionalClearance, short mode) {
            int top;
            int left = additionalClearance + page.getWidth(c) - (int)page.getMargin(c).right();
            if (mode == 1) {
                top = page.getPaintingTop() + (int)page.getMargin(c).top();
            } else if (mode == 2) {
                top = (int)page.getMargin(c).top();
            } else {
                throw new IllegalArgumentException("Illegal mode");
            }
            return new Point(left, top);
        }

        @Override
        public int getDirection() {
            return 1;
        }
    }

    private static class LeftMarginArea
    extends MarginArea {
        public LeftMarginArea() {
            super(new MarginBoxName[]{MarginBoxName.LEFT_TOP, MarginBoxName.LEFT_MIDDLE, MarginBoxName.LEFT_BOTTOM});
        }

        @Override
        public Dimension getLayoutDimension(CssContext c, PageBox page, RectPropertySet margin) {
            return new Dimension((int)margin.left(), page.getContentHeight(c));
        }

        @Override
        public Point getPaintingPosition(RenderingContext c, PageBox page, int additionalClearance, short mode) {
            int top;
            int left = additionalClearance;
            if (mode == 1) {
                top = page.getPaintingTop() + (int)page.getMargin(c).top();
            } else if (mode == 2) {
                top = (int)page.getMargin(c).top();
            } else {
                throw new IllegalArgumentException("Illegal mode");
            }
            return new Point(left, top);
        }

        @Override
        public int getDirection() {
            return 1;
        }
    }

    private static class BottomLeftCorner
    extends MarginArea {
        public BottomLeftCorner() {
            super(MarginBoxName.BOTTOM_LEFT_CORNER);
        }

        @Override
        public Dimension getLayoutDimension(CssContext c, PageBox page, RectPropertySet margin) {
            return new Dimension((int)margin.left(), (int)margin.bottom());
        }

        @Override
        public Point getPaintingPosition(RenderingContext c, PageBox page, int additionalClearance, short mode) {
            int top;
            int left = additionalClearance;
            if (mode == 1) {
                top = page.getPaintingBottom() - (int)page.getMargin(c).bottom();
            } else if (mode == 2) {
                top = page.getHeight(c) - (int)page.getMargin(c).bottom();
            } else {
                throw new IllegalArgumentException("Illegal mode");
            }
            return new Point(left, top);
        }
    }

    private static class BottomRightCorner
    extends MarginArea {
        public BottomRightCorner() {
            super(MarginBoxName.BOTTOM_RIGHT_CORNER);
        }

        @Override
        public Dimension getLayoutDimension(CssContext c, PageBox page, RectPropertySet margin) {
            return new Dimension((int)margin.right(), (int)margin.bottom());
        }

        @Override
        public Point getPaintingPosition(RenderingContext c, PageBox page, int additionalClearance, short mode) {
            int top;
            int left = additionalClearance + page.getWidth(c) - (int)page.getMargin(c).right();
            if (mode == 1) {
                top = page.getPaintingBottom() - (int)page.getMargin(c).bottom();
            } else if (mode == 2) {
                top = page.getHeight(c) - (int)page.getMargin(c).bottom();
            } else {
                throw new IllegalArgumentException("Illegal mode");
            }
            return new Point(left, top);
        }
    }

    private static class TopRightCorner
    extends MarginArea {
        public TopRightCorner() {
            super(MarginBoxName.TOP_RIGHT_CORNER);
        }

        @Override
        public Dimension getLayoutDimension(CssContext c, PageBox page, RectPropertySet margin) {
            return new Dimension((int)margin.right(), (int)margin.top());
        }

        @Override
        public Point getPaintingPosition(RenderingContext c, PageBox page, int additionalClearance, short mode) {
            int top;
            int left = additionalClearance + page.getWidth(c) - (int)page.getMargin(c).right();
            if (mode == 1) {
                top = page.getPaintingTop();
            } else if (mode == 2) {
                top = 0;
            } else {
                throw new IllegalArgumentException("Illegal mode");
            }
            return new Point(left, top);
        }
    }

    private static class TopLeftCorner
    extends MarginArea {
        public TopLeftCorner() {
            super(MarginBoxName.TOP_LEFT_CORNER);
        }

        @Override
        public Dimension getLayoutDimension(CssContext c, PageBox page, RectPropertySet margin) {
            return new Dimension((int)margin.left(), (int)margin.top());
        }

        @Override
        public Point getPaintingPosition(RenderingContext c, PageBox page, int additionalClearance, short mode) {
            int top;
            int left = additionalClearance;
            if (mode == 1) {
                top = page.getPaintingTop();
            } else if (mode == 2) {
                top = 0;
            } else {
                throw new IllegalArgumentException("Illegal mode");
            }
            return new Point(left, top);
        }
    }

    private static abstract class MarginArea {
        private final MarginBoxName[] _marginBoxNames;
        private TableBox _table;

        public abstract Dimension getLayoutDimension(CssContext var1, PageBox var2, RectPropertySet var3);

        public abstract Point getPaintingPosition(RenderingContext var1, PageBox var2, int var3, short var4);

        public MarginArea(MarginBoxName marginBoxName) {
            this._marginBoxNames = new MarginBoxName[]{marginBoxName};
        }

        public MarginArea(MarginBoxName[] marginBoxNames) {
            this._marginBoxNames = marginBoxNames;
        }

        public TableBox getTable() {
            return this._table;
        }

        public void setTable(TableBox table) {
            this._table = table;
        }

        public MarginBoxName[] getMarginBoxNames() {
            return this._marginBoxNames;
        }

        public int getDirection() {
            return 2;
        }
    }

    private static class MarginAreaContainer {
        private final MarginArea _area;
        private final TableBox _table;

        public MarginAreaContainer(MarginArea area, TableBox table) {
            this._area = area;
            this._table = table;
        }

        public MarginArea getArea() {
            return this._area;
        }

        public TableBox getTable() {
            return this._table;
        }
    }

    private static final class PageDimensions {
        private int _width;
        private int _height;

        private PageDimensions() {
        }

        public int getHeight() {
            return this._height;
        }

        public void setHeight(int height) {
            this._height = height;
        }

        public int getWidth() {
            return this._width;
        }

        public void setWidth(int width) {
            this._width = width;
        }
    }
}

