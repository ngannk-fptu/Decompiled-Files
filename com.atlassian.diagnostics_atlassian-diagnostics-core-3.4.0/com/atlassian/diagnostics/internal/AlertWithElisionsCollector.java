/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertTrigger$Builder
 *  com.atlassian.diagnostics.AlertWithElisions
 *  com.atlassian.diagnostics.Issue
 *  com.atlassian.diagnostics.PageRequest
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.AlertWithElisions;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.PageRequest;
import com.atlassian.diagnostics.internal.IssueSupplier;
import com.atlassian.diagnostics.internal.SimpleAlertWithElisions;
import com.atlassian.diagnostics.internal.SimpleElisions;
import com.atlassian.diagnostics.internal.SimpleInterval;
import com.atlassian.diagnostics.internal.dao.AlertEntity;
import com.atlassian.diagnostics.internal.dao.MinimalAlertEntity;
import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

class AlertWithElisionsCollector {
    private final int end;
    private final List<AlertCandidate> ids;
    private final IssueSupplier issueSupplier;
    private final int start;
    private final long windowMillis;
    private Map<AlertGroupKey, AlertCandidate> curEpochWindowCandidates;
    private long curEpochWindow;
    private int nextRow;

    AlertWithElisionsCollector(IssueSupplier issueSupplier, PageRequest pageRequest, Duration windowSize) {
        this.issueSupplier = issueSupplier;
        this.curEpochWindowCandidates = new HashMap<AlertGroupKey, AlertCandidate>();
        this.ids = new ArrayList<AlertCandidate>(Math.min(256, pageRequest.getLimit() + 1));
        this.start = pageRequest.getStart();
        this.end = this.start + pageRequest.getLimit() + 1;
        this.windowMillis = windowSize.toMillis();
    }

    void add(@Nonnull MinimalAlertEntity alert) {
        long timestamp = alert.getTimestamp().toEpochMilli();
        long epochWindow = timestamp / this.windowMillis;
        if (epochWindow != this.curEpochWindow) {
            this.nextWindow();
            this.curEpochWindow = epochWindow;
        }
        AlertGroupKey key = new AlertGroupKey(alert.getIssueId(), alert.getTriggerPluginKey(), alert.getNodeName());
        this.curEpochWindowCandidates.computeIfAbsent(key, k -> new AlertCandidate()).add(alert);
    }

    @Nonnull
    List<Long> getAlertIdsToLoad() {
        return this.ids.stream().map(candidate -> ((AlertCandidate)candidate).bestId).collect(Collectors.toList());
    }

    boolean hasCompletePage() {
        return this.nextRow > this.end;
    }

    List<AlertWithElisions> onEndAlertResolution() {
        ImmutableList.Builder builder = ImmutableList.builder();
        Iterator<AlertCandidate> it = this.ids.iterator();
        while (it.hasNext()) {
            AlertCandidate candidate = it.next();
            if (candidate.resolved != null) {
                builder.add((Object)candidate.resolved);
            }
            it.remove();
        }
        return builder.build();
    }

    void onEndAlertScan() {
        this.nextWindow();
    }

    @Nonnull
    List<AlertWithElisions> resolveCandidate(AlertEntity alert) {
        this.ids.stream().filter(candidate -> ((AlertCandidate)candidate).bestId == alert.getId()).findFirst().ifPresent(candidate -> candidate.resolve(alert, this.windowMillis));
        ImmutableList.Builder builder = ImmutableList.builder();
        Iterator<AlertCandidate> it = this.ids.iterator();
        while (it.hasNext()) {
            AlertCandidate candidate2 = it.next();
            if (candidate2.resolved != null) {
                builder.add((Object)candidate2.resolved);
                it.remove();
                continue;
            }
            return builder.build();
        }
        return builder.build();
    }

    private void nextWindow() {
        int skip = Math.max(0, this.start - this.nextRow);
        int limit = Math.max(0, this.end - this.nextRow);
        int size = this.curEpochWindowCandidates.size();
        if (skip < size && limit > 0) {
            this.curEpochWindowCandidates.values().stream().sorted().skip(skip).limit(limit).forEach(this.ids::add);
        }
        this.nextRow += size;
        this.curEpochWindowCandidates.clear();
    }

    private static class AlertGroupKey {
        private final String issueId;
        private final String node;
        private final String pluginKey;

        private AlertGroupKey(String issueId, String pluginKey, String node) {
            this.issueId = issueId;
            this.node = node;
            this.pluginKey = pluginKey;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            AlertGroupKey that = (AlertGroupKey)o;
            return Objects.equals(this.issueId, that.issueId) && Objects.equals(this.node, that.node) && Objects.equals(this.pluginKey, that.pluginKey);
        }

        public int hashCode() {
            return Objects.hash(this.issueId, this.node, this.pluginKey);
        }
    }

    private class AlertCandidate
    implements Comparable<AlertCandidate> {
        private long bestId;
        private int bestLength;
        private int count;
        private Instant latestTimestamp;
        private AlertWithElisions resolved;

        private AlertCandidate() {
        }

        @Override
        public int compareTo(AlertCandidate o) {
            return o.latestTimestamp.compareTo(this.latestTimestamp);
        }

        void add(MinimalAlertEntity minimalAlert) {
            Instant timestamp = minimalAlert.getTimestamp();
            if (this.latestTimestamp == null || timestamp.isAfter(this.latestTimestamp)) {
                this.latestTimestamp = timestamp;
            }
            int detailLength = minimalAlert.getDetailsJsonLength();
            if (this.bestId == 0L || detailLength > this.bestLength) {
                this.bestId = minimalAlert.getId();
                this.bestLength = detailLength;
            }
            ++this.count;
        }

        void resolve(AlertEntity alert, long windowMillis) {
            Issue issue = AlertWithElisionsCollector.this.issueSupplier.getIssue(alert.getIssueId(), alert.getIssueSeverity());
            SimpleAlertWithElisions.Builder builder = (SimpleAlertWithElisions.Builder)((SimpleAlertWithElisions.Builder)((SimpleAlertWithElisions.Builder)((SimpleAlertWithElisions.Builder)new SimpleAlertWithElisions.Builder(issue, alert.getNodeName()).id(alert.getId())).detailsAsJson(alert.getDetailsJson())).timestamp(alert.getTimestamp())).trigger(new AlertTrigger.Builder().plugin(alert.getTriggerPluginKey(), alert.getTriggerPluginVersion()).module(alert.getTriggerModule()).build());
            if (this.count > 1) {
                long windowStart = alert.getTimestamp().toEpochMilli();
                windowStart -= windowStart % windowMillis;
                Instant start = Instant.ofEpochMilli(windowStart);
                SimpleInterval interval = new SimpleInterval(start, start.plusMillis(windowMillis));
                builder.elisions(new SimpleElisions(interval, this.count - 1));
            }
            this.resolved = builder.build();
        }
    }
}

