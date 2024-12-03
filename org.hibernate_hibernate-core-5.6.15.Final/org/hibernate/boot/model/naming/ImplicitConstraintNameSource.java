/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import java.util.List;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitNameSource;

public interface ImplicitConstraintNameSource
extends ImplicitNameSource {
    public Identifier getTableName();

    public List<Identifier> getColumnNames();

    public Identifier getUserProvidedIdentifier();
}

