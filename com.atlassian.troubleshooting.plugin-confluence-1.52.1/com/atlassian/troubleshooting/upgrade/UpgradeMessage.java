/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.Message
 */
package com.atlassian.troubleshooting.upgrade;

import com.atlassian.sal.api.message.Message;
import java.io.Serializable;

public class UpgradeMessage
implements Message {
    private final String key;
    private final Serializable[] args;

    private UpgradeMessage(String key, Serializable ... args) {
        this.key = key;
        this.args = args;
    }

    static UpgradeMessage upgradeMsg(String key, Serializable ... args) {
        return new UpgradeMessage(key, args);
    }

    public String getKey() {
        return this.key;
    }

    public Serializable[] getArguments() {
        return this.args;
    }
}

