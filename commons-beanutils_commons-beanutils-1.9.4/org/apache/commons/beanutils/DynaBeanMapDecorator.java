/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import org.apache.commons.beanutils.BaseDynaBeanMapDecorator;
import org.apache.commons.beanutils.DynaBean;

@Deprecated
public class DynaBeanMapDecorator
extends BaseDynaBeanMapDecorator<Object> {
    public DynaBeanMapDecorator(DynaBean dynaBean, boolean readOnly) {
        super(dynaBean, readOnly);
    }

    public DynaBeanMapDecorator(DynaBean dynaBean) {
        super(dynaBean);
    }

    @Override
    protected Object convertKey(String propertyName) {
        return propertyName;
    }
}

