/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.xhtml.api.EditorFormatService
 *  com.atlassian.confluence.xhtml.api.Link
 *  com.atlassian.confluence.xhtml.api.LinkBody
 *  com.atlassian.confluence.xhtml.api.PlainTextLinkBody
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.renderer.RenderContext
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.tinymceplugin.rest;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.EditorFormatService;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.renderer.RenderContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/createlink")
@AnonymousAllowed
public class CreateLinkResource {
    private static final Logger log = LoggerFactory.getLogger(CreateLinkResource.class);
    private final EditorFormatService editorFormatService;

    public CreateLinkResource(@ComponentImport EditorFormatService editorFormatService) {
        this.editorFormatService = editorFormatService;
    }

    @GET
    @Path(value="/placeholder")
    @Consumes(value={"application/json"})
    @Produces(value={"text/plain"})
    public Response generatePlaceHolder(@QueryParam(value="pageTitle") String pageTitle, @QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="alias") String alias) {
        String linkHtml = "";
        PlainTextLinkBody plainLinkBody = null;
        if (StringUtils.isNotBlank((CharSequence)alias)) {
            plainLinkBody = new PlainTextLinkBody(alias);
        }
        PageResourceIdentifier resourceIdentifier = new PageResourceIdentifier(spaceKey, pageTitle);
        try {
            PageContext pageContext = new PageContext();
            pageContext.setOutputType(ConversionContextOutputType.DISPLAY.value());
            DefaultConversionContext context = new DefaultConversionContext((RenderContext)pageContext);
            linkHtml = this.editorFormatService.convertLinkToEdit((Link)new DefaultLink((ResourceIdentifier)resourceIdentifier, (LinkBody)plainLinkBody), (ConversionContext)context);
        }
        catch (XhtmlException e) {
            log.error("Error occurred rendering resource", (Throwable)e);
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok((Object)linkHtml).build();
    }
}

