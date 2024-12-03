/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.OrderColumn
 */
package org.hibernate.cfg;

import java.util.Map;
import javax.persistence.OrderColumn;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.PropertyData;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.mapping.Join;

public class IndexColumn
extends Ejb3Column {
    private int base;

    public IndexColumn(boolean isImplicit, String sqlType, int length, int precision, int scale, String name, boolean nullable, boolean unique, boolean insertable, boolean updatable, String secondaryTableName, Map<String, Join> joins, PropertyHolder propertyHolder, MetadataBuildingContext buildingContext) {
        this.setImplicit(isImplicit);
        this.setSqlType(sqlType);
        this.setLength(length);
        this.setPrecision(precision);
        this.setScale(scale);
        this.setLogicalColumnName(name);
        this.setNullable(nullable);
        this.setUnique(unique);
        this.setInsertable(insertable);
        this.setUpdatable(updatable);
        this.setExplicitTableName(secondaryTableName);
        this.setPropertyHolder(propertyHolder);
        this.setJoins(joins);
        this.setBuildingContext(buildingContext);
        this.bind();
    }

    public int getBase() {
        return this.base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public static IndexColumn buildColumnFromAnnotation(OrderColumn ann, PropertyHolder propertyHolder, PropertyData inferredData, Map<String, Join> secondaryTables, MetadataBuildingContext buildingContext) {
        IndexColumn column;
        if (ann != null) {
            String sqlType = BinderHelper.isEmptyAnnotationValue(ann.columnDefinition()) ? null : ann.columnDefinition();
            String name = BinderHelper.isEmptyAnnotationValue(ann.name()) ? inferredData.getPropertyName() + "_ORDER" : ann.name();
            column = new IndexColumn(false, sqlType, 0, 0, 0, name, ann.nullable(), false, ann.insertable(), ann.updatable(), null, secondaryTables, propertyHolder, buildingContext);
        } else {
            column = new IndexColumn(true, null, 0, 0, 0, null, true, false, true, true, null, null, propertyHolder, buildingContext);
        }
        return column;
    }

    public static IndexColumn buildColumnFromAnnotation(org.hibernate.annotations.IndexColumn ann, PropertyHolder propertyHolder, PropertyData inferredData, MetadataBuildingContext buildingContext) {
        IndexColumn column;
        if (ann != null) {
            String sqlType = BinderHelper.isEmptyAnnotationValue(ann.columnDefinition()) ? null : ann.columnDefinition();
            String name = BinderHelper.isEmptyAnnotationValue(ann.name()) ? inferredData.getPropertyName() : ann.name();
            column = new IndexColumn(false, sqlType, 0, 0, 0, name, ann.nullable(), false, true, true, null, null, propertyHolder, buildingContext);
            column.setBase(ann.base());
        } else {
            column = new IndexColumn(true, null, 0, 0, 0, null, true, false, true, true, null, null, propertyHolder, buildingContext);
        }
        return column;
    }
}

