/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import java.io.Serializable;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.java.BasicJavaDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
import org.hibernate.type.spi.TypeConfiguration;

public interface SqlTypeDescriptor
extends Serializable {
    public int getSqlType();

    public boolean canBeRemapped();

    default public <T> BasicJavaDescriptor<T> getJdbcRecommendedJavaTypeMapping(TypeConfiguration typeConfiguration) {
        return (BasicJavaDescriptor)typeConfiguration.getJavaTypeDescriptorRegistry().getDescriptor(JdbcTypeJavaClassMappings.INSTANCE.determineJavaClassForJdbcTypeCode(this.getSqlType()));
    }

    public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> var1);

    public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> var1);
}

