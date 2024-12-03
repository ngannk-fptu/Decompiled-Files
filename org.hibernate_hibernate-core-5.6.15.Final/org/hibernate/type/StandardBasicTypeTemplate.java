/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class StandardBasicTypeTemplate<J>
extends AbstractSingleColumnStandardBasicType<J> {
    private final String name;
    private final String[] registrationKeys;

    public StandardBasicTypeTemplate(SqlTypeDescriptor sqlTypeDescriptor, JavaTypeDescriptor<J> javaTypeDescriptor, String ... registrationKeys) {
        super(sqlTypeDescriptor, javaTypeDescriptor);
        this.registrationKeys = registrationKeys;
        this.name = javaTypeDescriptor.getJavaType() == null ? "(map-mode)" : javaTypeDescriptor.getJavaType().getName() + " -> " + sqlTypeDescriptor.getSqlType();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String[] getRegistrationKeys() {
        return this.registrationKeys;
    }
}

