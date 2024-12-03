/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import org.hibernate.engine.spi.EntityKey;

public final class AssociationKey
implements Serializable {
    private EntityKey ownerKey;
    private String propertyName;

    public AssociationKey(EntityKey ownerKey, String propertyName) {
        this.ownerKey = ownerKey;
        this.propertyName = propertyName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AssociationKey that = (AssociationKey)o;
        return this.ownerKey.equals(that.ownerKey) && this.propertyName.equals(that.propertyName);
    }

    public int hashCode() {
        int result = this.ownerKey.hashCode();
        result = 31 * result + this.propertyName.hashCode();
        return result;
    }
}

