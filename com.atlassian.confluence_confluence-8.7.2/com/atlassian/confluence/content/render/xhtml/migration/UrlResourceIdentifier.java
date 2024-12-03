/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UrlResourceIdentifier
implements NamedResourceIdentifier {
    private final String url;

    public UrlResourceIdentifier(String url) {
        this.url = url;
    }

    @Override
    public String getResourceName() {
        return StringUtils.substringAfterLast((String)this.url, (String)"/");
    }

    public String getUrl() {
        return this.url;
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
        UrlResourceIdentifier that = (UrlResourceIdentifier)o;
        return !(this.url != null ? !this.url.equals(that.url) : that.url != null);
    }

    public int hashCode() {
        return this.url != null ? this.url.hashCode() : 0;
    }
}

