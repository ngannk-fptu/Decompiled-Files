/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.HibernateException;

public class HibernateError
extends HibernateException {
    public HibernateError(String message) {
        super(message);
    }

    public HibernateError(String message, Throwable cause) {
        super(message, cause);
    }
}

