/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.type.descriptor.sql;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.type.descriptor.JdbcTypeNameMapper;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.BigIntTypeDescriptor;
import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor;
import org.hibernate.type.descriptor.sql.BitTypeDescriptor;
import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
import org.hibernate.type.descriptor.sql.BooleanTypeDescriptor;
import org.hibernate.type.descriptor.sql.CharTypeDescriptor;
import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
import org.hibernate.type.descriptor.sql.DateTypeDescriptor;
import org.hibernate.type.descriptor.sql.DecimalTypeDescriptor;
import org.hibernate.type.descriptor.sql.DoubleTypeDescriptor;
import org.hibernate.type.descriptor.sql.FloatTypeDescriptor;
import org.hibernate.type.descriptor.sql.IntegerTypeDescriptor;
import org.hibernate.type.descriptor.sql.JdbcTypeFamilyInformation;
import org.hibernate.type.descriptor.sql.LongNVarcharTypeDescriptor;
import org.hibernate.type.descriptor.sql.LongVarbinaryTypeDescriptor;
import org.hibernate.type.descriptor.sql.LongVarcharTypeDescriptor;
import org.hibernate.type.descriptor.sql.NCharTypeDescriptor;
import org.hibernate.type.descriptor.sql.NClobTypeDescriptor;
import org.hibernate.type.descriptor.sql.NVarcharTypeDescriptor;
import org.hibernate.type.descriptor.sql.NumericTypeDescriptor;
import org.hibernate.type.descriptor.sql.RealTypeDescriptor;
import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.descriptor.sql.TimeTypeDescriptor;
import org.hibernate.type.descriptor.sql.TimestampTypeDescriptor;
import org.hibernate.type.descriptor.sql.TinyIntTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarbinaryTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;
import org.jboss.logging.Logger;

@Deprecated
public class SqlTypeDescriptorRegistry
implements Serializable {
    @Deprecated
    public static final SqlTypeDescriptorRegistry INSTANCE = new SqlTypeDescriptorRegistry();
    private static final Logger log = Logger.getLogger(SqlTypeDescriptorRegistry.class);
    private ConcurrentHashMap<Integer, SqlTypeDescriptor> descriptorMap = new ConcurrentHashMap();

    protected SqlTypeDescriptorRegistry() {
        this.addDescriptorInternal(BooleanTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(BitTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(BigIntTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(DecimalTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(DoubleTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(FloatTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(IntegerTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(NumericTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(RealTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(SmallIntTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(TinyIntTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(DateTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(TimestampTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(TimeTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(BinaryTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(VarbinaryTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(LongVarbinaryTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(BlobTypeDescriptor.DEFAULT);
        this.addDescriptorInternal(CharTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(VarcharTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(LongVarcharTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(ClobTypeDescriptor.DEFAULT);
        this.addDescriptorInternal(NCharTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(NVarcharTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(LongNVarcharTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(NClobTypeDescriptor.DEFAULT);
    }

    @Deprecated
    public void addDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
        this.descriptorMap.put(sqlTypeDescriptor.getSqlType(), sqlTypeDescriptor);
    }

    private void addDescriptorInternal(SqlTypeDescriptor sqlTypeDescriptor) {
        this.descriptorMap.put(sqlTypeDescriptor.getSqlType(), sqlTypeDescriptor);
    }

    @Deprecated
    public SqlTypeDescriptor getDescriptor(int jdbcTypeCode) {
        JdbcTypeFamilyInformation.Family family;
        SqlTypeDescriptor descriptor = this.descriptorMap.get(jdbcTypeCode);
        if (descriptor != null) {
            return descriptor;
        }
        if (JdbcTypeNameMapper.isStandardTypeCode(jdbcTypeCode)) {
            log.debugf("A standard JDBC type code [%s] was not defined in SqlTypeDescriptorRegistry", jdbcTypeCode);
        }
        if ((family = JdbcTypeFamilyInformation.INSTANCE.locateJdbcTypeFamilyByTypeCode(jdbcTypeCode)) != null) {
            for (int potentialAlternateTypeCode : family.getTypeCodes()) {
                if (potentialAlternateTypeCode == jdbcTypeCode) continue;
                SqlTypeDescriptor potentialAlternateDescriptor = this.descriptorMap.get(potentialAlternateTypeCode);
                if (potentialAlternateDescriptor != null) {
                    return potentialAlternateDescriptor;
                }
                if (!JdbcTypeNameMapper.isStandardTypeCode(potentialAlternateTypeCode)) continue;
                log.debugf("A standard JDBC type code [%s] was not defined in SqlTypeDescriptorRegistry", potentialAlternateTypeCode);
            }
        }
        ObjectSqlTypeDescriptor fallBackDescriptor = new ObjectSqlTypeDescriptor(jdbcTypeCode);
        this.addDescriptor(fallBackDescriptor);
        return fallBackDescriptor;
    }

    public static class ObjectSqlTypeDescriptor
    implements SqlTypeDescriptor {
        private final int jdbcTypeCode;

        public ObjectSqlTypeDescriptor(int jdbcTypeCode) {
            this.jdbcTypeCode = jdbcTypeCode;
        }

        @Override
        public int getSqlType() {
            return this.jdbcTypeCode;
        }

        @Override
        public boolean canBeRemapped() {
            return true;
        }

        @Override
        public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
            if (Serializable.class.isAssignableFrom(javaTypeDescriptor.getJavaType())) {
                return VarbinaryTypeDescriptor.INSTANCE.getBinder(javaTypeDescriptor);
            }
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    st.setObject(index, value, jdbcTypeCode);
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    st.setObject(name, value, jdbcTypeCode);
                }
            };
        }

        public ValueExtractor getExtractor(JavaTypeDescriptor javaTypeDescriptor) {
            if (Serializable.class.isAssignableFrom(javaTypeDescriptor.getJavaType())) {
                return VarbinaryTypeDescriptor.INSTANCE.getExtractor(javaTypeDescriptor);
            }
            return new BasicExtractor(javaTypeDescriptor, this){

                protected Object doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                    return rs.getObject(name);
                }

                protected Object doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                    return statement.getObject(index);
                }

                protected Object doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                    return statement.getObject(name);
                }
            };
        }
    }
}

