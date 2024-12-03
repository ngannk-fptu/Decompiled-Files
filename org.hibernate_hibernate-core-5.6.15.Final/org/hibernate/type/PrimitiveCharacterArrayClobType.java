/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.CharacterArrayClobType;
import org.hibernate.type.descriptor.java.PrimitiveCharacterArrayTypeDescriptor;
import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;

public class PrimitiveCharacterArrayClobType
extends AbstractSingleColumnStandardBasicType<char[]> {
    public static final CharacterArrayClobType INSTANCE = new CharacterArrayClobType();

    public PrimitiveCharacterArrayClobType() {
        super(ClobTypeDescriptor.DEFAULT, PrimitiveCharacterArrayTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return null;
    }
}

