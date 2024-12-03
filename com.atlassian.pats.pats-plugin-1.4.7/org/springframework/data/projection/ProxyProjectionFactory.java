/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.converter.ConverterRegistry
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.core.convert.support.GenericConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 */
package org.springframework.data.projection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor;
import org.springframework.data.projection.DefaultProjectionInformation;
import org.springframework.data.projection.MapAccessingMethodInterceptor;
import org.springframework.data.projection.MethodInterceptorFactory;
import org.springframework.data.projection.ProjectingMethodInterceptor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.ProjectionInformation;
import org.springframework.data.projection.PropertyAccessingMethodInterceptor;
import org.springframework.data.projection.TargetAware;
import org.springframework.data.util.NullableWrapperConverters;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

class ProxyProjectionFactory
implements ProjectionFactory,
BeanClassLoaderAware {
    static final GenericConversionService CONVERSION_SERVICE = new DefaultConversionService();
    private final List<MethodInterceptorFactory> factories;
    private final Map<Class<?>, ProjectionInformation> projectionInformationCache = new ConcurrentReferenceHashMap();
    @Nullable
    private ClassLoader classLoader;

    protected ProxyProjectionFactory() {
        this.factories = new ArrayList<MethodInterceptorFactory>();
        this.factories.add(MapAccessingMethodInterceptorFactory.INSTANCE);
        this.factories.add(PropertyAccessingMethodInvokerFactory.INSTANCE);
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void registerMethodInvokerFactory(MethodInterceptorFactory factory) {
        Assert.notNull((Object)factory, (String)"MethodInterceptorFactory must not be null!");
        this.factories.add(0, factory);
    }

    @Override
    public <T> T createProjection(Class<T> projectionType, Object source) {
        Assert.notNull(projectionType, (String)"Projection type must not be null!");
        Assert.notNull((Object)source, (String)"Source must not be null!");
        Assert.isTrue((boolean)projectionType.isInterface(), (String)"Projection type must be an interface!");
        if (projectionType.isInstance(source)) {
            return (T)source;
        }
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(source);
        factory.setOpaque(true);
        factory.setInterfaces(new Class[]{projectionType, TargetAware.class});
        factory.addAdvice((Advice)new DefaultMethodInvokingMethodInterceptor());
        factory.addAdvice((Advice)new TargetAwareMethodInterceptor(source.getClass()));
        factory.addAdvice((Advice)this.getMethodInterceptor(source, projectionType));
        return (T)factory.getProxy(this.classLoader == null ? ClassUtils.getDefaultClassLoader() : this.classLoader);
    }

    @Override
    public <T> T createProjection(Class<T> projectionType) {
        Assert.notNull(projectionType, (String)"Projection type must not be null!");
        return this.createProjection(projectionType, new HashMap());
    }

    @Override
    public final ProjectionInformation getProjectionInformation(Class<?> projectionType) {
        return this.projectionInformationCache.computeIfAbsent(projectionType, this::createProjectionInformation);
    }

    protected MethodInterceptor postProcessAccessorInterceptor(MethodInterceptor interceptor, Object source, Class<?> projectionType) {
        return interceptor;
    }

    protected ProjectionInformation createProjectionInformation(Class<?> projectionType) {
        return new DefaultProjectionInformation(projectionType);
    }

    private MethodInterceptor getMethodInterceptor(Object source, Class<?> projectionType) {
        MethodInterceptor propertyInvocationInterceptor = this.getFactoryFor(source, projectionType).createMethodInterceptor(source, projectionType);
        return new ProjectingMethodInterceptor(this, this.postProcessAccessorInterceptor(propertyInvocationInterceptor, source, projectionType), (ConversionService)CONVERSION_SERVICE);
    }

    private MethodInterceptorFactory getFactoryFor(Object source, Class<?> projectionType) {
        for (MethodInterceptorFactory factory : this.factories) {
            if (!factory.supports(source, projectionType)) continue;
            return factory;
        }
        throw new IllegalStateException("No MethodInterceptorFactory found for type ".concat(source.getClass().getName()));
    }

    static {
        Jsr310Converters.getConvertersToRegister().forEach(arg_0 -> ((GenericConversionService)CONVERSION_SERVICE).addConverter(arg_0));
        NullableWrapperConverters.registerConvertersIn((ConverterRegistry)CONVERSION_SERVICE);
        CONVERSION_SERVICE.removeConvertible(Object.class, Object.class);
    }

    private static enum PropertyAccessingMethodInvokerFactory implements MethodInterceptorFactory
    {
        INSTANCE;


        @Override
        public MethodInterceptor createMethodInterceptor(Object source, Class<?> targetType) {
            return new PropertyAccessingMethodInterceptor(source);
        }

        @Override
        public boolean supports(Object source, Class<?> targetType) {
            return true;
        }
    }

    private static enum MapAccessingMethodInterceptorFactory implements MethodInterceptorFactory
    {
        INSTANCE;


        @Override
        public MethodInterceptor createMethodInterceptor(Object source, Class<?> targetType) {
            return new MapAccessingMethodInterceptor((Map)source);
        }

        @Override
        public boolean supports(Object source, Class<?> targetType) {
            return Map.class.isInstance(source);
        }
    }

    private static class TargetAwareMethodInterceptor
    implements MethodInterceptor {
        private static final Method GET_TARGET_CLASS_METHOD;
        private static final Method GET_TARGET_METHOD;
        private final Class<?> targetType;

        public TargetAwareMethodInterceptor(Class<?> targetType) {
            Assert.notNull(targetType, (String)"Target type must not be null!");
            this.targetType = targetType;
        }

        @Nullable
        public Object invoke(MethodInvocation invocation) throws Throwable {
            if (invocation.getMethod().equals(GET_TARGET_CLASS_METHOD)) {
                return this.targetType;
            }
            if (invocation.getMethod().equals(GET_TARGET_METHOD)) {
                return invocation.getThis();
            }
            return invocation.proceed();
        }

        static {
            try {
                GET_TARGET_CLASS_METHOD = TargetAware.class.getMethod("getTargetClass", new Class[0]);
                GET_TARGET_METHOD = TargetAware.class.getMethod("getTarget", new Class[0]);
            }
            catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}

