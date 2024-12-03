/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.migration.agent.dto.assessment;

import java.util.Arrays;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum AppUsageStatus {
    RUNNING("Running"),
    SUCCESS("Success"),
    ERROR("Error");

    private final String value;

    private AppUsageStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static AppUsageStatus fromValue(String value) {
        return Arrays.stream(AppUsageStatus.values()).filter(it -> it.value.equals(value)).findAny().orElseThrow(() -> new IllegalArgumentException(String.format("Failed to parse AppUsageStatus from raw value [%s]", value)));
    }

    @JsonValue
    public String value() {
        return this.value;
    }
}

