/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.util.TextUtils
 */
package org.apache.http.client;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.TextUtils;

public class HttpResponseException
extends ClientProtocolException {
    private static final long serialVersionUID = -7186627969477257933L;
    private final int statusCode;
    private final String reasonPhrase;

    public HttpResponseException(int statusCode, String reasonPhrase) {
        super(String.format("status code: %d" + (TextUtils.isBlank((CharSequence)reasonPhrase) ? "" : ", reason phrase: %s"), statusCode, reasonPhrase));
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }
}

