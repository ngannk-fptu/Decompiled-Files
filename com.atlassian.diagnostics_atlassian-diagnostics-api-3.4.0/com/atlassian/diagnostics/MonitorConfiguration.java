/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics;

public interface MonitorConfiguration {
    public boolean isEnabled();

    default public boolean isDataCenterOnly() {
        return false;
    }
}

