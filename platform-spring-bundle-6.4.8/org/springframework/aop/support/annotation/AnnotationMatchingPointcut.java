/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support.annotation;

import java.lang.annotation.Annotation;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class AnnotationMatchingPointcut
implements Pointcut {
    private final ClassFilter classFilter;
    private final MethodMatcher methodMatcher;

    public AnnotationMatchingPointcut(Class<? extends Annotation> classAnnotationType) {
        this(classAnnotationType, false);
    }

    public AnnotationMatchingPointcut(Class<? extends Annotation> classAnnotationType, boolean checkInherited) {
        this.classFilter = new AnnotationClassFilter(classAnnotationType, checkInherited);
        this.methodMatcher = MethodMatcher.TRUE;
    }

    public AnnotationMatchingPointcut(@Nullable Class<? extends Annotation> classAnnotationType, @Nullable Class<? extends Annotation> methodAnnotationType) {
        this(classAnnotationType, methodAnnotationType, false);
    }

    public AnnotationMatchingPointcut(@Nullable Class<? extends Annotation> classAnnotationType, @Nullable Class<? extends Annotation> methodAnnotationType, boolean checkInherited) {
        Assert.isTrue(classAnnotationType != null || methodAnnotationType != null, "Either Class annotation type or Method annotation type needs to be specified (or both)");
        this.classFilter = classAnnotationType != null ? new AnnotationClassFilter(classAnnotationType, checkInherited) : new AnnotationCandidateClassFilter(methodAnnotationType);
        this.methodMatcher = methodAnnotationType != null ? new AnnotationMethodMatcher(methodAnnotationType, checkInherited) : MethodMatcher.TRUE;
    }

    @Override
    public ClassFilter getClassFilter() {
        return this.classFilter;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this.methodMatcher;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotationMatchingPointcut)) {
            return false;
        }
        AnnotationMatchingPointcut otherPointcut = (AnnotationMatchingPointcut)other;
        return this.classFilter.equals(otherPointcut.classFilter) && this.methodMatcher.equals(otherPointcut.methodMatcher);
    }

    public int hashCode() {
        return this.classFilter.hashCode() * 37 + this.methodMatcher.hashCode();
    }

    public String toString() {
        return "AnnotationMatchingPointcut: " + this.classFilter + ", " + this.methodMatcher;
    }

    public static AnnotationMatchingPointcut forClassAnnotation(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        return new AnnotationMatchingPointcut(annotationType);
    }

    public static AnnotationMatchingPointcut forMethodAnnotation(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        return new AnnotationMatchingPointcut(null, annotationType);
    }

    private static class AnnotationCandidateClassFilter
    implements ClassFilter {
        private final Class<? extends Annotation> annotationType;

        AnnotationCandidateClassFilter(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        @Override
        public boolean matches(Class<?> clazz) {
            return AnnotationUtils.isCandidateClass(clazz, this.annotationType);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof AnnotationCandidateClassFilter)) {
                return false;
            }
            AnnotationCandidateClassFilter that = (AnnotationCandidateClassFilter)obj;
            return this.annotationType.equals(that.annotationType);
        }

        public int hashCode() {
            return this.annotationType.hashCode();
        }

        public String toString() {
            return this.getClass().getName() + ": " + this.annotationType;
        }
    }
}

