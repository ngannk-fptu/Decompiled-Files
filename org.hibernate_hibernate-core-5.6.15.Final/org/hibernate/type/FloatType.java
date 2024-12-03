/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.descriptor.sql.FloatTypeDescriptor;

public class FloatType
extends AbstractSingleColumnStandardBasicType<Float>
implements PrimitiveType<Float> {
    public static final FloatType INSTANCE = new FloatType();
    public static final Float ZERO = Float.valueOf(0.0f);

    public FloatType() {
        super(FloatTypeDescriptor.INSTANCE, org.hibernate.type.descriptor.java.FloatTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "float";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), Float.TYPE.getName(), Float.class.getName()};
    }

    @Override
    public Serializable getDefaultValue() {
        return ZERO;
    }

    @Override
    public Class getPrimitiveClass() {
        return Float.TYPE;
    }

    @Override
    public String objectToSQLString(Float value, Dialect dialect) throws Exception {
        return this.toString(value);
    }
}

