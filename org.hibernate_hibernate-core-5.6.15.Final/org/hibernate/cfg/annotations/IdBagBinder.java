/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.cfg.annotations;

import java.util.Collections;
import java.util.Map;
import javax.persistence.Column;
import org.hibernate.AnnotationException;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.IdGeneratorResolverSecondPass;
import org.hibernate.cfg.PropertyInferredData;
import org.hibernate.cfg.WrappedInferredData;
import org.hibernate.cfg.annotations.BagBinder;
import org.hibernate.cfg.annotations.Nullability;
import org.hibernate.cfg.annotations.SimpleValueBinder;
import org.hibernate.cfg.annotations.TableBinder;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.IdentifierCollection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;

public class IdBagBinder
extends BagBinder {
    @Override
    protected Collection createCollection(PersistentClass persistentClass) {
        return new IdentifierBag(this.getBuildingContext(), persistentClass);
    }

    protected boolean bindStarToManySecondPass(Map persistentClasses, XClass collType, Ejb3JoinColumn[] fkJoinColumns, Ejb3JoinColumn[] keyColumns, Ejb3JoinColumn[] inverseColumns, Ejb3Column[] elementColumns, boolean isEmbedded, XProperty property, boolean unique, TableBinder associationTableBinder, NotFoundAction notFoundAction, MetadataBuildingContext buildingContext) {
        boolean result = super.bindStarToManySecondPass(persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns, isEmbedded, property, unique, associationTableBinder, notFoundAction, this.getBuildingContext());
        CollectionId collectionIdAnn = (CollectionId)property.getAnnotation(CollectionId.class);
        if (collectionIdAnn != null) {
            String generatorType;
            Ejb3Column[] idColumns;
            SimpleValueBinder simpleValue = new SimpleValueBinder();
            WrappedInferredData propertyData = new WrappedInferredData(new PropertyInferredData(null, property, null, buildingContext.getBootstrapContext().getReflectionManager()), "id");
            for (Ejb3Column idColumn : idColumns = Ejb3Column.buildColumnFromAnnotation(this.determineColumns(collectionIdAnn), null, null, Nullability.FORCED_NOT_NULL, this.propertyHolder, propertyData, Collections.EMPTY_MAP, buildingContext)) {
                idColumn.setNullable(false);
            }
            Table table = this.collection.getCollectionTable();
            simpleValue.setTable(table);
            simpleValue.setColumns(idColumns);
            Type typeAnn = collectionIdAnn.type();
            if (typeAnn == null || BinderHelper.isEmptyAnnotationValue(typeAnn.type())) {
                throw new AnnotationException("@CollectionId is missing type: " + StringHelper.qualify(this.propertyHolder.getPath(), this.propertyName));
            }
            simpleValue.setExplicitType(typeAnn);
            simpleValue.setBuildingContext(this.getBuildingContext());
            SimpleValue id = simpleValue.make();
            ((IdentifierCollection)this.collection).setIdentifier(id);
            String generator = collectionIdAnn.generator();
            if ("identity".equals(generator) || "assigned".equals(generator) || "sequence".equals(generator) || "native".equals(generator)) {
                generatorType = generator;
                generator = "";
            } else {
                generatorType = null;
            }
            if (buildingContext.getBootstrapContext().getJpaCompliance().isGlobalGeneratorScopeEnabled()) {
                IdGeneratorResolverSecondPass secondPass = new IdGeneratorResolverSecondPass(id, property, generatorType, generator, this.getBuildingContext());
                buildingContext.getMetadataCollector().addSecondPass(secondPass);
            } else {
                BinderHelper.makeIdGenerator(id, property, generatorType, generator, this.getBuildingContext(), this.localGenerators);
            }
        }
        return result;
    }

    private Column[] determineColumns(CollectionId collectionIdAnn) {
        if (collectionIdAnn.columns().length > 0) {
            return collectionIdAnn.columns();
        }
        Column column = collectionIdAnn.column();
        if (StringHelper.isNotEmpty(column.name())) {
            return new Column[]{column};
        }
        return new Column[0];
    }
}

