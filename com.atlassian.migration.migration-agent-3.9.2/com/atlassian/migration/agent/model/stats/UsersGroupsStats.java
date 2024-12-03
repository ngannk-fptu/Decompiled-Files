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

import com.atlassian.migration.json.DurationLongDeserializer;
import com.atlassian.migration.json.DurationLongSerializer;
import java.time.Duration;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@ParametersAreNonnullByDefault
public final class UsersGroupsStats {
    @JsonProperty
    private final int numberOfUsers;
    @JsonProperty
    private final int numberOfGroups;
    @JsonSerialize(using=DurationLongSerializer.class)
    @JsonDeserialize(using=DurationLongDeserializer.class)
    @JsonProperty
    private final Duration totalMigrationTime;

    @JsonCreator
    public UsersGroupsStats(@JsonProperty(value="numberOfUsers") int numberOfUsers, @JsonProperty(value="numberOfGroups") int numberOfGroups, @JsonProperty(value="totalMigrationTime") Duration totalMigrationTime) {
        this.numberOfUsers = numberOfUsers;
        this.numberOfGroups = numberOfGroups;
        this.totalMigrationTime = totalMigrationTime;
    }

    public int getNumberOfUsers() {
        return this.numberOfUsers;
    }

    public int getNumberOfGroups() {
        return this.numberOfGroups;
    }

    public Duration getTotalMigrationTime() {
        return this.totalMigrationTime;
    }
}

