/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.api.PageReader;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.google.common.base.Preconditions;
import java.net.URI;

public final class PageReference<T> {
    private final URI uri;
    private final QueryBounds bounds;
    private final PageReader<T> reader;

    public PageReference(URI uri, QueryBounds bounds, PageReader<T> reader) {
        this.uri = (URI)Preconditions.checkNotNull((Object)uri);
        this.bounds = (QueryBounds)Preconditions.checkNotNull((Object)bounds);
        this.reader = (PageReader)Preconditions.checkNotNull(reader);
    }

    public URI getUri() {
        return this.uri;
    }

    public QueryBounds getBounds() {
        return this.bounds;
    }

    public PageReader<T> getReader() {
        return this.reader;
    }

    public boolean equals(Object other) {
        if (other instanceof PageReference) {
            PageReference p = (PageReference)other;
            return this.uri.equals(p.uri) && this.bounds.equals(p.bounds);
        }
        return false;
    }

    public int hashCode() {
        return this.uri.hashCode() + this.bounds.hashCode();
    }
}

