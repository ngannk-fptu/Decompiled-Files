/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.beans;

import net.sf.ehcache.statistics.beans.AttributeProxy;

public class BooleanBeanProxy
extends AttributeProxy<Boolean> {
    private Boolean b = Boolean.FALSE;

    public BooleanBeanProxy(String name, String description, boolean isRead, boolean isWrite) {
        super(Boolean.class, name, description, isRead, isWrite);
    }

    @Override
    public Boolean get(String name) {
        return this.b;
    }

    @Override
    public void set(String name, Boolean t) {
        this.b = t;
    }
}

