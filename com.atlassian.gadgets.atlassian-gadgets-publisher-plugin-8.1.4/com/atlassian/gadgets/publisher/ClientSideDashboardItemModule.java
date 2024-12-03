/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.plugins.DashboardItemModule$DirectoryDefinition
 *  com.atlassian.plugin.web.Condition
 *  io.atlassian.fugue.Option
 */
package com.atlassian.gadgets.publisher;

import com.atlassian.gadgets.plugins.DashboardItemModule;
import com.atlassian.gadgets.publisher.AbstractDashboardItemModule;
import com.atlassian.plugin.web.Condition;
import io.atlassian.fugue.Option;
import java.io.Writer;
import java.util.Map;

public class ClientSideDashboardItemModule
extends AbstractDashboardItemModule {
    public ClientSideDashboardItemModule(Option<String> amdModule, boolean configurable, Option<DashboardItemModule.DirectoryDefinition> description, Condition condition, Option<String> webResourceKey) {
        super(description, amdModule, configurable, condition, webResourceKey);
    }

    public void renderContent(Writer writer, Map<String, Object> context) {
    }
}

