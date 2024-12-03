/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.cluster.monitoring.spi.model;

import java.io.Serializable;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class MonitoringError
implements Serializable {
    @JsonProperty
    private final String error;

    @JsonCreator
    public MonitoringError(@JsonProperty String error) {
        this.error = Objects.requireNonNull(error);
    }

    public String getError() {
        return this.error;
    }
}

