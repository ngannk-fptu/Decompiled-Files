/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.plugins.DashboardItemModule
 *  com.atlassian.gadgets.plugins.DashboardItemModule$DirectoryDefinition
 *  com.atlassian.plugin.web.Condition
 *  com.google.common.base.Preconditions
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 */
package com.atlassian.gadgets.publisher;

import com.atlassian.gadgets.plugins.DashboardItemModule;
import com.atlassian.plugin.web.Condition;
import com.google.common.base.Preconditions;
import io.atlassian.fugue.Option;
import javax.annotation.Nonnull;

public abstract class AbstractDashboardItemModule
implements DashboardItemModule {
    private final Option<DashboardItemModule.DirectoryDefinition> directoryDefinition;
    private final Option<String> amdModule;
    private final Option<String> webResourceKey;
    private final boolean configurable;
    private final Condition condition;

    public AbstractDashboardItemModule(Option<DashboardItemModule.DirectoryDefinition> directoryDefinition, Option<String> amdModule, boolean configurable, @Nonnull Condition condition, Option<String> webResourceKey) {
        this.directoryDefinition = directoryDefinition;
        this.amdModule = amdModule;
        this.configurable = configurable;
        this.webResourceKey = webResourceKey;
        this.condition = (Condition)Preconditions.checkNotNull((Object)condition);
    }

    public Option<DashboardItemModule.DirectoryDefinition> getDirectoryDefinition() {
        return this.directoryDefinition;
    }

    public Option<String> getAMDModule() {
        return this.amdModule;
    }

    public Option<String> getWebResourceKey() {
        return this.webResourceKey;
    }

    public boolean isConfigurable() {
        return this.configurable;
    }

    @Nonnull
    public Condition getCondition() {
        return this.condition;
    }
}

