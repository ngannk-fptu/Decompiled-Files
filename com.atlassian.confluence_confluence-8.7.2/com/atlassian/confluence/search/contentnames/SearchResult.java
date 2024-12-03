/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.search.contentnames;

import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SearchResult {
    private final Long id;
    private String name;
    private String url;
    private String contentType;
    private String previewKey;
    private Category category;
    private String spaceName;
    private String spaceKey;
    private String ownerType;
    private String parentTitle;
    private String contentPluginKey;
    private UserKey creatorKey;
    private UserKey lastModifierKey;
    private Date createdDate;
    private Date lastModifiedDate;
    private String username;
    private final Supplier<ConfluenceUser> creator = Suppliers.memoize(() -> FindUserHelper.getUserByUserKey(this.creatorKey));
    private final Supplier<ConfluenceUser> lastModifier = Suppliers.memoize(() -> FindUserHelper.getUserByUserKey(this.lastModifierKey));

    public SearchResult(Long id, String name, String url, String contentType) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.contentType = contentType;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getPreviewKey() {
        return this.previewKey;
    }

    public Category getCategory() {
        return this.category;
    }

    public String getSpaceName() {
        return this.spaceName;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getOwnerType() {
        return this.ownerType;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public Date getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public String getParentTitle() {
        return this.parentTitle;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setPreviewKey(String previewKey) {
        this.previewKey = previewKey;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public void setContentPluginKey(String contentPluginKey) {
        this.contentPluginKey = contentPluginKey;
    }

    public String getContentPluginKey() {
        return this.contentPluginKey;
    }

    public ConfluenceUser getCreatorUser() {
        return (ConfluenceUser)this.creator.get();
    }

    public void setCreatorKey(UserKey creatorKey) {
        this.creatorKey = creatorKey;
    }

    public ConfluenceUser getLastModifierUser() {
        return (ConfluenceUser)this.lastModifier.get();
    }

    public void setLastModifierKey(UserKey lastModifierKey) {
        this.lastModifierKey = lastModifierKey;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String toString() {
        ToStringBuilder builder = new ToStringBuilder((Object)this);
        builder.append("id", (Object)this.id).append("name", (Object)this.name).append("url", (Object)this.url).append("contentType", (Object)this.contentType).append("previewKey", (Object)this.previewKey).append("category", (Object)this.category).append("spaceName", (Object)this.spaceName).append("spaceKey", (Object)this.spaceKey).append("contentPluginKey", (Object)this.contentPluginKey);
        return builder.toString();
    }
}

