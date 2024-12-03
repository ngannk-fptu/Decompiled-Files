/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jndi;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.naming.Context;
import javax.naming.NamingException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.jndi.JndiObjectLocator;
import org.springframework.jndi.JndiObjectTargetSource;
import org.springframework.jndi.JndiTemplate;
import org.springframework.jndi.TypeMismatchNamingException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class JndiObjectFactoryBean
extends JndiObjectLocator
implements FactoryBean<Object>,
BeanFactoryAware,
BeanClassLoaderAware {
    @Nullable
    private Class<?>[] proxyInterfaces;
    private boolean lookupOnStartup = true;
    private boolean cache = true;
    private boolean exposeAccessContext = false;
    @Nullable
    private Object defaultObject;
    @Nullable
    private ConfigurableBeanFactory beanFactory;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Nullable
    private Object jndiObject;

    public void setProxyInterface(Class<?> proxyInterface) {
        this.proxyInterfaces = new Class[]{proxyInterface};
    }

    public void setProxyInterfaces(Class<?> ... proxyInterfaces) {
        this.proxyInterfaces = proxyInterfaces;
    }

    public void setLookupOnStartup(boolean lookupOnStartup) {
        this.lookupOnStartup = lookupOnStartup;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public void setExposeAccessContext(boolean exposeAccessContext) {
        this.exposeAccessContext = exposeAccessContext;
    }

    public void setDefaultObject(Object defaultObject) {
        this.defaultObject = defaultObject;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.beanFactory = (ConfigurableBeanFactory)beanFactory;
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void afterPropertiesSet() throws IllegalArgumentException, NamingException {
        super.afterPropertiesSet();
        if (this.proxyInterfaces != null || !this.lookupOnStartup || !this.cache || this.exposeAccessContext) {
            if (this.defaultObject != null) {
                throw new IllegalArgumentException("'defaultObject' is not supported in combination with 'proxyInterface'");
            }
            this.jndiObject = JndiObjectProxyFactory.createJndiObjectProxy(this);
        } else {
            if (this.defaultObject != null && this.getExpectedType() != null && !this.getExpectedType().isInstance(this.defaultObject)) {
                TypeConverter converter = this.beanFactory != null ? this.beanFactory.getTypeConverter() : new SimpleTypeConverter();
                try {
                    this.defaultObject = converter.convertIfNecessary(this.defaultObject, this.getExpectedType());
                }
                catch (TypeMismatchException ex) {
                    throw new IllegalArgumentException("Default object [" + this.defaultObject + "] of type [" + this.defaultObject.getClass().getName() + "] is not of expected type [" + this.getExpectedType().getName() + "] and cannot be converted either", ex);
                }
            }
            this.jndiObject = this.lookupWithFallback();
        }
    }

    protected Object lookupWithFallback() throws NamingException {
        ClassLoader originalClassLoader = ClassUtils.overrideThreadContextClassLoader(this.beanClassLoader);
        try {
            Object object = this.lookup();
            return object;
        }
        catch (TypeMismatchNamingException ex) {
            throw ex;
        }
        catch (NamingException ex) {
            if (this.defaultObject != null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)"JNDI lookup failed - returning specified default object instead", (Throwable)ex);
                } else if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("JNDI lookup failed - returning specified default object instead: " + ex));
                }
                Object object = this.defaultObject;
                return object;
            }
            throw ex;
        }
        finally {
            if (originalClassLoader != null) {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        }
    }

    @Override
    @Nullable
    public Object getObject() {
        return this.jndiObject;
    }

    @Override
    public Class<?> getObjectType() {
        if (this.proxyInterfaces != null) {
            if (this.proxyInterfaces.length == 1) {
                return this.proxyInterfaces[0];
            }
            if (this.proxyInterfaces.length > 1) {
                return this.createCompositeInterface(this.proxyInterfaces);
            }
        }
        if (this.jndiObject != null) {
            return this.jndiObject.getClass();
        }
        return this.getExpectedType();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    protected Class<?> createCompositeInterface(Class<?>[] interfaces) {
        return ClassUtils.createCompositeInterface(interfaces, this.beanClassLoader);
    }

    private static class JndiContextExposingInterceptor
    implements MethodInterceptor {
        private final JndiTemplate jndiTemplate;

        public JndiContextExposingInterceptor(JndiTemplate jndiTemplate) {
            this.jndiTemplate = jndiTemplate;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Nullable
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Context ctx = this.isEligible(invocation.getMethod()) ? this.jndiTemplate.getContext() : null;
            try {
                Object object = invocation.proceed();
                return object;
            }
            finally {
                this.jndiTemplate.releaseContext(ctx);
            }
        }

        protected boolean isEligible(Method method) {
            return Object.class != method.getDeclaringClass();
        }
    }

    private static class JndiObjectProxyFactory {
        private JndiObjectProxyFactory() {
        }

        private static Object createJndiObjectProxy(JndiObjectFactoryBean jof) throws NamingException {
            JndiObjectTargetSource targetSource = new JndiObjectTargetSource();
            targetSource.setJndiTemplate(jof.getJndiTemplate());
            String jndiName = jof.getJndiName();
            Assert.state(jndiName != null, "No JNDI name specified");
            targetSource.setJndiName(jndiName);
            targetSource.setExpectedType(jof.getExpectedType());
            targetSource.setResourceRef(jof.isResourceRef());
            targetSource.setLookupOnStartup(jof.lookupOnStartup);
            targetSource.setCache(jof.cache);
            targetSource.afterPropertiesSet();
            ProxyFactory proxyFactory = new ProxyFactory();
            if (jof.proxyInterfaces != null) {
                proxyFactory.setInterfaces(jof.proxyInterfaces);
            } else {
                Class<?>[] ifcs;
                Class<?> targetClass = targetSource.getTargetClass();
                if (targetClass == null) {
                    throw new IllegalStateException("Cannot deactivate 'lookupOnStartup' without specifying a 'proxyInterface' or 'expectedType'");
                }
                for (Class<?> ifc : ifcs = ClassUtils.getAllInterfacesForClass(targetClass, jof.beanClassLoader)) {
                    if (!Modifier.isPublic(ifc.getModifiers())) continue;
                    proxyFactory.addInterface(ifc);
                }
            }
            if (jof.exposeAccessContext) {
                proxyFactory.addAdvice(new JndiContextExposingInterceptor(jof.getJndiTemplate()));
            }
            proxyFactory.setTargetSource(targetSource);
            return proxyFactory.getProxy(jof.beanClassLoader);
        }
    }
}

