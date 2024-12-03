/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import org.hibernate.engine.spi.IdentifierValue;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.PostInsertIdentifierGenerator;
import org.hibernate.tuple.AbstractAttribute;
import org.hibernate.tuple.IdentifierAttribute;
import org.hibernate.type.Type;

public class IdentifierProperty
extends AbstractAttribute
implements IdentifierAttribute {
    private final boolean virtual;
    private final boolean embedded;
    private final IdentifierValue unsavedValue;
    private final IdentifierGenerator identifierGenerator;
    private final boolean identifierAssignedByInsert;
    private final boolean hasIdentifierMapper;

    public IdentifierProperty(String name, Type type, boolean embedded, IdentifierValue unsavedValue, IdentifierGenerator identifierGenerator) {
        super(name, type);
        this.virtual = false;
        this.embedded = embedded;
        this.hasIdentifierMapper = false;
        this.unsavedValue = unsavedValue;
        this.identifierGenerator = identifierGenerator;
        this.identifierAssignedByInsert = identifierGenerator instanceof PostInsertIdentifierGenerator;
    }

    public IdentifierProperty(Type type, boolean embedded, boolean hasIdentifierMapper, IdentifierValue unsavedValue, IdentifierGenerator identifierGenerator) {
        super(null, type);
        this.virtual = true;
        this.embedded = embedded;
        this.hasIdentifierMapper = hasIdentifierMapper;
        this.unsavedValue = unsavedValue;
        this.identifierGenerator = identifierGenerator;
        this.identifierAssignedByInsert = identifierGenerator instanceof PostInsertIdentifierGenerator;
    }

    @Override
    public boolean isVirtual() {
        return this.virtual;
    }

    @Override
    public boolean isEmbedded() {
        return this.embedded;
    }

    @Override
    public IdentifierValue getUnsavedValue() {
        return this.unsavedValue;
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator() {
        return this.identifierGenerator;
    }

    @Override
    public boolean isIdentifierAssignedByInsert() {
        return this.identifierAssignedByInsert;
    }

    @Override
    public boolean hasIdentifierMapper() {
        return this.hasIdentifierMapper;
    }

    public String toString() {
        return "IdentifierAttribute(" + this.getName() + ")";
    }
}

