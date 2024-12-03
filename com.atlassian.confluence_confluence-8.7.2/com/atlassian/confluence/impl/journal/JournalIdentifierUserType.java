/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.impl.hibernate.HibernateUserType
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.impl.hibernate.HibernateUserType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class JournalIdentifierUserType
extends HibernateUserType {
    private static final int[] SQL_TYPES = new int[]{12};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return JournalIdentifier.class;
    }

    public boolean equals(Object x, Object y) {
        return Objects.equals(x, y);
    }

    public Object nullSafeGetImpl(ResultSet rs, String[] columns, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String journalName = rs.getString(columns[0]);
        return journalName != null ? new JournalIdentifier(journalName) : null;
    }

    public void nullSafeSetImpl(PreparedStatement ps, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            ps.setNull(index, 12);
        } else {
            ps.setString(index, ((JournalIdentifier)value).getJournalName());
        }
    }

    public Object deepCopy(Object object) {
        return object;
    }

    public boolean isMutable() {
        return false;
    }
}

