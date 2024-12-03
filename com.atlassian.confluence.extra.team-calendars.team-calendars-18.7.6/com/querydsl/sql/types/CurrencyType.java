/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;
import javax.annotation.Nullable;

public class CurrencyType
extends AbstractType<Currency> {
    public CurrencyType() {
        super(12);
    }

    public CurrencyType(int type) {
        super(type);
    }

    @Override
    public Class<Currency> getReturnedClass() {
        return Currency.class;
    }

    @Override
    @Nullable
    public Currency getValue(ResultSet rs, int startIndex) throws SQLException {
        String val = rs.getString(startIndex);
        return val != null ? Currency.getInstance(val) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Currency value) throws SQLException {
        st.setString(startIndex, value.getCurrencyCode());
    }
}

