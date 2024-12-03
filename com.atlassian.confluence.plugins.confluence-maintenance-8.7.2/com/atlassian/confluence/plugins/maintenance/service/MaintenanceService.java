/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 */
package com.atlassian.confluence.plugins.maintenance.service;

import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.plugins.maintenance.model.Addon;
import com.atlassian.confluence.plugins.maintenance.model.MaintenanceInfo;
import java.util.List;

@ReturnValuesAreNonnullByDefault
public interface MaintenanceService {
    public static final String READ_ONLY_ACCESS_MODE_COMPATIBLE = "read-only-access-mode-compatible";

    public List<Addon> getUserInstalledAddons();

    public void updateMaintenanceInfo(MaintenanceInfo var1);

    public MaintenanceInfo getMaintenanceInfo();
}

