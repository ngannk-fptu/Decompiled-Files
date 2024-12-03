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
import org.hibernate.type.descriptor.sql.IntegerTypeDescriptor;

public class NumericBooleanType
extends AbstractSingleColumnStandardBasicType<Boolean>
implements PrimitiveType<Boolean>,
DiscriminatorType<Boolean> {
    public static final NumericBooleanType INSTANCE = new NumericBooleanType();

    public NumericBooleanType() {
        super(IntegerTypeDescriptor.INSTANCE, BooleanTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "numeric_boolean";
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
        return value != false ? "1" : "0";
    }
}

