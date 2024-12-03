/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import org.apache.jackrabbit.server.io.IOListener;

public interface IOContext {
    public IOListener getIOListener();

    public boolean hasStream();

    public void informCompleted(boolean var1);

    public boolean isCompleted();
}

