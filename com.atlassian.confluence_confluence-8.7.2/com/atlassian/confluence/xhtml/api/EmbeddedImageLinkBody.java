/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.xhtml.api;

import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.LinkBody;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class EmbeddedImageLinkBody
implements LinkBody<EmbeddedImage> {
    private final EmbeddedImage image;

    public EmbeddedImageLinkBody(EmbeddedImage image) {
        this.image = image;
    }

    @Override
    public EmbeddedImage getBody() {
        return this.image;
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
        EmbeddedImageLinkBody rhs = (EmbeddedImageLinkBody)other;
        return new EqualsBuilder().append((Object)this.image, (Object)rhs.image).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(23, 45).append((Object)this.image).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("image", (Object)this.image).toString();
    }
}

