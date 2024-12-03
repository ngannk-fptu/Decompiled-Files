/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.TrashRetentionRule
 */
package com.atlassian.confluence.impl.retention.rules;

import com.atlassian.confluence.api.model.retention.TrashRetentionRule;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.impl.retention.rules.EvaluatedTrash;
import java.util.List;

public interface TrashRuleEvaluator {
    public List<EvaluatedTrash> evaluate(TrashRetentionRule var1, List<SpaceContentEntityObject> var2);
}

