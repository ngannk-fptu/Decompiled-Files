/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.reactivestreams.Publisher;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.ReactiveQueryMethodEvaluationContextProvider;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.data.repository.util.ReactiveWrappers;
import org.springframework.util.ClassUtils;

public abstract class ReactiveRepositoryFactorySupport
extends RepositoryFactorySupport {
    @Override
    protected void validate(RepositoryMetadata repositoryMetadata) {
        if (!ReactiveWrappers.isAvailable()) {
            throw new InvalidDataAccessApiUsageException(String.format("Cannot implement repository %s without reactive library support.", repositoryMetadata.getRepositoryInterface().getName()));
        }
        if (RxJavaOneConversionSetup.REACTIVE_STREAMS_PRESENT) {
            Arrays.stream(repositoryMetadata.getRepositoryInterface().getMethods()).forEach(x$0 -> RxJavaOneConversionSetup.validate(x$0));
        }
    }

    @Override
    public void setEvaluationContextProvider(QueryMethodEvaluationContextProvider evaluationContextProvider) {
        super.setEvaluationContextProvider(evaluationContextProvider == null ? ReactiveQueryMethodEvaluationContextProvider.DEFAULT : evaluationContextProvider);
    }

    private static class RxJavaOneConversionSetup {
        private static final boolean REACTIVE_STREAMS_PRESENT = ClassUtils.isPresent((String)"org.reactivestreams.Publisher", (ClassLoader)RxJavaOneConversionSetup.class.getClassLoader());

        private RxJavaOneConversionSetup() {
        }

        private static void validate(Method method) {
            if (ReactiveWrappers.supports(method.getReturnType()) && !ClassUtils.isAssignable(Publisher.class, method.getReturnType()) && !ReactiveWrapperConverters.supports(method.getReturnType())) {
                throw new InvalidDataAccessApiUsageException(String.format("No reactive type converter found for type %s used in %s, method %s.", method.getReturnType().getName(), method.getDeclaringClass().getName(), method));
            }
            Arrays.stream(method.getParameterTypes()).filter(ReactiveWrappers::supports).filter(parameterType -> !ClassUtils.isAssignable(Publisher.class, (Class)parameterType)).filter(parameterType -> !ReactiveWrapperConverters.supports(parameterType)).forEach(parameterType -> {
                throw new InvalidDataAccessApiUsageException(String.format("No reactive type converter found for type %s used in %s, method %s.", parameterType.getName(), method.getDeclaringClass().getName(), method));
            });
        }
    }
}

