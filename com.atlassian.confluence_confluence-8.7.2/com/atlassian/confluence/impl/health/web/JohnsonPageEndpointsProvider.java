/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.health.web;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public final class JohnsonPageEndpointsProvider {
    private static final Map<String, String> ENDPOINTS = ImmutableMap.of((Object)"data", (Object)"/johnson/data", (Object)"eventKbClickedAnalytics", (Object)"/johnson/analytics/kb/event", (Object)"generalKbClickedAnalytics", (Object)"/johnson/analytics/kb/general", (Object)"home", (Object)"/", (Object)"dismissEvents", (Object)"/johnson/events/dismiss");

    public static @NonNull Json getEndpoints(String contextPath) {
        JsonObject json = new JsonObject();
        ENDPOINTS.forEach((key, unpathedUrl) -> json.setProperty((String)key, contextPath + unpathedUrl));
        return json;
    }

    private JohnsonPageEndpointsProvider() {
    }
}

