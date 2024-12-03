/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.NamespaceHandler;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.extend.UserInterface;
import org.xhtmlrenderer.layout.BoxBuilder;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.render.ViewportBox;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import org.xhtmlrenderer.swing.AWTFontResolver;
import org.xhtmlrenderer.swing.Java2DFontContext;
import org.xhtmlrenderer.swing.Java2DOutputDevice;
import org.xhtmlrenderer.swing.Java2DTextRenderer;
import org.xhtmlrenderer.swing.NaiveUserAgent;
import org.xhtmlrenderer.swing.SwingReplacedElementFactory;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.ImageUtil;

public class BoxRenderer {
    private static final int DEFAULT_HEIGHT = 1000;
    private static final int DEFAULT_DOTS_PER_POINT = 1;
    private static final int DEFAULT_DOTS_PER_PIXEL = 1;
    private SharedContext sharedContext;
    private Java2DOutputDevice outputDevice;
    private Document doc;
    private Box root;
    private float dotsPerPoint;
    private boolean rendered;
    private String sourceDocument;
    private String sourceDocumentBase;
    private int width;
    private int height;
    private static final int NO_HEIGHT = -1;
    private Map renderingHints;
    private LayoutContext layoutContext;

    private BoxRenderer() {
    }

    private BoxRenderer(float dotsPerPoint, int dotsPerPixel) {
        this();
        this.init(dotsPerPoint, dotsPerPixel);
    }

    public BoxRenderer(String url, String baseUrl, int width, int height) {
        this(1.0f, 1);
        this.sourceDocument = url;
        this.sourceDocumentBase = baseUrl;
        this.width = width;
        this.height = height;
    }

    public BoxRenderer(File file, int width, int height) throws IOException {
        this(file.toURI().toURL().toExternalForm(), width, height);
    }

    public BoxRenderer(File file, int width) throws IOException {
        this(file.toURI().toURL().toExternalForm(), width);
    }

    public BoxRenderer(String url, int width) {
        this(url, url, width, -1);
    }

    public BoxRenderer(String url, String baseurl, int width) {
        this(url, baseurl, width, -1);
    }

    public BoxRenderer(String url, int width, int height) {
        this(url, url, width, height);
    }

    public void setRenderingHints(Map hints) {
        this.renderingHints = hints;
    }

    public SharedContext getSharedContext() {
        return this.sharedContext;
    }

    public LayoutContext getLayoutContext() {
        return this.layoutContext;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Box render() {
        if (!this.rendered) {
            this.setDocument(this.loadDocument(this.sourceDocument), this.sourceDocumentBase, new XhtmlNamespaceHandler());
            this.layout(this.width);
            this.height = this.height == -1 ? this.root.getHeight() : this.height;
            BufferedImage outputImage = this.createBufferedImage(this.width, this.height);
            this.outputDevice = new Java2DOutputDevice(outputImage);
            Graphics2D newG = (Graphics2D)outputImage.getGraphics();
            try {
                if (this.renderingHints != null) {
                    newG.getRenderingHints().putAll((Map<?, ?>)this.renderingHints);
                }
                RenderingContext rc = this.sharedContext.newRenderingContextInstance();
                rc.setFontContext(new Java2DFontContext(newG));
                rc.setOutputDevice(this.outputDevice);
                this.sharedContext.getTextRenderer().setup(rc.getFontContext());
                this.root.getLayer().paint(rc);
            }
            finally {
                if (newG != null) {
                    newG.dispose();
                }
            }
            this.rendered = true;
        }
        return this.root;
    }

    protected BufferedImage createBufferedImage(int width, int height) {
        BufferedImage image = ImageUtil.createCompatibleBufferedImage(width, height, 1);
        ImageUtil.clearImage(image);
        return image;
    }

    private void setDocument(Document doc, String url, NamespaceHandler nsh) {
        this.doc = doc;
        this.sharedContext.reset();
        if (Configuration.isTrue("xr.cache.stylesheets", true)) {
            this.sharedContext.getCss().flushStyleSheets();
        } else {
            this.sharedContext.getCss().flushAllStyleSheets();
        }
        this.sharedContext.setBaseURL(url);
        this.sharedContext.setNamespaceHandler(nsh);
        this.sharedContext.getCss().setDocumentContext(this.sharedContext, this.sharedContext.getNamespaceHandler(), doc, new NullUserInterface());
    }

    private void layout(int width) {
        Rectangle rect = new Rectangle(0, 0, width, 1000);
        this.sharedContext.set_TempCanvas(rect);
        BlockBox root = BoxBuilder.createRootBox(this.layoutContext, this.doc);
        root.setContainingBlock(new ViewportBox(rect));
        root.layout(this.layoutContext);
        this.root = root;
    }

    private Document loadDocument(String uri) {
        return this.sharedContext.getUac().getXMLResource(uri).getDocument();
    }

    private LayoutContext newLayoutContext() {
        LayoutContext result = this.sharedContext.newLayoutContextInstance();
        result.setFontContext(new Java2DFontContext(this.outputDevice.getGraphics()));
        this.sharedContext.getTextRenderer().setup(result.getFontContext());
        return result;
    }

    private void init(float dotsPerPoint, int dotsPerPixel) {
        this.dotsPerPoint = dotsPerPoint;
        BufferedImage outputImage = ImageUtil.createCompatibleBufferedImage(1, 1);
        this.outputDevice = new Java2DOutputDevice(outputImage);
        NaiveUserAgent userAgent = new NaiveUserAgent();
        this.sharedContext = this.newSharedContext(dotsPerPixel, userAgent);
        this.layoutContext = this.newLayoutContext();
    }

    private SharedContext newSharedContext(int dotsPerPixel, UserAgentCallback userAgent) {
        SharedContext context = new SharedContext(userAgent);
        AWTFontResolver fontResolver = new AWTFontResolver();
        context.setFontResolver(fontResolver);
        SwingReplacedElementFactory replacedElementFactory = new SwingReplacedElementFactory();
        context.setReplacedElementFactory(replacedElementFactory);
        context.setTextRenderer(new Java2DTextRenderer());
        context.setDPI(72.0f * this.dotsPerPoint);
        context.setDotsPerPixel(dotsPerPixel);
        context.setPrint(false);
        context.setInteractive(false);
        return context;
    }

    private static final class NullUserInterface
    implements UserInterface {
        private NullUserInterface() {
        }

        @Override
        public boolean isHover(Element e) {
            return false;
        }

        @Override
        public boolean isActive(Element e) {
            return false;
        }

        @Override
        public boolean isFocus(Element e) {
            return false;
        }
    }
}

