/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerStatementColumnEncryptionSetting;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;

public interface ISQLServerConnection
extends Connection {
    public static final int TRANSACTION_SNAPSHOT = 4096;

    public UUID getClientConnectionId() throws SQLServerException;

    public Statement createStatement(int var1, int var2, int var3, SQLServerStatementColumnEncryptionSetting var4) throws SQLServerException;

    public PreparedStatement prepareStatement(String var1, int var2, SQLServerStatementColumnEncryptionSetting var3) throws SQLServerException;

    public PreparedStatement prepareStatement(String var1, int[] var2, SQLServerStatementColumnEncryptionSetting var3) throws SQLServerException;

    public PreparedStatement prepareStatement(String var1, String[] var2, SQLServerStatementColumnEncryptionSetting var3) throws SQLServerException;

    public PreparedStatement prepareStatement(String var1, int var2, int var3, int var4, SQLServerStatementColumnEncryptionSetting var5) throws SQLServerException;

    public CallableStatement prepareCall(String var1, int var2, int var3, int var4, SQLServerStatementColumnEncryptionSetting var5) throws SQLServerException;

    public void setSendTimeAsDatetime(boolean var1) throws SQLServerException;

    public void setDatetimeParameterType(String var1) throws SQLServerException;

    public boolean getSendTimeAsDatetime() throws SQLServerException;

    public String getDatetimeParameterType() throws SQLServerException;

    public int getDiscardedServerPreparedStatementCount();

    public void closeUnreferencedPreparedStatementHandles();

    public boolean getEnablePrepareOnFirstPreparedStatementCall();

    public void setEnablePrepareOnFirstPreparedStatementCall(boolean var1);

    public String getPrepareMethod();

    public void setPrepareMethod(String var1);

    public int getServerPreparedStatementDiscardThreshold();

    public void setServerPreparedStatementDiscardThreshold(int var1);

    public void setStatementPoolingCacheSize(int var1);

    public int getStatementPoolingCacheSize();

    public boolean isStatementPoolingEnabled();

    public int getStatementHandleCacheEntryCount();

    public void setDisableStatementPooling(boolean var1);

    public boolean getDisableStatementPooling();

    public boolean getUseFmtOnly();

    public void setUseFmtOnly(boolean var1);

    public boolean getDelayLoadingLobs();

    public void setDelayLoadingLobs(boolean var1);

    public void setIPAddressPreference(String var1);

    public String getIPAddressPreference();

    @Deprecated(since="12.1.0", forRemoval=true)
    public int getMsiTokenCacheTtl();

    @Deprecated(since="12.1.0", forRemoval=true)
    public void setMsiTokenCacheTtl(int var1);

    public String getAccessTokenCallbackClass();

    public void setAccessTokenCallbackClass(String var1);
}

