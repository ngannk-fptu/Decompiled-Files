/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.HibernateException;

public class CallbackException
extends HibernateException {
    public CallbackException(Exception cause) {
        this("An exception occurred in a callback", cause);
    }

    public CallbackException(String message) {
        super(message);
    }

    public CallbackException(String message, Exception cause) {
        super(message, cause);
    }
}

