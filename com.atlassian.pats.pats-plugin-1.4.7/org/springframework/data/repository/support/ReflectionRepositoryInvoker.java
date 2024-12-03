/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.convert.ConversionException
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.support;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.core.CrudMethods;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.support.AnnotationAttribute;
import org.springframework.data.repository.support.MethodParameters;
import org.springframework.data.repository.support.QueryMethodParameterConversionException;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.repository.util.QueryExecutionConverters;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

class ReflectionRepositoryInvoker
implements RepositoryInvoker {
    private static final AnnotationAttribute PARAM_ANNOTATION = new AnnotationAttribute(Param.class);
    private static final String NAME_NOT_FOUND = "Unable to detect parameter names for query method %s! Use @Param or compile with -parameters on JDK 8.";
    private final Object repository;
    private final CrudMethods methods;
    private final Class<?> idType;
    private final ConversionService conversionService;

    public ReflectionRepositoryInvoker(Object repository, RepositoryMetadata metadata, ConversionService conversionService) {
        Assert.notNull((Object)repository, (String)"Repository must not be null!");
        Assert.notNull((Object)metadata, (String)"RepositoryMetadata must not be null!");
        Assert.notNull((Object)conversionService, (String)"ConversionService must not be null!");
        this.repository = repository;
        this.methods = metadata.getCrudMethods();
        this.idType = metadata.getIdType();
        this.conversionService = conversionService;
    }

    @Override
    public boolean hasFindAllMethod() {
        return this.methods.hasFindAllMethod();
    }

    @Override
    public Iterable<Object> invokeFindAll(Sort sort) {
        return this.invokeFindAllReflectively(sort);
    }

    @Override
    public Iterable<Object> invokeFindAll(Pageable pageable) {
        return this.invokeFindAllReflectively(pageable);
    }

    @Override
    public boolean hasSaveMethod() {
        return this.methods.hasSaveMethod();
    }

    @Override
    public <T> T invokeSave(T object) {
        Method method = this.methods.getSaveMethod().orElseThrow(() -> new IllegalStateException("Repository doesn't have a save-method declared!"));
        return this.invokeForNonNullResult(method, object);
    }

    @Override
    public boolean hasFindOneMethod() {
        return this.methods.hasFindOneMethod();
    }

    @Override
    public <T> Optional<T> invokeFindById(Object id) {
        Method method = this.methods.getFindOneMethod().orElseThrow(() -> new IllegalStateException("Repository doesn't have a find-one-method declared!"));
        return this.returnAsOptional(this.invoke(method, this.convertId(id)));
    }

    @Override
    public boolean hasDeleteMethod() {
        return this.methods.hasDelete();
    }

    @Override
    public void invokeDeleteById(Object id) {
        Assert.notNull((Object)id, (String)"Identifier must not be null!");
        Method method = this.methods.getDeleteMethod().orElseThrow(() -> new IllegalStateException("Repository doesn't have a delete-method declared!"));
        if (method.getName().endsWith("ById")) {
            this.invoke(method, this.convertId(id));
        } else {
            this.invoke(method, this.invokeFindById(id).orElse(null));
        }
    }

    public Optional<Object> invokeQueryMethod(Method method, MultiValueMap<String, ?> parameters, Pageable pageable, Sort sort) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        Assert.notNull(parameters, (String)"Parameters must not be null!");
        Assert.notNull((Object)pageable, (String)"Pageable must not be null!");
        Assert.notNull((Object)sort, (String)"Sort must not be null!");
        ReflectionUtils.makeAccessible((Method)method);
        return this.returnAsOptional(this.invoke(method, this.prepareParameters(method, parameters, pageable, sort)));
    }

    private Object[] prepareParameters(Method method, MultiValueMap<String, ?> rawParameters, Pageable pageable, Sort sort) {
        List<MethodParameter> parameters = new MethodParameters(method, Optional.of(PARAM_ANNOTATION)).getParameters();
        if (parameters.isEmpty()) {
            return new Object[0];
        }
        Object[] result = new Object[parameters.size()];
        Sort sortToUse = pageable.getSortOr(sort);
        for (int i = 0; i < result.length; ++i) {
            MethodParameter param = parameters.get(i);
            Class targetType = param.getParameterType();
            if (Pageable.class.isAssignableFrom(targetType)) {
                result[i] = pageable;
                continue;
            }
            if (Sort.class.isAssignableFrom(targetType)) {
                result[i] = sortToUse;
                continue;
            }
            String parameterName = param.getParameterName();
            if (!StringUtils.hasText((String)parameterName)) {
                throw new IllegalArgumentException(String.format(NAME_NOT_FOUND, ClassUtils.getQualifiedMethodName((Method)method)));
            }
            Object value = ReflectionRepositoryInvoker.unwrapSingleElement((List)rawParameters.get((Object)parameterName));
            result[i] = targetType.isInstance(value) ? value : this.convert(value, param);
        }
        return result;
    }

    @Nullable
    private Object convert(@Nullable Object value, MethodParameter parameter) {
        if (value == null) {
            return value;
        }
        try {
            return this.conversionService.convert(value, TypeDescriptor.forObject((Object)value), new TypeDescriptor(parameter));
        }
        catch (ConversionException o_O) {
            throw new QueryMethodParameterConversionException(value, parameter, o_O);
        }
    }

    @Nullable
    private <T> T invoke(Method method, Object ... arguments) {
        return (T)ReflectionUtils.invokeMethod((Method)method, (Object)this.repository, (Object[])arguments);
    }

    private <T> T invokeForNonNullResult(Method method, Object ... arguments) {
        T result = this.invoke(method, arguments);
        if (result == null) {
            throw new IllegalStateException(String.format("Invocation of method %s(%s) on %s unexpectedly returned null!", method, Arrays.toString(arguments), this.repository));
        }
        return result;
    }

    private <T> Optional<T> returnAsOptional(@Nullable Object source) {
        return source instanceof Optional ? source : Optional.ofNullable(QueryExecutionConverters.unwrap(source));
    }

    protected Object convertId(Object id) {
        Assert.notNull((Object)id, (String)"Id must not be null!");
        if (this.idType.isInstance(id)) {
            return id;
        }
        Object result = this.conversionService.convert(id, this.idType);
        if (result == null) {
            throw new IllegalStateException(String.format("Identifier conversion of %s to %s unexpectedly returned null!", id, this.idType));
        }
        return result;
    }

    protected Iterable<Object> invokeFindAllReflectively(Pageable pageable) {
        Method method = this.methods.getFindAllMethod().orElseThrow(() -> new IllegalStateException("Repository doesn't have a find-all-method declared!"));
        if (method.getParameterCount() == 0) {
            return (Iterable)this.invokeForNonNullResult(method, new Object[0]);
        }
        Class<?>[] types = method.getParameterTypes();
        if (Pageable.class.isAssignableFrom(types[0])) {
            return (Iterable)this.invokeForNonNullResult(method, pageable);
        }
        return this.invokeFindAll(pageable.getSort());
    }

    protected Iterable<Object> invokeFindAllReflectively(Sort sort) {
        Method method = this.methods.getFindAllMethod().orElseThrow(() -> new IllegalStateException("Repository doesn't have a find-all-method declared!"));
        if (method.getParameterCount() == 0) {
            return (Iterable)this.invokeForNonNullResult(method, new Object[0]);
        }
        return (Iterable)this.invokeForNonNullResult(method, sort);
    }

    @Nullable
    private static Object unwrapSingleElement(@Nullable List<? extends Object> source) {
        return source == null ? null : (source.size() == 1 ? source.get(0) : source);
    }
}

