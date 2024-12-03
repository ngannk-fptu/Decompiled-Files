/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.cache.filecache;

import java.io.OutputStream;

public interface Cache {
    public boolean cache(String var1, String var2, OutputStream var3, StreamProvider var4);

    public boolean cacheTwo(String var1, String var2, OutputStream var3, OutputStream var4, TwoStreamProvider var5);

    public void clear();

    public static interface TwoStreamProvider {
        public void write(OutputStream var1, OutputStream var2);
    }

    public static interface StreamProvider {
        public void write(OutputStream var1);
    }
}

