/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.factory.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class ServiceLocatorFactoryBean
implements FactoryBean<Object>,
BeanFactoryAware,
InitializingBean {
    @Nullable
    private Class<?> serviceLocatorInterface;
    @Nullable
    private Constructor<Exception> serviceLocatorExceptionConstructor;
    @Nullable
    private Properties serviceMappings;
    @Nullable
    private ListableBeanFactory beanFactory;
    @Nullable
    private Object proxy;

    public void setServiceLocatorInterface(Class<?> interfaceType) {
        this.serviceLocatorInterface = interfaceType;
    }

    public void setServiceLocatorExceptionClass(Class<? extends Exception> serviceLocatorExceptionClass) {
        this.serviceLocatorExceptionConstructor = this.determineServiceLocatorExceptionConstructor(serviceLocatorExceptionClass);
    }

    public void setServiceMappings(Properties serviceMappings) {
        this.serviceMappings = serviceMappings;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ListableBeanFactory)) {
            throw new FatalBeanException("ServiceLocatorFactoryBean needs to run in a BeanFactory that is a ListableBeanFactory");
        }
        this.beanFactory = (ListableBeanFactory)beanFactory;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.serviceLocatorInterface == null) {
            throw new IllegalArgumentException("Property 'serviceLocatorInterface' is required");
        }
        this.proxy = Proxy.newProxyInstance(this.serviceLocatorInterface.getClassLoader(), new Class[]{this.serviceLocatorInterface}, (InvocationHandler)new ServiceLocatorInvocationHandler());
    }

    protected Constructor<Exception> determineServiceLocatorExceptionConstructor(Class<? extends Exception> exceptionClass) {
        try {
            return exceptionClass.getConstructor(String.class, Throwable.class);
        }
        catch (NoSuchMethodException ex) {
            try {
                return exceptionClass.getConstructor(Throwable.class);
            }
            catch (NoSuchMethodException ex2) {
                try {
                    return exceptionClass.getConstructor(String.class);
                }
                catch (NoSuchMethodException ex3) {
                    throw new IllegalArgumentException("Service locator exception [" + exceptionClass.getName() + "] neither has a (String, Throwable) constructor nor a (String) constructor");
                }
            }
        }
    }

    protected Exception createServiceLocatorException(Constructor<Exception> exceptionConstructor, BeansException cause) {
        Class<?>[] paramTypes = exceptionConstructor.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; ++i) {
            if (String.class == paramTypes[i]) {
                args[i] = cause.getMessage();
                continue;
            }
            if (!paramTypes[i].isInstance((Object)cause)) continue;
            args[i] = cause;
        }
        return BeanUtils.instantiateClass(exceptionConstructor, args);
    }

    @Override
    @Nullable
    public Object getObject() {
        return this.proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return this.serviceLocatorInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private class ServiceLocatorInvocationHandler
    implements InvocationHandler {
        private ServiceLocatorInvocationHandler() {
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (ReflectionUtils.isEqualsMethod((Method)method)) {
                return proxy == args[0];
            }
            if (ReflectionUtils.isHashCodeMethod((Method)method)) {
                return System.identityHashCode(proxy);
            }
            if (ReflectionUtils.isToStringMethod((Method)method)) {
                return "Service locator: " + ServiceLocatorFactoryBean.this.serviceLocatorInterface;
            }
            return this.invokeServiceLocatorMethod(method, args);
        }

        private Object invokeServiceLocatorMethod(Method method, Object[] args) throws Exception {
            Class<?> serviceLocatorMethodReturnType = this.getServiceLocatorMethodReturnType(method);
            try {
                String beanName = this.tryGetBeanName(args);
                Assert.state((ServiceLocatorFactoryBean.this.beanFactory != null ? 1 : 0) != 0, (String)"No BeanFactory available");
                if (StringUtils.hasLength((String)beanName)) {
                    return ServiceLocatorFactoryBean.this.beanFactory.getBean(beanName, serviceLocatorMethodReturnType);
                }
                return ServiceLocatorFactoryBean.this.beanFactory.getBean(serviceLocatorMethodReturnType);
            }
            catch (BeansException ex) {
                if (ServiceLocatorFactoryBean.this.serviceLocatorExceptionConstructor != null) {
                    throw ServiceLocatorFactoryBean.this.createServiceLocatorException(ServiceLocatorFactoryBean.this.serviceLocatorExceptionConstructor, ex);
                }
                throw ex;
            }
        }

        private String tryGetBeanName(@Nullable Object[] args) {
            String mappedName;
            String beanName = "";
            if (args != null && args.length == 1 && args[0] != null) {
                beanName = args[0].toString();
            }
            if (ServiceLocatorFactoryBean.this.serviceMappings != null && (mappedName = ServiceLocatorFactoryBean.this.serviceMappings.getProperty(beanName)) != null) {
                beanName = mappedName;
            }
            return beanName;
        }

        private Class<?> getServiceLocatorMethodReturnType(Method method) throws NoSuchMethodException {
            Assert.state((ServiceLocatorFactoryBean.this.serviceLocatorInterface != null ? 1 : 0) != 0, (String)"No service locator interface specified");
            Class<?>[] paramTypes = method.getParameterTypes();
            Method interfaceMethod = ServiceLocatorFactoryBean.this.serviceLocatorInterface.getMethod(method.getName(), paramTypes);
            Class<?> serviceLocatorReturnType = interfaceMethod.getReturnType();
            if (paramTypes.length > 1 || Void.TYPE == serviceLocatorReturnType) {
                throw new UnsupportedOperationException("May only call methods with signature '<type> xxx()' or '<type> xxx(<idtype> id)' on factory interface, but tried to call: " + interfaceMethod);
            }
            return serviceLocatorReturnType;
        }
    }
}

