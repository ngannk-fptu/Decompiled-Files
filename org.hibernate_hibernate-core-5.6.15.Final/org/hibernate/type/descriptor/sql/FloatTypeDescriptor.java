/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import org.hibernate.type.descriptor.sql.RealTypeDescriptor;

public class FloatTypeDescriptor
extends RealTypeDescriptor {
    public static final FloatTypeDescriptor INSTANCE = new FloatTypeDescriptor();

    @Override
    public int getSqlType() {
        return 6;
    }
}

