/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WikiLinkBasedResourceIdentifier
implements ResourceIdentifier {
    private final String originalLinkText;

    public WikiLinkBasedResourceIdentifier(String originalLinkText) {
        if (StringUtils.isBlank((CharSequence)originalLinkText)) {
            throw new IllegalArgumentException("originalLinkText must be supplied.");
        }
        this.originalLinkText = originalLinkText;
    }

    public String getOriginalLinkText() {
        return this.originalLinkText;
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
        WikiLinkBasedResourceIdentifier that = (WikiLinkBasedResourceIdentifier)o;
        return this.originalLinkText.equals(that.originalLinkText);
    }

    public int hashCode() {
        return this.originalLinkText.hashCode();
    }
}

