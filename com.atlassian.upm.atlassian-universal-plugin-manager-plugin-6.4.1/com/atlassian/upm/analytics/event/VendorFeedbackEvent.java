/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.analytics.event;

import com.atlassian.upm.analytics.event.UpmAnalyticsEvent;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class VendorFeedbackEvent
extends UpmAnalyticsEvent {
    private final Map<String, String> metadata;
    private final String eventType;

    public VendorFeedbackEvent(String pluginKey, String reasonCode, String message, String type, String pluginVersion, Option<String> email, Option<String> fullName, Option<String> addonSen) {
        HashMap<String, String> builder = new HashMap<String, String>();
        builder.put("pk", pluginKey);
        builder.put("pv", pluginVersion);
        builder.put("reasonCode", reasonCode);
        builder.put("message", message);
        for (String e : email) {
            builder.put("email", e);
        }
        for (String n : fullName) {
            builder.put("fullName", n);
        }
        for (String s : addonSen) {
            builder.put("addonSen", s);
        }
        this.metadata = Collections.unmodifiableMap(builder);
        this.eventType = "vendor-feedback-" + type;
    }

    @Override
    public String getEventType() {
        return this.eventType;
    }

    @Override
    public Iterable<Pair<String, String>> getInvolvedPluginVersions() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<Pair<String, String>> getMetadata() {
        return this.metadata.entrySet().stream().map(e -> Pair.pair(e.getKey(), e.getValue())).collect(Collectors.toList());
    }
}

