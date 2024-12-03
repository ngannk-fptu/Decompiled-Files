/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.HibernateException;

public class SerializationException
extends HibernateException {
    public SerializationException(String message, Exception root) {
        super(message, root);
    }
}

