/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.security.DenyAll
 *  javax.annotation.security.PermitAll
 *  javax.annotation.security.RolesAllowed
 *  org.springframework.core.annotation.AnnotationUtils
 */
package org.springframework.security.access.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.Jsr250SecurityConfig;
import org.springframework.security.access.method.AbstractFallbackMethodSecurityMetadataSource;

@Deprecated
public class Jsr250MethodSecurityMetadataSource
extends AbstractFallbackMethodSecurityMetadataSource {
    private String defaultRolePrefix = "ROLE_";

    public void setDefaultRolePrefix(String defaultRolePrefix) {
        this.defaultRolePrefix = defaultRolePrefix;
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Class<?> clazz) {
        return this.processAnnotations(clazz.getAnnotations());
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Method method, Class<?> targetClass) {
        return this.processAnnotations(AnnotationUtils.getAnnotations((Method)method));
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    private List<ConfigAttribute> processAnnotations(Annotation[] annotations) {
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        ArrayList<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>();
        for (Annotation annotation : annotations) {
            if (annotation instanceof DenyAll) {
                attributes.add(Jsr250SecurityConfig.DENY_ALL_ATTRIBUTE);
                return attributes;
            }
            if (annotation instanceof PermitAll) {
                attributes.add(Jsr250SecurityConfig.PERMIT_ALL_ATTRIBUTE);
                return attributes;
            }
            if (!(annotation instanceof RolesAllowed)) continue;
            RolesAllowed ra = (RolesAllowed)annotation;
            for (String allowed : ra.value()) {
                String defaultedAllowed = this.getRoleWithDefaultPrefix(allowed);
                attributes.add(new Jsr250SecurityConfig(defaultedAllowed));
            }
            return attributes;
        }
        return null;
    }

    private String getRoleWithDefaultPrefix(String role) {
        if (role == null) {
            return role;
        }
        if (this.defaultRolePrefix == null || this.defaultRolePrefix.length() == 0) {
            return role;
        }
        if (role.startsWith(this.defaultRolePrefix)) {
            return role;
        }
        return this.defaultRolePrefix + role;
    }
}

