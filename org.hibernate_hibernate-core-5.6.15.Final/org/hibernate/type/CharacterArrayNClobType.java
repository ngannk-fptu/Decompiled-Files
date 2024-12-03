/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.CharacterArrayTypeDescriptor;
import org.hibernate.type.descriptor.sql.NClobTypeDescriptor;

public class CharacterArrayNClobType
extends AbstractSingleColumnStandardBasicType<Character[]> {
    public static final CharacterArrayNClobType INSTANCE = new CharacterArrayNClobType();

    public CharacterArrayNClobType() {
        super(NClobTypeDescriptor.DEFAULT, CharacterArrayTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return null;
    }
}

