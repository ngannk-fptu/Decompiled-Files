/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

public interface TaskLifecycleListener {
    public void onInit();

    public void onBeforeRun();

    public void onAfterRun();
}

