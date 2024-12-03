/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.util.Comparator;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.VersionType;
import org.hibernate.type.descriptor.java.IntegerTypeDescriptor;

public class IntegerType
extends AbstractSingleColumnStandardBasicType<Integer>
implements PrimitiveType<Integer>,
DiscriminatorType<Integer>,
VersionType<Integer> {
    public static final IntegerType INSTANCE = new IntegerType();
    public static final Integer ZERO = 0;

    public IntegerType() {
        super(org.hibernate.type.descriptor.sql.IntegerTypeDescriptor.INSTANCE, IntegerTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "integer";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), Integer.TYPE.getName(), Integer.class.getName()};
    }

    @Override
    public Serializable getDefaultValue() {
        return ZERO;
    }

    @Override
    public Class getPrimitiveClass() {
        return Integer.TYPE;
    }

    @Override
    public String objectToSQLString(Integer value, Dialect dialect) throws Exception {
        return this.toString(value);
    }

    @Override
    public Integer stringToObject(String xml) {
        return (Integer)this.fromString(xml);
    }

    @Override
    public Integer seed(SharedSessionContractImplementor session) {
        return ZERO;
    }

    @Override
    public Integer next(Integer current, SharedSessionContractImplementor session) {
        return current + 1;
    }

    @Override
    public Comparator<Integer> getComparator() {
        return this.getJavaTypeDescriptor().getComparator();
    }
}

