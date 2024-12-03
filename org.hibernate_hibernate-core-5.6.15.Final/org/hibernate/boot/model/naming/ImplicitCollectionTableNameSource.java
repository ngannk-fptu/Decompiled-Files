/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitNameSource;
import org.hibernate.boot.model.source.spi.AttributePath;

public interface ImplicitCollectionTableNameSource
extends ImplicitNameSource {
    public Identifier getOwningPhysicalTableName();

    public EntityNaming getOwningEntityNaming();

    public AttributePath getOwningAttributePath();
}

