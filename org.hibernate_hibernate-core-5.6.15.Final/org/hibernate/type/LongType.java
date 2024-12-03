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
import org.hibernate.type.descriptor.java.LongTypeDescriptor;
import org.hibernate.type.descriptor.sql.BigIntTypeDescriptor;

public class LongType
extends AbstractSingleColumnStandardBasicType<Long>
implements PrimitiveType<Long>,
DiscriminatorType<Long>,
VersionType<Long> {
    public static final LongType INSTANCE = new LongType();
    private static final Long ZERO = 0L;

    public LongType() {
        super(BigIntTypeDescriptor.INSTANCE, LongTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "long";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), Long.TYPE.getName(), Long.class.getName()};
    }

    @Override
    public Serializable getDefaultValue() {
        return ZERO;
    }

    @Override
    public Class getPrimitiveClass() {
        return Long.TYPE;
    }

    @Override
    public Long stringToObject(String xml) throws Exception {
        return Long.valueOf(xml);
    }

    @Override
    public Long next(Long current, SharedSessionContractImplementor session) {
        return current + 1L;
    }

    @Override
    public Long seed(SharedSessionContractImplementor session) {
        return ZERO;
    }

    @Override
    public Comparator<Long> getComparator() {
        return this.getJavaTypeDescriptor().getComparator();
    }

    @Override
    public String objectToSQLString(Long value, Dialect dialect) throws Exception {
        return value.toString();
    }
}

