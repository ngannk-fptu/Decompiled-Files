/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.StringTypeDescriptor;
import org.hibernate.type.descriptor.sql.LongNVarcharTypeDescriptor;

public class NTextType
extends AbstractSingleColumnStandardBasicType<String> {
    public static final NTextType INSTANCE = new NTextType();

    public NTextType() {
        super(LongNVarcharTypeDescriptor.INSTANCE, StringTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "ntext";
    }
}

