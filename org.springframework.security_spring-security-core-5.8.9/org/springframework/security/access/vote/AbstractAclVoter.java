/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.vote;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.util.Assert;

@Deprecated
public abstract class AbstractAclVoter
implements AccessDecisionVoter<MethodInvocation> {
    private Class<?> processDomainObjectClass;

    protected Object getDomainObjectInstance(MethodInvocation invocation) {
        Object[] args = invocation.getArguments();
        Class<?>[] params = invocation.getMethod().getParameterTypes();
        for (int i = 0; i < params.length; ++i) {
            if (!this.processDomainObjectClass.isAssignableFrom(params[i])) continue;
            return args[i];
        }
        throw new AuthorizationServiceException("MethodInvocation: " + invocation + " did not provide any argument of type: " + this.processDomainObjectClass);
    }

    public Class<?> getProcessDomainObjectClass() {
        return this.processDomainObjectClass;
    }

    public void setProcessDomainObjectClass(Class<?> processDomainObjectClass) {
        Assert.notNull(processDomainObjectClass, (String)"processDomainObjectClass cannot be set to null");
        this.processDomainObjectClass = processDomainObjectClass;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz);
    }
}

