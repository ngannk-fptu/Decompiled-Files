/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.est;

import java.net.URL;
import org.bouncycastle.est.ESTClient;
import org.bouncycastle.est.ESTHijacker;
import org.bouncycastle.est.ESTRequest;
import org.bouncycastle.est.ESTSourceConnectionListener;
import org.bouncycastle.est.HttpUtil;
import org.bouncycastle.util.Arrays;

public class ESTRequestBuilder {
    private final String method;
    private URL url;
    private HttpUtil.Headers headers;
    ESTHijacker hijacker;
    ESTSourceConnectionListener listener;
    ESTClient client;
    private byte[] data;

    public ESTRequestBuilder(ESTRequest request) {
        this.method = request.method;
        this.url = request.url;
        this.listener = request.listener;
        this.data = request.data;
        this.hijacker = request.hijacker;
        this.headers = (HttpUtil.Headers)request.headers.clone();
        this.client = request.getClient();
    }

    public ESTRequestBuilder(String method, URL url) {
        this.method = method;
        this.url = url;
        this.headers = new HttpUtil.Headers();
    }

    public ESTRequestBuilder withConnectionListener(ESTSourceConnectionListener listener) {
        this.listener = listener;
        return this;
    }

    public ESTRequestBuilder withHijacker(ESTHijacker hijacker) {
        this.hijacker = hijacker;
        return this;
    }

    public ESTRequestBuilder withURL(URL url) {
        this.url = url;
        return this;
    }

    public ESTRequestBuilder withData(byte[] data) {
        this.data = Arrays.clone((byte[])data);
        return this;
    }

    public ESTRequestBuilder addHeader(String key, String value) {
        this.headers.add(key, value);
        return this;
    }

    public ESTRequestBuilder setHeader(String key, String value) {
        this.headers.set(key, value);
        return this;
    }

    public ESTRequestBuilder withClient(ESTClient client) {
        this.client = client;
        return this;
    }

    public ESTRequest build() {
        return new ESTRequest(this.method, this.url, this.data, this.hijacker, this.listener, this.headers, this.client);
    }
}

