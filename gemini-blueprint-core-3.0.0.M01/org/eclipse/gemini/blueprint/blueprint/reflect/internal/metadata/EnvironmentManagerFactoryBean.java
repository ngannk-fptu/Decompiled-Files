/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 */
package org.eclipse.gemini.blueprint.blueprint.reflect.internal.metadata;

import org.springframework.beans.factory.FactoryBean;

public class EnvironmentManagerFactoryBean
implements FactoryBean<Object> {
    private final Object instance;

    public EnvironmentManagerFactoryBean(Object instance) {
        this.instance = instance;
    }

    public Object getObject() throws Exception {
        return this.instance;
    }

    public Class<?> getObjectType() {
        return this.instance.getClass();
    }

    public boolean isSingleton() {
        return true;
    }
}

