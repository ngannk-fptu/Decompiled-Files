/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.sql;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class IntegerTypeDescriptor
implements SqlTypeDescriptor {
    public static final IntegerTypeDescriptor INSTANCE = new IntegerTypeDescriptor();

    @Override
    public int getSqlType() {
        return 4;
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
                st.setInt(index, javaTypeDescriptor.unwrap(value, Integer.class, options));
            }

            @Override
            protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                st.setInt(name, (int)javaTypeDescriptor.unwrap(value, Integer.class, options));
            }
        };
    }

    @Override
    public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicExtractor<X>(javaTypeDescriptor, this){

            @Override
            protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(rs.getInt(name), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(statement.getInt(index), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(statement.getInt(name), options);
            }
        };
    }
}

