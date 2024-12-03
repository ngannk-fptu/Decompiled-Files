/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CharacterType
extends AbstractType<Character> {
    public CharacterType() {
        super(1);
    }

    public CharacterType(int type) {
        super(type);
    }

    @Override
    public Character getValue(ResultSet rs, int startIndex) throws SQLException {
        String str = rs.getString(startIndex);
        return str != null ? Character.valueOf(str.charAt(0)) : null;
    }

    @Override
    public Class<Character> getReturnedClass() {
        return Character.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Character value) throws SQLException {
        st.setString(startIndex, value.toString());
    }
}

