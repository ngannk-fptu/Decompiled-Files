/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.xhtmlrenderer.context.ContentFunctionFactory;
import org.xhtmlrenderer.context.StyleReference;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.CounterData;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.value.FontSpecification;
import org.xhtmlrenderer.extend.FSCanvas;
import org.xhtmlrenderer.extend.FontContext;
import org.xhtmlrenderer.extend.NamespaceHandler;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.TextRenderer;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.BlockFormattingContext;
import org.xhtmlrenderer.layout.BreakAtLineContext;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.LayoutState;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.layout.StyleTracker;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.FSFontMetrics;
import org.xhtmlrenderer.render.MarkerData;
import org.xhtmlrenderer.render.PageBox;

public class LayoutContext
implements CssContext {
    private SharedContext _sharedContext;
    private Layer _rootLayer;
    private StyleTracker _firstLines;
    private StyleTracker _firstLetters;
    private MarkerData _currentMarkerData;
    private LinkedList _bfcs;
    private LinkedList _layers;
    private FontContext _fontContext;
    private ContentFunctionFactory _contentFunctionFactory = new ContentFunctionFactory();
    private int _extraSpaceTop;
    private int _extraSpaceBottom;
    private Map _counterContextMap = new HashMap();
    private String _pendingPageName;
    private String _pageName;
    private int _noPageBreak = 0;
    private Layer _rootDocumentLayer;
    private PageBox _page;
    private boolean _mayCheckKeepTogether = true;
    private BreakAtLineContext _breakAtLineContext;

    public TextRenderer getTextRenderer() {
        return this._sharedContext.getTextRenderer();
    }

    @Override
    public StyleReference getCss() {
        return this._sharedContext.getCss();
    }

    public FSCanvas getCanvas() {
        return this._sharedContext.getCanvas();
    }

    public Rectangle getFixedRectangle() {
        return this._sharedContext.getFixedRectangle();
    }

    public NamespaceHandler getNamespaceHandler() {
        return this._sharedContext.getNamespaceHandler();
    }

    LayoutContext(SharedContext sharedContext) {
        this._sharedContext = sharedContext;
        this._bfcs = new LinkedList();
        this._layers = new LinkedList();
        this._firstLines = new StyleTracker();
        this._firstLetters = new StyleTracker();
    }

    public void reInit(boolean keepLayers) {
        this._firstLines = new StyleTracker();
        this._firstLetters = new StyleTracker();
        this._currentMarkerData = null;
        this._bfcs = new LinkedList();
        if (!keepLayers) {
            this._rootLayer = null;
            this._layers = new LinkedList();
        }
        this._extraSpaceTop = 0;
        this._extraSpaceBottom = 0;
    }

    public LayoutState captureLayoutState() {
        LayoutState result = new LayoutState();
        result.setFirstLines(this._firstLines);
        result.setFirstLetters(this._firstLetters);
        result.setCurrentMarkerData(this._currentMarkerData);
        result.setBFCs(this._bfcs);
        if (this.isPrint()) {
            result.setPageName(this.getPageName());
            result.setExtraSpaceBottom(this.getExtraSpaceBottom());
            result.setExtraSpaceTop(this.getExtraSpaceTop());
            result.setNoPageBreak(this.getNoPageBreak());
        }
        return result;
    }

    public void restoreLayoutState(LayoutState layoutState) {
        this._firstLines = layoutState.getFirstLines();
        this._firstLetters = layoutState.getFirstLetters();
        this._currentMarkerData = layoutState.getCurrentMarkerData();
        this._bfcs = layoutState.getBFCs();
        if (this.isPrint()) {
            this.setPageName(layoutState.getPageName());
            this.setExtraSpaceBottom(layoutState.getExtraSpaceBottom());
            this.setExtraSpaceTop(layoutState.getExtraSpaceTop());
            this.setNoPageBreak(layoutState.getNoPageBreak());
        }
    }

    public LayoutState copyStateForRelayout() {
        LayoutState result = new LayoutState();
        result.setFirstLetters(this._firstLetters.copyOf());
        result.setFirstLines(this._firstLines.copyOf());
        result.setCurrentMarkerData(this._currentMarkerData);
        if (this.isPrint()) {
            result.setPageName(this.getPageName());
        }
        return result;
    }

    public void restoreStateForRelayout(LayoutState layoutState) {
        this._firstLines = layoutState.getFirstLines();
        this._firstLetters = layoutState.getFirstLetters();
        this._currentMarkerData = layoutState.getCurrentMarkerData();
        if (this.isPrint()) {
            this.setPageName(layoutState.getPageName());
        }
    }

    public BlockFormattingContext getBlockFormattingContext() {
        return (BlockFormattingContext)this._bfcs.getLast();
    }

    public void pushBFC(BlockFormattingContext bfc) {
        this._bfcs.add(bfc);
    }

    public void popBFC() {
        this._bfcs.removeLast();
    }

    public void pushLayer(Box master) {
        Layer layer = null;
        if (this._rootLayer == null) {
            this._rootLayer = layer = new Layer(master);
        } else {
            Layer parent = this.getLayer();
            layer = new Layer(parent, master);
            parent.addChild(layer);
        }
        this.pushLayer(layer);
    }

    public void pushLayer(Layer layer) {
        this._layers.add(layer);
    }

    public void popLayer() {
        Layer layer = this.getLayer();
        layer.finish(this);
        this._layers.removeLast();
    }

    public Layer getLayer() {
        return (Layer)this._layers.getLast();
    }

    public Layer getRootLayer() {
        return this._rootLayer;
    }

    public void translate(int x, int y) {
        this.getBlockFormattingContext().translate(x, y);
    }

    public void addBoxId(String id, Box box) {
        this._sharedContext.addBoxId(id, box);
    }

    public void removeBoxId(String id) {
        this._sharedContext.removeBoxId(id);
    }

    public boolean isInteractive() {
        return this._sharedContext.isInteractive();
    }

    @Override
    public float getMmPerDot() {
        return this._sharedContext.getMmPerPx();
    }

    @Override
    public int getDotsPerPixel() {
        return this._sharedContext.getDotsPerPixel();
    }

    @Override
    public float getFontSize2D(FontSpecification font) {
        return this._sharedContext.getFont(font).getSize2D();
    }

    @Override
    public float getXHeight(FontSpecification parentFont) {
        return this._sharedContext.getXHeight(this.getFontContext(), parentFont);
    }

    @Override
    public FSFont getFont(FontSpecification font) {
        return this._sharedContext.getFont(font);
    }

    public UserAgentCallback getUac() {
        return this._sharedContext.getUac();
    }

    public boolean isPrint() {
        return this._sharedContext.isPrint();
    }

    public StyleTracker getFirstLinesTracker() {
        return this._firstLines;
    }

    public StyleTracker getFirstLettersTracker() {
        return this._firstLetters;
    }

    public MarkerData getCurrentMarkerData() {
        return this._currentMarkerData;
    }

    public void setCurrentMarkerData(MarkerData currentMarkerData) {
        this._currentMarkerData = currentMarkerData;
    }

    public ReplacedElementFactory getReplacedElementFactory() {
        return this._sharedContext.getReplacedElementFactory();
    }

    public FontContext getFontContext() {
        return this._fontContext;
    }

    public void setFontContext(FontContext fontContext) {
        this._fontContext = fontContext;
    }

    public ContentFunctionFactory getContentFunctionFactory() {
        return this._contentFunctionFactory;
    }

    public SharedContext getSharedContext() {
        return this._sharedContext;
    }

    public int getExtraSpaceBottom() {
        return this._extraSpaceBottom;
    }

    public void setExtraSpaceBottom(int extraSpaceBottom) {
        this._extraSpaceBottom = extraSpaceBottom;
    }

    public int getExtraSpaceTop() {
        return this._extraSpaceTop;
    }

    public void setExtraSpaceTop(int extraSpaceTop) {
        this._extraSpaceTop = extraSpaceTop;
    }

    public void resolveCounters(CalculatedStyle style, Integer startIndex) {
        CounterContext cc = new CounterContext(style, startIndex);
        this._counterContextMap.put(style, cc);
    }

    public void resolveCounters(CalculatedStyle style) {
        this.resolveCounters(style, null);
    }

    public CounterContext getCounterContext(CalculatedStyle style) {
        return (CounterContext)this._counterContextMap.get(style);
    }

    @Override
    public FSFontMetrics getFSFontMetrics(FSFont font) {
        return this.getTextRenderer().getFSFontMetrics(this.getFontContext(), font, "");
    }

    public String getPageName() {
        return this._pageName;
    }

    public void setPageName(String currentPageName) {
        this._pageName = currentPageName;
    }

    public int getNoPageBreak() {
        return this._noPageBreak;
    }

    public void setNoPageBreak(int noPageBreak) {
        this._noPageBreak = noPageBreak;
    }

    public boolean isPageBreaksAllowed() {
        return this._noPageBreak == 0;
    }

    public String getPendingPageName() {
        return this._pendingPageName;
    }

    public void setPendingPageName(String pendingPageName) {
        this._pendingPageName = pendingPageName;
    }

    public Layer getRootDocumentLayer() {
        return this._rootDocumentLayer;
    }

    public void setRootDocumentLayer(Layer rootDocumentLayer) {
        this._rootDocumentLayer = rootDocumentLayer;
    }

    public PageBox getPage() {
        return this._page;
    }

    public void setPage(PageBox page) {
        this._page = page;
    }

    public boolean isMayCheckKeepTogether() {
        return this._mayCheckKeepTogether;
    }

    public void setMayCheckKeepTogether(boolean mayKeepTogether) {
        this._mayCheckKeepTogether = mayKeepTogether;
    }

    public BreakAtLineContext getBreakAtLineContext() {
        return this._breakAtLineContext;
    }

    public void setBreakAtLineContext(BreakAtLineContext breakAtLineContext) {
        this._breakAtLineContext = breakAtLineContext;
    }

    public class CounterContext {
        private Map _counters = new HashMap();
        private CounterContext _parent;

        CounterContext(CalculatedStyle style, Integer startIndex) {
            List increments;
            List resets;
            if (startIndex != null) {
                this._counters.put("list-item", startIndex);
            }
            this._parent = (CounterContext)LayoutContext.this._counterContextMap.get(style.getParent());
            if (this._parent == null) {
                this._parent = new CounterContext();
            }
            if ((resets = style.getCounterReset()) != null) {
                for (CounterData cd : resets) {
                    this._parent.resetCounter(cd);
                }
            }
            if ((increments = style.getCounterIncrement()) != null) {
                for (CounterData cd : increments) {
                    if (this._parent.incrementCounter(cd)) continue;
                    this._parent.resetCounter(new CounterData(cd.getName(), 0));
                    this._parent.incrementCounter(cd);
                }
            }
            if (style.isIdent(CSSName.DISPLAY, IdentValue.LIST_ITEM)) {
                if (startIndex != null) {
                    this._parent._counters.put("list-item", startIndex);
                }
                this._parent.incrementListItemCounter(1);
            }
        }

        private CounterContext() {
        }

        private boolean incrementCounter(CounterData cd) {
            if ("list-item".equals(cd.getName())) {
                this.incrementListItemCounter(cd.getValue());
                return true;
            }
            Integer currentValue = (Integer)this._counters.get(cd.getName());
            if (currentValue == null) {
                if (this._parent == null) {
                    return false;
                }
                return this._parent.incrementCounter(cd);
            }
            this._counters.put(cd.getName(), new Integer(currentValue + cd.getValue()));
            return true;
        }

        private void incrementListItemCounter(int increment) {
            Integer currentValue = (Integer)this._counters.get("list-item");
            if (currentValue == null) {
                currentValue = new Integer(0);
            }
            this._counters.put("list-item", new Integer(currentValue + increment));
        }

        private void resetCounter(CounterData cd) {
            this._counters.put(cd.getName(), new Integer(cd.getValue()));
        }

        public int getCurrentCounterValue(String name) {
            Integer value = this._parent.getCounter(name);
            if (value == null) {
                this._parent.resetCounter(new CounterData(name, 0));
                return 0;
            }
            return value;
        }

        private Integer getCounter(String name) {
            Integer value = (Integer)this._counters.get(name);
            if (value != null) {
                return value;
            }
            if (this._parent == null) {
                return null;
            }
            return this._parent.getCounter(name);
        }

        public List getCurrentCounterValues(String name) {
            ArrayList<Integer> values = new ArrayList<Integer>();
            this._parent.getCounterValues(name, values);
            if (values.size() == 0) {
                this._parent.resetCounter(new CounterData(name, 0));
                values.add(new Integer(0));
            }
            return values;
        }

        private void getCounterValues(String name, List values) {
            Integer value;
            if (this._parent != null) {
                this._parent.getCounterValues(name, values);
            }
            if ((value = (Integer)this._counters.get(name)) != null) {
                values.add(value);
            }
        }
    }
}

