/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.context.AWTFontResolver;
import org.xhtmlrenderer.context.StyleReference;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.EmptyStyle;
import org.xhtmlrenderer.css.value.FontSpecification;
import org.xhtmlrenderer.extend.FSCanvas;
import org.xhtmlrenderer.extend.FontContext;
import org.xhtmlrenderer.extend.FontResolver;
import org.xhtmlrenderer.extend.NamespaceHandler;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.TextRenderer;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.breaker.DefaultLineBreakingStrategy;
import org.xhtmlrenderer.layout.breaker.LineBreakingStrategy;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.FSFontMetrics;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.xhtmlrenderer.swing.Java2DTextRenderer;
import org.xhtmlrenderer.swing.SwingReplacedElementFactory;
import org.xhtmlrenderer.util.XRLog;

public class SharedContext {
    private TextRenderer text_renderer;
    private String media;
    private UserAgentCallback uac;
    private boolean interactive = true;
    private Map idMap;
    private float dpi;
    private static final int MM__PER__CM = 10;
    private static final float CM__PER__IN = 2.54f;
    private float mm_per_dot;
    private static final float DEFAULT_DPI = 72.0f;
    private boolean print;
    private int dotsPerPixel = 1;
    private Map styleMap;
    private ReplacedElementFactory replacedElementFactory;
    private Rectangle temp_canvas;
    private LineBreakingStrategy lineBreakingStrategy = new DefaultLineBreakingStrategy();
    protected FontResolver font_resolver;
    protected StyleReference css;
    protected boolean debug_draw_boxes;
    protected boolean debug_draw_line_boxes;
    protected boolean debug_draw_inline_boxes;
    protected boolean debug_draw_font_metrics;
    protected FSCanvas canvas;
    protected Box selection_start;
    protected Box selection_end;
    protected int selection_end_x;
    protected int selection_start_x;
    protected boolean in_selection = false;
    private NamespaceHandler namespaceHandler;

    public SharedContext() {
    }

    public SharedContext(UserAgentCallback uac) {
        this.font_resolver = new AWTFontResolver();
        this.replacedElementFactory = new SwingReplacedElementFactory();
        this.setMedia("screen");
        this.uac = uac;
        this.setCss(new StyleReference(uac));
        XRLog.render("Using CSS implementation from: " + this.getCss().getClass().getName());
        this.setTextRenderer(new Java2DTextRenderer());
        try {
            this.setDPI(Toolkit.getDefaultToolkit().getScreenResolution());
        }
        catch (HeadlessException e) {
            this.setDPI(72.0f);
        }
    }

    public SharedContext(UserAgentCallback uac, FontResolver fr, ReplacedElementFactory ref, TextRenderer tr, float dpi) {
        this.font_resolver = fr;
        this.replacedElementFactory = ref;
        this.setMedia("screen");
        this.uac = uac;
        this.setCss(new StyleReference(uac));
        XRLog.render("Using CSS implementation from: " + this.getCss().getClass().getName());
        this.setTextRenderer(tr);
        this.setDPI(dpi);
    }

    public void setFormSubmissionListener(FormSubmissionListener fsl) {
        this.replacedElementFactory.setFormSubmissionListener(fsl);
    }

    public LayoutContext newLayoutContextInstance() {
        LayoutContext c = new LayoutContext(this);
        return c;
    }

    public RenderingContext newRenderingContextInstance() {
        RenderingContext c = new RenderingContext(this);
        return c;
    }

    public FontResolver getFontResolver() {
        return this.font_resolver;
    }

    public void flushFonts() {
        this.font_resolver.flushCache();
    }

    public String getMedia() {
        return this.media;
    }

    public TextRenderer getTextRenderer() {
        return this.text_renderer;
    }

    public boolean debugDrawBoxes() {
        return this.debug_draw_boxes;
    }

    public boolean debugDrawLineBoxes() {
        return this.debug_draw_line_boxes;
    }

    public boolean debugDrawInlineBoxes() {
        return this.debug_draw_inline_boxes;
    }

    public boolean debugDrawFontMetrics() {
        return this.debug_draw_font_metrics;
    }

    public void setDebug_draw_boxes(boolean debug_draw_boxes) {
        this.debug_draw_boxes = debug_draw_boxes;
    }

    public void setDebug_draw_line_boxes(boolean debug_draw_line_boxes) {
        this.debug_draw_line_boxes = debug_draw_line_boxes;
    }

    public void setDebug_draw_inline_boxes(boolean debug_draw_inline_boxes) {
        this.debug_draw_inline_boxes = debug_draw_inline_boxes;
    }

    public void setDebug_draw_font_metrics(boolean debug_draw_font_metrics) {
        this.debug_draw_font_metrics = debug_draw_font_metrics;
    }

    public StyleReference getCss() {
        return this.css;
    }

    public void setCss(StyleReference css) {
        this.css = css;
    }

    public FSCanvas getCanvas() {
        return this.canvas;
    }

    public void setCanvas(FSCanvas canvas) {
        this.canvas = canvas;
    }

    public void set_TempCanvas(Rectangle rect) {
        this.temp_canvas = rect;
    }

    public Rectangle getFixedRectangle() {
        if (this.getCanvas() == null) {
            return this.temp_canvas;
        }
        Rectangle rect = this.getCanvas().getFixedRectangle();
        rect.translate(this.getCanvas().getX(), this.getCanvas().getY());
        return rect;
    }

    public void setNamespaceHandler(NamespaceHandler nh) {
        this.namespaceHandler = nh;
    }

    public NamespaceHandler getNamespaceHandler() {
        return this.namespaceHandler;
    }

    public void addBoxId(String id, Box box) {
        if (this.idMap == null) {
            this.idMap = new HashMap();
        }
        this.idMap.put(id, box);
    }

    public Box getBoxById(String id) {
        if (this.idMap == null) {
            this.idMap = new HashMap();
        }
        return (Box)this.idMap.get(id);
    }

    public void removeBoxId(String id) {
        if (this.idMap != null) {
            this.idMap.remove(id);
        }
    }

    public Map getIdMap() {
        return this.idMap;
    }

    public void setTextRenderer(TextRenderer text_renderer) {
        this.text_renderer = text_renderer;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public UserAgentCallback getUac() {
        return this.uac;
    }

    public UserAgentCallback getUserAgentCallback() {
        return this.uac;
    }

    public void setUserAgentCallback(UserAgentCallback userAgentCallback) {
        StyleReference styleReference = this.getCss();
        if (styleReference != null) {
            styleReference.setUserAgentCallback(userAgentCallback);
        }
        this.uac = userAgentCallback;
    }

    public float getDPI() {
        return this.dpi;
    }

    public void setDPI(float dpi) {
        this.dpi = dpi;
        this.mm_per_dot = 25.4f / dpi;
    }

    public float getMmPerPx() {
        return this.mm_per_dot;
    }

    public FSFont getFont(FontSpecification spec) {
        return this.getFontResolver().resolveFont(this, spec);
    }

    public float getXHeight(FontContext fontContext, FontSpecification fs) {
        FSFont font = this.getFontResolver().resolveFont(this, fs);
        FSFontMetrics fm = this.getTextRenderer().getFSFontMetrics(fontContext, font, " ");
        float sto = fm.getStrikethroughOffset();
        return fm.getAscent() - 2.0f * Math.abs(sto) + fm.getStrikethroughThickness();
    }

    public String getBaseURL() {
        return this.uac.getBaseURL();
    }

    public void setBaseURL(String url) {
        this.uac.setBaseURL(url);
    }

    public boolean isPaged() {
        if (this.media.equals("print")) {
            return true;
        }
        if (this.media.equals("projection")) {
            return true;
        }
        if (this.media.equals("embossed")) {
            return true;
        }
        if (this.media.equals("handheld")) {
            return true;
        }
        return this.media.equals("tv");
    }

    public boolean isInteractive() {
        return this.interactive;
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    public boolean isPrint() {
        return this.print;
    }

    public void setPrint(boolean print) {
        this.print = print;
        if (print) {
            this.setMedia("print");
        } else {
            this.setMedia("screen");
        }
    }

    public void setFontMapping(String name, Font font) {
        FontResolver resolver = this.getFontResolver();
        if (resolver instanceof AWTFontResolver) {
            ((AWTFontResolver)resolver).setFontMapping(name, font);
        }
    }

    public void setFontResolver(FontResolver resolver) {
        this.font_resolver = resolver;
    }

    public int getDotsPerPixel() {
        return this.dotsPerPixel;
    }

    public void setDotsPerPixel(int pixelsPerDot) {
        this.dotsPerPixel = pixelsPerDot;
    }

    public CalculatedStyle getStyle(Element e) {
        return this.getStyle(e, false);
    }

    public CalculatedStyle getStyle(Element e, boolean restyle) {
        if (this.styleMap == null) {
            this.styleMap = new HashMap(1024, 0.75f);
        }
        CalculatedStyle result = null;
        if (!restyle) {
            result = (CalculatedStyle)this.styleMap.get(e);
        }
        if (result == null) {
            Node parent = e.getParentNode();
            CalculatedStyle parentCalculatedStyle = parent instanceof Document ? new EmptyStyle() : this.getStyle((Element)parent, false);
            result = parentCalculatedStyle.deriveStyle(this.getCss().getCascadedStyle(e, restyle));
            this.styleMap.put(e, result);
        }
        return result;
    }

    public void reset() {
        this.styleMap = null;
        this.idMap = null;
        this.replacedElementFactory.reset();
    }

    public ReplacedElementFactory getReplacedElementFactory() {
        return this.replacedElementFactory;
    }

    public void setReplacedElementFactory(ReplacedElementFactory ref) {
        if (ref == null) {
            throw new NullPointerException("replacedElementFactory may not be null");
        }
        if (this.replacedElementFactory != null) {
            this.replacedElementFactory.reset();
        }
        this.replacedElementFactory = ref;
    }

    public void removeElementReferences(Element e) {
        String id = this.namespaceHandler.getID(e);
        if (id != null && id.length() > 0) {
            this.removeBoxId(id);
        }
        if (this.styleMap != null) {
            this.styleMap.remove(e);
        }
        this.getCss().removeStyle(e);
        this.getReplacedElementFactory().remove(e);
        if (e.hasChildNodes()) {
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (child.getNodeType() != 1) continue;
                this.removeElementReferences((Element)child);
            }
        }
    }

    public LineBreakingStrategy getLineBreakingStrategy() {
        return this.lineBreakingStrategy;
    }

    public void setLineBreakingStrategy(LineBreakingStrategy lineBreakingStrategy) {
        this.lineBreakingStrategy = lineBreakingStrategy;
    }
}

