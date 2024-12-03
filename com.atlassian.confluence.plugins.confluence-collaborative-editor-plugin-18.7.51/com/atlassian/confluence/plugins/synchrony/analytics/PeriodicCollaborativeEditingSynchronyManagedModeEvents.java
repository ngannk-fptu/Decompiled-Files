/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent
 */
package com.atlassian.confluence.plugins.synchrony.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;

class PeriodicCollaborativeEditingSynchronyManagedModeEvents {
    PeriodicCollaborativeEditingSynchronyManagedModeEvents() {
    }

    @EventName(value="confluence.collaborative.editing.synchrony.managed.mode.periodic.off")
    static class OffEvent
    implements PeriodicEvent {
        OffEvent() {
        }
    }

    @EventName(value="confluence.collaborative.editing.synchrony.managed.mode.periodic.on")
    static class OnEvent
    implements PeriodicEvent {
        OnEvent() {
        }
    }
}

