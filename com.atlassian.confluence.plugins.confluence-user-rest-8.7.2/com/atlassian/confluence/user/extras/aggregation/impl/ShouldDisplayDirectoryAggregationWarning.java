/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.user.extras.aggregation.impl;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.extras.aggregation.impl.AggregationWarningManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

@Deprecated
public class ShouldDisplayDirectoryAggregationWarning
implements Condition {
    private final AggregationWarningManager warningManager;

    public ShouldDisplayDirectoryAggregationWarning(AggregationWarningManager warningManager) {
        this.warningManager = warningManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        return this.warningManager.shouldShow(AuthenticatedUserThreadLocal.getUsername());
    }
}

