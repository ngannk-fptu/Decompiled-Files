/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg.annotations;

import java.util.Map;
import org.hibernate.AnnotationException;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.CollectionPropertyHolder;
import org.hibernate.cfg.CollectionSecondPass;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.PropertyHolderBuilder;
import org.hibernate.cfg.SecondPass;
import org.hibernate.cfg.annotations.CollectionBinder;
import org.hibernate.cfg.annotations.SimpleValueBinder;
import org.hibernate.cfg.annotations.TableBinder;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.IndexBackref;
import org.hibernate.mapping.List;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.SimpleValue;
import org.jboss.logging.Logger;

public class ListBinder
extends CollectionBinder {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)ListBinder.class.getName());

    public ListBinder() {
        super(false);
    }

    @Override
    protected Collection createCollection(PersistentClass persistentClass) {
        return new List(this.getBuildingContext(), persistentClass);
    }

    @Override
    public void setSqlOrderBy(OrderBy orderByAnn) {
        if (orderByAnn != null) {
            LOG.orderByAnnotationIndexedCollection();
        }
    }

    @Override
    public void setSort(Sort sortAnn) {
        if (sortAnn != null) {
            LOG.sortAnnotationIndexedCollection();
        }
    }

    @Override
    public SecondPass getSecondPass(final Ejb3JoinColumn[] fkJoinColumns, final Ejb3JoinColumn[] keyColumns, final Ejb3JoinColumn[] inverseColumns, final Ejb3Column[] elementColumns, Ejb3Column[] mapKeyColumns, Ejb3JoinColumn[] mapKeyManyToManyColumns, final boolean isEmbedded, final XProperty property, final XClass collType, final NotFoundAction notFoundAction, final boolean unique, final TableBinder assocTableBinder, final MetadataBuildingContext buildingContext) {
        return new CollectionSecondPass(this.getBuildingContext(), this.collection){

            @Override
            public void secondPass(Map persistentClasses, Map inheritedMetas) {
                ListBinder.this.bindStarToManySecondPass(persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns, isEmbedded, property, unique, assocTableBinder, notFoundAction, buildingContext);
                ListBinder.this.bindIndex(buildingContext);
            }
        };
    }

    private void bindIndex(MetadataBuildingContext buildingContext) {
        if (!this.indexColumn.isImplicit()) {
            CollectionPropertyHolder valueHolder = PropertyHolderBuilder.buildPropertyHolder(this.collection, StringHelper.qualify(this.collection.getRole(), "key"), null, null, this.propertyHolder, this.getBuildingContext());
            List list = (List)this.collection;
            if (!list.isOneToMany()) {
                this.indexColumn.forceNotNull();
            }
            this.indexColumn.setPropertyHolder(valueHolder);
            SimpleValueBinder value = new SimpleValueBinder();
            value.setColumns(new Ejb3Column[]{this.indexColumn});
            value.setExplicitType("integer");
            value.setBuildingContext(this.getBuildingContext());
            SimpleValue indexValue = value.make();
            this.indexColumn.linkWithValue(indexValue);
            list.setIndex(indexValue);
            list.setBaseIndex(this.indexColumn.getBase());
            if (list.isOneToMany() && !list.getKey().isNullable() && !list.isInverse()) {
                String entityName = ((OneToMany)list.getElement()).getReferencedEntityName();
                PersistentClass referenced = buildingContext.getMetadataCollector().getEntityBinding(entityName);
                IndexBackref ib = new IndexBackref();
                ib.setName('_' + this.propertyName + "IndexBackref");
                ib.setUpdateable(false);
                ib.setSelectable(false);
                ib.setCollectionRole(list.getRole());
                ib.setEntityName(list.getOwner().getEntityName());
                ib.setValue(list.getIndex());
                referenced.addProperty(ib);
            }
        } else {
            Collection coll = this.collection;
            throw new AnnotationException("List/array has to be annotated with an @OrderColumn (or @IndexColumn): " + coll.getRole());
        }
    }
}

