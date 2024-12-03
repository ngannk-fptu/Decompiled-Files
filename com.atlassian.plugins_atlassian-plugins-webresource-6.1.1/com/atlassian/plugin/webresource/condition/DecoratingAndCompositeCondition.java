/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.QueryParams
 */
package com.atlassian.plugin.webresource.condition;

import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.DecoratingCompositeCondition;
import com.atlassian.plugin.webresource.condition.DecoratingCondition;
import com.atlassian.plugin.webresource.condition.DecoratingOrCompositeCondition;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import java.util.Map;

public class DecoratingAndCompositeCondition
extends DecoratingCompositeCondition {
    @Override
    public boolean shouldDisplay(QueryParams params) {
        for (DecoratingCondition condition : this.conditions) {
            if (condition.shouldDisplay(params)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean shouldDisplayImmediate(Map<String, Object> context, UrlBuildingStrategy urlBuilderStrategy) {
        for (DecoratingCondition condition : this.conditions) {
            if (condition.shouldDisplayImmediate(context, urlBuilderStrategy)) continue;
            return false;
        }
        return true;
    }

    @Override
    public DecoratingCondition invertCondition() {
        DecoratingOrCompositeCondition orCondition = new DecoratingOrCompositeCondition();
        for (DecoratingCondition condition : this.conditions) {
            orCondition.addCondition(condition.invertCondition());
        }
        return orCondition;
    }
}

