/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.BindingAnnotation;
import com.google.inject.Key;
import com.google.inject.ScopeAnnotation;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.util.$Classes;
import com.google.inject.internal.util.$Function;
import com.google.inject.internal.util.$MapMaker;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.inject.Qualifier;
import javax.inject.Scope;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Annotations {
    private static final AnnotationChecker scopeChecker = new AnnotationChecker(Arrays.asList(ScopeAnnotation.class, Scope.class));
    private static final AnnotationChecker bindingAnnotationChecker = new AnnotationChecker(Arrays.asList(BindingAnnotation.class, Qualifier.class));

    public static boolean isMarker(Class<? extends Annotation> annotationType) {
        return annotationType.getDeclaredMethods().length == 0;
    }

    public static boolean isRetainedAtRuntime(Class<? extends Annotation> annotationType) {
        Retention retention = annotationType.getAnnotation(Retention.class);
        return retention != null && retention.value() == RetentionPolicy.RUNTIME;
    }

    public static Class<? extends Annotation> findScopeAnnotation(Errors errors, Class<?> implementation) {
        return Annotations.findScopeAnnotation(errors, implementation.getAnnotations());
    }

    public static Class<? extends Annotation> findScopeAnnotation(Errors errors, Annotation[] annotations) {
        Class<? extends Annotation> found = null;
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (!Annotations.isScopeAnnotation(annotationType)) continue;
            if (found != null) {
                errors.duplicateScopeAnnotations(found, annotationType);
                continue;
            }
            found = annotationType;
        }
        return found;
    }

    public static boolean isScopeAnnotation(Class<? extends Annotation> annotationType) {
        return scopeChecker.hasAnnotations(annotationType);
    }

    public static void checkForMisplacedScopeAnnotations(Class<?> type, Object source, Errors errors) {
        if ($Classes.isConcrete(type)) {
            return;
        }
        Class<? extends Annotation> scopeAnnotation = Annotations.findScopeAnnotation(errors, type);
        if (scopeAnnotation != null) {
            errors.withSource(type).scopeAnnotationOnAbstractType(scopeAnnotation, type, source);
        }
    }

    public static Key<?> getKey(TypeLiteral<?> type, Member member, Annotation[] annotations, Errors errors) throws ErrorsException {
        int numErrorsBefore = errors.size();
        Annotation found = Annotations.findBindingAnnotation(errors, member, annotations);
        errors.throwIfNewErrors(numErrorsBefore);
        return found == null ? Key.get(type) : Key.get(type, found);
    }

    public static Annotation findBindingAnnotation(Errors errors, Member member, Annotation[] annotations) {
        Annotation found = null;
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (!Annotations.isBindingAnnotation(annotationType)) continue;
            if (found != null) {
                errors.duplicateBindingAnnotations(member, found.annotationType(), annotationType);
                continue;
            }
            found = annotation;
        }
        return found;
    }

    public static boolean isBindingAnnotation(Class<? extends Annotation> annotationType) {
        return bindingAnnotationChecker.hasAnnotations(annotationType);
    }

    public static Annotation canonicalizeIfNamed(Annotation annotation) {
        if (annotation instanceof javax.inject.Named) {
            return Names.named(((javax.inject.Named)annotation).value());
        }
        return annotation;
    }

    public static Class<? extends Annotation> canonicalizeIfNamed(Class<? extends Annotation> annotationType) {
        if (annotationType == javax.inject.Named.class) {
            return Named.class;
        }
        return annotationType;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class AnnotationChecker {
        private final Collection<Class<? extends Annotation>> annotationTypes;
        private $Function<Class<? extends Annotation>, Boolean> hasAnnotations = new $Function<Class<? extends Annotation>, Boolean>(){

            @Override
            public Boolean apply(Class<? extends Annotation> annotationType) {
                for (Annotation annotation : annotationType.getAnnotations()) {
                    if (!AnnotationChecker.this.annotationTypes.contains(annotation.annotationType())) continue;
                    return true;
                }
                return false;
            }
        };
        final Map<Class<? extends Annotation>, Boolean> cache = new $MapMaker().weakKeys().makeComputingMap(this.hasAnnotations);

        AnnotationChecker(Collection<Class<? extends Annotation>> annotationTypes) {
            this.annotationTypes = annotationTypes;
        }

        boolean hasAnnotations(Class<? extends Annotation> annotated) {
            return this.cache.get(annotated);
        }
    }
}

