/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess
 *  com.benryan.components.OcSettingsManager
 *  com.google.common.collect.Maps
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.benryan.rest;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess;
import com.benryan.components.OcSettingsManager;
import com.benryan.conversion.WebDavUtil;
import com.google.common.collect.Maps;
import java.util.HashMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@UnlicensedSiteAccess
@Produces(value={"application/json"})
@Path(value="metadata")
public class EditInOfficeResource {
    private final AttachmentManager attachmentManager;
    private final OcSettingsManager ocSettings;

    public EditInOfficeResource(@ComponentImport AttachmentManager attachmentManager, OcSettingsManager ocSettings) {
        this.attachmentManager = attachmentManager;
        this.ocSettings = ocSettings;
    }

    @GET
    @Path(value="/{attachmentId}")
    public Response getEditInOfficeMetadata(@PathParam(value="attachmentId") long attachmentId) {
        HashMap metadata = Maps.newHashMap();
        Attachment attachment = this.attachmentManager.getAttachment(attachmentId);
        WebDavUtil webDavUtil = new WebDavUtil((AbstractPage)attachment.getContainer());
        String webDavUrl = webDavUtil.getRelWebDavUrl(attachment.getFileName());
        boolean pathAuth = this.ocSettings.getPathAuth();
        metadata.put("webDavUrl", webDavUrl);
        metadata.put("usePathAuth", pathAuth);
        return Response.ok((Object)metadata).build();
    }
}

