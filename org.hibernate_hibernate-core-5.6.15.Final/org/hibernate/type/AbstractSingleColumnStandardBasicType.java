/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractStandardBasicType;
import org.hibernate.type.SingleColumnType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public abstract class AbstractSingleColumnStandardBasicType<T>
extends AbstractStandardBasicType<T>
implements SingleColumnType<T> {
    public AbstractSingleColumnStandardBasicType(SqlTypeDescriptor sqlTypeDescriptor, JavaTypeDescriptor<T> javaTypeDescriptor) {
        super(sqlTypeDescriptor, javaTypeDescriptor);
    }

    @Override
    public final int sqlType() {
        return this.getSqlTypeDescriptor().getSqlType();
    }

    @Override
    public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (settable[0]) {
            this.nullSafeSet(st, value, index, session);
        }
    }
}

