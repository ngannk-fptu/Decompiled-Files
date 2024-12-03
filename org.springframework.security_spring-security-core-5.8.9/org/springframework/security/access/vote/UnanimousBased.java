/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.vote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.core.Authentication;

@Deprecated
public class UnanimousBased
extends AbstractAccessDecisionManager {
    public UnanimousBased(List<AccessDecisionVoter<?>> decisionVoters) {
        super(decisionVoters);
    }

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) throws AccessDeniedException {
        int grant = 0;
        ArrayList<ConfigAttribute> singleAttributeList = new ArrayList<ConfigAttribute>(1);
        singleAttributeList.add(null);
        for (ConfigAttribute attribute : attributes) {
            singleAttributeList.set(0, attribute);
            for (AccessDecisionVoter<?> voter : this.getDecisionVoters()) {
                int result = voter.vote(authentication, object, singleAttributeList);
                switch (result) {
                    case 1: {
                        ++grant;
                        break;
                    }
                    case -1: {
                        throw new AccessDeniedException(this.messages.getMessage("AbstractAccessDecisionManager.accessDenied", "Access is denied"));
                    }
                }
            }
        }
        if (grant > 0) {
            return;
        }
        this.checkAllowIfAllAbstainDecisions();
    }
}

