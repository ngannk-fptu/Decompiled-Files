/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.HibernateException;

public class ResourceClosedException
extends HibernateException {
    public ResourceClosedException(String message) {
        super(message);
    }
}

