/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.core.annotation.MergedAnnotations
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class PreferredConstructor<T, P extends PersistentProperty<P>> {
    private final Constructor<T> constructor;
    private final List<Parameter<Object, P>> parameters;
    private final Map<PersistentProperty<?>, Boolean> isPropertyParameterCache = new ConcurrentHashMap();

    @SafeVarargs
    public PreferredConstructor(Constructor<T> constructor, Parameter<Object, P> ... parameters) {
        Assert.notNull(constructor, (String)"Constructor must not be null!");
        Assert.notNull(parameters, (String)"Parameters must not be null!");
        ReflectionUtils.makeAccessible(constructor);
        this.constructor = constructor;
        this.parameters = Arrays.asList(parameters);
    }

    public Constructor<T> getConstructor() {
        return this.constructor;
    }

    public List<Parameter<Object, P>> getParameters() {
        return this.parameters;
    }

    public boolean hasParameters() {
        return !this.parameters.isEmpty();
    }

    public boolean isNoArgConstructor() {
        return this.parameters.isEmpty();
    }

    public boolean isExplicitlyAnnotated() {
        return this.constructor.isAnnotationPresent(PersistenceConstructor.class);
    }

    public boolean isConstructorParameter(PersistentProperty<?> property) {
        Assert.notNull(property, (String)"Property must not be null!");
        Boolean cached = this.isPropertyParameterCache.get(property);
        if (cached != null) {
            return cached;
        }
        boolean result = false;
        for (Parameter<Object, P> parameter : this.parameters) {
            if (!parameter.maps(property)) continue;
            result = true;
            break;
        }
        this.isPropertyParameterCache.put(property, result);
        return result;
    }

    public boolean isEnclosingClassParameter(Parameter<?, P> parameter) {
        Assert.notNull(parameter, (String)"Parameter must not be null!");
        if (this.parameters.isEmpty() || !((Parameter)parameter).isEnclosingClassParameter()) {
            return false;
        }
        return this.parameters.get(0).equals(parameter);
    }

    public static class Parameter<T, P extends PersistentProperty<P>> {
        @Nullable
        private final String name;
        private final TypeInformation<T> type;
        private final MergedAnnotations annotations;
        private final String key;
        @Nullable
        private final PersistentEntity<T, P> entity;
        private final Lazy<Boolean> enclosingClassCache;
        private final Lazy<Boolean> hasSpelExpression;

        public Parameter(@Nullable String name, TypeInformation<T> type, Annotation[] annotations, @Nullable PersistentEntity<T, P> entity) {
            Assert.notNull(type, (String)"Type must not be null!");
            Assert.notNull((Object)annotations, (String)"Annotations must not be null!");
            this.name = name;
            this.type = type;
            this.annotations = MergedAnnotations.from((Annotation[])annotations);
            this.key = Parameter.getValue(this.annotations);
            this.entity = entity;
            this.enclosingClassCache = Lazy.of(() -> {
                if (entity == null) {
                    throw new IllegalStateException();
                }
                Class owningType = entity.getType();
                return owningType.isMemberClass() && type.getType().equals(owningType.getEnclosingClass());
            });
            this.hasSpelExpression = Lazy.of(() -> StringUtils.hasText((String)this.getSpelExpression()));
        }

        @Nullable
        private static String getValue(MergedAnnotations annotations) {
            return annotations.get(Value.class).getValue("value", String.class).filter(StringUtils::hasText).orElse(null);
        }

        @Nullable
        public String getName() {
            return this.name;
        }

        public TypeInformation<T> getType() {
            return this.type;
        }

        public MergedAnnotations getAnnotations() {
            return this.annotations;
        }

        public Class<T> getRawType() {
            return this.type.getType();
        }

        public String getSpelExpression() {
            return this.key;
        }

        public boolean hasSpelExpression() {
            return this.hasSpelExpression.get();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Parameter)) {
                return false;
            }
            Parameter parameter = (Parameter)o;
            if (!ObjectUtils.nullSafeEquals((Object)this.name, (Object)parameter.name)) {
                return false;
            }
            if (!ObjectUtils.nullSafeEquals(this.type, parameter.type)) {
                return false;
            }
            if (!ObjectUtils.nullSafeEquals((Object)this.key, (Object)parameter.key)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals(this.entity, parameter.entity);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode((Object)this.name);
            result = 31 * result + ObjectUtils.nullSafeHashCode(this.type);
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.key);
            result = 31 * result + ObjectUtils.nullSafeHashCode(this.entity);
            return result;
        }

        boolean maps(PersistentProperty<?> property) {
            PersistentEntity<T, P> entity = this.entity;
            String name = this.name;
            Object referencedProperty = entity == null ? null : (name == null ? null : entity.getPersistentProperty(name));
            return property.equals(referencedProperty);
        }

        private boolean isEnclosingClassParameter() {
            return this.enclosingClassCache.get();
        }
    }
}

