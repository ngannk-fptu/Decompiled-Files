/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import org.apache.jackrabbit.server.io.IOContext;
import org.apache.jackrabbit.server.io.IOHandler;

public interface IOListener {
    public void onBegin(IOHandler var1, IOContext var2);

    public void onEnd(IOHandler var1, IOContext var2, boolean var3);

    public void onError(IOHandler var1, IOContext var2, Exception var3);
}

