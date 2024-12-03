/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 */
package org.hibernate;

import javax.persistence.PersistenceException;

public class HibernateException
extends PersistenceException {
    public HibernateException(String message) {
        super(message);
    }

    public HibernateException(Throwable cause) {
        super(cause);
    }

    public HibernateException(String message, Throwable cause) {
        super(message, cause);
    }
}

