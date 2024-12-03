/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.DecoratingProxy
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.DecoratingProxy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

final class JdkDynamicAopProxy
implements AopProxy,
InvocationHandler,
Serializable {
    private static final long serialVersionUID = 5531744639992436476L;
    private static final Log logger = LogFactory.getLog(JdkDynamicAopProxy.class);
    private final AdvisedSupport advised;
    private final Class<?>[] proxiedInterfaces;
    private boolean equalsDefined;
    private boolean hashCodeDefined;

    public JdkDynamicAopProxy(AdvisedSupport config) throws AopConfigException {
        Assert.notNull((Object)config, (String)"AdvisedSupport must not be null");
        if (config.getAdvisorCount() == 0 && config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
            throw new AopConfigException("No advisors and no TargetSource specified");
        }
        this.advised = config;
        this.proxiedInterfaces = AopProxyUtils.completeProxiedInterfaces(this.advised, true);
        this.findDefinedEqualsAndHashCodeMethods(this.proxiedInterfaces);
    }

    @Override
    public Object getProxy() {
        return this.getProxy(ClassUtils.getDefaultClassLoader());
    }

    @Override
    public Object getProxy(@Nullable ClassLoader classLoader) {
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Creating JDK dynamic proxy: " + this.advised.getTargetSource()));
        }
        return Proxy.newProxyInstance(this.determineClassLoader(classLoader), this.proxiedInterfaces, (InvocationHandler)this);
    }

    private ClassLoader determineClassLoader(@Nullable ClassLoader classLoader) {
        if (classLoader == null) {
            return this.getClass().getClassLoader();
        }
        if (classLoader.getParent() == null) {
            ClassLoader aopClassLoader = this.getClass().getClassLoader();
            for (ClassLoader aopParent = aopClassLoader.getParent(); aopParent != null; aopParent = aopParent.getParent()) {
                if (classLoader != aopParent) continue;
                return aopClassLoader;
            }
        }
        return classLoader;
    }

    private void findDefinedEqualsAndHashCodeMethods(Class<?>[] proxiedInterfaces) {
        for (Class<?> proxiedInterface : proxiedInterfaces) {
            Method[] methods;
            for (Method method : methods = proxiedInterface.getDeclaredMethods()) {
                if (AopUtils.isEqualsMethod(method)) {
                    this.equalsDefined = true;
                }
                if (AopUtils.isHashCodeMethod(method)) {
                    this.hashCodeDefined = true;
                }
                if (!this.equalsDefined || !this.hashCodeDefined) continue;
                return;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object object;
        boolean setProxyContext;
        Object oldProxy;
        block27: {
            Object retVal;
            Class<?> targetClass;
            List<Object> chain;
            Object target;
            TargetSource targetSource;
            block25: {
                Object object2;
                block26: {
                    block23: {
                        Class<?> clazz;
                        block24: {
                            block21: {
                                Integer n;
                                block22: {
                                    block19: {
                                        Boolean bl;
                                        block20: {
                                            oldProxy = null;
                                            setProxyContext = false;
                                            targetSource = this.advised.targetSource;
                                            target = null;
                                            try {
                                                if (this.equalsDefined || !AopUtils.isEqualsMethod(method)) break block19;
                                                bl = this.equals(args[0]);
                                                if (target == null || targetSource.isStatic()) break block20;
                                            }
                                            catch (Throwable throwable) {
                                                if (target != null && !targetSource.isStatic()) {
                                                    targetSource.releaseTarget(target);
                                                }
                                                if (setProxyContext) {
                                                    AopContext.setCurrentProxy(oldProxy);
                                                }
                                                throw throwable;
                                            }
                                            targetSource.releaseTarget(target);
                                        }
                                        if (setProxyContext) {
                                            AopContext.setCurrentProxy(oldProxy);
                                        }
                                        return bl;
                                    }
                                    if (this.hashCodeDefined || !AopUtils.isHashCodeMethod(method)) break block21;
                                    n = this.hashCode();
                                    if (target == null || targetSource.isStatic()) break block22;
                                    targetSource.releaseTarget(target);
                                }
                                if (setProxyContext) {
                                    AopContext.setCurrentProxy(oldProxy);
                                }
                                return n;
                            }
                            if (method.getDeclaringClass() != DecoratingProxy.class) break block23;
                            clazz = AopProxyUtils.ultimateTargetClass(this.advised);
                            if (target == null || targetSource.isStatic()) break block24;
                            targetSource.releaseTarget(target);
                        }
                        if (setProxyContext) {
                            AopContext.setCurrentProxy(oldProxy);
                        }
                        return clazz;
                    }
                    if (this.advised.opaque || !method.getDeclaringClass().isInterface() || !method.getDeclaringClass().isAssignableFrom(Advised.class)) break block25;
                    object2 = AopUtils.invokeJoinpointUsingReflection(this.advised, method, args);
                    if (target == null || targetSource.isStatic()) break block26;
                    targetSource.releaseTarget(target);
                }
                if (setProxyContext) {
                    AopContext.setCurrentProxy(oldProxy);
                }
                return object2;
            }
            if (this.advised.exposeProxy) {
                oldProxy = AopContext.setCurrentProxy(proxy);
                setProxyContext = true;
            }
            if ((chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass = (target = targetSource.getTarget()) != null ? target.getClass() : null)).isEmpty()) {
                Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
                retVal = AopUtils.invokeJoinpointUsingReflection(target, method, argsToUse);
            } else {
                ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
                retVal = invocation.proceed();
            }
            Class<?> returnType = method.getReturnType();
            if (retVal != null && retVal == target && returnType != Object.class && returnType.isInstance(proxy) && !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
                retVal = proxy;
            } else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
                throw new AopInvocationException("Null return value from advice does not match primitive return type for: " + method);
            }
            object = retVal;
            if (target == null || targetSource.isStatic()) break block27;
            targetSource.releaseTarget(target);
        }
        if (setProxyContext) {
            AopContext.setCurrentProxy(oldProxy);
        }
        return object;
    }

    public boolean equals(@Nullable Object other) {
        JdkDynamicAopProxy otherProxy;
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other instanceof JdkDynamicAopProxy) {
            otherProxy = (JdkDynamicAopProxy)other;
        } else if (Proxy.isProxyClass(other.getClass())) {
            InvocationHandler ih = Proxy.getInvocationHandler(other);
            if (!(ih instanceof JdkDynamicAopProxy)) {
                return false;
            }
            otherProxy = (JdkDynamicAopProxy)ih;
        } else {
            return false;
        }
        return AopProxyUtils.equalsInProxy(this.advised, otherProxy.advised);
    }

    public int hashCode() {
        return JdkDynamicAopProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
    }
}

