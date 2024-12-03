/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.analytics;

import com.atlassian.migration.agent.dto.analytics.AnalyticsEventDto;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ScreenAnalyticsEventDto
extends AnalyticsEventDto {
    @JsonProperty
    private final String name;

    @JsonCreator
    public ScreenAnalyticsEventDto(@JsonProperty(value="timestamp") long timestamp, @JsonProperty(value="name") String name, @JsonProperty(value="attributes") Map<String, Object> attributes) {
        super(timestamp, null, attributes);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

