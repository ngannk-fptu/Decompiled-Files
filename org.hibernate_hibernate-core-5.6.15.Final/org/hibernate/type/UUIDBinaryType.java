/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.UUID;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor;

public class UUIDBinaryType
extends AbstractSingleColumnStandardBasicType<UUID> {
    public static final UUIDBinaryType INSTANCE = new UUIDBinaryType();

    public UUIDBinaryType() {
        super(BinaryTypeDescriptor.INSTANCE, UUIDTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "uuid-binary";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }
}

