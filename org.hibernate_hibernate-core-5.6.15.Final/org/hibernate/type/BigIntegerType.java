/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.math.BigInteger;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.descriptor.java.BigIntegerTypeDescriptor;
import org.hibernate.type.descriptor.sql.NumericTypeDescriptor;

public class BigIntegerType
extends AbstractSingleColumnStandardBasicType<BigInteger>
implements DiscriminatorType<BigInteger> {
    public static final BigIntegerType INSTANCE = new BigIntegerType();

    public BigIntegerType() {
        super(NumericTypeDescriptor.INSTANCE, BigIntegerTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "big_integer";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public String objectToSQLString(BigInteger value, Dialect dialect) {
        return BigIntegerTypeDescriptor.INSTANCE.toString(value);
    }

    @Override
    public BigInteger stringToObject(String string) {
        return BigIntegerTypeDescriptor.INSTANCE.fromString(string);
    }
}

