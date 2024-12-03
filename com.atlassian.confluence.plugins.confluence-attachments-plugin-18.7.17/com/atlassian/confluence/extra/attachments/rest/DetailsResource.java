/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 */
package com.atlassian.confluence.extra.attachments.rest;

import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.extra.attachments.ImagePreviewRenderer;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path(value="details")
public class DetailsResource {
    private final AttachmentManager attachmentManager;
    private final ImagePreviewRenderer imagePreviewRenderer;
    private final PermissionManager permissionManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;

    public DetailsResource(@ComponentImport AttachmentManager attachmentManager, ImagePreviewRenderer imagePreviewRenderer, @ComponentImport PermissionManager permissionManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager) {
        this.attachmentManager = attachmentManager;
        this.imagePreviewRenderer = imagePreviewRenderer;
        this.permissionManager = permissionManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    @Produces(value={"text/html"})
    @GET
    @Path(value="preview")
    @AnonymousAllowed
    public String getPreview(@QueryParam(value="attachmentId") long attachmentId) {
        Attachment attachment = this.attachmentManager.getAttachment(attachmentId);
        if (attachment != null) {
            if (!this.hasViewPermission(attachment)) {
                throw new PermissionException(this.getI18NBean().getText("confluence.extra.attachments.error.noviewpermission"));
            }
            try {
                return this.getAttachmentPreviewHtml(attachment);
            }
            catch (XhtmlException e) {
                throw new ServiceException();
            }
        }
        return "";
    }

    private String getAttachmentPreviewHtml(Attachment attachment) throws XhtmlException {
        return this.imagePreviewRenderer.render(attachment, (ConversionContext)new DefaultConversionContext((RenderContext)attachment.getContainer().toPageContext()));
    }

    private boolean hasViewPermission(Attachment attachment) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)attachment);
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }
}

