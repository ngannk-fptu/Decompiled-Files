/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.baseconditions.CompositeCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.plugin.webresource.condition;

import com.atlassian.plugin.web.baseconditions.CompositeCondition;
import com.atlassian.plugin.webresource.condition.DecoratingCondition;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.util.ArrayList;
import java.util.List;

abstract class DecoratingCompositeCondition
implements CompositeCondition<DecoratingCondition>,
DecoratingCondition {
    protected List<DecoratingCondition> conditions = new ArrayList<DecoratingCondition>();

    public void addCondition(DecoratingCondition condition) {
        this.conditions.add(condition);
    }

    @Override
    public void addToUrl(UrlBuilder urlBuilder, UrlBuildingStrategy urlBuilderStrategy) {
        for (DecoratingCondition condition : this.conditions) {
            condition.addToUrl(urlBuilder, urlBuilderStrategy);
        }
    }

    @Override
    public Dimensions computeDimensions() {
        Dimensions d = Dimensions.empty();
        for (DecoratingCondition condition : this.conditions) {
            d = d.product(condition.computeDimensions());
        }
        return d;
    }

    @Override
    public boolean canEncodeStateIntoUrl() {
        for (DecoratingCondition condition : this.conditions) {
            if (condition.canEncodeStateIntoUrl()) continue;
            return false;
        }
        return true;
    }

    public List<DecoratingCondition> getConditions() {
        return this.conditions;
    }
}

