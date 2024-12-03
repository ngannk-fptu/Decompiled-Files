/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.util.GeneralUtil
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.util.GeneralUtil;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemoteComment {
    long id;
    long pageId;
    long parentId;
    Date created;
    Date modified;
    String title;
    String url;
    String creator;
    String content;
    String modifier;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.pages.Comment comment \nequals java.lang.Object o \nsetContent java.lang.String content \nsetCreated java.util.Date created \nsetCreator java.lang.String creator \nsetId long id \nsetModified java.util.Date modified \nsetModifier java.lang.String modifier \nsetPageId long pageId \nsetParentId long parentId \nsetTitle java.lang.String title \nsetUrl java.lang.String url \n";

    public RemoteComment() {
    }

    public RemoteComment(Comment comment) {
        String lastModifierName;
        Date lastModificationDate;
        String creatorName;
        this.id = comment.getId();
        ContentEntityObject container = Objects.requireNonNull(comment.getContainer());
        this.pageId = container.getId();
        Comment parent = comment.getParent();
        this.parentId = parent != null && container.equals((Object)parent.getContainer()) ? parent.getId() : 0L;
        this.title = comment.getDisplayTitle();
        this.url = GeneralUtil.getGlobalSettings().getBaseUrl() + comment.getUrlPath();
        this.content = comment.getBodyAsString();
        Date creationDate = comment.getCreationDate();
        if (creationDate != null) {
            this.created = new Date(creationDate.getTime());
        }
        if ((creatorName = comment.getCreatorName()) != null) {
            this.creator = creatorName;
        }
        if ((lastModificationDate = comment.getLastModificationDate()) != null) {
            this.modified = new Date(lastModificationDate.getTime());
        }
        if ((lastModifierName = comment.getLastModifierName()) != null) {
            this.modifier = lastModifierName;
        }
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPageId() {
        return this.pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getParentId() {
        return this.parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public Date getModified() {
        return this.modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getModifier() {
        return this.modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RemoteComment)) {
            return false;
        }
        RemoteComment remoteComment = (RemoteComment)o;
        if (this.id != remoteComment.id) {
            return false;
        }
        if (this.pageId != remoteComment.pageId) {
            return false;
        }
        if (this.content != null ? !this.content.equals(remoteComment.content) : remoteComment.content != null) {
            return false;
        }
        if (this.created != null ? !this.created.equals(remoteComment.created) : remoteComment.created != null) {
            return false;
        }
        if (this.creator != null ? !this.creator.equals(remoteComment.creator) : remoteComment.creator != null) {
            return false;
        }
        if (this.title != null ? !this.title.equals(remoteComment.title) : remoteComment.title != null) {
            return false;
        }
        return !(this.url != null ? !this.url.equals(remoteComment.url) : remoteComment.url != null);
    }

    public int hashCode() {
        int result = (int)(this.id ^ this.id >>> 32);
        result = 29 * result + (int)(this.pageId ^ this.pageId >>> 32);
        result = 29 * result + (this.created != null ? this.created.hashCode() : 0);
        result = 29 * result + (this.title != null ? this.title.hashCode() : 0);
        result = 29 * result + (this.url != null ? this.url.hashCode() : 0);
        result = 29 * result + (this.creator != null ? this.creator.hashCode() : 0);
        result = 29 * result + (this.content != null ? this.content.hashCode() : 0);
        return result;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this);
    }
}

