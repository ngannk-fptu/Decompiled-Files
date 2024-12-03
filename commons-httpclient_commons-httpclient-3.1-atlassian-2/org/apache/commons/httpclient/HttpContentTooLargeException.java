/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import org.apache.commons.httpclient.HttpException;

public class HttpContentTooLargeException
extends HttpException {
    private int maxlen;

    public HttpContentTooLargeException(String message, int maxlen) {
        super(message);
        this.maxlen = maxlen;
    }

    public int getMaxLength() {
        return this.maxlen;
    }
}

