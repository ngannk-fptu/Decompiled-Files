/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.math.BigDecimal;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.BigDecimalTypeDescriptor;
import org.hibernate.type.descriptor.sql.NumericTypeDescriptor;

public class BigDecimalType
extends AbstractSingleColumnStandardBasicType<BigDecimal> {
    public static final BigDecimalType INSTANCE = new BigDecimalType();

    public BigDecimalType() {
        super(NumericTypeDescriptor.INSTANCE, BigDecimalTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "big_decimal";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }
}

