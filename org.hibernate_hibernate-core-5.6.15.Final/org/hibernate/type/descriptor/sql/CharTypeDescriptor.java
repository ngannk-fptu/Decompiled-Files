/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class CharTypeDescriptor
extends VarcharTypeDescriptor {
    public static final CharTypeDescriptor INSTANCE = new CharTypeDescriptor();

    @Override
    public int getSqlType() {
        return 1;
    }
}

