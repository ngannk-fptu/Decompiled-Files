/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.StringTypeDescriptor;
import org.hibernate.type.descriptor.sql.LongVarcharTypeDescriptor;

public class TextType
extends AbstractSingleColumnStandardBasicType<String> {
    public static final TextType INSTANCE = new TextType();

    public TextType() {
        super(LongVarcharTypeDescriptor.INSTANCE, StringTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "text";
    }
}

