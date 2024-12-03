/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 */
package org.eclipse.gemini.blueprint.config.internal.adapter;

import org.springframework.beans.factory.FactoryBean;

public class ToStringClassAdapter
implements FactoryBean<String> {
    private final String toString;

    private ToStringClassAdapter(Object target) {
        this.toString = target instanceof Class ? ((Class)target).getName() : (target == null ? "" : target.toString());
    }

    public String getObject() throws Exception {
        return this.toString;
    }

    public Class<? extends String> getObjectType() {
        return String.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

