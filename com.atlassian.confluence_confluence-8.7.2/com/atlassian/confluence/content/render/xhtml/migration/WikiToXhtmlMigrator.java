/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.RendererConfiguration
 *  com.atlassian.renderer.embedded.EmbeddedResourceRenderer
 *  com.atlassian.renderer.links.LinkRenderer
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.migration.ErrorReportingV2Renderer;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.RendererConfiguration;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.renderer.links.LinkRenderer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class WikiToXhtmlMigrator
implements ExceptionTolerantMigrator {
    private final RendererConfiguration rendererConfiguration;
    private final LinkRenderer defaultLinkRenderer;
    private final EmbeddedResourceRenderer defaultEmbeddedRenderer;
    private final ErrorReportingV2Renderer renderer;

    public WikiToXhtmlMigrator(RendererConfiguration rendererConfiguration, LinkRenderer defaultLinkRenderer, EmbeddedResourceRenderer defaultEmbeddedRenderer, ErrorReportingV2Renderer renderer) {
        this.rendererConfiguration = rendererConfiguration;
        this.defaultLinkRenderer = defaultLinkRenderer;
        this.defaultEmbeddedRenderer = defaultEmbeddedRenderer;
        this.renderer = renderer;
    }

    @Override
    public String migrate(String wiki, RenderContext context, List<RuntimeException> exceptions) {
        if (StringUtils.isBlank((CharSequence)wiki)) {
            return "";
        }
        if (context.getRenderMode() != null && context.getRenderMode().renderNothing()) {
            return wiki;
        }
        this.initializeContext(context);
        return this.renderer.render(wiki, context, exceptions);
    }

    @Override
    public ExceptionTolerantMigrator.MigrationResult migrate(String wiki, ConversionContext conversionContext) {
        ArrayList<RuntimeException> exceptions = new ArrayList<RuntimeException>();
        String outputContent = this.migrate(wiki, conversionContext.getRenderContext(), exceptions);
        return new ExceptionTolerantMigrator.MigrationResult(outputContent, true, exceptions);
    }

    private void initializeContext(RenderContext context) {
        if (context.getSiteRoot() == null) {
            context.setSiteRoot(this.rendererConfiguration.getWebAppContextPath());
        }
        if (context.getImagePath() == null) {
            context.setImagePath(context.getSiteRoot() + "/images");
        }
        if (context.getLinkRenderer() == null) {
            context.setLinkRenderer(this.defaultLinkRenderer);
        }
        if (context.getEmbeddedResourceRenderer() == null) {
            context.setEmbeddedResourceRenderer(this.defaultEmbeddedRenderer);
        }
        if (context.getCharacterEncoding() == null) {
            context.setCharacterEncoding(this.rendererConfiguration.getCharacterEncoding());
        }
    }
}

