/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.Iterator;
import java.util.Objects;
import org.hibernate.FetchMode;
import org.hibernate.MappingException;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class OneToMany
implements Value {
    private final MetadataImplementor metadata;
    private final Table referencingTable;
    private String referencedEntityName;
    private PersistentClass associatedClass;
    private NotFoundAction notFoundAction;

    @Deprecated
    public OneToMany(MetadataImplementor metadata, PersistentClass owner) throws MappingException {
        this.metadata = metadata;
        this.referencingTable = owner == null ? null : owner.getTable();
    }

    public OneToMany(MetadataBuildingContext buildingContext, PersistentClass owner) throws MappingException {
        this.metadata = buildingContext.getMetadataCollector();
        this.referencingTable = owner == null ? null : owner.getTable();
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return this.metadata.getMetadataBuildingOptions().getServiceRegistry();
    }

    private EntityType getEntityType() {
        return this.metadata.getTypeResolver().getTypeFactory().manyToOne(this.getReferencedEntityName(), true, null, false, false, this.notFoundAction, false);
    }

    public PersistentClass getAssociatedClass() {
        return this.associatedClass;
    }

    public void setAssociatedClass(PersistentClass associatedClass) {
        this.associatedClass = associatedClass;
    }

    @Override
    public void createForeignKey() {
    }

    @Override
    public Iterator<Selectable> getColumnIterator() {
        return this.associatedClass.getKey().getColumnIterator();
    }

    @Override
    public int getColumnSpan() {
        return this.associatedClass.getKey().getColumnSpan();
    }

    @Override
    public FetchMode getFetchMode() {
        return FetchMode.JOIN;
    }

    @Override
    public Table getTable() {
        return this.referencingTable;
    }

    @Override
    public Type getType() {
        return this.getEntityType();
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public boolean isSimpleValue() {
        return false;
    }

    @Override
    public boolean isAlternateUniqueKey() {
        return false;
    }

    @Override
    public boolean hasFormula() {
        return false;
    }

    @Override
    public boolean isValid(Mapping mapping) throws MappingException {
        if (this.referencedEntityName == null) {
            throw new MappingException("one to many association must specify the referenced entity");
        }
        return true;
    }

    public String getReferencedEntityName() {
        return this.referencedEntityName;
    }

    public void setReferencedEntityName(String referencedEntityName) {
        this.referencedEntityName = referencedEntityName == null ? null : referencedEntityName.intern();
    }

    @Override
    public void setTypeUsingReflection(String className, String propertyName) {
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }

    @Override
    public boolean isSame(Value other) {
        return this == other || other instanceof OneToMany && this.isSame((OneToMany)other);
    }

    public boolean isSame(OneToMany other) {
        return Objects.equals(this.referencingTable, other.referencingTable) && Objects.equals(this.referencedEntityName, other.referencedEntityName) && Objects.equals(this.associatedClass, other.associatedClass);
    }

    @Override
    public boolean[] getColumnInsertability() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean[] getColumnUpdateability() {
        throw new UnsupportedOperationException();
    }

    public NotFoundAction getNotFoundAction() {
        return this.notFoundAction;
    }

    public void setNotFoundAction(NotFoundAction notFoundAction) {
        this.notFoundAction = notFoundAction;
    }

    public boolean isIgnoreNotFound() {
        return this.notFoundAction == NotFoundAction.IGNORE;
    }

    public void setIgnoreNotFound(boolean ignoreNotFound) {
        this.notFoundAction = ignoreNotFound ? NotFoundAction.IGNORE : null;
    }
}

