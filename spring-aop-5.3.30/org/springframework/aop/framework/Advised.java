/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AopConfigException;

public interface Advised
extends TargetClassAware {
    public boolean isFrozen();

    public boolean isProxyTargetClass();

    public Class<?>[] getProxiedInterfaces();

    public boolean isInterfaceProxied(Class<?> var1);

    public void setTargetSource(TargetSource var1);

    public TargetSource getTargetSource();

    public void setExposeProxy(boolean var1);

    public boolean isExposeProxy();

    public void setPreFiltered(boolean var1);

    public boolean isPreFiltered();

    public Advisor[] getAdvisors();

    default public int getAdvisorCount() {
        return this.getAdvisors().length;
    }

    public void addAdvisor(Advisor var1) throws AopConfigException;

    public void addAdvisor(int var1, Advisor var2) throws AopConfigException;

    public boolean removeAdvisor(Advisor var1);

    public void removeAdvisor(int var1) throws AopConfigException;

    public int indexOf(Advisor var1);

    public boolean replaceAdvisor(Advisor var1, Advisor var2) throws AopConfigException;

    public void addAdvice(Advice var1) throws AopConfigException;

    public void addAdvice(int var1, Advice var2) throws AopConfigException;

    public boolean removeAdvice(Advice var1);

    public int indexOf(Advice var1);

    public String toProxyConfigString();
}

