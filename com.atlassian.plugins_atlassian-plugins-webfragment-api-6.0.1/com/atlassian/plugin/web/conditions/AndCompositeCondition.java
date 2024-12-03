/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.web.conditions;

import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.conditions.AbstractCompositeCondition;
import java.util.Map;

public class AndCompositeCondition
extends AbstractCompositeCondition {
    public AndCompositeCondition() {
        super(new Condition[0]);
    }

    public AndCompositeCondition(Condition ... conditions) {
        super(conditions);
    }

    @Override
    public boolean shouldDisplay(Map<String, Object> context) {
        for (Condition condition : this.conditions) {
            if (condition.shouldDisplay(context)) continue;
            return false;
        }
        return true;
    }
}

