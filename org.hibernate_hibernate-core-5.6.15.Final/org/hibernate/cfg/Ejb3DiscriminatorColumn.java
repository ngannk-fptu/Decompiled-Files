/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.DiscriminatorColumn
 *  javax.persistence.DiscriminatorType
 */
package org.hibernate.cfg;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import org.hibernate.AssertionFailure;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.Ejb3Column;

public class Ejb3DiscriminatorColumn
extends Ejb3Column {
    public static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "DTYPE";
    public static final String DEFAULT_DISCRIMINATOR_TYPE = "string";
    private static final int DEFAULT_DISCRIMINATOR_LENGTH = 31;
    private String discriminatorTypeName;

    public Ejb3DiscriminatorColumn() {
        this.setLogicalColumnName(DEFAULT_DISCRIMINATOR_COLUMN_NAME);
        this.setNullable(false);
        this.setDiscriminatorTypeName(DEFAULT_DISCRIMINATOR_TYPE);
        this.setLength(31);
    }

    public String getDiscriminatorTypeName() {
        return this.discriminatorTypeName;
    }

    public void setDiscriminatorTypeName(String discriminatorTypeName) {
        this.discriminatorTypeName = discriminatorTypeName;
    }

    public static Ejb3DiscriminatorColumn buildDiscriminatorColumn(DiscriminatorType type, DiscriminatorColumn discAnn, DiscriminatorFormula discFormulaAnn, MetadataBuildingContext context) {
        Ejb3DiscriminatorColumn discriminatorColumn = new Ejb3DiscriminatorColumn();
        discriminatorColumn.setBuildingContext(context);
        discriminatorColumn.setImplicit(true);
        if (discFormulaAnn != null) {
            discriminatorColumn.setImplicit(false);
            discriminatorColumn.setFormula(discFormulaAnn.value());
        } else if (discAnn != null) {
            discriminatorColumn.setImplicit(false);
            if (!BinderHelper.isEmptyAnnotationValue(discAnn.columnDefinition())) {
                discriminatorColumn.setSqlType(discAnn.columnDefinition());
            }
            if (!BinderHelper.isEmptyAnnotationValue(discAnn.name())) {
                discriminatorColumn.setLogicalColumnName(discAnn.name());
            }
            discriminatorColumn.setNullable(false);
        }
        if (DiscriminatorType.CHAR.equals((Object)type)) {
            discriminatorColumn.setDiscriminatorTypeName("character");
            discriminatorColumn.setImplicit(false);
        } else if (DiscriminatorType.INTEGER.equals((Object)type)) {
            discriminatorColumn.setDiscriminatorTypeName("integer");
            discriminatorColumn.setImplicit(false);
        } else if (DiscriminatorType.STRING.equals((Object)type) || type == null) {
            if (discAnn != null) {
                discriminatorColumn.setLength(discAnn.length());
            }
            discriminatorColumn.setDiscriminatorTypeName(DEFAULT_DISCRIMINATOR_TYPE);
        } else {
            throw new AssertionFailure("Unknown discriminator type: " + type);
        }
        discriminatorColumn.bind();
        return discriminatorColumn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ejb3DiscriminatorColumn");
        sb.append("{logicalColumnName'").append(this.getLogicalColumnName()).append('\'');
        sb.append(", discriminatorTypeName='").append(this.discriminatorTypeName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

