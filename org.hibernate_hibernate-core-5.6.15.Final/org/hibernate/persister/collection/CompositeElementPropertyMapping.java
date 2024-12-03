/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.collection;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.persister.entity.AbstractPropertyMapping;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public class CompositeElementPropertyMapping
extends AbstractPropertyMapping {
    private final CompositeType compositeType;

    public CompositeElementPropertyMapping(String[] elementColumns, String[] elementColumnReaders, String[] elementColumnReaderTemplates, String[] elementFormulaTemplates, CompositeType compositeType, Mapping factory) throws MappingException {
        this.compositeType = compositeType;
        this.initComponentPropertyPaths(null, compositeType, elementColumns, elementColumnReaders, elementColumnReaderTemplates, elementFormulaTemplates, factory);
    }

    @Override
    public Type getType() {
        return this.compositeType;
    }

    @Override
    protected String getEntityName() {
        return this.compositeType.getName();
    }
}

