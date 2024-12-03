/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertCount
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.AlertCount;
import com.atlassian.diagnostics.internal.rest.RestEntity;
import com.google.common.collect.ImmutableMap;
import java.util.stream.Collectors;

public class RestAlertCount
extends RestEntity {
    public RestAlertCount(AlertCount count) {
        this.put("issueId", count.getIssue().getId());
        this.put("pluginKey", count.getPlugin().getKey());
        this.put("pluginName", count.getPlugin().getName());
        this.putIfNotNull("pluginVersion", count.getPlugin().getVersion());
        this.put("nodeCounts", count.getCountsByNodeName().entrySet().stream().map(entry -> ImmutableMap.of((Object)"nodeName", entry.getKey(), (Object)"count", entry.getValue())).collect(Collectors.toList()));
        this.put("totalCount", count.getTotalCount());
    }
}

