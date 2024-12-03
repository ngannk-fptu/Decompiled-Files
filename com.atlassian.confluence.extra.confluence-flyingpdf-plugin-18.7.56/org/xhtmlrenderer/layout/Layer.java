/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.PageElementPosition;
import org.xhtmlrenderer.css.newmatch.PageInfo;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.EmptyStyle;
import org.xhtmlrenderer.layout.BlockFormattingContext;
import org.xhtmlrenderer.layout.BoxCollector;
import org.xhtmlrenderer.layout.BoxRangeHelper;
import org.xhtmlrenderer.layout.BoxRangeLists;
import org.xhtmlrenderer.layout.CollapsedBorderSide;
import org.xhtmlrenderer.layout.InlinePaintable;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.LayoutState;
import org.xhtmlrenderer.layout.PaintingInfo;
import org.xhtmlrenderer.newtable.TableBox;
import org.xhtmlrenderer.newtable.TableCellBox;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.BoxDimensions;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.render.ViewportBox;

public class Layer {
    public static final short PAGED_MODE_SCREEN = 1;
    public static final short PAGED_MODE_PRINT = 2;
    private Layer _parent;
    private boolean _stackingContext;
    private List _children;
    private Box _master;
    private Box _end;
    private List _floats;
    private boolean _fixedBackground;
    private boolean _inline;
    private boolean _requiresLayout;
    private List _pages;
    private PageBox _lastRequestedPage = null;
    private Set _pageSequences;
    private List _sortedPageSequences;
    private Map _runningBlocks;
    private Box _selectionStart;
    private Box _selectionEnd;
    private int _selectionStartX;
    private int _selectionStartY;
    private int _selectionEndX;
    private int _selectionEndY;
    private static final int POSITIVE = 1;
    private static final int ZERO = 2;
    private static final int NEGATIVE = 3;
    private static final int AUTO = 4;

    public Layer(Box master) {
        this(null, master);
        this.setStackingContext(true);
    }

    public Layer(Layer parent, Box master) {
        this._parent = parent;
        this._master = master;
        this.setStackingContext(master.getStyle().isPositioned() && !master.getStyle().isAutoZIndex());
        master.setLayer(this);
        master.setContainingLayer(this);
    }

    public Layer getParent() {
        return this._parent;
    }

    public boolean isStackingContext() {
        return this._stackingContext;
    }

    public void setStackingContext(boolean stackingContext) {
        this._stackingContext = stackingContext;
    }

    public int getZIndex() {
        return (int)this._master.getStyle().asFloat(CSSName.Z_INDEX);
    }

    public Box getMaster() {
        return this._master;
    }

    public synchronized void addChild(Layer layer) {
        if (this._children == null) {
            this._children = new ArrayList();
        }
        this._children.add(layer);
    }

    public void addFloat(BlockBox floater, BlockFormattingContext bfc) {
        if (this._floats == null) {
            this._floats = new ArrayList();
        }
        this._floats.add(floater);
        floater.getFloatedBoxData().setDrawingLayer(this);
    }

    public void removeFloat(BlockBox floater) {
        if (this._floats != null) {
            this._floats.remove(floater);
        }
    }

    private void paintFloats(RenderingContext c) {
        if (this._floats != null) {
            for (int i = this._floats.size() - 1; i >= 0; --i) {
                BlockBox floater = (BlockBox)this._floats.get(i);
                this.paintAsLayer(c, floater);
            }
        }
    }

    private void paintLayers(RenderingContext c, List layers) {
        for (int i = 0; i < layers.size(); ++i) {
            Layer layer = (Layer)layers.get(i);
            layer.paint(c);
        }
    }

    private List collectLayers(int which) {
        ArrayList<Layer> result = new ArrayList<Layer>();
        if (which != 4) {
            result.addAll(this.getStackingContextLayers(which));
        }
        List children = this.getChildren();
        for (int i = 0; i < children.size(); ++i) {
            Layer child = (Layer)children.get(i);
            if (child.isStackingContext()) continue;
            if (which == 4) {
                result.add(child);
            }
            result.addAll(child.collectLayers(which));
        }
        return result;
    }

    private List getStackingContextLayers(int which) {
        ArrayList<Layer> result = new ArrayList<Layer>();
        List children = this.getChildren();
        for (int i = 0; i < children.size(); ++i) {
            Layer target = (Layer)children.get(i);
            if (!target.isStackingContext()) continue;
            int zIndex = target.getZIndex();
            if (which == 3 && zIndex < 0) {
                result.add(target);
                continue;
            }
            if (which == 1 && zIndex > 0) {
                result.add(target);
                continue;
            }
            if (which != 2 || zIndex != 0) continue;
            result.add(target);
        }
        return result;
    }

    private List getSortedLayers(int which) {
        List result = this.collectLayers(which);
        Collections.sort(result, new ZIndexComparator());
        return result;
    }

    private void paintBackgroundsAndBorders(RenderingContext c, List blocks, Map collapsedTableBorders, BoxRangeLists rangeLists) {
        BoxRangeHelper helper = new BoxRangeHelper(c.getOutputDevice(), rangeLists.getBlock());
        for (int i = 0; i < blocks.size(); ++i) {
            List borders;
            TableCellBox cell;
            helper.popClipRegions(c, i);
            BlockBox box = (BlockBox)blocks.get(i);
            box.paintBackground(c);
            box.paintBorder(c);
            if (c.debugDrawBoxes()) {
                box.paintDebugOutline(c);
            }
            if (collapsedTableBorders != null && box instanceof TableCellBox && (cell = (TableCellBox)box).hasCollapsedPaintingBorder() && (borders = (List)collapsedTableBorders.get(cell)) != null) {
                this.paintCollapsedTableBorders(c, borders);
            }
            helper.pushClipRegion(c, i);
        }
        helper.popClipRegions(c, blocks.size());
    }

    private void paintInlineContent(RenderingContext c, List lines, BoxRangeLists rangeLists) {
        BoxRangeHelper helper = new BoxRangeHelper(c.getOutputDevice(), rangeLists.getInline());
        for (int i = 0; i < lines.size(); ++i) {
            helper.popClipRegions(c, i);
            helper.pushClipRegion(c, i);
            InlinePaintable paintable = (InlinePaintable)lines.get(i);
            paintable.paintInline(c);
        }
        helper.popClipRegions(c, lines.size());
    }

    private void paintSelection(RenderingContext c, List lines) {
        if (c.getOutputDevice().isSupportsSelection()) {
            for (InlinePaintable paintable : lines) {
                if (!(paintable instanceof InlineLayoutBox)) continue;
                ((InlineLayoutBox)paintable).paintSelection(c);
            }
        }
    }

    public Dimension getPaintingDimension(LayoutContext c) {
        return this.calcPaintingDimension(c).getOuterMarginCorner();
    }

    public void paint(RenderingContext c) {
        if (this.getMaster().getStyle().isFixed()) {
            this.positionFixedLayer(c);
        }
        if (this.isRootLayer()) {
            this.getMaster().paintRootElementBackground(c);
        }
        if (!this.isInline() && ((BlockBox)this.getMaster()).isReplaced()) {
            this.paintLayerBackgroundAndBorder(c);
            this.paintReplacedElement(c, (BlockBox)this.getMaster());
        } else {
            BoxRangeLists rangeLists = new BoxRangeLists();
            ArrayList blocks = new ArrayList();
            ArrayList lines = new ArrayList();
            BoxCollector collector = new BoxCollector();
            collector.collect(c, c.getOutputDevice().getClip(), this, blocks, lines, rangeLists);
            if (!this.isInline()) {
                this.paintLayerBackgroundAndBorder(c);
                if (c.debugDrawBoxes()) {
                    ((BlockBox)this.getMaster()).paintDebugOutline(c);
                }
            }
            if (this.isRootLayer() || this.isStackingContext()) {
                this.paintLayers(c, this.getSortedLayers(3));
            }
            Map collapsedTableBorders = this.collectCollapsedTableBorders(c, blocks);
            this.paintBackgroundsAndBorders(c, blocks, collapsedTableBorders, rangeLists);
            this.paintFloats(c);
            this.paintListMarkers(c, blocks, rangeLists);
            this.paintInlineContent(c, lines, rangeLists);
            this.paintReplacedElements(c, blocks, rangeLists);
            this.paintSelection(c, lines);
            if (this.isRootLayer() || this.isStackingContext()) {
                this.paintLayers(c, this.collectLayers(4));
                this.paintLayers(c, this.getSortedLayers(2));
                this.paintLayers(c, this.getSortedLayers(1));
            }
        }
    }

    private List getFloats() {
        return this._floats == null ? Collections.EMPTY_LIST : this._floats;
    }

    public Box find(CssContext cssCtx, int absX, int absY, boolean findAnonymous) {
        Box result = null;
        if (this.isRootLayer() || this.isStackingContext()) {
            result = this.find(cssCtx, absX, absY, this.getSortedLayers(1), findAnonymous);
            if (result != null) {
                return result;
            }
            result = this.find(cssCtx, absX, absY, this.getSortedLayers(2), findAnonymous);
            if (result != null) {
                return result;
            }
            result = this.find(cssCtx, absX, absY, this.collectLayers(4), findAnonymous);
            if (result != null) {
                return result;
            }
        }
        for (int i = 0; i < this.getFloats().size(); ++i) {
            Box floater = (Box)this.getFloats().get(i);
            result = floater.find(cssCtx, absX, absY, findAnonymous);
            if (result == null) continue;
            return result;
        }
        result = this.getMaster().find(cssCtx, absX, absY, findAnonymous);
        if (result != null) {
            return result;
        }
        if ((this.isRootLayer() || this.isStackingContext()) && (result = this.find(cssCtx, absX, absY, this.getSortedLayers(3), findAnonymous)) != null) {
            return result;
        }
        return null;
    }

    private Box find(CssContext cssCtx, int absX, int absY, List layers, boolean findAnonymous) {
        Box result = null;
        for (int i = layers.size() - 1; i >= 0; --i) {
            Layer l = (Layer)layers.get(i);
            result = l.find(cssCtx, absX, absY, findAnonymous);
            if (result == null) continue;
            return result;
        }
        return result;
    }

    private Map collectCollapsedTableBorders(RenderingContext c, List blocks) {
        ArrayList borders;
        HashMap cellBordersByTable = new HashMap();
        HashMap<TableBox, TableCellBox> triggerCellsByTable = new HashMap<TableBox, TableCellBox>();
        HashSet all = new HashSet();
        for (Box b : blocks) {
            TableCellBox cell;
            if (!(b instanceof TableCellBox) || !(cell = (TableCellBox)b).hasCollapsedPaintingBorder()) continue;
            borders = (ArrayList)cellBordersByTable.get(cell.getTable());
            if (borders == null) {
                borders = new ArrayList();
                cellBordersByTable.put(cell.getTable(), borders);
            }
            triggerCellsByTable.put(cell.getTable(), cell);
            cell.addCollapsedBorders(all, borders);
        }
        if (triggerCellsByTable.size() == 0) {
            return null;
        }
        HashMap result = new HashMap();
        for (TableCellBox cell : triggerCellsByTable.values()) {
            borders = (List)cellBordersByTable.get(cell.getTable());
            Collections.sort(borders);
            result.put(cell, borders);
        }
        return result;
    }

    private void paintCollapsedTableBorders(RenderingContext c, List borders) {
        for (CollapsedBorderSide border : borders) {
            border.getCell().paintCollapsedBorder(c, border.getSide());
        }
    }

    public void paintAsLayer(RenderingContext c, BlockBox startingPoint) {
        BoxRangeLists rangeLists = new BoxRangeLists();
        ArrayList blocks = new ArrayList();
        ArrayList lines = new ArrayList();
        BoxCollector collector = new BoxCollector();
        collector.collect(c, c.getOutputDevice().getClip(), this, startingPoint, blocks, lines, rangeLists);
        Map collapsedTableBorders = this.collectCollapsedTableBorders(c, blocks);
        this.paintBackgroundsAndBorders(c, blocks, collapsedTableBorders, rangeLists);
        this.paintListMarkers(c, blocks, rangeLists);
        this.paintInlineContent(c, lines, rangeLists);
        this.paintSelection(c, lines);
        this.paintReplacedElements(c, blocks, rangeLists);
    }

    private void paintListMarkers(RenderingContext c, List blocks, BoxRangeLists rangeLists) {
        BoxRangeHelper helper = new BoxRangeHelper(c.getOutputDevice(), rangeLists.getBlock());
        for (int i = 0; i < blocks.size(); ++i) {
            helper.popClipRegions(c, i);
            BlockBox box = (BlockBox)blocks.get(i);
            box.paintListMarker(c);
            helper.pushClipRegion(c, i);
        }
        helper.popClipRegions(c, blocks.size());
    }

    private void paintReplacedElements(RenderingContext c, List blocks, BoxRangeLists rangeLists) {
        BoxRangeHelper helper = new BoxRangeHelper(c.getOutputDevice(), rangeLists.getBlock());
        for (int i = 0; i < blocks.size(); ++i) {
            helper.popClipRegions(c, i);
            BlockBox box = (BlockBox)blocks.get(i);
            if (box.isReplaced()) {
                this.paintReplacedElement(c, box);
            }
            helper.pushClipRegion(c, i);
        }
        helper.popClipRegions(c, blocks.size());
    }

    private void positionFixedLayer(RenderingContext c) {
        Rectangle rect = c.getFixedRectangle();
        Box fixed = this.getMaster();
        fixed.setX(0);
        fixed.setY(0);
        fixed.setAbsX(0);
        fixed.setAbsY(0);
        fixed.setContainingBlock(new ViewportBox(rect));
        ((BlockBox)fixed).positionAbsolute(c, 3);
        fixed.calcPaintingInfo(c, false);
    }

    private void paintLayerBackgroundAndBorder(RenderingContext c) {
        if (this.getMaster() instanceof BlockBox) {
            BlockBox box = (BlockBox)this.getMaster();
            box.paintBackground(c);
            box.paintBorder(c);
        }
    }

    private void paintReplacedElement(RenderingContext c, BlockBox replaced) {
        Rectangle contentBounds = replaced.getContentAreaEdge(replaced.getAbsX(), replaced.getAbsY(), c);
        Point loc = replaced.getReplacedElement().getLocation();
        if (contentBounds.x != loc.x || contentBounds.y != loc.y) {
            replaced.getReplacedElement().setLocation(contentBounds.x, contentBounds.y);
        }
        if (!c.isInteractive() || replaced.getReplacedElement().isRequiresInteractivePaint()) {
            c.getOutputDevice().paintReplacedElement(c, replaced);
        }
    }

    public boolean isRootLayer() {
        return this.getParent() == null && this.isStackingContext();
    }

    private void moveIfGreater(Dimension result, Dimension test) {
        if (test.width > result.width) {
            result.width = test.width;
        }
        if (test.height > result.height) {
            result.height = test.height;
        }
    }

    private PaintingInfo calcPaintingDimension(LayoutContext c) {
        this.getMaster().calcPaintingInfo(c, true);
        PaintingInfo result = this.getMaster().getPaintingInfo().copyOf();
        List children = this.getChildren();
        for (int i = 0; i < children.size(); ++i) {
            Layer child = (Layer)children.get(i);
            if (child.getMaster().getStyle().isFixed() || !child.getMaster().getStyle().isAbsolute()) continue;
            PaintingInfo info = child.calcPaintingDimension(c);
            this.moveIfGreater(result.getOuterMarginCorner(), info.getOuterMarginCorner());
        }
        return result;
    }

    public void positionChildren(LayoutContext c) {
        for (Layer child : this.getChildren()) {
            child.position(c);
        }
    }

    private void position(LayoutContext c) {
        if (this.getMaster().getStyle().isAbsolute() && !c.isPrint()) {
            ((BlockBox)this.getMaster()).positionAbsolute(c, 3);
        } else if (this.getMaster().getStyle().isRelative() && (this.isInline() || ((BlockBox)this.getMaster()).isInline())) {
            this.getMaster().positionRelative(c);
            if (!this.isInline()) {
                this.getMaster().calcCanvasLocation();
                this.getMaster().calcChildLocations();
            }
        }
    }

    private boolean containsFixedLayer() {
        for (Layer child : this.getChildren()) {
            if (!child.getMaster().getStyle().isFixed() && !child.containsFixedLayer()) continue;
            return true;
        }
        return false;
    }

    public boolean containsFixedContent() {
        return this._fixedBackground || this.containsFixedLayer();
    }

    public void setFixedBackground(boolean b) {
        this._fixedBackground = b;
    }

    public synchronized List getChildren() {
        return this._children == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(this._children);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void remove(Layer layer) {
        boolean removed = false;
        Layer layer2 = this;
        synchronized (layer2) {
            if (this._children != null) {
                Iterator i = this._children.iterator();
                while (i.hasNext()) {
                    Layer child = (Layer)i.next();
                    if (child != layer) continue;
                    removed = true;
                    i.remove();
                    break;
                }
            }
        }
        if (!removed) {
            throw new RuntimeException("Could not find layer to remove");
        }
    }

    public void detach() {
        if (this.getParent() != null) {
            this.getParent().remove(this);
        }
    }

    public boolean isInline() {
        return this._inline;
    }

    public void setInline(boolean inline) {
        this._inline = inline;
    }

    public Box getEnd() {
        return this._end;
    }

    public void setEnd(Box end) {
        this._end = end;
    }

    public boolean isRequiresLayout() {
        return this._requiresLayout;
    }

    public void setRequiresLayout(boolean requiresLayout) {
        this._requiresLayout = requiresLayout;
    }

    public void finish(LayoutContext c) {
        if (c.isPrint()) {
            this.layoutAbsoluteChildren(c);
        }
        if (!this.isInline()) {
            this.positionChildren(c);
        }
    }

    private void layoutAbsoluteChildren(LayoutContext c) {
        ArrayList children = new ArrayList(this.getChildren());
        if (children.size() > 0) {
            LayoutState state = c.captureLayoutState();
            for (int i = 0; i < children.size(); ++i) {
                Layer child = (Layer)children.get(i);
                if (!child.isRequiresLayout()) continue;
                this.layoutAbsoluteChild(c, child);
                if (child.getMaster().getStyle().isAvoidPageBreakInside() && child.getMaster().crossesPageBreak(c)) {
                    child.getMaster().reset(c);
                    ((BlockBox)child.getMaster()).setNeedPageClear(true);
                    this.layoutAbsoluteChild(c, child);
                    if (child.getMaster().crossesPageBreak(c)) {
                        child.getMaster().reset(c);
                        this.layoutAbsoluteChild(c, child);
                    }
                }
                child.setRequiresLayout(false);
                child.finish(c);
                c.getRootLayer().ensureHasPage(c, child.getMaster());
            }
            c.restoreLayoutState(state);
        }
    }

    private void layoutAbsoluteChild(LayoutContext c, Layer child) {
        BlockBox master = (BlockBox)child.getMaster();
        if (child.getMaster().getStyle().isBottomAuto()) {
            master.positionAbsolute(c, 3);
            master.positionAbsoluteOnPage(c);
            c.reInit(true);
            ((BlockBox)child.getMaster()).layout(c);
            master.positionAbsolute(c, 2);
        } else {
            c.reInit(true);
            master.layout(c);
            BoxDimensions before = master.getBoxDimensions();
            master.reset(c);
            BoxDimensions after = master.getBoxDimensions();
            master.setBoxDimensions(before);
            master.positionAbsolute(c, 3);
            master.positionAbsoluteOnPage(c);
            master.setBoxDimensions(after);
            c.reInit(true);
            ((BlockBox)child.getMaster()).layout(c);
        }
    }

    public List getPages() {
        return this._pages == null ? Collections.EMPTY_LIST : this._pages;
    }

    public void setPages(List pages) {
        this._pages = pages;
    }

    public boolean isLastPage(PageBox pageBox) {
        return this._pages.get(this._pages.size() - 1) == pageBox;
    }

    public void addPage(CssContext c) {
        List pages;
        String pseudoPage = null;
        if (this._pages == null) {
            this._pages = new ArrayList();
        }
        pseudoPage = (pages = this.getPages()).size() == 0 ? "first" : (pages.size() % 2 == 0 ? "right" : "left");
        PageBox pageBox = Layer.createPageBox(c, pseudoPage);
        if (pages.size() == 0) {
            pageBox.setTopAndBottom(c, 0);
        } else {
            PageBox previous = (PageBox)pages.get(pages.size() - 1);
            pageBox.setTopAndBottom(c, previous.getBottom());
        }
        pageBox.setPageNo(pages.size());
        pages.add(pageBox);
    }

    public void removeLastPage() {
        PageBox pageBox = (PageBox)this._pages.remove(this._pages.size() - 1);
        if (pageBox == this.getLastRequestedPage()) {
            this.setLastRequestedPage(null);
        }
    }

    public static PageBox createPageBox(CssContext c, String pseudoPage) {
        PageBox result = new PageBox();
        String pageName = null;
        if (c instanceof LayoutContext) {
            pageName = ((LayoutContext)c).getPageName();
        }
        PageInfo pageInfo = c.getCss().getPageStyle(pageName, pseudoPage);
        result.setPageInfo(pageInfo);
        CalculatedStyle cs = new EmptyStyle().deriveStyle(pageInfo.getPageStyle());
        result.setStyle(cs);
        result.setOuterPageWidth(result.getWidth(c));
        return result;
    }

    public PageBox getFirstPage(CssContext c, Box box) {
        return this.getPage(c, box.getAbsY());
    }

    public PageBox getLastPage(CssContext c, Box box) {
        return this.getPage(c, box.getAbsY() + box.getHeight() - 1);
    }

    public void ensureHasPage(CssContext c, Box box) {
        this.getLastPage(c, box);
    }

    public PageBox getPage(CssContext c, int yOffset) {
        List pages = this.getPages();
        if (yOffset < 0) {
            return null;
        }
        PageBox lastRequested = this.getLastRequestedPage();
        if (lastRequested != null && yOffset >= lastRequested.getTop() && yOffset < lastRequested.getBottom()) {
            return lastRequested;
        }
        PageBox last = (PageBox)pages.get(pages.size() - 1);
        if (yOffset < last.getBottom()) {
            int count = pages.size();
            for (int i = count - 1; i >= 0 && i >= count - 5; --i) {
                PageBox pageBox = (PageBox)pages.get(i);
                if (yOffset < pageBox.getTop() || yOffset >= pageBox.getBottom()) continue;
                this.setLastRequestedPage(pageBox);
                return pageBox;
            }
            int low = 0;
            int high = count - 6;
            while (low <= high) {
                int mid = low + high >> 1;
                PageBox pageBox = (PageBox)pages.get(mid);
                if (yOffset >= pageBox.getTop() && yOffset < pageBox.getBottom()) {
                    this.setLastRequestedPage(pageBox);
                    return pageBox;
                }
                if (pageBox.getTop() < yOffset) {
                    low = mid + 1;
                    continue;
                }
                high = mid - 1;
            }
        } else {
            this.addPagesUntilPosition(c, yOffset);
            PageBox result = (PageBox)pages.get(pages.size() - 1);
            this.setLastRequestedPage(result);
            return result;
        }
        throw new RuntimeException("internal error");
    }

    private void addPagesUntilPosition(CssContext c, int position) {
        List pages = this.getPages();
        PageBox last = (PageBox)pages.get(pages.size() - 1);
        while (position >= last.getBottom()) {
            this.addPage(c);
            last = (PageBox)pages.get(pages.size() - 1);
        }
    }

    public void trimEmptyPages(CssContext c, int maxYHeight) {
        PageBox page;
        List pages = this.getPages();
        for (int i = pages.size() - 1; i > 0 && (page = (PageBox)pages.get(i)).getTop() >= maxYHeight; --i) {
            if (page == this.getLastRequestedPage()) {
                this.setLastRequestedPage(null);
            }
            pages.remove(i);
        }
    }

    public void trimPageCount(int newPageCount) {
        while (this._pages.size() > newPageCount) {
            PageBox pageBox = (PageBox)this._pages.remove(this._pages.size() - 1);
            if (pageBox != this.getLastRequestedPage()) continue;
            this.setLastRequestedPage(null);
        }
    }

    public void assignPagePaintingPositions(CssContext cssCtx, short mode) {
        this.assignPagePaintingPositions(cssCtx, mode, 0);
    }

    public void assignPagePaintingPositions(CssContext cssCtx, int mode, int additionalClearance) {
        List pages = this.getPages();
        int paintingTop = additionalClearance;
        for (PageBox page : pages) {
            page.setPaintingTop(paintingTop);
            if (mode == 1) {
                page.setPaintingBottom(paintingTop + page.getHeight(cssCtx));
            } else if (mode == 2) {
                page.setPaintingBottom(paintingTop + page.getContentHeight(cssCtx));
            } else {
                throw new IllegalArgumentException("Illegal mode");
            }
            paintingTop = page.getPaintingBottom() + additionalClearance;
        }
    }

    public int getMaxPageWidth(CssContext cssCtx, int additionalClearance) {
        List pages = this.getPages();
        int maxWidth = 0;
        for (PageBox page : pages) {
            int pageWidth = page.getWidth(cssCtx) + additionalClearance * 2;
            if (pageWidth <= maxWidth) continue;
            maxWidth = pageWidth;
        }
        return maxWidth;
    }

    public PageBox getLastPage() {
        List pages = this.getPages();
        return pages.size() == 0 ? null : (PageBox)pages.get(pages.size() - 1);
    }

    public boolean crossesPageBreak(LayoutContext c, int top, int bottom) {
        if (top < 0) {
            return false;
        }
        PageBox page = this.getPage(c, top);
        return bottom >= page.getBottom() - c.getExtraSpaceBottom();
    }

    public Layer findRoot() {
        if (this.isRootLayer()) {
            return this;
        }
        return this.getParent().findRoot();
    }

    public void addRunningBlock(BlockBox block) {
        String identifier;
        ArrayList<BlockBox> blocks;
        if (this._runningBlocks == null) {
            this._runningBlocks = new HashMap();
        }
        if ((blocks = (ArrayList<BlockBox>)this._runningBlocks.get(identifier = block.getStyle().getRunningName())) == null) {
            blocks = new ArrayList<BlockBox>();
            this._runningBlocks.put(identifier, blocks);
        }
        blocks.add(block);
        Collections.sort(blocks, new Comparator(){

            public int compare(Object o1, Object o2) {
                BlockBox b1 = (BlockBox)o1;
                BlockBox b2 = (BlockBox)o2;
                return b1.getAbsY() - b2.getAbsY();
            }
        });
    }

    public void removeRunningBlock(BlockBox block) {
        if (this._runningBlocks == null) {
            return;
        }
        String identifier = block.getStyle().getRunningName();
        List blocks = (List)this._runningBlocks.get(identifier);
        if (blocks == null) {
            return;
        }
        blocks.remove(block);
    }

    public BlockBox getRunningBlock(String identifer, PageBox page, PageElementPosition which) {
        if (this._runningBlocks == null) {
            return null;
        }
        List blocks = (List)this._runningBlocks.get(identifer);
        if (blocks == null) {
            return null;
        }
        if (which == PageElementPosition.START) {
            BlockBox b;
            BlockBox prev = null;
            Iterator i = blocks.iterator();
            while (i.hasNext() && (b = (BlockBox)i.next()).getStaticEquivalent().getAbsY() < page.getTop()) {
                prev = b;
            }
            return prev;
        }
        if (which == PageElementPosition.FIRST) {
            for (BlockBox b : blocks) {
                int absY = b.getStaticEquivalent().getAbsY();
                if (absY < page.getTop() || absY >= page.getBottom()) continue;
                return b;
            }
            return this.getRunningBlock(identifer, page, PageElementPosition.START);
        }
        if (which == PageElementPosition.LAST) {
            BlockBox b;
            BlockBox prev = null;
            Iterator i = blocks.iterator();
            while (i.hasNext() && (b = (BlockBox)i.next()).getStaticEquivalent().getAbsY() <= page.getBottom()) {
                prev = b;
            }
            return prev;
        }
        if (which == PageElementPosition.LAST_EXCEPT) {
            BlockBox prev = null;
            for (BlockBox b : blocks) {
                int absY = b.getStaticEquivalent().getAbsY();
                if (absY >= page.getTop() && absY < page.getBottom()) {
                    return null;
                }
                if (absY > page.getBottom()) break;
                prev = b;
            }
            return prev;
        }
        throw new RuntimeException("bug: internal error");
    }

    public void layoutPages(LayoutContext c) {
        c.setRootDocumentLayer(c.getRootLayer());
        for (PageBox pageBox : this._pages) {
            pageBox.layout(c);
        }
    }

    public void addPageSequence(BlockBox start) {
        if (this._pageSequences == null) {
            this._pageSequences = new HashSet();
        }
        this._pageSequences.add(start);
    }

    private List getSortedPageSequences() {
        if (this._pageSequences == null) {
            return null;
        }
        if (this._sortedPageSequences == null) {
            ArrayList result = new ArrayList(this._pageSequences);
            Collections.sort(result, new Comparator(){

                public int compare(Object o1, Object o2) {
                    BlockBox b1 = (BlockBox)o1;
                    BlockBox b2 = (BlockBox)o2;
                    return b1.getAbsY() - b2.getAbsY();
                }
            });
            this._sortedPageSequences = result;
        }
        return this._sortedPageSequences;
    }

    public int getRelativePageNo(RenderingContext c, int absY) {
        List sequences = this.getSortedPageSequences();
        int initial = 0;
        if (c.getInitialPageNo() > 0) {
            initial = c.getInitialPageNo() - 1;
        }
        if (sequences == null || sequences.isEmpty()) {
            return initial + this.getPage(c, absY).getPageNo();
        }
        BlockBox pageSequence = this.findPageSequence(sequences, absY);
        int sequenceStartAbsolutePageNo = this.getPage(c, pageSequence.getAbsY()).getPageNo();
        int absoluteRequiredPageNo = this.getPage(c, absY).getPageNo();
        return absoluteRequiredPageNo - sequenceStartAbsolutePageNo;
    }

    private BlockBox findPageSequence(List sequences, int absY) {
        BlockBox result = null;
        for (int i = 0; i < sequences.size(); ++i) {
            result = (BlockBox)sequences.get(i);
            if (i < sequences.size() - 1 && ((BlockBox)sequences.get(i + 1)).getAbsY() > absY) break;
        }
        return result;
    }

    public int getRelativePageNo(RenderingContext c) {
        List sequences = this.getSortedPageSequences();
        int initial = 0;
        if (c.getInitialPageNo() > 0) {
            initial = c.getInitialPageNo() - 1;
        }
        if (sequences == null) {
            return initial + c.getPageNo();
        }
        int sequenceStartIndex = this.getPageSequenceStart(c, sequences, c.getPage());
        if (sequenceStartIndex == -1) {
            return initial + c.getPageNo();
        }
        BlockBox block = (BlockBox)sequences.get(sequenceStartIndex);
        return c.getPageNo() - this.getFirstPage(c, block).getPageNo();
    }

    public int getRelativePageCount(RenderingContext c) {
        int lastPage;
        BlockBox block;
        int firstPage;
        List sequences = this.getSortedPageSequences();
        int initial = 0;
        if (c.getInitialPageNo() > 0) {
            initial = c.getInitialPageNo() - 1;
        }
        if (sequences == null) {
            return initial + c.getPageCount();
        }
        int sequenceStartIndex = this.getPageSequenceStart(c, sequences, c.getPage());
        if (sequenceStartIndex == -1) {
            firstPage = 0;
        } else {
            block = (BlockBox)sequences.get(sequenceStartIndex);
            firstPage = this.getFirstPage(c, block).getPageNo();
        }
        if (sequenceStartIndex < sequences.size() - 1) {
            block = (BlockBox)sequences.get(sequenceStartIndex + 1);
            lastPage = this.getFirstPage(c, block).getPageNo();
        } else {
            lastPage = c.getPageCount();
        }
        int sequenceLength = lastPage - firstPage;
        if (sequenceStartIndex == -1) {
            sequenceLength += initial;
        }
        return sequenceLength;
    }

    private int getPageSequenceStart(RenderingContext c, List sequences, PageBox page) {
        for (int i = sequences.size() - 1; i >= 0; --i) {
            BlockBox start = (BlockBox)sequences.get(i);
            if (start.getAbsY() >= page.getBottom() - 1) continue;
            return i;
        }
        return -1;
    }

    public Box getSelectionEnd() {
        return this._selectionEnd;
    }

    public void setSelectionEnd(Box selectionEnd) {
        this._selectionEnd = selectionEnd;
    }

    public Box getSelectionStart() {
        return this._selectionStart;
    }

    public void setSelectionStart(Box selectionStart) {
        this._selectionStart = selectionStart;
    }

    public int getSelectionEndX() {
        return this._selectionEndX;
    }

    public void setSelectionEndX(int selectionEndX) {
        this._selectionEndX = selectionEndX;
    }

    public int getSelectionEndY() {
        return this._selectionEndY;
    }

    public void setSelectionEndY(int selectionEndY) {
        this._selectionEndY = selectionEndY;
    }

    public int getSelectionStartX() {
        return this._selectionStartX;
    }

    public void setSelectionStartX(int selectionStartX) {
        this._selectionStartX = selectionStartX;
    }

    public int getSelectionStartY() {
        return this._selectionStartY;
    }

    public void setSelectionStartY(int selectionStartY) {
        this._selectionStartY = selectionStartY;
    }

    private PageBox getLastRequestedPage() {
        return this._lastRequestedPage;
    }

    private void setLastRequestedPage(PageBox lastRequestedPage) {
        this._lastRequestedPage = lastRequestedPage;
    }

    private static class ZIndexComparator
    implements Comparator {
        private ZIndexComparator() {
        }

        public int compare(Object o1, Object o2) {
            Layer l1 = (Layer)o1;
            Layer l2 = (Layer)o2;
            return l1.getZIndex() - l2.getZIndex();
        }
    }
}

