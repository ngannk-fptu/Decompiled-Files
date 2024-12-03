/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.HibernateUserType
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.confluence.impl.hibernate.HibernateUserType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class InstantType
extends HibernateUserType {
    private static final int[] SQL_TYPES = new int[]{-5};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return Instant.class;
    }

    public boolean equals(Object x, Object y) {
        return Objects.equals(x, y);
    }

    public Object nullSafeGetImpl(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return Instant.ofEpochMilli(rs.getLong(names[0]));
    }

    public void nullSafeSetImpl(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setLong(index, Instant.EPOCH.toEpochMilli());
        } else {
            Instant instant = (Instant)value;
            st.setLong(index, instant.toEpochMilli());
        }
    }

    public Object deepCopy(Object value) {
        return value;
    }

    public boolean isMutable() {
        return false;
    }
}

