/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.util;

import com.atlassian.plugins.rest.common.security.AdminOnly;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.plugins.rest.common.security.LicensedOnly;
import com.atlassian.plugins.rest.common.security.SystemAdminOnly;
import com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess;
import com.atlassian.plugins.rest.common.security.UnrestrictedAccess;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Function;

public class AnnotationUtils {
    public static final Class<Annotation>[] VALID_ANNOTATION_LIST = new Class[]{SystemAdminOnly.class, AdminOnly.class, LicensedOnly.class, UnlicensedSiteAccess.class, AnonymousSiteAccess.class, UnrestrictedAccess.class, AnonymousAllowed.class};
    private final AbstractMethod abstractMethod;

    public AnnotationUtils(AbstractMethod abstractMethod) {
        this.abstractMethod = abstractMethod;
    }

    public Class<Annotation> getAnnotation() {
        Package aPackage;
        Class<Annotation> annotation = null;
        Method aMethod = this.abstractMethod.getMethod();
        if (aMethod != null) {
            annotation = this.extractAnnotation(aMethod::getAnnotation);
        }
        if (annotation != null) {
            return annotation;
        }
        AbstractResource resource = this.abstractMethod.getResource();
        if (resource != null) {
            annotation = this.extractAnnotation(resource::getAnnotation);
        }
        if (annotation != null) {
            return annotation;
        }
        Package package_ = aMethod != null ? aMethod.getDeclaringClass().getPackage() : (aPackage = resource != null ? resource.getResourceClass().getPackage() : null);
        return aPackage != null ? this.extractAnnotation(aPackage::getAnnotation) : annotation;
    }

    private Class<Annotation> extractAnnotation(Function<Class<Annotation>, Annotation> function) {
        for (Class<Annotation> annotation : VALID_ANNOTATION_LIST) {
            if (function.apply(annotation) == null) continue;
            return annotation;
        }
        return null;
    }
}

