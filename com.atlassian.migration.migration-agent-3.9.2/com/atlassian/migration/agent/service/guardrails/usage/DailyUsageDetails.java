/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.SetMultimap
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.guardrails.usage;

import com.atlassian.migration.agent.service.guardrails.logs.PageType;
import com.atlassian.migration.agent.service.guardrails.logs.UsageMetricsNodeData;
import com.atlassian.migration.agent.service.guardrails.usage.DailyUsageSummary;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public final class DailyUsageDetails {
    private static final int HOUR_START = 12;
    @JsonProperty(value="date")
    private final LocalDate date;
    @JsonProperty(value="uniqueUsers")
    private final Set<String> uniqueUsers;
    @JsonIgnore
    private final SetMultimap<String, String> hourToUsers;
    @JsonProperty(value="hourRequestsCount")
    private final Map<String, Integer> hourRequestsCount;
    @JsonProperty(value="requestsTypeCount")
    private final Map<PageType, Integer> requestsTypeCount;
    @JsonProperty(value="nodes")
    private final List<UsageMetricsNodeData> nodes;

    public DailyUsageDetails(LocalDate date, Set<String> uniqueUsers, SetMultimap<String, String> hourToUsers, Map<String, Integer> hourRequestsCount, Map<PageType, Integer> requestsTypeCount, List<UsageMetricsNodeData> nodes) {
        this.date = Objects.requireNonNull(date);
        this.nodes = ImmutableList.copyOf(nodes);
        this.uniqueUsers = ImmutableSet.copyOf(uniqueUsers);
        this.hourToUsers = ImmutableSetMultimap.copyOf(hourToUsers);
        this.hourRequestsCount = ImmutableMap.copyOf(hourRequestsCount);
        this.requestsTypeCount = ImmutableMap.copyOf(requestsTypeCount);
    }

    @JsonCreator
    private static DailyUsageDetails fromJson(@JsonProperty(value="date") LocalDate date, @JsonProperty(value="uniqueUsers") Set<String> uniqueUsers, @JsonProperty(value="hourToUsers") Map<String, Set<String>> hourToUsers, @JsonProperty(value="hourRequestsCount") Map<String, Integer> hourRequestsCount, @JsonProperty(value="requestsTypeCount") Map<PageType, Integer> requestsTypeCount, @JsonProperty(value="nodes") List<UsageMetricsNodeData> nodes) {
        HashMultimap hourToUsersMultiMap = HashMultimap.create();
        hourToUsers.forEach((arg_0, arg_1) -> ((SetMultimap)hourToUsersMultiMap).putAll(arg_0, arg_1));
        return new DailyUsageDetails(date, uniqueUsers, (SetMultimap<String, String>)hourToUsersMultiMap, hourRequestsCount, requestsTypeCount, nodes);
    }

    @JsonProperty(value="hourToUsers")
    private Map<String, Collection<String>> hourToUsersJsonFriendly() {
        return this.hourToUsers.asMap();
    }

    public DailyUsageSummary toDailyUsageSummary() {
        return new DailyUsageSummary(this.date, this.nodes, this.uniqueUsers.size(), this.getPeakHourUsers(), this.getPeakHourRequests(), this.requestsTypeCount);
    }

    private int getPeakHourUsers() {
        return this.hourToUsers.asMap().values().stream().mapToInt(Collection::size).max().orElse(0);
    }

    private int getPeakHourRequests() {
        return this.hourRequestsCount.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    public static DailyUsageDetailsBuilder createBuilder(LocalDate date) {
        return DailyUsageDetails.builder().date(date);
    }

    @Generated
    public static DailyUsageDetailsBuilder builder() {
        return new DailyUsageDetailsBuilder();
    }

    @Generated
    public DailyUsageDetailsBuilder toBuilder() {
        return new DailyUsageDetailsBuilder().date(this.date).uniqueUsers(this.uniqueUsers).hourToUsers(this.hourToUsers).hourRequestsCount(this.hourRequestsCount).requestsTypeCount(this.requestsTypeCount).nodes(this.nodes);
    }

    @Generated
    public LocalDate getDate() {
        return this.date;
    }

    @Generated
    public Set<String> getUniqueUsers() {
        return this.uniqueUsers;
    }

    @Generated
    public SetMultimap<String, String> getHourToUsers() {
        return this.hourToUsers;
    }

    @Generated
    public Map<String, Integer> getHourRequestsCount() {
        return this.hourRequestsCount;
    }

    @Generated
    public Map<PageType, Integer> getRequestsTypeCount() {
        return this.requestsTypeCount;
    }

    @Generated
    public List<UsageMetricsNodeData> getNodes() {
        return this.nodes;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DailyUsageDetails)) {
            return false;
        }
        DailyUsageDetails other = (DailyUsageDetails)o;
        LocalDate this$date = this.getDate();
        LocalDate other$date = other.getDate();
        if (this$date == null ? other$date != null : !((Object)this$date).equals(other$date)) {
            return false;
        }
        Set<String> this$uniqueUsers = this.getUniqueUsers();
        Set<String> other$uniqueUsers = other.getUniqueUsers();
        if (this$uniqueUsers == null ? other$uniqueUsers != null : !((Object)this$uniqueUsers).equals(other$uniqueUsers)) {
            return false;
        }
        SetMultimap<String, String> this$hourToUsers = this.getHourToUsers();
        SetMultimap<String, String> other$hourToUsers = other.getHourToUsers();
        if (this$hourToUsers == null ? other$hourToUsers != null : !this$hourToUsers.equals(other$hourToUsers)) {
            return false;
        }
        Map<String, Integer> this$hourRequestsCount = this.getHourRequestsCount();
        Map<String, Integer> other$hourRequestsCount = other.getHourRequestsCount();
        if (this$hourRequestsCount == null ? other$hourRequestsCount != null : !((Object)this$hourRequestsCount).equals(other$hourRequestsCount)) {
            return false;
        }
        Map<PageType, Integer> this$requestsTypeCount = this.getRequestsTypeCount();
        Map<PageType, Integer> other$requestsTypeCount = other.getRequestsTypeCount();
        if (this$requestsTypeCount == null ? other$requestsTypeCount != null : !((Object)this$requestsTypeCount).equals(other$requestsTypeCount)) {
            return false;
        }
        List<UsageMetricsNodeData> this$nodes = this.getNodes();
        List<UsageMetricsNodeData> other$nodes = other.getNodes();
        return !(this$nodes == null ? other$nodes != null : !((Object)this$nodes).equals(other$nodes));
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        LocalDate $date = this.getDate();
        result = result * 59 + ($date == null ? 43 : ((Object)$date).hashCode());
        Set<String> $uniqueUsers = this.getUniqueUsers();
        result = result * 59 + ($uniqueUsers == null ? 43 : ((Object)$uniqueUsers).hashCode());
        SetMultimap<String, String> $hourToUsers = this.getHourToUsers();
        result = result * 59 + ($hourToUsers == null ? 43 : $hourToUsers.hashCode());
        Map<String, Integer> $hourRequestsCount = this.getHourRequestsCount();
        result = result * 59 + ($hourRequestsCount == null ? 43 : ((Object)$hourRequestsCount).hashCode());
        Map<PageType, Integer> $requestsTypeCount = this.getRequestsTypeCount();
        result = result * 59 + ($requestsTypeCount == null ? 43 : ((Object)$requestsTypeCount).hashCode());
        List<UsageMetricsNodeData> $nodes = this.getNodes();
        result = result * 59 + ($nodes == null ? 43 : ((Object)$nodes).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "DailyUsageDetails(date=" + this.getDate() + ", uniqueUsers=" + this.getUniqueUsers() + ", hourToUsers=" + this.getHourToUsers() + ", hourRequestsCount=" + this.getHourRequestsCount() + ", requestsTypeCount=" + this.getRequestsTypeCount() + ", nodes=" + this.getNodes() + ")";
    }

    public static class DailyUsageDetailsBuilder {
        @Generated
        private LocalDate date;
        @Generated
        private Set<String> uniqueUsers;
        @Generated
        private SetMultimap<String, String> hourToUsers;
        @Generated
        private Map<String, Integer> hourRequestsCount;
        @Generated
        private Map<PageType, Integer> requestsTypeCount;
        @Generated
        private List<UsageMetricsNodeData> nodes = new ArrayList<UsageMetricsNodeData>();

        public DailyUsageDetailsBuilder() {
            this.uniqueUsers = new HashSet<String>();
            this.hourToUsers = HashMultimap.create();
            this.hourRequestsCount = new HashMap<String, Integer>();
            this.requestsTypeCount = new HashMap<PageType, Integer>();
        }

        public DailyUsageDetailsBuilder add(DailyUsageDetails metrics) {
            this.nodes.addAll(metrics.nodes);
            this.uniqueUsers.addAll(metrics.uniqueUsers);
            this.hourToUsers.putAll((Multimap)metrics.hourToUsers);
            metrics.hourRequestsCount.forEach((k, v) -> this.hourRequestsCount.merge((String)k, (Integer)v, Integer::sum));
            metrics.requestsTypeCount.forEach((k, v) -> this.requestsTypeCount.merge((PageType)((Object)k), (Integer)v, Integer::sum));
            return this;
        }

        public DailyUsageDetailsBuilder addLogEntry(String date, String user, PageType pageType) {
            String hour = date.substring(12, 14);
            if (user != null) {
                this.uniqueUsers.add(user);
                this.hourToUsers.put((Object)hour, (Object)user);
            }
            this.hourRequestsCount.merge(hour, 1, Integer::sum);
            this.requestsTypeCount.merge(pageType, 1, Integer::sum);
            return this;
        }

        public void addNodesIfMissing(List<String> requiredNodes) {
            HashSet<String> missingNodes = new HashSet<String>(requiredNodes);
            this.nodes.forEach(node -> missingNodes.remove(node.getId()));
            for (String missingNode : missingNodes) {
                this.nodes.add(UsageMetricsNodeData.builder().id(missingNode).nodeStatus(UsageMetricsNodeData.NodeStatus.UNAVAILABLE).dataCollectionStatus(UsageMetricsNodeData.DataCollectionStatus.FAILED).build());
            }
        }

        @Generated
        public DailyUsageDetailsBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        @Generated
        public DailyUsageDetailsBuilder uniqueUsers(Set<String> uniqueUsers) {
            this.uniqueUsers = uniqueUsers;
            return this;
        }

        @Generated
        public DailyUsageDetailsBuilder hourToUsers(SetMultimap<String, String> hourToUsers) {
            this.hourToUsers = hourToUsers;
            return this;
        }

        @Generated
        public DailyUsageDetailsBuilder hourRequestsCount(Map<String, Integer> hourRequestsCount) {
            this.hourRequestsCount = hourRequestsCount;
            return this;
        }

        @Generated
        public DailyUsageDetailsBuilder requestsTypeCount(Map<PageType, Integer> requestsTypeCount) {
            this.requestsTypeCount = requestsTypeCount;
            return this;
        }

        @Generated
        public DailyUsageDetailsBuilder nodes(List<UsageMetricsNodeData> nodes) {
            this.nodes = nodes;
            return this;
        }

        @Generated
        public DailyUsageDetails build() {
            return new DailyUsageDetails(this.date, this.uniqueUsers, this.hourToUsers, this.hourRequestsCount, this.requestsTypeCount, this.nodes);
        }

        @Generated
        public String toString() {
            return "DailyUsageDetails.DailyUsageDetailsBuilder(date=" + this.date + ", uniqueUsers=" + this.uniqueUsers + ", hourToUsers=" + this.hourToUsers + ", hourRequestsCount=" + this.hourRequestsCount + ", requestsTypeCount=" + this.requestsTypeCount + ", nodes=" + this.nodes + ")";
        }
    }
}

