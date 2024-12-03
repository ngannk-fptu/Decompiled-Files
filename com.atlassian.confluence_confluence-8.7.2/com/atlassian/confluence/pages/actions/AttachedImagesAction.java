/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.thumbnail.CannotGenerateThumbnailException;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.util.AttachmentFileNameComparator;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AttachedImagesAction
extends ConfluenceActionSupport
implements Beanable {
    private AttachmentManager attachmentManager;
    private ThumbnailManager thumbnailManager;
    private ContentEntityManager contentEntityManager;
    private final List<Attachment> attachedImages = new ArrayList<Attachment>();
    private long contentId;
    private ContentEntityObject contentEntity;

    @Override
    public void validate() {
        super.validate();
        if (this.getContentEntity() == null) {
            this.addActionError("No entity was found with id: " + this.contentId);
        }
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, this.getContentEntity());
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        List<Attachment> allAttachments = this.attachmentManager.getLatestVersionsOfAttachments(this.getContentEntity());
        for (Attachment attachment : allAttachments) {
            if (!this.thumbnailManager.isThumbnailable(attachment)) continue;
            try {
                this.thumbnailManager.getThumbnailInfo(attachment);
            }
            catch (CannotGenerateThumbnailException e) {
                continue;
            }
            this.attachedImages.add(attachment);
        }
        Collections.sort(this.attachedImages, new AttachmentFileNameComparator(this.getLocale()));
        return "success";
    }

    @Override
    public Object getBean() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("images", this.attachedImages);
        result.put("totalImages", this.attachedImages.size());
        return result;
    }

    private ContentEntityObject getContentEntity() {
        if (this.contentEntity == null) {
            this.contentEntity = this.contentEntityManager.getById(this.contentId);
        }
        return this.contentEntity;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setThumbnailManager(ThumbnailManager thumbnailManager) {
        this.thumbnailManager = thumbnailManager;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }
}

