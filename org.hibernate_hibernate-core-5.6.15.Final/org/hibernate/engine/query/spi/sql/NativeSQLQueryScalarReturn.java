/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi.sql;

import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.type.Type;

public class NativeSQLQueryScalarReturn
implements NativeSQLQueryReturn {
    private final Type type;
    private final String columnAlias;
    private final int hashCode;

    public NativeSQLQueryScalarReturn(String alias, Type type) {
        this.type = type;
        this.columnAlias = alias;
        this.hashCode = this.determineHashCode();
    }

    private int determineHashCode() {
        int result = this.type != null ? this.type.hashCode() : 0;
        result = 31 * result + this.getClass().getName().hashCode();
        result = 31 * result + (this.columnAlias != null ? this.columnAlias.hashCode() : 0);
        return result;
    }

    public String getColumnAlias() {
        return this.columnAlias;
    }

    public Type getType() {
        return this.type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NativeSQLQueryScalarReturn that = (NativeSQLQueryScalarReturn)o;
        if (this.columnAlias != null ? !this.columnAlias.equals(that.columnAlias) : that.columnAlias != null) {
            return false;
        }
        return !(this.type != null ? !this.type.equals(that.type) : that.type != null);
    }

    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public void traceLog(NativeSQLQueryReturn.TraceLogger logger2) {
        logger2.writeLine("Scalar[");
        logger2.writeLine("    columnAlias=" + this.columnAlias + ",");
        logger2.writeLine("    type=" + (this.type == null ? "<unknown>" : this.type.getName()) + ",");
        logger2.writeLine("]");
    }
}

