/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.naming.JavaBeanObjectFactory
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.impl.IdentityTokenized;
import com.mchange.v2.naming.JavaBeanObjectFactory;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

public class C3P0JavaBeanObjectFactory
extends JavaBeanObjectFactory {
    private static final Class[] CTOR_ARG_TYPES = new Class[]{Boolean.TYPE};
    private static final Object[] CTOR_ARGS = new Object[]{Boolean.FALSE};

    protected Object createBlankInstance(Class beanClass) throws Exception {
        if (IdentityTokenized.class.isAssignableFrom(beanClass)) {
            Constructor ctor = beanClass.getConstructor(CTOR_ARG_TYPES);
            return ctor.newInstance(CTOR_ARGS);
        }
        return super.createBlankInstance(beanClass);
    }

    protected Object findBean(Class beanClass, Map propertyMap, Set refProps) throws Exception {
        Object out = super.findBean(beanClass, propertyMap, refProps);
        if (out instanceof IdentityTokenized) {
            out = C3P0Registry.reregister((IdentityTokenized)out);
        }
        return out;
    }
}

