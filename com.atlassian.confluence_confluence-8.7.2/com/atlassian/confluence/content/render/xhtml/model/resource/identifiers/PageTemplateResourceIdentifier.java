/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;

public class PageTemplateResourceIdentifier
implements ResourceIdentifier {
    private final long templateId;

    public PageTemplateResourceIdentifier(long templateId) {
        this.templateId = templateId;
    }

    public long getTemplateId() {
        return this.templateId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PageTemplateResourceIdentifier)) {
            return false;
        }
        return this.templateId == ((PageTemplateResourceIdentifier)o).templateId;
    }

    public int hashCode() {
        return Long.hashCode(this.templateId);
    }
}

