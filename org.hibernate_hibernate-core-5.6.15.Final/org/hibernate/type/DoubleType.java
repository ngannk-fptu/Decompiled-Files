/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.descriptor.sql.DoubleTypeDescriptor;

public class DoubleType
extends AbstractSingleColumnStandardBasicType<Double>
implements PrimitiveType<Double> {
    public static final DoubleType INSTANCE = new DoubleType();
    public static final Double ZERO = 0.0;

    public DoubleType() {
        super(DoubleTypeDescriptor.INSTANCE, org.hibernate.type.descriptor.java.DoubleTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "double";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), Double.TYPE.getName(), Double.class.getName()};
    }

    @Override
    public Serializable getDefaultValue() {
        return ZERO;
    }

    @Override
    public Class getPrimitiveClass() {
        return Double.TYPE;
    }

    @Override
    public String objectToSQLString(Double value, Dialect dialect) throws Exception {
        return this.toString(value);
    }
}

