/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.HibernateUserType
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.usertype.EnhancedUserType
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.impl.hibernate.HibernateUserType;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.EnhancedUserType;

public class BodyTypeUserType
extends HibernateUserType
implements EnhancedUserType {
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        if (cached == null) {
            return null;
        }
        return BodyType.fromInt((Integer)cached);
    }

    public Serializable disassemble(Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        return Integer.valueOf(((BodyType)value).toInt());
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        return x != null && y != null && x.getClass() == y.getClass() && ((BodyType)x).toInt() == ((BodyType)y).toInt();
    }

    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    public boolean isMutable() {
        return false;
    }

    public Object nullSafeGetImpl(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        int code = rs.getInt(names[0]);
        return rs.wasNull() ? null : BodyType.fromInt(code);
    }

    public void nullSafeSetImpl(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, 4);
        } else {
            st.setInt(index, ((BodyType)value).toInt());
        }
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    public Class returnedClass() {
        return BodyType.class;
    }

    public int[] sqlTypes() {
        return new int[]{5};
    }

    public String objectToSQLString(Object value) {
        return Integer.toString(((BodyType)value).toInt());
    }

    public String toXMLString(Object value) {
        return Integer.toString(((BodyType)value).toInt());
    }

    public Object fromXMLString(String xmlValue) {
        return BodyType.fromInt(Integer.parseInt(xmlValue));
    }
}

