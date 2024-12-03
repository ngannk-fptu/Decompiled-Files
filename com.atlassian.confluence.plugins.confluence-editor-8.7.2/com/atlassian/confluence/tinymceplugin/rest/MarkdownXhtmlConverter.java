/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.plugins.rest.common.security.AuthenticationContext
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.commonmark.Extension
 *  org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
 *  org.commonmark.ext.gfm.tables.TablesExtension
 *  org.commonmark.ext.ins.InsExtension
 *  org.commonmark.node.Node
 *  org.commonmark.parser.Parser
 *  org.commonmark.renderer.html.HtmlRenderer
 */
package com.atlassian.confluence.tinymceplugin.rest;

import com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner;
import com.atlassian.confluence.tinymceplugin.rest.entities.WikiToXhtmlConversionData;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.plugins.rest.common.security.AuthenticationContext;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.ins.InsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

@Path(value="/markdownxhtmlconverter")
public class MarkdownXhtmlConverter {
    private final RenderedContentCleaner renderedContentCleaner;
    @Context
    protected AuthenticationContext authContext;

    public MarkdownXhtmlConverter(@ComponentImport RenderedContentCleaner renderedContentCleaner) {
        this.renderedContentCleaner = renderedContentCleaner;
    }

    @POST
    @AnonymousAllowed
    @Consumes(value={"application/json"})
    @Produces(value={"text/html"})
    public Response convert(WikiToXhtmlConversionData data) {
        if (data == null || StringUtils.isBlank((CharSequence)data.getWiki()) || data.getEntityId() < 0L) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        List<Extension> extensions = Arrays.asList(InsExtension.create(), StrikethroughExtension.create(), TablesExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
        Node node = parser.parse(data.getWiki());
        String xhtml = renderer.render(node);
        xhtml = this.renderedContentCleaner.cleanQuietly(xhtml);
        return Response.ok((Object)xhtml).build();
    }
}

