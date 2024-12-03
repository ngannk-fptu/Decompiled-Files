/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.PrimitiveCharacterArrayTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class CharArrayType
extends AbstractSingleColumnStandardBasicType<char[]> {
    public static final CharArrayType INSTANCE = new CharArrayType();

    public CharArrayType() {
        super(VarcharTypeDescriptor.INSTANCE, PrimitiveCharacterArrayTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "characters";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), "char[]", char[].class.getName()};
    }
}

