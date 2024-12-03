/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpEntity
 */
package org.apache.hc.client5.http.classic.methods;

import java.net.URI;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.HttpEntity;

public class HttpTrace
extends HttpUriRequestBase {
    private static final long serialVersionUID = 1L;
    public static final String METHOD_NAME = "TRACE";

    public HttpTrace(URI uri) {
        super(METHOD_NAME, uri);
    }

    public HttpTrace(String uri) {
        this(URI.create(uri));
    }

    public void setEntity(HttpEntity entity) {
        throw new IllegalStateException("TRACE requests may not include an entity.");
    }
}

