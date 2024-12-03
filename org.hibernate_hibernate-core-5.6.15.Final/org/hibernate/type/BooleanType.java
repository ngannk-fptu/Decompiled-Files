/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.descriptor.java.BooleanTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class BooleanType
extends AbstractSingleColumnStandardBasicType<Boolean>
implements PrimitiveType<Boolean>,
DiscriminatorType<Boolean> {
    public static final BooleanType INSTANCE = new BooleanType();

    public BooleanType() {
        this((SqlTypeDescriptor)org.hibernate.type.descriptor.sql.BooleanTypeDescriptor.INSTANCE, BooleanTypeDescriptor.INSTANCE);
    }

    protected BooleanType(SqlTypeDescriptor sqlTypeDescriptor, BooleanTypeDescriptor javaTypeDescriptor) {
        super(sqlTypeDescriptor, javaTypeDescriptor);
    }

    @Override
    public String getName() {
        return "boolean";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), Boolean.TYPE.getName(), Boolean.class.getName()};
    }

    @Override
    public Class getPrimitiveClass() {
        return Boolean.TYPE;
    }

    @Override
    public Serializable getDefaultValue() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean stringToObject(String string) {
        return (Boolean)this.fromString(string);
    }

    @Override
    public String objectToSQLString(Boolean value, Dialect dialect) {
        return dialect.toBooleanValueString(value);
    }
}

