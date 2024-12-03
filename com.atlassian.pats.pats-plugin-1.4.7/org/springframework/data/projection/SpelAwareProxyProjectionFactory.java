/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.projection;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.projection.DefaultProjectionInformation;
import org.springframework.data.projection.ProjectionInformation;
import org.springframework.data.projection.ProxyProjectionFactory;
import org.springframework.data.projection.SpelEvaluatingMethodInterceptor;
import org.springframework.data.util.AnnotationDetectionMethodCallback;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class SpelAwareProxyProjectionFactory
extends ProxyProjectionFactory
implements BeanFactoryAware {
    private final Map<Class<?>, Boolean> typeCache = new ConcurrentHashMap();
    private final SpelExpressionParser parser = new SpelExpressionParser();
    @Nullable
    private BeanFactory beanFactory;

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    protected ProjectionInformation createProjectionInformation(Class<?> projectionType) {
        return new SpelAwareProjectionInformation(projectionType);
    }

    @Override
    protected MethodInterceptor postProcessAccessorInterceptor(MethodInterceptor interceptor, Object source, Class<?> projectionType) {
        return this.typeCache.computeIfAbsent(projectionType, SpelAwareProxyProjectionFactory::hasMethodWithValueAnnotation) != false ? new SpelEvaluatingMethodInterceptor(interceptor, source, this.beanFactory, this.parser, projectionType) : interceptor;
    }

    private static boolean hasMethodWithValueAnnotation(Class<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        AnnotationDetectionMethodCallback<Value> callback = new AnnotationDetectionMethodCallback<Value>(Value.class);
        ReflectionUtils.doWithMethods(type, callback);
        return callback.hasFoundAnnotation();
    }

    protected static class SpelAwareProjectionInformation
    extends DefaultProjectionInformation {
        protected SpelAwareProjectionInformation(Class<?> projectionType) {
            super(projectionType);
        }

        @Override
        protected boolean isInputProperty(PropertyDescriptor descriptor) {
            if (!super.isInputProperty(descriptor)) {
                return false;
            }
            Method readMethod = descriptor.getReadMethod();
            if (readMethod == null) {
                return false;
            }
            return AnnotationUtils.findAnnotation((Method)readMethod, Value.class) == null;
        }
    }
}

