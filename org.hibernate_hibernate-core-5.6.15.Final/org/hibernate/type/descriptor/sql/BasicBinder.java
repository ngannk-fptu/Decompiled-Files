/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.type.descriptor.sql;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.type.descriptor.JdbcTypeNameMapper;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.jboss.logging.Logger;

public abstract class BasicBinder<J>
implements ValueBinder<J> {
    private static final Logger log = CoreLogging.logger(BasicBinder.class);
    private static final String BIND_MSG_TEMPLATE = "binding parameter [%s] as [%s] - [%s]";
    private static final String NULL_BIND_MSG_TEMPLATE = "binding parameter [%s] as [%s] - [null]";
    private final JavaTypeDescriptor<J> javaDescriptor;
    private final SqlTypeDescriptor sqlDescriptor;

    public JavaTypeDescriptor<J> getJavaDescriptor() {
        return this.javaDescriptor;
    }

    public SqlTypeDescriptor getSqlDescriptor() {
        return this.sqlDescriptor;
    }

    public BasicBinder(JavaTypeDescriptor<J> javaDescriptor, SqlTypeDescriptor sqlDescriptor) {
        this.javaDescriptor = javaDescriptor;
        this.sqlDescriptor = sqlDescriptor;
    }

    @Override
    public final void bind(PreparedStatement st, J value, int index, WrapperOptions options) throws SQLException {
        if (value == null) {
            if (log.isTraceEnabled()) {
                log.trace((Object)String.format(NULL_BIND_MSG_TEMPLATE, index, JdbcTypeNameMapper.getTypeName(this.getSqlDescriptor().getSqlType())));
            }
            st.setNull(index, this.sqlDescriptor.getSqlType());
        } else {
            if (log.isTraceEnabled()) {
                log.trace((Object)String.format(BIND_MSG_TEMPLATE, index, JdbcTypeNameMapper.getTypeName(this.sqlDescriptor.getSqlType()), this.getJavaDescriptor().extractLoggableRepresentation(value)));
            }
            this.doBind(st, value, index, options);
        }
    }

    @Override
    public final void bind(CallableStatement st, J value, String name, WrapperOptions options) throws SQLException {
        if (value == null) {
            if (log.isTraceEnabled()) {
                log.trace((Object)String.format(NULL_BIND_MSG_TEMPLATE, name, JdbcTypeNameMapper.getTypeName(this.getSqlDescriptor().getSqlType())));
            }
            st.setNull(name, this.sqlDescriptor.getSqlType());
        } else {
            if (log.isTraceEnabled()) {
                log.trace((Object)String.format(BIND_MSG_TEMPLATE, name, JdbcTypeNameMapper.getTypeName(this.sqlDescriptor.getSqlType()), this.getJavaDescriptor().extractLoggableRepresentation(value)));
            }
            this.doBind(st, value, name, options);
        }
    }

    protected abstract void doBind(PreparedStatement var1, J var2, int var3, WrapperOptions var4) throws SQLException;

    protected abstract void doBind(CallableStatement var1, J var2, String var3, WrapperOptions var4) throws SQLException;
}

