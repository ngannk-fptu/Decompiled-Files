/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.methods.HttpEntityEnclosingRequestBase
 */
package com.amazonaws.http.apache.request.impl;

import java.net.URI;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class HttpGetWithBody
extends HttpEntityEnclosingRequestBase {
    public HttpGetWithBody(String uri) {
        this.setURI(URI.create(uri));
    }

    public String getMethod() {
        return "GET";
    }
}

