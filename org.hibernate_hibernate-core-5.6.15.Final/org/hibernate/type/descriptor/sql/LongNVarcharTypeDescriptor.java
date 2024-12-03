/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import org.hibernate.type.descriptor.sql.NVarcharTypeDescriptor;

public class LongNVarcharTypeDescriptor
extends NVarcharTypeDescriptor {
    public static final LongNVarcharTypeDescriptor INSTANCE = new LongNVarcharTypeDescriptor();

    @Override
    public int getSqlType() {
        return -16;
    }
}

