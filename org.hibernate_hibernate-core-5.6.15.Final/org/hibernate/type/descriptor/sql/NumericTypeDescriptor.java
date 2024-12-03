/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import org.hibernate.type.descriptor.sql.DecimalTypeDescriptor;

public class NumericTypeDescriptor
extends DecimalTypeDescriptor {
    public static final NumericTypeDescriptor INSTANCE = new NumericTypeDescriptor();

    @Override
    public int getSqlType() {
        return 2;
    }
}

