/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi.sql;

import java.util.Map;
import org.hibernate.LockMode;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryNonScalarReturn;

public class NativeSQLQueryRootReturn
extends NativeSQLQueryNonScalarReturn {
    private final String returnEntityName;
    private final int hashCode;

    public NativeSQLQueryRootReturn(String alias, String entityName, LockMode lockMode) {
        this(alias, entityName, null, lockMode);
    }

    public NativeSQLQueryRootReturn(String alias, String entityName, Map<String, String[]> propertyResults, LockMode lockMode) {
        super(alias, propertyResults, lockMode);
        this.returnEntityName = entityName;
        this.hashCode = this.determineHashCode();
    }

    private int determineHashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.returnEntityName != null ? this.returnEntityName.hashCode() : 0);
        return result;
    }

    public String getReturnEntityName() {
        return this.returnEntityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        NativeSQLQueryRootReturn that = (NativeSQLQueryRootReturn)o;
        return !(this.returnEntityName != null ? !this.returnEntityName.equals(that.returnEntityName) : that.returnEntityName != null);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}

