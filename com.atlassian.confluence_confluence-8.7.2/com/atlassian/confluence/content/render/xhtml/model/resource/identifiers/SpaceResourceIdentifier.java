/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import org.apache.commons.lang3.StringUtils;

public class SpaceResourceIdentifier
implements ResourceIdentifier {
    private final String spaceKey;

    public SpaceResourceIdentifier(String spaceKey) {
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            throw new IllegalArgumentException("spaceKey cannot be null or blank");
        }
        this.spaceKey = spaceKey;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SpaceResourceIdentifier that = (SpaceResourceIdentifier)o;
        return this.spaceKey.equals(that.spaceKey);
    }

    public int hashCode() {
        return this.spaceKey.hashCode();
    }
}

