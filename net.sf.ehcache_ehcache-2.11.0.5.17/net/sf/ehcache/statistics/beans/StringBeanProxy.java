/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.beans;

import net.sf.ehcache.statistics.beans.AttributeProxy;

public class StringBeanProxy
extends AttributeProxy<String> {
    private String val = "";

    public StringBeanProxy(String name, String descr, boolean isRead, boolean isWrite) {
        super(String.class, name, descr, isRead, isWrite);
    }

    @Override
    public String get(String name) {
        return this.val;
    }

    @Override
    public void set(String name, String t) {
        this.val = t;
    }
}

