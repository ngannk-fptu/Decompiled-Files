/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.security.access.prepost;

import java.util.Collection;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice;
import org.springframework.security.core.Authentication;

@Deprecated
public class PreInvocationAuthorizationAdviceVoter
implements AccessDecisionVoter<MethodInvocation> {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final PreInvocationAuthorizationAdvice preAdvice;

    public PreInvocationAuthorizationAdviceVoter(PreInvocationAuthorizationAdvice pre) {
        this.preAdvice = pre;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute instanceof PreInvocationAttribute;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication, MethodInvocation method, Collection<ConfigAttribute> attributes) {
        PreInvocationAttribute preAttr = this.findPreInvocationAttribute(attributes);
        if (preAttr == null) {
            return 0;
        }
        return this.preAdvice.before(authentication, method, preAttr) ? 1 : -1;
    }

    private PreInvocationAttribute findPreInvocationAttribute(Collection<ConfigAttribute> config) {
        for (ConfigAttribute attribute : config) {
            if (!(attribute instanceof PreInvocationAttribute)) continue;
            return (PreInvocationAttribute)attribute;
        }
        return null;
    }
}

