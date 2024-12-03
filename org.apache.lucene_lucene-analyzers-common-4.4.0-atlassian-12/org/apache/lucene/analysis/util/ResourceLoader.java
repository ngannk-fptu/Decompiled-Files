/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.util;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceLoader {
    public InputStream openResource(String var1) throws IOException;

    public <T> Class<? extends T> findClass(String var1, Class<T> var2);

    public <T> T newInstance(String var1, Class<T> var2);
}

