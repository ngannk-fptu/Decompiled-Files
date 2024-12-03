/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ResolvableType
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.query;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.util.ClassUtils;
import org.springframework.data.repository.util.QueryExecutionConverters;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;

public class Parameter {
    static final List<Class<?>> TYPES;
    private static final String NAMED_PARAMETER_TEMPLATE = ":%s";
    private static final String POSITION_PARAMETER_TEMPLATE = "?%s";
    private final MethodParameter parameter;
    private final Class<?> parameterType;
    private final boolean isDynamicProjectionParameter;
    private final Lazy<Optional<String>> name;

    protected Parameter(MethodParameter parameter) {
        Assert.notNull((Object)parameter, (String)"MethodParameter must not be null!");
        this.parameter = parameter;
        this.parameterType = Parameter.potentiallyUnwrapParameterType(parameter);
        this.isDynamicProjectionParameter = Parameter.isDynamicProjectionParameter(parameter);
        this.name = TYPES.contains(parameter.getParameterType()) ? Lazy.of(Optional.empty()) : Lazy.of(() -> {
            Param annotation = (Param)parameter.getParameterAnnotation(Param.class);
            return Optional.ofNullable(annotation == null ? parameter.getParameterName() : annotation.value());
        });
    }

    public boolean isSpecialParameter() {
        return this.isDynamicProjectionParameter || TYPES.contains(this.parameter.getParameterType());
    }

    public boolean isBindable() {
        return !this.isSpecialParameter();
    }

    public boolean isDynamicProjectionParameter() {
        return this.isDynamicProjectionParameter;
    }

    public String getPlaceholder() {
        if (this.isNamedParameter()) {
            return String.format(NAMED_PARAMETER_TEMPLATE, this.getName().get());
        }
        return String.format(POSITION_PARAMETER_TEMPLATE, this.getIndex());
    }

    public int getIndex() {
        return this.parameter.getParameterIndex();
    }

    public boolean isNamedParameter() {
        return !this.isSpecialParameter() && this.getName().isPresent();
    }

    public Optional<String> getName() {
        return this.name.get();
    }

    public Class<?> getType() {
        return this.parameterType;
    }

    public boolean isExplicitlyNamed() {
        return this.parameter.hasParameterAnnotation(Param.class);
    }

    public String toString() {
        return String.format("%s:%s", this.isNamedParameter() ? this.getName() : "#" + this.getIndex(), this.getType().getName());
    }

    boolean isPageable() {
        return Pageable.class.isAssignableFrom(this.getType());
    }

    boolean isSort() {
        return Sort.class.isAssignableFrom(this.getType());
    }

    private static boolean isDynamicProjectionParameter(MethodParameter parameter) {
        Method method = parameter.getMethod();
        if (method == null) {
            throw new IllegalStateException(String.format("Method parameter %s is not backed by a method!", parameter));
        }
        ClassTypeInformation ownerType = ClassTypeInformation.from(parameter.getDeclaringClass());
        TypeInformation parameterTypes = (TypeInformation)ownerType.getParameterTypes(method).get(parameter.getParameterIndex());
        if (!parameterTypes.getType().equals(Class.class)) {
            return false;
        }
        TypeInformation<?> bound = parameterTypes.getTypeArguments().get(0);
        TypeInformation returnType = ClassTypeInformation.fromReturnTypeOf(method);
        return bound.equals(QueryExecutionConverters.unwrapWrapperTypes(ReactiveWrapperConverters.unwrapWrapperTypes(returnType)));
    }

    private static boolean isWrapped(MethodParameter parameter) {
        return QueryExecutionConverters.supports(parameter.getParameterType()) || ReactiveWrapperConverters.supports(parameter.getParameterType());
    }

    private static boolean shouldUnwrap(MethodParameter parameter) {
        return QueryExecutionConverters.supportsUnwrapping(parameter.getParameterType());
    }

    private static Class<?> potentiallyUnwrapParameterType(MethodParameter parameter) {
        Class originalType = parameter.getParameterType();
        if (Parameter.isWrapped(parameter) && Parameter.shouldUnwrap(parameter)) {
            return ResolvableType.forMethodParameter((MethodParameter)parameter).getGeneric(new int[]{0}).resolve(Object.class);
        }
        return originalType;
    }

    static {
        ArrayList<Class> types = new ArrayList<Class>(Arrays.asList(Pageable.class, Sort.class));
        ClassUtils.ifPresent("kotlin.coroutines.Continuation", Parameter.class.getClassLoader(), types::add);
        TYPES = Collections.unmodifiableList(types);
    }
}

