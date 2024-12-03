/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 */
package com.atlassian.plugin.web.conditions;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.baseconditions.CompositeCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class AbstractCompositeCondition
implements Condition,
CompositeCondition<Condition> {
    protected List<Condition> conditions = new ArrayList<Condition>();

    protected AbstractCompositeCondition(Condition ... conditions) {
        if (conditions != null) {
            this.conditions.addAll(Arrays.asList(conditions));
        }
    }

    @Override
    public void addCondition(Condition condition) {
        this.conditions.add(condition);
    }

    @Override
    public void init(Map<String, String> params) throws PluginParseException {
    }

    @Override
    public abstract boolean shouldDisplay(Map<String, Object> var1);
}

