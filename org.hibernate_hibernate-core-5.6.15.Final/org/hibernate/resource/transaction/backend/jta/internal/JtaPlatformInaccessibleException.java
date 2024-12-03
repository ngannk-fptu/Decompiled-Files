/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.backend.jta.internal;

import org.hibernate.HibernateException;

public class JtaPlatformInaccessibleException
extends HibernateException {
    public JtaPlatformInaccessibleException(String message) {
        super(message);
    }

    public JtaPlatformInaccessibleException(String message, Throwable cause) {
        super(message, cause);
    }
}

