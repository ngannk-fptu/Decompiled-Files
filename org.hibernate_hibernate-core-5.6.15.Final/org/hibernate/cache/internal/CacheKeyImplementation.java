/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.internal;

import java.io.Serializable;
import java.util.Objects;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

final class CacheKeyImplementation
implements Serializable {
    private final Object id;
    private final Type type;
    private final String entityOrRoleName;
    private final String tenantId;
    private final int hashCode;

    CacheKeyImplementation(Object id, Type type, String entityOrRoleName, String tenantId, SessionFactoryImplementor factory) {
        this.id = id;
        this.type = type;
        this.entityOrRoleName = entityOrRoleName;
        this.tenantId = tenantId;
        this.hashCode = this.calculateHashCode(type, factory);
    }

    private int calculateHashCode(Type type, SessionFactoryImplementor factory) {
        int result = type.getHashCode(this.id, factory);
        result = 31 * result + (this.tenantId != null ? this.tenantId.hashCode() : 0);
        return result;
    }

    public Object getId() {
        return this.id;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (this.hashCode != other.hashCode() || !(other instanceof CacheKeyImplementation)) {
            return false;
        }
        CacheKeyImplementation that = (CacheKeyImplementation)other;
        return Objects.equals(this.entityOrRoleName, that.entityOrRoleName) && this.type.isEqual(this.id, that.id) && Objects.equals(this.tenantId, that.tenantId);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        return this.entityOrRoleName + '#' + this.id.toString();
    }
}

