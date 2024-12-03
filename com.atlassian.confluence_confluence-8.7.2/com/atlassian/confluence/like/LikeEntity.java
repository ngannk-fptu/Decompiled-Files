/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.like;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.base.Preconditions;
import java.util.Date;
import java.util.Objects;

public class LikeEntity {
    private long id;
    private ContentEntityObject content;
    private ConfluenceUser user;
    private Date creationDate;

    public LikeEntity() {
    }

    public LikeEntity(ContentEntityObject content, ConfluenceUser user, Date creationDate) {
        Preconditions.checkNotNull((Object)content, (Object)"Content to like must not be null");
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }
        if (creationDate == null) {
            throw new IllegalArgumentException("creationDate must not be null");
        }
        this.content = content;
        this.user = user;
        this.creationDate = creationDate;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }

    public ConfluenceUser getUser() {
        return this.user;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public long getId() {
        return this.id;
    }

    public void setContent(ContentEntityObject content) {
        this.content = content;
    }

    public void setUser(ConfluenceUser user) {
        this.user = user;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LikeEntity)) {
            return false;
        }
        LikeEntity that = (LikeEntity)obj;
        return Objects.equals(this.user, that.user) && Objects.equals(this.content, that.content);
    }

    public int hashCode() {
        return Objects.hash(this.user, this.content);
    }
}

