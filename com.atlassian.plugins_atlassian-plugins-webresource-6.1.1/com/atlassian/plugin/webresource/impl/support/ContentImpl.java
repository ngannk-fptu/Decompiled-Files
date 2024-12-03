/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.support;

import com.atlassian.plugin.webresource.impl.support.Content;

public abstract class ContentImpl
implements Content {
    private String contentType;
    private boolean isTransformed;

    public ContentImpl(String contentType, boolean isTransformed) {
        this.contentType = contentType;
        this.isTransformed = isTransformed;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isTransformed() {
        return this.isTransformed;
    }
}

