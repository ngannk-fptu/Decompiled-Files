/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.methods.HttpEntityEnclosingRequestBase
 */
package org.bedework.util.http;

import java.net.URI;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class HttpMkcol
extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "MKCOL";

    public HttpMkcol() {
    }

    public HttpMkcol(URI uri) {
        this.setURI(uri);
    }

    public HttpMkcol(String uri) {
        this.setURI(URI.create(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }
}

