/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.context;

import org.hibernate.HibernateException;

public class TenantIdentifierMismatchException
extends HibernateException {
    public TenantIdentifierMismatchException(String message) {
        super(message);
    }

    public TenantIdentifierMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}

