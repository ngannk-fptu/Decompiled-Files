/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.MpacException;
import java.io.InputStream;
import java.io.OutputStream;

public interface EntityEncoding {
    public <T> T decode(InputStream var1, Class<T> var2) throws MpacException;

    public <T> void encode(OutputStream var1, T var2, boolean var3) throws MpacException;

    public <T> void encodeChanges(OutputStream var1, T var2, T var3) throws MpacException;
}

