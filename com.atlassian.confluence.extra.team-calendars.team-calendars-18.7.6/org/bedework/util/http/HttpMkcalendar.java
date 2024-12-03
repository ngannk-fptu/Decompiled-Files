/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.methods.HttpRequestBase
 */
package org.bedework.util.http;

import java.net.URI;
import org.apache.http.client.methods.HttpRequestBase;

public class HttpMkcalendar
extends HttpRequestBase {
    public static final String METHOD_NAME = "MKCALENDAR";

    public HttpMkcalendar() {
    }

    public HttpMkcalendar(URI uri) {
        this.setURI(uri);
    }

    public HttpMkcalendar(String uri) {
        this.setURI(URI.create(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }
}

