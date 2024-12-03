/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.guardrails.usage;

import com.atlassian.migration.agent.service.guardrails.logs.PageType;
import com.atlassian.migration.agent.service.guardrails.logs.UsageMetricsNodeData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public final class DailyUsageSummary {
    @JsonProperty(value="date")
    private final LocalDate date;
    @JsonProperty(value="nodes")
    private final List<UsageMetricsNodeData> nodes;
    @JsonProperty(value="uniqueUsers")
    private final int uniqueUsers;
    @JsonProperty(value="peakHourUsers")
    private final int peakHourUsers;
    @JsonProperty(value="peakHourRequests")
    private final int peakHourRequests;
    @JsonProperty(value="requestsTypeCount")
    private final Map<PageType, Integer> requestsTypeCount;

    public DailyUsageSummary(@JsonProperty(value="date") LocalDate date, @JsonProperty(value="nodes") List<UsageMetricsNodeData> nodes, @JsonProperty(value="uniqueUsers") int uniqueUsers, @JsonProperty(value="peakHourUsers") int peakHourUsers, @JsonProperty(value="peakHourRequests") int peakHourRequests, @JsonProperty(value="requestsTypeCount") Map<PageType, Integer> requestsTypeCount) {
        this.date = date;
        this.nodes = ImmutableList.copyOf((Collection)nodes.stream().sorted(Comparator.comparing(UsageMetricsNodeData::getId)).collect(Collectors.toList()));
        this.uniqueUsers = uniqueUsers;
        this.peakHourUsers = peakHourUsers;
        this.peakHourRequests = peakHourRequests;
        this.requestsTypeCount = ImmutableMap.copyOf(requestsTypeCount);
    }

    @Generated
    public static DailyUsageSummaryBuilder builder() {
        return new DailyUsageSummaryBuilder();
    }

    @Generated
    public DailyUsageSummaryBuilder toBuilder() {
        return new DailyUsageSummaryBuilder().date(this.date).nodes(this.nodes).uniqueUsers(this.uniqueUsers).peakHourUsers(this.peakHourUsers).peakHourRequests(this.peakHourRequests).requestsTypeCount(this.requestsTypeCount);
    }

    @Generated
    public LocalDate getDate() {
        return this.date;
    }

    @Generated
    public List<UsageMetricsNodeData> getNodes() {
        return this.nodes;
    }

    @Generated
    public int getUniqueUsers() {
        return this.uniqueUsers;
    }

    @Generated
    public int getPeakHourUsers() {
        return this.peakHourUsers;
    }

    @Generated
    public int getPeakHourRequests() {
        return this.peakHourRequests;
    }

    @Generated
    public Map<PageType, Integer> getRequestsTypeCount() {
        return this.requestsTypeCount;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DailyUsageSummary)) {
            return false;
        }
        DailyUsageSummary other = (DailyUsageSummary)o;
        LocalDate this$date = this.getDate();
        LocalDate other$date = other.getDate();
        if (this$date == null ? other$date != null : !((Object)this$date).equals(other$date)) {
            return false;
        }
        List<UsageMetricsNodeData> this$nodes = this.getNodes();
        List<UsageMetricsNodeData> other$nodes = other.getNodes();
        if (this$nodes == null ? other$nodes != null : !((Object)this$nodes).equals(other$nodes)) {
            return false;
        }
        if (this.getUniqueUsers() != other.getUniqueUsers()) {
            return false;
        }
        if (this.getPeakHourUsers() != other.getPeakHourUsers()) {
            return false;
        }
        if (this.getPeakHourRequests() != other.getPeakHourRequests()) {
            return false;
        }
        Map<PageType, Integer> this$requestsTypeCount = this.getRequestsTypeCount();
        Map<PageType, Integer> other$requestsTypeCount = other.getRequestsTypeCount();
        return !(this$requestsTypeCount == null ? other$requestsTypeCount != null : !((Object)this$requestsTypeCount).equals(other$requestsTypeCount));
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        LocalDate $date = this.getDate();
        result = result * 59 + ($date == null ? 43 : ((Object)$date).hashCode());
        List<UsageMetricsNodeData> $nodes = this.getNodes();
        result = result * 59 + ($nodes == null ? 43 : ((Object)$nodes).hashCode());
        result = result * 59 + this.getUniqueUsers();
        result = result * 59 + this.getPeakHourUsers();
        result = result * 59 + this.getPeakHourRequests();
        Map<PageType, Integer> $requestsTypeCount = this.getRequestsTypeCount();
        result = result * 59 + ($requestsTypeCount == null ? 43 : ((Object)$requestsTypeCount).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "DailyUsageSummary(date=" + this.getDate() + ", nodes=" + this.getNodes() + ", uniqueUsers=" + this.getUniqueUsers() + ", peakHourUsers=" + this.getPeakHourUsers() + ", peakHourRequests=" + this.getPeakHourRequests() + ", requestsTypeCount=" + this.getRequestsTypeCount() + ")";
    }

    @Generated
    public static class DailyUsageSummaryBuilder {
        @Generated
        private LocalDate date;
        @Generated
        private List<UsageMetricsNodeData> nodes;
        @Generated
        private int uniqueUsers;
        @Generated
        private int peakHourUsers;
        @Generated
        private int peakHourRequests;
        @Generated
        private Map<PageType, Integer> requestsTypeCount;

        @Generated
        DailyUsageSummaryBuilder() {
        }

        @Generated
        public DailyUsageSummaryBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        @Generated
        public DailyUsageSummaryBuilder nodes(List<UsageMetricsNodeData> nodes) {
            this.nodes = nodes;
            return this;
        }

        @Generated
        public DailyUsageSummaryBuilder uniqueUsers(int uniqueUsers) {
            this.uniqueUsers = uniqueUsers;
            return this;
        }

        @Generated
        public DailyUsageSummaryBuilder peakHourUsers(int peakHourUsers) {
            this.peakHourUsers = peakHourUsers;
            return this;
        }

        @Generated
        public DailyUsageSummaryBuilder peakHourRequests(int peakHourRequests) {
            this.peakHourRequests = peakHourRequests;
            return this;
        }

        @Generated
        public DailyUsageSummaryBuilder requestsTypeCount(Map<PageType, Integer> requestsTypeCount) {
            this.requestsTypeCount = requestsTypeCount;
            return this;
        }

        @Generated
        public DailyUsageSummary build() {
            return new DailyUsageSummary(this.date, this.nodes, this.uniqueUsers, this.peakHourUsers, this.peakHourRequests, this.requestsTypeCount);
        }

        @Generated
        public String toString() {
            return "DailyUsageSummary.DailyUsageSummaryBuilder(date=" + this.date + ", nodes=" + this.nodes + ", uniqueUsers=" + this.uniqueUsers + ", peakHourUsers=" + this.peakHourUsers + ", peakHourRequests=" + this.peakHourRequests + ", requestsTypeCount=" + this.requestsTypeCount + ")";
        }
    }
}

