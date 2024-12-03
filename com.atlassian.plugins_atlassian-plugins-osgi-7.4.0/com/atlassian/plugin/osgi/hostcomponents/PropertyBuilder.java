/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.osgi.hostcomponents;

import com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy;

public interface PropertyBuilder {
    public static final String BEAN_NAME = "bean-name";
    public static final String CONTEXT_CLASS_LOADER_STRATEGY = "context-class-loader-strategy";
    public static final String TRACK_BUNDLE = "track-bundle";

    public PropertyBuilder withName(String var1);

    public PropertyBuilder withContextClassLoaderStrategy(ContextClassLoaderStrategy var1);

    public PropertyBuilder withTrackBundleEnabled(boolean var1);

    public PropertyBuilder withProperty(String var1, String var2);
}

