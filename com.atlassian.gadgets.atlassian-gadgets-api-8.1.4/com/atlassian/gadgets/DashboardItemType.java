/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.gadgets;

import io.atlassian.fugue.Option;

public enum DashboardItemType {
    LOCAL_DASHBOARD_ITEM("dashboard-item"),
    OPEN_SOCIAL_GADGET("gadget");

    private final String name;

    private DashboardItemType(String name) {
        this.name = name;
    }

    public static Option<DashboardItemType> forName(String name) {
        for (DashboardItemType type : DashboardItemType.values()) {
            if (!type.getName().equals(name)) continue;
            return Option.some((Object)((Object)type));
        }
        return Option.none();
    }

    public String getName() {
        return this.name;
    }

    public boolean isOpenSocialGadget() {
        return this == OPEN_SOCIAL_GADGET;
    }
}

