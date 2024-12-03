/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitNameSource;

public interface ImplicitPrimaryKeyJoinColumnNameSource
extends ImplicitNameSource {
    public Identifier getReferencedTableName();

    public Identifier getReferencedPrimaryKeyColumnName();
}

