/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.confluence.spring;

import java.util.Objects;
import org.springframework.beans.factory.FactoryBean;

@Deprecated
public class ObjectHolder
implements FactoryBean<Object> {
    private Object object;

    public void setObject(Object object) {
        this.object = Objects.requireNonNull(object);
    }

    public Object getObject() throws Exception {
        return this.object;
    }

    public Class<?> getObjectType() {
        return this.object.getClass();
    }

    public boolean isSingleton() {
        return true;
    }
}

