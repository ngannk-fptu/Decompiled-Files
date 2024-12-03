/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.DriverJDBCVersion;
import com.microsoft.sqlserver.jdbc.ISQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerAccessTokenCallback;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.microsoft.sqlserver.jdbc.SQLServerDriverBooleanProperty;
import com.microsoft.sqlserver.jdbc.SQLServerDriverIntProperty;
import com.microsoft.sqlserver.jdbc.SQLServerDriverObjectProperty;
import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerPooledConnection;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;
import org.ietf.jgss.GSSCredential;

public class SQLServerDataSource
implements ISQLServerDataSource,
DataSource,
Serializable,
Referenceable {
    static final Logger dsLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerDataSource");
    static final Logger loggerExternal = Logger.getLogger("com.microsoft.sqlserver.jdbc.DataSource");
    private static final Logger parentLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc");
    static final String TRUSTSTORE_PASSWORD_STRIPPED = "trustStorePasswordStripped";
    private final String loggingClassName;
    private boolean trustStorePasswordStripped = false;
    private static final long serialVersionUID = 654861379544314296L;
    private Properties connectionProps = new Properties();
    private String dataSourceURL;
    private String dataSourceDescription;
    private static final AtomicInteger baseDataSourceID = new AtomicInteger(0);
    private final String traceID = this.getClass().getSimpleName() + ":" + SQLServerDataSource.nextDataSourceID();
    private transient PrintWriter logWriter;

    public SQLServerDataSource() {
        this.loggingClassName = "com.microsoft.sqlserver.jdbc." + this.traceID;
    }

    String getClassNameLogging() {
        return this.loggingClassName;
    }

    public String toString() {
        return this.traceID;
    }

    @Override
    public Connection getConnection() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getConnection");
        SQLServerConnection con = this.getConnectionInternal(null, null, null);
        loggerExternal.exiting(this.getClassNameLogging(), "getConnection", con);
        return con;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getConnection", new Object[]{username, "Password not traced"});
        }
        SQLServerConnection con = this.getConnectionInternal(username, password, null);
        loggerExternal.exiting(this.getClassNameLogging(), "getConnection", con);
        return con;
    }

    @Override
    public void setLoginTimeout(int loginTimeout) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.LOGIN_TIMEOUT.toString(), loginTimeout);
    }

    @Override
    public int getLoginTimeout() {
        int defaultTimeOut = SQLServerDriverIntProperty.LOGIN_TIMEOUT.getDefaultValue();
        int logintimeout = this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.LOGIN_TIMEOUT.toString(), defaultTimeOut);
        return logintimeout == 0 ? defaultTimeOut : logintimeout;
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        loggerExternal.entering(this.getClassNameLogging(), "setLogWriter", out);
        this.logWriter = out;
        loggerExternal.exiting(this.getClassNameLogging(), "setLogWriter");
    }

    @Override
    public PrintWriter getLogWriter() {
        loggerExternal.entering(this.getClassNameLogging(), "getLogWriter");
        loggerExternal.exiting(this.getClassNameLogging(), "getLogWriter", this.logWriter);
        return this.logWriter;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return parentLogger;
    }

    @Override
    public void setApplicationName(String applicationName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.APPLICATION_NAME.toString(), applicationName);
    }

    @Override
    public String getApplicationName() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.APPLICATION_NAME.toString(), SQLServerDriverStringProperty.APPLICATION_NAME.getDefaultValue());
    }

    @Override
    public void setDatabaseName(String databaseName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.DATABASE_NAME.toString(), databaseName);
    }

    @Override
    public String getDatabaseName() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.DATABASE_NAME.toString(), null);
    }

    @Override
    public void setInstanceName(String instanceName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.INSTANCE_NAME.toString(), instanceName);
    }

    @Override
    public String getInstanceName() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.INSTANCE_NAME.toString(), null);
    }

    @Override
    public void setIntegratedSecurity(boolean enable) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.INTEGRATED_SECURITY.toString(), enable);
    }

    @Override
    public void setAuthenticationScheme(String authenticationScheme) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.AUTHENTICATION_SCHEME.toString(), authenticationScheme);
    }

    @Override
    public void setAuthentication(String authentication) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.AUTHENTICATION.toString(), authentication);
    }

    @Override
    public String getAuthentication() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.AUTHENTICATION.toString(), SQLServerDriverStringProperty.AUTHENTICATION.getDefaultValue());
    }

    @Override
    public void setGSSCredentials(GSSCredential userCredential) {
        this.setObjectProperty(this.connectionProps, SQLServerDriverObjectProperty.GSS_CREDENTIAL.toString(), userCredential);
    }

    @Override
    public GSSCredential getGSSCredentials() {
        return (GSSCredential)this.getObjectProperty(this.connectionProps, SQLServerDriverObjectProperty.GSS_CREDENTIAL.toString(), SQLServerDriverObjectProperty.GSS_CREDENTIAL.getDefaultValue());
    }

    @Override
    public void setAccessToken(String accessToken) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.ACCESS_TOKEN.toString(), accessToken);
    }

    @Override
    public String getAccessToken() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.ACCESS_TOKEN.toString(), null);
    }

    @Override
    public void setColumnEncryptionSetting(String columnEncryptionSetting) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.COLUMN_ENCRYPTION.toString(), columnEncryptionSetting);
    }

    @Override
    public String getColumnEncryptionSetting() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.COLUMN_ENCRYPTION.toString(), SQLServerDriverStringProperty.COLUMN_ENCRYPTION.getDefaultValue());
    }

    @Override
    public void setKeyStoreAuthentication(String keyStoreAuthentication) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_STORE_AUTHENTICATION.toString(), keyStoreAuthentication);
    }

    @Override
    public String getKeyStoreAuthentication() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_STORE_AUTHENTICATION.toString(), SQLServerDriverStringProperty.KEY_STORE_AUTHENTICATION.getDefaultValue());
    }

    @Override
    public void setKeyStoreSecret(String keyStoreSecret) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_STORE_SECRET.toString(), keyStoreSecret);
    }

    @Override
    public void setKeyStoreLocation(String keyStoreLocation) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_STORE_LOCATION.toString(), keyStoreLocation);
    }

    @Override
    public String getKeyStoreLocation() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_STORE_LOCATION.toString(), SQLServerDriverStringProperty.KEY_STORE_LOCATION.getDefaultValue());
    }

    @Override
    public void setLastUpdateCount(boolean lastUpdateCount) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.toString(), lastUpdateCount);
    }

    @Override
    public boolean getLastUpdateCount() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.toString(), SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.getDefaultValue());
    }

    @Override
    public void setEncrypt(String encryptOption) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.ENCRYPT.toString(), encryptOption);
    }

    @Override
    @Deprecated(since="10.1.0", forRemoval=true)
    public void setEncrypt(boolean encryptOption) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.ENCRYPT.toString(), Boolean.toString(encryptOption));
    }

    @Override
    public String getEncrypt() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.ENCRYPT.toString(), SQLServerDriverStringProperty.ENCRYPT.getDefaultValue());
    }

    @Override
    public void setServerCertificate(String cert) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.SERVER_CERTIFICATE.toString(), cert);
    }

    @Override
    public String getServerCertificate() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.SERVER_CERTIFICATE.toString(), null);
    }

    @Override
    public void setTransparentNetworkIPResolution(boolean tnir) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.toString(), tnir);
    }

    @Override
    public boolean getTransparentNetworkIPResolution() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.toString(), SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.getDefaultValue());
    }

    @Override
    public void setTrustServerCertificate(boolean e) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.toString(), e);
    }

    @Override
    public boolean getTrustServerCertificate() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.toString(), SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.getDefaultValue());
    }

    @Override
    public void setTrustStoreType(String trustStoreType) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_STORE_TYPE.toString(), trustStoreType);
    }

    @Override
    public String getTrustStoreType() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_STORE_TYPE.toString(), SQLServerDriverStringProperty.TRUST_STORE_TYPE.getDefaultValue());
    }

    @Override
    public void setTrustStore(String trustStore) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_STORE.toString(), trustStore);
    }

    @Override
    public String getTrustStore() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_STORE.toString(), null);
    }

    @Override
    public void setTrustStorePassword(String trustStorePassword) {
        if (trustStorePassword != null) {
            this.trustStorePasswordStripped = false;
        }
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString(), trustStorePassword);
    }

    String getTrustStorePassword() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString(), null);
    }

    @Override
    public void setHostNameInCertificate(String hostName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString(), hostName);
    }

    @Override
    public String getHostNameInCertificate() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString(), null);
    }

    @Override
    public void setLockTimeout(int lockTimeout) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.LOCK_TIMEOUT.toString(), lockTimeout);
    }

    @Override
    public int getLockTimeout() {
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.LOCK_TIMEOUT.toString(), SQLServerDriverIntProperty.LOCK_TIMEOUT.getDefaultValue());
    }

    @Override
    public void setPassword(String password) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.PASSWORD.toString(), password);
    }

    String getPassword() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.PASSWORD.toString(), null);
    }

    @Override
    public void setPortNumber(int portNumber) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.PORT_NUMBER.toString(), portNumber);
    }

    @Override
    public int getPortNumber() {
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.PORT_NUMBER.toString(), SQLServerDriverIntProperty.PORT_NUMBER.getDefaultValue());
    }

    @Override
    public void setSelectMethod(String selectMethod) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.SELECT_METHOD.toString(), selectMethod);
    }

    @Override
    public String getSelectMethod() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.SELECT_METHOD.toString(), SQLServerDriverStringProperty.SELECT_METHOD.getDefaultValue());
    }

    @Override
    public void setResponseBuffering(String bufferingMode) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.RESPONSE_BUFFERING.toString(), bufferingMode);
    }

    @Override
    public String getResponseBuffering() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.RESPONSE_BUFFERING.toString(), SQLServerDriverStringProperty.RESPONSE_BUFFERING.getDefaultValue());
    }

    @Override
    public void setApplicationIntent(String applicationIntent) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.APPLICATION_INTENT.toString(), applicationIntent);
    }

    @Override
    public String getApplicationIntent() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.APPLICATION_INTENT.toString(), SQLServerDriverStringProperty.APPLICATION_INTENT.getDefaultValue());
    }

    @Override
    public void setReplication(boolean replication) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.REPLICATION.toString(), replication);
    }

    @Override
    public boolean getReplication() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.REPLICATION.toString(), SQLServerDriverBooleanProperty.REPLICATION.getDefaultValue());
    }

    @Override
    public void setSendTimeAsDatetime(boolean sendTimeAsDatetime) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.toString(), sendTimeAsDatetime);
    }

    @Override
    public boolean getSendTimeAsDatetime() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.toString(), SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.getDefaultValue());
    }

    @Override
    public void setDatetimeParameterType(String datetimeParameterType) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.DATETIME_DATATYPE.toString(), datetimeParameterType);
    }

    @Override
    public String getDatetimeParameterType() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.DATETIME_DATATYPE.toString(), SQLServerDriverStringProperty.DATETIME_DATATYPE.getDefaultValue());
    }

    @Override
    public void setUseFmtOnly(boolean useFmtOnly) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.USE_FMT_ONLY.toString(), useFmtOnly);
    }

    @Override
    public boolean getUseFmtOnly() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.USE_FMT_ONLY.toString(), SQLServerDriverBooleanProperty.USE_FMT_ONLY.getDefaultValue());
    }

    @Override
    public void setDelayLoadingLobs(boolean delayLoadingLobs) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.DELAY_LOADING_LOBS.toString(), delayLoadingLobs);
    }

    @Override
    public boolean getDelayLoadingLobs() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.DELAY_LOADING_LOBS.toString(), SQLServerDriverBooleanProperty.DELAY_LOADING_LOBS.getDefaultValue());
    }

    @Override
    public void setSendStringParametersAsUnicode(boolean sendStringParametersAsUnicode) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.toString(), sendStringParametersAsUnicode);
    }

    @Override
    public boolean getSendStringParametersAsUnicode() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.toString(), SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.getDefaultValue());
    }

    @Override
    public void setServerNameAsACE(boolean serverNameAsACE) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.toString(), serverNameAsACE);
    }

    @Override
    public boolean getServerNameAsACE() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.toString(), SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.getDefaultValue());
    }

    @Override
    public void setServerName(String serverName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.SERVER_NAME.toString(), serverName);
    }

    @Override
    public String getServerName() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.SERVER_NAME.toString(), null);
    }

    @Override
    public void setIPAddressPreference(String iPAddressPreference) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.IPADDRESS_PREFERENCE.toString(), iPAddressPreference);
    }

    @Override
    public String getIPAddressPreference() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.IPADDRESS_PREFERENCE.toString(), SQLServerDriverStringProperty.IPADDRESS_PREFERENCE.getDefaultValue());
    }

    @Override
    public void setRealm(String realm) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.REALM.toString(), realm);
    }

    @Override
    public String getRealm() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.REALM.toString(), null);
    }

    @Override
    public void setServerSpn(String serverSpn) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.SERVER_SPN.toString(), serverSpn);
    }

    @Override
    public String getServerSpn() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.SERVER_SPN.toString(), null);
    }

    @Override
    public void setFailoverPartner(String serverName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.FAILOVER_PARTNER.toString(), serverName);
    }

    @Override
    public String getFailoverPartner() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.FAILOVER_PARTNER.toString(), null);
    }

    @Override
    public void setMultiSubnetFailover(boolean multiSubnetFailover) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.toString(), multiSubnetFailover);
    }

    @Override
    public boolean getMultiSubnetFailover() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.toString(), SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.getDefaultValue());
    }

    @Override
    public void setUser(String user) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.USER.toString(), user);
    }

    @Override
    public String getUser() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.USER.toString(), null);
    }

    @Override
    public void setWorkstationID(String workstationID) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.WORKSTATION_ID.toString(), workstationID);
    }

    @Override
    public String getWorkstationID() {
        String getWSID;
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "getWorkstationID");
        }
        if (null == (getWSID = this.connectionProps.getProperty(SQLServerDriverStringProperty.WORKSTATION_ID.toString()))) {
            getWSID = Util.lookupHostName();
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getWorkstationID", getWSID);
        return getWSID;
    }

    @Override
    public void setXopenStates(boolean xopenStates) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.XOPEN_STATES.toString(), xopenStates);
    }

    @Override
    public boolean getXopenStates() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.XOPEN_STATES.toString(), SQLServerDriverBooleanProperty.XOPEN_STATES.getDefaultValue());
    }

    @Override
    public void setFIPS(boolean fips) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.FIPS.toString(), fips);
    }

    @Override
    public boolean getFIPS() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.FIPS.toString(), SQLServerDriverBooleanProperty.FIPS.getDefaultValue());
    }

    @Override
    public String getSocketFactoryClass() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.SOCKET_FACTORY_CLASS.toString(), null);
    }

    @Override
    public void setSocketFactoryClass(String socketFactoryClass) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.SOCKET_FACTORY_CLASS.toString(), socketFactoryClass);
    }

    @Override
    public String getSocketFactoryConstructorArg() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.SOCKET_FACTORY_CONSTRUCTOR_ARG.toString(), null);
    }

    @Override
    public void setSocketFactoryConstructorArg(String socketFactoryConstructorArg) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.SOCKET_FACTORY_CONSTRUCTOR_ARG.toString(), socketFactoryConstructorArg);
    }

    @Override
    public void setSSLProtocol(String sslProtocol) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.SSL_PROTOCOL.toString(), sslProtocol);
    }

    @Override
    public String getSSLProtocol() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.SSL_PROTOCOL.toString(), SQLServerDriverStringProperty.SSL_PROTOCOL.getDefaultValue());
    }

    @Override
    public void setTrustManagerClass(String trustManagerClass) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_MANAGER_CLASS.toString(), trustManagerClass);
    }

    @Override
    public String getTrustManagerClass() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_MANAGER_CLASS.toString(), SQLServerDriverStringProperty.TRUST_MANAGER_CLASS.getDefaultValue());
    }

    @Override
    public void setTrustManagerConstructorArg(String trustManagerConstructorArg) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_MANAGER_CONSTRUCTOR_ARG.toString(), trustManagerConstructorArg);
    }

    @Override
    public String getTrustManagerConstructorArg() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_MANAGER_CONSTRUCTOR_ARG.toString(), SQLServerDriverStringProperty.TRUST_MANAGER_CONSTRUCTOR_ARG.getDefaultValue());
    }

    @Override
    public void setURL(String url) {
        loggerExternal.entering(this.getClassNameLogging(), "setURL", url);
        this.dataSourceURL = url;
        loggerExternal.exiting(this.getClassNameLogging(), "setURL");
    }

    @Override
    public String getURL() {
        String url = this.dataSourceURL;
        loggerExternal.entering(this.getClassNameLogging(), "getURL");
        if (null == this.dataSourceURL) {
            url = "jdbc:sqlserver://";
        }
        loggerExternal.exiting(this.getClassNameLogging(), "getURL", url);
        return url;
    }

    @Override
    public void setDescription(String description) {
        loggerExternal.entering(this.getClassNameLogging(), "setDescription", description);
        this.dataSourceDescription = description;
        loggerExternal.exiting(this.getClassNameLogging(), "setDescription");
    }

    @Override
    public String getDescription() {
        loggerExternal.entering(this.getClassNameLogging(), "getDescription");
        loggerExternal.exiting(this.getClassNameLogging(), "getDescription", this.dataSourceDescription);
        return this.dataSourceDescription;
    }

    @Override
    public void setPacketSize(int packetSize) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.PACKET_SIZE.toString(), packetSize);
    }

    @Override
    public int getPacketSize() {
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.PACKET_SIZE.toString(), SQLServerDriverIntProperty.PACKET_SIZE.getDefaultValue());
    }

    @Override
    public void setQueryTimeout(int queryTimeout) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.QUERY_TIMEOUT.toString(), queryTimeout);
    }

    @Override
    public int getQueryTimeout() {
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.QUERY_TIMEOUT.toString(), SQLServerDriverIntProperty.QUERY_TIMEOUT.getDefaultValue());
    }

    @Override
    public void setCancelQueryTimeout(int cancelQueryTimeout) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.toString(), cancelQueryTimeout);
    }

    @Override
    public int getCancelQueryTimeout() {
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.toString(), SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.getDefaultValue());
    }

    @Override
    public void setEnablePrepareOnFirstPreparedStatementCall(boolean enablePrepareOnFirstPreparedStatementCall) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT.toString(), enablePrepareOnFirstPreparedStatementCall);
    }

    @Override
    public boolean getEnablePrepareOnFirstPreparedStatementCall() {
        boolean defaultValue = SQLServerDriverBooleanProperty.ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT.getDefaultValue();
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT.toString(), defaultValue);
    }

    @Override
    public void setServerPreparedStatementDiscardThreshold(int serverPreparedStatementDiscardThreshold) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD.toString(), serverPreparedStatementDiscardThreshold);
    }

    @Override
    public int getServerPreparedStatementDiscardThreshold() {
        int defaultSize = SQLServerDriverIntProperty.SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD.getDefaultValue();
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD.toString(), defaultSize);
    }

    @Override
    public void setStatementPoolingCacheSize(int statementPoolingCacheSize) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.STATEMENT_POOLING_CACHE_SIZE.toString(), statementPoolingCacheSize);
    }

    @Override
    public int getStatementPoolingCacheSize() {
        int defaultSize = SQLServerDriverIntProperty.STATEMENT_POOLING_CACHE_SIZE.getDefaultValue();
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.STATEMENT_POOLING_CACHE_SIZE.toString(), defaultSize);
    }

    @Override
    public void setDisableStatementPooling(boolean disableStatementPooling) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.DISABLE_STATEMENT_POOLING.toString(), disableStatementPooling);
    }

    @Override
    public boolean getDisableStatementPooling() {
        boolean defaultValue = SQLServerDriverBooleanProperty.DISABLE_STATEMENT_POOLING.getDefaultValue();
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.DISABLE_STATEMENT_POOLING.toString(), defaultValue);
    }

    @Override
    public void setSocketTimeout(int socketTimeout) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.SOCKET_TIMEOUT.toString(), socketTimeout);
    }

    @Override
    public int getSocketTimeout() {
        int defaultTimeOut = SQLServerDriverIntProperty.SOCKET_TIMEOUT.getDefaultValue();
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.SOCKET_TIMEOUT.toString(), defaultTimeOut);
    }

    @Override
    public void setUseBulkCopyForBatchInsert(boolean useBulkCopyForBatchInsert) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.USE_BULK_COPY_FOR_BATCH_INSERT.toString(), useBulkCopyForBatchInsert);
    }

    @Override
    public boolean getUseBulkCopyForBatchInsert() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.USE_BULK_COPY_FOR_BATCH_INSERT.toString(), SQLServerDriverBooleanProperty.USE_BULK_COPY_FOR_BATCH_INSERT.getDefaultValue());
    }

    @Override
    @Deprecated(since="9.3.0", forRemoval=true)
    public void setJASSConfigurationName(String configurationName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.JAAS_CONFIG_NAME.toString(), configurationName);
    }

    @Override
    @Deprecated(since="9.3.0", forRemoval=true)
    public String getJASSConfigurationName() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.JAAS_CONFIG_NAME.toString(), SQLServerDriverStringProperty.JAAS_CONFIG_NAME.getDefaultValue());
    }

    @Override
    public void setJAASConfigurationName(String configurationName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.JAAS_CONFIG_NAME.toString(), configurationName);
    }

    @Override
    public String getJAASConfigurationName() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.JAAS_CONFIG_NAME.toString(), SQLServerDriverStringProperty.JAAS_CONFIG_NAME.getDefaultValue());
    }

    @Override
    @Deprecated(since="12.1.0", forRemoval=true)
    public void setMSIClientId(String managedIdentityClientId) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.MSI_CLIENT_ID.toString(), managedIdentityClientId);
    }

    @Override
    @Deprecated(since="12.1.0", forRemoval=true)
    public String getMSIClientId() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.MSI_CLIENT_ID.toString(), SQLServerDriverStringProperty.MSI_CLIENT_ID.getDefaultValue());
    }

    @Override
    public void setKeyVaultProviderClientId(String keyVaultProviderClientId) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_ID.toString(), keyVaultProviderClientId);
    }

    @Override
    public String getKeyVaultProviderClientId() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_ID.toString(), SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_ID.getDefaultValue());
    }

    @Override
    public void setKeyVaultProviderClientKey(String keyVaultProviderClientKey) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_KEY.toString(), keyVaultProviderClientKey);
    }

    @Override
    public void setKeyStorePrincipalId(String keyStorePrincipalId) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_STORE_PRINCIPAL_ID.toString(), keyStorePrincipalId);
    }

    @Override
    public String getKeyStorePrincipalId() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_STORE_PRINCIPAL_ID.toString(), SQLServerDriverStringProperty.KEY_STORE_PRINCIPAL_ID.getDefaultValue());
    }

    @Override
    public String getDomain() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.DOMAIN.toString(), SQLServerDriverStringProperty.DOMAIN.getDefaultValue());
    }

    @Override
    public void setDomain(String domain) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.DOMAIN.toString(), domain);
    }

    @Override
    public String getEnclaveAttestationUrl() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_URL.toString(), SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_URL.getDefaultValue());
    }

    @Override
    public void setEnclaveAttestationUrl(String url) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_URL.toString(), url);
    }

    @Override
    public String getEnclaveAttestationProtocol() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_PROTOCOL.toString(), SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_PROTOCOL.getDefaultValue());
    }

    @Override
    public void setEnclaveAttestationProtocol(String protocol) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_PROTOCOL.toString(), protocol);
    }

    @Override
    public String getClientCertificate() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.CLIENT_CERTIFICATE.toString(), SQLServerDriverStringProperty.CLIENT_CERTIFICATE.getDefaultValue());
    }

    @Override
    public void setClientCertificate(String certPath) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.CLIENT_CERTIFICATE.toString(), certPath);
    }

    @Override
    public String getClientKey() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.CLIENT_KEY.toString(), SQLServerDriverStringProperty.CLIENT_KEY.getDefaultValue());
    }

    @Override
    public void setClientKey(String keyPath) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.CLIENT_KEY.toString(), keyPath);
    }

    @Override
    public void setClientKeyPassword(String password) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.CLIENT_KEY_PASSWORD.toString(), password);
    }

    @Override
    @Deprecated(since="9.4.1", forRemoval=true)
    public String getAADSecurePrincipalId() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_ID.toString(), SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_ID.getDefaultValue());
    }

    @Override
    @Deprecated(since="9.4.1", forRemoval=true)
    public void setAADSecurePrincipalId(String aadSecurePrincipalId) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_ID.toString(), aadSecurePrincipalId);
    }

    @Override
    @Deprecated(since="9.4.1", forRemoval=true)
    public void setAADSecurePrincipalSecret(String aadSecurePrincipalSecret) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_SECRET.toString(), aadSecurePrincipalSecret);
    }

    @Override
    public boolean getSendTemporalDataTypesAsStringForBulkCopy() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SEND_TEMPORAL_DATATYPES_AS_STRING_FOR_BULK_COPY.toString(), SQLServerDriverBooleanProperty.SEND_TEMPORAL_DATATYPES_AS_STRING_FOR_BULK_COPY.getDefaultValue());
    }

    @Override
    public void setSendTemporalDataTypesAsStringForBulkCopy(boolean sendTemporalDataTypesAsStringForBulkCopy) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SEND_TEMPORAL_DATATYPES_AS_STRING_FOR_BULK_COPY.toString(), sendTemporalDataTypesAsStringForBulkCopy);
    }

    @Override
    public String getMaxResultBuffer() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.MAX_RESULT_BUFFER.toString(), SQLServerDriverStringProperty.MAX_RESULT_BUFFER.getDefaultValue());
    }

    @Override
    public void setMaxResultBuffer(String maxResultBuffer) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.MAX_RESULT_BUFFER.toString(), maxResultBuffer);
    }

    @Override
    public void setConnectRetryCount(int count) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.CONNECT_RETRY_COUNT.toString(), count);
    }

    @Override
    public int getConnectRetryCount() {
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.CONNECT_RETRY_COUNT.toString(), SQLServerDriverIntProperty.CONNECT_RETRY_COUNT.getDefaultValue());
    }

    @Override
    public void setConnectRetryInterval(int interval) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.CONNECT_RETRY_INTERVAL.toString(), interval);
    }

    @Override
    public int getConnectRetryInterval() {
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.CONNECT_RETRY_INTERVAL.toString(), SQLServerDriverIntProperty.CONNECT_RETRY_INTERVAL.getDefaultValue());
    }

    @Override
    public void setPrepareMethod(String prepareMethod) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.PREPARE_METHOD.toString(), prepareMethod);
    }

    @Override
    public String getPrepareMethod() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.PREPARE_METHOD.toString(), SQLServerDriverStringProperty.PREPARE_METHOD.getDefaultValue());
    }

    @Override
    @Deprecated(since="12.1.0", forRemoval=true)
    public void setMsiTokenCacheTtl(int timeToLive) {
    }

    @Override
    @Deprecated(since="12.1.0", forRemoval=true)
    public int getMsiTokenCacheTtl() {
        return 0;
    }

    @Override
    public void setAccessTokenCallback(SQLServerAccessTokenCallback accessTokenCallback) {
        this.setObjectProperty(this.connectionProps, SQLServerDriverObjectProperty.ACCESS_TOKEN_CALLBACK.toString(), accessTokenCallback);
    }

    @Override
    public SQLServerAccessTokenCallback getAccessTokenCallback() {
        return (SQLServerAccessTokenCallback)this.getObjectProperty(this.connectionProps, SQLServerDriverObjectProperty.ACCESS_TOKEN_CALLBACK.toString(), SQLServerDriverObjectProperty.ACCESS_TOKEN_CALLBACK.getDefaultValue());
    }

    @Override
    public void setAccessTokenCallbackClass(String accessTokenCallbackClass) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.ACCESS_TOKEN_CALLBACK_CLASS.toString(), accessTokenCallbackClass);
    }

    @Override
    public String getAccessTokenCallbackClass() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.ACCESS_TOKEN_CALLBACK_CLASS.toString(), null);
    }

    private void setStringProperty(Properties props, String propKey, String propValue) {
        if (loggerExternal.isLoggable(Level.FINER) && !propKey.contains("password") && !propKey.contains("Password")) {
            loggerExternal.entering(this.getClassNameLogging(), "set" + propKey, propValue);
        } else {
            loggerExternal.entering(this.getClassNameLogging(), "set" + propKey);
        }
        if (null != propValue) {
            props.setProperty(propKey, propValue);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "set" + propKey);
    }

    private String getStringProperty(Properties props, String propKey, String defaultValue) {
        String propValue;
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "get" + propKey);
        }
        if (null == (propValue = props.getProperty(propKey))) {
            propValue = defaultValue;
        }
        if (loggerExternal.isLoggable(Level.FINER) && !propKey.contains("password") && !propKey.contains("Password")) {
            loggerExternal.exiting(this.getClassNameLogging(), "get" + propKey, propValue);
        }
        return propValue;
    }

    private void setIntProperty(Properties props, String propKey, int propValue) {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "set" + propKey, propValue);
        }
        props.setProperty(propKey, Integer.toString(propValue));
        loggerExternal.exiting(this.getClassNameLogging(), "set" + propKey);
    }

    private int getIntProperty(Properties props, String propKey, int defaultValue) {
        int value;
        block5: {
            if (loggerExternal.isLoggable(Level.FINER)) {
                loggerExternal.entering(this.getClassNameLogging(), "get" + propKey);
            }
            String propValue = props.getProperty(propKey);
            value = defaultValue;
            if (null != propValue) {
                try {
                    value = Integer.parseInt(propValue);
                }
                catch (NumberFormatException nfe) {
                    if ($assertionsDisabled) break block5;
                    throw new AssertionError((Object)("Bad portNumber:-" + propValue));
                }
            }
        }
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.exiting(this.getClassNameLogging(), "get" + propKey, value);
        }
        return value;
    }

    private void setBooleanProperty(Properties props, String propKey, boolean propValue) {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "set" + propKey, propValue);
        }
        props.setProperty(propKey, propValue ? "true" : "false");
        loggerExternal.exiting(this.getClassNameLogging(), "set" + propKey);
    }

    private boolean getBooleanProperty(Properties props, String propKey, boolean defaultValue) {
        String propValue;
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "get" + propKey);
        }
        boolean value = null == (propValue = props.getProperty(propKey)) ? defaultValue : Boolean.valueOf(propValue);
        loggerExternal.exiting(this.getClassNameLogging(), "get" + propKey, value);
        return value;
    }

    private void setObjectProperty(Properties props, String propKey, Object propValue) {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "set" + propKey);
        }
        if (null != propValue) {
            props.put(propKey, propValue);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "set" + propKey);
    }

    private Object getObjectProperty(Properties props, String propKey, Object defaultValue) {
        Object propValue;
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "get" + propKey);
        }
        if (null == (propValue = props.get(propKey))) {
            propValue = defaultValue;
        }
        loggerExternal.exiting(this.getClassNameLogging(), "get" + propKey);
        return propValue;
    }

    SQLServerConnection getConnectionInternal(String username, String password, SQLServerPooledConnection pooledConnection) throws SQLServerException {
        Properties mergedProps;
        Properties userSuppliedProps;
        if (this.trustStorePasswordStripped) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_referencingFailedTSP"), null, true);
        }
        if (null != username || null != password) {
            userSuppliedProps = (Properties)this.connectionProps.clone();
            userSuppliedProps.remove(SQLServerDriverStringProperty.USER.toString());
            userSuppliedProps.remove(SQLServerDriverStringProperty.PASSWORD.toString());
            if (null != username) {
                userSuppliedProps.put(SQLServerDriverStringProperty.USER.toString(), username);
            }
            if (null != password) {
                userSuppliedProps.put(SQLServerDriverStringProperty.PASSWORD.toString(), password);
            }
        } else {
            userSuppliedProps = this.connectionProps;
        }
        if (null != this.dataSourceURL) {
            Properties urlProps = Util.parseUrl(this.dataSourceURL, dsLogger);
            if (null == urlProps) {
                SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
            }
            mergedProps = SQLServerDriver.mergeURLAndSuppliedProperties(urlProps, userSuppliedProps);
        } else {
            mergedProps = userSuppliedProps;
        }
        if (dsLogger.isLoggable(Level.FINER)) {
            dsLogger.finer(this.toString() + " Begin create new connection.");
        }
        SQLServerConnection result = null;
        result = DriverJDBCVersion.getSQLServerConnection(this.toString());
        result.connect(mergedProps, pooledConnection);
        if (dsLogger.isLoggable(Level.FINER)) {
            dsLogger.finer(this.toString() + " End create new connection " + result.toString());
        }
        return result;
    }

    @Override
    public Reference getReference() {
        loggerExternal.entering(this.getClassNameLogging(), "getReference");
        Reference ref = this.getReferenceInternal("com.microsoft.sqlserver.jdbc.SQLServerDataSource");
        loggerExternal.exiting(this.getClassNameLogging(), "getReference", ref);
        return ref;
    }

    Reference getReferenceInternal(String dataSourceClassString) {
        if (dsLogger.isLoggable(Level.FINER)) {
            dsLogger.finer(this.toString() + " creating reference for " + dataSourceClassString + ".");
        }
        Reference ref = new Reference(this.getClass().getName(), "com.microsoft.sqlserver.jdbc.SQLServerDataSourceObjectFactory", null);
        if (null != dataSourceClassString) {
            ref.add(new StringRefAddr("class", dataSourceClassString));
        }
        if (this.trustStorePasswordStripped) {
            ref.add(new StringRefAddr(TRUSTSTORE_PASSWORD_STRIPPED, "true"));
        }
        Enumeration<Object> e = this.connectionProps.keys();
        while (e.hasMoreElements()) {
            String propertyName = (String)e.nextElement();
            if (propertyName.equals(SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString())) {
                assert (!this.trustStorePasswordStripped);
                ref.add(new StringRefAddr(TRUSTSTORE_PASSWORD_STRIPPED, "true"));
                continue;
            }
            if (propertyName.contains(SQLServerDriverStringProperty.PASSWORD.toString())) continue;
            ref.add(new StringRefAddr(propertyName, this.connectionProps.getProperty(propertyName)));
        }
        if (null != this.dataSourceURL) {
            ref.add(new StringRefAddr("dataSourceURL", this.dataSourceURL));
        }
        if (null != this.dataSourceDescription) {
            ref.add(new StringRefAddr("dataSourceDescription", this.dataSourceDescription));
        }
        return ref;
    }

    void initializeFromReference(Reference ref) {
        Enumeration<RefAddr> e = ref.getAll();
        while (e.hasMoreElements()) {
            StringRefAddr addr = (StringRefAddr)e.nextElement();
            String propertyName = addr.getType();
            String propertyValue = (String)addr.getContent();
            if ("dataSourceURL".equals(propertyName)) {
                this.dataSourceURL = propertyValue;
                continue;
            }
            if ("dataSourceDescription".equals(propertyName)) {
                this.dataSourceDescription = propertyValue;
                continue;
            }
            if (TRUSTSTORE_PASSWORD_STRIPPED.equals(propertyName)) {
                this.trustStorePasswordStripped = true;
                continue;
            }
            if ("class".equals(propertyName)) continue;
            this.connectionProps.setProperty(propertyName, propertyValue);
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        loggerExternal.entering(this.getClassNameLogging(), "isWrapperFor", iface);
        boolean f = iface.isInstance(this);
        loggerExternal.exiting(this.getClassNameLogging(), "isWrapperFor", f);
        return f;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        T t;
        loggerExternal.entering(this.getClassNameLogging(), "unwrap", iface);
        try {
            t = iface.cast(this);
        }
        catch (ClassCastException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "unwrap", t);
        return t;
    }

    private static int nextDataSourceID() {
        return baseDataSourceID.incrementAndGet();
    }

    private Object writeReplace() throws ObjectStreamException {
        return new SerializationProxy(this);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("");
    }

    private static class SerializationProxy
    implements Serializable {
        private final Reference ref;
        private static final long serialVersionUID = 654661379542314226L;

        SerializationProxy(SQLServerDataSource ds) {
            this.ref = ds.getReferenceInternal(null);
        }

        private Object readResolve() {
            SQLServerDataSource ds = new SQLServerDataSource();
            ds.initializeFromReference(this.ref);
            return ds;
        }
    }
}

