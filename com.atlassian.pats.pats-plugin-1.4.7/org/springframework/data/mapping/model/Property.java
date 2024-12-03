/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.mapping.model;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.Optionals;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class Property {
    private final Optional<Field> field;
    private final Optional<PropertyDescriptor> descriptor;
    private final Class<?> rawType;
    private final Lazy<Integer> hashCode;
    private final Optional<Method> getter;
    private final Optional<Method> setter;
    private final Lazy<String> name;
    private final Lazy<String> toString;
    private final Lazy<Optional<Method>> wither;

    private Property(TypeInformation<?> type, Optional<Field> field, Optional<PropertyDescriptor> descriptor) {
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.isTrue((boolean)Optionals.isAnyPresent(field, descriptor), (String)"Either field or descriptor has to be given!");
        this.field = field;
        this.descriptor = descriptor;
        this.rawType = this.withFieldOrDescriptor(it -> type.getRequiredProperty(it.getName()).getType(), it -> type.getRequiredProperty(it.getName()).getType());
        this.hashCode = Lazy.of(() -> this.withFieldOrDescriptor(Object::hashCode));
        this.name = Lazy.of(() -> this.withFieldOrDescriptor(Field::getName, FeatureDescriptor::getName));
        this.toString = Lazy.of(() -> this.withFieldOrDescriptor(Object::toString, it -> String.format("%s.%s", type.getType().getName(), it.getDisplayName())));
        this.getter = descriptor.map(PropertyDescriptor::getReadMethod).filter(it -> this.getType() != null).filter(it -> this.getType().isAssignableFrom(type.getReturnType((Method)it).getType()));
        this.setter = descriptor.map(PropertyDescriptor::getWriteMethod).filter(it -> this.getType() != null).filter(it -> type.getParameterTypes((Method)it).get(0).getType().isAssignableFrom(this.getType()));
        this.wither = Lazy.of(() -> Property.findWither(type, this.getName(), this.getType()));
    }

    public static Property of(TypeInformation<?> type, Field field) {
        Assert.notNull((Object)field, (String)"Field must not be null!");
        return new Property(type, Optional.of(field), Optional.empty());
    }

    public static Property of(TypeInformation<?> type, Field field, PropertyDescriptor descriptor) {
        Assert.notNull((Object)field, (String)"Field must not be null!");
        Assert.notNull((Object)descriptor, (String)"PropertyDescriptor must not be null!");
        return new Property(type, Optional.of(field), Optional.of(descriptor));
    }

    public static Property of(TypeInformation<?> type, PropertyDescriptor descriptor) {
        Assert.notNull((Object)descriptor, (String)"PropertyDescriptor must not be null!");
        return new Property(type, Optional.empty(), Optional.of(descriptor));
    }

    public static boolean supportsStandalone(PropertyDescriptor descriptor) {
        Assert.notNull((Object)descriptor, (String)"PropertyDescriptor must not be null!");
        return descriptor.getPropertyType() != null;
    }

    public boolean isFieldBacked() {
        return this.field.isPresent();
    }

    public Optional<Method> getGetter() {
        return this.getter;
    }

    public Optional<Method> getSetter() {
        return this.setter;
    }

    public Optional<Method> getWither() {
        return this.wither.get();
    }

    public Optional<Field> getField() {
        return this.field;
    }

    public boolean hasAccessor() {
        return this.getGetter().isPresent() || this.getSetter().isPresent();
    }

    public String getName() {
        return this.name.get();
    }

    public Class<?> getType() {
        return this.rawType;
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Property)) {
            return false;
        }
        Property that = (Property)obj;
        return this.field.isPresent() ? this.field.equals(that.field) : this.descriptor.equals(that.descriptor);
    }

    public int hashCode() {
        return this.hashCode.get();
    }

    public String toString() {
        return this.toString.get();
    }

    private <T> T withFieldOrDescriptor(Function<Object, T> function) {
        return this.withFieldOrDescriptor(function, function);
    }

    private <T> T withFieldOrDescriptor(Function<? super Field, T> field, Function<? super PropertyDescriptor, T> descriptor) {
        return Optionals.firstNonEmpty(() -> this.field.map(field), () -> this.descriptor.map(descriptor)).orElseThrow(() -> new IllegalStateException("Should not occur! Either field or descriptor has to be given"));
    }

    private static Optional<Method> findWither(TypeInformation<?> owner, String propertyName, Class<?> rawType) {
        AtomicReference resultHolder = new AtomicReference();
        String methodName = String.format("with%s", StringUtils.capitalize((String)propertyName));
        ReflectionUtils.doWithMethods(owner.getType(), it -> {
            if (owner.isAssignableFrom(owner.getReturnType(it))) {
                resultHolder.set(it);
            }
        }, it -> Property.isMethodWithSingleParameterOfType(it, methodName, rawType));
        Method method = (Method)resultHolder.get();
        return method != null ? Optional.of(method) : Optional.empty();
    }

    private static boolean isMethodWithSingleParameterOfType(Method method, String name, Class<?> type) {
        return method.getParameterCount() == 1 && method.getName().equals(name) && method.getParameterTypes()[0].equals(type);
    }
}

