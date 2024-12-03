/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;

public class DefaultNotificationAddress
implements NotificationAddress {
    private final String addressData;
    private final Option<String> key;

    public DefaultNotificationAddress(Option<String> key, String addressData) {
        this.key = key;
        this.addressData = addressData;
    }

    @Override
    public Option<String> getMediumKey() {
        return this.key;
    }

    @Override
    public String getAddressData() {
        return this.addressData;
    }
}

