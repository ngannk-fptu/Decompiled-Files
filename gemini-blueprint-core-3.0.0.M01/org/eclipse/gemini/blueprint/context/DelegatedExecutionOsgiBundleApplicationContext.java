/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.context;

import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.OsgiBundleApplicationContextExecutor;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticaster;

public interface DelegatedExecutionOsgiBundleApplicationContext
extends ConfigurableOsgiBundleApplicationContext {
    public void normalRefresh();

    public void normalClose();

    public void startRefresh();

    public void completeRefresh();

    public void setExecutor(OsgiBundleApplicationContextExecutor var1);

    public void setDelegatedEventMulticaster(OsgiBundleApplicationContextEventMulticaster var1);

    public OsgiBundleApplicationContextEventMulticaster getDelegatedEventMulticaster();
}

