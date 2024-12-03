/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.methods.HttpEntityEnclosingRequestBase
 */
package org.bedework.util.http;

import java.net.URI;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class HttpPropfind
extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "PROPFIND";

    public HttpPropfind() {
    }

    public HttpPropfind(URI uri) {
        this.setURI(uri);
    }

    public HttpPropfind(String uri) {
        this.setURI(URI.create(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }
}

