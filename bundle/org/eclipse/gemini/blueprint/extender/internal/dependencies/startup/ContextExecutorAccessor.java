/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticaster
 */
package org.eclipse.gemini.blueprint.extender.internal.dependencies.startup;

import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticaster;
import org.eclipse.gemini.blueprint.extender.internal.dependencies.startup.ContextState;

public interface ContextExecutorAccessor {
    public ContextState getContextState();

    public OsgiBundleApplicationContextEventMulticaster getEventMulticaster();

    public void fail(Throwable var1);
}

