/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.reflect.KFunction
 *  kotlin.reflect.jvm.ReflectJvmMapping
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.mapping.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kotlin.reflect.KFunction;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.util.KotlinReflectionUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public class MappingInstantiationException
extends RuntimeException {
    private static final long serialVersionUID = 822211065035487628L;
    private static final String TEXT_TEMPLATE = "Failed to instantiate %s using constructor %s with arguments %s";
    private final Class<?> entityType;
    private final Constructor<?> constructor;
    private final List<Object> constructorArguments;

    public MappingInstantiationException(PersistentEntity<?, ?> entity, List<Object> arguments, Exception cause) {
        this(Optional.ofNullable(entity), arguments, null, cause);
    }

    public MappingInstantiationException(List<Object> arguments, Exception cause) {
        this(Optional.empty(), arguments, null, cause);
    }

    private MappingInstantiationException(Optional<PersistentEntity<?, ?>> entity, List<Object> arguments, @Nullable String message, Exception cause) {
        super(MappingInstantiationException.buildExceptionMessage(entity, arguments, message), cause);
        this.entityType = entity.map(PersistentEntity::getType).orElse(null);
        this.constructor = entity.map(PersistentEntity::getPersistenceConstructor).map(PreferredConstructor::getConstructor).orElse(null);
        this.constructorArguments = arguments;
    }

    private static String buildExceptionMessage(Optional<PersistentEntity<?, ?>> entity, List<Object> arguments, @Nullable String defaultMessage) {
        return entity.map(it -> {
            Optional constructor = Optional.ofNullable(it.getPersistenceConstructor());
            ArrayList<String> toStringArgs = new ArrayList<String>(arguments.size());
            for (Object o : arguments) {
                toStringArgs.add(ObjectUtils.nullSafeToString(o));
            }
            return String.format(TEXT_TEMPLATE, it.getType().getName(), constructor.map(MappingInstantiationException::toString).orElse("NO_CONSTRUCTOR"), String.join((CharSequence)",", toStringArgs));
        }).orElse(defaultMessage);
    }

    private static String toString(PreferredConstructor<?, ?> preferredConstructor) {
        KFunction kotlinFunction;
        Constructor<?> constructor = preferredConstructor.getConstructor();
        if (KotlinReflectionUtils.isSupportedKotlinClass(constructor.getDeclaringClass()) && (kotlinFunction = ReflectJvmMapping.getKotlinFunction(constructor)) != null) {
            return kotlinFunction.toString();
        }
        return constructor.toString();
    }

    public Optional<Class<?>> getEntityType() {
        return Optional.ofNullable(this.entityType);
    }

    public Optional<Constructor<?>> getConstructor() {
        return Optional.ofNullable(this.constructor);
    }

    public List<Object> getConstructorArguments() {
        return this.constructorArguments;
    }
}

