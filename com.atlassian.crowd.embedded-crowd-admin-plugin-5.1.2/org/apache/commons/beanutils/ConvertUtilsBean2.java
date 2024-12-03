/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import org.apache.commons.beanutils.ConvertUtilsBean;

public class ConvertUtilsBean2
extends ConvertUtilsBean {
    @Override
    public String convert(Object value) {
        return (String)this.convert(value, String.class);
    }

    @Override
    public Object convert(String value, Class<?> clazz) {
        return this.convert((Object)value, clazz);
    }

    @Override
    public Object convert(String[] value, Class<?> clazz) {
        return this.convert((Object)value, clazz);
    }
}

