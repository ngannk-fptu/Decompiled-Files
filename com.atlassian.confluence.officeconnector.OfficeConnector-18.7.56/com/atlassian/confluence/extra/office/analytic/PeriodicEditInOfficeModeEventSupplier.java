/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.office.analytic;

import com.atlassian.confluence.extra.office.analytic.PeriodicEditInOfficeModeEvents;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PeriodicEditInOfficeModeEventSupplier
implements PeriodicEventSupplier {
    private static final String EDIT_IN_OFFICE_DARK_FEATURE_KEY = "enable.legacy.edit.in.office";
    private final DarkFeatureManager darkFeatureManager;

    @Autowired
    public PeriodicEditInOfficeModeEventSupplier(@ComponentImport DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
    }

    public PeriodicEvent call() throws Exception {
        boolean isEditInOfficeEnable = this.darkFeatureManager.isEnabledForAllUsers(EDIT_IN_OFFICE_DARK_FEATURE_KEY).orElse(false);
        return isEditInOfficeEnable ? new PeriodicEditInOfficeModeEvents.OnEvent() : new PeriodicEditInOfficeModeEvents.OffEvent();
    }
}

