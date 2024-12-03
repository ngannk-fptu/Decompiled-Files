/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache;

import org.hibernate.HibernateException;

public class CacheException
extends HibernateException {
    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }
}

