/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import org.hibernate.type.descriptor.sql.VarbinaryTypeDescriptor;

public class LongVarbinaryTypeDescriptor
extends VarbinaryTypeDescriptor {
    public static final LongVarbinaryTypeDescriptor INSTANCE = new LongVarbinaryTypeDescriptor();

    @Override
    public int getSqlType() {
        return -4;
    }
}

