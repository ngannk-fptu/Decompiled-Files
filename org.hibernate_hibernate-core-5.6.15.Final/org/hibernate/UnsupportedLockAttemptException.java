/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.HibernateException;

public class UnsupportedLockAttemptException
extends HibernateException {
    public UnsupportedLockAttemptException(String message) {
        super(message);
    }

    public UnsupportedLockAttemptException(Throwable cause) {
        super(cause);
    }

    public UnsupportedLockAttemptException(String message, Throwable cause) {
        super(message, cause);
    }
}

