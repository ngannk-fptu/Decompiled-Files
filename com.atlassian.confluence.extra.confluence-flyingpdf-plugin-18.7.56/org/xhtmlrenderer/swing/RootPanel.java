/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.CellRendererPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.derived.ColorValue;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.css.style.derived.StringValue;
import org.xhtmlrenderer.event.DocumentListener;
import org.xhtmlrenderer.extend.FSCanvas;
import org.xhtmlrenderer.extend.NamespaceHandler;
import org.xhtmlrenderer.extend.UserInterface;
import org.xhtmlrenderer.layout.BoxBuilder;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.render.ViewportBox;
import org.xhtmlrenderer.swing.Java2DFontContext;
import org.xhtmlrenderer.swing.Java2DOutputDevice;
import org.xhtmlrenderer.swing.RepaintListener;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.XRLog;

public class RootPanel
extends JPanel
implements Scrollable,
UserInterface,
FSCanvas,
RepaintListener {
    static final long serialVersionUID = 1L;
    private Box rootBox = null;
    private boolean needRelayout = false;
    private CellRendererPane cellRendererPane;
    private final Set<DocumentListener> documentListeners = new HashSet<DocumentListener>();
    private boolean defaultFontFromComponent;
    protected SharedContext sharedContext;
    private volatile LayoutContext layoutContext;
    private JScrollPane enclosingScrollPane;
    private boolean viewportMatchWidth = true;
    private int default_scroll_mode = 1;
    protected Document doc = null;
    public Element hovered_element = null;
    public Element active_element = null;
    public Element focus_element = null;
    private long lastRepaintRunAt = System.currentTimeMillis();
    private final long maxRepaintRequestWaitMs = 50L;
    private boolean repaintRequestPending = false;
    private long pendingRepaintCount = 0L;

    public SharedContext getSharedContext() {
        return this.sharedContext;
    }

    public LayoutContext getLayoutContext() {
        return this.layoutContext;
    }

    public void setDocument(Document doc, String url, NamespaceHandler nsh) {
        this.fireDocumentStarted();
        this.resetScrollPosition();
        this.setRootBox(null);
        this.doc = doc;
        if (Configuration.isTrue("xr.cache.stylesheets", true)) {
            this.getSharedContext().getCss().flushStyleSheets();
        } else {
            this.getSharedContext().getCss().flushAllStyleSheets();
        }
        this.getSharedContext().reset();
        this.getSharedContext().setBaseURL(url);
        this.getSharedContext().setNamespaceHandler(nsh);
        this.getSharedContext().getCss().setDocumentContext(this.getSharedContext(), this.getSharedContext().getNamespaceHandler(), doc, this);
        this.repaint();
    }

    private void requestBGImages(Box box) {
        if (box.getChildCount() == 0) {
            return;
        }
        Iterator ci = box.getChildIterator();
        while (ci.hasNext()) {
            Box cb = (Box)ci.next();
            CalculatedStyle style = cb.getStyle();
            if (!style.isIdent(CSSName.BACKGROUND_IMAGE, IdentValue.NONE)) {
                String uri = style.getStringProperty(CSSName.BACKGROUND_IMAGE);
                XRLog.load(Level.FINE, "Greedily loading background property " + uri);
                try {
                    this.getSharedContext().getUac().getImageResource(uri);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            this.requestBGImages(cb);
        }
    }

    public void resetScrollPosition() {
        JScrollBar scrollBar;
        if (this.enclosingScrollPane != null && (scrollBar = this.enclosingScrollPane.getVerticalScrollBar()) != null) {
            scrollBar.setValue(0);
        }
    }

    protected void setEnclosingScrollPane(JScrollPane scrollPane) {
        JViewport viewPort;
        this.enclosingScrollPane = scrollPane;
        if (this.enclosingScrollPane != null && (viewPort = this.enclosingScrollPane.getViewport()) != null) {
            this.default_scroll_mode = viewPort.getScrollMode();
        }
    }

    protected JScrollPane getEnclosingScrollPane() {
        return this.enclosingScrollPane;
    }

    @Override
    public Rectangle getFixedRectangle() {
        if (this.enclosingScrollPane != null) {
            return this.enclosingScrollPane.getViewportBorderBounds();
        }
        Dimension dim = this.getSize();
        return new Rectangle(0, 0, dim.width, dim.height);
    }

    @Override
    public void addNotify() {
        Container vp;
        super.addNotify();
        XRLog.general(Level.FINE, "add notify called");
        Container p = this.getParent();
        if (p instanceof JViewport && (vp = p.getParent()) instanceof JScrollPane) {
            this.setEnclosingScrollPane((JScrollPane)vp);
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        this.setEnclosingScrollPane(null);
    }

    protected void init() {
        this.setBackground(Color.white);
        super.setLayout(null);
    }

    public RenderingContext newRenderingContext(Graphics2D g) {
        XRLog.layout(Level.FINEST, "new context begin");
        this.getSharedContext().setCanvas(this);
        XRLog.layout(Level.FINEST, "new context end");
        RenderingContext result = this.getSharedContext().newRenderingContextInstance();
        result.setFontContext(new Java2DFontContext(g));
        result.setOutputDevice(new Java2DOutputDevice(g));
        this.getSharedContext().getTextRenderer().setup(result.getFontContext());
        Box rb = this.getRootBox();
        if (rb != null) {
            result.setRootLayer(rb.getLayer());
        }
        return result;
    }

    protected LayoutContext newLayoutContext(Graphics2D g) {
        XRLog.layout(Level.FINEST, "new context begin");
        this.getSharedContext().setCanvas(this);
        XRLog.layout(Level.FINEST, "new context end");
        LayoutContext result = this.getSharedContext().newLayoutContextInstance();
        Graphics2D layoutGraphics = g.getDeviceConfiguration().createCompatibleImage(1, 1).createGraphics();
        result.setFontContext(new Java2DFontContext(layoutGraphics));
        this.getSharedContext().getTextRenderer().setup(result.getFontContext());
        return result;
    }

    private Rectangle getInitialExtents(LayoutContext c) {
        if (!c.isPrint()) {
            Rectangle extents = this.getScreenExtents();
            if (extents.width == 0 && extents.height == 0) {
                extents = new Rectangle(0, 0, 1, 1);
            }
            return extents;
        }
        PageBox first = Layer.createPageBox(c, "first");
        return new Rectangle(0, 0, first.getContentWidth(c), first.getContentHeight(c));
    }

    public Rectangle getScreenExtents() {
        Rectangle extents;
        if (this.enclosingScrollPane != null) {
            Rectangle bnds = this.enclosingScrollPane.getViewportBorderBounds();
            extents = new Rectangle(0, 0, bnds.width, bnds.height);
        } else {
            extents = new Rectangle(this.getWidth(), this.getHeight());
            Insets insets = this.getInsets();
            extents.width -= insets.left + insets.right;
            extents.height -= insets.top + insets.bottom;
        }
        return extents;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doDocumentLayout(Graphics g) {
        try {
            JViewport viewPort;
            this.removeAll();
            if (g == null) {
                return;
            }
            if (this.doc == null) {
                return;
            }
            LayoutContext c = this.newLayoutContext((Graphics2D)g);
            RootPanel rootPanel = this;
            synchronized (rootPanel) {
                this.layoutContext = c;
            }
            long start = System.currentTimeMillis();
            BlockBox root = (BlockBox)this.getRootBox();
            if (root != null && this.isNeedRelayout()) {
                root.reset(c);
            } else {
                root = BoxBuilder.createRootBox(c, this.doc);
                this.setRootBox(root);
            }
            this.initFontFromComponent(root);
            Rectangle initialExtents = this.getInitialExtents(c);
            root.setContainingBlock(new ViewportBox(initialExtents));
            root.layout(c);
            long end = System.currentTimeMillis();
            XRLog.layout(Level.INFO, "Layout took " + (end - start) + "ms");
            if (root.getLayer().containsFixedContent()) {
                super.setOpaque(false);
            } else {
                super.setOpaque(true);
            }
            XRLog.layout(Level.FINEST, "after layout: " + root);
            Dimension intrinsic_size = root.getLayer().getPaintingDimension(c);
            if (c.isPrint()) {
                root.getLayer().trimEmptyPages(c, intrinsic_size.height);
                root.getLayer().layoutPages(c);
            }
            this.viewportMatchWidth = initialExtents.width == intrinsic_size.width;
            this.setPreferredSize(intrinsic_size);
            this.revalidate();
            if (this.enclosingScrollPane != null && (viewPort = this.enclosingScrollPane.getViewport()) != null) {
                if (root.getLayer().containsFixedContent()) {
                    viewPort.setScrollMode(0);
                } else {
                    viewPort.setScrollMode(this.default_scroll_mode);
                }
            }
            this.fireDocumentLoaded();
        }
        catch (ThreadDeath t) {
            throw t;
        }
        catch (Throwable t) {
            if (this.hasDocumentListeners()) {
                this.fireOnLayoutException(t);
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            XRLog.exception(t.getMessage(), t);
        }
    }

    private void initFontFromComponent(BlockBox root) {
        if (this.isDefaultFontFromComponent()) {
            CalculatedStyle style = root.getStyle();
            PropertyValue fontFamilyProp = new PropertyValue(19, this.getFont().getFamily(), this.getFont().getFamily());
            fontFamilyProp.setStringArrayValue(new String[]{fontFamilyProp.getStringValue()});
            style.setDefaultValue(CSSName.FONT_FAMILY, new StringValue(CSSName.FONT_FAMILY, fontFamilyProp));
            style.setDefaultValue(CSSName.FONT_SIZE, new LengthValue(style, CSSName.FONT_SIZE, new PropertyValue(5, this.getFont().getSize(), Integer.toString(this.getFont().getSize()))));
            Color c = this.getForeground();
            style.setDefaultValue(CSSName.COLOR, new ColorValue(CSSName.COLOR, new PropertyValue(new FSRGBColor(c.getRed(), c.getGreen(), c.getBlue()))));
            if (this.getFont().isBold()) {
                style.setDefaultValue(CSSName.FONT_WEIGHT, IdentValue.BOLD);
            }
            if (this.getFont().isItalic()) {
                style.setDefaultValue(CSSName.FONT_STYLE, IdentValue.ITALIC);
            }
        }
    }

    public void addDocumentListener(DocumentListener listener) {
        if (listener == null) {
            return;
        }
        this.documentListeners.add(listener);
    }

    public void removeDocumentListener(DocumentListener listener) {
        if (listener == null) {
            return;
        }
        this.documentListeners.remove(listener);
    }

    protected boolean hasDocumentListeners() {
        return !this.documentListeners.isEmpty();
    }

    protected void fireDocumentStarted() {
        for (DocumentListener list : this.documentListeners) {
            try {
                list.documentStarted();
            }
            catch (Exception e) {
                XRLog.load(Level.WARNING, "Document listener threw an exception; continuing processing", e);
            }
        }
    }

    protected void fireDocumentLoaded() {
        for (DocumentListener list : this.documentListeners) {
            try {
                list.documentLoaded();
            }
            catch (Exception e) {
                XRLog.load(Level.WARNING, "Document listener threw an exception; continuing processing", e);
            }
        }
    }

    protected void fireOnLayoutException(Throwable t) {
        for (DocumentListener list : this.documentListeners) {
            try {
                list.onLayoutException(t);
            }
            catch (Exception e) {
                XRLog.load(Level.WARNING, "Document listener threw an exception; continuing processing", e);
            }
        }
    }

    protected void fireOnRenderException(Throwable t) {
        for (DocumentListener list : this.documentListeners) {
            try {
                list.onRenderException(t);
            }
            catch (Exception e) {
                XRLog.load(Level.WARNING, "Document listener threw an exception; continuing processing", e);
            }
        }
    }

    public CellRendererPane getCellRendererPane() {
        if (this.cellRendererPane == null || this.cellRendererPane.getParent() != this) {
            this.cellRendererPane = new CellRendererPane();
            this.add(this.cellRendererPane);
        }
        return this.cellRendererPane;
    }

    @Override
    public boolean isHover(Element e) {
        return e == this.hovered_element;
    }

    @Override
    public boolean isActive(Element e) {
        return e == this.active_element;
    }

    @Override
    public boolean isFocus(Element e) {
        return e == this.focus_element;
    }

    protected void relayout() {
        if (this.doc != null) {
            this.setNeedRelayout(true);
            this.repaint();
        }
    }

    public double getLayoutWidth() {
        if (this.enclosingScrollPane != null) {
            return this.enclosingScrollPane.getViewportBorderBounds().width;
        }
        return this.getSize().width;
    }

    public boolean isPrintView() {
        return false;
    }

    public synchronized Box getRootBox() {
        return this.rootBox;
    }

    public synchronized void setRootBox(Box rootBox) {
        this.rootBox = rootBox;
    }

    public synchronized Layer getRootLayer() {
        return this.getRootBox() == null ? null : this.getRootBox().getLayer();
    }

    public Box find(MouseEvent e) {
        return this.find(e.getX(), e.getY());
    }

    public Box find(int x, int y) {
        Layer l = this.getRootLayer();
        if (l != null) {
            return l.find(this.layoutContext, x, y, false);
        }
        return null;
    }

    @Override
    public void doLayout() {
        if (this.isExtentsHaveChanged()) {
            this.setNeedRelayout(true);
        }
        super.doLayout();
    }

    @Override
    public void validate() {
        super.validate();
        if (this.isExtentsHaveChanged()) {
            this.setNeedRelayout(true);
        }
    }

    protected boolean isExtentsHaveChanged() {
        if (this.rootBox == null) {
            return true;
        }
        Rectangle oldExtents = ((ViewportBox)this.rootBox.getContainingBlock()).getExtents();
        return !oldExtents.equals(this.getScreenExtents());
    }

    protected synchronized boolean isNeedRelayout() {
        return this.needRelayout;
    }

    protected synchronized void setNeedRelayout(boolean needRelayout) {
        this.needRelayout = needRelayout;
    }

    @Override
    public void repaintRequested(final boolean doLayout) {
        long now = System.currentTimeMillis();
        final long el = now - this.lastRepaintRunAt;
        if (!doLayout || el > 50L || this.pendingRepaintCount > 5L) {
            XRLog.general(Level.FINE, "*** Repainting panel, by request, el: " + el + " pending " + this.pendingRepaintCount);
            if (doLayout) {
                this.relayout();
            } else {
                this.repaint();
            }
            this.lastRepaintRunAt = System.currentTimeMillis();
            this.repaintRequestPending = false;
            this.pendingRepaintCount = 0L;
        } else if (!this.repaintRequestPending) {
            XRLog.general(Level.FINE, "... Queueing new repaint request, el: " + el + " < " + 50L);
            this.repaintRequestPending = true;
            new Thread(new Runnable(){

                @Override
                public void run() {
                    try {
                        Thread.currentThread();
                        Thread.sleep(Math.min(50L, Math.abs(50L - el)));
                        EventQueue.invokeLater(new Runnable(){

                            @Override
                            public void run() {
                                XRLog.general(Level.FINE, "--> running queued repaint request");
                                RootPanel.this.repaintRequested(doLayout);
                                RootPanel.this.repaintRequestPending = false;
                            }
                        });
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
            }).start();
        } else {
            ++this.pendingRepaintCount;
            XRLog.general("hmm... repaint request, but already have one");
        }
    }

    public boolean isDefaultFontFromComponent() {
        return this.defaultFontFromComponent;
    }

    public void setDefaultFontFromComponent(boolean defaultFontFromComponent) {
        this.defaultFontFromComponent = defaultFontFromComponent;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return this.getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        int dif = 1;
        if (orientation == 1) {
            dif = visibleRect.height;
        } else if (orientation == 0) {
            dif = visibleRect.width;
        }
        return Math.min(35, dif);
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        int dif = 1;
        if (orientation == 1) {
            dif = Math.max(visibleRect.height - 10, dif);
        } else if (orientation == 0) {
            dif = Math.max(visibleRect.width, dif);
        }
        return dif;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return this.viewportMatchWidth;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        JViewport viewPort;
        if (this.enclosingScrollPane != null && (viewPort = this.enclosingScrollPane.getViewport()) != null) {
            return this.getPreferredSize().height <= viewPort.getHeight();
        }
        return false;
    }
}

