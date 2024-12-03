/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.spi;

import org.hibernate.HibernateException;

public class PropertyAccessBuildingException
extends HibernateException {
    public PropertyAccessBuildingException(String message) {
        super(message);
    }

    public PropertyAccessBuildingException(String message, Throwable cause) {
        super(message, cause);
    }
}

