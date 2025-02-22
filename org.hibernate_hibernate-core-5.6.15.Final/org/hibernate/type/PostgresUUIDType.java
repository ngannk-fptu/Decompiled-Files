/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.BasicJavaDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.spi.TypeConfiguration;

public class PostgresUUIDType
extends AbstractSingleColumnStandardBasicType<UUID> {
    public static final PostgresUUIDType INSTANCE = new PostgresUUIDType();

    public PostgresUUIDType() {
        super(PostgresUUIDSqlTypeDescriptor.INSTANCE, UUIDTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "pg-uuid";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    public static class PostgresUUIDSqlTypeDescriptor
    implements SqlTypeDescriptor {
        public static final PostgresUUIDSqlTypeDescriptor INSTANCE = new PostgresUUIDSqlTypeDescriptor();

        @Override
        public int getSqlType() {
            return 1111;
        }

        @Override
        public boolean canBeRemapped() {
            return true;
        }

        public BasicJavaDescriptor getJdbcRecommendedJavaTypeMapping(TypeConfiguration typeConfiguration) {
            return (BasicJavaDescriptor)typeConfiguration.getJavaTypeDescriptorRegistry().getDescriptor(UUID.class);
        }

        @Override
        public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    st.setObject(index, (Object)javaTypeDescriptor.unwrap(value, UUID.class, options), this.getSqlType());
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    st.setObject(name, (Object)javaTypeDescriptor.unwrap(value, UUID.class, options), this.getSqlType());
                }
            };
        }

        @Override
        public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicExtractor<X>(javaTypeDescriptor, this){

                @Override
                protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(rs.getObject(name), options);
                }

                @Override
                protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(statement.getObject(index), options);
                }

                @Override
                protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(statement.getObject(name), options);
                }
            };
        }
    }
}

