/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean2;

public class BeanUtilsBean2
extends BeanUtilsBean {
    public BeanUtilsBean2() {
        super(new ConvertUtilsBean2());
    }

    @Override
    protected Object convert(Object value, Class<?> type) {
        return this.getConvertUtils().convert(value, type);
    }
}

