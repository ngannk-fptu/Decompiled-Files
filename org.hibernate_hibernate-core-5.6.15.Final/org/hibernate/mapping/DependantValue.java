/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.type.Type;

public class DependantValue
extends SimpleValue {
    private KeyValue wrappedValue;
    private boolean nullable;
    private boolean updateable;

    @Deprecated
    public DependantValue(MetadataImplementor metadata, Table table, KeyValue prototype) {
        super(metadata, table);
        this.wrappedValue = prototype;
    }

    public DependantValue(MetadataBuildingContext buildingContext, Table table, KeyValue prototype) {
        super(buildingContext, table);
        this.wrappedValue = prototype;
    }

    @Override
    public Type getType() throws MappingException {
        return this.wrappedValue.getType();
    }

    @Override
    public void setTypeUsingReflection(String className, String propertyName) {
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }

    @Override
    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public boolean isUpdateable() {
        return this.updateable;
    }

    public void setUpdateable(boolean updateable) {
        this.updateable = updateable;
    }

    @Override
    public boolean isSame(SimpleValue other) {
        return other instanceof DependantValue && this.isSame((DependantValue)other);
    }

    public boolean isSame(DependantValue other) {
        return super.isSame(other) && DependantValue.isSame(this.wrappedValue, other.wrappedValue);
    }
}

