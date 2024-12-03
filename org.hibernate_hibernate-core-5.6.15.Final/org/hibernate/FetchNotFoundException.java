/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityNotFoundException
 */
package org.hibernate;

import java.util.Locale;
import javax.persistence.EntityNotFoundException;

public class FetchNotFoundException
extends EntityNotFoundException {
    private final String entityName;
    private final Object identifier;

    public FetchNotFoundException(String entityName, Object identifier) {
        super(String.format(Locale.ROOT, "Entity `%s` with identifier value `%s` does not exist", entityName, identifier));
        this.entityName = entityName;
        this.identifier = identifier;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public Object getIdentifier() {
        return this.identifier;
    }
}

