/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.HibernateException;

public class StaleStateException
extends HibernateException {
    public StaleStateException(String message) {
        super(message);
    }
}

