/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.pretty.MessageHelper;

public class UnresolvableObjectException
extends HibernateException {
    private final Serializable identifier;
    private final String entityName;

    public UnresolvableObjectException(Serializable identifier, String entityName) {
        this("No row with the given identifier exists", identifier, entityName);
    }

    protected UnresolvableObjectException(String message, Serializable identifier, String clazz) {
        super(message);
        this.identifier = identifier;
        this.entityName = clazz;
    }

    public static void throwIfNull(Object entity, Serializable identifier, String entityName) throws UnresolvableObjectException {
        if (entity == null) {
            throw new UnresolvableObjectException(identifier, entityName);
        }
    }

    public Serializable getIdentifier() {
        return this.identifier;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public String getMessage() {
        return super.getMessage() + ": " + MessageHelper.infoString(this.entityName, this.identifier);
    }
}

