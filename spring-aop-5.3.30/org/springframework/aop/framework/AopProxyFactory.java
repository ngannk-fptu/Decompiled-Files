/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopProxy;

public interface AopProxyFactory {
    public AopProxy createAopProxy(AdvisedSupport var1) throws AopConfigException;
}

