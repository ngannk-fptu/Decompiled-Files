/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.CharacterArrayTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class CharacterArrayType
extends AbstractSingleColumnStandardBasicType<Character[]> {
    public static final CharacterArrayType INSTANCE = new CharacterArrayType();

    public CharacterArrayType() {
        super(VarcharTypeDescriptor.INSTANCE, CharacterArrayTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "wrapper-characters";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), Character[].class.getName(), "Character[]"};
    }
}

