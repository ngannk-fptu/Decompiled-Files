/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.client.async;

import org.apache.axis.client.async.Status;

public interface IAsyncResult {
    public void abort();

    public Status getStatus();

    public void waitFor(long var1) throws InterruptedException;

    public Object getResponse();

    public Throwable getException();
}

