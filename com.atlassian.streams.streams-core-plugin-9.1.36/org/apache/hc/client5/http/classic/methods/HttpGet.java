/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.classic.methods;

import java.net.URI;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

public class HttpGet
extends HttpUriRequestBase {
    private static final long serialVersionUID = 1L;
    public static final String METHOD_NAME = "GET";

    public HttpGet(URI uri) {
        super(METHOD_NAME, uri);
    }

    public HttpGet(String uri) {
        this(URI.create(uri));
    }
}

