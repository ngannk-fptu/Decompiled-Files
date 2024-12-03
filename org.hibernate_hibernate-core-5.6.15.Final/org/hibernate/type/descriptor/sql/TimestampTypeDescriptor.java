/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class TimestampTypeDescriptor
implements SqlTypeDescriptor {
    public static final TimestampTypeDescriptor INSTANCE = new TimestampTypeDescriptor();

    @Override
    public int getSqlType() {
        return 93;
    }

    @Override
    public boolean canBeRemapped() {
        return true;
    }

    @Override
    public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicBinder<X>(javaTypeDescriptor, this){

            @Override
            protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                Timestamp timestamp = javaTypeDescriptor.unwrap(value, Timestamp.class, options);
                if (value instanceof Calendar) {
                    st.setTimestamp(index, timestamp, (Calendar)value);
                } else if (options.getJdbcTimeZone() != null) {
                    st.setTimestamp(index, timestamp, Calendar.getInstance(options.getJdbcTimeZone()));
                } else {
                    st.setTimestamp(index, timestamp);
                }
            }

            @Override
            protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                Timestamp timestamp = javaTypeDescriptor.unwrap(value, Timestamp.class, options);
                if (value instanceof Calendar) {
                    st.setTimestamp(name, timestamp, (Calendar)value);
                } else if (options.getJdbcTimeZone() != null) {
                    st.setTimestamp(name, timestamp, Calendar.getInstance(options.getJdbcTimeZone()));
                } else {
                    st.setTimestamp(name, timestamp);
                }
            }
        };
    }

    @Override
    public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicExtractor<X>(javaTypeDescriptor, this){

            @Override
            protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                return options.getJdbcTimeZone() != null ? javaTypeDescriptor.wrap(rs.getTimestamp(name, Calendar.getInstance(options.getJdbcTimeZone())), options) : javaTypeDescriptor.wrap(rs.getTimestamp(name), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                return options.getJdbcTimeZone() != null ? javaTypeDescriptor.wrap(statement.getTimestamp(index, Calendar.getInstance(options.getJdbcTimeZone())), options) : javaTypeDescriptor.wrap(statement.getTimestamp(index), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                return options.getJdbcTimeZone() != null ? javaTypeDescriptor.wrap(statement.getTimestamp(name, Calendar.getInstance(options.getJdbcTimeZone())), options) : javaTypeDescriptor.wrap(statement.getTimestamp(name), options);
            }
        };
    }
}

