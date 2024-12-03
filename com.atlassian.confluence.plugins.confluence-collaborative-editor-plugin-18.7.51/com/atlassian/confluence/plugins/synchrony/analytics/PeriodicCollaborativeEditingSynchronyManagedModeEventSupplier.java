/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.analytics;

import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier;
import com.atlassian.confluence.plugins.synchrony.analytics.PeriodicCollaborativeEditingSynchronyManagedModeEvents;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class PeriodicCollaborativeEditingSynchronyManagedModeEventSupplier
implements PeriodicEventSupplier {
    public PeriodicEvent call() {
        if (StringUtils.isNotBlank((CharSequence)System.getProperty("synchrony.service.url"))) {
            return new PeriodicCollaborativeEditingSynchronyManagedModeEvents.OnEvent();
        }
        return new PeriodicCollaborativeEditingSynchronyManagedModeEvents.OffEvent();
    }
}

