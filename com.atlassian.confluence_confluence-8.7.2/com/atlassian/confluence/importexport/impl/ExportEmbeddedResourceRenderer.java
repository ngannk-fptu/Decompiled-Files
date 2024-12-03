/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.embedded.EmbeddedResource
 *  com.atlassian.renderer.embedded.EmbeddedResourceRenderer
 *  com.atlassian.renderer.util.UrlUtil
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.embedded.EmbeddedResourceUtils;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.renderer.util.UrlUtil;

public class ExportEmbeddedResourceRenderer
implements EmbeddedResourceRenderer {
    public String renderResource(EmbeddedResource embeddedResource, RenderContext renderContext) {
        if (embeddedResource.isExternal()) {
            return embeddedResource.getUrl();
        }
        Attachment attachment = EmbeddedResourceUtils.resolveAttachment((PageContext)renderContext, embeddedResource);
        Object objectUrl = "";
        Object attachmentsPath = renderContext.getAttachmentsPath();
        if (attachmentsPath == null) {
            attachmentsPath = attachment != null ? ConfluenceRenderUtils.getAbsoluteAttachmentRemotePath(attachment) : GeneralUtil.getGlobalSettings().getBaseUrl() + "/download/attachments/" + ((PageContext)renderContext).getEntity().getId();
        }
        if (attachmentsPath != null) {
            objectUrl = UrlUtil.escapeSpecialCharacters((String)attachmentsPath) + "/";
        }
        objectUrl = (String)objectUrl + UrlUtil.escapeSpecialCharacters((String)embeddedResource.getFilename());
        return "<a href=\"" + (String)objectUrl + "\" title=\"Link to embedded resource\">" + embeddedResource.getFilename() + "</a>";
    }
}

