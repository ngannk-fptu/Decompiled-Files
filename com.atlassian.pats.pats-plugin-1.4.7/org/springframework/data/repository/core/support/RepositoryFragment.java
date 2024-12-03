/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

public interface RepositoryFragment<T> {
    public static <T> RepositoryFragment<T> implemented(T implementation) {
        return new ImplementedRepositoryFragment(Optional.empty(), implementation);
    }

    public static <T> RepositoryFragment<T> implemented(Class<T> interfaceClass, T implementation) {
        return new ImplementedRepositoryFragment<T>(Optional.of(interfaceClass), implementation);
    }

    public static <T> RepositoryFragment<T> structural(Class<T> interfaceOrImplementation) {
        return new StructuralRepositoryFragment<T>(interfaceOrImplementation);
    }

    default public boolean hasMethod(Method method) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        return ReflectionUtils.findMethod(this.getSignatureContributor(), (String)method.getName(), (Class[])method.getParameterTypes()) != null;
    }

    default public Optional<T> getImplementation() {
        return Optional.empty();
    }

    default public Stream<Method> methods() {
        return Arrays.stream(this.getSignatureContributor().getMethods());
    }

    public Class<?> getSignatureContributor();

    public RepositoryFragment<T> withImplementation(T var1);

    public static class ImplementedRepositoryFragment<T>
    implements RepositoryFragment<T> {
        private final Optional<Class<T>> interfaceClass;
        private final T implementation;
        private final Optional<T> optionalImplementation;

        public ImplementedRepositoryFragment(Optional<Class<T>> interfaceClass, T implementation) {
            Assert.notNull(interfaceClass, (String)"Interface class must not be null!");
            Assert.notNull(implementation, (String)"Implementation object must not be null!");
            interfaceClass.ifPresent(it -> Assert.isTrue((boolean)ClassUtils.isAssignableValue((Class)it, (Object)implementation), () -> String.format("Fragment implementation %s does not implement %s!", ClassUtils.getQualifiedName(implementation.getClass()), ClassUtils.getQualifiedName((Class)it))));
            this.interfaceClass = interfaceClass;
            this.implementation = implementation;
            this.optionalImplementation = Optional.of(implementation);
        }

        @Override
        public Class<?> getSignatureContributor() {
            return this.interfaceClass.orElse(this.implementation.getClass());
        }

        @Override
        public Optional<T> getImplementation() {
            return this.optionalImplementation;
        }

        @Override
        public RepositoryFragment<T> withImplementation(T implementation) {
            return new ImplementedRepositoryFragment<T>(this.interfaceClass, implementation);
        }

        public String toString() {
            return String.format("ImplementedRepositoryFragment %s%s", this.interfaceClass.map(ClassUtils::getShortName).map(it -> it + ":").orElse(""), ClassUtils.getShortName(this.implementation.getClass()));
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ImplementedRepositoryFragment)) {
                return false;
            }
            ImplementedRepositoryFragment that = (ImplementedRepositoryFragment)o;
            if (!ObjectUtils.nullSafeEquals(this.interfaceClass, that.interfaceClass)) {
                return false;
            }
            if (!ObjectUtils.nullSafeEquals(this.implementation, that.implementation)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals(this.optionalImplementation, that.optionalImplementation);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode(this.interfaceClass);
            result = 31 * result + ObjectUtils.nullSafeHashCode(this.implementation);
            result = 31 * result + ObjectUtils.nullSafeHashCode(this.optionalImplementation);
            return result;
        }
    }

    public static class StructuralRepositoryFragment<T>
    implements RepositoryFragment<T> {
        private final Class<T> interfaceOrImplementation;

        public StructuralRepositoryFragment(Class<T> interfaceOrImplementation) {
            this.interfaceOrImplementation = interfaceOrImplementation;
        }

        @Override
        public Class<?> getSignatureContributor() {
            return this.interfaceOrImplementation;
        }

        @Override
        public RepositoryFragment<T> withImplementation(T implementation) {
            return new ImplementedRepositoryFragment<T>(Optional.of(this.interfaceOrImplementation), implementation);
        }

        public String toString() {
            return String.format("StructuralRepositoryFragment %s", ClassUtils.getShortName(this.interfaceOrImplementation));
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof StructuralRepositoryFragment)) {
                return false;
            }
            StructuralRepositoryFragment that = (StructuralRepositoryFragment)o;
            return ObjectUtils.nullSafeEquals(this.interfaceOrImplementation, that.interfaceOrImplementation);
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.interfaceOrImplementation);
        }
    }
}

