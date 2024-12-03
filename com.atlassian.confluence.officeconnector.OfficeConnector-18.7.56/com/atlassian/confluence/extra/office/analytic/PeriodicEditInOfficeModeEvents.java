/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent
 */
package com.atlassian.confluence.extra.office.analytic;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;

public class PeriodicEditInOfficeModeEvents {

    @EventName(value="confluence.edit.in.office.mode.periodic.off")
    static class OffEvent
    implements PeriodicEvent {
        OffEvent() {
        }
    }

    @EventName(value="confluence.edit.in.office.mode.periodic.on")
    static class OnEvent
    implements PeriodicEvent {
        OnEvent() {
        }
    }
}

