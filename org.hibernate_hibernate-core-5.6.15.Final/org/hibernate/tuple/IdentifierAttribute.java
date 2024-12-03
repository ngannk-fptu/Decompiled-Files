/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import org.hibernate.engine.spi.IdentifierValue;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.tuple.Attribute;
import org.hibernate.tuple.Property;

public interface IdentifierAttribute
extends Attribute,
Property {
    public boolean isVirtual();

    public boolean isEmbedded();

    public IdentifierValue getUnsavedValue();

    public IdentifierGenerator getIdentifierGenerator();

    public boolean isIdentifierAssignedByInsert();

    public boolean hasIdentifierMapper();
}

