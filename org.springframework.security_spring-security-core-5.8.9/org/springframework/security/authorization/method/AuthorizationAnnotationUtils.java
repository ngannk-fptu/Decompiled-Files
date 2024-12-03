/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotationConfigurationException
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.annotation.MergedAnnotation
 *  org.springframework.core.annotation.MergedAnnotations
 *  org.springframework.core.annotation.MergedAnnotations$SearchStrategy
 *  org.springframework.core.annotation.RepeatableContainers
 */
package org.springframework.security.authorization.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;

final class AuthorizationAnnotationUtils {
    static <A extends Annotation> A findUniqueAnnotation(Method method, Class<A> annotationType) {
        MergedAnnotations mergedAnnotations = MergedAnnotations.from((AnnotatedElement)method, (MergedAnnotations.SearchStrategy)MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, (RepeatableContainers)RepeatableContainers.none());
        if (AuthorizationAnnotationUtils.hasDuplicate(mergedAnnotations, annotationType)) {
            throw new AnnotationConfigurationException("Found more than one annotation of type " + annotationType + " attributed to " + method + " Please remove the duplicate annotations and publish a bean to handle your authorization logic.");
        }
        return (A)AnnotationUtils.findAnnotation((Method)method, annotationType);
    }

    static <A extends Annotation> A findUniqueAnnotation(Class<?> type, Class<A> annotationType) {
        MergedAnnotations mergedAnnotations = MergedAnnotations.from(type, (MergedAnnotations.SearchStrategy)MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, (RepeatableContainers)RepeatableContainers.none());
        if (AuthorizationAnnotationUtils.hasDuplicate(mergedAnnotations, annotationType)) {
            throw new AnnotationConfigurationException("Found more than one annotation of type " + annotationType + " attributed to " + type + " Please remove the duplicate annotations and publish a bean to handle your authorization logic.");
        }
        return (A)AnnotationUtils.findAnnotation(type, annotationType);
    }

    private static <A extends Annotation> boolean hasDuplicate(MergedAnnotations mergedAnnotations, Class<A> annotationType) {
        MergedAnnotation alreadyFound = null;
        for (MergedAnnotation mergedAnnotation : mergedAnnotations) {
            if (AuthorizationAnnotationUtils.isSynthetic(mergedAnnotation.getSource()) || mergedAnnotation.getType() != annotationType) continue;
            if (alreadyFound == null) {
                alreadyFound = mergedAnnotation;
                continue;
            }
            if (!mergedAnnotation.getSource().equals(alreadyFound.getSource())) {
                return true;
            }
            if (mergedAnnotation.getRoot().getType() == alreadyFound.getRoot().getType()) continue;
            return true;
        }
        return false;
    }

    private static boolean isSynthetic(Object object) {
        if (object instanceof Executable) {
            return ((Executable)object).isSynthetic();
        }
        return false;
    }

    private AuthorizationAnnotationUtils() {
    }
}

