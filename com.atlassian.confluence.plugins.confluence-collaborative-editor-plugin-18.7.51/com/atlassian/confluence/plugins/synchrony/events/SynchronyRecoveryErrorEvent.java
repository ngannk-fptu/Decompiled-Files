/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.plugins.synchrony.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.annotations.Internal;

@EventName(value="confluence.synchrony.recovery.error")
@Internal
public class SynchronyRecoveryErrorEvent {
    private String recoveryState;

    public SynchronyRecoveryErrorEvent(String recoveryState) {
        this.recoveryState = recoveryState;
    }

    public String getRecoveryState() {
        return this.recoveryState;
    }
}

