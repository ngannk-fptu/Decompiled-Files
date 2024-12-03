/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.aop.framework;

import java.lang.reflect.Constructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.CglibAopProxy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.objenesis.SpringObjenesis;
import org.springframework.util.ReflectionUtils;

class ObjenesisCglibAopProxy
extends CglibAopProxy {
    private static final Log logger = LogFactory.getLog(ObjenesisCglibAopProxy.class);
    private static final SpringObjenesis objenesis = new SpringObjenesis();

    public ObjenesisCglibAopProxy(AdvisedSupport config) {
        super(config);
    }

    @Override
    protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {
        Class proxyClass = enhancer.createClass();
        Object proxyInstance = null;
        if (objenesis.isWorthTrying()) {
            try {
                proxyInstance = objenesis.newInstance(proxyClass, enhancer.getUseCache());
            }
            catch (Throwable ex) {
                logger.debug((Object)"Unable to instantiate proxy using Objenesis, falling back to regular proxy construction", ex);
            }
        }
        if (proxyInstance == null) {
            try {
                Constructor ctor = this.constructorArgs != null ? proxyClass.getDeclaredConstructor(this.constructorArgTypes) : proxyClass.getDeclaredConstructor(new Class[0]);
                ReflectionUtils.makeAccessible(ctor);
                proxyInstance = (this.constructorArgs != null ? ctor.newInstance(this.constructorArgs) : ctor.newInstance(new Object[0]));
            }
            catch (Throwable ex) {
                throw new AopConfigException("Unable to instantiate proxy using Objenesis, and regular proxy instantiation via default constructor fails as well", ex);
            }
        }
        ((Factory)proxyInstance).setCallbacks(callbacks);
        return proxyInstance;
    }
}

