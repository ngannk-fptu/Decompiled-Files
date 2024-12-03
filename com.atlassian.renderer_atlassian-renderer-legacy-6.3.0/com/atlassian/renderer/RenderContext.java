/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.renderer;

import com.atlassian.renderer.RenderContextOutputType;
import com.atlassian.renderer.RenderedContentStore;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.renderer.escaper.RenderEscaper;
import com.atlassian.renderer.escaper.RenderEscapers;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkRenderer;
import com.atlassian.renderer.v2.RenderMode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderContext
implements RenderContextOutputType {
    private static final Logger log = LoggerFactory.getLogger(RenderContext.class);
    private Stack<RenderMode> renderModes = new Stack();
    private RenderedContentStore store;
    private RenderEscaper escaper;
    private String baseUrl;
    private String siteRoot;
    private String imagePath;
    private String attachmentsPath;
    private String characterEncoding;
    private LinkRenderer linkRenderer;
    private EmbeddedResourceRenderer resourceRenderer;
    private boolean renderingForWysiwyg;
    private List<Link> externalReferences = new LinkedList<Link>();
    private Map<Object, Object> parameters = new HashMap<Object, Object>();
    private String outputType;

    public RenderContext() {
        this(new RenderedContentStore());
    }

    protected RenderContext(RenderedContentStore store) {
        this.renderModes.push(RenderMode.ALL);
        this.store = store == null ? new RenderedContentStore() : store;
    }

    public RenderMode getRenderMode() {
        if (this.renderModes.empty()) {
            return RenderMode.ALL;
        }
        return this.renderModes.peek();
    }

    public void pushRenderMode(RenderMode renderMode) {
        this.renderModes.push(renderMode);
    }

    public RenderMode popRenderMode() {
        if (this.renderModes.empty()) {
            log.warn("Render mode stack is empty!", (Throwable)new Exception("Render mode stack is empty"));
            return RenderMode.ALL;
        }
        return this.renderModes.pop();
    }

    public RenderedContentStore getRenderedContentStore() {
        return this.store;
    }

    public String addRenderedContent(Object content) {
        return this.store.addBlock(content);
    }

    public String addRenderedContent(Object content, TokenType type) {
        return this.store.addContent(content, type);
    }

    @Deprecated
    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setSiteRoot(String siteRoot) {
        this.siteRoot = siteRoot;
    }

    @Deprecated
    public String getSiteRoot() {
        return this.siteRoot;
    }

    public void setLinkRenderer(LinkRenderer linkRenderer) {
        this.linkRenderer = linkRenderer;
    }

    public LinkRenderer getLinkRenderer() {
        return this.linkRenderer;
    }

    public void setEmbeddedResourceRenderer(EmbeddedResourceRenderer renderer) {
        this.resourceRenderer = renderer;
    }

    public EmbeddedResourceRenderer getEmbeddedResourceRenderer() {
        return this.resourceRenderer;
    }

    public String getAttachmentsPath() {
        return this.attachmentsPath;
    }

    public void setAttachmentsPath(String attachmentsPath) {
        this.attachmentsPath = attachmentsPath;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RenderContext)) {
            return false;
        }
        RenderContext renderContext = (RenderContext)o;
        if (this.imagePath != null ? !this.imagePath.equals(renderContext.imagePath) : renderContext.imagePath != null) {
            return false;
        }
        if (this.renderModes != null ? !this.renderModes.equals(renderContext.renderModes) : renderContext.renderModes != null) {
            return false;
        }
        return !(this.store != null ? !this.store.equals(renderContext.store) : renderContext.store != null);
    }

    public int hashCode() {
        int result = this.renderModes != null ? this.renderModes.hashCode() : 0;
        result = 29 * result + (this.store != null ? this.store.hashCode() : 0);
        result = 29 * result + (this.imagePath != null ? this.imagePath.hashCode() : 0);
        return result;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public boolean isRenderingForWysiwyg() {
        return this.renderingForWysiwyg;
    }

    public void setRenderingForWysiwyg(boolean renderingForWysiwyg) {
        this.renderingForWysiwyg = renderingForWysiwyg;
    }

    public void addExternalReference(Link link) {
        if (!this.externalReferences.contains(link)) {
            this.externalReferences.add(link);
        }
    }

    public List<Link> getExternalReferences() {
        return this.externalReferences;
    }

    @Deprecated
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public Map<Object, Object> getParams() {
        return this.parameters;
    }

    public void addParam(Object key, Object value) {
        this.parameters.put(key, value);
    }

    public Object getParam(Object key) {
        return this.parameters.get(key);
    }

    public String getOutputType() {
        if (this.outputType == null) {
            return "display";
        }
        return this.outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public RenderEscaper getEscaper() {
        if (this.escaper == null) {
            this.escaper = RenderEscapers.NONE_RENDERER_ESCAPER;
        }
        return this.escaper;
    }

    public void setEscaper(RenderEscaper escaper) {
        this.escaper = escaper;
    }
}

