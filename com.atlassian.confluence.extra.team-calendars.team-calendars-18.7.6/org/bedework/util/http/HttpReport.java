/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.methods.HttpEntityEnclosingRequestBase
 */
package org.bedework.util.http;

import java.net.URI;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class HttpReport
extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "REPORT";

    public HttpReport() {
    }

    public HttpReport(URI uri) {
        this.setURI(uri);
    }

    public HttpReport(String uri) {
        this.setURI(URI.create(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }
}

