/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ApplicationIntent;
import com.microsoft.sqlserver.jdbc.AuthenticationScheme;
import com.microsoft.sqlserver.jdbc.ColumnEncryptionSetting;
import com.microsoft.sqlserver.jdbc.DatetimeType;
import com.microsoft.sqlserver.jdbc.DriverJDBCVersion;
import com.microsoft.sqlserver.jdbc.EncryptOption;
import com.microsoft.sqlserver.jdbc.IPAddressPreference;
import com.microsoft.sqlserver.jdbc.KeyStoreAuthentication;
import com.microsoft.sqlserver.jdbc.PrepareMethod;
import com.microsoft.sqlserver.jdbc.SQLServerAccessTokenCallback;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriverBooleanProperty;
import com.microsoft.sqlserver.jdbc.SQLServerDriverIntProperty;
import com.microsoft.sqlserver.jdbc.SQLServerDriverObjectProperty;
import com.microsoft.sqlserver.jdbc.SQLServerDriverPropertyInfo;
import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SSLProtocol;
import com.microsoft.sqlserver.jdbc.SqlAuthentication;
import com.microsoft.sqlserver.jdbc.Util;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketOption;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ietf.jgss.GSSCredential;

public final class SQLServerDriver
implements Driver {
    static final String PRODUCT_NAME = "Microsoft JDBC Driver 12.4 for SQL Server";
    static final String AUTH_DLL_NAME;
    static final String DEFAULT_APP_NAME = "Microsoft JDBC Driver for SQL Server";
    private static final String[] TRUE_FALSE;
    private static final SQLServerDriverPropertyInfo[] DRIVER_PROPERTIES;
    private static final SQLServerDriverPropertyInfo[] DRIVER_PROPERTIES_PROPERTY_ONLY;
    private static final String[][] driverPropertiesSynonyms;
    private static final String[][] driverPropertyValuesSynonyms;
    private static final AtomicInteger baseID;
    private final int instanceID = SQLServerDriver.nextInstanceID();
    private final String traceID = "SQLServerDriver:" + this.instanceID;
    static Method socketSetOptionMethod;
    static SocketOption<Integer> socketKeepIdleOption;
    static SocketOption<Integer> socketKeepIntervalOption;
    private static final Logger loggerExternal;
    private static final Logger parentLogger;
    private final String loggingClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver:" + this.instanceID;
    private static final Logger drLogger;
    private static Driver mssqlDriver;
    private static final String[] systemPropertiesToLog;

    private static int nextInstanceID() {
        return baseID.incrementAndGet();
    }

    public final String toString() {
        return this.traceID;
    }

    String getClassNameLogging() {
        return this.loggingClassName;
    }

    public static void register() throws SQLException {
        if (!SQLServerDriver.isRegistered()) {
            mssqlDriver = new SQLServerDriver();
            DriverManager.registerDriver(mssqlDriver);
        }
    }

    public static void deregister() throws SQLException {
        if (SQLServerDriver.isRegistered()) {
            DriverManager.deregisterDriver(mssqlDriver);
            mssqlDriver = null;
        }
    }

    public static boolean isRegistered() {
        return mssqlDriver != null;
    }

    static Properties fixupProperties(Properties props) throws SQLServerException {
        Properties fixedup = new Properties();
        Enumeration<Object> e = props.keys();
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            String newname = SQLServerDriver.getNormalizedPropertyName(name, drLogger);
            if (null == newname) {
                newname = SQLServerDriver.getPropertyOnlyName(name, drLogger);
            }
            if (null == newname) continue;
            String val = props.getProperty(name);
            if (null != val) {
                fixedup.setProperty(newname, val);
                continue;
            }
            if ("gsscredential".equalsIgnoreCase(newname) && props.get(name) instanceof GSSCredential) {
                fixedup.put(newname, props.get(name));
                continue;
            }
            if ("accessTokenCallback".equalsIgnoreCase(newname) && props.get(name) instanceof SQLServerAccessTokenCallback) {
                fixedup.put(newname, props.get(name));
                continue;
            }
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidpropertyValue"));
            Object[] msgArgs = new Object[]{name};
            throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
        }
        return fixedup;
    }

    static Properties mergeURLAndSuppliedProperties(Properties urlProps, Properties suppliedProperties) throws SQLServerException {
        String sProp;
        if (null == suppliedProperties) {
            return urlProps;
        }
        if (suppliedProperties.isEmpty()) {
            return urlProps;
        }
        Properties suppliedPropertiesFixed = SQLServerDriver.fixupProperties(suppliedProperties);
        for (SQLServerDriverPropertyInfo DRIVER_PROPERTY : DRIVER_PROPERTIES) {
            sProp = DRIVER_PROPERTY.getName();
            String sPropVal = suppliedPropertiesFixed.getProperty(sProp);
            if (null == sPropVal) continue;
            urlProps.put(sProp, sPropVal);
        }
        for (SQLServerDriverPropertyInfo aDRIVER_PROPERTIES_PROPERTY_ONLY : DRIVER_PROPERTIES_PROPERTY_ONLY) {
            sProp = aDRIVER_PROPERTIES_PROPERTY_ONLY.getName();
            Object oPropVal = suppliedPropertiesFixed.get(sProp);
            if (null == oPropVal) continue;
            urlProps.put(sProp, oPropVal);
        }
        return urlProps;
    }

    static String getNormalizedPropertyName(String name, Logger logger) {
        if (null == name) {
            return name;
        }
        for (String[] stringArray : driverPropertiesSynonyms) {
            if (!stringArray[0].equalsIgnoreCase(name)) continue;
            return stringArray[1];
        }
        for (String[] stringArray : DRIVER_PROPERTIES) {
            if (!stringArray.getName().equalsIgnoreCase(name)) continue;
            return stringArray.getName();
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("Unknown property" + name);
        }
        return null;
    }

    static String getNormalizedPropertyValueName(String name) {
        if (null == name) {
            return name;
        }
        for (String[] driverPropertyValueSynonym : driverPropertyValuesSynonyms) {
            if (!driverPropertyValueSynonym[0].equalsIgnoreCase(name)) continue;
            return driverPropertyValueSynonym[1];
        }
        if (parentLogger.isLoggable(Level.FINER)) {
            parentLogger.finer("Unknown property value: " + name);
        }
        return "";
    }

    static String getPropertyOnlyName(String name, Logger logger) {
        if (null == name) {
            return name;
        }
        for (SQLServerDriverPropertyInfo aDRIVER_PROPERTIES_PROPERTY_ONLY : DRIVER_PROPERTIES_PROPERTY_ONLY) {
            if (!aDRIVER_PROPERTIES_PROPERTY_ONLY.getName().equalsIgnoreCase(name)) continue;
            return aDRIVER_PROPERTIES_PROPERTY_ONLY.getName();
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("Unknown property" + name);
        }
        return null;
    }

    @Override
    public Connection connect(String url, Properties suppliedProperties) throws SQLServerException {
        Properties connectProperties;
        loggerExternal.entering(this.getClassNameLogging(), "connect", "Arguments not traced.");
        SQLServerConnection result = null;
        if (loggerExternal.isLoggable(Level.FINE)) {
            loggerExternal.log(Level.FINE, "Microsoft JDBC Driver 12.4.2.0 for SQL Server");
            if (loggerExternal.isLoggable(Level.FINER)) {
                for (String propertyKeyName : systemPropertiesToLog) {
                    String propertyValue = System.getProperty(propertyKeyName);
                    if (propertyValue == null || propertyValue.isEmpty()) continue;
                    loggerExternal.log(Level.FINER, "System Property: " + propertyKeyName + " Value: " + System.getProperty(propertyKeyName));
                }
            }
        }
        if ((connectProperties = this.parseAndMergeProperties(url, suppliedProperties)) != null) {
            result = DriverJDBCVersion.getSQLServerConnection(this.toString());
            result.connect(connectProperties, null);
        }
        loggerExternal.exiting(this.getClassNameLogging(), "connect", result);
        return result;
    }

    private Properties parseAndMergeProperties(String url, Properties suppliedProperties) throws SQLServerException {
        if (url == null) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_nullConnection"), null, 0, false);
        }
        Properties connectProperties = Util.parseUrl(url, drLogger);
        if (null == connectProperties) {
            return null;
        }
        String loginTimeoutProp = connectProperties.getProperty(SQLServerDriverIntProperty.LOGIN_TIMEOUT.toString());
        int dmLoginTimeout = DriverManager.getLoginTimeout();
        if (dmLoginTimeout > 0 && null == loginTimeoutProp) {
            connectProperties.setProperty(SQLServerDriverIntProperty.LOGIN_TIMEOUT.toString(), String.valueOf(dmLoginTimeout));
        }
        connectProperties = SQLServerDriver.mergeURLAndSuppliedProperties(connectProperties, suppliedProperties);
        return connectProperties;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "acceptsURL", "Arguments not traced.");
        if (null == url) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_nullConnection"), null, 0, false);
        }
        boolean result = false;
        try {
            result = Util.parseUrl(url, drLogger) != null;
        }
        catch (SQLServerException e) {
            result = false;
        }
        loggerExternal.exiting(this.getClassNameLogging(), "acceptsURL", result);
        return result;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getPropertyInfo", "Arguments not traced.");
        Properties connProperties = this.parseAndMergeProperties(url, info);
        if (null == connProperties) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_invalidConnection"), null, 0, false);
        }
        DriverPropertyInfo[] properties = SQLServerDriver.getPropertyInfoFromProperties(connProperties);
        loggerExternal.exiting(this.getClassNameLogging(), "getPropertyInfo");
        return properties;
    }

    static final DriverPropertyInfo[] getPropertyInfoFromProperties(Properties props) {
        DriverPropertyInfo[] properties = new DriverPropertyInfo[DRIVER_PROPERTIES.length];
        for (int i = 0; i < DRIVER_PROPERTIES.length; ++i) {
            properties[i] = DRIVER_PROPERTIES[i].build(props);
        }
        return properties;
    }

    @Override
    public int getMajorVersion() {
        loggerExternal.entering(this.getClassNameLogging(), "getMajorVersion");
        loggerExternal.exiting(this.getClassNameLogging(), "getMajorVersion", 12);
        return 12;
    }

    @Override
    public int getMinorVersion() {
        loggerExternal.entering(this.getClassNameLogging(), "getMinorVersion");
        loggerExternal.exiting(this.getClassNameLogging(), "getMinorVersion", 4);
        return 4;
    }

    @Override
    public Logger getParentLogger() {
        return parentLogger;
    }

    @Override
    public boolean jdbcCompliant() {
        loggerExternal.entering(this.getClassNameLogging(), "jdbcCompliant");
        loggerExternal.exiting(this.getClassNameLogging(), "jdbcCompliant", Boolean.TRUE);
        return true;
    }

    static {
        block5: {
            block4: {
                AUTH_DLL_NAME = "mssql-jdbc_auth-12.4.2." + Util.getJVMArchOnWindows();
                TRUE_FALSE = new String[]{"true", "false"};
                DRIVER_PROPERTIES = new SQLServerDriverPropertyInfo[]{new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.APPLICATION_INTENT.toString(), SQLServerDriverStringProperty.APPLICATION_INTENT.getDefaultValue(), false, new String[]{ApplicationIntent.READ_ONLY.toString(), ApplicationIntent.READ_WRITE.toString()}), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.APPLICATION_NAME.toString(), SQLServerDriverStringProperty.APPLICATION_NAME.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.COLUMN_ENCRYPTION.toString(), SQLServerDriverStringProperty.COLUMN_ENCRYPTION.getDefaultValue(), false, new String[]{ColumnEncryptionSetting.DISABLED.toString(), ColumnEncryptionSetting.ENABLED.toString()}), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_URL.toString(), SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_URL.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_PROTOCOL.toString(), SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_PROTOCOL.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.DATABASE_NAME.toString(), SQLServerDriverStringProperty.DATABASE_NAME.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.DISABLE_STATEMENT_POOLING.toString(), Boolean.toString(SQLServerDriverBooleanProperty.DISABLE_STATEMENT_POOLING.getDefaultValue()), false, new String[]{"true", "false"}), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.ENCRYPT.toString(), SQLServerDriverStringProperty.ENCRYPT.getDefaultValue(), false, new String[]{EncryptOption.FALSE.toString(), EncryptOption.NO.toString(), EncryptOption.OPTIONAL.toString(), EncryptOption.TRUE.toString(), EncryptOption.MANDATORY.toString(), EncryptOption.STRICT.toString()}), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.SERVER_CERTIFICATE.toString(), SQLServerDriverStringProperty.SERVER_CERTIFICATE.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.PREPARE_METHOD.toString(), SQLServerDriverStringProperty.PREPARE_METHOD.getDefaultValue(), false, new String[]{PrepareMethod.PREPEXEC.toString(), PrepareMethod.PREPARE.toString()}), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.FAILOVER_PARTNER.toString(), SQLServerDriverStringProperty.FAILOVER_PARTNER.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString(), SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.INSTANCE_NAME.toString(), SQLServerDriverStringProperty.INSTANCE_NAME.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.INTEGRATED_SECURITY.toString(), Boolean.toString(SQLServerDriverBooleanProperty.INTEGRATED_SECURITY.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.KEY_STORE_AUTHENTICATION.toString(), SQLServerDriverStringProperty.KEY_STORE_AUTHENTICATION.getDefaultValue(), false, new String[]{KeyStoreAuthentication.JAVA_KEYSTORE_PASSWORD.toString()}), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.KEY_STORE_SECRET.toString(), SQLServerDriverStringProperty.KEY_STORE_SECRET.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.KEY_STORE_LOCATION.toString(), SQLServerDriverStringProperty.KEY_STORE_LOCATION.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.toString(), Boolean.toString(SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.LOCK_TIMEOUT.toString(), Integer.toString(SQLServerDriverIntProperty.LOCK_TIMEOUT.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.LOGIN_TIMEOUT.toString(), Integer.toString(SQLServerDriverIntProperty.LOGIN_TIMEOUT.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.toString(), Boolean.toString(SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.PACKET_SIZE.toString(), Integer.toString(SQLServerDriverIntProperty.PACKET_SIZE.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.PASSWORD.toString(), SQLServerDriverStringProperty.PASSWORD.getDefaultValue(), true, null), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.PORT_NUMBER.toString(), Integer.toString(SQLServerDriverIntProperty.PORT_NUMBER.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.QUERY_TIMEOUT.toString(), Integer.toString(SQLServerDriverIntProperty.QUERY_TIMEOUT.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.RESPONSE_BUFFERING.toString(), SQLServerDriverStringProperty.RESPONSE_BUFFERING.getDefaultValue(), false, new String[]{"adaptive", "full"}), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.SELECT_METHOD.toString(), SQLServerDriverStringProperty.SELECT_METHOD.getDefaultValue(), false, new String[]{"direct", "cursor"}), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.toString(), Boolean.toString(SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.toString(), Boolean.toString(SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.DOMAIN.toString(), SQLServerDriverStringProperty.DOMAIN.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.SERVER_NAME.toString(), SQLServerDriverStringProperty.SERVER_NAME.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.IPADDRESS_PREFERENCE.toString(), SQLServerDriverStringProperty.IPADDRESS_PREFERENCE.getDefaultValue(), false, new String[]{IPAddressPreference.IPV4_FIRST.toString(), IPAddressPreference.IPV6_FIRST.toString(), IPAddressPreference.USE_PLATFORM_DEFAULT.toString()}), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.SERVER_SPN.toString(), SQLServerDriverStringProperty.SERVER_SPN.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.REALM.toString(), SQLServerDriverStringProperty.REALM.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.SOCKET_FACTORY_CLASS.toString(), SQLServerDriverStringProperty.SOCKET_FACTORY_CLASS.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.SOCKET_FACTORY_CONSTRUCTOR_ARG.toString(), SQLServerDriverStringProperty.SOCKET_FACTORY_CONSTRUCTOR_ARG.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.toString(), Boolean.toString(SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.toString(), Boolean.toString(SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.TRUST_STORE_TYPE.toString(), SQLServerDriverStringProperty.TRUST_STORE_TYPE.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.TRUST_STORE.toString(), SQLServerDriverStringProperty.TRUST_STORE.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString(), SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.TRUST_MANAGER_CLASS.toString(), SQLServerDriverStringProperty.TRUST_MANAGER_CLASS.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.TRUST_MANAGER_CONSTRUCTOR_ARG.toString(), SQLServerDriverStringProperty.TRUST_MANAGER_CONSTRUCTOR_ARG.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverObjectProperty.ACCESS_TOKEN_CALLBACK.toString(), SQLServerDriverObjectProperty.ACCESS_TOKEN_CALLBACK.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.ACCESS_TOKEN_CALLBACK_CLASS.toString(), SQLServerDriverStringProperty.ACCESS_TOKEN_CALLBACK_CLASS.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.REPLICATION.toString(), Boolean.toString(SQLServerDriverBooleanProperty.REPLICATION.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.toString(), Boolean.toString(SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.DATETIME_DATATYPE.toString(), SQLServerDriverStringProperty.DATETIME_DATATYPE.getDefaultValue(), false, new String[]{DatetimeType.DATETIME.toString(), DatetimeType.DATETIME2.toString(), DatetimeType.DATETIMEOFFSET.toString()}), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.USER.toString(), SQLServerDriverStringProperty.USER.getDefaultValue(), true, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.WORKSTATION_ID.toString(), SQLServerDriverStringProperty.WORKSTATION_ID.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.XOPEN_STATES.toString(), Boolean.toString(SQLServerDriverBooleanProperty.XOPEN_STATES.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.AUTHENTICATION_SCHEME.toString(), SQLServerDriverStringProperty.AUTHENTICATION_SCHEME.getDefaultValue(), false, new String[]{AuthenticationScheme.JAVA_KERBEROS.toString(), AuthenticationScheme.NATIVE_AUTHENTICATION.toString(), AuthenticationScheme.NTLM.toString()}), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.AUTHENTICATION.toString(), SQLServerDriverStringProperty.AUTHENTICATION.getDefaultValue(), false, new String[]{SqlAuthentication.NOT_SPECIFIED.toString(), SqlAuthentication.SQLPASSWORD.toString(), SqlAuthentication.ACTIVE_DIRECTORY_PASSWORD.toString(), SqlAuthentication.ACTIVE_DIRECTORY_INTEGRATED.toString(), SqlAuthentication.ACTIVE_DIRECTORY_MANAGED_IDENTITY.toString(), SqlAuthentication.ACTIVE_DIRECTORY_SERVICE_PRINCIPAL.toString(), SqlAuthentication.ACTIVE_DIRECTORY_SERVICE_PRINCIPAL_CERTIFICATE.toString(), SqlAuthentication.ACTIVE_DIRECTORY_INTERACTIVE.toString()}), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.SOCKET_TIMEOUT.toString(), Integer.toString(SQLServerDriverIntProperty.SOCKET_TIMEOUT.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.FIPS.toString(), Boolean.toString(SQLServerDriverBooleanProperty.FIPS.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT.toString(), Boolean.toString(SQLServerDriverBooleanProperty.ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD.toString(), Integer.toString(SQLServerDriverIntProperty.SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.STATEMENT_POOLING_CACHE_SIZE.toString(), Integer.toString(SQLServerDriverIntProperty.STATEMENT_POOLING_CACHE_SIZE.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.JAAS_CONFIG_NAME.toString(), SQLServerDriverStringProperty.JAAS_CONFIG_NAME.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.SSL_PROTOCOL.toString(), SQLServerDriverStringProperty.SSL_PROTOCOL.getDefaultValue(), false, new String[]{SSLProtocol.TLS.toString(), SSLProtocol.TLS_V10.toString(), SSLProtocol.TLS_V11.toString(), SSLProtocol.TLS_V12.toString()}), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.toString(), Integer.toString(SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.USE_BULK_COPY_FOR_BATCH_INSERT.toString(), Boolean.toString(SQLServerDriverBooleanProperty.USE_BULK_COPY_FOR_BATCH_INSERT.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.MSI_CLIENT_ID.toString(), SQLServerDriverStringProperty.MSI_CLIENT_ID.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_ID.toString(), SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_ID.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_KEY.toString(), SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_KEY.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.USE_FMT_ONLY.toString(), Boolean.toString(SQLServerDriverBooleanProperty.USE_FMT_ONLY.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.KEY_STORE_PRINCIPAL_ID.toString(), SQLServerDriverStringProperty.KEY_STORE_PRINCIPAL_ID.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.CLIENT_CERTIFICATE.toString(), SQLServerDriverStringProperty.CLIENT_CERTIFICATE.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.CLIENT_KEY.toString(), SQLServerDriverStringProperty.CLIENT_KEY.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.CLIENT_KEY_PASSWORD.toString(), SQLServerDriverStringProperty.CLIENT_KEY_PASSWORD.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.DELAY_LOADING_LOBS.toString(), Boolean.toString(SQLServerDriverBooleanProperty.DELAY_LOADING_LOBS.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.SEND_TEMPORAL_DATATYPES_AS_STRING_FOR_BULK_COPY.toString(), Boolean.toString(SQLServerDriverBooleanProperty.SEND_TEMPORAL_DATATYPES_AS_STRING_FOR_BULK_COPY.getDefaultValue()), false, TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_ID.toString(), SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_ID.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_SECRET.toString(), SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_SECRET.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.MAX_RESULT_BUFFER.toString(), SQLServerDriverStringProperty.MAX_RESULT_BUFFER.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.CONNECT_RETRY_COUNT.toString(), Integer.toString(SQLServerDriverIntProperty.CONNECT_RETRY_COUNT.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.CONNECT_RETRY_INTERVAL.toString(), Integer.toString(SQLServerDriverIntProperty.CONNECT_RETRY_INTERVAL.getDefaultValue()), false, null)};
                DRIVER_PROPERTIES_PROPERTY_ONLY = new SQLServerDriverPropertyInfo[]{new SQLServerDriverPropertyInfo(SQLServerDriverObjectProperty.ACCESS_TOKEN_CALLBACK.toString(), SQLServerDriverObjectProperty.ACCESS_TOKEN_CALLBACK.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.ACCESS_TOKEN.toString(), SQLServerDriverStringProperty.ACCESS_TOKEN.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverObjectProperty.GSS_CREDENTIAL.toString(), SQLServerDriverObjectProperty.GSS_CREDENTIAL.getDefaultValue(), false, null)};
                driverPropertiesSynonyms = new String[][]{{"database", SQLServerDriverStringProperty.DATABASE_NAME.toString()}, {"userName", SQLServerDriverStringProperty.USER.toString()}, {"server", SQLServerDriverStringProperty.SERVER_NAME.toString()}, {"domainName", SQLServerDriverStringProperty.DOMAIN.toString()}, {"port", SQLServerDriverIntProperty.PORT_NUMBER.toString()}};
                driverPropertyValuesSynonyms = new String[][]{{"ActiveDirectoryMSI", SqlAuthentication.ACTIVE_DIRECTORY_MANAGED_IDENTITY.toString()}};
                baseID = new AtomicInteger(0);
                socketSetOptionMethod = null;
                socketKeepIdleOption = null;
                socketKeepIntervalOption = null;
                loggerExternal = Logger.getLogger("com.microsoft.sqlserver.jdbc.Driver");
                parentLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc");
                drLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerDriver");
                mssqlDriver = null;
                try {
                    SQLServerDriver.register();
                }
                catch (SQLException e) {
                    if (!drLogger.isLoggable(Level.FINER) || !Util.isActivityTraceOn()) break block4;
                    drLogger.finer("Error registering driver: " + e);
                }
            }
            try {
                socketSetOptionMethod = Socket.class.getMethod("setOption", SocketOption.class, Object.class);
                Class<?> clazz = Class.forName("jdk.net.ExtendedSocketOptions");
                socketKeepIdleOption = (SocketOption)clazz.getDeclaredField("TCP_KEEPIDLE").get(null);
                socketKeepIntervalOption = (SocketOption)clazz.getDeclaredField("TCP_KEEPINTERVAL").get(null);
            }
            catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
                if (!drLogger.isLoggable(Level.FINER) || !Util.isActivityTraceOn()) break block5;
                drLogger.finer("KeepAlive extended socket options not supported on this platform.");
            }
        }
        systemPropertiesToLog = new String[]{"java.specification.vendor", "java.specification.version", "java.class.path", "java.class.version", "java.runtime.name", "java.runtime.version", "java.vendor", "java.version", "java.vm.name", "java.vm.vendor", "java.vm.version", "java.vm.specification.vendor", "java.vm.specification.version", "os.name", "os.version", "os.arch"};
    }
}

