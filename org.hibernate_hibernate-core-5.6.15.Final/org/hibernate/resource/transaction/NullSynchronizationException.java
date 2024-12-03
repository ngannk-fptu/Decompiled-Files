/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction;

import org.hibernate.HibernateException;

public class NullSynchronizationException
extends HibernateException {
    public NullSynchronizationException() {
        this("Synchronization to register cannot be null");
    }

    public NullSynchronizationException(String s) {
        super(s);
    }
}

