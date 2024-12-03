/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class LocaleType
extends AbstractType<Locale> {
    private static final Pattern LOCALE = Pattern.compile("[_#-]+");

    public LocaleType() {
        super(12);
    }

    public LocaleType(int type) {
        super(type);
    }

    @Override
    public Class<Locale> getReturnedClass() {
        return Locale.class;
    }

    @Override
    @Nullable
    public Locale getValue(ResultSet rs, int startIndex) throws SQLException {
        String val = rs.getString(startIndex);
        return val != null ? LocaleType.toLocale(val) : null;
    }

    public static Locale toLocale(String val) {
        String[] tokens = LOCALE.split(val);
        switch (tokens.length) {
            case 1: {
                return new Locale(tokens[0]);
            }
            case 2: {
                return new Locale(tokens[0], tokens[1]);
            }
            case 3: {
                return new Locale(tokens[0], tokens[1], tokens[2]);
            }
        }
        return null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Locale value) throws SQLException {
        st.setString(startIndex, value.toString());
    }
}

