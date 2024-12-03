/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.HibernateUserType
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 */
package com.atlassian.confluence.internal.relations.dao.hibernate;

import com.atlassian.confluence.impl.hibernate.HibernateUserType;
import com.atlassian.confluence.internal.relations.RelatableEntityTypeEnum;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class RelatableEntityTypeEnumUserType
extends HibernateUserType {
    private static final int[] SQL_TYPES = new int[]{12};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return RelatableEntityTypeEnum.class;
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

    public RelatableEntityTypeEnum nullSafeGetImpl(ResultSet rs, String[] columns, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String typeEnumAsString = rs.getString(columns[0]);
        return RelatableEntityTypeEnum.valueOf(typeEnumAsString);
    }

    public void nullSafeSetImpl(PreparedStatement ps, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            ps.setNull(index, 12);
        } else {
            ps.setString(index, ((RelatableEntityTypeEnum)((Object)value)).name());
        }
    }

    public Object deepCopy(Object object) {
        return object;
    }

    public boolean isMutable() {
        return false;
    }
}

