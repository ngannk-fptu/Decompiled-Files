/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute
 */
package org.hibernate.query.criteria.internal;

import javax.persistence.metamodel.Attribute;

public class BasicPathUsageException
extends RuntimeException {
    private final Attribute<?, ?> attribute;

    public BasicPathUsageException(String message, Attribute<?, ?> attribute) {
        super(message);
        this.attribute = attribute;
    }

    public BasicPathUsageException(String message, Throwable cause, Attribute<?, ?> attribute) {
        super(message, cause);
        this.attribute = attribute;
    }

    public Attribute<?, ?> getAttribute() {
        return this.attribute;
    }
}

