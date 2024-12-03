/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public final class JdbcTypeNameMapper {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(JdbcTypeNameMapper.class);
    private static Map<Integer, String> JDBC_TYPE_MAP = JdbcTypeNameMapper.buildJdbcTypeMap();

    private static Map<Integer, String> buildJdbcTypeMap() {
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        Field[] fields = Types.class.getFields();
        if (fields == null) {
            throw new HibernateException("Unexpected problem extracting JDBC type mapping codes from java.sql.Types");
        }
        for (Field field : fields) {
            try {
                int code = field.getInt(null);
                String old = map.put(code, field.getName());
                if (old == null) continue;
                LOG.JavaSqlTypesMappedSameCodeMultipleTimes(code, old, field.getName());
            }
            catch (IllegalAccessException e) {
                throw new HibernateException("Unable to access JDBC type mapping [" + field.getName() + "]", e);
            }
        }
        return Collections.unmodifiableMap(map);
    }

    public static boolean isStandardTypeCode(int typeCode) {
        return JdbcTypeNameMapper.isStandardTypeCode((Integer)typeCode);
    }

    public static boolean isStandardTypeCode(Integer typeCode) {
        return JDBC_TYPE_MAP.containsKey(typeCode);
    }

    public static String getTypeName(Integer typeCode) {
        String name = JDBC_TYPE_MAP.get(typeCode);
        if (name == null) {
            return "UNKNOWN(" + typeCode + ")";
        }
        return name;
    }

    private JdbcTypeNameMapper() {
    }
}

