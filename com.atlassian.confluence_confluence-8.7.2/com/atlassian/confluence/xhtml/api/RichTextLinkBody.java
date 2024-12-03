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
package com.atlassian.confluence.xhtml.api;

import com.atlassian.confluence.xhtml.api.LinkBody;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RichTextLinkBody
implements LinkBody<String> {
    private final String body;

    public RichTextLinkBody(String body) {
        if (StringUtils.isBlank((CharSequence)body)) {
            throw new IllegalArgumentException();
        }
        this.body = body;
    }

    @Override
    public String getBody() {
        return this.body;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (other.getClass() != this.getClass()) {
            return false;
        }
        RichTextLinkBody rhs = (RichTextLinkBody)other;
        return new EqualsBuilder().append((Object)this.body, (Object)rhs.body).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(23, 45).append((Object)this.body).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder((Object)this, ToStringStyle.SHORT_PREFIX_STYLE).append("body", (Object)this.body).toString();
    }
}

