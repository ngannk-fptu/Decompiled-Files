/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws;

import com.amazonaws.ImmutableRequest;
import java.io.InputStream;

public interface SignableRequest<T>
extends ImmutableRequest<T> {
    public void addHeader(String var1, String var2);

    public void addParameter(String var1, String var2);

    public void setContent(InputStream var1);
}

