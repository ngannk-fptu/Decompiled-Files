/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import org.hibernate.QueryException;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.AbstractPropertyMapping;
import org.hibernate.type.Type;

public class BasicEntityPropertyMapping
extends AbstractPropertyMapping {
    private final AbstractEntityPersister persister;

    public BasicEntityPropertyMapping(AbstractEntityPersister persister) {
        this.persister = persister;
    }

    @Override
    public String[] getIdentifierColumnNames() {
        return this.persister.getIdentifierColumnNames();
    }

    @Override
    public String[] getIdentifierColumnReaders() {
        return this.persister.getIdentifierColumnReaders();
    }

    @Override
    public String[] getIdentifierColumnReaderTemplates() {
        return this.persister.getIdentifierColumnReaderTemplates();
    }

    @Override
    protected String getEntityName() {
        return this.persister.getEntityName();
    }

    @Override
    public Type getType() {
        return this.persister.getType();
    }

    @Override
    public String[] toColumns(String alias, String propertyName) throws QueryException {
        return super.toColumns(AbstractEntityPersister.generateTableAlias(alias, this.persister.getSubclassPropertyTableNumber(propertyName)), propertyName);
    }
}

