/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.http.client.methods.HttpPost
 */
package com.atlassian.marketplace.client.http;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.http.RequestDecorator;
import com.atlassian.marketplace.client.http.SimpleHttpResponse;
import com.google.common.collect.Multimap;
import java.io.Closeable;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.http.client.methods.HttpPost;

@ParametersAreNonnullByDefault
public interface HttpTransport
extends Closeable {
    public SimpleHttpResponse get(URI var1) throws MpacException;

    public SimpleHttpResponse postParams(URI var1, Multimap<String, String> var2) throws MpacException;

    public SimpleHttpResponse post(URI var1, InputStream var2, long var3, String var5, String var6, Optional<Consumer<HttpPost>> var7) throws MpacException;

    public SimpleHttpResponse put(URI var1, byte[] var2) throws MpacException;

    public SimpleHttpResponse patch(URI var1, byte[] var2) throws MpacException;

    public SimpleHttpResponse delete(URI var1) throws MpacException;

    public HttpTransport withRequestDecorator(RequestDecorator var1);
}

