/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.core.CollectionFactory
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.projection;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.NullableWrapper;
import org.springframework.data.util.NullableWrapperConverters;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

class ProjectingMethodInterceptor
implements MethodInterceptor {
    private final ProjectionFactory factory;
    private final MethodInterceptor delegate;
    private final ConversionService conversionService;

    ProjectingMethodInterceptor(ProjectionFactory factory, MethodInterceptor delegate, ConversionService conversionService) {
        this.factory = factory;
        this.delegate = delegate;
        this.conversionService = conversionService;
    }

    @Nullable
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        TypeInformation type;
        TypeInformation<Object> resultType = type = ClassTypeInformation.fromReturnTypeOf(invocation.getMethod());
        TypeInformation typeToReturn = type;
        Object result = this.delegate.invoke(invocation);
        boolean applyWrapper = false;
        if (NullableWrapperConverters.supports(type.getType()) && (result == null || !NullableWrapperConverters.supports(result.getClass()))) {
            resultType = NullableWrapperConverters.unwrapActualType(typeToReturn);
            applyWrapper = true;
        }
        result = this.potentiallyConvertResult(resultType, result);
        if (applyWrapper) {
            return this.conversionService.convert((Object)new NullableWrapper(result), typeToReturn.getType());
        }
        return result;
    }

    @Nullable
    protected Object potentiallyConvertResult(TypeInformation<?> type, @Nullable Object result) {
        if (result == null) {
            return null;
        }
        Class<?> targetType = type.getType();
        if (type.isCollectionLike() && !ClassUtils.isPrimitiveArray(targetType)) {
            return this.projectCollectionElements(ProjectingMethodInterceptor.asCollection(result), type);
        }
        if (type.isMap()) {
            return this.projectMapValues((Map)result, type);
        }
        if (ClassUtils.isAssignable(targetType, result.getClass())) {
            return result;
        }
        if (this.conversionService.canConvert(result.getClass(), targetType)) {
            return this.conversionService.convert(result, targetType);
        }
        if (targetType.isInterface()) {
            return this.getProjection(result, targetType);
        }
        throw new UnsupportedOperationException(String.format("Cannot project %s to %s. Target type is not an interface and no matching Converter found!", ClassUtils.getDescriptiveType((Object)result), ClassUtils.getQualifiedName(targetType)));
    }

    private Object projectCollectionElements(Collection<?> sources, TypeInformation<?> type) {
        Class<?> rawType = type.getType();
        TypeInformation<?> componentType = type.getComponentType();
        Collection result = CollectionFactory.createCollection(rawType.isArray() ? List.class : rawType, componentType != null ? componentType.getType() : null, (int)sources.size());
        for (Object source : sources) {
            result.add(this.getProjection(source, type.getRequiredComponentType().getType()));
        }
        if (rawType.isArray()) {
            return result.toArray((Object[])Array.newInstance(type.getRequiredComponentType().getType(), result.size()));
        }
        return result;
    }

    private Map<Object, Object> projectMapValues(Map<?, ?> sources, TypeInformation<?> type) {
        Map result = CollectionFactory.createMap(type.getType(), (int)sources.size());
        for (Map.Entry<?, ?> source : sources.entrySet()) {
            result.put(source.getKey(), this.getProjection(source.getValue(), type.getRequiredMapValueType().getType()));
        }
        return result;
    }

    @Nullable
    private Object getProjection(@Nullable Object result, Class<?> returnType) {
        return result == null || ClassUtils.isAssignable(returnType, result.getClass()) ? result : this.factory.createProjection(returnType, result);
    }

    private static Collection<?> asCollection(Object source) {
        Assert.notNull((Object)source, (String)"Source object must not be null!");
        if (source instanceof Collection) {
            return (Collection)source;
        }
        if (source.getClass().isArray()) {
            return Arrays.asList(ObjectUtils.toObjectArray((Object)source));
        }
        return Collections.singleton(source);
    }
}

