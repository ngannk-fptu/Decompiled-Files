/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.HibernateUserType
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 */
package com.atlassian.confluence.impl.user.crowd.hibernate.types;

import com.atlassian.confluence.impl.hibernate.HibernateUserType;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

@Deprecated(forRemoval=true)
public abstract class EnumPersistentType<T extends Enum>
extends HibernateUserType
implements Serializable {
    private static final int[] SQL_TYPES = new int[]{12};

    public final int[] sqlTypes() {
        return Arrays.copyOf(SQL_TYPES, 1);
    }

    public abstract Class returnedClass();

    public final boolean equals(Object x, Object y) {
        return x == y;
    }

    public Object nullSafeGetImpl(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String typeAsString = rs.getString(names[0]);
        return typeAsString == null ? null : Enum.valueOf(this.returnedClass(), typeAsString);
    }

    public void nullSafeSetImpl(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, 12);
        } else {
            st.setString(index, ((Enum)value).name());
        }
    }

    public final Object deepCopy(Object value) {
        return value;
    }

    public final boolean isMutable() {
        return false;
    }
}

