/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.AopProxyFactory;
import org.springframework.aop.framework.JdkDynamicAopProxy;
import org.springframework.aop.framework.ObjenesisCglibAopProxy;
import org.springframework.core.NativeDetector;
import org.springframework.util.ClassUtils;

public class DefaultAopProxyFactory
implements AopProxyFactory,
Serializable {
    private static final long serialVersionUID = 7930414337282325166L;

    @Override
    public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
        if (!NativeDetector.inNativeImage() && (config.isOptimize() || config.isProxyTargetClass() || this.hasNoUserSuppliedProxyInterfaces(config))) {
            Class<?> targetClass = config.getTargetClass();
            if (targetClass == null) {
                throw new AopConfigException("TargetSource cannot determine target class: Either an interface or a target is required for proxy creation.");
            }
            if (targetClass.isInterface() || Proxy.isProxyClass(targetClass) || ClassUtils.isLambdaClass(targetClass)) {
                return new JdkDynamicAopProxy(config);
            }
            return new ObjenesisCglibAopProxy(config);
        }
        return new JdkDynamicAopProxy(config);
    }

    private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
        Class<?>[] ifcs = config.getProxiedInterfaces();
        return ifcs.length == 0 || ifcs.length == 1 && SpringProxy.class.isAssignableFrom(ifcs[0]);
    }
}

