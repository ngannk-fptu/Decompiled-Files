/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.troubleshooting.upgrade;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="troubleshooting.upgrade.result")
public class UpgradeResultEvent {
    private final Type type;
    private final String error;

    public UpgradeResultEvent(Type type, String error) {
        this.type = type;
        this.error = error;
    }

    public String getError() {
        return this.error;
    }

    public Type getType() {
        return this.type;
    }

    public static enum Type {
        ERROR,
        FINISHED;

    }
}

