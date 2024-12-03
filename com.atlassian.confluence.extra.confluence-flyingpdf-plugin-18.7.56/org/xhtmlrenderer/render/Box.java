/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.PaintingInfo;
import org.xhtmlrenderer.layout.Styleable;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.BoxDimensions;
import org.xhtmlrenderer.render.ContentLimitContainer;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.util.XRLog;

public abstract class Box
implements Styleable {
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private Element _element;
    private int _x;
    private int _y;
    private int _absY;
    private int _absX;
    private int _contentWidth;
    private int _rightMBP = 0;
    private int _leftMBP = 0;
    private int _height;
    private Layer _layer = null;
    private Layer _containingLayer;
    private Box _parent;
    private List _boxes;
    private int _tx;
    private int _ty;
    private CalculatedStyle _style;
    private Box _containingBlock;
    private Dimension _relativeOffset;
    private PaintingInfo _paintingInfo;
    private RectPropertySet _workingMargin;
    private int _index;
    private String _pseudoElementOrClass;
    private boolean _anonymous;
    public static final int NOTHING = 0;
    public static final int FLUX = 1;
    public static final int CHILDREN_FLUX = 2;
    public static final int DONE = 3;
    private int _state = 0;
    public static final int DUMP_RENDER = 2;
    public static final int DUMP_LAYOUT = 1;

    protected Box() {
    }

    public abstract String dump(LayoutContext var1, String var2, int var3);

    protected void dumpBoxes(LayoutContext c, String indent, List boxes, int which, StringBuffer result) {
        Iterator i = boxes.iterator();
        while (i.hasNext()) {
            Box b = (Box)i.next();
            result.append(b.dump(c, indent + "  ", which));
            if (!i.hasNext()) continue;
            result.append('\n');
        }
    }

    public int getWidth() {
        return this.getContentWidth() + this.getLeftMBP() + this.getRightMBP();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Box: ");
        sb.append(" (" + this.getAbsX() + "," + this.getAbsY() + ")->(" + this.getWidth() + " x " + this.getHeight() + ")");
        return sb.toString();
    }

    public void addChildForLayout(LayoutContext c, Box child) {
        this.addChild(child);
        child.initContainingLayer(c);
    }

    public void addChild(Box child) {
        if (this._boxes == null) {
            this._boxes = new ArrayList();
        }
        if (child == null) {
            throw new NullPointerException("trying to add null child");
        }
        child.setParent(this);
        child.setIndex(this._boxes.size());
        this._boxes.add(child);
    }

    public void addAllChildren(List children) {
        for (Box box : children) {
            this.addChild(box);
        }
    }

    public void removeAllChildren() {
        if (this._boxes != null) {
            this._boxes.clear();
        }
    }

    public void removeChild(Box target) {
        if (this._boxes != null) {
            boolean found = false;
            Iterator i = this.getChildIterator();
            while (i.hasNext()) {
                Box child = (Box)i.next();
                if (child.equals(target)) {
                    i.remove();
                    found = true;
                    continue;
                }
                if (!found) continue;
                child.setIndex(child.getIndex() - 1);
            }
        }
    }

    public Box getPreviousSibling() {
        Box parent = this.getParent();
        return parent == null ? null : parent.getPrevious(this);
    }

    public Box getNextSibling() {
        Box parent = this.getParent();
        return parent == null ? null : parent.getNext(this);
    }

    protected Box getPrevious(Box child) {
        return child.getIndex() == 0 ? null : this.getChild(child.getIndex() - 1);
    }

    protected Box getNext(Box child) {
        return child.getIndex() == this.getChildCount() - 1 ? null : this.getChild(child.getIndex() + 1);
    }

    public void removeChild(int i) {
        if (this._boxes != null) {
            this.removeChild(this.getChild(i));
        }
    }

    public void setParent(Box box) {
        this._parent = box;
    }

    public Box getParent() {
        return this._parent;
    }

    public Box getDocumentParent() {
        return this.getParent();
    }

    public int getChildCount() {
        return this._boxes == null ? 0 : this._boxes.size();
    }

    public Box getChild(int i) {
        if (this._boxes == null) {
            throw new IndexOutOfBoundsException();
        }
        return (Box)this._boxes.get(i);
    }

    public Iterator getChildIterator() {
        return this._boxes == null ? Collections.EMPTY_LIST.iterator() : this._boxes.iterator();
    }

    public List getChildren() {
        return this._boxes == null ? Collections.EMPTY_LIST : this._boxes;
    }

    public synchronized int getState() {
        return this._state;
    }

    public synchronized void setState(int state) {
        this._state = state;
    }

    public static String stateToString(int state) {
        switch (state) {
            case 0: {
                return "NOTHING";
            }
            case 1: {
                return "FLUX";
            }
            case 2: {
                return "CHILDREN_FLUX";
            }
            case 3: {
                return "DONE";
            }
        }
        return "unknown";
    }

    @Override
    public final CalculatedStyle getStyle() {
        return this._style;
    }

    @Override
    public void setStyle(CalculatedStyle style) {
        this._style = style;
    }

    public Box getContainingBlock() {
        return this._containingBlock == null ? this.getParent() : this._containingBlock;
    }

    public void setContainingBlock(Box containingBlock) {
        this._containingBlock = containingBlock;
    }

    public Rectangle getMarginEdge(int left, int top, CssContext cssCtx, int tx, int ty) {
        Rectangle result = new Rectangle(left, top, this.getWidth(), this.getHeight());
        result.translate(tx, ty);
        return result;
    }

    public Rectangle getMarginEdge(CssContext cssCtx, int tx, int ty) {
        return this.getMarginEdge(this.getX(), this.getY(), cssCtx, tx, ty);
    }

    public Rectangle getPaintingBorderEdge(CssContext cssCtx) {
        return this.getBorderEdge(this.getAbsX(), this.getAbsY(), cssCtx);
    }

    public Rectangle getPaintingPaddingEdge(CssContext cssCtx) {
        return this.getPaddingEdge(this.getAbsX(), this.getAbsY(), cssCtx);
    }

    public Rectangle getPaintingClipEdge(CssContext cssCtx) {
        return this.getPaintingBorderEdge(cssCtx);
    }

    public Rectangle getChildrenClipEdge(RenderingContext c) {
        return this.getPaintingPaddingEdge(c);
    }

    public boolean intersects(CssContext cssCtx, Shape clip) {
        return clip == null || clip.intersects(this.getPaintingClipEdge(cssCtx));
    }

    public Rectangle getBorderEdge(int left, int top, CssContext cssCtx) {
        RectPropertySet margin = this.getMargin(cssCtx);
        Rectangle result = new Rectangle(left + (int)margin.left(), top + (int)margin.top(), this.getWidth() - (int)margin.left() - (int)margin.right(), this.getHeight() - (int)margin.top() - (int)margin.bottom());
        return result;
    }

    public Rectangle getPaddingEdge(int left, int top, CssContext cssCtx) {
        RectPropertySet margin = this.getMargin(cssCtx);
        BorderPropertySet border = this.getBorder(cssCtx);
        Rectangle result = new Rectangle(left + (int)margin.left() + (int)border.left(), top + (int)margin.top() + (int)border.top(), this.getWidth() - (int)margin.width() - (int)border.width(), this.getHeight() - (int)margin.height() - (int)border.height());
        return result;
    }

    protected int getPaddingWidth(CssContext cssCtx) {
        RectPropertySet padding = this.getPadding(cssCtx);
        return (int)padding.left() + this.getContentWidth() + (int)padding.right();
    }

    public Rectangle getContentAreaEdge(int left, int top, CssContext cssCtx) {
        RectPropertySet margin = this.getMargin(cssCtx);
        BorderPropertySet border = this.getBorder(cssCtx);
        RectPropertySet padding = this.getPadding(cssCtx);
        Rectangle result = new Rectangle(left + (int)margin.left() + (int)border.left() + (int)padding.left(), top + (int)margin.top() + (int)border.top() + (int)padding.top(), this.getWidth() - (int)margin.width() - (int)border.width() - (int)padding.width(), this.getHeight() - (int)margin.height() - (int)border.height() - (int)padding.height());
        return result;
    }

    public Layer getLayer() {
        return this._layer;
    }

    public void setLayer(Layer layer) {
        this._layer = layer;
    }

    public Dimension positionRelative(CssContext cssCtx) {
        int initialX = this.getX();
        int initialY = this.getY();
        CalculatedStyle style = this.getStyle();
        if (!style.isIdent(CSSName.LEFT, IdentValue.AUTO)) {
            this.setX(this.getX() + (int)style.getFloatPropertyProportionalWidth(CSSName.LEFT, this.getContainingBlock().getContentWidth(), cssCtx));
        } else if (!style.isIdent(CSSName.RIGHT, IdentValue.AUTO)) {
            this.setX(this.getX() - (int)style.getFloatPropertyProportionalWidth(CSSName.RIGHT, this.getContainingBlock().getContentWidth(), cssCtx));
        }
        int cbContentHeight = 0;
        if (!this.getContainingBlock().getStyle().isAutoHeight()) {
            CalculatedStyle cbStyle = this.getContainingBlock().getStyle();
            cbContentHeight = (int)cbStyle.getFloatPropertyProportionalHeight(CSSName.HEIGHT, 0.0f, cssCtx);
        } else if (this.isInlineBlock()) {
            cbContentHeight = this.getContainingBlock().getHeight();
        }
        if (!style.isIdent(CSSName.TOP, IdentValue.AUTO)) {
            this.setY(this.getY() + (int)style.getFloatPropertyProportionalHeight(CSSName.TOP, cbContentHeight, cssCtx));
        } else if (!style.isIdent(CSSName.BOTTOM, IdentValue.AUTO)) {
            this.setY(this.getY() - (int)style.getFloatPropertyProportionalHeight(CSSName.BOTTOM, cbContentHeight, cssCtx));
        }
        this.setRelativeOffset(new Dimension(this.getX() - initialX, this.getY() - initialY));
        return this.getRelativeOffset();
    }

    protected boolean isInlineBlock() {
        return false;
    }

    public void setAbsY(int absY) {
        this._absY = absY;
    }

    public int getAbsY() {
        return this._absY;
    }

    public void setAbsX(int absX) {
        this._absX = absX;
    }

    public int getAbsX() {
        return this._absX;
    }

    public boolean isStyled() {
        return this._style != null;
    }

    public int getBorderSides() {
        return 15;
    }

    public void paintBorder(RenderingContext c) {
        c.getOutputDevice().paintBorder(c, this);
    }

    private boolean isPaintsRootElementBackground() {
        return this.isRoot() && this.getStyle().isHasBackground() || this.isBody() && !this.getParent().getStyle().isHasBackground();
    }

    public void paintBackground(RenderingContext c) {
        if (!this.isPaintsRootElementBackground()) {
            c.getOutputDevice().paintBackground(c, this);
        }
    }

    public void paintRootElementBackground(RenderingContext c) {
        PaintingInfo pI = this.getPaintingInfo();
        if (pI != null) {
            if (this.getStyle().isHasBackground()) {
                this.paintRootElementBackground(c, pI);
            } else if (this.getChildCount() > 0) {
                Box body = this.getChild(0);
                body.paintRootElementBackground(c, pI);
            }
        }
    }

    private void paintRootElementBackground(RenderingContext c, PaintingInfo pI) {
        Dimension marginCorner = pI.getOuterMarginCorner();
        Rectangle canvasBounds = new Rectangle(0, 0, marginCorner.width, marginCorner.height);
        canvasBounds.add(c.getViewportRectangle());
        c.getOutputDevice().paintBackground(c, this.getStyle(), canvasBounds, canvasBounds, BorderPropertySet.EMPTY_BORDER);
    }

    public Layer getContainingLayer() {
        return this._containingLayer;
    }

    public void setContainingLayer(Layer containingLayer) {
        this._containingLayer = containingLayer;
    }

    public void initContainingLayer(LayoutContext c) {
        if (this.getLayer() != null) {
            this.setContainingLayer(this.getLayer());
        } else if (this.getContainingLayer() == null) {
            List content;
            if (this.getParent() == null || this.getParent().getContainingLayer() == null) {
                throw new RuntimeException("internal error");
            }
            this.setContainingLayer(this.getParent().getContainingLayer());
            if (c.getLayer().isInline() && (content = ((InlineLayoutBox)c.getLayer().getMaster()).getElementWithContent()).contains(this)) {
                this.setContainingLayer(c.getLayer());
            }
        }
    }

    public void connectChildrenToCurrentLayer(LayoutContext c) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            Box box = this.getChild(i);
            box.setContainingLayer(c.getLayer());
            box.connectChildrenToCurrentLayer(c);
        }
    }

    public List getElementBoxes(Element elem) {
        ArrayList<Box> result = new ArrayList<Box>();
        for (int i = 0; i < this.getChildCount(); ++i) {
            Box child = this.getChild(i);
            if (child.getElement() == elem) {
                result.add(child);
            }
            result.addAll(child.getElementBoxes(elem));
        }
        return result;
    }

    public void reset(LayoutContext c) {
        String id;
        Element e;
        this.resetChildren(c);
        if (this._layer != null) {
            this._layer.detach();
            this._layer = null;
        }
        this.setContainingLayer(null);
        this.setLayer(null);
        this.setPaintingInfo(null);
        this.setContentWidth(0);
        this._workingMargin = null;
        String anchorName = c.getNamespaceHandler().getAnchorName(this.getElement());
        if (anchorName != null) {
            c.removeBoxId(anchorName);
        }
        if ((e = this.getElement()) != null && (id = c.getNamespaceHandler().getID(e)) != null) {
            c.removeBoxId(id);
        }
    }

    public void detach(LayoutContext c) {
        this.reset(c);
        if (this.getParent() != null) {
            this.getParent().removeChild(this);
            this.setParent(null);
        }
    }

    public void resetChildren(LayoutContext c, int start, int end) {
        for (int i = start; i <= end; ++i) {
            Box box = this.getChild(i);
            box.reset(c);
        }
    }

    protected void resetChildren(LayoutContext c) {
        int remaining = this.getChildCount();
        for (int i = 0; i < remaining; ++i) {
            Box box = this.getChild(i);
            box.reset(c);
        }
    }

    public abstract void calcCanvasLocation();

    public void calcChildLocations() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            Box child = this.getChild(i);
            child.calcCanvasLocation();
            child.calcChildLocations();
        }
    }

    public int forcePageBreakBefore(LayoutContext c, IdentValue pageBreakValue, boolean pendingPageName) {
        PageBox page = c.getRootLayer().getFirstPage(c, this);
        if (page == null) {
            XRLog.layout(Level.WARNING, "Box has no page");
            return 0;
        }
        int pageBreakCount = 1;
        if (page.getTop() == this.getAbsY()) {
            --pageBreakCount;
            if (pendingPageName && page == c.getRootLayer().getLastPage()) {
                c.getRootLayer().removeLastPage();
                c.setPageName(c.getPendingPageName());
                c.getRootLayer().addPage(c);
            }
        }
        if (page.isLeftPage() && pageBreakValue == IdentValue.LEFT || page.isRightPage() && pageBreakValue == IdentValue.RIGHT) {
            ++pageBreakCount;
        }
        if (pageBreakCount == 0) {
            return 0;
        }
        if (pageBreakCount == 1 && pendingPageName) {
            c.setPageName(c.getPendingPageName());
        }
        int delta = page.getBottom() + c.getExtraSpaceTop() - this.getAbsY();
        if (page == c.getRootLayer().getLastPage()) {
            c.getRootLayer().addPage(c);
        }
        if (pageBreakCount == 2) {
            page = (PageBox)c.getRootLayer().getPages().get(page.getPageNo() + 1);
            delta += page.getContentHeight(c);
            if (pageBreakCount == 2 && pendingPageName) {
                c.setPageName(c.getPendingPageName());
            }
            if (page == c.getRootLayer().getLastPage()) {
                c.getRootLayer().addPage(c);
            }
        }
        this.setY(this.getY() + delta);
        return delta;
    }

    public void forcePageBreakAfter(LayoutContext c, IdentValue pageBreakValue) {
        boolean needSecondPageBreak = false;
        PageBox page = c.getRootLayer().getLastPage(c, this);
        if (page != null) {
            if (page.isLeftPage() && pageBreakValue == IdentValue.LEFT || page.isRightPage() && pageBreakValue == IdentValue.RIGHT) {
                needSecondPageBreak = true;
            }
            int delta = page.getBottom() + c.getExtraSpaceTop() - (this.getAbsY() + this.getMarginBorderPadding(c, 3) + this.getHeight());
            if (page == c.getRootLayer().getLastPage()) {
                c.getRootLayer().addPage(c);
            }
            if (needSecondPageBreak) {
                page = (PageBox)c.getRootLayer().getPages().get(page.getPageNo() + 1);
                delta += page.getContentHeight(c);
                if (page == c.getRootLayer().getLastPage()) {
                    c.getRootLayer().addPage(c);
                }
            }
            this.setHeight(this.getHeight() + delta);
        }
    }

    public boolean crossesPageBreak(LayoutContext c) {
        if (!c.isPageBreaksAllowed()) {
            return false;
        }
        PageBox pageBox = c.getRootLayer().getFirstPage(c, this);
        if (pageBox == null) {
            return false;
        }
        return this.getAbsY() + this.getHeight() >= pageBox.getBottom() - c.getExtraSpaceBottom();
    }

    public Dimension getRelativeOffset() {
        return this._relativeOffset;
    }

    public void setRelativeOffset(Dimension relativeOffset) {
        this._relativeOffset = relativeOffset;
    }

    public Box find(CssContext cssCtx, int absX, int absY, boolean findAnonymous) {
        PaintingInfo pI = this.getPaintingInfo();
        if (pI != null && !pI.getAggregateBounds().contains(absX, absY)) {
            return null;
        }
        Box result = null;
        for (int i = 0; i < this.getChildCount(); ++i) {
            Box child = this.getChild(i);
            result = child.find(cssCtx, absX, absY, findAnonymous);
            if (result == null) continue;
            return result;
        }
        Rectangle edge = this.getContentAreaEdge(this.getAbsX(), this.getAbsY(), cssCtx);
        return edge.contains(absX, absY) && this.getStyle().isVisible() ? this : null;
    }

    public boolean isRoot() {
        return this.getElement() != null && !this.isAnonymous() && this.getElement().getParentNode().getNodeType() == 9;
    }

    public boolean isBody() {
        return this.getParent() != null && this.getParent().isRoot();
    }

    @Override
    public Element getElement() {
        return this._element;
    }

    @Override
    public void setElement(Element element) {
        this._element = element;
    }

    public void setMarginTop(CssContext cssContext, int marginTop) {
        this.ensureWorkingMargin(cssContext);
        this._workingMargin.setTop(marginTop);
    }

    public void setMarginBottom(CssContext cssContext, int marginBottom) {
        this.ensureWorkingMargin(cssContext);
        this._workingMargin.setBottom(marginBottom);
    }

    public void setMarginLeft(CssContext cssContext, int marginLeft) {
        this.ensureWorkingMargin(cssContext);
        this._workingMargin.setLeft(marginLeft);
    }

    public void setMarginRight(CssContext cssContext, int marginRight) {
        this.ensureWorkingMargin(cssContext);
        this._workingMargin.setRight(marginRight);
    }

    private void ensureWorkingMargin(CssContext cssContext) {
        if (this._workingMargin == null) {
            this._workingMargin = this.getStyleMargin(cssContext).copyOf();
        }
    }

    public RectPropertySet getMargin(CssContext cssContext) {
        return this._workingMargin != null ? this._workingMargin : this.getStyleMargin(cssContext);
    }

    protected RectPropertySet getStyleMargin(CssContext cssContext) {
        return this.getStyle().getMarginRect(this.getContainingBlockWidth(), cssContext);
    }

    protected RectPropertySet getStyleMargin(CssContext cssContext, boolean useCache) {
        return this.getStyle().getMarginRect(this.getContainingBlockWidth(), cssContext, useCache);
    }

    public RectPropertySet getPadding(CssContext cssCtx) {
        return this.getStyle().getPaddingRect(this.getContainingBlockWidth(), cssCtx);
    }

    public BorderPropertySet getBorder(CssContext cssCtx) {
        return this.getStyle().getBorder(cssCtx);
    }

    protected int getContainingBlockWidth() {
        return this.getContainingBlock().getContentWidth();
    }

    protected void resetTopMargin(CssContext cssContext) {
        if (this._workingMargin != null) {
            RectPropertySet styleMargin = this.getStyleMargin(cssContext);
            this._workingMargin.setTop(styleMargin.top());
        }
    }

    public void clearSelection(List modified) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            Box child = this.getChild(i);
            child.clearSelection(modified);
        }
    }

    public void selectAll() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            Box child = this.getChild(i);
            child.selectAll();
        }
    }

    public PaintingInfo calcPaintingInfo(CssContext c, boolean useCache) {
        PaintingInfo cached = this.getPaintingInfo();
        if (cached != null && useCache) {
            return cached;
        }
        PaintingInfo result = new PaintingInfo();
        Rectangle bounds = this.getMarginEdge(this.getAbsX(), this.getAbsY(), c, 0, 0);
        result.setOuterMarginCorner(new Dimension(bounds.x + bounds.width, bounds.y + bounds.height));
        result.setAggregateBounds(this.getPaintingClipEdge(c));
        if (!this.getStyle().isOverflowApplies() || this.getStyle().isOverflowVisible()) {
            this.calcChildPaintingInfo(c, result, useCache);
        }
        this.setPaintingInfo(result);
        return result;
    }

    protected void calcChildPaintingInfo(CssContext c, PaintingInfo result, boolean useCache) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            Box child = this.getChild(i);
            PaintingInfo info = child.calcPaintingInfo(c, useCache);
            this.moveIfGreater(result.getOuterMarginCorner(), info.getOuterMarginCorner());
            result.getAggregateBounds().add(info.getAggregateBounds());
        }
    }

    public int getMarginBorderPadding(CssContext cssCtx, int which) {
        BorderPropertySet border = this.getBorder(cssCtx);
        RectPropertySet margin = this.getMargin(cssCtx);
        RectPropertySet padding = this.getPadding(cssCtx);
        switch (which) {
            case 1: {
                return (int)(margin.left() + border.left() + padding.left());
            }
            case 2: {
                return (int)(margin.right() + border.right() + padding.right());
            }
            case 3: {
                return (int)(margin.top() + border.top() + padding.top());
            }
            case 4: {
                return (int)(margin.bottom() + border.bottom() + padding.bottom());
            }
        }
        throw new IllegalArgumentException();
    }

    protected void moveIfGreater(Dimension result, Dimension test) {
        if (test.width > result.width) {
            result.width = test.width;
        }
        if (test.height > result.height) {
            result.height = test.height;
        }
    }

    public void restyle(LayoutContext c) {
        Element e = this.getElement();
        CalculatedStyle style = null;
        String pe = this.getPseudoElementOrClass();
        if (pe != null) {
            if (e != null) {
                style = c.getSharedContext().getStyle(e, true);
                style = style.deriveStyle(c.getCss().getPseudoElementStyle(e, pe));
            } else {
                BlockBox container = (BlockBox)this.getParent().getParent();
                e = container.getElement();
                style = c.getSharedContext().getStyle(e, true);
                style = style.deriveStyle(c.getCss().getPseudoElementStyle(e, pe));
                style = style.createAnonymousStyle(IdentValue.INLINE);
            }
        } else if (e != null) {
            style = c.getSharedContext().getStyle(e, true);
            if (this.isAnonymous()) {
                style = style.createAnonymousStyle(this.getStyle().getIdent(CSSName.DISPLAY));
            }
        } else {
            Box parent = this.getParent();
            if (parent != null && (e = parent.getElement()) != null) {
                style = c.getSharedContext().getStyle(e, true);
                style = style.createAnonymousStyle(IdentValue.INLINE);
            }
        }
        if (style != null) {
            this.setStyle(style);
        }
        this.restyleChildren(c);
    }

    protected void restyleChildren(LayoutContext c) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            Box b = this.getChild(i);
            b.restyle(c);
        }
    }

    public Box getRestyleTarget() {
        return this;
    }

    protected int getIndex() {
        return this._index;
    }

    protected void setIndex(int index) {
        this._index = index;
    }

    @Override
    public String getPseudoElementOrClass() {
        return this._pseudoElementOrClass;
    }

    public void setPseudoElementOrClass(String pseudoElementOrClass) {
        this._pseudoElementOrClass = pseudoElementOrClass;
    }

    public void setX(int x) {
        this._x = x;
    }

    public int getX() {
        return this._x;
    }

    public void setY(int y) {
        this._y = y;
    }

    public int getY() {
        return this._y;
    }

    public void setTy(int ty) {
        this._ty = ty;
    }

    public int getTy() {
        return this._ty;
    }

    public void setTx(int tx) {
        this._tx = tx;
    }

    public int getTx() {
        return this._tx;
    }

    public void setRightMBP(int rightMBP) {
        this._rightMBP = rightMBP;
    }

    public int getRightMBP() {
        return this._rightMBP;
    }

    public void setLeftMBP(int leftMBP) {
        this._leftMBP = leftMBP;
    }

    public int getLeftMBP() {
        return this._leftMBP;
    }

    public void setHeight(int height) {
        this._height = height;
    }

    public int getHeight() {
        return this._height;
    }

    public void setContentWidth(int contentWidth) {
        this._contentWidth = contentWidth < 0 ? 0 : contentWidth;
    }

    public int getContentWidth() {
        return this._contentWidth;
    }

    public PaintingInfo getPaintingInfo() {
        return this._paintingInfo;
    }

    private void setPaintingInfo(PaintingInfo paintingInfo) {
        this._paintingInfo = paintingInfo;
    }

    public boolean isAnonymous() {
        return this._anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this._anonymous = anonymous;
    }

    public BoxDimensions getBoxDimensions() {
        BoxDimensions result = new BoxDimensions();
        result.setLeftMBP(this.getLeftMBP());
        result.setRightMBP(this.getRightMBP());
        result.setContentWidth(this.getContentWidth());
        result.setHeight(this.getHeight());
        return result;
    }

    public void setBoxDimensions(BoxDimensions dimensions) {
        this.setLeftMBP(dimensions.getLeftMBP());
        this.setRightMBP(dimensions.getRightMBP());
        this.setContentWidth(dimensions.getContentWidth());
        this.setHeight(dimensions.getHeight());
    }

    public void collectText(RenderingContext c, StringBuffer buffer) throws IOException {
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            Box b = (Box)i.next();
            b.collectText(c, buffer);
        }
    }

    public void exportText(RenderingContext c, Writer writer) throws IOException {
        if (c.isPrint() && this.isRoot()) {
            c.setPage(0, (PageBox)c.getRootLayer().getPages().get(0));
            c.getPage().exportLeadingText(c, writer);
        }
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            Box b = (Box)i.next();
            b.exportText(c, writer);
        }
        if (c.isPrint() && this.isRoot()) {
            this.exportPageBoxText(c, writer);
        }
    }

    private void exportPageBoxText(RenderingContext c, Writer writer) throws IOException {
        c.getPage().exportTrailingText(c, writer);
        if (c.getPage() != c.getRootLayer().getLastPage()) {
            List pages = c.getRootLayer().getPages();
            do {
                PageBox next = (PageBox)pages.get(c.getPageNo() + 1);
                c.setPage(next.getPageNo(), next);
                next.exportLeadingText(c, writer);
                next.exportTrailingText(c, writer);
            } while (c.getPage() != c.getRootLayer().getLastPage());
        }
    }

    protected void exportPageBoxText(RenderingContext c, Writer writer, int yPos) throws IOException {
        c.getPage().exportTrailingText(c, writer);
        List pages = c.getRootLayer().getPages();
        PageBox next = (PageBox)pages.get(c.getPageNo() + 1);
        c.setPage(next.getPageNo(), next);
        while (next.getBottom() < yPos) {
            next.exportLeadingText(c, writer);
            next.exportTrailingText(c, writer);
            next = (PageBox)pages.get(c.getPageNo() + 1);
            c.setPage(next.getPageNo(), next);
        }
        next.exportLeadingText(c, writer);
    }

    public boolean isInDocumentFlow() {
        Box parent;
        Box flowRoot = this;
        while ((parent = flowRoot.getParent()) != null) {
            flowRoot = parent;
        }
        return flowRoot.isRoot();
    }

    public void analyzePageBreaks(LayoutContext c, ContentLimitContainer container) {
        container.updateTop(c, this.getAbsY());
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            Box b = (Box)i.next();
            b.analyzePageBreaks(c, container);
        }
        container.updateBottom(c, this.getAbsY() + this.getHeight());
    }

    public FSColor getEffBackgroundColor(RenderingContext c) {
        FSColor result = null;
        for (Box current = this; current != null; current = current.getContainingBlock()) {
            result = current.getStyle().getBackgroundColor();
            if (result == null) continue;
            return result;
        }
        PageBox page = c.getPage();
        result = page.getStyle().getBackgroundColor();
        if (result == null) {
            return new FSRGBColor(255, 255, 255);
        }
        return result;
    }

    protected boolean isMarginAreaRoot() {
        return false;
    }

    public boolean isContainedInMarginBox() {
        Box parent;
        Box current = this;
        while ((parent = current.getParent()) != null) {
            current = parent;
        }
        return current.isMarginAreaRoot();
    }

    public int getEffectiveWidth() {
        return this.getWidth();
    }

    protected boolean isInitialContainingBlock() {
        return false;
    }
}

