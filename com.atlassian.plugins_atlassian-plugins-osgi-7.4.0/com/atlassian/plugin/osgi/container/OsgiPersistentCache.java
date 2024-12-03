/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.osgi.container;

import java.io.File;

public interface OsgiPersistentCache {
    public File getFrameworkBundleCache();

    public File getOsgiBundleCache();

    public File getTransformedPluginCache();

    public void clear();

    public void validate(String var1);
}

