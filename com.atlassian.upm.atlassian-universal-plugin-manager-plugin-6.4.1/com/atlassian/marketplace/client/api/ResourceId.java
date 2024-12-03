/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.marketplace.client.api;

import com.google.common.base.Preconditions;
import java.net.URI;
import java.util.function.Function;

public class ResourceId {
    private final URI uri;
    public static Function<ResourceId, URI> resourceIdToUriFunc = ResourceId::getUri;

    protected ResourceId(URI uri) {
        this.uri = (URI)Preconditions.checkNotNull((Object)uri);
    }

    public URI getUri() {
        return this.uri;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + this.uri + ")";
    }

    public boolean equals(Object other) {
        return other.getClass() == this.getClass() && ((ResourceId)other).uri.equals(this.uri);
    }

    public int hashCode() {
        return this.uri.hashCode();
    }
}

