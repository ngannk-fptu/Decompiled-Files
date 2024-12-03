/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext
 */
package org.eclipse.gemini.blueprint.extender.internal.activator;

import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;

public interface OsgiContextProcessor {
    public void preProcessRefresh(ConfigurableOsgiBundleApplicationContext var1);

    public void postProcessRefresh(ConfigurableOsgiBundleApplicationContext var1);

    public void postProcessRefreshFailure(ConfigurableOsgiBundleApplicationContext var1, Throwable var2);

    public void preProcessClose(ConfigurableOsgiBundleApplicationContext var1);

    public void postProcessClose(ConfigurableOsgiBundleApplicationContext var1);
}

