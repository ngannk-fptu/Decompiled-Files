/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.ValueHolder;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class NaturalIdCacheKey
implements Serializable {
    private final Serializable[] naturalIdValues;
    private final String entityName;
    private final String tenantId;
    private final int hashCode;
    private transient ValueHolder<String> toString;

    public NaturalIdCacheKey(Object[] naturalIdValues, Type[] propertyTypes, int[] naturalIdPropertyIndexes, String entityName, SharedSessionContractImplementor session) {
        this.entityName = entityName;
        this.tenantId = session.getTenantIdentifier();
        this.naturalIdValues = new Serializable[naturalIdValues.length];
        SessionFactoryImplementor factory = session.getFactory();
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.entityName == null ? 0 : this.entityName.hashCode());
        result = 31 * result + (this.tenantId == null ? 0 : this.tenantId.hashCode());
        for (int i = 0; i < naturalIdValues.length; ++i) {
            int naturalIdPropertyIndex = naturalIdPropertyIndexes[i];
            Type type = propertyTypes[naturalIdPropertyIndex];
            Object value = naturalIdValues[i];
            result = 31 * result + (value != null ? type.getHashCode(value, factory) : 0);
            this.naturalIdValues[i] = type instanceof EntityType && type.getSemiResolvedType(factory).getReturnedClass().isInstance(value) ? (Serializable)value : type.disassemble(value, session, null);
        }
        this.hashCode = result;
        this.initTransients();
    }

    private void initTransients() {
        this.toString = new ValueHolder<1>(new ValueHolder.DeferredInitializer<String>(){

            @Override
            public String initialize() {
                StringBuilder toStringBuilder = new StringBuilder().append(NaturalIdCacheKey.this.entityName).append("##NaturalId[");
                for (int i = 0; i < NaturalIdCacheKey.this.naturalIdValues.length; ++i) {
                    toStringBuilder.append(NaturalIdCacheKey.this.naturalIdValues[i]);
                    if (i + 1 >= NaturalIdCacheKey.this.naturalIdValues.length) continue;
                    toStringBuilder.append(", ");
                }
                toStringBuilder.append("]");
                return toStringBuilder.toString();
            }
        });
    }

    public String getEntityName() {
        return this.entityName;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public Serializable[] getNaturalIdValues() {
        return this.naturalIdValues;
    }

    public String toString() {
        return this.toString.getValue();
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (this.hashCode != o.hashCode() || !(o instanceof NaturalIdCacheKey)) {
            return false;
        }
        NaturalIdCacheKey other = (NaturalIdCacheKey)o;
        return Objects.equals(this.entityName, other.entityName) && Objects.equals(this.tenantId, other.tenantId) && Arrays.deepEquals(this.naturalIdValues, other.naturalIdValues);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.initTransients();
    }
}

