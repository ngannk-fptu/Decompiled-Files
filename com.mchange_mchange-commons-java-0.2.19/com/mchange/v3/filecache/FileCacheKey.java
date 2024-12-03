/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v3.filecache;

import java.net.URL;

public interface FileCacheKey {
    public URL getURL();

    public String getCacheFilePath();

    public boolean equals(Object var1);

    public int hashCode();
}

