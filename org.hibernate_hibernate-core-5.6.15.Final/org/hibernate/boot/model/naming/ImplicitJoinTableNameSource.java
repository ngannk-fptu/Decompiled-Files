/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.ImplicitNameSource;
import org.hibernate.boot.model.source.spi.AttributePath;

public interface ImplicitJoinTableNameSource
extends ImplicitNameSource {
    public String getOwningPhysicalTableName();

    public EntityNaming getOwningEntityNaming();

    public String getNonOwningPhysicalTableName();

    public EntityNaming getNonOwningEntityNaming();

    public AttributePath getAssociationOwningAttributePath();
}

