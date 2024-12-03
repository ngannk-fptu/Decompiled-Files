/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.RuleScope
 */
package com.atlassian.confluence.impl.retention.rules;

import com.atlassian.confluence.api.model.retention.RuleScope;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import java.util.StringJoiner;

public class EvaluatedTrash {
    private final SpaceContentEntityObject trash;
    private final boolean shouldBeDeleted;
    private final RuleScope ruleScope;

    public EvaluatedTrash(SpaceContentEntityObject trash, boolean shouldBeDeleted, RuleScope ruleScope) {
        this.trash = trash;
        this.shouldBeDeleted = shouldBeDeleted;
        this.ruleScope = ruleScope;
    }

    public SpaceContentEntityObject getTrash() {
        return this.trash;
    }

    public boolean shouldBeDeleted() {
        return this.shouldBeDeleted;
    }

    public RuleScope getRuleScope() {
        return this.ruleScope;
    }

    public String toString() {
        return new StringJoiner(", ", EvaluatedTrash.class.getSimpleName() + "[", "]").add("trash=" + this.trash).add("shouldBeDeleted=" + this.shouldBeDeleted).add("ruleScope=" + this.ruleScope).toString();
    }
}

