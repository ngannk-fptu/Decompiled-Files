/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.HibernateUserType
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 */
package com.atlassian.confluence.spaces.persistence.dao.hibernate;

import com.atlassian.confluence.impl.hibernate.HibernateUserType;
import com.atlassian.confluence.spaces.SpaceType;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class SpaceTypeUserType
extends HibernateUserType
implements Serializable {
    private static final int[] SQL_TYPES = new int[]{12};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return SpaceType.class;
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

    public Object nullSafeGetImpl(ResultSet rs, String[] columns, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String spaceTypeAsString = rs.getString(columns[0]);
        return SpaceType.getSpaceType(spaceTypeAsString);
    }

    public void nullSafeSetImpl(PreparedStatement ps, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            ps.setNull(index, 12);
        } else {
            ps.setString(index, value.toString());
        }
    }

    public Object deepCopy(Object object) {
        return object;
    }

    public boolean isMutable() {
        return false;
    }
}

