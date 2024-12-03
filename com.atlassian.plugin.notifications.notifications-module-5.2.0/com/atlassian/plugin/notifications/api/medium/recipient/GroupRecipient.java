/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.api.medium.recipient;

public interface GroupRecipient {
    public String getName();

    public int getServerId();

    public String getParamValue();

    public String getParamDisplay();
}

