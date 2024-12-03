/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.io.Serializable;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.RelationalModel;

@Deprecated
public interface AuxiliaryDatabaseObject
extends RelationalModel,
Serializable {
    public void addDialectScope(String var1);

    public boolean appliesToDialect(Dialect var1);
}

