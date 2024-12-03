/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStoppingEvent
 */
package com.atlassian.analytics.client.detect;

import com.atlassian.analytics.client.detect.SystemShutdownDetector;
import com.atlassian.config.lifecycle.events.ApplicationStoppingEvent;

public class DefaultSystemShutdownDetector
implements SystemShutdownDetector {
    @Override
    public boolean isShuttingDown(Object event) {
        return event instanceof ApplicationStoppingEvent;
    }
}

