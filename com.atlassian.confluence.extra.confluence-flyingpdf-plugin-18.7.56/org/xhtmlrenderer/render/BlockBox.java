/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.newmatch.CascadedStyle;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.layout.BlockBoxing;
import org.xhtmlrenderer.layout.BlockFormattingContext;
import org.xhtmlrenderer.layout.BoxBuilder;
import org.xhtmlrenderer.layout.BreakAtLineContext;
import org.xhtmlrenderer.layout.CounterFunction;
import org.xhtmlrenderer.layout.FloatManager;
import org.xhtmlrenderer.layout.InlineBoxing;
import org.xhtmlrenderer.layout.InlinePaintable;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.PaintingInfo;
import org.xhtmlrenderer.layout.PersistentBFC;
import org.xhtmlrenderer.layout.Styleable;
import org.xhtmlrenderer.newtable.TableRowBox;
import org.xhtmlrenderer.render.AnonymousBlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.ContentLimit;
import org.xhtmlrenderer.render.ContentLimitContainer;
import org.xhtmlrenderer.render.FloatedBoxData;
import org.xhtmlrenderer.render.InlineBox;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.LineBox;
import org.xhtmlrenderer.render.ListItemPainter;
import org.xhtmlrenderer.render.MarkerData;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.render.StrutMetrics;

public class BlockBox
extends Box
implements InlinePaintable {
    public static final int POSITION_VERTICALLY = 1;
    public static final int POSITION_HORIZONTALLY = 2;
    public static final int POSITION_BOTH = 3;
    public static final int CONTENT_UNKNOWN = 0;
    public static final int CONTENT_INLINE = 1;
    public static final int CONTENT_BLOCK = 2;
    public static final int CONTENT_EMPTY = 4;
    protected static final int NO_BASELINE = Integer.MIN_VALUE;
    private MarkerData _markerData;
    private int _listCounter;
    private PersistentBFC _persistentBFC;
    private Box _staticEquivalent;
    private boolean _needPageClear;
    private ReplacedElement _replacedElement;
    private int _childrenContentType;
    private List _inlineContent;
    private boolean _topMarginCalculated;
    private boolean _bottomMarginCalculated;
    private MarginCollapseResult _pendingCollapseCalculation;
    private int _minWidth;
    private int _maxWidth;
    private boolean _minMaxCalculated;
    private boolean _dimensionsCalculated;
    private boolean _needShrinkToFitCalculatation;
    private CascadedStyle _firstLineStyle;
    private CascadedStyle _firstLetterStyle;
    private FloatedBoxData _floatedBoxData;
    private int _childrenHeight;
    private boolean _fromCaptionedTable;

    public BlockBox copyOf() {
        BlockBox result = new BlockBox();
        result.setStyle(this.getStyle());
        result.setElement(this.getElement());
        return result;
    }

    protected String getExtraBoxDescription() {
        return "";
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        String className = this.getClass().getName();
        result.append(className.substring(className.lastIndexOf(46) + 1));
        result.append(": ");
        if (this.getElement() != null && !this.isAnonymous()) {
            result.append("<");
            result.append(this.getElement().getNodeName());
            result.append("> ");
        }
        if (this.isAnonymous()) {
            result.append("(anonymous) ");
        }
        if (this.getPseudoElementOrClass() != null) {
            result.append(':');
            result.append(this.getPseudoElementOrClass());
            result.append(' ');
        }
        result.append('(');
        result.append(this.getStyle().getIdent(CSSName.DISPLAY).toString());
        result.append(") ");
        if (this.getStyle().isRunning()) {
            result.append("(running) ");
        }
        result.append('(');
        switch (this.getChildrenContentType()) {
            case 2: {
                result.append('B');
                break;
            }
            case 1: {
                result.append('I');
                break;
            }
            case 4: {
                result.append('E');
            }
        }
        result.append(") ");
        result.append(this.getExtraBoxDescription());
        this.appendPositioningInfo(result);
        result.append("(" + this.getAbsX() + "," + this.getAbsY() + ")->(" + this.getWidth() + " x " + this.getHeight() + ")");
        return result.toString();
    }

    protected void appendPositioningInfo(StringBuffer result) {
        if (this.getStyle().isRelative()) {
            result.append("(relative) ");
        }
        if (this.getStyle().isFixed()) {
            result.append("(fixed) ");
        }
        if (this.getStyle().isAbsolute()) {
            result.append("(absolute) ");
        }
        if (this.getStyle().isFloated()) {
            result.append("(floated) ");
        }
    }

    @Override
    public String dump(LayoutContext c, String indent, int which) {
        StringBuffer result = new StringBuffer(indent);
        this.ensureChildren(c);
        result.append(this);
        RectPropertySet margin = this.getMargin(c);
        result.append(" effMargin=[" + margin.top() + ", " + margin.right() + ", " + margin.bottom() + ", " + margin.right() + "] ");
        RectPropertySet styleMargin = this.getStyleMargin(c);
        result.append(" styleMargin=[" + styleMargin.top() + ", " + styleMargin.right() + ", " + styleMargin.bottom() + ", " + styleMargin.right() + "] ");
        if (this.getChildrenContentType() != 4) {
            result.append('\n');
        }
        switch (this.getChildrenContentType()) {
            case 2: {
                this.dumpBoxes(c, indent, this.getChildren(), which, result);
                break;
            }
            case 1: {
                if (which == 2) {
                    this.dumpBoxes(c, indent, this.getChildren(), which, result);
                    break;
                }
                Iterator i = this.getInlineContent().iterator();
                while (i.hasNext()) {
                    Styleable styleable = (Styleable)i.next();
                    if (styleable instanceof BlockBox) {
                        BlockBox b = (BlockBox)styleable;
                        result.append(b.dump(c, indent + "  ", which));
                        if (result.charAt(result.length() - 1) == '\n') {
                            result.deleteCharAt(result.length() - 1);
                        }
                    } else {
                        result.append(indent + "  ");
                        result.append(styleable.toString());
                    }
                    if (!i.hasNext()) continue;
                    result.append('\n');
                }
                break;
            }
        }
        return result.toString();
    }

    public void paintListMarker(RenderingContext c) {
        if (!this.getStyle().isVisible()) {
            return;
        }
        if (this.getStyle().isListItem()) {
            ListItemPainter.paint(c, this);
        }
    }

    @Override
    public Rectangle getPaintingClipEdge(CssContext cssCtx) {
        Rectangle result = super.getPaintingClipEdge(cssCtx);
        if (this.getStyle().isListItem()) {
            int delta = result.x;
            result.x = 0;
            result.width += delta;
        }
        return result;
    }

    @Override
    public void paintInline(RenderingContext c) {
        if (!this.getStyle().isVisible()) {
            return;
        }
        this.getContainingLayer().paintAsLayer(c, this);
    }

    public boolean isInline() {
        Box parent = this.getParent();
        return parent instanceof LineBox || parent instanceof InlineLayoutBox;
    }

    public LineBox getLineBox() {
        if (!this.isInline()) {
            return null;
        }
        Box b = this.getParent();
        while (!(b instanceof LineBox)) {
            b = b.getParent();
        }
        return (LineBox)b;
    }

    public void paintDebugOutline(RenderingContext c) {
        c.getOutputDevice().drawDebugOutline(c, this, FSRGBColor.RED);
    }

    public MarkerData getMarkerData() {
        return this._markerData;
    }

    public void setMarkerData(MarkerData markerData) {
        this._markerData = markerData;
    }

    public void createMarkerData(LayoutContext c) {
        if (this.getMarkerData() != null) {
            return;
        }
        StrutMetrics strutMetrics = InlineBoxing.createDefaultStrutMetrics(c, this);
        boolean imageMarker = false;
        MarkerData result = new MarkerData();
        result.setStructMetrics(strutMetrics);
        CalculatedStyle style = this.getStyle();
        IdentValue listStyle = style.getIdent(CSSName.LIST_STYLE_TYPE);
        String image = style.getStringProperty(CSSName.LIST_STYLE_IMAGE);
        if (!image.equals("none")) {
            result.setImageMarker(this.makeImageMarker(c, strutMetrics, image));
            boolean bl = imageMarker = result.getImageMarker() != null;
        }
        if (listStyle != IdentValue.NONE && !imageMarker) {
            if (listStyle == IdentValue.CIRCLE || listStyle == IdentValue.SQUARE || listStyle == IdentValue.DISC) {
                result.setGlyphMarker(this.makeGlyphMarker(strutMetrics));
            } else {
                result.setTextMarker(this.makeTextMarker(c, listStyle));
            }
        }
        this.setMarkerData(result);
    }

    private MarkerData.GlyphMarker makeGlyphMarker(StrutMetrics strutMetrics) {
        int diameter = (int)((strutMetrics.getAscent() + strutMetrics.getDescent()) / 3.0f);
        MarkerData.GlyphMarker result = new MarkerData.GlyphMarker();
        result.setDiameter(diameter);
        result.setLayoutWidth(diameter * 3);
        return result;
    }

    private MarkerData.ImageMarker makeImageMarker(LayoutContext c, StrutMetrics structMetrics, String image) {
        FSImage img = null;
        if (!image.equals("none") && (img = c.getUac().getImageResource(image).getImage()) != null) {
            StrutMetrics strutMetrics = structMetrics;
            if ((float)img.getHeight() > strutMetrics.getAscent()) {
                img.scale(-1, (int)strutMetrics.getAscent());
            }
            MarkerData.ImageMarker result = new MarkerData.ImageMarker();
            result.setImage(img);
            result.setLayoutWidth(img.getWidth() * 2);
            return result;
        }
        return null;
    }

    private MarkerData.TextMarker makeTextMarker(LayoutContext c, IdentValue listStyle) {
        int listCounter = this.getListCounter();
        String text = CounterFunction.createCounterText(listStyle, listCounter);
        text = text + ".  ";
        int w = c.getTextRenderer().getWidth(c.getFontContext(), this.getStyle().getFSFont(c), text);
        MarkerData.TextMarker result = new MarkerData.TextMarker();
        result.setText(text);
        result.setLayoutWidth(w);
        return result;
    }

    public int getListCounter() {
        return this._listCounter;
    }

    public void setListCounter(int listCounter) {
        this._listCounter = listCounter;
    }

    public PersistentBFC getPersistentBFC() {
        return this._persistentBFC;
    }

    public void setPersistentBFC(PersistentBFC persistentBFC) {
        this._persistentBFC = persistentBFC;
    }

    public Box getStaticEquivalent() {
        return this._staticEquivalent;
    }

    public void setStaticEquivalent(Box staticEquivalent) {
        this._staticEquivalent = staticEquivalent;
    }

    public boolean isReplaced() {
        return this._replacedElement != null;
    }

    @Override
    public void calcCanvasLocation() {
        LineBox lineBox;
        FloatManager manager;
        if (this.isFloated() && (manager = this._floatedBoxData.getManager()) != null) {
            Point offset = manager.getOffset(this);
            this.setAbsX(manager.getMaster().getAbsX() + this.getX() - offset.x);
            this.setAbsY(manager.getMaster().getAbsY() + this.getY() - offset.y);
        }
        if ((lineBox = this.getLineBox()) == null) {
            Box cb;
            Box parent = this.getParent();
            if (parent != null) {
                this.setAbsX(parent.getAbsX() + parent.getTx() + this.getX());
                this.setAbsY(parent.getAbsY() + parent.getTy() + this.getY());
            } else if (this.isStyled() && this.getStyle().isAbsFixedOrInlineBlockEquiv() && (cb = this.getContainingBlock()) != null) {
                this.setAbsX(cb.getAbsX() + this.getX());
                this.setAbsY(cb.getAbsY() + this.getY());
            }
        } else {
            this.setAbsX(lineBox.getAbsX() + this.getX());
            this.setAbsY(lineBox.getAbsY() + this.getY());
        }
        if (this.isReplaced()) {
            Point location = this.getReplacedElement().getLocation();
            if (location.x != this.getAbsX() || location.y != this.getAbsY()) {
                this.getReplacedElement().setLocation(this.getAbsX(), this.getAbsY());
            }
        }
    }

    public void calcInitialFloatedCanvasLocation(LayoutContext c) {
        Point offset = c.getBlockFormattingContext().getOffset();
        FloatManager manager = c.getBlockFormattingContext().getFloatManager();
        this.setAbsX(manager.getMaster().getAbsX() + this.getX() - offset.x);
        this.setAbsY(manager.getMaster().getAbsY() + this.getY() - offset.y);
    }

    @Override
    public void calcChildLocations() {
        super.calcChildLocations();
        if (this._persistentBFC != null) {
            this._persistentBFC.getFloatManager().calcFloatLocations();
        }
    }

    public boolean isNeedPageClear() {
        return this._needPageClear;
    }

    public void setNeedPageClear(boolean needPageClear) {
        this._needPageClear = needPageClear;
    }

    private void alignToStaticEquivalent() {
        if (this._staticEquivalent.getAbsY() != this.getAbsY()) {
            this.setY(this._staticEquivalent.getAbsY() - this.getAbsY());
            this.setAbsY(this._staticEquivalent.getAbsY());
        }
    }

    public void positionAbsolute(CssContext cssCtx, int direction) {
        CalculatedStyle style = this.getStyle();
        Rectangle boundingBox = null;
        int cbContentHeight = this.getContainingBlock().getContentAreaEdge((int)0, (int)0, (CssContext)cssCtx).height;
        boundingBox = this.getContainingBlock() instanceof BlockBox ? this.getContainingBlock().getPaddingEdge(0, 0, cssCtx) : this.getContainingBlock().getContentAreaEdge(0, 0, cssCtx);
        if ((direction & 2) != 0) {
            this.setX(0);
            if (!style.isIdent(CSSName.LEFT, IdentValue.AUTO)) {
                this.setX((int)style.getFloatPropertyProportionalWidth(CSSName.LEFT, this.getContainingBlock().getContentWidth(), cssCtx));
            } else if (!style.isIdent(CSSName.RIGHT, IdentValue.AUTO)) {
                this.setX(boundingBox.width - (int)style.getFloatPropertyProportionalWidth(CSSName.RIGHT, this.getContainingBlock().getContentWidth(), cssCtx) - this.getWidth());
            }
            this.setX(this.getX() + boundingBox.x);
        }
        if ((direction & 1) != 0) {
            this.setY(0);
            if (!style.isIdent(CSSName.TOP, IdentValue.AUTO)) {
                this.setY((int)style.getFloatPropertyProportionalHeight(CSSName.TOP, cbContentHeight, cssCtx));
            } else if (!style.isIdent(CSSName.BOTTOM, IdentValue.AUTO)) {
                this.setY(boundingBox.height - (int)style.getFloatPropertyProportionalWidth(CSSName.BOTTOM, cbContentHeight, cssCtx) - this.getHeight());
            }
            int pinnedHeight = this.calcPinnedHeight(cssCtx);
            if (pinnedHeight != -1 && this.getCSSHeight(cssCtx) == -1) {
                this.setHeight(pinnedHeight);
                this.applyCSSMinMaxHeight(cssCtx);
            }
            this.setY(this.getY() + boundingBox.y);
        }
        this.calcCanvasLocation();
        if ((direction & 1) != 0 && this.getStyle().isTopAuto() && this.getStyle().isBottomAuto()) {
            this.alignToStaticEquivalent();
        }
        this.calcChildLocations();
    }

    public void positionAbsoluteOnPage(LayoutContext c) {
        if (c.isPrint() && (this.getStyle().isForcePageBreakBefore() || this.isNeedPageClear())) {
            this.forcePageBreakBefore(c, this.getStyle().getIdent(CSSName.PAGE_BREAK_BEFORE), false);
            this.calcCanvasLocation();
            this.calcChildLocations();
            this.setNeedPageClear(false);
        }
    }

    public ReplacedElement getReplacedElement() {
        return this._replacedElement;
    }

    public void setReplacedElement(ReplacedElement replacedElement) {
        this._replacedElement = replacedElement;
    }

    @Override
    public void reset(LayoutContext c) {
        super.reset(c);
        this.setTopMarginCalculated(false);
        this.setBottomMarginCalculated(false);
        this.setDimensionsCalculated(false);
        this.setMinMaxCalculated(false);
        this.setChildrenHeight(0);
        if (this.isReplaced()) {
            this.getReplacedElement().detach(c);
            this.setReplacedElement(null);
        }
        if (this.getChildrenContentType() == 1) {
            this.removeAllChildren();
        }
        if (this.isFloated()) {
            this._floatedBoxData.getManager().removeFloat(this);
            this._floatedBoxData.getDrawingLayer().removeFloat(this);
        }
        if (this.getStyle().isRunning()) {
            c.getRootLayer().removeRunningBlock(this);
        }
    }

    private int calcPinnedContentWidth(CssContext c) {
        if (!this.getStyle().isIdent(CSSName.LEFT, IdentValue.AUTO) && !this.getStyle().isIdent(CSSName.RIGHT, IdentValue.AUTO)) {
            int right;
            Rectangle paddingEdge = this.getContainingBlock().getPaddingEdge(0, 0, c);
            int left = (int)this.getStyle().getFloatPropertyProportionalTo(CSSName.LEFT, paddingEdge.width, c);
            int result = paddingEdge.width - left - (right = (int)this.getStyle().getFloatPropertyProportionalTo(CSSName.RIGHT, paddingEdge.width, c)) - this.getLeftMBP() - this.getRightMBP();
            return result < 0 ? 0 : result;
        }
        return -1;
    }

    private int calcPinnedHeight(CssContext c) {
        if (!this.getStyle().isIdent(CSSName.TOP, IdentValue.AUTO) && !this.getStyle().isIdent(CSSName.BOTTOM, IdentValue.AUTO)) {
            int bottom;
            Rectangle paddingEdge = this.getContainingBlock().getPaddingEdge(0, 0, c);
            int top = (int)this.getStyle().getFloatPropertyProportionalTo(CSSName.TOP, paddingEdge.height, c);
            int result = paddingEdge.height - top - (bottom = (int)this.getStyle().getFloatPropertyProportionalTo(CSSName.BOTTOM, paddingEdge.height, c));
            return result < 0 ? 0 : result;
        }
        return -1;
    }

    protected void resolveAutoMargins(LayoutContext c, int cssWidth, RectPropertySet padding, BorderPropertySet border) {
        int withoutMargins = (int)border.left() + (int)padding.left() + cssWidth + (int)padding.right() + (int)border.right();
        if (withoutMargins < this.getContainingBlockWidth()) {
            int available = this.getContainingBlockWidth() - withoutMargins;
            boolean autoLeft = this.getStyle().isAutoLeftMargin();
            boolean autoRight = this.getStyle().isAutoRightMargin();
            if (autoLeft && autoRight) {
                this.setMarginLeft(c, available / 2);
                this.setMarginRight(c, available / 2);
            } else if (autoLeft) {
                this.setMarginLeft(c, available);
            } else if (autoRight) {
                this.setMarginRight(c, available);
            }
        }
    }

    private int calcEffPageRelativeWidth(LayoutContext c) {
        int totalLeftMBP = 0;
        int totalRightMBP = 0;
        boolean usePageRelativeWidth = true;
        Box current = this;
        while (true) {
            CalculatedStyle style;
            if ((style = current.getStyle()).isAutoWidth() && !style.isCanBeShrunkToFit()) {
                totalLeftMBP += current.getLeftMBP();
                totalRightMBP += current.getRightMBP();
            } else {
                usePageRelativeWidth = false;
                break;
            }
            if (current.getContainingBlock().isInitialContainingBlock()) break;
            current = current.getContainingBlock();
        }
        if (usePageRelativeWidth) {
            PageBox currentPage = c.getRootLayer().getFirstPage(c, this);
            return currentPage.getContentWidth(c) - totalLeftMBP - totalRightMBP;
        }
        return this.getContainingBlockWidth() - this.getLeftMBP() - this.getRightMBP();
    }

    public void calcDimensions(LayoutContext c) {
        this.calcDimensions(c, this.getCSSWidth(c));
    }

    protected void calcDimensions(LayoutContext c, int cssWidth) {
        if (!this.isDimensionsCalculated()) {
            CalculatedStyle style = this.getStyle();
            RectPropertySet padding = this.getPadding(c);
            BorderPropertySet border = this.getBorder(c);
            if (cssWidth != -1 && !this.isAnonymous() && (this.getStyle().isIdent(CSSName.MARGIN_LEFT, IdentValue.AUTO) || this.getStyle().isIdent(CSSName.MARGIN_RIGHT, IdentValue.AUTO)) && this.getStyle().isNeedAutoMarginResolution()) {
                this.resolveAutoMargins(c, cssWidth, padding, border);
            }
            this.recalcMargin(c);
            RectPropertySet margin = this.getMargin(c);
            this.setLeftMBP((int)margin.left() + (int)border.left() + (int)padding.left());
            this.setRightMBP((int)padding.right() + (int)border.right() + (int)margin.right());
            if (c.isPrint() && this.getStyle().isDynamicAutoWidth()) {
                this.setContentWidth(this.calcEffPageRelativeWidth(c));
            } else {
                this.setContentWidth(this.getContainingBlockWidth() - this.getLeftMBP() - this.getRightMBP());
            }
            this.setHeight(0);
            if (!this.isAnonymous() || this.isFromCaptionedTable() && this.isFloated()) {
                ReplacedElement re;
                int pinnedContentWidth = -1;
                boolean borderBox = style.isBorderBox();
                if (cssWidth != -1) {
                    if (borderBox) {
                        this.setContentWidth(cssWidth - (int)border.width() - (int)padding.width());
                    } else {
                        this.setContentWidth(cssWidth);
                    }
                } else if ((this.getStyle().isAbsolute() || this.getStyle().isFixed()) && (pinnedContentWidth = this.calcPinnedContentWidth(c)) != -1) {
                    this.setContentWidth(pinnedContentWidth);
                }
                int cssHeight = this.getCSSHeight(c);
                if (cssHeight != -1) {
                    if (borderBox) {
                        this.setHeight(cssHeight - (int)padding.height() - (int)border.height());
                    } else {
                        this.setHeight(cssHeight);
                    }
                }
                if ((re = this.getReplacedElement()) == null && (re = c.getReplacedElementFactory().createReplacedElement(c, this, c.getUac(), cssWidth, cssHeight)) != null) {
                    re = this.fitReplacedElement(c, re);
                }
                if (re != null) {
                    this.setContentWidth(re.getIntrinsicWidth());
                    this.setHeight(re.getIntrinsicHeight());
                    this.setReplacedElement(re);
                } else if (cssWidth == -1 && pinnedContentWidth == -1 && style.isCanBeShrunkToFit()) {
                    this.setNeedShrinkToFitCalculatation(true);
                }
                if (!this.isReplaced()) {
                    this.applyCSSMinMaxWidth(c);
                }
            }
            this.setDimensionsCalculated(true);
        }
    }

    private void calcClearance(LayoutContext c) {
        if (this.getStyle().isCleared() && !this.getStyle().isFloated()) {
            c.translate(0, -this.getY());
            c.getBlockFormattingContext().clear(c, this);
            c.translate(0, this.getY());
            this.calcCanvasLocation();
        }
    }

    private void calcExtraPageClearance(LayoutContext c) {
        PageBox first;
        if (c.isPageBreaksAllowed() && c.getExtraSpaceTop() > 0 && (this.getStyle().isSpecifiedAsBlock() || this.getStyle().isListItem()) && (first = c.getRootLayer().getFirstPage(c, this)) != null && first.getTop() + c.getExtraSpaceTop() > this.getAbsY()) {
            int diff = first.getTop() + c.getExtraSpaceTop() - this.getAbsY();
            this.setY(this.getY() + diff);
            c.translate(0, diff);
            this.calcCanvasLocation();
        }
    }

    private void addBoxID(LayoutContext c) {
        if (!this.isAnonymous()) {
            String id;
            String name = c.getNamespaceHandler().getAnchorName(this.getElement());
            if (name != null) {
                c.addBoxId(name, this);
            }
            if ((id = c.getNamespaceHandler().getID(this.getElement())) != null) {
                c.addBoxId(id, this);
            }
        }
    }

    public void layout(LayoutContext c) {
        this.layout(c, 0);
    }

    public void layout(LayoutContext c, int contentStart) {
        int delta;
        PageBox firstPage;
        CalculatedStyle style = this.getStyle();
        boolean pushedLayer = false;
        if (this.isRoot() || style.requiresLayer()) {
            pushedLayer = true;
            if (this.getLayer() == null) {
                c.pushLayer(this);
            } else {
                c.pushLayer(this.getLayer());
            }
        }
        if (style.isFixedBackground()) {
            c.getRootLayer().setFixedBackground(true);
        }
        this.calcClearance(c);
        if (this.isRoot() || this.getStyle().establishesBFC() || this.isMarginAreaRoot()) {
            BlockFormattingContext bfc = new BlockFormattingContext(this, c);
            c.pushBFC(bfc);
        }
        this.addBoxID(c);
        if (c.isPrint() && this.getStyle().isIdent(CSSName.FS_PAGE_SEQUENCE, IdentValue.START)) {
            c.getRootLayer().addPageSequence(this);
        }
        this.calcDimensions(c);
        this.calcShrinkToFitWidthIfNeeded(c);
        this.collapseMargins(c);
        this.calcExtraPageClearance(c);
        if (c.isPrint() && (firstPage = c.getRootLayer().getFirstPage(c, this)) != null && firstPage.getTop() == this.getAbsY() - this.getPageClearance()) {
            this.resetTopMargin(c);
        }
        BorderPropertySet border = this.getBorder(c);
        RectPropertySet margin = this.getMargin(c);
        RectPropertySet padding = this.getPadding(c);
        int originalHeight = this.getHeight();
        if (!this.isReplaced()) {
            this.setHeight(0);
        }
        boolean didSetMarkerData = false;
        if (this.getStyle().isListItem()) {
            this.createMarkerData(c);
            c.setCurrentMarkerData(this.getMarkerData());
            didSetMarkerData = true;
        }
        int tx = (int)margin.left() + (int)border.left() + (int)padding.left();
        int ty = (int)margin.top() + (int)border.top() + (int)padding.top();
        this.setTx(tx);
        this.setTy(ty);
        c.translate(this.getTx(), this.getTy());
        if (!this.isReplaced()) {
            this.layoutChildren(c, contentStart);
        } else {
            this.setState(3);
        }
        c.translate(-this.getTx(), -this.getTy());
        this.setChildrenHeight(this.getHeight());
        if (!this.isReplaced()) {
            if (!this.isAutoHeight() && ((delta = originalHeight - this.getHeight()) > 0 || this.isAllowHeightToShrink())) {
                this.setHeight(originalHeight);
            }
            this.applyCSSMinMaxHeight(c);
        }
        if ((this.isRoot() || this.getStyle().establishesBFC()) && this.getStyle().isAutoHeight() && (delta = c.getBlockFormattingContext().getFloatManager().getClearDelta(c, this.getTy() + this.getHeight())) > 0) {
            this.setHeight(this.getHeight() + delta);
            this.setChildrenHeight(this.getChildrenHeight() + delta);
        }
        if (didSetMarkerData) {
            c.setCurrentMarkerData(null);
        }
        this.calcLayoutHeight(c, border, margin, padding);
        if (this.isRoot() || this.getStyle().establishesBFC()) {
            c.popBFC();
        }
        if (pushedLayer) {
            c.popLayer();
        }
    }

    protected boolean isAllowHeightToShrink() {
        return true;
    }

    protected int getPageClearance() {
        return 0;
    }

    protected void calcLayoutHeight(LayoutContext c, BorderPropertySet border, RectPropertySet margin, RectPropertySet padding) {
        this.setHeight(this.getHeight() + ((int)margin.top() + (int)border.top() + (int)padding.top() + (int)padding.bottom() + (int)border.bottom() + (int)margin.bottom()));
        this.setChildrenHeight(this.getChildrenHeight() + ((int)margin.top() + (int)border.top() + (int)padding.top() + (int)padding.bottom() + (int)border.bottom() + (int)margin.bottom()));
    }

    private void calcShrinkToFitWidthIfNeeded(LayoutContext c) {
        if (this.isNeedShrinkToFitCalculatation()) {
            this.setContentWidth(this.calcShrinkToFitWidth(c) - this.getLeftMBP() - this.getRightMBP());
            this.applyCSSMinMaxWidth(c);
            this.setNeedShrinkToFitCalculatation(false);
        }
    }

    private void applyCSSMinMaxWidth(CssContext c) {
        int cssMinWidth;
        if (!this.getStyle().isMaxWidthNone()) {
            int cssMaxWidth = this.getCSSMaxWidth(c);
            if (this.getContentWidth() > cssMaxWidth) {
                this.setContentWidth(cssMaxWidth);
            }
        }
        if ((cssMinWidth = this.getCSSMinWidth(c)) > 0 && this.getContentWidth() < cssMinWidth) {
            this.setContentWidth(cssMinWidth);
        }
    }

    private void applyCSSMinMaxHeight(CssContext c) {
        int cssMinHeight;
        if (!this.getStyle().isMaxHeightNone()) {
            int cssMaxHeight = this.getCSSMaxHeight(c);
            if (this.getHeight() > cssMaxHeight) {
                this.setHeight(cssMaxHeight);
            }
        }
        if ((cssMinHeight = this.getCSSMinHeight(c)) > 0 && this.getHeight() < cssMinHeight) {
            this.setHeight(cssMinHeight);
        }
    }

    public void ensureChildren(LayoutContext c) {
        if (this.getChildrenContentType() == 0) {
            BoxBuilder.createChildren(c, this);
        }
    }

    protected void layoutChildren(LayoutContext c, int contentStart) {
        this.setState(2);
        this.ensureChildren(c);
        if (this.getFirstLetterStyle() != null) {
            c.getFirstLettersTracker().addStyle(this.getFirstLetterStyle());
        }
        if (this.getFirstLineStyle() != null) {
            c.getFirstLinesTracker().addStyle(this.getFirstLineStyle());
        }
        switch (this.getChildrenContentType()) {
            case 1: {
                this.layoutInlineChildren(c, contentStart, this.calcInitialBreakAtLine(c), true);
                break;
            }
            case 2: {
                BlockBoxing.layoutContent(c, this, contentStart);
            }
        }
        if (this.getFirstLetterStyle() != null) {
            c.getFirstLettersTracker().removeLast();
        }
        if (this.getFirstLineStyle() != null) {
            c.getFirstLinesTracker().removeLast();
        }
        this.setState(3);
    }

    protected void layoutInlineChildren(LayoutContext c, int contentStart, int breakAtLine, boolean tryAgain) {
        InlineBoxing.layoutContent(c, this, contentStart, breakAtLine);
        if (c.isPrint() && c.isPageBreaksAllowed() && this.getChildCount() > 1) {
            this.satisfyWidowsAndOrphans(c, contentStart, tryAgain);
        }
        if (tryAgain && this.getStyle().isTextJustify()) {
            this.justifyText();
        }
    }

    private void justifyText() {
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            LineBox line = (LineBox)i.next();
            line.justify();
        }
    }

    private void satisfyWidowsAndOrphans(LayoutContext c, int contentStart, boolean tryAgain) {
        LineBox lB;
        int i;
        LineBox firstLineBox = (LineBox)this.getChild(0);
        PageBox firstPage = c.getRootLayer().getFirstPage(c, firstLineBox);
        if (firstPage == null) {
            return;
        }
        int noContentLBs = 0;
        int cCount = this.getChildCount();
        for (i = 0; i < cCount && (lB = (LineBox)this.getChild(i)).getAbsY() < firstPage.getBottom(); ++i) {
            if (lB.isContainsContent()) continue;
            ++noContentLBs;
        }
        if (i != cCount) {
            int orphans = (int)this.getStyle().asFloat(CSSName.ORPHANS);
            if (i - noContentLBs < orphans) {
                this.setNeedPageClear(true);
            } else {
                LineBox lB2;
                LineBox lastLineBox = (LineBox)this.getChild(cCount - 1);
                List pages = c.getRootLayer().getPages();
                PageBox lastPage = (PageBox)pages.get(firstPage.getPageNo() + 1);
                while (lastPage.getPageNo() != pages.size() - 1 && lastPage.getBottom() < lastLineBox.getAbsY()) {
                    lastPage = (PageBox)pages.get(lastPage.getPageNo() + 1);
                }
                noContentLBs = 0;
                for (i = cCount - 1; i >= 0 && ((LineBox)this.getChild(i)).getAbsY() >= lastPage.getTop() && (lB2 = (LineBox)this.getChild(i)).getAbsY() >= lastPage.getTop(); --i) {
                    if (lB2.isContainsContent()) continue;
                    ++noContentLBs;
                }
                int widows = (int)this.getStyle().asFloat(CSSName.WIDOWS);
                if (cCount - 1 - i - noContentLBs < widows) {
                    if (cCount - 1 - widows < orphans) {
                        this.setNeedPageClear(true);
                    } else if (tryAgain) {
                        int breakAtLine = cCount - 1 - widows;
                        this.resetChildren(c);
                        this.removeAllChildren();
                        this.layoutInlineChildren(c, contentStart, breakAtLine, false);
                    }
                }
            }
        }
    }

    public int getChildrenContentType() {
        return this._childrenContentType;
    }

    public void setChildrenContentType(int contentType) {
        this._childrenContentType = contentType;
    }

    public List getInlineContent() {
        return this._inlineContent;
    }

    public void setInlineContent(List inlineContent) {
        this._inlineContent = inlineContent;
        if (inlineContent != null) {
            for (Styleable child : inlineContent) {
                if (!(child instanceof Box)) continue;
                ((Box)child).setContainingBlock(this);
            }
        }
    }

    protected boolean isSkipWhenCollapsingMargins() {
        return false;
    }

    protected boolean isMayCollapseMarginsWithChildren() {
        return !this.isRoot() && this.getStyle().isMayCollapseMarginsWithChildren();
    }

    private void collapseMargins(LayoutContext c) {
        if (!this.isTopMarginCalculated() || !this.isBottomMarginCalculated()) {
            this.recalcMargin(c);
            RectPropertySet margin = this.getMargin(c);
            if (!this.isTopMarginCalculated() && !this.isBottomMarginCalculated() && this.isVerticalMarginsAdjoin(c)) {
                MarginCollapseResult collapsedMargin = this._pendingCollapseCalculation != null ? this._pendingCollapseCalculation : new MarginCollapseResult();
                this.collapseEmptySubtreeMargins(c, collapsedMargin);
                this.setCollapsedBottomMargin(c, margin, collapsedMargin);
            } else {
                MarginCollapseResult collapsedMargin;
                if (!this.isTopMarginCalculated()) {
                    collapsedMargin = this._pendingCollapseCalculation != null ? this._pendingCollapseCalculation : new MarginCollapseResult();
                    this.collapseTopMargin(c, true, collapsedMargin);
                    if ((int)margin.top() != collapsedMargin.getMargin()) {
                        this.setMarginTop(c, collapsedMargin.getMargin());
                    }
                }
                if (!this.isBottomMarginCalculated()) {
                    collapsedMargin = new MarginCollapseResult();
                    this.collapseBottomMargin(c, true, collapsedMargin);
                    this.setCollapsedBottomMargin(c, margin, collapsedMargin);
                }
            }
        }
    }

    private void setCollapsedBottomMargin(LayoutContext c, RectPropertySet margin, MarginCollapseResult collapsedMargin) {
        BlockBox next = null;
        if (!this.isInline()) {
            next = this.getNextCollapsableSibling(collapsedMargin);
        }
        if (next != null && !(next instanceof AnonymousBlockBox) && collapsedMargin.hasMargin()) {
            next._pendingCollapseCalculation = collapsedMargin;
            this.setMarginBottom(c, 0);
        } else if ((int)margin.bottom() != collapsedMargin.getMargin()) {
            this.setMarginBottom(c, collapsedMargin.getMargin());
        }
    }

    private BlockBox getNextCollapsableSibling(MarginCollapseResult collapsedMargin) {
        BlockBox next;
        for (next = (BlockBox)this.getNextSibling(); next != null; next = (BlockBox)next.getNextSibling()) {
            if (next instanceof AnonymousBlockBox) {
                ((AnonymousBlockBox)next).provideSiblingMarginToFloats(collapsedMargin.getMargin());
            }
            if (!next.isSkipWhenCollapsingMargins()) break;
        }
        return next;
    }

    private void collapseTopMargin(LayoutContext c, boolean calculationRoot, MarginCollapseResult result) {
        if (!this.isTopMarginCalculated()) {
            if (!this.isSkipWhenCollapsingMargins()) {
                this.calcDimensions(c);
                if (c.isPrint() && this.getStyle().isDynamicAutoWidthApplicable()) {
                    this.setDimensionsCalculated(false);
                }
                RectPropertySet margin = this.getMargin(c);
                result.update((int)margin.top());
                if (!calculationRoot && (int)margin.top() != 0) {
                    this.setMarginTop(c, 0);
                }
                if (this.isMayCollapseMarginsWithChildren() && this.isNoTopPaddingOrBorder(c)) {
                    this.ensureChildren(c);
                    if (this.getChildrenContentType() == 2) {
                        Iterator i = this.getChildIterator();
                        while (i.hasNext()) {
                            BlockBox child = (BlockBox)i.next();
                            child.collapseTopMargin(c, false, result);
                            if (child.isSkipWhenCollapsingMargins()) continue;
                        }
                    }
                }
            }
            this.setTopMarginCalculated(true);
        }
    }

    private void collapseBottomMargin(LayoutContext c, boolean calculationRoot, MarginCollapseResult result) {
        if (!this.isBottomMarginCalculated()) {
            if (!this.isSkipWhenCollapsingMargins()) {
                this.calcDimensions(c);
                if (c.isPrint() && this.getStyle().isDynamicAutoWidthApplicable()) {
                    this.setDimensionsCalculated(false);
                }
                RectPropertySet margin = this.getMargin(c);
                result.update((int)margin.bottom());
                if (!calculationRoot && (int)margin.bottom() != 0) {
                    this.setMarginBottom(c, 0);
                }
                if (this.isMayCollapseMarginsWithChildren() && !this.getStyle().isTable() && this.isNoBottomPaddingOrBorder(c)) {
                    this.ensureChildren(c);
                    if (this.getChildrenContentType() == 2) {
                        for (int i = this.getChildCount() - 1; i >= 0; --i) {
                            BlockBox child = (BlockBox)this.getChild(i);
                            if (child.isSkipWhenCollapsingMargins()) continue;
                            child.collapseBottomMargin(c, false, result);
                            break;
                        }
                    }
                }
            }
            this.setBottomMarginCalculated(true);
        }
    }

    private boolean isNoTopPaddingOrBorder(LayoutContext c) {
        RectPropertySet padding = this.getPadding(c);
        BorderPropertySet border = this.getBorder(c);
        return (int)padding.top() == 0 && (int)border.top() == 0;
    }

    private boolean isNoBottomPaddingOrBorder(LayoutContext c) {
        RectPropertySet padding = this.getPadding(c);
        BorderPropertySet border = this.getBorder(c);
        return (int)padding.bottom() == 0 && (int)border.bottom() == 0;
    }

    private void collapseEmptySubtreeMargins(LayoutContext c, MarginCollapseResult result) {
        RectPropertySet margin = this.getMargin(c);
        result.update((int)margin.top());
        result.update((int)margin.bottom());
        this.setMarginTop(c, 0);
        this.setTopMarginCalculated(true);
        this.setMarginBottom(c, 0);
        this.setBottomMarginCalculated(true);
        this.ensureChildren(c);
        if (this.getChildrenContentType() == 2) {
            Iterator i = this.getChildIterator();
            while (i.hasNext()) {
                BlockBox child = (BlockBox)i.next();
                child.collapseEmptySubtreeMargins(c, result);
            }
        }
    }

    private boolean isVerticalMarginsAdjoin(LayoutContext c) {
        boolean bordersOrPadding;
        CalculatedStyle style = this.getStyle();
        BorderPropertySet borderWidth = style.getBorder(c);
        RectPropertySet padding = this.getPadding(c);
        boolean bl = bordersOrPadding = (int)borderWidth.top() != 0 || (int)borderWidth.bottom() != 0 || (int)padding.top() != 0 || (int)padding.bottom() != 0;
        if (bordersOrPadding) {
            return false;
        }
        this.ensureChildren(c);
        if (this.getChildrenContentType() == 1) {
            return false;
        }
        if (this.getChildrenContentType() == 2) {
            Iterator i = this.getChildIterator();
            while (i.hasNext()) {
                BlockBox child = (BlockBox)i.next();
                if (!child.isSkipWhenCollapsingMargins() && child.isVerticalMarginsAdjoin(c)) continue;
                return false;
            }
        }
        return style.asFloat(CSSName.MIN_HEIGHT) == 0.0f && (this.isAutoHeight() || style.asFloat(CSSName.HEIGHT) == 0.0f);
    }

    public boolean isTopMarginCalculated() {
        return this._topMarginCalculated;
    }

    public void setTopMarginCalculated(boolean topMarginCalculated) {
        this._topMarginCalculated = topMarginCalculated;
    }

    public boolean isBottomMarginCalculated() {
        return this._bottomMarginCalculated;
    }

    public void setBottomMarginCalculated(boolean bottomMarginCalculated) {
        this._bottomMarginCalculated = bottomMarginCalculated;
    }

    protected int getCSSWidth(CssContext c) {
        return this.getCSSWidth(c, false);
    }

    protected int getCSSWidth(CssContext c, boolean shrinkingToFit) {
        if (!this.isAnonymous() && !this.getStyle().isAutoWidth()) {
            if (shrinkingToFit && !this.getStyle().isAbsoluteWidth()) {
                return -1;
            }
            int result = (int)this.getStyle().getFloatPropertyProportionalWidth(CSSName.WIDTH, this.getContainingBlock().getContentWidth(), c);
            return result >= 0 ? result : -1;
        }
        return -1;
    }

    protected int getCSSFitToWidth(CssContext c) {
        if (!this.isAnonymous() && !this.getStyle().isIdent(CSSName.FS_FIT_IMAGES_TO_WIDTH, IdentValue.AUTO)) {
            int result = (int)this.getStyle().getFloatPropertyProportionalWidth(CSSName.FS_FIT_IMAGES_TO_WIDTH, this.getContainingBlock().getContentWidth(), c);
            return result >= 0 ? result : -1;
        }
        return -1;
    }

    protected int getCSSHeight(CssContext c) {
        if (!this.isAnonymous() && !this.isAutoHeight()) {
            if (this.getStyle().hasAbsoluteUnit(CSSName.HEIGHT)) {
                return (int)this.getStyle().getFloatPropertyProportionalHeight(CSSName.HEIGHT, 0.0f, c);
            }
            return (int)this.getStyle().getFloatPropertyProportionalHeight(CSSName.HEIGHT, ((BlockBox)this.getContainingBlock()).getCSSHeight(c), c);
        }
        return -1;
    }

    public boolean isAutoHeight() {
        if (this.getStyle().isAutoHeight()) {
            return true;
        }
        if (this.getStyle().hasAbsoluteUnit(CSSName.HEIGHT)) {
            return false;
        }
        Box cb = this.getContainingBlock();
        if (cb.isStyled() && cb instanceof BlockBox) {
            return ((BlockBox)cb).isAutoHeight();
        }
        return !(cb instanceof BlockBox) || !((BlockBox)cb).isInitialContainingBlock();
    }

    private int getCSSMinWidth(CssContext c) {
        return this.getStyle().getMinWidth(c, this.getContainingBlockWidth());
    }

    private int getCSSMaxWidth(CssContext c) {
        return this.getStyle().getMaxWidth(c, this.getContainingBlockWidth());
    }

    private int getCSSMinHeight(CssContext c) {
        return this.getStyle().getMinHeight(c, this.getContainingBlockCSSHeight(c));
    }

    private int getCSSMaxHeight(CssContext c) {
        return this.getStyle().getMaxHeight(c, this.getContainingBlockCSSHeight(c));
    }

    private int getContainingBlockCSSHeight(CssContext c) {
        if (!this.getContainingBlock().isStyled() || this.getContainingBlock().getStyle().isAutoHeight()) {
            return 0;
        }
        if (this.getContainingBlock().getStyle().hasAbsoluteUnit(CSSName.HEIGHT)) {
            return (int)this.getContainingBlock().getStyle().getFloatPropertyProportionalTo(CSSName.HEIGHT, 0.0f, c);
        }
        return 0;
    }

    private int calcShrinkToFitWidth(LayoutContext c) {
        this.calcMinMaxWidth(c);
        return Math.min(Math.max(this.getMinWidth(), this.getAvailableWidth(c)), this.getMaxWidth());
    }

    protected int getAvailableWidth(LayoutContext c) {
        if (!this.getStyle().isAbsolute()) {
            return this.getContainingBlockWidth();
        }
        int left = 0;
        int right = 0;
        if (!this.getStyle().isIdent(CSSName.LEFT, IdentValue.AUTO)) {
            left = (int)this.getStyle().getFloatPropertyProportionalTo(CSSName.LEFT, this.getContainingBlock().getContentWidth(), c);
        }
        if (!this.getStyle().isIdent(CSSName.RIGHT, IdentValue.AUTO)) {
            right = (int)this.getStyle().getFloatPropertyProportionalTo(CSSName.RIGHT, this.getContainingBlock().getContentWidth(), c);
        }
        return this.getContainingBlock().getPaddingWidth(c) - left - right;
    }

    protected boolean isFixedWidthAdvisoryOnly() {
        return false;
    }

    private void recalcMargin(LayoutContext c) {
        boolean resetBottom;
        if (this.isTopMarginCalculated() && this.isBottomMarginCalculated()) {
            return;
        }
        FSDerivedValue topMargin = this.getStyle().valueByName(CSSName.MARGIN_TOP);
        boolean resetTop = topMargin instanceof LengthValue && !topMargin.hasAbsoluteUnit();
        FSDerivedValue bottomMargin = this.getStyle().valueByName(CSSName.MARGIN_BOTTOM);
        boolean bl = resetBottom = bottomMargin instanceof LengthValue && !bottomMargin.hasAbsoluteUnit();
        if (!resetTop && !resetBottom) {
            return;
        }
        RectPropertySet styleMargin = this.getStyleMargin(c, false);
        RectPropertySet workingMargin = this.getMargin(c);
        if (!this.isTopMarginCalculated() && styleMargin.top() != workingMargin.top()) {
            this.setMarginTop(c, (int)styleMargin.top());
        }
        if (!this.isBottomMarginCalculated() && styleMargin.bottom() != workingMargin.bottom()) {
            this.setMarginBottom(c, (int)styleMargin.bottom());
        }
    }

    public void calcMinMaxWidth(LayoutContext c) {
        if (!this.isMinMaxCalculated()) {
            RectPropertySet margin = this.getMargin(c);
            BorderPropertySet border = this.getBorder(c);
            RectPropertySet padding = this.getPadding(c);
            int width = this.getCSSWidth(c, true);
            if (width == -1) {
                if (this.isReplaced()) {
                    width = this.getReplacedElement().getIntrinsicWidth();
                } else {
                    int height = this.getCSSHeight(c);
                    ReplacedElement re = c.getReplacedElementFactory().createReplacedElement(c, this, c.getUac(), width, height);
                    if (re != null) {
                        re = this.fitReplacedElement(c, re);
                        this.setReplacedElement(re);
                        width = this.getReplacedElement().getIntrinsicWidth();
                    }
                }
            }
            if (this.isReplaced() || width != -1 && !this.isFixedWidthAdvisoryOnly()) {
                this._minWidth = this._maxWidth = (int)margin.left() + (int)border.left() + (int)padding.left() + width + (int)margin.right() + (int)border.right() + (int)padding.right();
            } else {
                int cw = -1;
                if (width != -1) {
                    cw = this.getContentWidth();
                    this.setContentWidth(width);
                }
                this._minWidth = this._maxWidth = (int)margin.left() + (int)border.left() + (int)padding.left() + (int)margin.right() + (int)border.right() + (int)padding.right();
                int minimumMaxWidth = this._maxWidth;
                if (width != -1) {
                    minimumMaxWidth += width;
                }
                this.ensureChildren(c);
                if (this.getChildrenContentType() == 2 || this.getChildrenContentType() == 1) {
                    switch (this.getChildrenContentType()) {
                        case 2: {
                            this.calcMinMaxWidthBlockChildren(c);
                            break;
                        }
                        case 1: {
                            this.calcMinMaxWidthInlineChildren(c);
                        }
                    }
                }
                if (minimumMaxWidth > this._maxWidth) {
                    this._maxWidth = minimumMaxWidth;
                }
                if (cw != -1) {
                    this.setContentWidth(cw);
                }
            }
            if (!this.isReplaced()) {
                this.calcMinMaxCSSMinMaxWidth(c, margin, border, padding);
            }
            this.setMinMaxCalculated(true);
        }
    }

    private ReplacedElement fitReplacedElement(LayoutContext c, ReplacedElement re) {
        int maxImageWidth = this.getCSSFitToWidth(c);
        if (maxImageWidth > -1 && re.getIntrinsicWidth() > maxImageWidth) {
            double oldWidth = re.getIntrinsicWidth();
            double scale = (double)maxImageWidth / oldWidth;
            re = c.getReplacedElementFactory().createReplacedElement(c, this, c.getUac(), maxImageWidth, (int)Math.rint(scale * (double)re.getIntrinsicHeight()));
        }
        return re;
    }

    private void calcMinMaxCSSMinMaxWidth(LayoutContext c, RectPropertySet margin, BorderPropertySet border, RectPropertySet padding) {
        int cssMinWidth = this.getCSSMinWidth(c);
        if (cssMinWidth > 0 && this._minWidth < (cssMinWidth += (int)margin.left() + (int)border.left() + (int)padding.left() + (int)margin.right() + (int)border.right() + (int)padding.right())) {
            this._minWidth = cssMinWidth;
        }
        if (!this.getStyle().isMaxWidthNone()) {
            int cssMaxWidth = this.getCSSMaxWidth(c);
            if (this._maxWidth > (cssMaxWidth += (int)margin.left() + (int)border.left() + (int)padding.left() + (int)margin.right() + (int)border.right() + (int)padding.right())) {
                this._maxWidth = cssMaxWidth > this._minWidth ? cssMaxWidth : this._minWidth;
            }
        }
    }

    private void calcMinMaxWidthBlockChildren(LayoutContext c) {
        int childMinWidth = 0;
        int childMaxWidth = 0;
        Iterator i = this.getChildIterator();
        while (i.hasNext()) {
            BlockBox child = (BlockBox)i.next();
            child.calcMinMaxWidth(c);
            if (child.getMinWidth() > childMinWidth) {
                childMinWidth = child.getMinWidth();
            }
            if (child.getMaxWidth() <= childMaxWidth) continue;
            childMaxWidth = child.getMaxWidth();
        }
        this._minWidth += childMinWidth;
        this._maxWidth += childMaxWidth;
    }

    private void calcMinMaxWidthInlineChildren(LayoutContext c) {
        int textIndent = (int)this.getStyle().getFloatPropertyProportionalWidth(CSSName.TEXT_INDENT, this.getContentWidth(), c);
        if (this.getStyle().isListItem() && this.getStyle().isListMarkerInside()) {
            this.createMarkerData(c);
            textIndent += this.getMarkerData().getLayoutWidth();
        }
        int childMinWidth = 0;
        int childMaxWidth = 0;
        int lineWidth = 0;
        InlineBox trimmableIB = null;
        for (Styleable child : this._inlineContent) {
            if (child.getStyle().isAbsolute() || child.getStyle().isFixed() || child.getStyle().isRunning()) continue;
            if (child.getStyle().isFloated() || child.getStyle().isInlineBlock() || child.getStyle().isInlineTable()) {
                if (child.getStyle().isFloated() && child.getStyle().isCleared()) {
                    if (trimmableIB != null) {
                        lineWidth -= trimmableIB.getTrailingSpaceWidth(c);
                    }
                    if (lineWidth > childMaxWidth) {
                        childMaxWidth = lineWidth;
                    }
                    lineWidth = 0;
                }
                trimmableIB = null;
                BlockBox block = (BlockBox)child;
                block.calcMinMaxWidth(c);
                lineWidth += block.getMaxWidth();
                if (block.getMinWidth() <= childMinWidth) continue;
                childMinWidth = block.getMinWidth();
                continue;
            }
            InlineBox iB = (InlineBox)child;
            IdentValue whitespace = iB.getStyle().getWhitespace();
            iB.calcMinMaxWidth(c, this.getContentWidth(), lineWidth == 0);
            if (whitespace == IdentValue.NOWRAP) {
                lineWidth += textIndent + iB.getMaxWidth();
                if (iB.getMinWidth() > childMinWidth) {
                    childMinWidth = iB.getMinWidth();
                }
                trimmableIB = iB;
            } else if (whitespace == IdentValue.PRE) {
                if (trimmableIB != null) {
                    lineWidth -= trimmableIB.getTrailingSpaceWidth(c);
                }
                trimmableIB = null;
                if (lineWidth > childMaxWidth) {
                    childMaxWidth = lineWidth;
                }
                if ((lineWidth = textIndent + iB.getFirstLineWidth()) > childMinWidth) {
                    childMinWidth = lineWidth;
                }
                if ((lineWidth = iB.getMaxWidth()) > childMinWidth) {
                    childMinWidth = lineWidth;
                }
                if (childMinWidth > childMaxWidth) {
                    childMaxWidth = childMinWidth;
                }
                lineWidth = 0;
            } else if (whitespace == IdentValue.PRE_WRAP || whitespace == IdentValue.PRE_LINE) {
                lineWidth += textIndent + iB.getFirstLineWidth();
                if (trimmableIB != null) {
                    lineWidth -= trimmableIB.getTrailingSpaceWidth(c);
                }
                if (lineWidth > childMaxWidth) {
                    childMaxWidth = lineWidth;
                }
                if (iB.getMaxWidth() > childMaxWidth) {
                    childMaxWidth = iB.getMaxWidth();
                }
                if (iB.getMinWidth() > childMinWidth) {
                    childMinWidth = iB.getMinWidth();
                }
                trimmableIB = whitespace == IdentValue.PRE_LINE ? iB : null;
                lineWidth = 0;
            } else {
                lineWidth += textIndent + iB.getMaxWidth();
                if (iB.getMinWidth() > childMinWidth) {
                    childMinWidth = textIndent + iB.getMinWidth();
                }
                trimmableIB = iB;
            }
            if (textIndent <= 0) continue;
            textIndent = 0;
        }
        if (trimmableIB != null) {
            lineWidth -= trimmableIB.getTrailingSpaceWidth(c);
        }
        if (lineWidth > childMaxWidth) {
            childMaxWidth = lineWidth;
        }
        this._minWidth += childMinWidth;
        this._maxWidth += childMaxWidth;
    }

    public int getMaxWidth() {
        return this._maxWidth;
    }

    protected void setMaxWidth(int maxWidth) {
        this._maxWidth = maxWidth;
    }

    public int getMinWidth() {
        return this._minWidth;
    }

    protected void setMinWidth(int minWidth) {
        this._minWidth = minWidth;
    }

    public void styleText(LayoutContext c) {
        this.styleText(c, this.getStyle());
    }

    public void styleText(LayoutContext c, CalculatedStyle style) {
        if (this.getChildrenContentType() == 1) {
            LinkedList<CalculatedStyle> styles = new LinkedList<CalculatedStyle>();
            styles.add(style);
            for (Styleable child : this._inlineContent) {
                if (!(child instanceof InlineBox)) continue;
                InlineBox iB = (InlineBox)child;
                if (iB.isStartsHere()) {
                    CascadedStyle cs = null;
                    if (iB.getElement() != null) {
                        cs = iB.getPseudoElementOrClass() == null ? c.getCss().getCascadedStyle(iB.getElement(), false) : c.getCss().getPseudoElementStyle(iB.getElement(), iB.getPseudoElementOrClass());
                        styles.add(((CalculatedStyle)styles.getLast()).deriveStyle(cs));
                    } else {
                        styles.add(style.createAnonymousStyle(IdentValue.INLINE));
                    }
                }
                iB.setStyle((CalculatedStyle)styles.getLast());
                iB.applyTextTransform();
                if (!iB.isEndsHere()) continue;
                styles.removeLast();
            }
        }
    }

    @Override
    protected void calcChildPaintingInfo(final CssContext c, final PaintingInfo result, final boolean useCache) {
        if (this.getPersistentBFC() != null) {
            this.getPersistentBFC().getFloatManager().performFloatOperation(new FloatManager.FloatOperation(){

                @Override
                public void operate(Box floater) {
                    PaintingInfo info = floater.calcPaintingInfo(c, useCache);
                    BlockBox.this.moveIfGreater(result.getOuterMarginCorner(), info.getOuterMarginCorner());
                }
            });
        }
        super.calcChildPaintingInfo(c, result, useCache);
    }

    public CascadedStyle getFirstLetterStyle() {
        return this._firstLetterStyle;
    }

    public void setFirstLetterStyle(CascadedStyle firstLetterStyle) {
        this._firstLetterStyle = firstLetterStyle;
    }

    public CascadedStyle getFirstLineStyle() {
        return this._firstLineStyle;
    }

    public void setFirstLineStyle(CascadedStyle firstLineStyle) {
        this._firstLineStyle = firstLineStyle;
    }

    protected boolean isMinMaxCalculated() {
        return this._minMaxCalculated;
    }

    protected void setMinMaxCalculated(boolean minMaxCalculated) {
        this._minMaxCalculated = minMaxCalculated;
    }

    protected void setDimensionsCalculated(boolean dimensionsCalculated) {
        this._dimensionsCalculated = dimensionsCalculated;
    }

    private boolean isDimensionsCalculated() {
        return this._dimensionsCalculated;
    }

    protected void setNeedShrinkToFitCalculatation(boolean needShrinkToFitCalculatation) {
        this._needShrinkToFitCalculatation = needShrinkToFitCalculatation;
    }

    private boolean isNeedShrinkToFitCalculatation() {
        return this._needShrinkToFitCalculatation;
    }

    public void initStaticPos(LayoutContext c, BlockBox parent, int childOffset) {
        this.setX(0);
        this.setY(childOffset);
    }

    public int calcBaseline(LayoutContext c) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            Box b = this.getChild(i);
            if (b instanceof LineBox) {
                return b.getAbsY() + ((LineBox)b).getBaseline();
            }
            if (b instanceof TableRowBox) {
                return b.getAbsY() + ((TableRowBox)b).getBaseline();
            }
            int result = ((BlockBox)b).calcBaseline(c);
            if (result == Integer.MIN_VALUE) continue;
            return result;
        }
        return Integer.MIN_VALUE;
    }

    protected int calcInitialBreakAtLine(LayoutContext c) {
        BreakAtLineContext bContext = c.getBreakAtLineContext();
        if (bContext != null && bContext.getBlock() == this) {
            return bContext.getLine();
        }
        return 0;
    }

    public boolean isCurrentBreakAtLineContext(LayoutContext c) {
        BreakAtLineContext bContext = c.getBreakAtLineContext();
        return bContext != null && bContext.getBlock() == this;
    }

    public BreakAtLineContext calcBreakAtLineContext(LayoutContext c) {
        if (!c.isPrint() || !this.getStyle().isKeepWithInline()) {
            return null;
        }
        LineBox breakLine = this.findLastNthLineBox((int)this.getStyle().asFloat(CSSName.WIDOWS));
        if (breakLine != null) {
            PageBox linePage = c.getRootLayer().getLastPage(c, breakLine);
            PageBox ourPage = c.getRootLayer().getLastPage(c, this);
            if (linePage != null && ourPage != null && linePage.getPageNo() + 1 == ourPage.getPageNo()) {
                BlockBox breakBox = (BlockBox)breakLine.getParent();
                return new BreakAtLineContext(breakBox, breakBox.findOffset(breakLine));
            }
        }
        return null;
    }

    public int calcInlineBaseline(CssContext c) {
        if (this.isReplaced() && this.getReplacedElement().hasBaseline()) {
            Rectangle bounds = this.getContentAreaEdge(this.getAbsX(), this.getAbsY(), c);
            return bounds.y + this.getReplacedElement().getBaseline() - this.getAbsY();
        }
        LineBox lastLine = this.findLastLineBox();
        if (lastLine == null) {
            return this.getHeight();
        }
        return lastLine.getAbsY() + lastLine.getBaseline() - this.getAbsY();
    }

    public int findOffset(Box box) {
        int ccount = this.getChildCount();
        for (int i = 0; i < ccount; ++i) {
            if (this.getChild(i) != box) continue;
            return i;
        }
        return -1;
    }

    public LineBox findLastNthLineBox(int count) {
        LastLineBoxContext context = new LastLineBoxContext(count);
        this.findLastLineBox(context);
        return context.line;
    }

    private void findLastLineBox(LastLineBoxContext context) {
        block3: {
            int type;
            block4: {
                type = this.getChildrenContentType();
                int ccount = this.getChildCount();
                if (ccount <= 0) break block3;
                if (type != 1) break block4;
                for (int i = ccount - 1; i >= 0; --i) {
                    LineBox child = (LineBox)this.getChild(i);
                    if (child.getHeight() <= 0) continue;
                    context.line = child;
                    if (--context.current != 0) continue;
                    return;
                }
                break block3;
            }
            if (type != 2) break block3;
            for (int i = ccount - 1; i >= 0; --i) {
                ((BlockBox)this.getChild(i)).findLastLineBox(context);
                if (context.current != 0) {
                    continue;
                }
                break;
            }
        }
    }

    private LineBox findLastLineBox() {
        block2: {
            int type;
            block3: {
                type = this.getChildrenContentType();
                int ccount = this.getChildCount();
                if (ccount <= 0) break block2;
                if (type != 1) break block3;
                for (int i = ccount - 1; i >= 0; --i) {
                    LineBox result = (LineBox)this.getChild(i);
                    if (result.getHeight() <= 0) continue;
                    return result;
                }
                break block2;
            }
            if (type != 2) break block2;
            for (int i = ccount - 1; i >= 0; --i) {
                LineBox result = ((BlockBox)this.getChild(i)).findLastLineBox();
                if (result == null) continue;
                return result;
            }
        }
        return null;
    }

    private LineBox findFirstLineBox() {
        block2: {
            int ccount;
            int type;
            block3: {
                type = this.getChildrenContentType();
                ccount = this.getChildCount();
                if (ccount <= 0) break block2;
                if (type != 1) break block3;
                for (int i = 0; i < ccount; ++i) {
                    LineBox result = (LineBox)this.getChild(i);
                    if (result.getHeight() <= 0) continue;
                    return result;
                }
                break block2;
            }
            if (type != 2) break block2;
            for (int i = 0; i < ccount; ++i) {
                LineBox result = ((BlockBox)this.getChild(i)).findFirstLineBox();
                if (result == null) continue;
                return result;
            }
        }
        return null;
    }

    public boolean isNeedsKeepWithInline(LayoutContext c) {
        LineBox line;
        if (c.isPrint() && this.getStyle().isKeepWithInline() && (line = this.findFirstLineBox()) != null) {
            PageBox linePage = c.getRootLayer().getFirstPage(c, line);
            PageBox ourPage = c.getRootLayer().getFirstPage(c, this);
            return linePage != null && ourPage != null && linePage.getPageNo() == ourPage.getPageNo() + 1;
        }
        return false;
    }

    public boolean isFloated() {
        return this._floatedBoxData != null;
    }

    public FloatedBoxData getFloatedBoxData() {
        return this._floatedBoxData;
    }

    public void setFloatedBoxData(FloatedBoxData floatedBoxData) {
        this._floatedBoxData = floatedBoxData;
    }

    public int getChildrenHeight() {
        return this._childrenHeight;
    }

    protected void setChildrenHeight(int childrenHeight) {
        this._childrenHeight = childrenHeight;
    }

    public boolean isFromCaptionedTable() {
        return this._fromCaptionedTable;
    }

    public void setFromCaptionedTable(boolean fromTable) {
        this._fromCaptionedTable = fromTable;
    }

    @Override
    protected boolean isInlineBlock() {
        return this.isInline();
    }

    public boolean isInMainFlow() {
        Box flowRoot = this;
        while (flowRoot.getParent() != null) {
            flowRoot = flowRoot.getParent();
        }
        return flowRoot.isRoot();
    }

    @Override
    public Box getDocumentParent() {
        Box staticEquivalent = this.getStaticEquivalent();
        if (staticEquivalent != null) {
            return staticEquivalent;
        }
        return this.getParent();
    }

    public boolean isContainsInlineContent(LayoutContext c) {
        this.ensureChildren(c);
        switch (this.getChildrenContentType()) {
            case 1: {
                return true;
            }
            case 4: {
                return false;
            }
            case 2: {
                Iterator i = this.getChildIterator();
                while (i.hasNext()) {
                    BlockBox box = (BlockBox)i.next();
                    if (!box.isContainsInlineContent(c)) continue;
                    return true;
                }
                return false;
            }
        }
        throw new RuntimeException("internal error: no children");
    }

    public boolean checkPageContext(LayoutContext c) {
        if (!this.getStyle().isIdent(CSSName.PAGE, IdentValue.AUTO)) {
            String pageName = this.getStyle().getStringProperty(CSSName.PAGE);
            if (!pageName.equals(c.getPageName()) && this.isInDocumentFlow() && this.isContainsInlineContent(c)) {
                c.setPendingPageName(pageName);
                return true;
            }
        } else if (c.getPageName() != null && this.isInDocumentFlow()) {
            c.setPendingPageName(null);
            return true;
        }
        return false;
    }

    public boolean isNeedsClipOnPaint(RenderingContext c) {
        return !this.isReplaced() && this.getStyle().isIdent(CSSName.OVERFLOW, IdentValue.HIDDEN) && this.getStyle().isOverflowApplies();
    }

    protected void propagateExtraSpace(LayoutContext c, ContentLimitContainer parentContainer, ContentLimitContainer currentContainer, int extraTop, int extraBottom) {
        int start = currentContainer.getInitialPageNo();
        int end = currentContainer.getLastPageNo();
        for (int current = start; current <= end; ++current) {
            int bottom;
            int top;
            ContentLimit contentLimit = currentContainer.getContentLimit(current);
            if (current != start && (top = contentLimit.getTop()) != -1) {
                parentContainer.updateTop(c, top - extraTop);
            }
            if (current == end || (bottom = contentLimit.getBottom()) == -1) continue;
            parentContainer.updateBottom(c, bottom + extraBottom);
        }
    }

    private static class MarginCollapseResult {
        private int maxPositive;
        private int maxNegative;

        private MarginCollapseResult() {
        }

        public void update(int value) {
            if (value < 0 && value < this.maxNegative) {
                this.maxNegative = value;
            }
            if (value > 0 && value > this.maxPositive) {
                this.maxPositive = value;
            }
        }

        public int getMargin() {
            return this.maxPositive + this.maxNegative;
        }

        public boolean hasMargin() {
            return this.maxPositive != 0 || this.maxNegative != 0;
        }
    }

    private static class LastLineBoxContext {
        public int current;
        public LineBox line;

        public LastLineBoxContext(int i) {
            this.current = i;
        }
    }
}

