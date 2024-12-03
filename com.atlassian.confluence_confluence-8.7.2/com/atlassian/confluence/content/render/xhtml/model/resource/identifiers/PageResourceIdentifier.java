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
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PageResourceIdentifier
implements AttachmentContainerResourceIdentifier,
NamedResourceIdentifier {
    private final String spaceKey;
    private final String title;

    public PageResourceIdentifier(String title) {
        this(null, title);
    }

    public PageResourceIdentifier(String spaceKey, String title) {
        if (StringUtils.isBlank((CharSequence)title)) {
            throw new IllegalArgumentException("Title must not be null or empty.");
        }
        this.spaceKey = spaceKey;
        this.title = title;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public String getResourceName() {
        return this.title;
    }

    public boolean isPopulated() {
        return StringUtils.isNotBlank((CharSequence)this.spaceKey) || StringUtils.isNotBlank((CharSequence)this.title);
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
        PageResourceIdentifier rhs = (PageResourceIdentifier)o;
        return new EqualsBuilder().append((Object)this.spaceKey, (Object)rhs.spaceKey).append((Object)this.title, (Object)rhs.title).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(19, 31).append((Object)this.spaceKey).append((Object)this.title).toHashCode();
    }
}

