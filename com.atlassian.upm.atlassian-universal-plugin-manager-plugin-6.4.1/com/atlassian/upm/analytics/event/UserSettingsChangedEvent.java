/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.analytics.event;

import com.atlassian.upm.UserSettings;
import com.atlassian.upm.analytics.event.UpmAnalyticsEvent;
import com.atlassian.upm.api.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserSettingsChangedEvent
extends UpmAnalyticsEvent {
    private final List<Pair<String, String>> metadata;

    public UserSettingsChangedEvent(Map<UserSettings, String> values) {
        ArrayList<Pair<String, String>> builder = new ArrayList<Pair<String, String>>();
        for (Map.Entry<UserSettings, String> entry : values.entrySet()) {
            builder.add(Pair.pair("name", entry.getKey().getKey()));
            builder.add(Pair.pair("value", entry.getValue()));
        }
        this.metadata = Collections.unmodifiableList(builder);
    }

    @Override
    public String getEventType() {
        return "user-settings-changed";
    }

    @Override
    public Iterable<Pair<String, String>> getInvolvedPluginVersions() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<Pair<String, String>> getMetadata() {
        return this.metadata;
    }
}

