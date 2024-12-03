/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.RedirectRule;
import com.amazonaws.services.s3.model.RoutingRuleCondition;
import java.io.Serializable;

public class RoutingRule
implements Serializable {
    RoutingRuleCondition condition;
    RedirectRule redirect;

    public void setCondition(RoutingRuleCondition condition) {
        this.condition = condition;
    }

    public RoutingRuleCondition getCondition() {
        return this.condition;
    }

    public RoutingRule withCondition(RoutingRuleCondition condition) {
        this.setCondition(condition);
        return this;
    }

    public void setRedirect(RedirectRule redirect) {
        this.redirect = redirect;
    }

    public RedirectRule getRedirect() {
        return this.redirect;
    }

    public RoutingRule withRedirect(RedirectRule redirect) {
        this.setRedirect(redirect);
        return this;
    }
}

