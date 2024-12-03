/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.PageTemplateContext
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.xhtml.api.EditorFormatService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.plugins.rest.common.security.AuthenticationContext
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.tinymceplugin.rest;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.PageTemplateContext;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.tinymceplugin.rest.entities.WikiToXhtmlConversionData;
import com.atlassian.confluence.xhtml.api.EditorFormatService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.plugins.rest.common.security.AuthenticationContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import java.security.Principal;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/wikixhtmlconverter")
public class WikiXhtmlConverter {
    private static final Logger log = LoggerFactory.getLogger(WikiXhtmlConverter.class);
    private static final String TEMPLATE_CONTEXT = "template";
    private final EditorFormatService editorFormatService;
    private final ContentEntityManager contentEntityManager;
    private final PageTemplateManager pageTemplateManager;
    private final SpaceManager spaceManager;
    @Context
    protected AuthenticationContext authContext;

    @Autowired
    public WikiXhtmlConverter(@ComponentImport EditorFormatService editorFormatService, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport PageTemplateManager pageTemplateManager, @ComponentImport SpaceManager spaceManager) {
        this.editorFormatService = editorFormatService;
        this.contentEntityManager = contentEntityManager;
        this.pageTemplateManager = pageTemplateManager;
        this.spaceManager = spaceManager;
    }

    @POST
    @AnonymousAllowed
    @Consumes(value={"application/json"})
    @Produces(value={"text/html"})
    public Response convert(WikiToXhtmlConversionData data) {
        PageTemplateContext renderContext;
        if (data == null || StringUtils.isBlank((CharSequence)data.getWiki()) || data.getEntityId() < 0L) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (log.isDebugEnabled()) {
            Principal principal = this.authContext.getPrincipal();
            String userName = "Anonymous";
            if (principal != null) {
                userName = principal.getName();
            }
            log.debug("[{}][{}-{}]\n{}", (Object[])new String[]{userName, data.getSpaceKey(), String.valueOf(data.getEntityId()), data.getWiki()});
        }
        if (TEMPLATE_CONTEXT.equals(data.getContextType())) {
            PageTemplate templateEntity = this.pageTemplateManager.getPageTemplate(data.getEntityId());
            if (templateEntity == null) {
                templateEntity = new PageTemplate();
                templateEntity.setSpace(this.spaceManager.getSpace(data.getSpaceKey()));
            }
            renderContext = new PageTemplateContext(templateEntity);
        } else {
            ContentEntityObject contentEntity = this.contentEntityManager.getById(data.getEntityId());
            if (contentEntity == null) {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)("Could not find content with contentId: " + data.getEntityId())).build();
            }
            renderContext = new PageContext(contentEntity);
        }
        renderContext.addParam((Object)"DO_LINK_PERMISSION_CHECK", (Object)true);
        if (data.shouldSuppressFirstParagraph()) {
            renderContext.pushRenderMode(RenderMode.suppress((long)256L));
        }
        DefaultConversionContext context = new DefaultConversionContext((RenderContext)renderContext);
        try {
            String xhtml = this.editorFormatService.convertWikiToEdit(data.getWiki(), (ConversionContext)context);
            return Response.ok((Object)xhtml).build();
        }
        catch (XhtmlException ex) {
            throw new RuntimeException(ex);
        }
    }
}

