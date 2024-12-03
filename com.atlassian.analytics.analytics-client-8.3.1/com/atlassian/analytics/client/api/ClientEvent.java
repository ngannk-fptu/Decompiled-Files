/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.api;

import java.util.Map;

public interface ClientEvent {
    public String getName();

    public Map<String, Object> getProperties();

    public long getClientTime();
}

