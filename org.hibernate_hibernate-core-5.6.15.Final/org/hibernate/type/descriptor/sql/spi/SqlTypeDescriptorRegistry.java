/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql.spi;

import java.io.Serializable;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.spi.TypeConfiguration;

public class SqlTypeDescriptorRegistry
extends org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry
implements Serializable {
    private final TypeConfiguration typeConfiguration;
    private final org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry sqlTypeDescriptorRegistry;

    public SqlTypeDescriptorRegistry(TypeConfiguration typeConfiguration) {
        this.typeConfiguration = typeConfiguration;
        this.sqlTypeDescriptorRegistry = org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry.INSTANCE;
    }

    @Override
    public void addDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
        this.sqlTypeDescriptorRegistry.addDescriptor(sqlTypeDescriptor);
    }

    @Override
    public SqlTypeDescriptor getDescriptor(int jdbcTypeCode) {
        return this.sqlTypeDescriptorRegistry.getDescriptor(jdbcTypeCode);
    }
}

