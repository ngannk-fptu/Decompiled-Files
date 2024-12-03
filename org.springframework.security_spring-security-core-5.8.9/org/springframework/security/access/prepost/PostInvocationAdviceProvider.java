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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AfterInvocationProvider;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PostInvocationAuthorizationAdvice;
import org.springframework.security.core.Authentication;

@Deprecated
public class PostInvocationAdviceProvider
implements AfterInvocationProvider {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final PostInvocationAuthorizationAdvice postAdvice;

    public PostInvocationAdviceProvider(PostInvocationAuthorizationAdvice postAdvice) {
        this.postAdvice = postAdvice;
    }

    @Override
    public Object decide(Authentication authentication, Object object, Collection<ConfigAttribute> config, Object returnedObject) throws AccessDeniedException {
        PostInvocationAttribute postInvocationAttribute = this.findPostInvocationAttribute(config);
        if (postInvocationAttribute == null) {
            return returnedObject;
        }
        return this.postAdvice.after(authentication, (MethodInvocation)object, postInvocationAttribute, returnedObject);
    }

    private PostInvocationAttribute findPostInvocationAttribute(Collection<ConfigAttribute> config) {
        for (ConfigAttribute attribute : config) {
            if (!(attribute instanceof PostInvocationAttribute)) continue;
            return (PostInvocationAttribute)attribute;
        }
        return null;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute instanceof PostInvocationAttribute;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz);
    }
}

