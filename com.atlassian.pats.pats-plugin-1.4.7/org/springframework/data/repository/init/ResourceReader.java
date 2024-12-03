/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.repository.init;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public interface ResourceReader {
    public Object readFrom(Resource var1, @Nullable ClassLoader var2) throws Exception;

    public static enum Type {
        XML,
        JSON;

    }
}

