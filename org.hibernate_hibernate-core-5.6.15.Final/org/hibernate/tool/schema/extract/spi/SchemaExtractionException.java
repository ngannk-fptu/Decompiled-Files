/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.spi;

import org.hibernate.HibernateException;

public class SchemaExtractionException
extends HibernateException {
    public SchemaExtractionException(String message) {
        super(message);
    }

    public SchemaExtractionException(String message, Throwable root) {
        super(message, root);
    }
}

