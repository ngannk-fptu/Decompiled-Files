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
public class AffirmativeBased
extends AbstractAccessDecisionManager {
    public AffirmativeBased(List<AccessDecisionVoter<?>> decisionVoters) {
        super(decisionVoters);
    }

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException {
        int deny = 0;
        for (AccessDecisionVoter<?> voter : this.getDecisionVoters()) {
            int result = voter.vote(authentication, object, configAttributes);
            switch (result) {
                case 1: {
                    return;
                }
                case -1: {
                    ++deny;
                    break;
                }
            }
        }
        if (deny > 0) {
            throw new AccessDeniedException(this.messages.getMessage("AbstractAccessDecisionManager.accessDenied", "Access is denied"));
        }
        this.checkAllowIfAllAbstainDecisions();
    }
}

