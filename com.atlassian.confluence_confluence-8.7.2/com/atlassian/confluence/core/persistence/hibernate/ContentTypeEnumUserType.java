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
import com.atlassian.confluence.search.service.ContentTypeEnum;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class ContentTypeEnumUserType
extends HibernateUserType {
    private static final int[] SQL_TYPES = new int[]{12};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return ContentTypeEnum.class;
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

    public ContentTypeEnum nullSafeGetImpl(ResultSet rs, String[] columns, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String contentTypeEnumAsString = rs.getString(columns[0]);
        return ContentTypeEnum.getByRepresentation(contentTypeEnumAsString);
    }

    public void nullSafeSetImpl(PreparedStatement ps, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            ps.setNull(index, 12);
        } else {
            ps.setString(index, ((ContentTypeEnum)((Object)value)).getRepresentation());
        }
    }

    public Object deepCopy(Object object) {
        return object;
    }

    public boolean isMutable() {
        return false;
    }
}

