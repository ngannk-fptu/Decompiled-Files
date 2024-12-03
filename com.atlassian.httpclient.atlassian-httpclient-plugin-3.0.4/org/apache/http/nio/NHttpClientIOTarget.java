/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio;

import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.NHttpClientHandler;

@Deprecated
public interface NHttpClientIOTarget
extends NHttpClientConnection {
    public void consumeInput(NHttpClientHandler var1);

    public void produceOutput(NHttpClientHandler var1);
}

