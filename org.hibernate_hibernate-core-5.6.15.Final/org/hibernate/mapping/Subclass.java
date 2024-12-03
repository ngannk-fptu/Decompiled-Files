/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.AssertionFailure;
import org.hibernate.EntityMode;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.internal.util.collections.JoinedIterator;
import org.hibernate.internal.util.collections.SingletonIterator;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PersistentClassVisitor;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;

public class Subclass
extends PersistentClass {
    private PersistentClass superclass;
    private Class classPersisterClass;
    private final int subclassId;

    public Subclass(PersistentClass superclass, MetadataBuildingContext metadataBuildingContext) {
        super(metadataBuildingContext);
        this.superclass = superclass;
        this.subclassId = superclass.nextSubclassId();
    }

    @Override
    int nextSubclassId() {
        return this.getSuperclass().nextSubclassId();
    }

    @Override
    public int getSubclassId() {
        return this.subclassId;
    }

    @Override
    public String getNaturalIdCacheRegionName() {
        return this.getSuperclass().getNaturalIdCacheRegionName();
    }

    @Override
    public String getCacheConcurrencyStrategy() {
        return this.getRootClass().getCacheConcurrencyStrategy();
    }

    @Override
    public RootClass getRootClass() {
        return this.getSuperclass().getRootClass();
    }

    @Override
    public PersistentClass getSuperclass() {
        return this.superclass;
    }

    @Override
    public Property getIdentifierProperty() {
        return this.getSuperclass().getIdentifierProperty();
    }

    @Override
    public Property getDeclaredIdentifierProperty() {
        return null;
    }

    @Override
    public KeyValue getIdentifier() {
        return this.getSuperclass().getIdentifier();
    }

    @Override
    public boolean hasIdentifierProperty() {
        return this.getSuperclass().hasIdentifierProperty();
    }

    @Override
    public Value getDiscriminator() {
        return this.getSuperclass().getDiscriminator();
    }

    @Override
    public boolean isMutable() {
        return this.getSuperclass().isMutable();
    }

    @Override
    public boolean isInherited() {
        return true;
    }

    @Override
    public boolean isPolymorphic() {
        return true;
    }

    @Override
    public void addProperty(Property p) {
        super.addProperty(p);
        this.getSuperclass().addSubclassProperty(p);
    }

    @Override
    public void addMappedsuperclassProperty(Property p) {
        super.addMappedsuperclassProperty(p);
        this.getSuperclass().addSubclassProperty(p);
    }

    @Override
    public void addJoin(Join j) {
        super.addJoin(j);
        this.getSuperclass().addSubclassJoin(j);
    }

    @Override
    public Iterator getPropertyClosureIterator() {
        return new JoinedIterator(this.getSuperclass().getPropertyClosureIterator(), this.getPropertyIterator());
    }

    @Override
    public Iterator getTableClosureIterator() {
        return new JoinedIterator(this.getSuperclass().getTableClosureIterator(), new SingletonIterator<Table>(this.getTable()));
    }

    @Override
    public Iterator getKeyClosureIterator() {
        return new JoinedIterator(this.getSuperclass().getKeyClosureIterator(), new SingletonIterator<KeyValue>(this.getKey()));
    }

    @Override
    protected void addSubclassProperty(Property p) {
        super.addSubclassProperty(p);
        this.getSuperclass().addSubclassProperty(p);
    }

    @Override
    protected void addSubclassJoin(Join j) {
        super.addSubclassJoin(j);
        this.getSuperclass().addSubclassJoin(j);
    }

    @Override
    protected void addSubclassTable(Table table) {
        super.addSubclassTable(table);
        this.getSuperclass().addSubclassTable(table);
    }

    @Override
    public boolean isVersioned() {
        return this.getSuperclass().isVersioned();
    }

    @Override
    public Property getVersion() {
        return this.getSuperclass().getVersion();
    }

    @Override
    public Property getDeclaredVersion() {
        return null;
    }

    @Override
    public boolean hasEmbeddedIdentifier() {
        return this.getSuperclass().hasEmbeddedIdentifier();
    }

    @Override
    public Class getEntityPersisterClass() {
        if (this.classPersisterClass == null) {
            return this.getSuperclass().getEntityPersisterClass();
        }
        return this.classPersisterClass;
    }

    @Override
    public Table getRootTable() {
        return this.getSuperclass().getRootTable();
    }

    @Override
    public KeyValue getKey() {
        return this.getSuperclass().getIdentifier();
    }

    @Override
    public boolean isExplicitPolymorphism() {
        return this.getSuperclass().isExplicitPolymorphism();
    }

    public void setSuperclass(PersistentClass superclass) {
        this.superclass = superclass;
    }

    @Override
    public String getWhere() {
        return this.getSuperclass().getWhere();
    }

    @Override
    public boolean isJoinedSubclass() {
        return this.getTable() != this.getRootTable();
    }

    public void createForeignKey() {
        if (!this.isJoinedSubclass()) {
            throw new AssertionFailure("not a joined-subclass");
        }
        this.getKey().createForeignKeyOfEntity(this.getSuperclass().getEntityName());
    }

    @Override
    public void setEntityPersisterClass(Class classPersisterClass) {
        this.classPersisterClass = classPersisterClass;
    }

    @Override
    public int getJoinClosureSpan() {
        return this.getSuperclass().getJoinClosureSpan() + super.getJoinClosureSpan();
    }

    @Override
    public int getPropertyClosureSpan() {
        return this.getSuperclass().getPropertyClosureSpan() + super.getPropertyClosureSpan();
    }

    @Override
    public Iterator getJoinClosureIterator() {
        return new JoinedIterator(this.getSuperclass().getJoinClosureIterator(), super.getJoinClosureIterator());
    }

    @Override
    public boolean isClassOrSuperclassJoin(Join join) {
        return super.isClassOrSuperclassJoin(join) || this.getSuperclass().isClassOrSuperclassJoin(join);
    }

    @Override
    public boolean isClassOrSuperclassTable(Table table) {
        return super.isClassOrSuperclassTable(table) || this.getSuperclass().isClassOrSuperclassTable(table);
    }

    @Override
    public Table getTable() {
        return this.getSuperclass().getTable();
    }

    @Override
    public boolean isForceDiscriminator() {
        return this.getSuperclass().isForceDiscriminator();
    }

    @Override
    public boolean isDiscriminatorInsertable() {
        return this.getSuperclass().isDiscriminatorInsertable();
    }

    @Override
    public Set getSynchronizedTables() {
        HashSet result = new HashSet();
        result.addAll(this.synchronizedTables);
        result.addAll(this.getSuperclass().getSynchronizedTables());
        return result;
    }

    @Override
    public Object accept(PersistentClassVisitor mv) {
        return mv.accept(this);
    }

    @Override
    public List getFilters() {
        ArrayList filters = new ArrayList(super.getFilters());
        filters.addAll(this.getSuperclass().getFilters());
        return filters;
    }

    @Override
    public boolean hasSubselectLoadableCollections() {
        return super.hasSubselectLoadableCollections() || this.getSuperclass().hasSubselectLoadableCollections();
    }

    @Override
    public String getTuplizerImplClassName(EntityMode mode) {
        String impl = super.getTuplizerImplClassName(mode);
        if (impl == null) {
            impl = this.getSuperclass().getTuplizerImplClassName(mode);
        }
        return impl;
    }

    @Override
    public Map getTuplizerMap() {
        Map specificTuplizerDefs = super.getTuplizerMap();
        Map superclassTuplizerDefs = this.getSuperclass().getTuplizerMap();
        if (specificTuplizerDefs == null && superclassTuplizerDefs == null) {
            return null;
        }
        HashMap combined = new HashMap();
        if (superclassTuplizerDefs != null) {
            combined.putAll(superclassTuplizerDefs);
        }
        if (specificTuplizerDefs != null) {
            combined.putAll(specificTuplizerDefs);
        }
        return Collections.unmodifiableMap(combined);
    }

    @Override
    public Component getIdentifierMapper() {
        return this.superclass.getIdentifierMapper();
    }

    @Override
    public OptimisticLockStyle getOptimisticLockStyle() {
        return this.superclass.getOptimisticLockStyle();
    }
}

