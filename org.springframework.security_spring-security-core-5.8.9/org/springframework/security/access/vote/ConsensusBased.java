/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.vote;

import java.util.Collection;
import java.util.List;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.core.Authentication;

@Deprecated
public class ConsensusBased
extends AbstractAccessDecisionManager {
    private boolean allowIfEqualGrantedDeniedDecisions = true;

    public ConsensusBased(List<AccessDecisionVoter<?>> decisionVoters) {
        super(decisionVoters);
    }

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException {
        int grant = 0;
        int deny = 0;
        for (AccessDecisionVoter<?> voter : this.getDecisionVoters()) {
            int result = voter.vote(authentication, object, configAttributes);
            switch (result) {
                case 1: {
                    ++grant;
                    break;
                }
                case -1: {
                    ++deny;
                    break;
                }
            }
        }
        if (grant > deny) {
            return;
        }
        if (deny > grant) {
            throw new AccessDeniedException(this.messages.getMessage("AbstractAccessDecisionManager.accessDenied", "Access is denied"));
        }
        if (grant == deny && grant != 0) {
            if (this.allowIfEqualGrantedDeniedDecisions) {
                return;
            }
            throw new AccessDeniedException(this.messages.getMessage("AbstractAccessDecisionManager.accessDenied", "Access is denied"));
        }
        this.checkAllowIfAllAbstainDecisions();
    }

    public boolean isAllowIfEqualGrantedDeniedDecisions() {
        return this.allowIfEqualGrantedDeniedDecisions;
    }

    public void setAllowIfEqualGrantedDeniedDecisions(boolean allowIfEqualGrantedDeniedDecisions) {
        this.allowIfEqualGrantedDeniedDecisions = allowIfEqualGrantedDeniedDecisions;
    }
}

