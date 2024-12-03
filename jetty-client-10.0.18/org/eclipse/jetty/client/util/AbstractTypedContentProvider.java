/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client.util;

import org.eclipse.jetty.client.api.ContentProvider;

@Deprecated
public abstract class AbstractTypedContentProvider
implements ContentProvider.Typed {
    private final String contentType;

    protected AbstractTypedContentProvider(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }
}

