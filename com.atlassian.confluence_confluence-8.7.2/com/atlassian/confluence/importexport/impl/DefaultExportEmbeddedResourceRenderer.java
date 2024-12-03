/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.attachments.RendererAttachmentManager
 *  com.atlassian.renderer.embedded.EmbeddedImage
 *  com.atlassian.renderer.embedded.EmbeddedImageRenderer
 *  com.atlassian.renderer.embedded.EmbeddedResource
 *  com.atlassian.renderer.embedded.EmbeddedResourceRenderer
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.importexport.impl.ExportEmbeddedResourceRenderer;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.embedded.EmbeddedResourceUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.attachments.RendererAttachmentManager;
import com.atlassian.renderer.embedded.EmbeddedImage;
import com.atlassian.renderer.embedded.EmbeddedImageRenderer;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultExportEmbeddedResourceRenderer
implements EmbeddedResourceRenderer {
    private static final Logger log = LoggerFactory.getLogger(DefaultExportEmbeddedResourceRenderer.class);
    private PermissionManager permissionManager;

    public String renderResource(EmbeddedResource resource, RenderContext context) {
        try {
            Attachment attachment = EmbeddedResourceUtils.resolveAttachment((PageContext)context, resource);
            if (attachment == null || !this.isPermittedToViewAttachment(attachment)) {
                throw new IllegalArgumentException("!" + resource.getOriginalLinkText() + "!");
            }
            if (resource instanceof EmbeddedImage) {
                EmbeddedImageRenderer renderer = new EmbeddedImageRenderer((RendererAttachmentManager)ContainerManager.getComponent((String)"rendererAttachmentManager"));
                return renderer.renderResource(resource, context);
            }
            ExportEmbeddedResourceRenderer renderer = new ExportEmbeddedResourceRenderer();
            return renderer.renderResource(resource, context);
        }
        catch (IllegalArgumentException e) {
            log.error("Unable to render resource", (Throwable)e);
            return RenderUtils.error((String)e.getMessage());
        }
    }

    protected boolean isPermittedToViewAttachment(Attachment attachment) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.permissionManager.hasPermission((User)user, Permission.VIEW, attachment);
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

