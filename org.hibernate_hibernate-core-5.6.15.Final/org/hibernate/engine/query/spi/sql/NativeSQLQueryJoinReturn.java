/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi.sql;

import java.util.Map;
import org.hibernate.LockMode;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryNonScalarReturn;

public class NativeSQLQueryJoinReturn
extends NativeSQLQueryNonScalarReturn {
    private final String ownerAlias;
    private final String ownerProperty;
    private final int hashCode;

    public NativeSQLQueryJoinReturn(String alias, String ownerAlias, String ownerProperty, Map propertyResults, LockMode lockMode) {
        super(alias, propertyResults, lockMode);
        this.ownerAlias = ownerAlias;
        this.ownerProperty = ownerProperty;
        this.hashCode = this.determineHashCode();
    }

    private int determineHashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.ownerAlias != null ? this.ownerAlias.hashCode() : 0);
        result = 31 * result + (this.ownerProperty != null ? this.ownerProperty.hashCode() : 0);
        return result;
    }

    public String getOwnerAlias() {
        return this.ownerAlias;
    }

    public String getOwnerProperty() {
        return this.ownerProperty;
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
        NativeSQLQueryJoinReturn that = (NativeSQLQueryJoinReturn)o;
        if (this.ownerAlias != null ? !this.ownerAlias.equals(that.ownerAlias) : that.ownerAlias != null) {
            return false;
        }
        return !(this.ownerProperty != null ? !this.ownerProperty.equals(that.ownerProperty) : that.ownerProperty != null);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}

