/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.message;

import java.util.Locale;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.message.AbstractMessageWrapper;

public class HttpResponseWrapper
extends AbstractMessageWrapper<HttpResponse>
implements HttpResponse {
    public HttpResponseWrapper(HttpResponse message) {
        super(message);
    }

    @Override
    public int getCode() {
        return ((HttpResponse)this.getMessage()).getCode();
    }

    @Override
    public void setCode(int code) {
        ((HttpResponse)this.getMessage()).setCode(code);
    }

    @Override
    public String getReasonPhrase() {
        return ((HttpResponse)this.getMessage()).getReasonPhrase();
    }

    @Override
    public void setReasonPhrase(String reason) {
        ((HttpResponse)this.getMessage()).setReasonPhrase(reason);
    }

    @Override
    public Locale getLocale() {
        return ((HttpResponse)this.getMessage()).getLocale();
    }

    @Override
    public void setLocale(Locale loc) {
        ((HttpResponse)this.getMessage()).setLocale(loc);
    }
}

