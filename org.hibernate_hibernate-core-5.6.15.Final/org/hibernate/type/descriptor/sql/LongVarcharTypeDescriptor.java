/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class LongVarcharTypeDescriptor
extends VarcharTypeDescriptor {
    public static final LongVarcharTypeDescriptor INSTANCE = new LongVarcharTypeDescriptor();

    @Override
    public int getSqlType() {
        return -1;
    }
}

