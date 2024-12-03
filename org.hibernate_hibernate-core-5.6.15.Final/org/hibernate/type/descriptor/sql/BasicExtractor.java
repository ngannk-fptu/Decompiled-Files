/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.type.descriptor.sql;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.type.descriptor.JdbcTypeNameMapper;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.jboss.logging.Logger;

public abstract class BasicExtractor<J>
implements ValueExtractor<J> {
    private static final Logger log = CoreLogging.logger(BasicExtractor.class);
    private final JavaTypeDescriptor<J> javaDescriptor;
    private final SqlTypeDescriptor sqlDescriptor;

    public BasicExtractor(JavaTypeDescriptor<J> javaDescriptor, SqlTypeDescriptor sqlDescriptor) {
        this.javaDescriptor = javaDescriptor;
        this.sqlDescriptor = sqlDescriptor;
    }

    public JavaTypeDescriptor<J> getJavaDescriptor() {
        return this.javaDescriptor;
    }

    public SqlTypeDescriptor getSqlDescriptor() {
        return this.sqlDescriptor;
    }

    @Override
    public J extract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
        J value = this.doExtract(rs, name, options);
        if (value == null || rs.wasNull()) {
            if (log.isTraceEnabled()) {
                log.tracef("extracted value ([%s] : [%s]) - [null]", (Object)name, (Object)JdbcTypeNameMapper.getTypeName(this.getSqlDescriptor().getSqlType()));
            }
            return null;
        }
        if (log.isTraceEnabled()) {
            log.tracef("extracted value ([%s] : [%s]) - [%s]", (Object)name, (Object)JdbcTypeNameMapper.getTypeName(this.getSqlDescriptor().getSqlType()), (Object)this.getJavaDescriptor().extractLoggableRepresentation(value));
        }
        return value;
    }

    protected abstract J doExtract(ResultSet var1, String var2, WrapperOptions var3) throws SQLException;

    @Override
    public J extract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
        J value = this.doExtract(statement, index, options);
        if (value == null || statement.wasNull()) {
            if (log.isTraceEnabled()) {
                log.tracef("extracted procedure output  parameter ([%s] : [%s]) - [null]", index, (Object)JdbcTypeNameMapper.getTypeName(this.getSqlDescriptor().getSqlType()));
            }
            return null;
        }
        if (log.isTraceEnabled()) {
            log.tracef("extracted procedure output  parameter ([%s] : [%s]) - [%s]", index, (Object)JdbcTypeNameMapper.getTypeName(this.getSqlDescriptor().getSqlType()), (Object)this.getJavaDescriptor().extractLoggableRepresentation(value));
        }
        return value;
    }

    protected abstract J doExtract(CallableStatement var1, int var2, WrapperOptions var3) throws SQLException;

    @Override
    public J extract(CallableStatement statement, String[] paramNames, WrapperOptions options) throws SQLException {
        if (paramNames.length > 1) {
            throw new IllegalArgumentException("Basic value extraction cannot handle multiple output parameters");
        }
        String paramName = paramNames[0];
        J value = this.doExtract(statement, paramName, options);
        if (value == null || statement.wasNull()) {
            if (log.isTraceEnabled()) {
                log.tracef("extracted named procedure output  parameter ([%s] : [%s]) - [null]", (Object)paramName, (Object)JdbcTypeNameMapper.getTypeName(this.getSqlDescriptor().getSqlType()));
            }
            return null;
        }
        if (log.isTraceEnabled()) {
            log.tracef("extracted named procedure output  parameter ([%s] : [%s]) - [%s]", (Object)paramName, (Object)JdbcTypeNameMapper.getTypeName(this.getSqlDescriptor().getSqlType()), (Object)this.getJavaDescriptor().extractLoggableRepresentation(value));
        }
        return value;
    }

    protected abstract J doExtract(CallableStatement var1, String var2, WrapperOptions var3) throws SQLException;
}

