/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.QueryParams
 */
package com.atlassian.plugin.webresource.condition;

import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.DecoratingAndCompositeCondition;
import com.atlassian.plugin.webresource.condition.DecoratingCompositeCondition;
import com.atlassian.plugin.webresource.condition.DecoratingCondition;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import java.util.Map;

class DecoratingOrCompositeCondition
extends DecoratingCompositeCondition {
    DecoratingOrCompositeCondition() {
    }

    @Override
    public boolean shouldDisplay(QueryParams params) {
        for (DecoratingCondition condition : this.conditions) {
            if (!condition.shouldDisplay(params)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldDisplayImmediate(Map<String, Object> context, UrlBuildingStrategy urlBuilderStrategy) {
        for (DecoratingCondition condition : this.conditions) {
            if (!condition.shouldDisplayImmediate(context, urlBuilderStrategy)) continue;
            return true;
        }
        return false;
    }

    @Override
    public DecoratingCondition invertCondition() {
        DecoratingAndCompositeCondition andCondition = new DecoratingAndCompositeCondition();
        for (DecoratingCondition condition : this.conditions) {
            andCondition.addCondition(condition.invertCondition());
        }
        return andCondition;
    }
}

