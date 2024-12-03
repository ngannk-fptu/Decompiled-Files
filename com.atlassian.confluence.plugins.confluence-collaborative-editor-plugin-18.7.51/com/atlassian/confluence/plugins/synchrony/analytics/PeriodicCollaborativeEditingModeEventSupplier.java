/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.analytics;

import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier;
import com.atlassian.confluence.plugins.synchrony.analytics.PeriodicCollaborativeEditingModeEvents;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PeriodicCollaborativeEditingModeEventSupplier
implements PeriodicEventSupplier {
    private final SynchronyConfigurationManager configurationManager;

    @Autowired
    PeriodicCollaborativeEditingModeEventSupplier(SynchronyConfigurationManager synchronyConfigurationManager) {
        this.configurationManager = Objects.requireNonNull(synchronyConfigurationManager);
    }

    public PeriodicEvent call() throws Exception {
        if (this.configurationManager.isSharedDraftsEnabled()) {
            return new PeriodicCollaborativeEditingModeEvents.OnEvent();
        }
        return new PeriodicCollaborativeEditingModeEvents.OffEvent();
    }
}

