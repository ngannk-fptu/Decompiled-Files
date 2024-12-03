/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.johnson.event.Event
 */
package com.atlassian.confluence.internal.health.analytics;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.johnson.event.Event;

@ParametersAreNonnullByDefault
public interface HealthCheckAnalyticsSender {
    public void sendHealthCheckResult(Event var1);

    public void sendHelpLinkClickedForEvent(String var1);

    public void sendGeneralHelpLinkClicked(String var1);
}

