/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.type.descriptor.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLXML;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.mapping.Array;
import org.jboss.logging.Logger;

public class JdbcTypeJavaClassMappings {
    private static final Logger log = Logger.getLogger(JdbcTypeJavaClassMappings.class);
    public static final JdbcTypeJavaClassMappings INSTANCE = new JdbcTypeJavaClassMappings();
    private final ConcurrentHashMap<Class, Integer> javaClassToJdbcTypeCodeMap = JdbcTypeJavaClassMappings.buildJavaClassToJdbcTypeCodeMappings();
    private final ConcurrentHashMap<Integer, Class> jdbcTypeCodeToJavaClassMap = JdbcTypeJavaClassMappings.buildJdbcTypeCodeToJavaClassMappings();

    private JdbcTypeJavaClassMappings() {
    }

    public int determineJdbcTypeCodeForJavaClass(Class cls) {
        Integer typeCode = this.javaClassToJdbcTypeCodeMap.get(cls);
        if (typeCode != null) {
            return typeCode;
        }
        int specialCode = cls.hashCode();
        log.debug((Object)("JDBC type code mapping not known for class [" + cls.getName() + "]; using custom code [" + specialCode + "]"));
        return specialCode;
    }

    public Class determineJavaClassForJdbcTypeCode(Integer typeCode) {
        Class cls = this.jdbcTypeCodeToJavaClassMap.get(typeCode);
        if (cls != null) {
            return cls;
        }
        log.debugf("Java Class mapping not known for JDBC type code [%s]; using java.lang.Object", (Object)typeCode);
        return Object.class;
    }

    public Class determineJavaClassForJdbcTypeCode(int typeCode) {
        return this.determineJavaClassForJdbcTypeCode((Integer)typeCode);
    }

    private static ConcurrentHashMap<Class, Integer> buildJavaClassToJdbcTypeCodeMappings() {
        ConcurrentHashMap<Class, Integer> workMap = new ConcurrentHashMap<Class, Integer>();
        workMap.put(String.class, 12);
        workMap.put(BigDecimal.class, 2);
        workMap.put(BigInteger.class, 2);
        workMap.put(Boolean.class, -7);
        workMap.put(Byte.class, -6);
        workMap.put(Short.class, 5);
        workMap.put(Integer.class, 4);
        workMap.put(Long.class, -5);
        workMap.put(Float.class, 7);
        workMap.put(Double.class, 8);
        workMap.put(byte[].class, -4);
        workMap.put(Date.class, 91);
        workMap.put(Time.class, 92);
        workMap.put(Timestamp.class, 93);
        workMap.put(Blob.class, 2004);
        workMap.put(Clob.class, 2005);
        workMap.put(Array.class, 2003);
        workMap.put(Struct.class, 2002);
        workMap.put(Ref.class, 2006);
        workMap.put(Class.class, 2000);
        workMap.put(RowId.class, -8);
        workMap.put(SQLXML.class, 2009);
        workMap.put(Character.class, 1);
        workMap.put(char[].class, 12);
        workMap.put(Character[].class, 12);
        workMap.put(Byte[].class, -4);
        workMap.put(java.util.Date.class, 93);
        workMap.put(Calendar.class, 93);
        return workMap;
    }

    private static ConcurrentHashMap<Integer, Class> buildJdbcTypeCodeToJavaClassMappings() {
        ConcurrentHashMap<Integer, Class> workMap = new ConcurrentHashMap<Integer, Class>();
        workMap.put(1, String.class);
        workMap.put(12, String.class);
        workMap.put(-1, String.class);
        workMap.put(-15, String.class);
        workMap.put(-9, String.class);
        workMap.put(-16, String.class);
        workMap.put(2, BigDecimal.class);
        workMap.put(3, BigDecimal.class);
        workMap.put(-7, Boolean.class);
        workMap.put(16, Boolean.class);
        workMap.put(-6, Byte.class);
        workMap.put(5, Short.class);
        workMap.put(4, Integer.class);
        workMap.put(-5, Long.class);
        workMap.put(7, Float.class);
        workMap.put(8, Double.class);
        workMap.put(6, Double.class);
        workMap.put(-2, byte[].class);
        workMap.put(-3, byte[].class);
        workMap.put(-4, byte[].class);
        workMap.put(91, Date.class);
        workMap.put(92, Time.class);
        workMap.put(93, Timestamp.class);
        workMap.put(2004, Blob.class);
        workMap.put(2005, Clob.class);
        workMap.put(2011, NClob.class);
        workMap.put(2003, Array.class);
        workMap.put(2002, Struct.class);
        workMap.put(2006, Ref.class);
        workMap.put(2000, Class.class);
        workMap.put(-8, RowId.class);
        workMap.put(2009, SQLXML.class);
        return workMap;
    }
}

