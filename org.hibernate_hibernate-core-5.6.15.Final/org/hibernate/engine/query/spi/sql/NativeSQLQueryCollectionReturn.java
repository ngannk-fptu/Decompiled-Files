/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi.sql;

import java.util.Map;
import org.hibernate.LockMode;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryNonScalarReturn;

public class NativeSQLQueryCollectionReturn
extends NativeSQLQueryNonScalarReturn {
    private final String ownerEntityName;
    private final String ownerProperty;
    private final int hashCode;

    public NativeSQLQueryCollectionReturn(String alias, String ownerEntityName, String ownerProperty, Map propertyResults, LockMode lockMode) {
        super(alias, propertyResults, lockMode);
        this.ownerEntityName = ownerEntityName;
        this.ownerProperty = ownerProperty;
        this.hashCode = this.determineHashCode();
    }

    private int determineHashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.ownerEntityName != null ? this.ownerEntityName.hashCode() : 0);
        result = 31 * result + (this.ownerProperty != null ? this.ownerProperty.hashCode() : 0);
        return result;
    }

    public String getOwnerEntityName() {
        return this.ownerEntityName;
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
        NativeSQLQueryCollectionReturn that = (NativeSQLQueryCollectionReturn)o;
        if (this.ownerEntityName != null ? !this.ownerEntityName.equals(that.ownerEntityName) : that.ownerEntityName != null) {
            return false;
        }
        return !(this.ownerProperty != null ? !this.ownerProperty.equals(that.ownerProperty) : that.ownerProperty != null);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}

