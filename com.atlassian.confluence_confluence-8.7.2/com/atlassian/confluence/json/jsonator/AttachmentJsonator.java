/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.jsonator;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.json.jsonator.Jsonator;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.thumbnail.CannotGenerateThumbnailException;
import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.util.actions.ContentTypesDisplayMapper;

public class AttachmentJsonator
implements Jsonator<Attachment> {
    private final ContextPathHolder contextPathHolder;
    private final ThumbnailManager thumbnailManager;

    public AttachmentJsonator(ContextPathHolder context, ThumbnailManager thumbnailManager) {
        this.contextPathHolder = context;
        this.thumbnailManager = thumbnailManager;
    }

    @Override
    public Json convert(Attachment attachment) {
        JsonObject json = new JsonObject();
        String id = String.valueOf(attachment.getId());
        String fileName = attachment.getFileName();
        String ownerId = attachment.getContainer().getIdAsString();
        String niceFileSize = attachment.getNiceFileSize();
        json.setProperty("id", id);
        json.setProperty("name", fileName);
        json.setProperty("contentId", ownerId);
        json.setProperty("version", String.valueOf(attachment.getVersion()));
        json.setProperty("type", attachment.getMediaType());
        json.setProperty("niceSize", niceFileSize);
        json.setProperty("size", attachment.getFileSize());
        json.setProperty("creatorName", attachment.getCreatorName());
        json.setProperty("creationDate", attachment.getCreationDate());
        json.setProperty("lastModifier", attachment.getLastModifierName());
        json.setProperty("lastModificationDate", attachment.getLastModificationDate());
        json.setProperty("url", this.contextPathHolder.getContextPath() + attachment.getUrlPath());
        json.setProperty("downloadUrl", this.contextPathHolder.getContextPath() + attachment.getDownloadPath());
        json.setProperty("comment", attachment.getVersionComment());
        String iconClass = ContentTypesDisplayMapper.getIconForAttachment(attachment.getMediaType(), fileName);
        json.setProperty("iconClass", iconClass);
        json.setProperty("title", fileName);
        json.setProperty("fileName", fileName);
        json.setProperty("ownerId", ownerId);
        json.setProperty("niceFileSize", niceFileSize);
        json.setProperty("attachmentId", id);
        try {
            ThumbnailInfo thumbnailInfo = this.thumbnailManager.getThumbnailInfo(attachment);
            json.setProperty("thumbnailUrl", thumbnailInfo.getThumbnailUrlPath());
            json.setProperty("thumbnailHeight", thumbnailInfo.getThumbnailHeight());
            json.setProperty("thumbnailWidth", thumbnailInfo.getThumbnailWidth());
        }
        catch (CannotGenerateThumbnailException cannotGenerateThumbnailException) {
            // empty catch block
        }
        return json;
    }
}

