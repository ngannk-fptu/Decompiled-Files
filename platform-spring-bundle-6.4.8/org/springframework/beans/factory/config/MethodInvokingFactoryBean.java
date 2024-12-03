/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.config.MethodInvokingBean;
import org.springframework.lang.Nullable;

public class MethodInvokingFactoryBean
extends MethodInvokingBean
implements FactoryBean<Object> {
    private boolean singleton = true;
    private boolean initialized = false;
    @Nullable
    private Object singletonObject;

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.prepare();
        if (this.singleton) {
            this.initialized = true;
            this.singletonObject = this.invokeWithTargetException();
        }
    }

    @Override
    @Nullable
    public Object getObject() throws Exception {
        if (this.singleton) {
            if (!this.initialized) {
                throw new FactoryBeanNotInitializedException();
            }
            return this.singletonObject;
        }
        return this.invokeWithTargetException();
    }

    @Override
    public Class<?> getObjectType() {
        if (!this.isPrepared()) {
            return null;
        }
        return this.getPreparedMethod().getReturnType();
    }

    @Override
    public boolean isSingleton() {
        return this.singleton;
    }
}

