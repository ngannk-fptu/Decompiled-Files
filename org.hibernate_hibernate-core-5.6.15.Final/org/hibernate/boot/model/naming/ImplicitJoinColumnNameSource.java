/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitNameSource;
import org.hibernate.boot.model.source.spi.AttributePath;

public interface ImplicitJoinColumnNameSource
extends ImplicitNameSource {
    public Nature getNature();

    public EntityNaming getEntityNaming();

    public AttributePath getAttributePath();

    public Identifier getReferencedTableName();

    public Identifier getReferencedColumnName();

    public static enum Nature {
        ELEMENT_COLLECTION,
        ENTITY_COLLECTION,
        ENTITY;

    }
}

