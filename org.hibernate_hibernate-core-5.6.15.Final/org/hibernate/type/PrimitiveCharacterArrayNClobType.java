/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.CharacterArrayNClobType;
import org.hibernate.type.descriptor.java.PrimitiveCharacterArrayTypeDescriptor;
import org.hibernate.type.descriptor.sql.NClobTypeDescriptor;

public class PrimitiveCharacterArrayNClobType
extends AbstractSingleColumnStandardBasicType<char[]> {
    public static final CharacterArrayNClobType INSTANCE = new CharacterArrayNClobType();

    public PrimitiveCharacterArrayNClobType() {
        super(NClobTypeDescriptor.DEFAULT, PrimitiveCharacterArrayTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return null;
    }
}

