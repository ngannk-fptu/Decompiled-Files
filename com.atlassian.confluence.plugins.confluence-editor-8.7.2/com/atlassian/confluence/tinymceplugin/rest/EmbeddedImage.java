/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.xhtml.api.EditorFormatService
 *  com.atlassian.confluence.xhtml.api.EmbeddedImage
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.renderer.RenderContext
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.tinymceplugin.rest;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.tinymceplugin.rest.entities.EmbeddedImagePlaceholderRequest;
import com.atlassian.confluence.xhtml.api.EditorFormatService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.renderer.RenderContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/embed")
@AnonymousAllowed
public class EmbeddedImage {
    private final EditorFormatService editorFormatService;
    private final ContentEntityManager contentEntityManager;

    public EmbeddedImage(@ComponentImport EditorFormatService editorFormatService, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager) {
        this.editorFormatService = editorFormatService;
        this.contentEntityManager = contentEntityManager;
    }

    @POST
    @Path(value="/placeholder/image")
    @Consumes(value={"application/json"})
    @Produces(value={"text/html"})
    public Response generatePlaceHolder(EmbeddedImagePlaceholderRequest request) {
        DefaultConversionContext conversionContext;
        ContentEntityObject contentEntityObject = this.contentEntityManager.getById(request.getContentId().longValue());
        if (contentEntityObject == null) {
            PageContext renderContext = new PageContext();
            conversionContext = new DefaultConversionContext((RenderContext)renderContext);
        } else {
            conversionContext = new DefaultConversionContext((RenderContext)contentEntityObject.toPageContext());
        }
        DefaultEmbeddedImage embeddedImage = StringUtils.isNotBlank((CharSequence)request.getUrl()) ? new DefaultEmbeddedImage((NamedResourceIdentifier)new UrlResourceIdentifier(request.getUrl())) : new DefaultEmbeddedImage((NamedResourceIdentifier)new AttachmentResourceIdentifier(request.getFilename()));
        if (StringUtils.isNotBlank((CharSequence)request.getAlignment()) && !"none".equals(request.getAlignment())) {
            embeddedImage.setAlignment(request.getAlignment());
        }
        embeddedImage.setBorder(request.isBorder());
        embeddedImage.setThumbnail(request.isThumbnail());
        try {
            return Response.ok((Object)this.editorFormatService.convertEmbeddedImageToEdit((com.atlassian.confluence.xhtml.api.EmbeddedImage)embeddedImage, (ConversionContext)conversionContext)).build();
        }
        catch (XhtmlException e) {
            return Response.serverError().build();
        }
    }
}

