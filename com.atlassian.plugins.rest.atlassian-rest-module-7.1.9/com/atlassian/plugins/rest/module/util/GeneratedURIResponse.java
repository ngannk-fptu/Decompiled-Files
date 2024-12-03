/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.module.util;

import java.net.URI;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

class GeneratedURIResponse
extends Response {
    private URI uri;

    GeneratedURIResponse(URI uri) {
        this.uri = uri;
    }

    public URI getURI() {
        return this.uri;
    }

    public String toString() {
        return this.uri.toString();
    }

    @Override
    public Object getEntity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        throw new UnsupportedOperationException();
    }
}

