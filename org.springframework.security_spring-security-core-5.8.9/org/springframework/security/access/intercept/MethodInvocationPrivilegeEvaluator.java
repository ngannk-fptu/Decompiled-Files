/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.intercept;

import java.util.Collection;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

@Deprecated
public class MethodInvocationPrivilegeEvaluator
implements InitializingBean {
    protected static final Log logger = LogFactory.getLog(MethodInvocationPrivilegeEvaluator.class);
    private AbstractSecurityInterceptor securityInterceptor;

    public void afterPropertiesSet() {
        Assert.notNull((Object)this.securityInterceptor, (String)"SecurityInterceptor required");
    }

    public boolean isAllowed(MethodInvocation invocation, Authentication authentication) {
        Assert.notNull((Object)invocation, (String)"MethodInvocation required");
        Assert.notNull((Object)invocation.getMethod(), (String)"MethodInvocation must provide a non-null getMethod()");
        Collection<ConfigAttribute> attrs = this.securityInterceptor.obtainSecurityMetadataSource().getAttributes(invocation);
        if (attrs == null) {
            return !this.securityInterceptor.isRejectPublicInvocations();
        }
        if (authentication == null || authentication.getAuthorities().isEmpty()) {
            return false;
        }
        try {
            this.securityInterceptor.getAccessDecisionManager().decide(authentication, invocation, attrs);
            return true;
        }
        catch (AccessDeniedException unauthorized) {
            logger.debug((Object)LogMessage.format((String)"%s denied for %s", (Object)invocation, (Object)authentication), (Throwable)unauthorized);
            return false;
        }
    }

    public void setSecurityInterceptor(AbstractSecurityInterceptor securityInterceptor) {
        Assert.notNull((Object)securityInterceptor, (String)"AbstractSecurityInterceptor cannot be null");
        Assert.isTrue((boolean)MethodInvocation.class.equals(securityInterceptor.getSecureObjectClass()), (String)"AbstractSecurityInterceptor does not support MethodInvocations");
        Assert.notNull((Object)securityInterceptor.getAccessDecisionManager(), (String)"AbstractSecurityInterceptor must provide a non-null AccessDecisionManager");
        this.securityInterceptor = securityInterceptor;
    }
}

