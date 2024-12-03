/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext
 */
package org.eclipse.gemini.blueprint.extender.internal.activator;

import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.extender.internal.activator.OsgiContextProcessor;

public class NoOpOsgiContextProcessor
implements OsgiContextProcessor {
    @Override
    public void postProcessClose(ConfigurableOsgiBundleApplicationContext context) {
    }

    @Override
    public void postProcessRefresh(ConfigurableOsgiBundleApplicationContext context) {
    }

    @Override
    public void postProcessRefreshFailure(ConfigurableOsgiBundleApplicationContext localApplicationContext, Throwable th) {
    }

    @Override
    public void preProcessClose(ConfigurableOsgiBundleApplicationContext context) {
    }

    @Override
    public void preProcessRefresh(ConfigurableOsgiBundleApplicationContext context) {
    }
}

