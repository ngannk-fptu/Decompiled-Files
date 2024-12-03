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

import com.atlassian.migration.agent.service.impl.MigrationTimeEstimationUtils;
import com.atlassian.migration.json.DurationLongDeserializer;
import com.atlassian.migration.json.DurationLongSerializer;
import java.time.Duration;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@ParametersAreNonnullByDefault
public final class GlobalEntitiesStats {
    @JsonProperty
    private final long numberOfGlobalPageTemplates;
    @JsonProperty
    private final long numberOfEditedSystemTemplates;
    @JsonSerialize(using=DurationLongSerializer.class)
    @JsonDeserialize(using=DurationLongDeserializer.class)
    @JsonProperty
    private final Duration totalMigrationTime;

    @JsonCreator
    public GlobalEntitiesStats(@JsonProperty(value="numberOfGlobalEntities") long numberOfGlobalPageTemplates, @JsonProperty(value="numberOfEditedSystemTemplates") long numberOfEditedSystemTemplates) {
        this.numberOfGlobalPageTemplates = numberOfGlobalPageTemplates;
        this.numberOfEditedSystemTemplates = numberOfEditedSystemTemplates;
        this.totalMigrationTime = MigrationTimeEstimationUtils.estimateGlobalEntitiesMigrationTime(this.numberOfGlobalPageTemplates + this.numberOfEditedSystemTemplates);
    }

    public long getNumberOfGlobalPageTemplates() {
        return this.numberOfGlobalPageTemplates;
    }

    public Duration getTotalMigrationTime() {
        return this.totalMigrationTime;
    }

    public long getNumberOfEditedSystemTemplates() {
        return this.numberOfEditedSystemTemplates;
    }
}

