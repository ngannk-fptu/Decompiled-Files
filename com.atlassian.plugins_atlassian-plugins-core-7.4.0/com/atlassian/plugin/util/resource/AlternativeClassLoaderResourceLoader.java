/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.util.resource;

import com.atlassian.plugin.util.resource.AlternativeResourceLoader;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.net.URL;

public final class AlternativeClassLoaderResourceLoader
implements AlternativeResourceLoader {
    private final Class<?> clazz;

    public AlternativeClassLoaderResourceLoader() {
        this(AlternativeClassLoaderResourceLoader.class);
    }

    public AlternativeClassLoaderResourceLoader(Class<?> clazz) {
        this.clazz = (Class)Preconditions.checkNotNull(clazz);
    }

    @Override
    public URL getResource(String path) {
        return this.clazz.getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return this.clazz.getResourceAsStream(name);
    }
}

