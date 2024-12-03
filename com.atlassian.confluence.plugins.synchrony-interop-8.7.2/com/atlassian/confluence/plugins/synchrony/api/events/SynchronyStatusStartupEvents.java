/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.synchrony.api.events;

import com.atlassian.analytics.api.annotations.EventName;

public class SynchronyStatusStartupEvents {

    @EventName(value="confluence.synchrony.status.start.failed")
    public static class Failed {
    }

    @EventName(value="confluence.synchrony.status.start.down")
    public static class Down {
    }

    @EventName(value="confluence.synchrony.status.start.up")
    public static class Up {
    }
}

