/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util.resource;

import com.atlassian.plugin.util.resource.AlternativeResourceLoader;
import java.io.InputStream;
import java.net.URL;

public class NoOpAlternativeResourceLoader
implements AlternativeResourceLoader {
    @Override
    public URL getResource(String path) {
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return null;
    }
}

