/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.dispatcher;

import org.apache.struts2.dispatcher.Dispatcher;

public interface DispatcherListener {
    public void dispatcherInitialized(Dispatcher var1);

    public void dispatcherDestroyed(Dispatcher var1);
}

