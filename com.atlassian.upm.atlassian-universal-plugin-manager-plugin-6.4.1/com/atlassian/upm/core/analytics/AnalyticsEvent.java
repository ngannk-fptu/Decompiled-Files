/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.analytics;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import java.util.ArrayList;
import java.util.Collections;

public interface AnalyticsEvent {
    public boolean isRecordedByMarketplace();

    public String getEventType();

    public Iterable<AnalyticsEventInfo> getInvolvedPluginInfo();

    public Iterable<Pair<String, String>> getInvolvedPluginVersions();

    public Iterable<Pair<String, String>> getMetadata();

    public static class AnalyticsEventInfo {
        private final String pluginKey;
        private final String version;
        private Option<String> sen;

        public AnalyticsEventInfo(String pluginKey, String version, Option<String> sen) {
            this.pluginKey = pluginKey;
            this.version = version;
            this.sen = sen;
        }

        public static Iterable<Pair<String, String>> getInvolvedPluginVersions(Iterable<AnalyticsEventInfo> src) {
            ArrayList resultsBuilder = new ArrayList();
            src.forEach(info -> resultsBuilder.add(Pair.pair(info.getPluginKey(), info.getVersion())));
            return Collections.unmodifiableList(resultsBuilder);
        }

        public String getPluginKey() {
            return this.pluginKey;
        }

        public String getVersion() {
            return this.version;
        }

        public Option<String> getSen() {
            return this.sen;
        }

        public void setSen(Option<String> sen) {
            this.sen = sen;
        }
    }
}

