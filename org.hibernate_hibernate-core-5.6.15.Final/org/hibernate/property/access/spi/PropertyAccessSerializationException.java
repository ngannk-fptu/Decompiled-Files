/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.spi;

import org.hibernate.HibernateException;

public class PropertyAccessSerializationException
extends HibernateException {
    public PropertyAccessSerializationException(String message) {
        super(message);
    }

    public PropertyAccessSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}

