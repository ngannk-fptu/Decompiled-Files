/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.classloading.spi;

import org.hibernate.HibernateException;

public class ClassLoadingException
extends HibernateException {
    public ClassLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassLoadingException(String message) {
        super(message);
    }
}

