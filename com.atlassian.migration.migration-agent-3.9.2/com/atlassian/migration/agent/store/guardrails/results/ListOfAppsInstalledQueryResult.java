/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.store.guardrails.results;

import com.atlassian.migration.agent.store.guardrails.GrResult;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentResult;
import com.atlassian.plugin.Plugin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class ListOfAppsInstalledQueryResult
implements GrResult,
L1AssessmentResult {
    @JsonProperty
    List<String> plugins;

    public ListOfAppsInstalledQueryResult(Collection<Plugin> plugins) {
        this.plugins = plugins == null ? new ArrayList<String>() : plugins.stream().map(Plugin::getName).collect(Collectors.toList());
    }

    @Override
    public String generateGrResult() {
        return this.plugins.toString();
    }

    @Override
    public String generateL1AssessmentData() {
        return this.plugins.toString();
    }

    @Generated
    public List<String> getPlugins() {
        return this.plugins;
    }

    @Generated
    public void setPlugins(List<String> plugins) {
        this.plugins = plugins;
    }
}

