/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.DTV;
import com.microsoft.sqlserver.jdbc.InputStreamGetterArgs;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.JavaType;
import com.microsoft.sqlserver.jdbc.SQLCollation;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SqlVariant;
import com.microsoft.sqlserver.jdbc.StreamSetterArgs;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import java.util.Calendar;

abstract class DTVImpl {
    DTVImpl() {
    }

    abstract void setValue(DTV var1, SQLCollation var2, JDBCType var3, Object var4, JavaType var5, StreamSetterArgs var6, Calendar var7, Integer var8, SQLServerConnection var9, boolean var10) throws SQLServerException;

    abstract void setValue(Object var1, JavaType var2);

    abstract void setStreamSetterArgs(StreamSetterArgs var1);

    abstract void setCalendar(Calendar var1);

    abstract void setScale(Integer var1);

    abstract void setForceEncrypt(boolean var1);

    abstract StreamSetterArgs getStreamSetterArgs();

    abstract Calendar getCalendar();

    abstract Integer getScale();

    abstract boolean isNull();

    abstract void setJdbcType(JDBCType var1);

    abstract JDBCType getJdbcType();

    abstract JavaType getJavaType();

    abstract Object getValue(DTV var1, JDBCType var2, int var3, InputStreamGetterArgs var4, Calendar var5, TypeInfo var6, CryptoMetadata var7, TDSReader var8, SQLServerStatement var9) throws SQLServerException;

    abstract Object getSetterValue();

    abstract void skipValue(TypeInfo var1, TDSReader var2, boolean var3) throws SQLServerException;

    abstract void initFromCompressedNull();

    abstract SqlVariant getInternalVariant();
}

