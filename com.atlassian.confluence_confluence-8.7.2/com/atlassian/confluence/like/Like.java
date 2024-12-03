/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.like;

import com.atlassian.confluence.like.LikeEntity;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Like {
    private final long contentId;
    private final String username;
    private final Date createdDate;

    public Like(long contentId, String username, Date createdDate) {
        if (contentId <= 0L) {
            throw new IllegalArgumentException("contentId must be greater than 0");
        }
        if (StringUtils.isBlank((CharSequence)username)) {
            throw new IllegalArgumentException("username must not be null or blank");
        }
        if (createdDate == null) {
            throw new IllegalArgumentException("createdDate must not be null");
        }
        this.contentId = contentId;
        this.username = username;
        this.createdDate = createdDate;
    }

    public Like(LikeEntity likeEntity) {
        this.contentId = likeEntity.getContent().getId();
        ConfluenceUser user = likeEntity.getUser();
        this.username = user != null ? user.getName() : null;
        this.createdDate = likeEntity.getCreationDate();
    }

    public long getContentId() {
        return this.contentId;
    }

    public String getUsername() {
        return this.username;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Like)) {
            return false;
        }
        Like that = (Like)obj;
        return new EqualsBuilder().append(this.contentId, that.contentId).append((Object)this.username, (Object)that.username).append((Object)this.createdDate, (Object)that.createdDate).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(this.contentId).append((Object)this.username).append((Object)this.createdDate).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder((Object)this, ToStringStyle.SHORT_PREFIX_STYLE).append(this.contentId).append((Object)this.username).append((Object)this.createdDate).toString();
    }
}

