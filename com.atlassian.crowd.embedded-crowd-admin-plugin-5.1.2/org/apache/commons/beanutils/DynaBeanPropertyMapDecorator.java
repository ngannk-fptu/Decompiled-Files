/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import org.apache.commons.beanutils.BaseDynaBeanMapDecorator;
import org.apache.commons.beanutils.DynaBean;

public class DynaBeanPropertyMapDecorator
extends BaseDynaBeanMapDecorator<String> {
    public DynaBeanPropertyMapDecorator(DynaBean dynaBean, boolean readOnly) {
        super(dynaBean, readOnly);
    }

    public DynaBeanPropertyMapDecorator(DynaBean dynaBean) {
        super(dynaBean);
    }

    @Override
    protected String convertKey(String propertyName) {
        return propertyName;
    }
}

