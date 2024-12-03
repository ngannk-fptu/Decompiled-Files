/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import java.util.List;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitConstraintNameSource;

public interface ImplicitForeignKeyNameSource
extends ImplicitConstraintNameSource {
    public Identifier getReferencedTableName();

    public List<Identifier> getReferencedColumnNames();
}

