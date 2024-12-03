/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.renderer.RenderContext
 *  com.google.common.collect.Maps
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.UriInfo
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.benryan.rest;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.renderer.RenderContext;
import com.benryan.conversion.Converter;
import com.benryan.conversion.ConverterFactory;
import com.benryan.conversion.ConverterHelper;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@AnonymousAllowed
@Path(value="view")
@Produces(value={"application/json"})
public class ConverterMacroResource {
    private static final Logger log = LoggerFactory.getLogger(ConverterMacroResource.class);
    private final AttachmentManager attachmentManager;
    private final ContentEntityManager contentEntityManager;
    private final ConverterFactory converterFactory;
    private final ConverterHelper converterHelper;

    public ConverterMacroResource(@ComponentImport AttachmentManager attachmentManager, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ConverterFactory converterFactory, ConverterHelper converterHelper) {
        this.attachmentManager = attachmentManager;
        this.contentEntityManager = contentEntityManager;
        this.converterFactory = converterFactory;
        this.converterHelper = converterHelper;
    }

    @GET
    public Response getContent(@Context UriInfo uriInfo, @QueryParam(value="pageID") Long pageId, @QueryParam(value="name") String name) {
        ContentEntityObject page = this.contentEntityManager.getById(pageId.longValue());
        Attachment attachment = this.attachmentManager.getAttachment(page, name);
        if (null == page || null == attachment) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        try {
            Map queryParameters = Maps.transformValues((Map)uriInfo.getQueryParameters(), entry -> entry != null ? (String)entry.get(0) : null);
            Map<String, Object> arguments = this.converterHelper.validateArguments(queryParameters, (ConversionContext)new DefaultConversionContext((RenderContext)new PageContext(page)));
            Converter converter = this.converterFactory.create((String)queryParameters.get("type"));
            String content = converter.execute(arguments);
            return Response.ok(Collections.singletonMap("content", content)).build();
        }
        catch (Exception e) {
            log.error(e.getMessage());
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("attachmentId", attachment.getId());
            data.put("downloadUrl", this.attachmentManager.getAttachmentDownloadPath(page, name));
            data.put("fileName", attachment.getFileName());
            data.put("pageId", page.getContentId());
            return Response.status((int)422).entity(data).build();
        }
    }
}

