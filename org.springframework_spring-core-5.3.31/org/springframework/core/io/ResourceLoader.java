/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.io;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public interface ResourceLoader {
    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    public Resource getResource(String var1);

    @Nullable
    public ClassLoader getClassLoader();
}

