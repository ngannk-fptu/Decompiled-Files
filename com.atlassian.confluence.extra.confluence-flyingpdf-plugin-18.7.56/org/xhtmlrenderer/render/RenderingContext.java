/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.awt.Rectangle;
import org.xhtmlrenderer.context.StyleReference;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.value.FontSpecification;
import org.xhtmlrenderer.extend.FSCanvas;
import org.xhtmlrenderer.extend.FontContext;
import org.xhtmlrenderer.extend.FontResolver;
import org.xhtmlrenderer.extend.OutputDevice;
import org.xhtmlrenderer.extend.TextRenderer;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.FSFontMetrics;
import org.xhtmlrenderer.render.PageBox;

public class RenderingContext
implements CssContext {
    protected SharedContext sharedContext;
    private OutputDevice outputDevice;
    private FontContext fontContext;
    private int pageCount;
    private int pageNo;
    private PageBox page;
    private Layer rootLayer;
    private int initialPageNo;

    public RenderingContext(SharedContext sharedContext) {
        this.sharedContext = sharedContext;
    }

    public void setContext(SharedContext sharedContext) {
        this.sharedContext = sharedContext;
    }

    public void setBaseURL(String url) {
        this.sharedContext.setBaseURL(url);
    }

    public UserAgentCallback getUac() {
        return this.sharedContext.getUac();
    }

    public String getBaseURL() {
        return this.sharedContext.getBaseURL();
    }

    public float getDPI() {
        return this.sharedContext.getDPI();
    }

    @Override
    public float getMmPerDot() {
        return this.sharedContext.getMmPerPx();
    }

    @Override
    public int getDotsPerPixel() {
        return this.sharedContext.getDotsPerPixel();
    }

    @Override
    public float getFontSize2D(FontSpecification font) {
        return this.sharedContext.getFont(font).getSize2D();
    }

    @Override
    public float getXHeight(FontSpecification parentFont) {
        return this.sharedContext.getXHeight(this.getFontContext(), parentFont);
    }

    public TextRenderer getTextRenderer() {
        return this.sharedContext.getTextRenderer();
    }

    public boolean isPaged() {
        return this.sharedContext.isPaged();
    }

    public FontResolver getFontResolver() {
        return this.sharedContext.getFontResolver();
    }

    @Override
    public FSFont getFont(FontSpecification font) {
        return this.sharedContext.getFont(font);
    }

    public FSCanvas getCanvas() {
        return this.sharedContext.getCanvas();
    }

    public Rectangle getFixedRectangle() {
        Rectangle result = !this.isPrint() ? this.sharedContext.getFixedRectangle() : new Rectangle(0, -this.page.getTop(), this.page.getContentWidth(this), this.page.getContentHeight(this) - 1);
        result.translate(-1, -1);
        return result;
    }

    public Rectangle getViewportRectangle() {
        Rectangle result = new Rectangle(this.getFixedRectangle());
        result.y *= -1;
        return result;
    }

    public boolean debugDrawBoxes() {
        return this.sharedContext.debugDrawBoxes();
    }

    public boolean debugDrawLineBoxes() {
        return this.sharedContext.debugDrawLineBoxes();
    }

    public boolean debugDrawInlineBoxes() {
        return this.sharedContext.debugDrawInlineBoxes();
    }

    public boolean debugDrawFontMetrics() {
        return this.sharedContext.debugDrawFontMetrics();
    }

    public boolean isInteractive() {
        return this.sharedContext.isInteractive();
    }

    public boolean isPrint() {
        return this.sharedContext.isPrint();
    }

    public OutputDevice getOutputDevice() {
        return this.outputDevice;
    }

    public void setOutputDevice(OutputDevice outputDevice) {
        this.outputDevice = outputDevice;
    }

    public FontContext getFontContext() {
        return this.fontContext;
    }

    public void setFontContext(FontContext fontContext) {
        this.fontContext = fontContext;
    }

    public void setPage(int pageNo, PageBox page) {
        this.pageNo = pageNo;
        this.page = page;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public PageBox getPage() {
        return this.page;
    }

    public int getPageNo() {
        return this.pageNo;
    }

    @Override
    public StyleReference getCss() {
        return this.sharedContext.getCss();
    }

    @Override
    public FSFontMetrics getFSFontMetrics(FSFont font) {
        return this.getTextRenderer().getFSFontMetrics(this.getFontContext(), font, "");
    }

    public Layer getRootLayer() {
        return this.rootLayer;
    }

    public void setRootLayer(Layer rootLayer) {
        this.rootLayer = rootLayer;
    }

    public int getInitialPageNo() {
        return this.initialPageNo;
    }

    public void setInitialPageNo(int initialPageNo) {
        this.initialPageNo = initialPageNo;
    }

    public Box getBoxById(String id) {
        return this.sharedContext.getBoxById(id);
    }
}

