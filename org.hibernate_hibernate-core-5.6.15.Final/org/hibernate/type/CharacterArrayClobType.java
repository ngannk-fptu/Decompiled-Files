/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.CharacterArrayTypeDescriptor;
import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;

public class CharacterArrayClobType
extends AbstractSingleColumnStandardBasicType<Character[]> {
    public static final CharacterArrayClobType INSTANCE = new CharacterArrayClobType();

    public CharacterArrayClobType() {
        super(ClobTypeDescriptor.DEFAULT, CharacterArrayTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return null;
    }
}

