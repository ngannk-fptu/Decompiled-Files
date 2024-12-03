/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import java.util.Calendar;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BlogPostsForDateResourceIdentifier
implements ResourceIdentifier {
    private final Calendar postingDay;
    private final String spaceKey;

    public BlogPostsForDateResourceIdentifier(String spaceKey, Calendar postingDay) {
        if (postingDay == null) {
            throw new IllegalArgumentException("Posting day cannot be null");
        }
        this.postingDay = postingDay;
        this.spaceKey = spaceKey;
    }

    public Calendar getPostingDay() {
        return this.postingDay;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public SpaceResourceIdentifier getSpace() {
        return new SpaceResourceIdentifier(this.spaceKey);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BlogPostsForDateResourceIdentifier that = (BlogPostsForDateResourceIdentifier)o;
        if (!StringUtils.equals((CharSequence)this.spaceKey, (CharSequence)that.spaceKey)) {
            return false;
        }
        if (this.postingDay == null && that.postingDay == null) {
            return true;
        }
        if (this.postingDay == null || that.postingDay == null) {
            return false;
        }
        if (this.postingDay.get(6) != that.postingDay.get(6)) {
            return false;
        }
        return this.postingDay.get(1) == that.postingDay.get(1);
    }

    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(23, 37);
        builder.append((Object)this.spaceKey);
        if (this.postingDay != null) {
            builder.append(this.postingDay.get(6)).append(this.postingDay.get(1));
        }
        return builder.toHashCode();
    }
}

