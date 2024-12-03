/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.classic.methods;

import java.net.URI;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

public class HttpPatch
extends HttpUriRequestBase {
    private static final long serialVersionUID = 1L;
    public static final String METHOD_NAME = "PATCH";

    public HttpPatch(URI uri) {
        super(METHOD_NAME, uri);
    }

    public HttpPatch(String uri) {
        this(URI.create(uri));
    }
}

