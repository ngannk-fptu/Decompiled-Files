/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.model.stats;

import com.atlassian.migration.agent.model.stats.ContentSummary;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ParametersAreNonnullByDefault
public final class SpaceStats {
    @JsonProperty
    private final String spaceKey;
    @JsonProperty
    private final ContentSummary summary;

    @JsonCreator
    public SpaceStats(@JsonProperty(value="spaceKey") String spaceKey, @JsonProperty(value="summary") ContentSummary summary) {
        this.spaceKey = spaceKey;
        this.summary = summary;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public ContentSummary getSummary() {
        return this.summary;
    }
}

