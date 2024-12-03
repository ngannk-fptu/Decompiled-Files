/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.beans;

import net.sf.ehcache.statistics.beans.AttributeProxy;

public class LongBeanProxy
extends AttributeProxy<Long> {
    private long val = 0L;

    public LongBeanProxy(String name, String description, boolean isRead, boolean isWrite) {
        super(Long.class, name, description, isRead, isWrite);
    }

    @Override
    public Long get(String name) {
        return this.val;
    }

    @Override
    public void set(String name, Long t) {
        this.val = t;
    }
}

