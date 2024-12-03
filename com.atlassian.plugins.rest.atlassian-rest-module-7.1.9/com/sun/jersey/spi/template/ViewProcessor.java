/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.template;

import com.sun.jersey.api.view.Viewable;
import java.io.IOException;
import java.io.OutputStream;

public interface ViewProcessor<T> {
    public T resolve(String var1);

    public void writeTo(T var1, Viewable var2, OutputStream var3) throws IOException;
}

