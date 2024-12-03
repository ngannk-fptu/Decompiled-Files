/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Attachment$Type
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.sharepage.notifications.context.attachment;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.sharepage.ContentTypeResolver;
import com.atlassian.confluence.plugins.sharepage.ShareGroupEmailManager;
import com.atlassian.confluence.plugins.sharepage.notifications.context.AbstractContentEventRenderContextProvider;
import com.atlassian.confluence.plugins.sharepage.notifications.payload.ShareContentPayload;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractShareAttachmentEventRenderContextProvider
extends AbstractContentEventRenderContextProvider {
    private final AttachmentManager attachmentManager;
    private static final String DEFAULT_IMAGE_HEIGHT = "250";
    private static final String ICONS_RESOURCES_MODULE = "com.atlassian.confluence.plugins.share-page:share-content-plugin-icons";

    public AbstractShareAttachmentEventRenderContextProvider(@Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ContentTypeResolver contentTypeResolver, UserAccessor userAccessor, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, TransactionTemplate transactionTemplate, AttachmentManager attachmentManager, ShareGroupEmailManager shareGroupEmailManager) {
        super(contentEntityManager, contentTypeResolver, userAccessor, i18NBeanFactory, localeManager, transactionTemplate, shareGroupEmailManager);
        this.attachmentManager = attachmentManager;
    }

    @Override
    protected Content getContentForEntityId(Long entityId, Long contextualPageId) {
        return (Content)this.transactionTemplate.execute(() -> {
            Attachment attachment = this.getAttachment(entityId);
            Preconditions.checkNotNull((Object)attachment, (Object)"Attachment should not be null");
            ContentEntityObject attachmentOwner = attachment.getContainer();
            Preconditions.checkNotNull((Object)attachmentOwner, (Object)"Attachment owner should not be null");
            ContentEntityObject container = this.getAttachmentContainer(contextualPageId, attachmentOwner);
            return Content.builder().id(attachment.getContentId()).title(attachment.getTitle()).type(attachment.getContentTypeObject()).addLink(LinkType.WEB_UI, this.buildPreviewUrl(container, attachment, attachmentOwner.getId())).build();
        });
    }

    @Override
    protected Map<String, Object> buildContentSpecificContext(ShareContentPayload payload) {
        ImmutableMap.Builder context = ImmutableMap.builder();
        this.transactionTemplate.execute(() -> {
            Attachment attachment = this.getAttachment(payload.getEntityId());
            Preconditions.checkNotNull((Object)attachment, (Object)"Attachment should not be null");
            Preconditions.checkNotNull((Object)attachment.getContainer(), (Object)"Attachment Owner should not be null");
            ContentEntityObject container = this.getAttachmentContainer(payload.getContextualPageId(), attachment.getContainer());
            context.put((Object)"contentIconResourceModule", (Object)ICONS_RESOURCES_MODULE);
            context.put((Object)"contentIconResourceKey", (Object)this.getAttachmentIcon(attachment));
            boolean isImage = this.isImage(attachment);
            context.put((Object)"isImage", (Object)isImage);
            if (isImage) {
                context.put((Object)"imageUrl", (Object)attachment.getDownloadPath());
                context.put((Object)"imageHeight", (Object)DEFAULT_IMAGE_HEIGHT);
            }
            Content ContainerContent = Content.builder().id(container.getContentId()).title(container.getTitle()).type(ContentType.valueOf((String)container.getType())).addLink(LinkType.WEB_UI, container.getUrlPath()).build();
            context.put((Object)"container", (Object)ContainerContent);
            context.put((Object)"containerIconResourceModule", (Object)this.getContentIconResourceModule(container.getId()));
            context.put((Object)"containerIconResourceKey", (Object)(container.getType() + "-icon"));
            return null;
        });
        return context.build();
    }

    private ContentEntityObject getAttachmentContainer(Long attachmentContainerId, ContentEntityObject attachmentOwner) {
        ContentEntityObject ceo = attachmentContainerId == null || attachmentContainerId.longValue() == attachmentOwner.getId() ? attachmentOwner : this.contentEntityManager.getById(attachmentContainerId.longValue());
        Preconditions.checkNotNull((Object)ceo, (Object)"Attachment container should not be null");
        return ceo;
    }

    private boolean isImage(Attachment attachment) {
        String mediaType = attachment.getMediaType();
        return Attachment.Type.IMAGE.equals((Object)Attachment.Type.getForMimeType((String)mediaType, null));
    }

    private String getAttachmentIcon(Attachment attachment) {
        return this.isImage(attachment) ? "image-icon" : "generic-file-icon";
    }

    private Attachment getAttachment(Long attachmentId) {
        return this.attachmentManager.getAttachment(attachmentId.longValue());
    }

    private String buildPreviewUrl(ContentEntityObject container, Attachment attachment, Long attachmentOwnerId) {
        String containerLink = container.getUrlPath();
        try {
            if (containerLink != null) {
                String previewParamValue = "/" + attachmentOwnerId + "/" + String.valueOf(attachment.getId()) + "/" + URLEncoder.encode(attachment.getTitle(), "UTF-8");
                UrlBuilder urlBuilder = new UrlBuilder(containerLink).add("preview", previewParamValue);
                return urlBuilder.toUrl();
            }
            return "#";
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported", e);
        }
    }
}

