/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.json.DurationLongDeserializer
 *  com.atlassian.migration.json.DurationLongSerializer
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.migration.agent.model.stats;

import com.atlassian.migration.agent.model.stats.ContentSummary;
import com.atlassian.migration.agent.model.stats.InstanceStats;
import com.atlassian.migration.json.DurationLongDeserializer;
import com.atlassian.migration.json.DurationLongSerializer;
import java.time.Duration;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@ParametersAreNonnullByDefault
public final class ServerStats {
    @JsonProperty
    private final InstanceStats instanceStats;
    @JsonProperty
    private final ContentSummary contentSummary;
    @JsonSerialize(using=DurationLongSerializer.class)
    @JsonDeserialize(using=DurationLongDeserializer.class)
    @JsonProperty
    private final Duration totalUserGroupMigrationTime;
    @JsonSerialize(using=DurationLongSerializer.class)
    @JsonDeserialize(using=DurationLongDeserializer.class)
    @JsonProperty
    private final Duration totalSpaceMigrationTime;
    @JsonSerialize(using=DurationLongSerializer.class)
    @JsonDeserialize(using=DurationLongDeserializer.class)
    @JsonProperty
    private final Duration baseMigrationTime;
    @JsonProperty
    private final long bandwidthKBS;

    @JsonCreator
    public ServerStats(@JsonProperty(value="instanceStats") InstanceStats instanceStats, @JsonProperty(value="contentSummary") ContentSummary contentSummary, @JsonProperty(value="totalUserGroupMigrationTime") Duration totalUserGroupMigrationTime, @JsonProperty(value="totalSpaceMigrationTime") Duration totalSpaceMigrationTime, @JsonProperty(value="baseMigrationTime") Duration baseMigrationTime, @JsonProperty(value="bandwidthKBS") long bandwidthKBS) {
        this.instanceStats = instanceStats;
        this.contentSummary = contentSummary;
        this.totalUserGroupMigrationTime = totalUserGroupMigrationTime;
        this.totalSpaceMigrationTime = totalSpaceMigrationTime;
        this.baseMigrationTime = baseMigrationTime;
        this.bandwidthKBS = bandwidthKBS;
    }

    public InstanceStats getInstanceStats() {
        return this.instanceStats;
    }

    public ContentSummary getContentSummary() {
        return this.contentSummary;
    }

    public Duration getTotalUserGroupMigrationTime() {
        return this.totalUserGroupMigrationTime;
    }

    public Duration getTotalSpaceMigrationTime() {
        return this.totalSpaceMigrationTime;
    }

    public Duration getBaseMigrationTime() {
        return this.baseMigrationTime;
    }

    public long getBandwidthKBS() {
        return this.bandwidthKBS;
    }
}

