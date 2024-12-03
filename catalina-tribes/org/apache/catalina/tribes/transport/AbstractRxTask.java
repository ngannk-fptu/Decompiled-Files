/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.transport;

import org.apache.catalina.tribes.io.ListenCallback;
import org.apache.catalina.tribes.transport.RxTaskPool;

public abstract class AbstractRxTask
implements Runnable {
    public static final int OPTION_DIRECT_BUFFER = 4;
    private ListenCallback callback;
    private RxTaskPool pool;
    @Deprecated
    private boolean doRun = true;
    private int options;
    protected boolean useBufferPool = true;

    public AbstractRxTask(ListenCallback callback) {
        this.callback = callback;
    }

    public void setTaskPool(RxTaskPool pool) {
        this.pool = pool;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    public void setCallback(ListenCallback callback) {
        this.callback = callback;
    }

    @Deprecated
    public void setDoRun(boolean doRun) {
        this.doRun = doRun;
    }

    public RxTaskPool getTaskPool() {
        return this.pool;
    }

    public int getOptions() {
        return this.options;
    }

    public ListenCallback getCallback() {
        return this.callback;
    }

    @Deprecated
    public boolean isDoRun() {
        return this.doRun;
    }

    public void close() {
        this.doRun = false;
    }

    public void setUseBufferPool(boolean usebufpool) {
        this.useBufferPool = usebufpool;
    }

    public boolean getUseBufferPool() {
        return this.useBufferPool;
    }
}

