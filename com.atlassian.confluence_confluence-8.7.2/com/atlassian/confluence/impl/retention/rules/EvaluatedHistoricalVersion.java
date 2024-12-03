/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.RuleScope
 */
package com.atlassian.confluence.impl.retention.rules;

import com.atlassian.confluence.api.model.retention.RuleScope;
import com.atlassian.confluence.impl.retention.rules.HistoricalVersion;
import java.util.Objects;

public class EvaluatedHistoricalVersion {
    private final RuleScope ruleScope;
    private final Boolean shouldBeDeleted;
    private final HistoricalVersion historicalVersion;

    public EvaluatedHistoricalVersion(HistoricalVersion historicalVersion, RuleScope ruleScope, Boolean shouldBeDeleted) {
        this.historicalVersion = historicalVersion;
        this.ruleScope = ruleScope;
        this.shouldBeDeleted = shouldBeDeleted;
    }

    public RuleScope getRuleScope() {
        return this.ruleScope;
    }

    public Boolean getShouldBeDeleted() {
        return this.shouldBeDeleted;
    }

    public HistoricalVersion getHistoricalVersion() {
        return this.historicalVersion;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof EvaluatedHistoricalVersion)) {
            return false;
        }
        EvaluatedHistoricalVersion evaluatedHistoricalVersion = (EvaluatedHistoricalVersion)obj;
        return Objects.equals(evaluatedHistoricalVersion.ruleScope, this.ruleScope) && Objects.equals(evaluatedHistoricalVersion.shouldBeDeleted, this.shouldBeDeleted) && Objects.equals(evaluatedHistoricalVersion.historicalVersion, this.historicalVersion);
    }

    public int hashCode() {
        return Objects.hash(this.ruleScope, this.shouldBeDeleted, this.historicalVersion);
    }
}

