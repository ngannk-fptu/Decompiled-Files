/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.DefaultNotificationAddress
 */
package com.atlassian.confluence.plugins.sharepage.notifications.context;

import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.DefaultNotificationAddress;

public class ShareNotificationAddress
extends DefaultNotificationAddress {
    private final String groupName;

    public ShareNotificationAddress(Option<String> key, String addressData, String groupName) {
        super(key, addressData);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return this.groupName;
    }
}

