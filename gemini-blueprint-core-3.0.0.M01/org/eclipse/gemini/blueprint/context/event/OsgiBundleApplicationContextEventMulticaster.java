/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.context.event;

import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;

public interface OsgiBundleApplicationContextEventMulticaster {
    public void addApplicationListener(OsgiBundleApplicationContextListener var1);

    public void removeApplicationListener(OsgiBundleApplicationContextListener var1);

    public void removeAllListeners();

    public void multicastEvent(OsgiBundleApplicationContextEvent var1);
}

