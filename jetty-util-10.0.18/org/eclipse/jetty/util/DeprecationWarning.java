/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util;

import org.eclipse.jetty.util.Decorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeprecationWarning
implements Decorator {
    private static final Logger LOG = LoggerFactory.getLogger(DeprecationWarning.class);

    @Override
    public <T> T decorate(T o) {
        if (o == null) {
            return null;
        }
        Class<?> clazz = o.getClass();
        try {
            Deprecated depr = clazz.getAnnotation(Deprecated.class);
            if (depr != null) {
                LOG.warn("Using @Deprecated Class {}", (Object)clazz.getName());
            }
        }
        catch (Throwable t) {
            LOG.trace("IGNORED", t);
        }
        this.verifyIndirectTypes(clazz.getSuperclass(), clazz, "Class");
        for (Class<?> ifaceClazz : clazz.getInterfaces()) {
            this.verifyIndirectTypes(ifaceClazz, clazz, "Interface");
        }
        return o;
    }

    private void verifyIndirectTypes(Class<?> superClazz, Class<?> clazz, String typeName) {
        try {
            while (superClazz != null && superClazz != Object.class) {
                Deprecated supDepr = superClazz.getAnnotation(Deprecated.class);
                if (supDepr != null) {
                    LOG.warn("Using indirect @Deprecated {} {} - (seen from {})", new Object[]{typeName, superClazz.getName(), clazz});
                }
                superClazz = superClazz.getSuperclass();
            }
        }
        catch (Throwable t) {
            LOG.trace("IGNORED", t);
        }
    }

    @Override
    public void destroy(Object o) {
    }
}

