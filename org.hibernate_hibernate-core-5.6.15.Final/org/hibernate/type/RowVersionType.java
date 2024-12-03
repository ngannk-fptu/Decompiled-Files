/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.Comparator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.VersionType;
import org.hibernate.type.descriptor.java.RowVersionTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarbinaryTypeDescriptor;

public class RowVersionType
extends AbstractSingleColumnStandardBasicType<byte[]>
implements VersionType<byte[]> {
    public static final RowVersionType INSTANCE = new RowVersionType();

    @Override
    public String getName() {
        return "row_version";
    }

    public RowVersionType() {
        super(VarbinaryTypeDescriptor.INSTANCE, RowVersionTypeDescriptor.INSTANCE);
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName()};
    }

    @Override
    public byte[] seed(SharedSessionContractImplementor session) {
        return null;
    }

    @Override
    public byte[] next(byte[] current, SharedSessionContractImplementor session) {
        return current;
    }

    @Override
    public Comparator<byte[]> getComparator() {
        return this.getJavaTypeDescriptor().getComparator();
    }
}

