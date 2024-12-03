/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import org.hibernate.type.descriptor.sql.VarbinaryTypeDescriptor;

public class BinaryTypeDescriptor
extends VarbinaryTypeDescriptor {
    public static final BinaryTypeDescriptor INSTANCE = new BinaryTypeDescriptor();

    @Override
    public int getSqlType() {
        return -2;
    }
}

