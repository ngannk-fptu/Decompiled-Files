/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.HibernateException;

public class SchemaManagementException
extends HibernateException {
    public SchemaManagementException(String message) {
        super(message);
    }

    public SchemaManagementException(String message, Throwable root) {
        super(message, root);
    }
}

