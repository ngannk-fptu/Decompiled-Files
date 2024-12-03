/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.HibernateUserType
 *  com.atlassian.sal.api.user.UserKey
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 */
package com.atlassian.confluence.user.persistence.dao.hibernate;

import com.atlassian.confluence.impl.hibernate.HibernateUserType;
import com.atlassian.sal.api.user.UserKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class UserKeyUserType
extends HibernateUserType {
    private static final int[] SQL_TYPES = new int[]{12};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return UserKey.class;
    }

    public boolean equals(Object x, Object y) {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        return x.equals(y);
    }

    public Object nullSafeGetImpl(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String userKeyAsString = rs.getString(names[0]);
        return userKeyAsString == null ? null : new UserKey(userKeyAsString);
    }

    public void nullSafeSetImpl(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, 12);
        } else if (value instanceof UserKey) {
            st.setString(index, UserKeyUserType.getStringValue((UserKey)value));
        } else {
            throw new IllegalArgumentException("Value must be a UserKey, but was a " + value.getClass().getName());
        }
    }

    public static String getStringValue(UserKey userKey) {
        return userKey.getStringValue();
    }

    public Object deepCopy(Object value) {
        return value;
    }

    public boolean isMutable() {
        return false;
    }
}

