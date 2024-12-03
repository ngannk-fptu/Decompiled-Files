/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.MutableRenderer
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.components.RendererComponent
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.MutableRenderer;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.RendererComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorReportingV2Renderer
implements MutableRenderer {
    private static final Logger log = LoggerFactory.getLogger(ErrorReportingV2Renderer.class);
    private final List<RendererComponent> components;
    private final Marshaller<MacroDefinition> wikiMarkupMacroMarshaller;

    public ErrorReportingV2Renderer(List<RendererComponent> components, Marshaller<MacroDefinition> wikiMarkupMacroMarshaller) {
        this.components = components == null || components.isEmpty() ? Collections.emptyList() : new ArrayList<RendererComponent>(components);
        this.wikiMarkupMacroMarshaller = wikiMarkupMacroMarshaller;
    }

    public String render(String originalContent, RenderContext renderContext, List<RuntimeException> exceptions) {
        if (StringUtils.isBlank((CharSequence)originalContent)) {
            return "";
        }
        try {
            if (renderContext.getRenderMode().renderNothing()) {
                return originalContent;
            }
            String renderedWiki = originalContent;
            for (RendererComponent component : this.components) {
                RenderMode renderMode;
                if (!component.shouldRender(renderMode = renderContext.getRenderMode())) continue;
                renderedWiki = component.render(renderedWiki, renderContext);
            }
            return renderedWiki;
        }
        catch (RuntimeException ex) {
            if (exceptions != null) {
                exceptions.add(ex);
            } else {
                Object pageContextStr = "";
                if (renderContext instanceof PageContext) {
                    PageContext pageContext = (PageContext)renderContext;
                    pageContextStr = "on page " + pageContext.getSpaceKey() + ":" + pageContext.getPageTitle();
                }
                String msg = "Unable to render content " + (String)pageContextStr + "\nWrapping content in unmigrated wiki markup macro due to system error: " + ex.getMessage();
                if (log.isDebugEnabled()) {
                    log.debug(msg, (Throwable)ex);
                } else {
                    log.info(msg);
                }
            }
            try {
                return Streamables.writeToString(this.wikiMarkupMacroMarshaller.marshal(MacroDefinition.builder("unmigrated-wiki-markup").withMacroBody(new PlainTextMacroBody(originalContent)).build(), new DefaultConversionContext(renderContext)));
            }
            catch (XhtmlException e) {
                throw new RuntimeException("Unable to render content to storage format due to a system error: " + ex.getMessage());
            }
        }
    }

    public String render(String originalContent, RenderContext renderContext) {
        return this.render(originalContent, renderContext, null);
    }

    public String getRendererType() {
        return "atlassian-wiki-to-xhtml-conversion-renderer";
    }

    public void setComponents(List components) {
        throw new UnsupportedOperationException("This renderer has a fixed list of components");
    }

    public String renderAsText(String originalContent, RenderContext context) {
        throw new UnsupportedOperationException("The renderer does not support renderAsText");
    }
}

