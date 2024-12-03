/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.StringTypeDescriptor;
import org.hibernate.type.descriptor.sql.NClobTypeDescriptor;

public class MaterializedNClobType
extends AbstractSingleColumnStandardBasicType<String> {
    public static final MaterializedNClobType INSTANCE = new MaterializedNClobType();

    public MaterializedNClobType() {
        super(NClobTypeDescriptor.DEFAULT, StringTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "materialized_nclob";
    }
}

