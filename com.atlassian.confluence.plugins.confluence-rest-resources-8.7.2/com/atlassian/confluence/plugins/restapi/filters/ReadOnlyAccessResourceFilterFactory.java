/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.settings.SettingsService
 *  com.sun.jersey.api.model.AbstractMethod
 *  com.sun.jersey.spi.container.ResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilterFactory
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.restapi.filters;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.settings.SettingsService;
import com.atlassian.confluence.plugins.restapi.filters.ReadOnlyAccessResourceFilter;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReadOnlyAccessResourceFilterFactory
implements ResourceFilterFactory {
    private final AccessModeService accessModeService;
    private final SettingsService settingsService;
    private static final String READ_ONLY_ACCESS_ALLOWED_ANNOTATION = "ReadOnlyAccessAllowed";
    private static final String READ_ONLY_ACCESS_BLOCKED_ANNOTATION = "ReadOnlyAccessBlocked";

    @Autowired
    public ReadOnlyAccessResourceFilterFactory(AccessModeService accessModeService, SettingsService settingsService) {
        this.accessModeService = accessModeService;
        this.settingsService = settingsService;
    }

    public List<ResourceFilter> create(AbstractMethod abstractMethod) {
        if (this.hasReadOnlyAccessAllowedAnnotation(abstractMethod)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new ReadOnlyAccessResourceFilter(this.accessModeService, this.settingsService, this.hasReadOnlyAccessBlockedAnnotation(abstractMethod)));
    }

    private boolean hasReadOnlyAccessAllowedAnnotation(AbstractMethod abstractMethod) {
        return this.isAnnotated((AnnotatedElement)abstractMethod, READ_ONLY_ACCESS_ALLOWED_ANNOTATION) || this.isAnnotated((AnnotatedElement)abstractMethod.getResource(), READ_ONLY_ACCESS_ALLOWED_ANNOTATION) || this.isAnnotated(abstractMethod.getResource().getResourceClass().getPackage(), READ_ONLY_ACCESS_ALLOWED_ANNOTATION);
    }

    private boolean hasReadOnlyAccessBlockedAnnotation(AbstractMethod abstractMethod) {
        return this.isAnnotated((AnnotatedElement)abstractMethod, READ_ONLY_ACCESS_BLOCKED_ANNOTATION) || this.isAnnotated((AnnotatedElement)abstractMethod.getResource(), READ_ONLY_ACCESS_BLOCKED_ANNOTATION) || this.isAnnotated(abstractMethod.getResource().getResourceClass().getPackage(), READ_ONLY_ACCESS_BLOCKED_ANNOTATION);
    }

    private boolean isAnnotated(@NonNull AnnotatedElement annotatedElement, @NonNull String simpleAnnotationName) {
        Annotation[] annotations = annotatedElement.getDeclaredAnnotations();
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (!simpleAnnotationName.equals(annotation.annotationType().getSimpleName())) continue;
                return true;
            }
        }
        return false;
    }
}

