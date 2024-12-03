/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.framework.ProxyFactory
 */
package org.springframework.data.repository.core.support;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.repository.core.RepositoryInformation;

public interface RepositoryProxyPostProcessor {
    public void postProcess(ProxyFactory var1, RepositoryInformation var2);
}

