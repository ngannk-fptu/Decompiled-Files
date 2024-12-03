/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.pretty.MessageHelper;

public class NonUniqueObjectException
extends HibernateException {
    private final Serializable identifier;
    private final String entityName;

    public NonUniqueObjectException(String message, Serializable entityId, String entityName) {
        super(message);
        this.entityName = entityName;
        this.identifier = entityId;
    }

    public NonUniqueObjectException(Serializable entityId, String entityName) {
        this("A different object with the same identifier value was already associated with the session", entityId, entityName);
    }

    public String getEntityName() {
        return this.entityName;
    }

    public Serializable getIdentifier() {
        return this.identifier;
    }

    public String getMessage() {
        return super.getMessage() + " : " + MessageHelper.infoString(this.entityName, this.identifier);
    }
}

