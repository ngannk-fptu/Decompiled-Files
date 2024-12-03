/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.extra.masterdetail;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.pageproperties.api.model.PageProperty;
import java.util.Map;

public class ExtractedDetails {
    private final ContentEntityObject content;
    private final Map<String, PageProperty> details;

    public ExtractedDetails(ContentEntityObject content, Map<String, PageProperty> details) {
        this.content = content;
        this.details = details;
    }

    public String getTitle() {
        if (this.content != null) {
            return this.content.getTitle();
        }
        return null;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }

    public Map<String, PageProperty> getDetails() {
        return this.details;
    }

    public String getDetailStorageFormat(String heading) {
        if (this.details == null) {
            return "";
        }
        PageProperty pageProperty = this.details.get(heading);
        return pageProperty == null ? "" : pageProperty.getDetailStorageFormat();
    }
}

