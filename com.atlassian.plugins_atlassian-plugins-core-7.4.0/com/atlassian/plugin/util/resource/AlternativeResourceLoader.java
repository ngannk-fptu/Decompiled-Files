/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util.resource;

import java.io.InputStream;
import java.net.URL;

public interface AlternativeResourceLoader {
    public URL getResource(String var1);

    public InputStream getResourceAsStream(String var1);
}

