/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.PrimitiveByteArrayTypeDescriptor;
import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;

public class MaterializedBlobType
extends AbstractSingleColumnStandardBasicType<byte[]> {
    public static final MaterializedBlobType INSTANCE = new MaterializedBlobType();

    public MaterializedBlobType() {
        super(BlobTypeDescriptor.DEFAULT, PrimitiveByteArrayTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "materialized_blob";
    }
}

