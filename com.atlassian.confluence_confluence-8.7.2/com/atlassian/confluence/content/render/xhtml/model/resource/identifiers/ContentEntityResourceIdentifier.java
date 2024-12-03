/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ContentEntityResourceIdentifier
implements ResourceIdentifier,
AttachmentContainerResourceIdentifier {
    private long contentId;

    public ContentEntityResourceIdentifier(long contentId) {
        this.contentId = contentId;
    }

    public long getContentId() {
        return this.contentId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ContentEntityResourceIdentifier that = (ContentEntityResourceIdentifier)o;
        return this.contentId == that.contentId;
    }

    public int hashCode() {
        return (int)(this.contentId ^ this.contentId >>> 32);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

