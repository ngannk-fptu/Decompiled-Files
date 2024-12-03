/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.spi.JdbcRecommendedSqlTypeMappingContext;
import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public interface BasicJavaDescriptor<T>
extends JavaTypeDescriptor<T> {
    default public SqlTypeDescriptor getJdbcRecommendedSqlType(JdbcRecommendedSqlTypeMappingContext context) {
        return context.getTypeConfiguration().getSqlTypeDescriptorRegistry().getDescriptor(JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass(this.getJavaType()));
    }
}

