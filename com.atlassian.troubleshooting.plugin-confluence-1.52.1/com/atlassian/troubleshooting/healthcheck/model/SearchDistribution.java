/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonValue
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.healthcheck.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SearchDistribution {
    ELASTICSEARCH("Elasticsearch"),
    OPENSEARCH("OpenSearch"),
    UNKNOWN("Unknown");

    private static final Logger LOG;
    private final String label;

    private SearchDistribution(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    @JsonCreator
    public static SearchDistribution fromString(String searchDistribution) {
        try {
            return SearchDistribution.valueOf(searchDistribution.toUpperCase());
        }
        catch (IllegalArgumentException iae) {
            LOG.debug("Unknown search distribution: {}", (Object)searchDistribution, (Object)iae);
            return UNKNOWN;
        }
    }

    @JsonValue
    public String stringValue() {
        return this.name().toLowerCase();
    }

    static {
        LOG = LoggerFactory.getLogger(SearchDistribution.class);
    }
}

