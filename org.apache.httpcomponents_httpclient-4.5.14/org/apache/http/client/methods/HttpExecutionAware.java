/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.concurrent.Cancellable
 */
package org.apache.http.client.methods;

import org.apache.http.concurrent.Cancellable;

public interface HttpExecutionAware {
    public boolean isAborted();

    public void setCancellable(Cancellable var1);
}

