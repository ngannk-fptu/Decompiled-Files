/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerAccessTokenCallback;
import javax.sql.CommonDataSource;
import org.ietf.jgss.GSSCredential;

public interface ISQLServerDataSource
extends CommonDataSource {
    public void setApplicationIntent(String var1);

    public String getApplicationIntent();

    public void setApplicationName(String var1);

    public String getApplicationName();

    public void setDatabaseName(String var1);

    public String getDatabaseName();

    public void setInstanceName(String var1);

    public String getInstanceName();

    public void setIntegratedSecurity(boolean var1);

    public void setLastUpdateCount(boolean var1);

    public boolean getLastUpdateCount();

    public void setEncrypt(String var1);

    @Deprecated(since="10.1.0", forRemoval=true)
    public void setEncrypt(boolean var1);

    public String getEncrypt();

    public String getServerCertificate();

    public void setServerCertificate(String var1);

    public void setTransparentNetworkIPResolution(boolean var1);

    public boolean getTransparentNetworkIPResolution();

    public void setTrustServerCertificate(boolean var1);

    public boolean getTrustServerCertificate();

    public void setTrustStoreType(String var1);

    public String getTrustStoreType();

    public void setTrustStore(String var1);

    public String getTrustStore();

    public void setTrustStorePassword(String var1);

    public void setHostNameInCertificate(String var1);

    public String getHostNameInCertificate();

    public void setLockTimeout(int var1);

    public int getLockTimeout();

    public void setPassword(String var1);

    public void setPortNumber(int var1);

    public int getPortNumber();

    public void setSelectMethod(String var1);

    public String getSelectMethod();

    public void setResponseBuffering(String var1);

    public String getResponseBuffering();

    public void setReplication(boolean var1);

    public boolean getReplication();

    public void setSendTimeAsDatetime(boolean var1);

    public boolean getSendTimeAsDatetime();

    public void setDatetimeParameterType(String var1);

    public String getDatetimeParameterType();

    public void setSendStringParametersAsUnicode(boolean var1);

    public boolean getSendStringParametersAsUnicode();

    public void setServerNameAsACE(boolean var1);

    public boolean getServerNameAsACE();

    public void setServerName(String var1);

    public String getServerName();

    public void setIPAddressPreference(String var1);

    public String getIPAddressPreference();

    public void setFailoverPartner(String var1);

    public String getFailoverPartner();

    public void setMultiSubnetFailover(boolean var1);

    public boolean getMultiSubnetFailover();

    public void setUser(String var1);

    public String getUser();

    public void setWorkstationID(String var1);

    public String getWorkstationID();

    public void setXopenStates(boolean var1);

    public boolean getXopenStates();

    public void setURL(String var1);

    public String getURL();

    public void setDescription(String var1);

    public String getDescription();

    public void setPacketSize(int var1);

    public int getPacketSize();

    public void setAuthenticationScheme(String var1);

    public void setAuthentication(String var1);

    public String getAuthentication();

    public void setRealm(String var1);

    public String getRealm();

    public void setServerSpn(String var1);

    public String getServerSpn();

    public void setGSSCredentials(GSSCredential var1);

    public GSSCredential getGSSCredentials();

    public void setAccessToken(String var1);

    public String getAccessToken();

    public void setColumnEncryptionSetting(String var1);

    public String getColumnEncryptionSetting();

    public void setKeyStoreAuthentication(String var1);

    public String getKeyStoreAuthentication();

    public void setKeyStoreSecret(String var1);

    public void setKeyStoreLocation(String var1);

    public String getKeyStoreLocation();

    public void setQueryTimeout(int var1);

    public int getQueryTimeout();

    public void setCancelQueryTimeout(int var1);

    public int getCancelQueryTimeout();

    public void setEnablePrepareOnFirstPreparedStatementCall(boolean var1);

    public boolean getEnablePrepareOnFirstPreparedStatementCall();

    public void setServerPreparedStatementDiscardThreshold(int var1);

    public int getServerPreparedStatementDiscardThreshold();

    public void setStatementPoolingCacheSize(int var1);

    public int getStatementPoolingCacheSize();

    public void setDisableStatementPooling(boolean var1);

    public boolean getDisableStatementPooling();

    public void setSocketTimeout(int var1);

    public int getSocketTimeout();

    @Deprecated(since="9.3.0", forRemoval=true)
    public void setJASSConfigurationName(String var1);

    @Deprecated(since="9.3.0", forRemoval=true)
    public String getJASSConfigurationName();

    public void setJAASConfigurationName(String var1);

    public String getJAASConfigurationName();

    public void setFIPS(boolean var1);

    public boolean getFIPS();

    public void setSSLProtocol(String var1);

    public String getSSLProtocol();

    public String getSocketFactoryClass();

    public void setSocketFactoryClass(String var1);

    public String getSocketFactoryConstructorArg();

    public void setSocketFactoryConstructorArg(String var1);

    public void setTrustManagerClass(String var1);

    public String getTrustManagerClass();

    public void setTrustManagerConstructorArg(String var1);

    public String getTrustManagerConstructorArg();

    public boolean getUseBulkCopyForBatchInsert();

    public void setUseBulkCopyForBatchInsert(boolean var1);

    @Deprecated(since="12.1.0", forRemoval=true)
    public void setMSIClientId(String var1);

    @Deprecated(since="12.1.0", forRemoval=true)
    public String getMSIClientId();

    public void setKeyStorePrincipalId(String var1);

    public String getKeyStorePrincipalId();

    public void setKeyVaultProviderClientId(String var1);

    public String getKeyVaultProviderClientId();

    public void setKeyVaultProviderClientKey(String var1);

    public String getDomain();

    public void setDomain(String var1);

    public boolean getUseFmtOnly();

    public void setUseFmtOnly(boolean var1);

    public String getEnclaveAttestationUrl();

    public void setEnclaveAttestationUrl(String var1);

    public String getEnclaveAttestationProtocol();

    public void setEnclaveAttestationProtocol(String var1);

    public String getClientCertificate();

    public void setClientCertificate(String var1);

    public String getClientKey();

    public void setClientKey(String var1);

    public void setClientKeyPassword(String var1);

    public void setDelayLoadingLobs(boolean var1);

    public boolean getDelayLoadingLobs();

    public boolean getSendTemporalDataTypesAsStringForBulkCopy();

    public void setSendTemporalDataTypesAsStringForBulkCopy(boolean var1);

    @Deprecated(since="9.4.1", forRemoval=true)
    public String getAADSecurePrincipalId();

    @Deprecated(since="9.4.1", forRemoval=true)
    public void setAADSecurePrincipalId(String var1);

    @Deprecated(since="9.4.1", forRemoval=true)
    public void setAADSecurePrincipalSecret(String var1);

    public String getMaxResultBuffer();

    public void setMaxResultBuffer(String var1);

    public void setConnectRetryCount(int var1);

    public int getConnectRetryCount();

    public void setConnectRetryInterval(int var1);

    public int getConnectRetryInterval();

    public void setPrepareMethod(String var1);

    public String getPrepareMethod();

    @Deprecated(since="12.1.0", forRemoval=true)
    public void setMsiTokenCacheTtl(int var1);

    @Deprecated(since="12.1.0", forRemoval=true)
    public int getMsiTokenCacheTtl();

    public void setAccessTokenCallback(SQLServerAccessTokenCallback var1);

    public SQLServerAccessTokenCallback getAccessTokenCallback();

    public String getAccessTokenCallbackClass();

    public void setAccessTokenCallbackClass(String var1);
}

