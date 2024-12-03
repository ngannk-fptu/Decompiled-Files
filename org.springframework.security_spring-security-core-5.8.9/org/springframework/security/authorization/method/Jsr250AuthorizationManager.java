/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.security.DenyAll
 *  javax.annotation.security.PermitAll
 *  javax.annotation.security.RolesAllowed
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.core.annotation.AnnotationConfigurationException
 *  org.springframework.lang.NonNull
 *  org.springframework.util.Assert
 */
package org.springframework.security.authorization.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.lang.NonNull;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.method.AbstractAuthorizationManagerRegistry;
import org.springframework.security.authorization.method.AuthorizationAnnotationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

public final class Jsr250AuthorizationManager
implements AuthorizationManager<MethodInvocation> {
    private static final Set<Class<? extends Annotation>> JSR250_ANNOTATIONS = new HashSet<Class<? extends Annotation>>();
    private final Jsr250AuthorizationManagerRegistry registry = new Jsr250AuthorizationManagerRegistry();
    private String rolePrefix = "ROLE_";

    public void setRolePrefix(String rolePrefix) {
        Assert.notNull((Object)rolePrefix, (String)"rolePrefix cannot be null");
        this.rolePrefix = rolePrefix;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation methodInvocation) {
        AuthorizationManager<MethodInvocation> delegate = this.registry.getManager(methodInvocation);
        return delegate.check(authentication, methodInvocation);
    }

    static {
        JSR250_ANNOTATIONS.add(DenyAll.class);
        JSR250_ANNOTATIONS.add(PermitAll.class);
        JSR250_ANNOTATIONS.add(RolesAllowed.class);
    }

    private final class Jsr250AuthorizationManagerRegistry
    extends AbstractAuthorizationManagerRegistry {
        private Jsr250AuthorizationManagerRegistry() {
        }

        @Override
        @NonNull
        AuthorizationManager<MethodInvocation> resolveManager(Method method, Class<?> targetClass) {
            Annotation annotation = this.findJsr250Annotation(method, targetClass);
            if (annotation instanceof DenyAll) {
                return (a, o) -> new AuthorizationDecision(false);
            }
            if (annotation instanceof PermitAll) {
                return (a, o) -> new AuthorizationDecision(true);
            }
            if (annotation instanceof RolesAllowed) {
                RolesAllowed rolesAllowed = (RolesAllowed)annotation;
                return AuthorityAuthorizationManager.hasAnyRole(Jsr250AuthorizationManager.this.rolePrefix, rolesAllowed.value());
            }
            return NULL_MANAGER;
        }

        private Annotation findJsr250Annotation(Method method, Class<?> targetClass) {
            Method specificMethod = AopUtils.getMostSpecificMethod((Method)method, targetClass);
            Annotation annotation = this.findAnnotation(specificMethod);
            return annotation != null ? annotation : this.findAnnotation(specificMethod.getDeclaringClass());
        }

        private Annotation findAnnotation(Method method) {
            HashSet annotations = new HashSet();
            for (Class annotationClass : JSR250_ANNOTATIONS) {
                Object annotation = AuthorizationAnnotationUtils.findUniqueAnnotation(method, annotationClass);
                if (annotation == null) continue;
                annotations.add(annotation);
            }
            if (annotations.isEmpty()) {
                return null;
            }
            if (annotations.size() > 1) {
                throw new AnnotationConfigurationException("The JSR-250 specification disallows DenyAll, PermitAll, and RolesAllowed from appearing on the same method.");
            }
            return (Annotation)annotations.iterator().next();
        }

        private Annotation findAnnotation(Class<?> clazz) {
            HashSet annotations = new HashSet();
            for (Class annotationClass : JSR250_ANNOTATIONS) {
                Object annotation = AuthorizationAnnotationUtils.findUniqueAnnotation(clazz, annotationClass);
                if (annotation == null) continue;
                annotations.add(annotation);
            }
            if (annotations.isEmpty()) {
                return null;
            }
            if (annotations.size() > 1) {
                throw new AnnotationConfigurationException("The JSR-250 specification disallows DenyAll, PermitAll, and RolesAllowed from appearing on the same class definition.");
            }
            return (Annotation)annotations.iterator().next();
        }
    }
}

