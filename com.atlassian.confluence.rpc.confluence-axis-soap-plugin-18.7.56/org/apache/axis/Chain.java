/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis;

import org.apache.axis.Handler;

public interface Chain
extends Handler {
    public void addHandler(Handler var1);

    public boolean contains(Handler var1);

    public Handler[] getHandlers();
}

