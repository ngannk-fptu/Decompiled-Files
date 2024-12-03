/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.web.conditions;

import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class AlwaysDisplayCondition
implements Condition {
    @Override
    public void init(Map<String, String> params) {
    }

    @Override
    public boolean shouldDisplay(Map<String, Object> context) {
        return true;
    }
}

