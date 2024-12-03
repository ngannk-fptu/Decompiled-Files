/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.lang.NonNull
 */
package org.springframework.security.authorization.method;

import java.lang.reflect.Method;
import java.util.function.Supplier;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.method.AbstractAuthorizationManagerRegistry;
import org.springframework.security.authorization.method.AuthorizationAnnotationUtils;
import org.springframework.security.core.Authentication;

public final class SecuredAuthorizationManager
implements AuthorizationManager<MethodInvocation> {
    private final SecuredAuthorizationManagerRegistry registry = new SecuredAuthorizationManagerRegistry();

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation mi) {
        AuthorizationManager<MethodInvocation> delegate = this.registry.getManager(mi);
        return delegate.check(authentication, mi);
    }

    private static final class SecuredAuthorizationManagerRegistry
    extends AbstractAuthorizationManagerRegistry {
        private SecuredAuthorizationManagerRegistry() {
        }

        @Override
        @NonNull
        AuthorizationManager<MethodInvocation> resolveManager(Method method, Class<?> targetClass) {
            Method specificMethod = AopUtils.getMostSpecificMethod((Method)method, targetClass);
            Secured secured = this.findSecuredAnnotation(specificMethod);
            return secured != null ? AuthorityAuthorizationManager.hasAnyAuthority(secured.value()) : NULL_MANAGER;
        }

        private Secured findSecuredAnnotation(Method method) {
            Secured secured = AuthorizationAnnotationUtils.findUniqueAnnotation(method, Secured.class);
            return secured != null ? secured : AuthorizationAnnotationUtils.findUniqueAnnotation(method.getDeclaringClass(), Secured.class);
        }
    }
}

