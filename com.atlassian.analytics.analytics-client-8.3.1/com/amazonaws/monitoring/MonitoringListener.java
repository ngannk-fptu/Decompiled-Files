/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.monitoring;

import com.amazonaws.monitoring.MonitoringEvent;

public abstract class MonitoringListener {
    public abstract void handleEvent(MonitoringEvent var1);
}

