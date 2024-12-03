/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.spi;

import java.util.concurrent.Future;

public interface HotRestartService {
    public Future<?> doHotRestart();
}

