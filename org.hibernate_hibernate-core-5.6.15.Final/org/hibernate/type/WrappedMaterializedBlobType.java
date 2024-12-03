/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.ByteArrayTypeDescriptor;
import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;

public class WrappedMaterializedBlobType
extends AbstractSingleColumnStandardBasicType<Byte[]> {
    public static final WrappedMaterializedBlobType INSTANCE = new WrappedMaterializedBlobType();

    public WrappedMaterializedBlobType() {
        super(BlobTypeDescriptor.DEFAULT, ByteArrayTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return null;
    }
}

