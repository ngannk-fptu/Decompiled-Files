/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;
import org.hibernate.HibernateException;

public class WrongClassException
extends HibernateException {
    private final Serializable identifier;
    private final String entityName;

    public WrongClassException(String message, Serializable identifier, String entityName) {
        super(String.format("Object [id=%s] was not of the specified subclass [%s] : %s", identifier, entityName, message));
        this.identifier = identifier;
        this.entityName = entityName;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public Serializable getIdentifier() {
        return this.identifier;
    }
}

