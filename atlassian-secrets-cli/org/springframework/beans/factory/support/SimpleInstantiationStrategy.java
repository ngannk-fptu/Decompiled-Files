/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.InstantiationStrategy;
import org.springframework.beans.factory.support.NullBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class SimpleInstantiationStrategy
implements InstantiationStrategy {
    private static final ThreadLocal<Method> currentlyInvokedFactoryMethod = new ThreadLocal();

    @Nullable
    public static Method getCurrentlyInvokedFactoryMethod() {
        return currentlyInvokedFactoryMethod.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
        if (!bd.hasMethodOverrides()) {
            Constructor constructorToUse;
            Object object = bd.constructorArgumentLock;
            synchronized (object) {
                constructorToUse = (Constructor)bd.resolvedConstructorOrFactoryMethod;
                if (constructorToUse == null) {
                    Class<?> clazz = bd.getBeanClass();
                    if (clazz.isInterface()) {
                        throw new BeanInstantiationException(clazz, "Specified class is an interface");
                    }
                    try {
                        constructorToUse = System.getSecurityManager() != null ? AccessController.doPrivileged(() -> clazz.getDeclaredConstructor(new Class[0])) : clazz.getDeclaredConstructor(new Class[0]);
                        bd.resolvedConstructorOrFactoryMethod = constructorToUse;
                    }
                    catch (Throwable ex) {
                        throw new BeanInstantiationException(clazz, "No default constructor found", ex);
                    }
                }
            }
            return BeanUtils.instantiateClass(constructorToUse, new Object[0]);
        }
        return this.instantiateWithMethodInjection(bd, beanName, owner);
    }

    protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
        throw new UnsupportedOperationException("Method Injection not supported in SimpleInstantiationStrategy");
    }

    @Override
    public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner, Constructor<?> ctor, Object ... args) {
        if (!bd.hasMethodOverrides()) {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(() -> {
                    ReflectionUtils.makeAccessible(ctor);
                    return null;
                });
            }
            return BeanUtils.instantiateClass(ctor, args);
        }
        return this.instantiateWithMethodInjection(bd, beanName, owner, ctor, args);
    }

    protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner, @Nullable Constructor<?> ctor, Object ... args) {
        throw new UnsupportedOperationException("Method Injection not supported in SimpleInstantiationStrategy");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner, @Nullable Object factoryBean, Method factoryMethod, Object ... args) {
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged(() -> {
                ReflectionUtils.makeAccessible(factoryMethod);
                return null;
            });
        } else {
            ReflectionUtils.makeAccessible(factoryMethod);
        }
        Method priorInvokedFactoryMethod = currentlyInvokedFactoryMethod.get();
        try {
            currentlyInvokedFactoryMethod.set(factoryMethod);
            Object result = factoryMethod.invoke(factoryBean, args);
            if (result == null) {
                result = new NullBean();
            }
            Object object = result;
            if (priorInvokedFactoryMethod != null) {
                currentlyInvokedFactoryMethod.set(priorInvokedFactoryMethod);
            } else {
                currentlyInvokedFactoryMethod.remove();
            }
            return object;
        }
        catch (Throwable throwable) {
            try {
                if (priorInvokedFactoryMethod != null) {
                    currentlyInvokedFactoryMethod.set(priorInvokedFactoryMethod);
                } else {
                    currentlyInvokedFactoryMethod.remove();
                }
                throw throwable;
            }
            catch (IllegalArgumentException ex) {
                throw new BeanInstantiationException(factoryMethod, "Illegal arguments to factory method '" + factoryMethod.getName() + "'; args: " + StringUtils.arrayToCommaDelimitedString(args), (Throwable)ex);
            }
            catch (IllegalAccessException ex) {
                throw new BeanInstantiationException(factoryMethod, "Cannot access factory method '" + factoryMethod.getName() + "'; is it public?", (Throwable)ex);
            }
            catch (InvocationTargetException ex) {
                String msg = "Factory method '" + factoryMethod.getName() + "' threw exception";
                if (bd.getFactoryBeanName() != null && owner instanceof ConfigurableBeanFactory && ((ConfigurableBeanFactory)owner).isCurrentlyInCreation(bd.getFactoryBeanName())) {
                    msg = "Circular reference involving containing bean '" + bd.getFactoryBeanName() + "' - consider declaring the factory method as static for independence from its containing instance. " + msg;
                }
                throw new BeanInstantiationException(factoryMethod, msg, ex.getTargetException());
            }
        }
    }
}

