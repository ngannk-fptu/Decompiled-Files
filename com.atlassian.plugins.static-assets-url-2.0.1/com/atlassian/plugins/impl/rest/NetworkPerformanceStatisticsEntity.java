/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.impl.rest;

import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class NetworkPerformanceStatisticsEntity {
    @JsonProperty
    private final List<Long> recentTransferCosts;

    public NetworkPerformanceStatisticsEntity(List<Long> recentTransferCosts) {
        this.recentTransferCosts = recentTransferCosts;
    }
}

