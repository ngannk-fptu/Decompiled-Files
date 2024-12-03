/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.component;

import org.eclipse.jetty.util.component.LifeCycle;

public class HaltLifeCycleListener
implements LifeCycle.Listener {
    @Override
    public void lifeCycleStarted(LifeCycle lifecycle) {
        Runtime.getRuntime().halt(0);
    }
}

