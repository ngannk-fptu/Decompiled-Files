/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.event.ComponentManagerShutdownEvent
 */
package com.atlassian.analytics.client.detect;

import com.atlassian.analytics.client.detect.SystemShutdownDetector;
import com.atlassian.jira.event.ComponentManagerShutdownEvent;

public class JiraSystemShutdownDetector
implements SystemShutdownDetector {
    @Override
    public boolean isShuttingDown(Object event) {
        return event instanceof ComponentManagerShutdownEvent;
    }
}

