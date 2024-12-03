/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.Objects;
import org.hibernate.FetchMode;
import org.hibernate.MappingException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.mapping.Fetchable;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.type.Type;

public abstract class ToOne
extends SimpleValue
implements Fetchable {
    private FetchMode fetchMode;
    protected String referencedPropertyName;
    private String referencedEntityName;
    private String propertyName;
    private boolean embedded;
    private boolean lazy = true;
    protected boolean unwrapProxy;
    protected boolean isUnwrapProxyImplicit;
    protected boolean referenceToPrimaryKey = true;

    @Deprecated
    protected ToOne(MetadataImplementor metadata, Table table) {
        super(metadata, table);
    }

    protected ToOne(MetadataBuildingContext buildingContext, Table table) {
        super(buildingContext, table);
    }

    @Override
    public FetchMode getFetchMode() {
        return this.fetchMode;
    }

    @Override
    public void setFetchMode(FetchMode fetchMode) {
        this.fetchMode = fetchMode;
    }

    @Override
    public abstract void createForeignKey() throws MappingException;

    @Override
    public abstract Type getType() throws MappingException;

    public String getReferencedPropertyName() {
        return this.referencedPropertyName;
    }

    public void setReferencedPropertyName(String name) {
        this.referencedPropertyName = name == null ? null : name.intern();
    }

    public String getReferencedEntityName() {
        return this.referencedEntityName;
    }

    public void setReferencedEntityName(String referencedEntityName) {
        this.referencedEntityName = referencedEntityName == null ? null : referencedEntityName.intern();
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName == null ? null : propertyName.intern();
    }

    @Override
    public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
        if (this.referencedEntityName == null) {
            ClassLoaderService cls = this.getMetadata().getMetadataBuildingOptions().getServiceRegistry().getService(ClassLoaderService.class);
            this.referencedEntityName = ReflectHelper.reflectedPropertyClass(className, propertyName, cls).getName();
        }
    }

    @Override
    public boolean isTypeSpecified() {
        return this.referencedEntityName != null;
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }

    @Override
    public boolean isSame(SimpleValue other) {
        return other instanceof ToOne && this.isSame((ToOne)other);
    }

    public boolean isSame(ToOne other) {
        return super.isSame(other) && Objects.equals(this.referencedPropertyName, other.referencedPropertyName) && Objects.equals(this.referencedEntityName, other.referencedEntityName) && this.embedded == other.embedded;
    }

    @Override
    public boolean isValid(Mapping mapping) throws MappingException {
        if (this.referencedEntityName == null) {
            throw new MappingException("association must specify the referenced entity");
        }
        return super.isValid(mapping);
    }

    @Override
    public boolean isLazy() {
        return this.lazy;
    }

    @Override
    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public boolean isUnwrapProxy() {
        return this.unwrapProxy;
    }

    public void setUnwrapProxy(boolean unwrapProxy) {
        this.unwrapProxy = unwrapProxy;
    }

    public boolean isUnwrapProxyImplicit() {
        return this.isUnwrapProxyImplicit;
    }

    public void setUnwrapProxyImplicit(boolean unwrapProxyImplicit) {
        this.isUnwrapProxyImplicit = unwrapProxyImplicit;
    }

    public boolean isReferenceToPrimaryKey() {
        return this.referenceToPrimaryKey;
    }

    public void setReferenceToPrimaryKey(boolean referenceToPrimaryKey) {
        this.referenceToPrimaryKey = referenceToPrimaryKey;
    }
}

