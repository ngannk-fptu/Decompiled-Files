/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.web.conditions;

import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.conditions.AbstractCompositeCondition;
import java.util.Map;

public class OrCompositeCondition
extends AbstractCompositeCondition {
    public OrCompositeCondition() {
        super(new Condition[0]);
    }

    public OrCompositeCondition(Condition ... conditions) {
        super(conditions);
    }

    @Override
    public boolean shouldDisplay(Map<String, Object> context) {
        for (Condition condition : this.conditions) {
            if (!condition.shouldDisplay(context)) continue;
            return true;
        }
        return false;
    }
}

