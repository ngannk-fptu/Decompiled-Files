/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.descriptor.java.CharacterTypeDescriptor;
import org.hibernate.type.descriptor.sql.NCharTypeDescriptor;

public class CharacterNCharType
extends AbstractSingleColumnStandardBasicType<Character>
implements PrimitiveType<Character>,
DiscriminatorType<Character> {
    public static final CharacterNCharType INSTANCE = new CharacterNCharType();

    public CharacterNCharType() {
        super(NCharTypeDescriptor.INSTANCE, CharacterTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "ncharacter";
    }

    @Override
    public Serializable getDefaultValue() {
        throw new UnsupportedOperationException("not a valid id type");
    }

    @Override
    public Class getPrimitiveClass() {
        return Character.TYPE;
    }

    @Override
    public String objectToSQLString(Character value, Dialect dialect) {
        return '\'' + this.toString(value) + '\'';
    }

    @Override
    public Character stringToObject(String xml) {
        return (Character)this.fromString(xml);
    }
}

