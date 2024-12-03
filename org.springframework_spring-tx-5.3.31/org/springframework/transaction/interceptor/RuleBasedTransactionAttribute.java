/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.interceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;

public class RuleBasedTransactionAttribute
extends DefaultTransactionAttribute
implements Serializable {
    public static final String PREFIX_ROLLBACK_RULE = "-";
    public static final String PREFIX_COMMIT_RULE = "+";
    @Nullable
    private List<RollbackRuleAttribute> rollbackRules;

    public RuleBasedTransactionAttribute() {
    }

    public RuleBasedTransactionAttribute(RuleBasedTransactionAttribute other) {
        super(other);
        this.rollbackRules = other.rollbackRules != null ? new ArrayList<RollbackRuleAttribute>(other.rollbackRules) : null;
    }

    public RuleBasedTransactionAttribute(int propagationBehavior, List<RollbackRuleAttribute> rollbackRules) {
        super(propagationBehavior);
        this.rollbackRules = rollbackRules;
    }

    public void setRollbackRules(List<RollbackRuleAttribute> rollbackRules) {
        this.rollbackRules = rollbackRules;
    }

    public List<RollbackRuleAttribute> getRollbackRules() {
        if (this.rollbackRules == null) {
            this.rollbackRules = new ArrayList<RollbackRuleAttribute>();
        }
        return this.rollbackRules;
    }

    @Override
    public boolean rollbackOn(Throwable ex) {
        RollbackRuleAttribute winner = null;
        int deepest = Integer.MAX_VALUE;
        if (this.rollbackRules != null) {
            for (RollbackRuleAttribute rule : this.rollbackRules) {
                int depth = rule.getDepth(ex);
                if (depth < 0 || depth >= deepest) continue;
                deepest = depth;
                winner = rule;
            }
        }
        if (winner == null) {
            return super.rollbackOn(ex);
        }
        return !(winner instanceof NoRollbackRuleAttribute);
    }

    @Override
    public String toString() {
        StringBuilder result = this.getAttributeDescription();
        if (this.rollbackRules != null) {
            for (RollbackRuleAttribute rule : this.rollbackRules) {
                String sign = rule instanceof NoRollbackRuleAttribute ? PREFIX_COMMIT_RULE : PREFIX_ROLLBACK_RULE;
                result.append(',').append(sign).append(rule.getExceptionName());
            }
        }
        return result.toString();
    }
}

