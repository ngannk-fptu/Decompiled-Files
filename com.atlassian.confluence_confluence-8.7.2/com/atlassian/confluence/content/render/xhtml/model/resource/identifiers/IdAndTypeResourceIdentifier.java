/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class IdAndTypeResourceIdentifier
implements ResourceIdentifier {
    private final long id;
    private final ContentTypeEnum type;

    public IdAndTypeResourceIdentifier(long id, ContentTypeEnum type) {
        if (id <= 0L) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        this.id = id;
        this.type = type;
    }

    public long getId() {
        return this.id;
    }

    public ContentTypeEnum getType() {
        return this.type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IdAndTypeResourceIdentifier that = (IdAndTypeResourceIdentifier)o;
        if (this.id != that.id) {
            return false;
        }
        return this.type == that.type;
    }

    public int hashCode() {
        int result = (int)(this.id ^ this.id >>> 32);
        result = 31 * result + this.type.hashCode();
        return result;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

