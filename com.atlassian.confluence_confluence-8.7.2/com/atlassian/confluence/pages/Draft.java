/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.content.Content;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Draft
extends ContentEntityObject {
    public static final String LEGACY_DRAFT_PARENT_ID_KEY = "legacy.draft.parent.id";
    public static final String NEW = Content.UNSET.toString();
    public static final String CONTENT_TYPE = "draft";
    private String pageId = NEW;
    private String draftType;
    private int pageVersion;
    private String draftSpaceKey;

    @Override
    public boolean isDraft() {
        return true;
    }

    @Override
    @Deprecated
    public boolean sharedAccessAllowed(String shareId) {
        return false;
    }

    @Override
    public boolean sharedAccessAllowed(User user) {
        return this.wasCreatedBy(user);
    }

    @Override
    public String getShareId() {
        this.getProperties().removeProperty("share-id");
        return null;
    }

    @Override
    public boolean isUnpublished() {
        return NEW.equals(this.pageId);
    }

    @Deprecated
    public String getContentSummary() {
        return StringUtils.isEmpty((CharSequence)this.getBodyAsString()) ? "blank" : this.getExcerpt();
    }

    @Deprecated
    public boolean isAuthor(User user) {
        return this.wasCreatedBy(user);
    }

    public boolean isNewPage() {
        return NEW.equals(this.pageId);
    }

    @Override
    public boolean isIndexable() {
        return false;
    }

    public boolean isBlank() {
        return StringUtils.isEmpty((CharSequence)this.getTitle()) && StringUtils.isEmpty((CharSequence)this.getBodyAsString()) && this.getAttachments().isEmpty();
    }

    @Override
    public String getUrlPath() {
        return "/pages/resumedraft.action?draftId=" + this.getId();
    }

    @Override
    public String getNameForComparison() {
        return "Draft: " + (StringUtils.isNotBlank((CharSequence)this.getTitle()) ? this.getTitle() : "(No Title Specified)") + " by " + (StringUtils.isNotBlank((CharSequence)this.getCreatorName()) ? this.getCreatorName() : "Anonymous");
    }

    @Override
    public String getType() {
        return CONTENT_TYPE;
    }

    public String getDraftSpaceKey() {
        return this.draftSpaceKey;
    }

    public void setDraftSpaceKey(String draftSpaceKey) {
        this.draftSpaceKey = draftSpaceKey;
    }

    public void setPageVersion(int pageVersion) {
        this.pageVersion = pageVersion;
    }

    public int getPageVersion() {
        return this.pageVersion;
    }

    public String getPageId() {
        return this.pageId;
    }

    public Long getPageIdAsLong() {
        return Long.parseLong(this.pageId);
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = String.valueOf(pageId);
    }

    public String getDraftType() {
        return this.draftType;
    }

    public void setDraftType(String draftType) {
        this.draftType = draftType;
    }

    @Override
    public ContentId getContentId() {
        return ContentId.of((long)this.getId());
    }

    @Override
    public String toString() {
        return "Draft = { id: " + this.getId() + ", type: " + this.getDraftType() + ", title: " + this.getTitle() + ", pageId: " + this.getPageId() + ", pageVersion: " + this.getPageVersion() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Draft)) {
            return false;
        }
        Draft other = (Draft)o;
        return new EqualsBuilder().append((Object)this.getPageId(), (Object)other.getPageId()).append((Object)this.getCreatorName(), (Object)other.getCreatorName()).append((Object)this.getDraftType(), (Object)other.getDraftType()).append((Object)this.getDraftSpaceKey(), (Object)other.getDraftSpaceKey()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.getPageId()).append((Object)this.getCreatorName()).append((Object)this.getDraftType()).append((Object)this.getDraftSpaceKey()).hashCode();
    }
}

