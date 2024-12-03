/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.method;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public final class HandlerTypePredicate
implements Predicate<Class<?>> {
    private final Set<String> basePackages;
    private final List<Class<?>> assignableTypes;
    private final List<Class<? extends Annotation>> annotations;

    private HandlerTypePredicate(Set<String> basePackages, List<Class<?>> assignableTypes, List<Class<? extends Annotation>> annotations) {
        this.basePackages = Collections.unmodifiableSet(basePackages);
        this.assignableTypes = Collections.unmodifiableList(assignableTypes);
        this.annotations = Collections.unmodifiableList(annotations);
    }

    @Override
    public boolean test(@Nullable Class<?> controllerType) {
        if (!this.hasSelectors()) {
            return true;
        }
        if (controllerType != null) {
            for (String string : this.basePackages) {
                if (!controllerType.getName().startsWith(string)) continue;
                return true;
            }
            for (Class clazz : this.assignableTypes) {
                if (!ClassUtils.isAssignable((Class)clazz, controllerType)) continue;
                return true;
            }
            for (Class clazz : this.annotations) {
                if (AnnotationUtils.findAnnotation(controllerType, (Class)clazz) == null) continue;
                return true;
            }
        }
        return false;
    }

    private boolean hasSelectors() {
        return !this.basePackages.isEmpty() || !this.assignableTypes.isEmpty() || !this.annotations.isEmpty();
    }

    public static HandlerTypePredicate forAnyHandlerType() {
        return new HandlerTypePredicate(Collections.emptySet(), Collections.emptyList(), Collections.emptyList());
    }

    public static HandlerTypePredicate forBasePackage(String ... packages) {
        return new Builder().basePackage(packages).build();
    }

    public static HandlerTypePredicate forBasePackageClass(Class<?> ... packageClasses) {
        return new Builder().basePackageClass(packageClasses).build();
    }

    public static HandlerTypePredicate forAssignableType(Class<?> ... types) {
        return new Builder().assignableType(types).build();
    }

    @SafeVarargs
    public static HandlerTypePredicate forAnnotation(Class<? extends Annotation> ... annotations) {
        return new Builder().annotation(annotations).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Set<String> basePackages = new LinkedHashSet<String>();
        private final List<Class<?>> assignableTypes = new ArrayList();
        private final List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();

        public Builder basePackage(String ... packages) {
            Arrays.stream(packages).filter(StringUtils::hasText).forEach(this::addBasePackage);
            return this;
        }

        public Builder basePackageClass(Class<?> ... packageClasses) {
            Arrays.stream(packageClasses).forEach(clazz -> this.addBasePackage(ClassUtils.getPackageName((Class)clazz)));
            return this;
        }

        private void addBasePackage(String basePackage) {
            this.basePackages.add(basePackage.endsWith(".") ? basePackage : basePackage + ".");
        }

        public Builder assignableType(Class<?> ... types) {
            this.assignableTypes.addAll(Arrays.asList(types));
            return this;
        }

        public final Builder annotation(Class<? extends Annotation> ... annotations) {
            this.annotations.addAll(Arrays.asList(annotations));
            return this;
        }

        public HandlerTypePredicate build() {
            return new HandlerTypePredicate(this.basePackages, this.assignableTypes, this.annotations);
        }
    }
}

