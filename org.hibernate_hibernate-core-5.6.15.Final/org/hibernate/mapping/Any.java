/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.Map;
import java.util.Objects;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.type.MetaType;
import org.hibernate.type.Type;

public class Any
extends SimpleValue {
    private String identifierTypeName;
    private String metaTypeName = "string";
    private Map metaValues;
    private boolean lazy = true;

    @Deprecated
    public Any(MetadataImplementor metadata, Table table) {
        super(metadata, table);
    }

    public Any(MetadataBuildingContext buildingContext, Table table) {
        super(buildingContext, table);
    }

    public String getIdentifierType() {
        return this.identifierTypeName;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierTypeName = identifierType;
    }

    @Override
    public Type getType() throws MappingException {
        Type metaType = this.getMetadata().getTypeResolver().heuristicType(this.metaTypeName);
        return this.getMetadata().getTypeResolver().getTypeFactory().any(this.metaValues == null ? metaType : new MetaType(this.metaValues, metaType), this.getMetadata().getTypeResolver().heuristicType(this.identifierTypeName), this.isLazy());
    }

    public void setTypeByReflection(String propertyClass, String propertyName) {
    }

    public String getMetaType() {
        return this.metaTypeName;
    }

    public void setMetaType(String type) {
        this.metaTypeName = type;
    }

    public Map getMetaValues() {
        return this.metaValues;
    }

    public void setMetaValues(Map metaValues) {
        this.metaValues = metaValues;
    }

    public boolean isLazy() {
        return this.lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    @Override
    public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }

    @Override
    public boolean isSame(SimpleValue other) {
        return other instanceof Any && this.isSame((Any)other);
    }

    public boolean isSame(Any other) {
        return super.isSame(other) && Objects.equals(this.identifierTypeName, other.identifierTypeName) && Objects.equals(this.metaTypeName, other.metaTypeName) && Objects.equals(this.metaValues, other.metaValues) && this.lazy == other.lazy;
    }
}

