/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import org.hibernate.type.descriptor.sql.NVarcharTypeDescriptor;

public class NCharTypeDescriptor
extends NVarcharTypeDescriptor {
    public static final NCharTypeDescriptor INSTANCE = new NCharTypeDescriptor();

    @Override
    public int getSqlType() {
        return -15;
    }
}

