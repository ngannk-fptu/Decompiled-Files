/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jndi;

import org.hibernate.HibernateException;

public class JndiException
extends HibernateException {
    public JndiException(String message, Throwable cause) {
        super(message, cause);
    }

    public JndiException(String message) {
        super(message);
    }
}

