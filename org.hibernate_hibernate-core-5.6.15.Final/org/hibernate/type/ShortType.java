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
import org.hibernate.type.descriptor.java.ShortTypeDescriptor;
import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;

public class ShortType
extends AbstractSingleColumnStandardBasicType<Short>
implements PrimitiveType<Short>,
DiscriminatorType<Short>,
VersionType<Short> {
    public static final ShortType INSTANCE = new ShortType();
    private static final Short ZERO = 0;

    public ShortType() {
        super(SmallIntTypeDescriptor.INSTANCE, ShortTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "short";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), Short.TYPE.getName(), Short.class.getName()};
    }

    @Override
    public Serializable getDefaultValue() {
        return ZERO;
    }

    @Override
    public Class getPrimitiveClass() {
        return Short.TYPE;
    }

    @Override
    public String objectToSQLString(Short value, Dialect dialect) {
        return value.toString();
    }

    @Override
    public Short stringToObject(String xml) {
        return Short.valueOf(xml);
    }

    @Override
    public Short seed(SharedSessionContractImplementor session) {
        return ZERO;
    }

    @Override
    public Short next(Short current, SharedSessionContractImplementor session) {
        return (short)(current + 1);
    }

    @Override
    public Comparator<Short> getComparator() {
        return this.getJavaTypeDescriptor().getComparator();
    }
}

