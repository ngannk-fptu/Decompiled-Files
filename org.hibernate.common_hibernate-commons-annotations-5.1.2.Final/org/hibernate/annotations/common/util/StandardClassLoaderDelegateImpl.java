/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.annotations.common.util;

import org.hibernate.annotations.common.reflection.ClassLoaderDelegate;
import org.hibernate.annotations.common.reflection.ClassLoadingException;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;

@Deprecated
public class StandardClassLoaderDelegateImpl
implements ClassLoaderDelegate {
    public static final StandardClassLoaderDelegateImpl INSTANCE = new StandardClassLoaderDelegateImpl();
    private static final Logger log = LoggerFactory.logger(StandardClassLoaderDelegateImpl.class);

    @Override
    public <T> Class<T> classForName(String className) throws ClassLoadingException {
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                return Class.forName(className, true, contextClassLoader);
            }
        }
        catch (Throwable ignore) {
            log.debugf("Unable to locate Class [%s] using TCCL, falling back to HCANN ClassLoader", (Object)className);
        }
        try {
            return Class.forName(className, true, this.getClass().getClassLoader());
        }
        catch (ClassNotFoundException e) {
            throw new ClassLoadingException("Unable to load Class [" + className + "]", e);
        }
    }
}

