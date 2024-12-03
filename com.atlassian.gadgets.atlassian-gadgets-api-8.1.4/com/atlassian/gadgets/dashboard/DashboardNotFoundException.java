/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.dashboard;

import com.atlassian.gadgets.dashboard.DashboardId;

public class DashboardNotFoundException
extends RuntimeException {
    private final DashboardId dashboardId;

    public DashboardNotFoundException(DashboardId id) {
        this.dashboardId = id;
    }

    public DashboardId getDashboardId() {
        return this.dashboardId;
    }
}

