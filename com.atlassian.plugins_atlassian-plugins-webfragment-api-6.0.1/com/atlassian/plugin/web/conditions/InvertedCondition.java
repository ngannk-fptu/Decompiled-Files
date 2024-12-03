/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 */
package com.atlassian.plugin.web.conditions;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class InvertedCondition
implements Condition {
    private Condition wrappedCondition;

    public InvertedCondition(Condition wrappedCondition) {
        this.wrappedCondition = wrappedCondition;
    }

    @Override
    public void init(Map<String, String> params) throws PluginParseException {
    }

    @Override
    public boolean shouldDisplay(Map<String, Object> context) {
        return !this.wrappedCondition.shouldDisplay(context);
    }
}

