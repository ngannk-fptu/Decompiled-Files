/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

import java.io.Serializable;
import org.hibernate.boot.model.relational.Exportable;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;

public interface AuxiliaryDatabaseObject
extends Exportable,
Serializable {
    public boolean appliesToDialect(Dialect var1);

    public boolean beforeTablesOnCreation();

    default public String[] sqlCreateStrings(SqlStringGenerationContext context) {
        return this.sqlCreateStrings(context.getDialect());
    }

    @Deprecated
    default public String[] sqlCreateStrings(Dialect dialect) {
        throw new IllegalStateException(this + " does not implement sqlCreateStrings(...)");
    }

    default public String[] sqlDropStrings(SqlStringGenerationContext context) {
        return this.sqlDropStrings(context.getDialect());
    }

    @Deprecated
    default public String[] sqlDropStrings(Dialect dialect) {
        throw new IllegalStateException(this + " does not implement sqlDropStrings(...)");
    }

    public static interface Expandable {
        public void addDialectScope(String var1);
    }
}

