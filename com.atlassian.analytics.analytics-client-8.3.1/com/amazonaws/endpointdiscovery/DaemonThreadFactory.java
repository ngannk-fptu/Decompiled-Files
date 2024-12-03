/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.endpointdiscovery;

import java.util.concurrent.ThreadFactory;

public class DaemonThreadFactory
implements ThreadFactory {
    public static final ThreadFactory INSTANCE = new DaemonThreadFactory();

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    }
}

