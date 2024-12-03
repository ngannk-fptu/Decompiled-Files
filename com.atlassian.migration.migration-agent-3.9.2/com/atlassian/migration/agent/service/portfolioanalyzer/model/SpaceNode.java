/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.portfolioanalyzer.model;

import com.atlassian.migration.agent.service.portfolioanalyzer.model.SpaceNodeStats;
import java.util.Set;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SpaceNode {
    private static final String TYPE = "SPACE";
    @JsonProperty(value="key")
    private final String key;
    @JsonProperty(value="instanceURL")
    private final String instanceURL;
    @JsonProperty(value="estimatedTime")
    private final Long estimatedTime;
    @JsonProperty(value="spaceStats")
    private final SpaceNodeStats spaceStats;
    @JsonProperty(value="archived")
    private final boolean archived;
    @JsonProperty(value="lastModified")
    private final long lastModified;
    @JsonProperty(value="users")
    private final Set<String> users;
    @JsonProperty(value="groups")
    private final Set<String> groups;

    public SpaceNode(@JsonProperty(value="key") String key, @JsonProperty(value="instanceURL") String instanceURL, @JsonProperty(value="estimatedTime") Long estimatedTime, @JsonProperty(value="spaceStats") SpaceNodeStats spaceStats, @JsonProperty(value="archived") boolean archived, @JsonProperty(value="lastModified") long lastModified, @JsonProperty(value="users") Set<String> users, @JsonProperty(value="groups") Set<String> groups) {
        this.key = key;
        this.instanceURL = instanceURL;
        this.estimatedTime = estimatedTime;
        this.spaceStats = spaceStats;
        this.archived = archived;
        this.lastModified = lastModified;
        this.users = users;
        this.groups = groups;
    }

    @JsonProperty(value="id")
    public String getId() {
        return this.instanceURL + ":" + this.key;
    }

    @JsonProperty(value="type")
    public String getType() {
        return TYPE;
    }

    @Generated
    public static SpaceNodeBuilder builder() {
        return new SpaceNodeBuilder();
    }

    @Generated
    public String getKey() {
        return this.key;
    }

    @Generated
    public String getInstanceURL() {
        return this.instanceURL;
    }

    @Generated
    public Long getEstimatedTime() {
        return this.estimatedTime;
    }

    @Generated
    public SpaceNodeStats getSpaceStats() {
        return this.spaceStats;
    }

    @Generated
    public boolean isArchived() {
        return this.archived;
    }

    @Generated
    public long getLastModified() {
        return this.lastModified;
    }

    @Generated
    public Set<String> getUsers() {
        return this.users;
    }

    @Generated
    public Set<String> getGroups() {
        return this.groups;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SpaceNode)) {
            return false;
        }
        SpaceNode other = (SpaceNode)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$key = this.getKey();
        String other$key = other.getKey();
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) {
            return false;
        }
        String this$instanceURL = this.getInstanceURL();
        String other$instanceURL = other.getInstanceURL();
        if (this$instanceURL == null ? other$instanceURL != null : !this$instanceURL.equals(other$instanceURL)) {
            return false;
        }
        Long this$estimatedTime = this.getEstimatedTime();
        Long other$estimatedTime = other.getEstimatedTime();
        if (this$estimatedTime == null ? other$estimatedTime != null : !((Object)this$estimatedTime).equals(other$estimatedTime)) {
            return false;
        }
        SpaceNodeStats this$spaceStats = this.getSpaceStats();
        SpaceNodeStats other$spaceStats = other.getSpaceStats();
        if (this$spaceStats == null ? other$spaceStats != null : !((Object)this$spaceStats).equals(other$spaceStats)) {
            return false;
        }
        if (this.isArchived() != other.isArchived()) {
            return false;
        }
        if (this.getLastModified() != other.getLastModified()) {
            return false;
        }
        Set<String> this$users = this.getUsers();
        Set<String> other$users = other.getUsers();
        if (this$users == null ? other$users != null : !((Object)this$users).equals(other$users)) {
            return false;
        }
        Set<String> this$groups = this.getGroups();
        Set<String> other$groups = other.getGroups();
        return !(this$groups == null ? other$groups != null : !((Object)this$groups).equals(other$groups));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof SpaceNode;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $key = this.getKey();
        result = result * 59 + ($key == null ? 43 : $key.hashCode());
        String $instanceURL = this.getInstanceURL();
        result = result * 59 + ($instanceURL == null ? 43 : $instanceURL.hashCode());
        Long $estimatedTime = this.getEstimatedTime();
        result = result * 59 + ($estimatedTime == null ? 43 : ((Object)$estimatedTime).hashCode());
        SpaceNodeStats $spaceStats = this.getSpaceStats();
        result = result * 59 + ($spaceStats == null ? 43 : ((Object)$spaceStats).hashCode());
        result = result * 59 + (this.isArchived() ? 79 : 97);
        long $lastModified = this.getLastModified();
        result = result * 59 + (int)($lastModified >>> 32 ^ $lastModified);
        Set<String> $users = this.getUsers();
        result = result * 59 + ($users == null ? 43 : ((Object)$users).hashCode());
        Set<String> $groups = this.getGroups();
        result = result * 59 + ($groups == null ? 43 : ((Object)$groups).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "SpaceNode(key=" + this.getKey() + ", instanceURL=" + this.getInstanceURL() + ", estimatedTime=" + this.getEstimatedTime() + ", spaceStats=" + this.getSpaceStats() + ", archived=" + this.isArchived() + ", lastModified=" + this.getLastModified() + ", users=" + this.getUsers() + ", groups=" + this.getGroups() + ")";
    }

    @Generated
    public static class SpaceNodeBuilder {
        @Generated
        private String key;
        @Generated
        private String instanceURL;
        @Generated
        private Long estimatedTime;
        @Generated
        private SpaceNodeStats spaceStats;
        @Generated
        private boolean archived;
        @Generated
        private long lastModified;
        @Generated
        private Set<String> users;
        @Generated
        private Set<String> groups;

        @Generated
        SpaceNodeBuilder() {
        }

        @Generated
        public SpaceNodeBuilder key(String key) {
            this.key = key;
            return this;
        }

        @Generated
        public SpaceNodeBuilder instanceURL(String instanceURL) {
            this.instanceURL = instanceURL;
            return this;
        }

        @Generated
        public SpaceNodeBuilder estimatedTime(Long estimatedTime) {
            this.estimatedTime = estimatedTime;
            return this;
        }

        @Generated
        public SpaceNodeBuilder spaceStats(SpaceNodeStats spaceStats) {
            this.spaceStats = spaceStats;
            return this;
        }

        @Generated
        public SpaceNodeBuilder archived(boolean archived) {
            this.archived = archived;
            return this;
        }

        @Generated
        public SpaceNodeBuilder lastModified(long lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        @Generated
        public SpaceNodeBuilder users(Set<String> users) {
            this.users = users;
            return this;
        }

        @Generated
        public SpaceNodeBuilder groups(Set<String> groups) {
            this.groups = groups;
            return this;
        }

        @Generated
        public SpaceNode build() {
            return new SpaceNode(this.key, this.instanceURL, this.estimatedTime, this.spaceStats, this.archived, this.lastModified, this.users, this.groups);
        }

        @Generated
        public String toString() {
            return "SpaceNode.SpaceNodeBuilder(key=" + this.key + ", instanceURL=" + this.instanceURL + ", estimatedTime=" + this.estimatedTime + ", spaceStats=" + this.spaceStats + ", archived=" + this.archived + ", lastModified=" + this.lastModified + ", users=" + this.users + ", groups=" + this.groups + ")";
        }
    }
}

