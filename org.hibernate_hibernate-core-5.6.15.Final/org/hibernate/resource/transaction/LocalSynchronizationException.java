/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction;

import org.hibernate.HibernateException;

public class LocalSynchronizationException
extends HibernateException {
    public LocalSynchronizationException(String message, Throwable cause) {
        super(message, cause);
    }
}

