/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client;

import org.eclipse.jetty.client.api.Request;

public class HttpRequestException
extends RuntimeException {
    private final Request request;

    public HttpRequestException(String message, Request request) {
        super(message);
        this.request = request;
    }

    public Request getRequest() {
        return this.request;
    }
}

