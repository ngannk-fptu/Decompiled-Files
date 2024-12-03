/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.print.PrinterGraphics;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import org.w3c.dom.Document;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.extend.NamespaceHandler;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.simple.NoNamespaceHandler;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.xhtmlrenderer.swing.FSMouseListener;
import org.xhtmlrenderer.swing.Java2DOutputDevice;
import org.xhtmlrenderer.swing.MouseTracker;
import org.xhtmlrenderer.swing.NaiveUserAgent;
import org.xhtmlrenderer.swing.RootPanel;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.Uu;
import org.xhtmlrenderer.util.XRLog;
import org.xml.sax.InputSource;

public abstract class BasicPanel
extends RootPanel
implements FormSubmissionListener {
    private static final int PAGE_PAINTING_CLEARANCE_WIDTH = 10;
    private static final int PAGE_PAINTING_CLEARANCE_HEIGHT = 10;
    private boolean explicitlyOpaque;
    private final MouseTracker mouseTracker;
    private boolean centeredPagedView;
    protected FormSubmissionListener formSubmissionListener;

    public BasicPanel() {
        this(new NaiveUserAgent());
    }

    public BasicPanel(UserAgentCallback uac) {
        this.sharedContext = new SharedContext(uac);
        this.mouseTracker = new MouseTracker(this);
        this.formSubmissionListener = new FormSubmissionListener(){

            @Override
            public void submit(String query) {
                System.out.println("Form Submitted!");
                System.out.println("Data: " + query);
                JOptionPane.showMessageDialog(null, "Form submit called; check console to see the query string that would have been submitted.", "Form Submission", 1);
            }
        };
        this.sharedContext.setFormSubmissionListener(this.formSubmissionListener);
        this.init();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics gg;
        if (this.doc == null) {
            this.paintDefaultBackground(g);
            return;
        }
        Layer root = this.getRootLayer();
        if (root == null || this.isNeedRelayout()) {
            gg = g.create();
            try {
                this.doDocumentLayout(gg);
            }
            finally {
                gg.dispose();
            }
            root = this.getRootLayer();
        }
        this.setNeedRelayout(false);
        if (root == null) {
            XRLog.render(Level.FINE, "skipping the actual painting");
        } else {
            gg = g.create();
            try {
                RenderingContext c = this.newRenderingContext((Graphics2D)gg);
                long start = System.currentTimeMillis();
                this.doRender(c, root);
                long end = System.currentTimeMillis();
                XRLog.render(Level.FINE, "RENDERING TOOK " + (end - start) + " ms");
            }
            finally {
                gg.dispose();
            }
        }
    }

    protected void doRender(RenderingContext c, Layer root) {
        try {
            Graphics2D g = ((Java2DOutputDevice)c.getOutputDevice()).getGraphics();
            this.paintDefaultBackground(g);
            JScrollPane scrollPane = this.getEnclosingScrollPane();
            if (scrollPane == null) {
                Insets insets = this.getInsets();
                ((Graphics)g).translate(insets.left, insets.top);
            }
            long start = System.currentTimeMillis();
            if (!c.isPrint()) {
                root.paint(c);
            } else {
                this.paintPagedView(c, root);
            }
            long after = System.currentTimeMillis();
            if (Configuration.isTrue("xr.incremental.repaint.print-timing", false)) {
                Uu.p("repaint took ms: " + (after - start));
            }
        }
        catch (ThreadDeath t) {
            throw t;
        }
        catch (Throwable t) {
            if (this.hasDocumentListeners()) {
                this.fireOnRenderException(t);
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

    private void paintDefaultBackground(Graphics g) {
        if (!(g instanceof PrinterGraphics) && this.explicitlyOpaque) {
            g.setColor(this.getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }

    private void paintPagedView(RenderingContext c, Layer root) {
        if (root.getLastPage() == null) {
            return;
        }
        int pagePaintingClearanceWidth = this.isCenteredPagedView() ? this.calcCenteredPageLeftOffset(root.getMaxPageWidth(c, 0)) : 10;
        root.assignPagePaintingPositions(c, 1, 10);
        this.setPreferredSize(new Dimension(root.getMaxPageWidth(c, pagePaintingClearanceWidth), root.getLastPage().getPaintingBottom() + 10));
        this.revalidate();
        Graphics2D g = ((Java2DOutputDevice)c.getOutputDevice()).getGraphics();
        Shape working = g.getClip();
        List pages = root.getPages();
        c.setPageCount(pages.size());
        for (int i = 0; i < pages.size(); ++i) {
            PageBox page = (PageBox)pages.get(i);
            c.setPage(i, page);
            g.setClip(working);
            Rectangle overall = page.getScreenPaintingBounds(c, pagePaintingClearanceWidth);
            --overall.x;
            --overall.y;
            ++overall.width;
            ++overall.height;
            Rectangle bounds = new Rectangle(overall);
            ++bounds.width;
            ++bounds.height;
            if (!working.intersects(bounds)) continue;
            page.paintBackground(c, pagePaintingClearanceWidth, (short)1);
            page.paintMarginAreas(c, pagePaintingClearanceWidth, (short)1);
            page.paintBorder(c, pagePaintingClearanceWidth, (short)1);
            Color old = g.getColor();
            g.setColor(Color.BLACK);
            g.drawRect(overall.x, overall.y, overall.width, overall.height);
            g.setColor(old);
            Rectangle content = page.getPagedViewClippingBounds(c, pagePaintingClearanceWidth);
            g.clip(content);
            int left = pagePaintingClearanceWidth + page.getMarginBorderPadding(c, 1);
            int top = page.getPaintingTop() + page.getMarginBorderPadding(c, 3) - page.getTop();
            g.translate(left, top);
            root.paint(c);
            g.translate(-left, -top);
            g.setClip(working);
        }
        g.setClip(working);
    }

    private int calcCenteredPageLeftOffset(int maxPageWidth) {
        return (this.getWidth() - maxPageWidth) / 2;
    }

    public void paintPage(Graphics2D g, int pageNo) {
        Layer root = this.getRootLayer();
        if (root == null) {
            throw new RuntimeException("Document needs layout");
        }
        if (pageNo < 0 || pageNo >= root.getPages().size()) {
            throw new IllegalArgumentException("Page " + pageNo + " is not between 0 and " + root.getPages().size());
        }
        RenderingContext c = this.newRenderingContext(g);
        PageBox page = (PageBox)root.getPages().get(pageNo);
        c.setPageCount(root.getPages().size());
        c.setPage(pageNo, page);
        page.paintBackground(c, 0, (short)2);
        page.paintMarginAreas(c, 0, (short)2);
        page.paintBorder(c, 0, (short)2);
        Shape working = g.getClip();
        Rectangle content = page.getPrintClippingBounds(c);
        g.clip(content);
        int top = -page.getPaintingTop() + page.getMarginBorderPadding(c, 3);
        int left = page.getMarginBorderPadding(c, 1);
        g.translate(left, top);
        root.paint(c);
        g.translate(-left, -top);
        g.setClip(working);
    }

    public void assignPagePrintPositions(Graphics2D g) {
        RenderingContext c = this.newRenderingContext(g);
        this.getRootLayer().assignPagePaintingPositions(c, (short)2);
    }

    public void printTree() {
        this.printTree(this.getRootBox(), "");
    }

    private void printTree(Box box, String tab) {
        XRLog.layout(Level.FINEST, tab + "Box = " + box);
        Iterator it = box.getChildIterator();
        while (it.hasNext()) {
            Box bx = (Box)it.next();
            this.printTree(bx, tab + " ");
        }
    }

    @Override
    public void setLayout(LayoutManager l) {
    }

    public void setSharedContext(SharedContext ctx) {
        this.sharedContext = ctx;
    }

    @Override
    public void setSize(Dimension d) {
        XRLog.layout(Level.FINEST, "set size called");
        super.setSize(d);
    }

    public void setDocument(InputStream stream, String url, NamespaceHandler nsh) {
        Document dom = XMLResource.load(stream).getDocument();
        this.setDocument(dom, url, nsh);
    }

    public void setDocumentFromString(String content, String url, NamespaceHandler nsh) {
        InputSource is = new InputSource(new BufferedReader(new StringReader(content)));
        Document dom = XMLResource.load(is).getDocument();
        this.setDocument(dom, url, nsh);
    }

    public void setDocument(Document doc, String url) {
        this.setDocument(doc, url, (NamespaceHandler)new NoNamespaceHandler());
    }

    public void setDocument(String url) {
        this.setDocument(this.loadDocument(url), url, (NamespaceHandler)new NoNamespaceHandler());
    }

    public void setDocument(String url, NamespaceHandler nsh) {
        this.setDocument(this.loadDocument(url), url, nsh);
    }

    protected void setDocument(InputStream stream, String url) throws Exception {
        this.setDocument(stream, url, (NamespaceHandler)new NoNamespaceHandler());
    }

    protected void setDocumentRelative(String filename) {
        String url = this.getSharedContext().getUac().resolveURI(filename);
        if (this.isAnchorInCurrentDocument(filename)) {
            String id = this.getAnchorId(filename);
            Box box = this.getSharedContext().getBoxById(id);
            if (box != null) {
                Point pt;
                if (box.getStyle().isInline()) {
                    pt = new Point(box.getAbsX(), box.getAbsY());
                } else {
                    RectPropertySet margin = box.getMargin(this.getLayoutContext());
                    pt = new Point(box.getAbsX() + (int)margin.left(), box.getAbsY() + (int)margin.top());
                }
                this.scrollTo(pt);
                return;
            }
        }
        Document dom = this.loadDocument(url);
        this.setDocument(dom, url);
    }

    public void reloadDocument(String URI2) {
        this.reloadDocument(this.loadDocument(URI2));
    }

    public void reloadDocument(Document doc) {
        if (this.doc == null) {
            XRLog.render("Reload called on BasicPanel, but there is no document set on the panel yet.");
            return;
        }
        this.doc = doc;
        this.setDocument(this.doc, this.getSharedContext().getBaseURL(), this.getSharedContext().getNamespaceHandler());
    }

    public URL getURL() {
        URL base = null;
        try {
            base = new URL(this.getSharedContext().getUac().getBaseURL());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return base;
    }

    public Document getDocument() {
        return this.doc;
    }

    public String getDocumentTitle() {
        return this.doc == null ? "" : this.getSharedContext().getNamespaceHandler().getDocumentTitle(this.doc);
    }

    protected Document loadDocument(String uri) {
        XMLResource xmlResource = this.sharedContext.getUac().getXMLResource(uri);
        return xmlResource.getDocument();
    }

    @Override
    public boolean isOpaque() {
        this.checkOpacityMethodClient();
        return this.explicitlyOpaque;
    }

    @Override
    public void setOpaque(boolean opaque) {
        this.checkOpacityMethodClient();
        this.explicitlyOpaque = opaque;
    }

    private void checkOpacityMethodClient() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length > 2) {
            String callingClassName = stackTrace[2].getClassName();
            if (BasicPanel.class.getName().equals(callingClassName)) {
                throw new IllegalStateException("BasicPanel should not use its own opacity methods. Use super.isOpaque()/setOpaque() instead.");
            }
        }
    }

    @Override
    public SharedContext getSharedContext() {
        return this.sharedContext;
    }

    private boolean isAnchorInCurrentDocument(String str) {
        return str.charAt(0) == '#';
    }

    private String getAnchorId(String url) {
        return url.substring(1, url.length());
    }

    public void scrollTo(Point pt) {
        JScrollBar scrollBar;
        JScrollPane scrollPane = this.getEnclosingScrollPane();
        if (scrollPane != null && (scrollBar = scrollPane.getVerticalScrollBar()) != null) {
            scrollBar.setValue(pt.y);
        }
    }

    public boolean isInteractive() {
        return this.getSharedContext().isInteractive();
    }

    public void setInteractive(boolean interactive) {
        this.getSharedContext().setInteractive(interactive);
    }

    public void addMouseTrackingListener(FSMouseListener l) {
        this.mouseTracker.addListener(l);
    }

    public void removeMouseTrackingListener(FSMouseListener l) {
        this.mouseTracker.removeListener(l);
    }

    public List getMouseTrackingListeners() {
        return this.mouseTracker.getListeners();
    }

    protected void resetMouseTracker() {
        this.mouseTracker.reset();
    }

    public boolean isCenteredPagedView() {
        return this.centeredPagedView;
    }

    public void setCenteredPagedView(boolean centeredPagedView) {
        this.centeredPagedView = centeredPagedView;
    }

    @Override
    public void submit(String url) {
        this.formSubmissionListener.submit(url);
    }

    public void setFormSubmissionListener(FormSubmissionListener fsl) {
        this.formSubmissionListener = fsl;
        this.sharedContext.setFormSubmissionListener(this.formSubmissionListener);
    }
}

