/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.MessageSource
 *  org.springframework.context.MessageSourceAware
 *  org.springframework.context.support.MessageSourceAccessor
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.vote;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;

@Deprecated
public abstract class AbstractAccessDecisionManager
implements AccessDecisionManager,
InitializingBean,
MessageSourceAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private List<AccessDecisionVoter<?>> decisionVoters;
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private boolean allowIfAllAbstainDecisions = false;

    protected AbstractAccessDecisionManager(List<AccessDecisionVoter<?>> decisionVoters) {
        Assert.notEmpty(decisionVoters, (String)"A list of AccessDecisionVoters is required");
        this.decisionVoters = decisionVoters;
    }

    public void afterPropertiesSet() {
        Assert.notEmpty(this.decisionVoters, (String)"A list of AccessDecisionVoters is required");
        Assert.notNull((Object)this.messages, (String)"A message source must be set");
    }

    protected final void checkAllowIfAllAbstainDecisions() {
        if (!this.isAllowIfAllAbstainDecisions()) {
            throw new AccessDeniedException(this.messages.getMessage("AbstractAccessDecisionManager.accessDenied", "Access is denied"));
        }
    }

    public List<AccessDecisionVoter<?>> getDecisionVoters() {
        return this.decisionVoters;
    }

    public boolean isAllowIfAllAbstainDecisions() {
        return this.allowIfAllAbstainDecisions;
    }

    public void setAllowIfAllAbstainDecisions(boolean allowIfAllAbstainDecisions) {
        this.allowIfAllAbstainDecisions = allowIfAllAbstainDecisions;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        for (AccessDecisionVoter<?> voter : this.decisionVoters) {
            if (!voter.supports(attribute)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        for (AccessDecisionVoter<?> voter : this.decisionVoters) {
            if (voter.supports(clazz)) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        return this.getClass().getSimpleName() + " [DecisionVoters=" + this.decisionVoters + ", AllowIfAllAbstainDecisions=" + this.allowIfAllAbstainDecisions + "]";
    }
}

