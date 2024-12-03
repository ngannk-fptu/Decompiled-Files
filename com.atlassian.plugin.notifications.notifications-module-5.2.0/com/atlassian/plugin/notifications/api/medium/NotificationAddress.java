/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.fugue.Option;

public interface NotificationAddress {
    public Option<String> getMediumKey();

    public String getAddressData();
}

