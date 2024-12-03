/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.Comparator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.VersionType;
import org.hibernate.type.descriptor.java.PrimitiveByteArrayTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarbinaryTypeDescriptor;

public class BinaryType
extends AbstractSingleColumnStandardBasicType<byte[]>
implements VersionType<byte[]> {
    public static final BinaryType INSTANCE = new BinaryType();

    @Override
    public String getName() {
        return "binary";
    }

    public BinaryType() {
        super(VarbinaryTypeDescriptor.INSTANCE, PrimitiveByteArrayTypeDescriptor.INSTANCE);
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), "byte[]", byte[].class.getName()};
    }

    @Override
    @Deprecated
    public byte[] seed(SharedSessionContractImplementor session) {
        return null;
    }

    @Override
    @Deprecated
    public byte[] next(byte[] current, SharedSessionContractImplementor session) {
        return current;
    }

    @Override
    @Deprecated
    public Comparator<byte[]> getComparator() {
        return PrimitiveByteArrayTypeDescriptor.INSTANCE.getComparator();
    }
}

