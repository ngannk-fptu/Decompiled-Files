/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.http;

import com.atlassian.marketplace.client.MpacException;
import java.io.Closeable;
import java.io.InputStream;

public interface SimpleHttpResponse
extends Closeable {
    public int getStatus();

    public InputStream getContentStream() throws MpacException;

    public Iterable<String> getHeader(String var1);

    public boolean isEmpty();

    @Override
    public void close();
}

