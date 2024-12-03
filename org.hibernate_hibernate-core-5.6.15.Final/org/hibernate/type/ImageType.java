/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.PrimitiveByteArrayTypeDescriptor;
import org.hibernate.type.descriptor.sql.LongVarbinaryTypeDescriptor;

public class ImageType
extends AbstractSingleColumnStandardBasicType<byte[]> {
    public static final ImageType INSTANCE = new ImageType();

    public ImageType() {
        super(LongVarbinaryTypeDescriptor.INSTANCE, PrimitiveByteArrayTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "image";
    }
}

