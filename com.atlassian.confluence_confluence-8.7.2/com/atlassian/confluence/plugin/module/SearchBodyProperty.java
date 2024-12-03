/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 */
package com.atlassian.confluence.plugin.module;

import com.atlassian.confluence.api.model.content.ContentType;

public class SearchBodyProperty {
    private ContentType contentType;
    private String contentProperty;

    public SearchBodyProperty(ContentType contentType, String contentProperty) {
        this.contentType = contentType;
        this.contentProperty = contentProperty;
    }

    public ContentType getContentType() {
        return this.contentType;
    }

    public String getContentProperty() {
        return this.contentProperty;
    }
}

