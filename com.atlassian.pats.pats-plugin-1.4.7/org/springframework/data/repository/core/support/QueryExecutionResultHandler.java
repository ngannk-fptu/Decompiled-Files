/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.CollectionFactory
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.support.GenericConversionService
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.repository.util.QueryExecutionConverters;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.data.util.NullableWrapper;
import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;

class QueryExecutionResultHandler {
    private static final TypeDescriptor WRAPPER_TYPE = TypeDescriptor.valueOf(NullableWrapper.class);
    private final GenericConversionService conversionService;
    private final Object mutex = new Object();
    private Map<Method, ReturnTypeDescriptor> descriptorCache = Collections.emptyMap();

    QueryExecutionResultHandler(GenericConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Nullable
    Object postProcessInvocationResult(@Nullable Object result, Method method) {
        if (!QueryExecutionResultHandler.processingRequired(result, method.getReturnType())) {
            return result;
        }
        ReturnTypeDescriptor descriptor = this.getOrCreateReturnTypeDescriptor(method);
        return this.postProcessInvocationResult(result, 0, descriptor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ReturnTypeDescriptor getOrCreateReturnTypeDescriptor(Method method) {
        Map<Method, ReturnTypeDescriptor> descriptorCache = this.descriptorCache;
        ReturnTypeDescriptor descriptor = descriptorCache.get(method);
        if (descriptor == null) {
            Map<Method, ReturnTypeDescriptor> updatedDescriptorCache;
            descriptor = ReturnTypeDescriptor.of(method);
            if (descriptorCache.isEmpty()) {
                updatedDescriptorCache = Collections.singletonMap(method, descriptor);
            } else {
                updatedDescriptorCache = new HashMap<Method, ReturnTypeDescriptor>(descriptorCache.size() + 1, 1.0f);
                updatedDescriptorCache.putAll(descriptorCache);
                updatedDescriptorCache.put(method, descriptor);
            }
            Object object = this.mutex;
            synchronized (object) {
                this.descriptorCache = updatedDescriptorCache;
            }
        }
        return descriptor;
    }

    @Nullable
    Object postProcessInvocationResult(@Nullable Object result, int nestingLevel, ReturnTypeDescriptor descriptor) {
        TypeDescriptor returnTypeDescriptor = descriptor.getReturnTypeDescriptor(nestingLevel);
        if (returnTypeDescriptor == null) {
            return result;
        }
        Class expectedReturnType = returnTypeDescriptor.getType();
        result = QueryExecutionResultHandler.unwrapOptional(result);
        if (QueryExecutionConverters.supports(expectedReturnType) || ReactiveWrapperConverters.supports(expectedReturnType)) {
            TypeDescriptor source;
            result = this.postProcessInvocationResult(result, nestingLevel + 1, descriptor);
            if (this.conversionRequired(WRAPPER_TYPE, returnTypeDescriptor)) {
                return this.conversionService.convert((Object)new NullableWrapper(result), returnTypeDescriptor);
            }
            if (result != null && this.conversionRequired(source = TypeDescriptor.valueOf(result.getClass()), returnTypeDescriptor)) {
                return this.conversionService.convert(result, returnTypeDescriptor);
            }
        }
        if (result != null) {
            TypeDescriptor elementDescriptor;
            boolean requiresConversion;
            if (ReactiveWrapperConverters.supports(expectedReturnType)) {
                return ReactiveWrapperConverters.toWrapper(result, expectedReturnType);
            }
            if (result instanceof Collection && !(requiresConversion = this.requiresConversion((Collection)result, expectedReturnType, elementDescriptor = descriptor.getReturnTypeDescriptor(nestingLevel + 1)))) {
                return result;
            }
            TypeDescriptor resultDescriptor = TypeDescriptor.forObject((Object)result);
            return this.conversionService.canConvert(resultDescriptor, returnTypeDescriptor) ? this.conversionService.convert(result, returnTypeDescriptor) : result;
        }
        return Map.class.equals((Object)expectedReturnType) ? CollectionFactory.createMap((Class)expectedReturnType, (int)0) : null;
    }

    private boolean requiresConversion(Collection<?> collection, Class<?> expectedReturnType, @Nullable TypeDescriptor elementDescriptor) {
        if (Streamable.class.isAssignableFrom(expectedReturnType) || !expectedReturnType.isInstance(collection)) {
            return true;
        }
        if (elementDescriptor == null || !Iterable.class.isAssignableFrom(expectedReturnType)) {
            return false;
        }
        Class type = elementDescriptor.getType();
        for (Object o : collection) {
            if (type.isInstance(o)) continue;
            return true;
        }
        return false;
    }

    private boolean conversionRequired(TypeDescriptor source, TypeDescriptor target) {
        return this.conversionService.canConvert(source, target) && !this.conversionService.canBypassConvert(source, target);
    }

    @Nullable
    private static Object unwrapOptional(@Nullable Object source) {
        if (source == null) {
            return null;
        }
        return Optional.class.isInstance(source) ? ((Optional)Optional.class.cast(source)).orElse(null) : source;
    }

    private static boolean processingRequired(@Nullable Object source, Class<?> targetType) {
        return !targetType.isInstance(source) || source == null || Collection.class.isInstance(source);
    }

    static class ReturnTypeDescriptor {
        private final MethodParameter methodParameter;
        private final TypeDescriptor typeDescriptor;
        @Nullable
        private final TypeDescriptor nestedTypeDescriptor;

        private ReturnTypeDescriptor(Method method) {
            this.methodParameter = new MethodParameter(method, -1);
            this.typeDescriptor = TypeDescriptor.nested((MethodParameter)this.methodParameter, (int)0);
            this.nestedTypeDescriptor = TypeDescriptor.nested((MethodParameter)this.methodParameter, (int)1);
        }

        public static ReturnTypeDescriptor of(Method method) {
            return new ReturnTypeDescriptor(method);
        }

        @Nullable
        TypeDescriptor getReturnTypeDescriptor(int nestingLevel) {
            switch (nestingLevel) {
                case 0: {
                    return this.typeDescriptor;
                }
                case 1: {
                    return this.nestedTypeDescriptor;
                }
            }
            return TypeDescriptor.nested((MethodParameter)this.methodParameter, (int)nestingLevel);
        }
    }
}

