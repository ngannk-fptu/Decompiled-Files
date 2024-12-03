/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.type.descriptor.converter;

import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.converter.AttributeConverterMutabilityPlanImpl;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.java.MutabilityPlan;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.jboss.logging.Logger;

public class AttributeConverterTypeAdapter<T>
extends AbstractSingleColumnStandardBasicType<T> {
    private static final Logger log = Logger.getLogger(AttributeConverterTypeAdapter.class);
    public static final String NAME_PREFIX = "converted::";
    private final String name;
    private final String description;
    private final Class modelType;
    private final Class jdbcType;
    private final JpaAttributeConverter<? extends T, ?> attributeConverter;
    private final MutabilityPlan<T> mutabilityPlan;

    public AttributeConverterTypeAdapter(String name, String description, JpaAttributeConverter<? extends T, ?> attributeConverter, SqlTypeDescriptor sqlTypeDescriptorAdapter, Class modelType, Class jdbcType, JavaTypeDescriptor<T> entityAttributeJavaTypeDescriptor) {
        super(sqlTypeDescriptorAdapter, entityAttributeJavaTypeDescriptor);
        this.name = name;
        this.description = description;
        this.modelType = modelType;
        this.jdbcType = jdbcType;
        this.attributeConverter = attributeConverter;
        this.mutabilityPlan = entityAttributeJavaTypeDescriptor.getMutabilityPlan().isMutable() ? new AttributeConverterMutabilityPlanImpl(attributeConverter) : ImmutableMutabilityPlan.INSTANCE;
        log.debug((Object)("Created AttributeConverterTypeAdapter -> " + name));
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Class getModelType() {
        return this.modelType;
    }

    public Class getJdbcType() {
        return this.jdbcType;
    }

    public JpaAttributeConverter<? extends T, ?> getAttributeConverter() {
        return this.attributeConverter;
    }

    @Override
    protected MutabilityPlan<T> getMutabilityPlan() {
        return this.mutabilityPlan;
    }

    public String toString() {
        return this.description;
    }
}

