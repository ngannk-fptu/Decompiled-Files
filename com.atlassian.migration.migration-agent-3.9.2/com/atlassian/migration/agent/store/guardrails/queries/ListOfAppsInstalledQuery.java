/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.results.ListOfAppsInstalledQueryResult;
import com.atlassian.plugin.Plugin;
import java.util.Collection;

public class ListOfAppsInstalledQuery
implements GrQuery<ListOfAppsInstalledQueryResult>,
L1AssessmentQuery<ListOfAppsInstalledQueryResult> {
    private static final String LIST_OF_APPS_INSTALLED = "LIST_OF_APPS_INSTALLED";
    private final PluginManager pluginManager;

    public ListOfAppsInstalledQuery(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public String getQueryId() {
        return LIST_OF_APPS_INSTALLED;
    }

    @Override
    public ListOfAppsInstalledQueryResult execute() {
        Collection<Plugin> result = this.pluginManager.getActualUserInstalledPlugins();
        return new ListOfAppsInstalledQueryResult(result);
    }
}

