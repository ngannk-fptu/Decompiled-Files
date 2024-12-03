/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.apache.httpcomponents;

import java.io.IOException;
import org.apache.http.HttpResponse;

public class EntityTooLargeException
extends IOException {
    private final HttpResponse response;

    public EntityTooLargeException(HttpResponse response, String message) {
        super(message);
        this.response = response;
    }

    public HttpResponse getResponse() {
        return this.response;
    }
}

