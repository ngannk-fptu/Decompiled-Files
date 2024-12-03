/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.ByteArrayTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarbinaryTypeDescriptor;

public class WrapperBinaryType
extends AbstractSingleColumnStandardBasicType<Byte[]> {
    public static final WrapperBinaryType INSTANCE = new WrapperBinaryType();

    public WrapperBinaryType() {
        super(VarbinaryTypeDescriptor.INSTANCE, ByteArrayTypeDescriptor.INSTANCE);
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), "Byte[]", Byte[].class.getName()};
    }

    @Override
    public String getName() {
        return "wrapper-binary";
    }
}

