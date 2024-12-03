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
import org.hibernate.type.descriptor.java.ByteTypeDescriptor;
import org.hibernate.type.descriptor.sql.TinyIntTypeDescriptor;

public class ByteType
extends AbstractSingleColumnStandardBasicType<Byte>
implements PrimitiveType<Byte>,
DiscriminatorType<Byte>,
VersionType<Byte> {
    public static final ByteType INSTANCE = new ByteType();
    private static final Byte ZERO = 0;

    public ByteType() {
        super(TinyIntTypeDescriptor.INSTANCE, ByteTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "byte";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), Byte.TYPE.getName(), Byte.class.getName()};
    }

    @Override
    public Serializable getDefaultValue() {
        return ZERO;
    }

    @Override
    public Class getPrimitiveClass() {
        return Byte.TYPE;
    }

    @Override
    public String objectToSQLString(Byte value, Dialect dialect) {
        return this.toString(value);
    }

    @Override
    public Byte stringToObject(String xml) {
        return (Byte)this.fromString(xml);
    }

    @Override
    public Byte fromStringValue(String xml) {
        return (Byte)this.fromString(xml);
    }

    @Override
    public Byte next(Byte current, SharedSessionContractImplementor session) {
        return (byte)(current + 1);
    }

    @Override
    public Byte seed(SharedSessionContractImplementor session) {
        return ZERO;
    }

    @Override
    public Comparator<Byte> getComparator() {
        return this.getJavaTypeDescriptor().getComparator();
    }
}

