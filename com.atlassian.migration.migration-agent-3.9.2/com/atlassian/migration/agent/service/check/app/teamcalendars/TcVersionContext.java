/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.migration.agent.service.check.app.teamcalendars;

import com.atlassian.cmpt.check.base.CheckContext;
import com.atlassian.plugin.Plugin;
import java.util.List;

public class TcVersionContext
implements CheckContext {
    public final List<Plugin> enabledPlugins;

    public TcVersionContext(List<Plugin> enabledPlugins) {
        this.enabledPlugins = enabledPlugins;
    }
}

