/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ActivityCorrelator;
import com.microsoft.sqlserver.jdbc.ActivityId;
import com.microsoft.sqlserver.jdbc.ApplicationIntent;
import com.microsoft.sqlserver.jdbc.AttestationProtocol;
import com.microsoft.sqlserver.jdbc.AuthenticationJNI;
import com.microsoft.sqlserver.jdbc.AuthenticationScheme;
import com.microsoft.sqlserver.jdbc.ColumnEncryptionSetting;
import com.microsoft.sqlserver.jdbc.ColumnEncryptionVersion;
import com.microsoft.sqlserver.jdbc.DLLException;
import com.microsoft.sqlserver.jdbc.DatetimeType;
import com.microsoft.sqlserver.jdbc.DriverJDBCVersion;
import com.microsoft.sqlserver.jdbc.EnclaveType;
import com.microsoft.sqlserver.jdbc.EncryptOption;
import com.microsoft.sqlserver.jdbc.FailoverInfo;
import com.microsoft.sqlserver.jdbc.FailoverMapSingleton;
import com.microsoft.sqlserver.jdbc.FedAuthDllInfo;
import com.microsoft.sqlserver.jdbc.ICounter;
import com.microsoft.sqlserver.jdbc.IPAddressPreference;
import com.microsoft.sqlserver.jdbc.ISQLServerConnection;
import com.microsoft.sqlserver.jdbc.ISQLServerEnclaveProvider;
import com.microsoft.sqlserver.jdbc.ISQLServerStatement;
import com.microsoft.sqlserver.jdbc.IdleConnectionResiliency;
import com.microsoft.sqlserver.jdbc.JDBCSyntaxTranslator;
import com.microsoft.sqlserver.jdbc.KerbAuthentication;
import com.microsoft.sqlserver.jdbc.KeyStoreAuthentication;
import com.microsoft.sqlserver.jdbc.MaxResultBufferParser;
import com.microsoft.sqlserver.jdbc.NTLMAuthentication;
import com.microsoft.sqlserver.jdbc.Parameter;
import com.microsoft.sqlserver.jdbc.ParameterUtils;
import com.microsoft.sqlserver.jdbc.ParsedSQLCacheItem;
import com.microsoft.sqlserver.jdbc.PersistentTokenCacheAccessAspect;
import com.microsoft.sqlserver.jdbc.PrepareMethod;
import com.microsoft.sqlserver.jdbc.SQLCollation;
import com.microsoft.sqlserver.jdbc.SQLServerAASEnclaveProvider;
import com.microsoft.sqlserver.jdbc.SQLServerAccessTokenCallback;
import com.microsoft.sqlserver.jdbc.SQLServerBlob;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerClob;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionAzureKeyVaultProvider;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionCertificateStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionJavaKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerColumnEncryptionKeyStoreProvider;
import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolProxy;
import com.microsoft.sqlserver.jdbc.SQLServerDatabaseMetaData;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.microsoft.sqlserver.jdbc.SQLServerDriverBooleanProperty;
import com.microsoft.sqlserver.jdbc.SQLServerDriverIntProperty;
import com.microsoft.sqlserver.jdbc.SQLServerDriverObjectProperty;
import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerError;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerMSAL4JUtils;
import com.microsoft.sqlserver.jdbc.SQLServerNClob;
import com.microsoft.sqlserver.jdbc.SQLServerNoneEnclaveProvider;
import com.microsoft.sqlserver.jdbc.SQLServerParameterMetaData;
import com.microsoft.sqlserver.jdbc.SQLServerPooledConnection;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.microsoft.sqlserver.jdbc.SQLServerSQLXML;
import com.microsoft.sqlserver.jdbc.SQLServerSavepoint;
import com.microsoft.sqlserver.jdbc.SQLServerSecurityUtility;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SQLServerStatementColumnEncryptionSetting;
import com.microsoft.sqlserver.jdbc.SQLServerVSMEnclaveProvider;
import com.microsoft.sqlserver.jdbc.SSLProtocol;
import com.microsoft.sqlserver.jdbc.SSPIAuthentication;
import com.microsoft.sqlserver.jdbc.SecureStringUtil;
import com.microsoft.sqlserver.jdbc.ServerPortPlaceHolder;
import com.microsoft.sqlserver.jdbc.SessionStateTable;
import com.microsoft.sqlserver.jdbc.SessionStateValue;
import com.microsoft.sqlserver.jdbc.SharedTimer;
import com.microsoft.sqlserver.jdbc.SqlAuthentication;
import com.microsoft.sqlserver.jdbc.SqlAuthenticationToken;
import com.microsoft.sqlserver.jdbc.StreamLoginAck;
import com.microsoft.sqlserver.jdbc.StreamSSPI;
import com.microsoft.sqlserver.jdbc.StringUtils;
import com.microsoft.sqlserver.jdbc.TDS;
import com.microsoft.sqlserver.jdbc.TDSChannel;
import com.microsoft.sqlserver.jdbc.TDSCommand;
import com.microsoft.sqlserver.jdbc.TDSParser;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSReaderMark;
import com.microsoft.sqlserver.jdbc.TDSTokenHandler;
import com.microsoft.sqlserver.jdbc.TDSWriter;
import com.microsoft.sqlserver.jdbc.UninterruptableTDSCommand;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.IDN;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLPermission;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.XAConnection;
import mssql.googlecode.cityhash.CityHash;
import mssql.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import mssql.googlecode.concurrentlinkedhashmap.EvictionListener;
import org.ietf.jgss.GSSCredential;

public class SQLServerConnection
implements ISQLServerConnection,
Serializable {
    private static final long serialVersionUID = 1965647556064751510L;
    private static final byte[] netAddress = SQLServerConnection.getRandomNetAddress();
    long timerExpire;
    boolean attemptRefreshTokenLocked = false;
    static final int DEFAULT_SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD = 10;
    private int serverPreparedStatementDiscardThreshold = -1;
    static final boolean DEFAULT_ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT_CALL = false;
    static final int BACKOFF_INTERVAL = 100;
    private Boolean enablePrepareOnFirstPreparedStatementCall = null;
    private String prepareMethod = null;
    private ConcurrentLinkedQueue<PreparedStatementHandle> discardedPreparedStatementHandles = new ConcurrentLinkedQueue();
    private AtomicInteger discardedPreparedStatementHandleCount = new AtomicInteger(0);
    private boolean fedAuthRequiredByUser = false;
    private boolean fedAuthRequiredPreLoginResponse = false;
    private boolean federatedAuthenticationRequested = false;
    private transient String accessTokenCallbackClass = null;
    private boolean hasAccessTokenCallbackClass = false;
    private transient SQLServerAccessTokenCallback accessTokenCallback = null;
    private boolean federatedAuthenticationInfoRequested = false;
    private FederatedAuthenticationFeatureExtensionData fedAuthFeatureExtensionData = null;
    private String authenticationString = null;
    private byte[] accessTokenInByte = null;
    private SqlAuthenticationToken fedAuthToken = null;
    private String originalHostNameInCertificate = null;
    private String clientCertificate = null;
    private String clientKey = null;
    private String clientKeyPassword = "";
    private String servicePrincipalCertificate = null;
    private String servicePrincipalCertificateKey = null;
    private String servicePrincipalCertificatePassword = "";
    private String aadPrincipalID = "";
    private String aadPrincipalSecret = "";
    private boolean sendTemporalDataTypesAsStringForBulkCopy = true;
    private static final int ENGINE_EDITION_SQL_AZURE_DB = 5;
    private static final int ENGINE_EDITION_SQL_AZURE_SYNAPSE_ANALYTICS = 6;
    private static final int ENGINE_EDITION_SQL_AZURE_MI = 8;
    private static final int ENGINE_EDITION_SQL_AZURE_SQL_EDGE = 9;
    private static final int ENGINE_EDITION_SQL_AZURE_SYNAPSE_SERVERLESS_SQL_POOL = 11;
    private Boolean isAzure = null;
    private Boolean isAzureDW = null;
    private Boolean isAzureMI = null;
    private Boolean supportsTransactions = null;
    private SharedTimer sharedTimer;
    private int connectRetryCount = 0;
    private int connectRetryInterval = 0;
    private boolean isTDS8 = false;
    byte[] encryptedTrustStorePassword = null;
    private final transient Lock lock = new ReentrantLock();
    private static final Lock sLock = new ReentrantLock();
    transient IdleNetworkTracker idleNetworkTracker = new IdleNetworkTracker();
    private static final int PARSED_SQL_CACHE_SIZE = 100;
    private static ConcurrentLinkedHashMap<CityHash128Key, ParsedSQLCacheItem> parsedSQLCache = new ConcurrentLinkedHashMap.Builder().maximumWeightedCapacity(100L).build();
    static final int DEFAULT_STATEMENT_POOLING_CACHE_SIZE = 0;
    private int statementPoolingCacheSize = 0;
    private ConcurrentLinkedHashMap<CityHash128Key, PreparedStatementHandle> preparedStatementHandleCache;
    private ConcurrentLinkedHashMap<CityHash128Key, SQLServerParameterMetaData> parameterMetadataCache;
    private boolean disableStatementPooling = true;
    static final int TNIR_FIRST_ATTEMPT_TIMEOUT_MS = 500;
    private static final float TIMEOUTSTEP = 0.08f;
    private static final float TIMEOUTSTEP_TNIR = 0.125f;
    private static final int INTERMITTENT_TLS_MAX_RETRY = 5;
    private boolean isRoutedInCurrentAttempt = false;
    private ServerPortPlaceHolder routingInfo = null;
    private static final String CALL_ABORT_PERM = "callAbort";
    private static final String SET_NETWORK_TIMEOUT_PERM = "setNetworkTimeout";
    private static final String SET_SAVE_POINT = "setSaveoint";
    private static final String CREATE_STATEMENT = "createStatement";
    private static final String ACTIVITY_ID = " ActivityId: ";
    private static final String TRUSTED_KEY_MASTER_PATHS = "Trusted Master Key Paths";
    private boolean sendStringParametersAsUnicode = SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.getDefaultValue();
    private String hostName = null;
    private boolean lastUpdateCount;
    private boolean serverNameAsACE = SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.getDefaultValue();
    private boolean multiSubnetFailover;
    private boolean transparentNetworkIPResolution;
    private ApplicationIntent applicationIntent = null;
    private int nLockTimeout;
    private String selectMethod;
    private String responseBuffering;
    private int queryTimeoutSeconds;
    private int cancelQueryTimeoutSeconds;
    private int socketTimeoutMilliseconds;
    private boolean useBulkCopyForBatchInsert;
    boolean userSetTNIR = true;
    private boolean replication = SQLServerDriverBooleanProperty.REPLICATION.getDefaultValue();
    private boolean sendTimeAsDatetime = SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.getDefaultValue();
    private DatetimeType datetimeParameterType = null;
    private boolean useFmtOnly = SQLServerDriverBooleanProperty.USE_FMT_ONLY.getDefaultValue();
    private byte requestedEncryptionLevel = (byte)-1;
    private boolean trustServerCertificate;
    private String serverCertificate = null;
    private byte negotiatedEncryptionLevel = (byte)-1;
    private String socketFactoryClass = null;
    private String socketFactoryConstructorArg = null;
    private String trustManagerClass = null;
    private String trustManagerConstructorArg = null;
    static final String RESERVED_PROVIDER_NAME_PREFIX = "MSSQL_";
    static final String WINDOWS_KEY_STORE_NAME = "MSSQL_CERTIFICATE_STORE";
    String columnEncryptionSetting = null;
    String encryptOption = null;
    String enclaveAttestationUrl = null;
    String enclaveAttestationProtocol = null;
    String keyStoreAuthentication = null;
    String keyStoreSecret = null;
    String keyStoreLocation = null;
    String keyStorePrincipalId = null;
    private ColumnEncryptionVersion serverColumnEncryptionVersion = ColumnEncryptionVersion.AE_NOTSUPPORTED;
    private boolean serverSupportsEnclaveRetry = false;
    private boolean serverSupportsDataClassification = false;
    private byte serverSupportedDataClassificationVersion = 0;
    private boolean serverSupportsDNSCaching = false;
    private static ConcurrentHashMap<String, InetSocketAddress> dnsCache = null;
    private boolean delayLoadingLobs = SQLServerDriverBooleanProperty.DELAY_LOADING_LOBS.getDefaultValue();
    private transient IdleConnectionResiliency sessionRecovery = new IdleConnectionResiliency(this);
    static Map<String, SQLServerColumnEncryptionKeyStoreProvider> globalSystemColumnEncryptionKeyStoreProviders = new HashMap<String, SQLServerColumnEncryptionKeyStoreProvider>();
    static Map<String, SQLServerColumnEncryptionKeyStoreProvider> globalCustomColumnEncryptionKeyStoreProviders;
    transient Map<String, SQLServerColumnEncryptionKeyStoreProvider> systemColumnEncryptionKeyStoreProvider = new HashMap<String, SQLServerColumnEncryptionKeyStoreProvider>();
    transient Map<String, SQLServerColumnEncryptionKeyStoreProvider> connectionColumnEncryptionKeyStoreProvider = new HashMap<String, SQLServerColumnEncryptionKeyStoreProvider>();
    private String trustedServerNameAE = null;
    private static Map<String, List<String>> columnEncryptionTrustedMasterKeyPaths;
    Properties activeConnectionProperties;
    private boolean integratedSecurity = SQLServerDriverBooleanProperty.INTEGRATED_SECURITY.getDefaultValue();
    private boolean ntlmAuthentication = false;
    private byte[] ntlmPasswordHash = null;
    private AuthenticationScheme intAuthScheme = AuthenticationScheme.NATIVE_AUTHENTICATION;
    private transient GSSCredential impersonatedUserCred;
    private boolean isUserCreatedCredential;
    ServerPortPlaceHolder currentConnectPlaceHolder = null;
    String sqlServerVersion;
    boolean xopenStates;
    private boolean databaseAutoCommitMode;
    private boolean inXATransaction = false;
    private byte[] transactionDescriptor = new byte[8];
    private boolean rolledBackTransaction;
    private volatile State state = State.INITIALIZED;
    static final int MAX_DECIMAL_PRECISION = 38;
    static final int DEFAULT_DECIMAL_PRECISION = 18;
    final String traceID;
    private int maxFieldSize;
    private int maxRows;
    private SQLCollation databaseCollation;
    private static final AtomicInteger baseConnectionID;
    private String sCatalog = "master";
    private String originalCatalog = "master";
    private String sLanguage = "us_english";
    private int transactionIsolationLevel;
    private SQLServerPooledConnection pooledConnectionParent;
    private SQLServerDatabaseMetaData databaseMetaData;
    private int nNextSavePointId = 10000;
    private static final Logger connectionlogger;
    private static final Logger loggerExternal;
    private static String loggingClassNameBase;
    private String loggingClassName = loggingClassNameBase;
    private String failoverPartnerServerProvided = null;
    private int holdability;
    private int tdsPacketSize = 4096;
    private int requestedPacketSize = 8000;
    private TDSChannel tdsChannel;
    private TDSCommand currentCommand = null;
    private int tdsVersion = 0;
    private int serverMajorVersion;
    private SQLServerConnectionPoolProxy proxy;
    private UUID clientConnectionId = null;
    static final int MAX_SQL_LOGIN_NAME_WCHARS = 128;
    static final int DEFAULTPORT;
    private final transient Lock schedulerLock = new ReentrantLock();
    volatile SQLWarning sqlWarnings;
    private final transient Lock warningSynchronization = new ReentrantLock();
    private static final int ENVCHANGE_DATABASE = 1;
    private static final int ENVCHANGE_LANGUAGE = 2;
    private static final int ENVCHANGE_CHARSET = 3;
    private static final int ENVCHANGE_PACKETSIZE = 4;
    private static final int ENVCHANGE_SORTLOCALEID = 5;
    private static final int ENVCHANGE_SORTFLAGS = 6;
    private static final int ENVCHANGE_SQLCOLLATION = 7;
    private static final int ENVCHANGE_XACT_BEGIN = 8;
    private static final int ENVCHANGE_XACT_COMMIT = 9;
    private static final int ENVCHANGE_XACT_ROLLBACK = 10;
    private static final int ENVCHANGE_DTC_ENLIST = 11;
    private static final int ENVCHANGE_DTC_DEFECT = 12;
    private static final int ENVCHANGE_CHANGE_MIRROR = 13;
    private static final int ENVCHANGE_UNUSED_14 = 14;
    private static final int ENVCHANGE_DTC_PROMOTE = 15;
    private static final int ENVCHANGE_DTC_MGR_ADDR = 16;
    private static final int ENVCHANGE_XACT_ENDED = 17;
    private static final int ENVCHANGE_RESET_COMPLETE = 18;
    private static final int ENVCHANGE_USER_INFO = 19;
    private static final int ENVCHANGE_ROUTING = 20;
    private boolean requestStarted = false;
    private boolean originalDatabaseAutoCommitMode;
    private int originalTransactionIsolationLevel;
    private int originalNetworkTimeout;
    private int originalHoldability;
    private boolean originalSendTimeAsDatetime;
    private DatetimeType originalDatetimeParameterType;
    private int originalStatementPoolingCacheSize;
    private boolean originalDisableStatementPooling;
    private int originalServerPreparedStatementDiscardThreshold;
    private Boolean originalEnablePrepareOnFirstPreparedStatementCall;
    private String originalSCatalog;
    private boolean originalUseBulkCopyForBatchInsert;
    private volatile SQLWarning originalSqlWarnings;
    private List<ISQLServerStatement> openStatements;
    private boolean originalUseFmtOnly;
    private boolean originalDelayLoadingLobs;
    private int aeVersion = 0;
    static final char[] OUT;
    private static final int BROWSER_PORT = 1434;
    private static long columnEncryptionKeyCacheTtl;
    private transient ISQLServerEnclaveProvider enclaveProvider;

    private static byte[] getRandomNetAddress() {
        byte[] a = new byte[6];
        Random random = new Random();
        random.nextBytes(a);
        return a;
    }

    SharedTimer getSharedTimer() throws SQLServerException {
        if (this.state == State.CLOSED) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_connectionIsClosed"), "08006", false);
        }
        if (null == this.sharedTimer) {
            this.sharedTimer = SharedTimer.getTimer();
        }
        return this.sharedTimer;
    }

    String getServerNameString(String serverName) {
        String serverNameFromConnectionStr = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.SERVER_NAME.toString());
        if (null == serverName || serverName.equals(serverNameFromConnectionStr)) {
            return serverName;
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_redirectedFrom"));
        Object[] msgArgs = new Object[]{serverName, serverNameFromConnectionStr};
        return form.format(msgArgs);
    }

    static ParsedSQLCacheItem getCachedParsedSQL(CityHash128Key key) {
        return parsedSQLCache.get(key);
    }

    static ParsedSQLCacheItem parseAndCacheSQL(CityHash128Key key, String sql) throws SQLServerException {
        JDBCSyntaxTranslator translator = new JDBCSyntaxTranslator();
        String parsedSql = translator.translate(sql);
        String procName = translator.getProcedureName();
        boolean returnValueSyntax = translator.hasReturnValueSyntax();
        int[] parameterPositions = SQLServerConnection.locateParams(parsedSql);
        ParsedSQLCacheItem cacheItem = new ParsedSQLCacheItem(parsedSql, parameterPositions, procName, returnValueSyntax);
        parsedSQLCache.putIfAbsent(key, cacheItem);
        return cacheItem;
    }

    private static int[] locateParams(String sql) {
        LinkedList<Integer> parameterPositions = new LinkedList<Integer>();
        int offset = -1;
        while (true) {
            ++offset;
            if ((offset = ParameterUtils.scanSQLForChar('?', sql, offset)) >= sql.length()) break;
            parameterPositions.add(offset);
        }
        return parameterPositions.stream().mapToInt(Integer::valueOf).toArray();
    }

    ServerPortPlaceHolder getRoutingInfo() {
        return this.routingInfo;
    }

    boolean sendStringParametersAsUnicode() {
        return this.sendStringParametersAsUnicode;
    }

    final boolean useLastUpdateCount() {
        return this.lastUpdateCount;
    }

    boolean serverNameAsACE() {
        return this.serverNameAsACE;
    }

    final boolean getMultiSubnetFailover() {
        return this.multiSubnetFailover;
    }

    final boolean getTransparentNetworkIPResolution() {
        return this.transparentNetworkIPResolution;
    }

    final ApplicationIntent getApplicationIntent() {
        return this.applicationIntent;
    }

    final String getSelectMethod() {
        return this.selectMethod;
    }

    final String getResponseBuffering() {
        return this.responseBuffering;
    }

    final int getQueryTimeoutSeconds() {
        return this.queryTimeoutSeconds;
    }

    final int getCancelQueryTimeoutSeconds() {
        return this.cancelQueryTimeoutSeconds;
    }

    final int getSocketTimeoutMilliseconds() {
        return this.socketTimeoutMilliseconds;
    }

    public boolean getUseBulkCopyForBatchInsert() {
        return this.useBulkCopyForBatchInsert;
    }

    public void setUseBulkCopyForBatchInsert(boolean useBulkCopyForBatchInsert) {
        this.useBulkCopyForBatchInsert = useBulkCopyForBatchInsert;
    }

    @Override
    public final boolean getSendTimeAsDatetime() {
        return !this.isKatmaiOrLater() || this.sendTimeAsDatetime;
    }

    final int baseYear() {
        return this.getSendTimeAsDatetime() ? 1970 : 1900;
    }

    @Override
    public final String getDatetimeParameterType() {
        return this.datetimeParameterType.toString();
    }

    final byte getRequestedEncryptionLevel() {
        assert (-1 != this.requestedEncryptionLevel);
        return this.requestedEncryptionLevel;
    }

    final boolean getTrustServerCertificate() {
        return this.trustServerCertificate;
    }

    final String getEncrypt() {
        return this.encryptOption;
    }

    final String getServerCertificate() {
        return this.serverCertificate;
    }

    final byte getNegotiatedEncryptionLevel() {
        assert (this.isTDS8 || -1 != this.negotiatedEncryptionLevel);
        return this.negotiatedEncryptionLevel;
    }

    final String getSocketFactoryClass() {
        return this.socketFactoryClass;
    }

    final String getSocketFactoryConstructorArg() {
        return this.socketFactoryConstructorArg;
    }

    final String getTrustManagerClass() {
        assert (-1 != this.requestedEncryptionLevel);
        return this.trustManagerClass;
    }

    final String getTrustManagerConstructorArg() {
        assert (-1 != this.requestedEncryptionLevel);
        return this.trustManagerConstructorArg;
    }

    boolean isColumnEncryptionSettingEnabled() {
        return this.columnEncryptionSetting.equalsIgnoreCase(ColumnEncryptionSetting.ENABLED.toString());
    }

    boolean getSendTemporalDataTypesAsStringForBulkCopy() {
        return this.sendTemporalDataTypesAsStringForBulkCopy;
    }

    boolean getServerSupportsColumnEncryption() {
        return this.serverColumnEncryptionVersion.value() > ColumnEncryptionVersion.AE_NOTSUPPORTED.value();
    }

    ColumnEncryptionVersion getServerColumnEncryptionVersion() {
        return this.serverColumnEncryptionVersion;
    }

    boolean getServerSupportsDataClassification() {
        return this.serverSupportsDataClassification;
    }

    static InetSocketAddress getDNSEntry(String key) {
        return null != dnsCache ? dnsCache.get(key) : null;
    }

    byte getServerSupportedDataClassificationVersion() {
        return this.serverSupportedDataClassificationVersion;
    }

    @Override
    public boolean getDelayLoadingLobs() {
        return this.delayLoadingLobs;
    }

    @Override
    public void setDelayLoadingLobs(boolean b) {
        this.delayLoadingLobs = b;
    }

    IdleConnectionResiliency getSessionRecovery() {
        return this.sessionRecovery;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void registerColumnEncryptionKeyStoreProviders(Map<String, SQLServerColumnEncryptionKeyStoreProvider> clientKeyStoreProviders) throws SQLServerException {
        loggerExternal.entering(loggingClassNameBase, "registerColumnEncryptionKeyStoreProviders", "Registering Column Encryption Key Store Providers");
        sLock.lock();
        try {
            if (null == clientKeyStoreProviders) {
                throw new SQLServerException(null, SQLServerException.getErrString("R_CustomKeyStoreProviderMapNull"), null, 0, false);
            }
            if (null != globalCustomColumnEncryptionKeyStoreProviders && !globalCustomColumnEncryptionKeyStoreProviders.isEmpty()) {
                throw new SQLServerException(null, SQLServerException.getErrString("R_CustomKeyStoreProviderSetOnce"), null, 0, false);
            }
            globalCustomColumnEncryptionKeyStoreProviders = new HashMap<String, SQLServerColumnEncryptionKeyStoreProvider>();
            for (Map.Entry<String, SQLServerColumnEncryptionKeyStoreProvider> entry : clientKeyStoreProviders.entrySet()) {
                String providerName = entry.getKey();
                if (null == providerName || 0 == providerName.trim().length()) {
                    throw new SQLServerException(null, SQLServerException.getErrString("R_EmptyCustomKeyStoreProviderName"), null, 0, false);
                }
                if (providerName.substring(0, 6).equalsIgnoreCase(RESERVED_PROVIDER_NAME_PREFIX)) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidCustomKeyStoreProviderName"));
                    Object[] msgArgs = new Object[]{providerName, RESERVED_PROVIDER_NAME_PREFIX};
                    throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
                }
                SQLServerColumnEncryptionKeyStoreProvider provider = entry.getValue();
                if (null == provider) {
                    throw new SQLServerException(null, String.format(SQLServerException.getErrString("R_CustomKeyStoreProviderValueNull"), providerName), null, 0, false);
                }
                provider.setColumnEncryptionCacheTtl(Duration.ZERO);
                globalCustomColumnEncryptionKeyStoreProviders.put(providerName, provider);
            }
        }
        finally {
            sLock.unlock();
        }
        loggerExternal.exiting(loggingClassNameBase, "registerColumnEncryptionKeyStoreProviders", "Number of Key store providers that are registered:" + globalCustomColumnEncryptionKeyStoreProviders.size());
    }

    public static void unregisterColumnEncryptionKeyStoreProviders() {
        loggerExternal.entering(loggingClassNameBase, "unregisterColumnEncryptionKeyStoreProviders", "Removing Column Encryption Key Store Provider");
        sLock.lock();
        try {
            if (null != globalCustomColumnEncryptionKeyStoreProviders) {
                globalCustomColumnEncryptionKeyStoreProviders.clear();
                globalCustomColumnEncryptionKeyStoreProviders = null;
            }
        }
        finally {
            sLock.unlock();
        }
        loggerExternal.exiting(loggingClassNameBase, "unregisterColumnEncryptionKeyStoreProviders", "Number of Key store providers that are registered: 0");
    }

    SQLServerColumnEncryptionKeyStoreProvider getGlobalSystemColumnEncryptionKeyStoreProvider(String providerName) {
        this.lock.lock();
        try {
            SQLServerColumnEncryptionKeyStoreProvider sQLServerColumnEncryptionKeyStoreProvider = null != globalSystemColumnEncryptionKeyStoreProviders && globalSystemColumnEncryptionKeyStoreProviders.containsKey(providerName) ? globalSystemColumnEncryptionKeyStoreProviders.get(providerName) : null;
            return sQLServerColumnEncryptionKeyStoreProvider;
        }
        finally {
            this.lock.unlock();
        }
    }

    String getAllGlobalCustomSystemColumnEncryptionKeyStoreProviders() {
        this.lock.lock();
        try {
            String string = null != globalCustomColumnEncryptionKeyStoreProviders ? globalCustomColumnEncryptionKeyStoreProviders.keySet().toString() : null;
            return string;
        }
        finally {
            this.lock.unlock();
        }
    }

    String getAllSystemColumnEncryptionKeyStoreProviders() {
        this.lock.lock();
        try {
            Object keyStores = "";
            if (0 != this.systemColumnEncryptionKeyStoreProvider.size()) {
                keyStores = this.systemColumnEncryptionKeyStoreProvider.keySet().toString();
            }
            if (0 != globalSystemColumnEncryptionKeyStoreProviders.size()) {
                keyStores = (String)keyStores + "," + globalSystemColumnEncryptionKeyStoreProviders.keySet().toString();
            }
            String string = keyStores;
            return string;
        }
        finally {
            this.lock.unlock();
        }
    }

    SQLServerColumnEncryptionKeyStoreProvider getGlobalCustomColumnEncryptionKeyStoreProvider(String providerName) {
        this.lock.lock();
        try {
            SQLServerColumnEncryptionKeyStoreProvider sQLServerColumnEncryptionKeyStoreProvider = null != globalCustomColumnEncryptionKeyStoreProviders && globalCustomColumnEncryptionKeyStoreProviders.containsKey(providerName) ? globalCustomColumnEncryptionKeyStoreProviders.get(providerName) : null;
            return sQLServerColumnEncryptionKeyStoreProvider;
        }
        finally {
            this.lock.unlock();
        }
    }

    SQLServerColumnEncryptionKeyStoreProvider getSystemColumnEncryptionKeyStoreProvider(String providerName) {
        this.lock.lock();
        try {
            SQLServerColumnEncryptionKeyStoreProvider sQLServerColumnEncryptionKeyStoreProvider = null != this.systemColumnEncryptionKeyStoreProvider && this.systemColumnEncryptionKeyStoreProvider.containsKey(providerName) ? this.systemColumnEncryptionKeyStoreProvider.get(providerName) : null;
            return sQLServerColumnEncryptionKeyStoreProvider;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    SQLServerColumnEncryptionKeyStoreProvider getSystemOrGlobalColumnEncryptionKeyStoreProvider(String providerName) throws SQLServerException {
        SQLServerColumnEncryptionKeyStoreProvider keystoreProvider = null;
        this.lock.lock();
        try {
            keystoreProvider = this.getGlobalSystemColumnEncryptionKeyStoreProvider(providerName);
            if (null == keystoreProvider) {
                keystoreProvider = this.getSystemColumnEncryptionKeyStoreProvider(providerName);
            }
            if (null == keystoreProvider) {
                keystoreProvider = this.getGlobalCustomColumnEncryptionKeyStoreProvider(providerName);
            }
            if (null == keystoreProvider) {
                String systemProviders = this.getAllSystemColumnEncryptionKeyStoreProviders();
                String customProviders = this.getAllGlobalCustomSystemColumnEncryptionKeyStoreProviders();
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnrecognizedKeyStoreProviderName"));
                Object[] msgArgs = new Object[]{providerName, systemProviders, customProviders};
                throw new SQLServerException(form.format(msgArgs), null);
            }
            SQLServerColumnEncryptionKeyStoreProvider sQLServerColumnEncryptionKeyStoreProvider = keystoreProvider;
            return sQLServerColumnEncryptionKeyStoreProvider;
        }
        finally {
            this.lock.unlock();
        }
    }

    boolean hasConnectionColumnEncryptionKeyStoreProvidersRegistered() {
        this.lock.lock();
        try {
            boolean bl = null != this.connectionColumnEncryptionKeyStoreProvider && this.connectionColumnEncryptionKeyStoreProvider.size() > 0;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    String getAllConnectionColumnEncryptionKeyStoreProviders() {
        this.lock.lock();
        try {
            String keyStores = "";
            if (0 != this.connectionColumnEncryptionKeyStoreProvider.size()) {
                keyStores = this.connectionColumnEncryptionKeyStoreProvider.keySet().toString();
            }
            String string = keyStores;
            return string;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    SQLServerColumnEncryptionKeyStoreProvider getColumnEncryptionKeyStoreProviderOnConnection(String providerName) throws SQLServerException {
        this.lock.lock();
        try {
            if (null != this.connectionColumnEncryptionKeyStoreProvider && this.connectionColumnEncryptionKeyStoreProvider.size() > 0) {
                if (this.connectionColumnEncryptionKeyStoreProvider.containsKey(providerName)) {
                    SQLServerColumnEncryptionKeyStoreProvider sQLServerColumnEncryptionKeyStoreProvider = this.connectionColumnEncryptionKeyStoreProvider.get(providerName);
                    return sQLServerColumnEncryptionKeyStoreProvider;
                }
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnrecognizedConnectionKeyStoreProviderName"));
                Object[] msgArgs = new Object[]{providerName, this.getAllConnectionColumnEncryptionKeyStoreProviders()};
                throw new SQLServerException(form.format(msgArgs), null);
            }
            SQLServerColumnEncryptionKeyStoreProvider sQLServerColumnEncryptionKeyStoreProvider = this.getSystemOrGlobalColumnEncryptionKeyStoreProvider(providerName);
            return sQLServerColumnEncryptionKeyStoreProvider;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerColumnEncryptionKeyStoreProvidersOnConnection(Map<String, SQLServerColumnEncryptionKeyStoreProvider> clientKeyStoreProviders) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "registerColumnEncryptionKeyStoreProvidersOnConnection", "Registering Column Encryption Key Store Providers on Connection");
        this.lock.lock();
        try {
            if (null == clientKeyStoreProviders) {
                throw new SQLServerException(null, SQLServerException.getErrString("R_CustomKeyStoreProviderMapNull"), null, 0, false);
            }
            this.connectionColumnEncryptionKeyStoreProvider.clear();
            for (Map.Entry<String, SQLServerColumnEncryptionKeyStoreProvider> entry : clientKeyStoreProviders.entrySet()) {
                String providerName = entry.getKey();
                if (null == providerName || 0 == providerName.trim().length()) {
                    throw new SQLServerException(null, SQLServerException.getErrString("R_EmptyCustomKeyStoreProviderName"), null, 0, false);
                }
                if (providerName.equalsIgnoreCase(WINDOWS_KEY_STORE_NAME)) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidCustomKeyStoreProviderName"));
                    Object[] msgArgs = new Object[]{providerName, WINDOWS_KEY_STORE_NAME};
                    throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
                }
                if (null == entry.getValue()) {
                    throw new SQLServerException(null, String.format(SQLServerException.getErrString("R_CustomKeyStoreProviderValueNull"), providerName), null, 0, false);
                }
                this.connectionColumnEncryptionKeyStoreProvider.put(entry.getKey(), entry.getValue());
            }
        }
        finally {
            this.lock.unlock();
        }
        loggerExternal.exiting(this.loggingClassName, "registerColumnEncryptionKeyStoreProvidersOnConnection", "Number of connection-level Key store providers that are registered: " + this.connectionColumnEncryptionKeyStoreProvider.size());
    }

    public static void setColumnEncryptionTrustedMasterKeyPaths(Map<String, List<String>> trustedKeyPaths) {
        loggerExternal.entering(loggingClassNameBase, "setColumnEncryptionTrustedMasterKeyPaths", "Setting Trusted Master Key Paths");
        sLock.lock();
        try {
            columnEncryptionTrustedMasterKeyPaths.clear();
            for (Map.Entry<String, List<String>> entry : trustedKeyPaths.entrySet()) {
                columnEncryptionTrustedMasterKeyPaths.put(entry.getKey().toUpperCase(), entry.getValue());
            }
        }
        finally {
            sLock.unlock();
        }
        loggerExternal.exiting(loggingClassNameBase, "setColumnEncryptionTrustedMasterKeyPaths", "Number of Trusted Master Key Paths: " + columnEncryptionTrustedMasterKeyPaths.size());
    }

    public static void updateColumnEncryptionTrustedMasterKeyPaths(String server, List<String> trustedKeyPaths) {
        loggerExternal.entering(loggingClassNameBase, "updateColumnEncryptionTrustedMasterKeyPaths", "Updating Trusted Master Key Paths");
        sLock.lock();
        try {
            columnEncryptionTrustedMasterKeyPaths.put(server.toUpperCase(), trustedKeyPaths);
        }
        finally {
            sLock.unlock();
        }
        loggerExternal.exiting(loggingClassNameBase, "updateColumnEncryptionTrustedMasterKeyPaths", "Number of Trusted Master Key Paths: " + columnEncryptionTrustedMasterKeyPaths.size());
    }

    public static void removeColumnEncryptionTrustedMasterKeyPaths(String server) {
        loggerExternal.entering(loggingClassNameBase, "removeColumnEncryptionTrustedMasterKeyPaths", "Removing Trusted Master Key Paths");
        sLock.lock();
        try {
            columnEncryptionTrustedMasterKeyPaths.remove(server.toUpperCase());
        }
        finally {
            sLock.unlock();
        }
        loggerExternal.exiting(loggingClassNameBase, "removeColumnEncryptionTrustedMasterKeyPaths", "Number of Trusted Master Key Paths: " + columnEncryptionTrustedMasterKeyPaths.size());
    }

    public static Map<String, List<String>> getColumnEncryptionTrustedMasterKeyPaths() {
        loggerExternal.entering(loggingClassNameBase, "getColumnEncryptionTrustedMasterKeyPaths", "Getting Trusted Master Key Paths");
        sLock.lock();
        try {
            HashMap<String, List<String>> masterKeyPathCopy = new HashMap<String, List<String>>();
            for (Map.Entry<String, List<String>> entry : columnEncryptionTrustedMasterKeyPaths.entrySet()) {
                masterKeyPathCopy.put(entry.getKey(), entry.getValue());
            }
            loggerExternal.exiting(loggingClassNameBase, "getColumnEncryptionTrustedMasterKeyPaths", "Number of Trusted Master Key Paths: " + masterKeyPathCopy.size());
            HashMap<String, List<String>> hashMap = masterKeyPathCopy;
            return hashMap;
        }
        finally {
            sLock.unlock();
        }
    }

    static List<String> getColumnEncryptionTrustedMasterKeyPaths(String server, Boolean[] hasEntry) {
        sLock.lock();
        try {
            if (columnEncryptionTrustedMasterKeyPaths.containsKey(server)) {
                hasEntry[0] = true;
                List<String> list = columnEncryptionTrustedMasterKeyPaths.get(server);
                return list;
            }
            hasEntry[0] = false;
            List<String> list = null;
            return list;
        }
        finally {
            sLock.unlock();
        }
    }

    public static void clearUserTokenCache() {
        sLock.lock();
        try {
            PersistentTokenCacheAccessAspect.clearUserTokenCache();
        }
        finally {
            sLock.unlock();
        }
    }

    final boolean rolledBackTransaction() {
        return this.rolledBackTransaction;
    }

    private void setState(State state) {
        this.state = state;
    }

    final boolean isSessionUnAvailable() {
        return !this.state.equals((Object)State.OPENED);
    }

    final void setMaxFieldSize(int limit) throws SQLServerException {
        if (this.maxFieldSize != limit) {
            if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
                loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
            }
            this.connectionCommand("SET TEXTSIZE " + (0 == limit ? Integer.MAX_VALUE : limit), "setMaxFieldSize");
            this.maxFieldSize = limit;
        }
    }

    final void initResettableValues() {
        this.setLockTimeout();
        this.rolledBackTransaction = false;
        this.transactionIsolationLevel = 2;
        this.maxFieldSize = 0;
        this.maxRows = 0;
        this.databaseAutoCommitMode = true;
        this.holdability = 1;
        this.sqlWarnings = null;
        this.sCatalog = this.originalCatalog;
        this.databaseMetaData = null;
    }

    final void setMaxRows(int limit) throws SQLServerException {
        if (this.maxRows != limit) {
            if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
                loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
            }
            this.connectionCommand("SET ROWCOUNT " + limit, "setMaxRows");
            this.maxRows = limit;
        }
    }

    final SQLCollation getDatabaseCollation() {
        return this.databaseCollation;
    }

    final int getHoldabilityInternal() {
        return this.holdability;
    }

    final int getTDSPacketSize() {
        return this.tdsPacketSize;
    }

    final boolean isKatmaiOrLater() {
        assert (0 != this.tdsVersion);
        return this.tdsVersion >= 1930100739 || this.tdsVersion == 0x8000000;
    }

    final boolean isDenaliOrLater() {
        return this.tdsVersion >= 0x74000004 || this.tdsVersion == 0x8000000;
    }

    int getServerMajorVersion() {
        return this.serverMajorVersion;
    }

    @Override
    public UUID getClientConnectionId() throws SQLServerException {
        this.checkClosed();
        return this.clientConnectionId;
    }

    final UUID getClientConIdInternal() {
        return this.clientConnectionId;
    }

    final int getRetryInterval() {
        return this.connectRetryInterval;
    }

    final int getRetryCount() {
        return this.connectRetryCount;
    }

    final boolean attachConnId() {
        return this.state.equals((Object)State.CONNECTED);
    }

    SQLServerPooledConnection getPooledConnectionParent() {
        return this.pooledConnectionParent;
    }

    SQLServerConnection(String parentInfo) {
        int connectionID = SQLServerConnection.nextConnectionID();
        this.traceID = "ConnectionID:" + connectionID;
        this.loggingClassName = this.loggingClassName + ":" + connectionID;
        if (connectionlogger.isLoggable(Level.FINE)) {
            connectionlogger.fine(this.toString() + " created by (" + parentInfo + ")");
        }
        this.initResettableValues();
        if (!this.getDisableStatementPooling() && 0 < this.getStatementPoolingCacheSize()) {
            this.prepareCache();
        }
    }

    void setFailoverPartnerServerProvided(String partner) {
        this.failoverPartnerServerProvided = partner;
    }

    final void setAssociatedProxy(SQLServerConnectionPoolProxy proxy) {
        this.proxy = proxy;
    }

    final Connection getConnection() {
        if (null != this.proxy) {
            return this.proxy;
        }
        return this;
    }

    final void resetPooledConnection() {
        this.tdsChannel.resetPooledConnection();
        this.initResettableValues();
    }

    private static int nextConnectionID() {
        return baseConnectionID.incrementAndGet();
    }

    Logger getConnectionLogger() {
        return connectionlogger;
    }

    public String toString() {
        if (null != this.clientConnectionId) {
            return this.traceID + " ClientConnectionId: " + this.clientConnectionId.toString();
        }
        return this.traceID;
    }

    void checkClosed() throws SQLServerException {
        if (this.isSessionUnAvailable()) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_connectionIsClosed"), "08006", false);
        }
    }

    protected boolean needsReconnect() {
        return null != this.fedAuthToken && Util.checkIfNeedNewAccessToken(this, this.fedAuthToken.getExpiresOn());
    }

    private boolean isBooleanPropertyOn(String propName, String propValue) throws SQLServerException {
        if (null == propValue) {
            return false;
        }
        if ("true".equalsIgnoreCase(propValue)) {
            return true;
        }
        if ("false".equalsIgnoreCase(propValue)) {
            return false;
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidBooleanValue"));
        Object[] msgArgs = new Object[]{propName};
        SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
        return false;
    }

    void validateMaxSQLLoginName(String propName, String propValue) throws SQLServerException {
        if (propValue != null && propValue.length() > 128) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_propertyMaximumExceedsChars"));
            Object[] msgArgs = new Object[]{propName, Integer.toString(128)};
            SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
        }
    }

    Connection connect(Properties propsIn, SQLServerPooledConnection pooledConnection) throws SQLServerException {
        int loginTimeoutSeconds = SQLServerDriverIntProperty.LOGIN_TIMEOUT.getDefaultValue();
        if (propsIn != null) {
            String sPropValue = propsIn.getProperty(SQLServerDriverIntProperty.LOGIN_TIMEOUT.toString());
            try {
                int sPropValueInt;
                if (null != sPropValue && sPropValue.length() > 0 && 0 != (sPropValueInt = Integer.parseInt(sPropValue))) {
                    loginTimeoutSeconds = sPropValueInt;
                }
            }
            catch (NumberFormatException e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidTimeOut"));
                Object[] msgArgs = new Object[]{sPropValue};
                SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
            }
        }
        if (SqlAuthentication.ACTIVE_DIRECTORY_INTERACTIVE.toString().equalsIgnoreCase(this.authenticationString)) {
            loginTimeoutSeconds *= 10;
        }
        long elapsedSeconds = 0L;
        long start = System.currentTimeMillis();
        int connectRetryAttempt = 0;
        int tlsRetryAttempt = 0;
        while (true) {
            try {
                while (0L != elapsedSeconds && elapsedSeconds >= (long)loginTimeoutSeconds) {
                }
                if (0 < tlsRetryAttempt && 5 > tlsRetryAttempt) {
                    if (connectionlogger.isLoggable(Level.FINE)) {
                        connectionlogger.fine("TLS retry " + tlsRetryAttempt + " of 5 elapsed time " + elapsedSeconds + " secs");
                    }
                } else if (0 < connectRetryAttempt && connectionlogger.isLoggable(Level.FINE)) {
                    connectionlogger.fine("Retrying connection " + connectRetryAttempt + " of " + this.connectRetryCount + " elapsed time " + elapsedSeconds + " secs");
                }
                return this.connectInternal(propsIn, pooledConnection);
            }
            catch (SQLServerException e) {
                elapsedSeconds = (System.currentTimeMillis() - start) / 1000L;
                if (7 == e.getDriverErrorCode() && tlsRetryAttempt < 5 && elapsedSeconds < (long)loginTimeoutSeconds) {
                    if (connectionlogger.isLoggable(Level.FINE)) {
                        connectionlogger.fine("Connection failed during SSL handshake. Retrying due to an intermittent TLS 1.2 failure issue. Retry attempt = " + tlsRetryAttempt + ".");
                    }
                    ++tlsRetryAttempt;
                    continue;
                }
                if (tlsRetryAttempt > 5 && connectionlogger.isLoggable(Level.FINE)) {
                    connectionlogger.fine("Connection failed during SSL handshake. Maximum retry attempt (5) reached.  ");
                }
                if (0 == this.connectRetryCount) {
                    throw e;
                }
                if (connectRetryAttempt++ > this.connectRetryCount) {
                    if (connectionlogger.isLoggable(Level.FINE)) {
                        connectionlogger.fine("Connection failed. Maximum connection retry count " + this.connectRetryCount + " reached.");
                    }
                    throw e;
                }
                SQLServerError sqlServerError = e.getSQLServerError();
                if (!SQLServerError.TransientError.isTransientError(sqlServerError)) {
                    throw e;
                }
                if (elapsedSeconds + (long)this.connectRetryInterval >= (long)loginTimeoutSeconds) {
                    if (connectionlogger.isLoggable(Level.FINEST)) {
                        connectionlogger.finest("Connection failed. No time left to retry timeout will be exceeded: elapsed time(" + elapsedSeconds + ")s + connectRetryInterval(" + this.connectRetryInterval + ")s >= loginTimeout(" + loginTimeoutSeconds + ")s");
                    }
                    throw e;
                }
                if (connectionlogger.isLoggable(Level.FINEST)) {
                    connectionlogger.finest(this.toString() + "Connection failed on transient error " + sqlServerError.getErrorNumber() + ". Wait for connectRetryInterval(" + this.connectRetryInterval + ")s before retry.");
                }
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(this.connectRetryInterval));
                    continue;
                }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    continue;
                }
            }
            break;
        }
    }

    private void registerKeyStoreProviderOnConnection(String keyStoreAuth, String keyStoreSecret, String keyStoreLocation) throws SQLServerException {
        if (null == keyStoreAuth) {
            if (null != keyStoreSecret) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_keyStoreAuthenticationNotSet"));
                Object[] msgArgs = new Object[]{"keyStoreSecret"};
                throw new SQLServerException(form.format(msgArgs), null);
            }
            if (null != keyStoreLocation) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_keyStoreAuthenticationNotSet"));
                Object[] msgArgs = new Object[]{"keyStoreLocation"};
                throw new SQLServerException(form.format(msgArgs), null);
            }
            if (null != this.keyStorePrincipalId) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_keyStoreAuthenticationNotSet"));
                Object[] msgArgs = new Object[]{"keyStorePrincipalId"};
                throw new SQLServerException(form.format(msgArgs), null);
            }
        } else {
            KeyStoreAuthentication auth = KeyStoreAuthentication.valueOfString(keyStoreAuth);
            switch (auth) {
                case JAVA_KEYSTORE_PASSWORD: {
                    this.setKeyStoreSecretAndLocation(keyStoreSecret, keyStoreLocation);
                    break;
                }
                case KEYVAULT_CLIENT_SECRET: {
                    this.setKeyVaultProvider(this.keyStorePrincipalId, keyStoreSecret);
                    break;
                }
                case KEYVAULT_MANAGED_IDENTITY: {
                    this.setKeyVaultProvider(this.keyStorePrincipalId);
                    break;
                }
            }
        }
    }

    private void setKeyStoreSecretAndLocation(String keyStoreSecret, String keyStoreLocation) throws SQLServerException {
        if (null == keyStoreSecret || null == keyStoreLocation) {
            throw new SQLServerException(SQLServerException.getErrString("R_keyStoreSecretOrLocationNotSet"), null);
        }
        SQLServerColumnEncryptionJavaKeyStoreProvider provider = new SQLServerColumnEncryptionJavaKeyStoreProvider(keyStoreLocation, keyStoreSecret.toCharArray());
        this.systemColumnEncryptionKeyStoreProvider.put(provider.getName(), provider);
    }

    private void setKeyVaultProvider(String keyStorePrincipalId) throws SQLServerException {
        SQLServerColumnEncryptionAzureKeyVaultProvider provider = null != keyStorePrincipalId ? new SQLServerColumnEncryptionAzureKeyVaultProvider(keyStorePrincipalId) : new SQLServerColumnEncryptionAzureKeyVaultProvider();
        this.systemColumnEncryptionKeyStoreProvider.put(provider.getName(), provider);
    }

    private void setKeyVaultProvider(String keyStorePrincipalId, String keyStoreSecret) throws SQLServerException {
        if (null == keyStoreSecret) {
            throw new SQLServerException(SQLServerException.getErrString("R_keyStoreSecretNotSet"), null);
        }
        SQLServerColumnEncryptionAzureKeyVaultProvider provider = new SQLServerColumnEncryptionAzureKeyVaultProvider(keyStorePrincipalId, keyStoreSecret);
        this.systemColumnEncryptionKeyStoreProvider.put(provider.getName(), provider);
    }

    int validateTimeout(SQLServerDriverIntProperty property) throws SQLServerException {
        int timeout = property.getDefaultValue();
        String sPropValue = this.activeConnectionProperties.getProperty(property.toString());
        if (null != sPropValue && sPropValue.length() > 0) {
            try {
                timeout = Integer.parseInt(sPropValue);
                if (!property.isValidValue(timeout)) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidTimeOut"));
                    Object[] msgArgs = new Object[]{sPropValue};
                    SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
                }
            }
            catch (NumberFormatException e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidTimeOut"));
                Object[] msgArgs = new Object[]{sPropValue};
                SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
            }
        }
        return timeout;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Connection connectInternal(Properties propsIn, SQLServerPooledConnection pooledConnection) throws SQLServerException {
        try {
            if (propsIn != null) {
                Object[] msgArgs;
                MessageFormat form;
                Object[] msgArgs2;
                MessageFormat form2;
                Object[] msgArgs3;
                MessageFormat form3;
                int defaultSocketTimeout;
                int defaultQueryTimeout;
                Object[] msgArgs4;
                MessageFormat form4;
                Object[] msgArgs5;
                MessageFormat form5;
                Object[] msgArgs6;
                MessageFormat form6;
                String sPropKey;
                String sPropValue;
                this.activeConnectionProperties = (Properties)propsIn.clone();
                this.pooledConnectionParent = pooledConnection;
                String trustStorePassword = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString());
                if (trustStorePassword != null) {
                    this.encryptedTrustStorePassword = SecureStringUtil.getInstance().getEncryptedBytes(trustStorePassword.toCharArray());
                    this.activeConnectionProperties.remove(SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString());
                }
                String hostNameInCertificate = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString());
                if (null == this.originalHostNameInCertificate && null != hostNameInCertificate && !hostNameInCertificate.isEmpty()) {
                    this.originalHostNameInCertificate = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString());
                }
                if (null != this.originalHostNameInCertificate && !this.originalHostNameInCertificate.isEmpty()) {
                    this.activeConnectionProperties.setProperty(SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString(), this.originalHostNameInCertificate);
                }
                if (null == (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.USER.toString()))) {
                    sPropValue = SQLServerDriverStringProperty.USER.getDefaultValue();
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.validateMaxSQLLoginName(sPropKey, sPropValue);
                sPropKey = SQLServerDriverStringProperty.PASSWORD.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = SQLServerDriverStringProperty.PASSWORD.getDefaultValue();
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.validateMaxSQLLoginName(sPropKey, sPropValue);
                sPropKey = SQLServerDriverStringProperty.DATABASE_NAME.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                this.validateMaxSQLLoginName(sPropKey, sPropValue);
                int loginTimeoutSeconds = this.validateTimeout(SQLServerDriverIntProperty.LOGIN_TIMEOUT);
                sPropKey = SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.getDefaultValue());
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.serverNameAsACE = this.isBooleanPropertyOn(sPropKey, sPropValue);
                sPropKey = SQLServerDriverStringProperty.SERVER_NAME.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = "localhost";
                }
                String sPropKeyPort = SQLServerDriverIntProperty.PORT_NUMBER.toString();
                String sPropValuePort = this.activeConnectionProperties.getProperty(sPropKeyPort);
                int px = sPropValue.indexOf(92);
                String instanceValue = null;
                String instanceNameProperty = SQLServerDriverStringProperty.INSTANCE_NAME.toString();
                if (px >= 0) {
                    instanceValue = sPropValue.substring(px + 1, sPropValue.length());
                    this.validateMaxSQLLoginName(instanceNameProperty, instanceValue);
                    sPropValue = sPropValue.substring(0, px);
                }
                this.trustedServerNameAE = sPropValue;
                if (this.serverNameAsACE) {
                    try {
                        sPropValue = IDN.toASCII(sPropValue);
                    }
                    catch (IllegalArgumentException ex) {
                        MessageFormat form7 = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
                        Object[] msgArgs7 = new Object[]{"serverNameAsACE", sPropValue};
                        throw new SQLServerException(form7.format(msgArgs7), ex);
                    }
                }
                this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                String instanceValueFromProp = this.activeConnectionProperties.getProperty(instanceNameProperty);
                if (null != instanceValueFromProp) {
                    instanceValue = instanceValueFromProp;
                }
                if (instanceValue != null) {
                    this.validateMaxSQLLoginName(instanceNameProperty, instanceValue);
                    this.activeConnectionProperties.setProperty(instanceNameProperty, instanceValue);
                    this.trustedServerNameAE = this.trustedServerNameAE + "\\" + instanceValue;
                }
                if (null != sPropValuePort) {
                    this.trustedServerNameAE = this.trustedServerNameAE + ":" + sPropValuePort;
                }
                if (null == (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.IPADDRESS_PREFERENCE.toString()))) {
                    sPropValue = SQLServerDriverStringProperty.IPADDRESS_PREFERENCE.getDefaultValue();
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                } else {
                    this.activeConnectionProperties.setProperty(sPropKey, IPAddressPreference.valueOfString(sPropValue).toString());
                }
                sPropKey = SQLServerDriverStringProperty.APPLICATION_NAME.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null != sPropValue) {
                    this.validateMaxSQLLoginName(sPropKey, sPropValue);
                } else {
                    this.activeConnectionProperties.setProperty(sPropKey, "Microsoft JDBC Driver for SQL Server");
                }
                sPropKey = SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.getDefaultValue());
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                if (null == (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.COLUMN_ENCRYPTION.toString()))) {
                    sPropValue = SQLServerDriverStringProperty.COLUMN_ENCRYPTION.getDefaultValue();
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.columnEncryptionSetting = ColumnEncryptionSetting.valueOfString(sPropValue).toString();
                sPropKey = SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_URL.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null != sPropValue) {
                    this.enclaveAttestationUrl = sPropValue;
                }
                if (null != (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_PROTOCOL.toString()))) {
                    this.enclaveAttestationProtocol = sPropValue;
                    if (this.enclaveAttestationProtocol.equalsIgnoreCase(AttestationProtocol.HGS.toString())) {
                        this.enclaveProvider = new SQLServerVSMEnclaveProvider();
                    } else if (this.enclaveAttestationProtocol.equalsIgnoreCase(AttestationProtocol.NONE.toString())) {
                        this.enclaveProvider = new SQLServerNoneEnclaveProvider();
                    } else if (this.enclaveAttestationProtocol.equalsIgnoreCase(AttestationProtocol.AAS.toString())) {
                        this.enclaveProvider = new SQLServerAASEnclaveProvider();
                    } else {
                        throw new SQLServerException(SQLServerException.getErrString("R_enclaveInvalidAttestationProtocol"), null);
                    }
                }
                if (!((null == this.enclaveAttestationUrl || this.enclaveAttestationUrl.isEmpty() || null != this.enclaveAttestationProtocol && !this.enclaveAttestationProtocol.isEmpty()) && (null == this.enclaveAttestationProtocol || this.enclaveAttestationProtocol.isEmpty() || this.enclaveAttestationProtocol.equalsIgnoreCase(AttestationProtocol.NONE.toString()) || null != this.enclaveAttestationUrl && !this.enclaveAttestationUrl.isEmpty()) && (null == this.enclaveAttestationUrl || this.enclaveAttestationUrl.isEmpty() || null == this.enclaveAttestationProtocol && this.enclaveAttestationProtocol.isEmpty() || null != this.columnEncryptionSetting && this.isColumnEncryptionSettingEnabled()))) {
                    throw new SQLServerException(SQLServerException.getErrString("R_enclavePropertiesError"), null);
                }
                sPropKey = SQLServerDriverStringProperty.KEY_STORE_AUTHENTICATION.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null != sPropValue) {
                    this.keyStoreAuthentication = KeyStoreAuthentication.valueOfString(sPropValue).toString();
                }
                if (null != (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.KEY_STORE_SECRET.toString()))) {
                    this.keyStoreSecret = sPropValue;
                }
                if (null != (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.KEY_STORE_LOCATION.toString()))) {
                    this.keyStoreLocation = sPropValue;
                }
                if (null != (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.KEY_STORE_PRINCIPAL_ID.toString()))) {
                    this.keyStorePrincipalId = sPropValue;
                }
                this.registerKeyStoreProviderOnConnection(this.keyStoreAuthentication, this.keyStoreSecret, this.keyStoreLocation);
                sPropKey = SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_ID.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null != sPropValue) {
                    if (null != this.keyStoreAuthentication) {
                        throw new SQLServerException(SQLServerException.getErrString("R_keyVaultProviderNotSupportedWithKeyStoreAuthentication"), null);
                    }
                    String keyVaultColumnEncryptionProviderClientId = sPropValue;
                    sPropKey = SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_KEY.toString();
                    sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                    if (null == sPropValue) {
                        throw new SQLServerException(SQLServerException.getErrString("R_keyVaultProviderClientKeyNotSet"), null);
                    }
                    String keyVaultColumnEncryptionProviderClientKey = sPropValue;
                    this.setKeyVaultProvider(keyVaultColumnEncryptionProviderClientId, keyVaultColumnEncryptionProviderClientKey);
                }
                if (null == (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.toString()))) {
                    sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.getDefaultValue());
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.multiSubnetFailover = this.isBooleanPropertyOn(sPropKey, sPropValue);
                sPropKey = SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    this.userSetTNIR = false;
                    sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.getDefaultValue());
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.transparentNetworkIPResolution = this.isBooleanPropertyOn(sPropKey, sPropValue);
                sPropKey = SQLServerDriverStringProperty.PREPARE_METHOD.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = SQLServerDriverStringProperty.PREPARE_METHOD.getDefaultValue();
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.setPrepareMethod(PrepareMethod.valueOfString(sPropValue).toString());
                sPropKey = SQLServerDriverStringProperty.ENCRYPT.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = SQLServerDriverStringProperty.ENCRYPT.getDefaultValue();
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.encryptOption = EncryptOption.valueOfString(sPropValue).toString();
                sPropKey = SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.getDefaultValue());
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.trustServerCertificate = this.isBooleanPropertyOn(sPropKey, sPropValue);
                if (this.encryptOption.compareToIgnoreCase(EncryptOption.FALSE.toString()) == 0) {
                    this.requestedEncryptionLevel = 0;
                } else if (this.encryptOption.compareToIgnoreCase(EncryptOption.TRUE.toString()) == 0) {
                    this.requestedEncryptionLevel = 1;
                } else if (this.encryptOption.compareToIgnoreCase(EncryptOption.STRICT.toString()) == 0) {
                    this.requestedEncryptionLevel = (byte)2;
                    if (this.trustServerCertificate && loggerExternal.isLoggable(Level.FINER)) {
                        loggerExternal.finer(this.toString() + " ignore trustServerCertificate for strict");
                    }
                    this.trustServerCertificate = false;
                    sPropKey = SQLServerDriverStringProperty.SERVER_CERTIFICATE.toString();
                    sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                    if (null == sPropValue) {
                        sPropValue = SQLServerDriverStringProperty.SERVER_CERTIFICATE.getDefaultValue();
                    }
                    this.serverCertificate = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.SERVER_CERTIFICATE.toString());
                    this.isTDS8 = true;
                } else {
                    form6 = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
                    msgArgs6 = new Object[]{"encrypt", this.encryptOption};
                    throw new SQLServerException(null, form6.format(msgArgs6), null, 0, false);
                }
                this.trustManagerClass = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.TRUST_MANAGER_CLASS.toString());
                this.trustManagerConstructorArg = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.TRUST_MANAGER_CONSTRUCTOR_ARG.toString());
                sPropKey = SQLServerDriverStringProperty.SELECT_METHOD.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = SQLServerDriverStringProperty.SELECT_METHOD.getDefaultValue();
                }
                this.socketFactoryClass = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.SOCKET_FACTORY_CLASS.toString());
                this.socketFactoryConstructorArg = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.SOCKET_FACTORY_CONSTRUCTOR_ARG.toString());
                if ("cursor".equalsIgnoreCase(sPropValue) || "direct".equalsIgnoreCase(sPropValue)) {
                    sPropValue = sPropValue.toLowerCase(Locale.ENGLISH);
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                    this.selectMethod = sPropValue;
                } else {
                    form6 = new MessageFormat(SQLServerException.getErrString("R_invalidselectMethod"));
                    msgArgs6 = new Object[]{sPropValue};
                    SQLServerException.makeFromDriverError(this, this, form6.format(msgArgs6), null, false);
                }
                sPropKey = SQLServerDriverStringProperty.RESPONSE_BUFFERING.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = SQLServerDriverStringProperty.RESPONSE_BUFFERING.getDefaultValue();
                }
                if ("full".equalsIgnoreCase(sPropValue) || "adaptive".equalsIgnoreCase(sPropValue)) {
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue.toLowerCase(Locale.ENGLISH));
                } else {
                    form6 = new MessageFormat(SQLServerException.getErrString("R_invalidresponseBuffering"));
                    msgArgs6 = new Object[]{sPropValue};
                    SQLServerException.makeFromDriverError(this, this, form6.format(msgArgs6), null, false);
                }
                sPropKey = SQLServerDriverStringProperty.APPLICATION_INTENT.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = SQLServerDriverStringProperty.APPLICATION_INTENT.getDefaultValue();
                }
                this.applicationIntent = ApplicationIntent.valueOfString(sPropValue);
                this.activeConnectionProperties.setProperty(sPropKey, this.applicationIntent.toString());
                sPropKey = SQLServerDriverBooleanProperty.REPLICATION.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.REPLICATION.getDefaultValue());
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.replication = this.isBooleanPropertyOn(sPropKey, sPropValue);
                sPropKey = SQLServerDriverStringProperty.DATETIME_DATATYPE.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = SQLServerDriverStringProperty.DATETIME_DATATYPE.getDefaultValue();
                }
                this.datetimeParameterType = DatetimeType.valueOfString(sPropValue);
                this.activeConnectionProperties.setProperty(sPropKey, this.datetimeParameterType.toString());
                sPropKey = SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.getDefaultValue());
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.sendTimeAsDatetime = this.isBooleanPropertyOn(sPropKey, sPropValue);
                sPropKey = SQLServerDriverBooleanProperty.USE_FMT_ONLY.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.USE_FMT_ONLY.getDefaultValue());
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.useFmtOnly = this.isBooleanPropertyOn(sPropKey, sPropValue);
                sPropKey = SQLServerDriverIntProperty.STATEMENT_POOLING_CACHE_SIZE.toString();
                if (this.activeConnectionProperties.getProperty(sPropKey) != null && this.activeConnectionProperties.getProperty(sPropKey).length() > 0) {
                    try {
                        int n = Integer.parseInt(this.activeConnectionProperties.getProperty(sPropKey));
                        this.setStatementPoolingCacheSize(n);
                    }
                    catch (NumberFormatException e) {
                        form5 = new MessageFormat(SQLServerException.getErrString("R_statementPoolingCacheSize"));
                        msgArgs5 = new Object[]{this.activeConnectionProperties.getProperty(sPropKey)};
                        SQLServerException.makeFromDriverError(this, this, form5.format(msgArgs5), null, false);
                    }
                }
                if (null == (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_ID.toString()))) {
                    sPropValue = SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_ID.getDefaultValue();
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.aadPrincipalID = sPropValue;
                sPropKey = SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_SECRET.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_SECRET.getDefaultValue();
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.aadPrincipalSecret = sPropValue;
                sPropKey = SQLServerDriverBooleanProperty.DISABLE_STATEMENT_POOLING.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null != sPropValue) {
                    this.setDisableStatementPooling(this.isBooleanPropertyOn(sPropKey, sPropValue));
                }
                if (null != (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverBooleanProperty.INTEGRATED_SECURITY.toString()))) {
                    this.integratedSecurity = this.isBooleanPropertyOn(sPropKey, sPropValue);
                }
                if (this.integratedSecurity && null != (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.AUTHENTICATION_SCHEME.toString()))) {
                    this.intAuthScheme = AuthenticationScheme.valueOfString(sPropValue);
                }
                if (this.intAuthScheme == AuthenticationScheme.JAVA_KERBEROS) {
                    sPropKey = SQLServerDriverObjectProperty.GSS_CREDENTIAL.toString();
                    if (this.activeConnectionProperties.containsKey(sPropKey)) {
                        this.impersonatedUserCred = (GSSCredential)this.activeConnectionProperties.get(sPropKey);
                        this.isUserCreatedCredential = true;
                    }
                } else if (this.intAuthScheme == AuthenticationScheme.NTLM) {
                    String sPropKeyDomain = SQLServerDriverStringProperty.DOMAIN.toString();
                    String sPropValueDomain = this.activeConnectionProperties.getProperty(sPropKeyDomain);
                    if (null == sPropValueDomain) {
                        this.activeConnectionProperties.setProperty(sPropKeyDomain, SQLServerDriverStringProperty.DOMAIN.getDefaultValue());
                    }
                    if (this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() || this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty()) {
                        throw new SQLServerException(SQLServerException.getErrString("R_NtlmNoUserPasswordDomain"), null);
                    }
                    this.ntlmAuthentication = true;
                }
                this.accessTokenCallback = (SQLServerAccessTokenCallback)this.activeConnectionProperties.get(SQLServerDriverObjectProperty.ACCESS_TOKEN_CALLBACK.toString());
                boolean bl = this.hasAccessTokenCallbackClass = null != this.activeConnectionProperties.get(SQLServerDriverStringProperty.ACCESS_TOKEN_CALLBACK_CLASS.toString()) && !this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.ACCESS_TOKEN_CALLBACK_CLASS.toString()).isEmpty();
                if (!(null == this.accessTokenCallback && !this.hasAccessTokenCallbackClass || this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() && this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty())) {
                    throw new SQLServerException(SQLServerException.getErrString("R_AccessTokenCallbackWithUserPassword"), null);
                }
                sPropKey = SQLServerDriverStringProperty.ACCESS_TOKEN_CALLBACK_CLASS.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = SQLServerDriverStringProperty.ACCESS_TOKEN_CALLBACK_CLASS.getDefaultValue();
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.setAccessTokenCallbackClass(sPropValue);
                sPropKey = SQLServerDriverStringProperty.AUTHENTICATION.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = SQLServerDriverStringProperty.AUTHENTICATION.getDefaultValue();
                }
                this.authenticationString = SqlAuthentication.valueOfString(sPropValue).toString().trim();
                if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_DEFAULT.toString()) && !this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty()) {
                    MessageFormat form8 = new MessageFormat(SQLServerException.getErrString("R_ManagedIdentityAuthenticationWithPassword"));
                    throw new SQLServerException(form8.format(new Object[]{this.authenticationString}), null);
                }
                if (this.integratedSecurity && !this.authenticationString.equalsIgnoreCase(SqlAuthentication.NOT_SPECIFIED.toString())) {
                    throw new SQLServerException(SQLServerException.getErrString("R_SetAuthenticationWhenIntegratedSecurityTrue"), null);
                }
                if (!(!this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_INTEGRATED.toString()) || this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() && this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty())) {
                    throw new SQLServerException(SQLServerException.getErrString("R_IntegratedAuthenticationWithUserPassword"), null);
                }
                if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_PASSWORD.toString()) && (this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() || this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty())) {
                    throw new SQLServerException(SQLServerException.getErrString("R_NoUserPasswordForActivePassword"), null);
                }
                if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_MANAGED_IDENTITY.toString()) && !this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty()) {
                    MessageFormat form9 = new MessageFormat(SQLServerException.getErrString("R_ManagedIdentityAuthenticationWithPassword"));
                    throw new SQLServerException(form9.format(new Object[]{this.authenticationString}), null);
                }
                if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_SERVICE_PRINCIPAL.toString())) {
                    if ((this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() || this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty()) && (this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_ID.toString()).isEmpty() || this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_SECRET.toString()).isEmpty())) {
                        throw new SQLServerException(SQLServerException.getErrString("R_NoUserPasswordForActiveServicePrincipal"), null);
                    }
                    if (!(this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() && this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty() || this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_ID.toString()).isEmpty() && this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_SECRET.toString()).isEmpty())) {
                        throw new SQLServerException(SQLServerException.getErrString("R_BothUserPasswordandDeprecated"), null);
                    }
                }
                if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.SQLPASSWORD.toString()) && (this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() || this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty())) {
                    throw new SQLServerException(SQLServerException.getErrString("R_NoUserPasswordForSqlPassword"), null);
                }
                sPropKey = SQLServerDriverStringProperty.ACCESS_TOKEN.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null != sPropValue) {
                    this.accessTokenInByte = sPropValue.getBytes(StandardCharsets.UTF_16LE);
                }
                if (null != this.accessTokenInByte && 0 == this.accessTokenInByte.length) {
                    throw new SQLServerException(SQLServerException.getErrString("R_AccessTokenCannotBeEmpty"), null);
                }
                if (this.integratedSecurity && null != this.accessTokenInByte) {
                    throw new SQLServerException(SQLServerException.getErrString("R_SetAccesstokenWhenIntegratedSecurityTrue"), null);
                }
                if (!this.authenticationString.equalsIgnoreCase(SqlAuthentication.NOT_SPECIFIED.toString()) && null != this.accessTokenInByte) {
                    throw new SQLServerException(SQLServerException.getErrString("R_SetBothAuthenticationAndAccessToken"), null);
                }
                if (!(null == this.accessTokenInByte || this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() && this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()).isEmpty())) {
                    throw new SQLServerException(SQLServerException.getErrString("R_AccessTokenWithUserPassword"), null);
                }
                if (!(this.userSetTNIR || this.authenticationString.equalsIgnoreCase(SqlAuthentication.NOT_SPECIFIED.toString()) && null == this.accessTokenInByte)) {
                    this.transparentNetworkIPResolution = false;
                }
                sPropKey = SQLServerDriverStringProperty.WORKSTATION_ID.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                this.validateMaxSQLLoginName(sPropKey, sPropValue);
                int nPort = 0;
                sPropKey = SQLServerDriverIntProperty.PORT_NUMBER.toString();
                try {
                    String strPort = this.activeConnectionProperties.getProperty(sPropKey);
                    if (null != strPort && ((nPort = Integer.parseInt(strPort)) < 0 || nPort > 65535)) {
                        form4 = new MessageFormat(SQLServerException.getErrString("R_invalidPortNumber"));
                        msgArgs4 = new Object[]{Integer.toString(nPort)};
                        SQLServerException.makeFromDriverError(this, this, form4.format(msgArgs4), null, false);
                    }
                }
                catch (NumberFormatException e) {
                    form4 = new MessageFormat(SQLServerException.getErrString("R_invalidPortNumber"));
                    msgArgs4 = new Object[]{this.activeConnectionProperties.getProperty(sPropKey)};
                    SQLServerException.makeFromDriverError(this, this, form4.format(msgArgs4), null, false);
                }
                sPropKey = SQLServerDriverIntProperty.PACKET_SIZE.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null != sPropValue && sPropValue.length() > 0) {
                    try {
                        this.requestedPacketSize = Integer.parseInt(sPropValue);
                        if (-1 == this.requestedPacketSize) {
                            this.requestedPacketSize = 0;
                        } else if (0 == this.requestedPacketSize) {
                            this.requestedPacketSize = Short.MAX_VALUE;
                        }
                    }
                    catch (NumberFormatException e) {
                        this.requestedPacketSize = -1;
                    }
                    if (0 != this.requestedPacketSize && (this.requestedPacketSize < 512 || this.requestedPacketSize > Short.MAX_VALUE)) {
                        form5 = new MessageFormat(SQLServerException.getErrString("R_invalidPacketSize"));
                        msgArgs5 = new Object[]{sPropValue};
                        SQLServerException.makeFromDriverError(this, this, form5.format(msgArgs5), null, false);
                    }
                }
                this.sendStringParametersAsUnicode = null == this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.toString()) ? SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.getDefaultValue() : this.isBooleanPropertyOn(sPropKey, this.activeConnectionProperties.getProperty(sPropKey));
                sPropKey = SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.toString();
                this.lastUpdateCount = this.isBooleanPropertyOn(sPropKey, this.activeConnectionProperties.getProperty(sPropKey));
                sPropKey = SQLServerDriverBooleanProperty.XOPEN_STATES.toString();
                this.xopenStates = this.isBooleanPropertyOn(sPropKey, this.activeConnectionProperties.getProperty(sPropKey));
                sPropKey = SQLServerDriverStringProperty.RESPONSE_BUFFERING.toString();
                this.responseBuffering = null != this.activeConnectionProperties.getProperty(sPropKey) && this.activeConnectionProperties.getProperty(sPropKey).length() > 0 ? this.activeConnectionProperties.getProperty(sPropKey) : null;
                String lockTimeoutKey = SQLServerDriverIntProperty.LOCK_TIMEOUT.toString();
                try {
                    if (!this.setLockTimeout()) {
                        form4 = new MessageFormat(SQLServerException.getErrString("R_invalidLockTimeOut"));
                        msgArgs4 = new Object[]{this.activeConnectionProperties.getProperty(lockTimeoutKey)};
                        SQLServerException.makeFromDriverError(this, this, form4.format(msgArgs4), null, false);
                    }
                }
                catch (NumberFormatException e) {
                    MessageFormat form10 = new MessageFormat(SQLServerException.getErrString("R_invalidLockTimeOut"));
                    Object[] msgArgs8 = new Object[]{this.activeConnectionProperties.getProperty(lockTimeoutKey)};
                    SQLServerException.makeFromDriverError(this, this, form10.format(msgArgs8), null, false);
                }
                sPropKey = SQLServerDriverIntProperty.QUERY_TIMEOUT.toString();
                this.queryTimeoutSeconds = defaultQueryTimeout = SQLServerDriverIntProperty.QUERY_TIMEOUT.getDefaultValue();
                if (this.activeConnectionProperties.getProperty(sPropKey) != null && this.activeConnectionProperties.getProperty(sPropKey).length() > 0) {
                    Object[] msgArgs9;
                    MessageFormat form11;
                    try {
                        int n = Integer.parseInt(this.activeConnectionProperties.getProperty(sPropKey));
                        if (n >= defaultQueryTimeout) {
                            this.queryTimeoutSeconds = n;
                        } else {
                            form11 = new MessageFormat(SQLServerException.getErrString("R_invalidQueryTimeout"));
                            msgArgs9 = new Object[]{this.activeConnectionProperties.getProperty(sPropKey)};
                            SQLServerException.makeFromDriverError(this, this, form11.format(msgArgs9), null, false);
                        }
                    }
                    catch (NumberFormatException e) {
                        form11 = new MessageFormat(SQLServerException.getErrString("R_invalidQueryTimeout"));
                        msgArgs9 = new Object[]{this.activeConnectionProperties.getProperty(sPropKey)};
                        SQLServerException.makeFromDriverError(this, this, form11.format(msgArgs9), null, false);
                    }
                }
                sPropKey = SQLServerDriverIntProperty.SOCKET_TIMEOUT.toString();
                this.socketTimeoutMilliseconds = defaultSocketTimeout = SQLServerDriverIntProperty.SOCKET_TIMEOUT.getDefaultValue();
                if (this.activeConnectionProperties.getProperty(sPropKey) != null && this.activeConnectionProperties.getProperty(sPropKey).length() > 0) {
                    Object[] msgArgs10;
                    MessageFormat form12;
                    try {
                        int n = Integer.parseInt(this.activeConnectionProperties.getProperty(sPropKey));
                        if (n >= defaultSocketTimeout) {
                            this.socketTimeoutMilliseconds = n;
                        } else {
                            form12 = new MessageFormat(SQLServerException.getErrString("R_invalidSocketTimeout"));
                            msgArgs10 = new Object[]{this.activeConnectionProperties.getProperty(sPropKey)};
                            SQLServerException.makeFromDriverError(this, this, form12.format(msgArgs10), null, false);
                        }
                    }
                    catch (NumberFormatException e) {
                        form12 = new MessageFormat(SQLServerException.getErrString("R_invalidSocketTimeout"));
                        msgArgs10 = new Object[]{this.activeConnectionProperties.getProperty(sPropKey)};
                        SQLServerException.makeFromDriverError(this, this, form12.format(msgArgs10), null, false);
                    }
                }
                sPropKey = SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.toString();
                int cancelQueryTimeout = SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.getDefaultValue();
                if (this.activeConnectionProperties.getProperty(sPropKey) != null && this.activeConnectionProperties.getProperty(sPropKey).length() > 0) {
                    try {
                        int n = Integer.parseInt(this.activeConnectionProperties.getProperty(sPropKey));
                        if (n >= cancelQueryTimeout) {
                            if (this.queryTimeoutSeconds > defaultQueryTimeout) {
                                this.cancelQueryTimeoutSeconds = n;
                            }
                        } else {
                            form3 = new MessageFormat(SQLServerException.getErrString("R_invalidCancelQueryTimeout"));
                            msgArgs3 = new Object[]{this.activeConnectionProperties.getProperty(sPropKey)};
                            SQLServerException.makeFromDriverError(this, this, form3.format(msgArgs3), null, false);
                        }
                    }
                    catch (NumberFormatException e) {
                        form3 = new MessageFormat(SQLServerException.getErrString("R_invalidCancelQueryTimeout"));
                        msgArgs3 = new Object[]{this.activeConnectionProperties.getProperty(sPropKey)};
                        SQLServerException.makeFromDriverError(this, this, form3.format(msgArgs3), null, false);
                    }
                }
                if (this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverIntProperty.SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD.toString()) != null && this.activeConnectionProperties.getProperty(sPropKey).length() > 0) {
                    try {
                        int n = Integer.parseInt(this.activeConnectionProperties.getProperty(sPropKey));
                        this.setServerPreparedStatementDiscardThreshold(n);
                    }
                    catch (NumberFormatException e) {
                        form3 = new MessageFormat(SQLServerException.getErrString("R_serverPreparedStatementDiscardThreshold"));
                        msgArgs3 = new Object[]{this.activeConnectionProperties.getProperty(sPropKey)};
                        SQLServerException.makeFromDriverError(this, this, form3.format(msgArgs3), null, false);
                    }
                }
                if (null != (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverBooleanProperty.ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT.toString()))) {
                    this.setEnablePrepareOnFirstPreparedStatementCall(this.isBooleanPropertyOn(sPropKey, sPropValue));
                }
                if (null != (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverBooleanProperty.USE_BULK_COPY_FOR_BATCH_INSERT.toString()))) {
                    this.useBulkCopyForBatchInsert = this.isBooleanPropertyOn(sPropKey, sPropValue);
                }
                if (null == (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.SSL_PROTOCOL.toString()))) {
                    sPropValue = SQLServerDriverStringProperty.SSL_PROTOCOL.getDefaultValue();
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                } else {
                    this.activeConnectionProperties.setProperty(sPropKey, SSLProtocol.valueOfString(sPropValue).toString());
                }
                sPropKey = SQLServerDriverStringProperty.MSI_CLIENT_ID.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null != sPropValue) {
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_SERVICE_PRINCIPAL_CERTIFICATE.toString())) {
                    sPropKey = SQLServerDriverStringProperty.CLIENT_CERTIFICATE.toString();
                    sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                    if (null != sPropValue) {
                        this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                        this.servicePrincipalCertificate = sPropValue;
                    }
                    if (null != (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.CLIENT_KEY.toString()))) {
                        this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                        this.servicePrincipalCertificateKey = sPropValue;
                    }
                    if (null != (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.CLIENT_KEY_PASSWORD.toString()))) {
                        this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                        this.servicePrincipalCertificatePassword = sPropValue;
                    }
                    if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_SERVICE_PRINCIPAL_CERTIFICATE.toString()) && (this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()).isEmpty() && this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_ID.toString()).isEmpty() || null == this.servicePrincipalCertificate || this.servicePrincipalCertificate.isEmpty())) {
                        throw new SQLServerException(SQLServerException.getErrString("R_NoUserOrCertForActiveServicePrincipalCertificate"), null);
                    }
                } else {
                    sPropKey = SQLServerDriverStringProperty.CLIENT_CERTIFICATE.toString();
                    sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                    if (null != sPropValue) {
                        this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                        this.clientCertificate = sPropValue;
                    }
                    if (null != (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.CLIENT_KEY.toString()))) {
                        this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                        this.clientKey = sPropValue;
                    }
                    if (null != (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverStringProperty.CLIENT_KEY_PASSWORD.toString()))) {
                        this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                        this.clientKeyPassword = sPropValue;
                    }
                }
                if (null != (sPropValue = this.activeConnectionProperties.getProperty(sPropKey = SQLServerDriverBooleanProperty.SEND_TEMPORAL_DATATYPES_AS_STRING_FOR_BULK_COPY.toString()))) {
                    this.sendTemporalDataTypesAsStringForBulkCopy = this.isBooleanPropertyOn(sPropKey, sPropValue);
                }
                sPropKey = SQLServerDriverStringProperty.MAX_RESULT_BUFFER.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                this.activeConnectionProperties.setProperty(sPropKey, String.valueOf(MaxResultBufferParser.validateMaxResultBuffer(sPropValue)));
                sPropKey = SQLServerDriverBooleanProperty.DELAY_LOADING_LOBS.toString();
                sPropValue = this.activeConnectionProperties.getProperty(sPropKey);
                if (null == sPropValue) {
                    sPropValue = Boolean.toString(SQLServerDriverBooleanProperty.DELAY_LOADING_LOBS.getDefaultValue());
                    this.activeConnectionProperties.setProperty(sPropKey, sPropValue);
                }
                this.delayLoadingLobs = this.isBooleanPropertyOn(sPropKey, sPropValue);
                FailoverInfo fo = null;
                String databaseNameProperty = SQLServerDriverStringProperty.DATABASE_NAME.toString();
                String serverNameProperty = SQLServerDriverStringProperty.SERVER_NAME.toString();
                String failOverPartnerProperty = SQLServerDriverStringProperty.FAILOVER_PARTNER.toString();
                String failOverPartnerPropertyValue = this.activeConnectionProperties.getProperty(failOverPartnerProperty);
                if (this.multiSubnetFailover && failOverPartnerPropertyValue != null) {
                    SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_dbMirroringWithMultiSubnetFailover"), null, false);
                }
                if ((this.multiSubnetFailover || null != failOverPartnerPropertyValue) && !this.userSetTNIR) {
                    this.transparentNetworkIPResolution = false;
                }
                if (this.applicationIntent != null && this.applicationIntent.equals((Object)ApplicationIntent.READ_ONLY) && failOverPartnerPropertyValue != null) {
                    SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_dbMirroringWithReadOnlyIntent"), null, false);
                }
                if (null != this.activeConnectionProperties.getProperty(databaseNameProperty)) {
                    fo = FailoverMapSingleton.getFailoverInfo(this, this.activeConnectionProperties.getProperty(serverNameProperty), this.activeConnectionProperties.getProperty(instanceNameProperty), this.activeConnectionProperties.getProperty(databaseNameProperty));
                } else if (null != failOverPartnerPropertyValue) {
                    SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_failoverPartnerWithoutDB"), null, true);
                }
                String mirror = null == fo ? failOverPartnerPropertyValue : null;
                this.connectRetryCount = SQLServerDriverIntProperty.CONNECT_RETRY_COUNT.getDefaultValue();
                sPropValue = this.activeConnectionProperties.getProperty(SQLServerDriverIntProperty.CONNECT_RETRY_COUNT.toString());
                if (null != sPropValue && sPropValue.length() > 0) {
                    try {
                        this.connectRetryCount = Integer.parseInt(sPropValue);
                    }
                    catch (NumberFormatException e) {
                        form2 = new MessageFormat(SQLServerException.getErrString("R_invalidConnectRetryCount"));
                        msgArgs2 = new Object[]{sPropValue};
                        SQLServerException.makeFromDriverError(this, this, form2.format(msgArgs2), null, false);
                    }
                    if (this.connectRetryCount < 0 || this.connectRetryCount > 255) {
                        form = new MessageFormat(SQLServerException.getErrString("R_invalidConnectRetryCount"));
                        msgArgs = new Object[]{sPropValue};
                        SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
                    }
                }
                this.connectRetryInterval = SQLServerDriverIntProperty.CONNECT_RETRY_INTERVAL.getDefaultValue();
                sPropValue = this.activeConnectionProperties.getProperty(SQLServerDriverIntProperty.CONNECT_RETRY_INTERVAL.toString());
                if (null != sPropValue && sPropValue.length() > 0) {
                    try {
                        this.connectRetryInterval = Integer.parseInt(sPropValue);
                    }
                    catch (NumberFormatException e) {
                        form2 = new MessageFormat(SQLServerException.getErrString("R_invalidConnectRetryInterval"));
                        msgArgs2 = new Object[]{sPropValue};
                        SQLServerException.makeFromDriverError(this, this, form2.format(msgArgs2), null, false);
                    }
                    if (this.connectRetryInterval < 1 || this.connectRetryInterval > 60) {
                        form = new MessageFormat(SQLServerException.getErrString("R_invalidConnectRetryInterval"));
                        msgArgs = new Object[]{sPropValue};
                        SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
                    }
                }
                long startTime = System.currentTimeMillis();
                this.sessionRecovery.setLoginParameters(instanceValue, nPort, fo, loginTimeoutSeconds > this.queryTimeoutSeconds && this.queryTimeoutSeconds > 0 ? this.queryTimeoutSeconds : loginTimeoutSeconds);
                this.login(this.activeConnectionProperties.getProperty(serverNameProperty), instanceValue, nPort, mirror, fo, loginTimeoutSeconds, startTime);
            } else {
                long startTime = System.currentTimeMillis();
                this.login(this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.SERVER_NAME.toString()), this.sessionRecovery.getInstanceValue(), this.sessionRecovery.getNPort(), this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.FAILOVER_PARTNER.toString()), this.sessionRecovery.getFailoverInfo(), this.sessionRecovery.getLoginTimeoutSeconds(), startTime);
            }
            if (1 == this.negotiatedEncryptionLevel || 3 == this.negotiatedEncryptionLevel) {
                int sslRecordSize;
                int n = sslRecordSize = Util.isIBM() ? 8192 : 16384;
                if (this.tdsPacketSize > sslRecordSize) {
                    if (connectionlogger.isLoggable(Level.FINER)) {
                        connectionlogger.finer(this.toString() + " Negotiated tdsPacketSize " + this.tdsPacketSize + " is too large for SSL with JRE " + Util.SYSTEM_JRE + " (max size is " + sslRecordSize + ")");
                    }
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_packetSizeTooBigForSSL"));
                    Object[] msgArgs = new Object[]{Integer.toString(sslRecordSize)};
                    this.terminate(6, form.format(msgArgs));
                }
            }
            this.state = State.OPENED;
            if (connectionlogger.isLoggable(Level.FINER)) {
                connectionlogger.finer(this.toString() + " End of connect");
            }
        }
        finally {
            if (!this.state.equals((Object)State.OPENED) && !this.state.equals((Object)State.CLOSED)) {
                this.close();
            }
            this.activeConnectionProperties.remove(SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString());
        }
        return this;
    }

    private void login(String primary, String primaryInstanceName, int primaryPortNumber, String mirror, FailoverInfo foActual, int timeout, long timerStart) throws SQLServerException {
        Object[] msgArgs;
        boolean isDBMirroring = null != mirror || null != foActual;
        int sleepInterval = 100;
        boolean useFailoverHost = false;
        FailoverInfo tempFailover = null;
        ServerPortPlaceHolder currentFOPlaceHolder = null;
        ServerPortPlaceHolder currentPrimaryPlaceHolder = null;
        if (null != foActual) {
            tempFailover = foActual;
            useFailoverHost = foActual.getUseFailoverPartner();
        } else if (isDBMirroring) {
            tempFailover = new FailoverInfo(mirror, false);
        }
        boolean useParallel = this.getMultiSubnetFailover();
        boolean useTnir = this.getTransparentNetworkIPResolution();
        if (0 == timeout) {
            timeout = SQLServerDriverIntProperty.LOGIN_TIMEOUT.getDefaultValue();
        }
        long timerTimeout = (long)timeout * 1000L;
        this.timerExpire = timerStart + timerTimeout;
        long timeoutUnitInterval = isDBMirroring || useParallel ? (long)(0.08f * (float)timerTimeout) : (useTnir ? (long)(0.125f * (float)timerTimeout) : timerTimeout);
        long intervalExpire = timerStart + timeoutUnitInterval;
        long intervalExpireFullTimeout = timerStart + timerTimeout;
        if (connectionlogger.isLoggable(Level.FINER)) {
            connectionlogger.finer(this.toString() + " Start time: " + timerStart + " Time out time: " + this.timerExpire + " Timeout Unit Interval: " + timeoutUnitInterval);
        }
        int attemptNumber = 0;
        int noOfRedirections = 0;
        while (true) {
            block43: {
                this.clientConnectionId = null;
                this.state = State.INITIALIZED;
                try {
                    String msg;
                    if (isDBMirroring && useFailoverHost) {
                        if (null == currentFOPlaceHolder) {
                            currentFOPlaceHolder = tempFailover.failoverPermissionCheck(this, this.integratedSecurity);
                        }
                        this.currentConnectPlaceHolder = currentFOPlaceHolder;
                    } else {
                        if (this.routingInfo != null) {
                            currentPrimaryPlaceHolder = this.routingInfo;
                            this.routingInfo = null;
                        } else if (null == currentPrimaryPlaceHolder) {
                            currentPrimaryPlaceHolder = this.primaryPermissionCheck(primary, primaryInstanceName, primaryPortNumber);
                        }
                        this.currentConnectPlaceHolder = currentPrimaryPlaceHolder;
                    }
                    if (connectionlogger.isLoggable(Level.FINE)) {
                        connectionlogger.fine(this.toString() + " This attempt server name: " + this.currentConnectPlaceHolder.getServerName() + " port: " + this.currentConnectPlaceHolder.getPortNumber() + " InstanceName: " + this.currentConnectPlaceHolder.getInstanceName() + " useParallel: " + useParallel);
                        connectionlogger.fine(this.toString() + " This attempt endtime: " + intervalExpire);
                        connectionlogger.fine(this.toString() + " This attempt No: " + attemptNumber);
                    }
                    InetSocketAddress inetSocketAddress = this.connectHelper(this.currentConnectPlaceHolder, SQLServerConnection.timerRemaining(intervalExpire), timeout, useParallel, useTnir, 0 == attemptNumber, SQLServerConnection.timerRemaining(intervalExpireFullTimeout));
                    if (this.serverSupportsDNSCaching) {
                        dnsCache.put(this.currentConnectPlaceHolder.getServerName(), inetSocketAddress);
                    }
                    if (!this.isRoutedInCurrentAttempt) break;
                    if (isDBMirroring) {
                        msg = SQLServerException.getErrString("R_invalidRoutingInfo");
                        this.terminate(6, msg);
                    }
                    if (++noOfRedirections > 1) {
                        msg = SQLServerException.getErrString("R_multipleRedirections");
                        this.terminate(6, msg);
                    }
                    if (this.tdsChannel != null) {
                        this.tdsChannel.close();
                    }
                    this.initResettableValues();
                    this.resetNonRoutingEnvchangeValues();
                    ++attemptNumber;
                    useParallel = false;
                    useTnir = false;
                    intervalExpire = this.timerExpire;
                    if (SQLServerConnection.timerHasExpired(this.timerExpire)) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_tcpipConnectionFailed"));
                        msgArgs = new Object[]{this.getServerNameString(this.currentConnectPlaceHolder.getServerName()), Integer.toString(this.currentConnectPlaceHolder.getPortNumber()), SQLServerException.getErrString("R_timedOutBeforeRouting")};
                        String msg2 = form.format(msgArgs);
                        this.terminate(6, msg2);
                        break block43;
                    }
                    this.isRoutedInCurrentAttempt = false;
                    continue;
                }
                catch (SQLServerException sqlex) {
                    long remainingMilliseconds;
                    int errorCode = sqlex.getErrorCode();
                    int driverErrorCode = sqlex.getDriverErrorCode();
                    if (18456 == errorCode || 18488 == errorCode || 18486 == errorCode || 4 == driverErrorCode || 5 == driverErrorCode || 7 == driverErrorCode || 6 == driverErrorCode || 8 == driverErrorCode && (!isDBMirroring || attemptNumber > 0) || SQLServerConnection.timerHasExpired(this.timerExpire)) {
                        this.close();
                        throw sqlex;
                    }
                    if (null != this.tdsChannel) {
                        this.tdsChannel.close();
                    }
                    if (isDBMirroring && 1 != attemptNumber % 2 || (remainingMilliseconds = (long)SQLServerConnection.timerRemaining(this.timerExpire)) > (long)sleepInterval) break block43;
                    throw sqlex;
                }
            }
            if (!isDBMirroring || 1 == attemptNumber % 2) {
                if (connectionlogger.isLoggable(Level.FINE)) {
                    connectionlogger.fine(this.toString() + " sleeping milisec: " + sleepInterval);
                }
                try {
                    Thread.sleep(sleepInterval);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                sleepInterval = sleepInterval < 500 ? sleepInterval * 2 : 1000;
            }
            ++attemptNumber;
            if (useParallel) {
                intervalExpire = System.currentTimeMillis() + timeoutUnitInterval * (long)(attemptNumber + 1);
            } else if (isDBMirroring) {
                intervalExpire = System.currentTimeMillis() + timeoutUnitInterval * (long)(attemptNumber / 2 + 1);
            } else if (useTnir) {
                long timeSlice = timeoutUnitInterval * (long)(1 << attemptNumber);
                if (1 == attemptNumber && 500L > timeSlice) {
                    timeSlice = 500L;
                }
                intervalExpire = System.currentTimeMillis() + timeSlice;
            } else {
                intervalExpire = this.timerExpire;
            }
            if (intervalExpire > this.timerExpire) {
                intervalExpire = this.timerExpire;
            }
            if (!isDBMirroring) continue;
            useFailoverHost = !useFailoverHost;
        }
        if (useFailoverHost && null == this.failoverPartnerServerProvided) {
            Object curserverinfo = this.currentConnectPlaceHolder.getServerName();
            if (null != currentFOPlaceHolder.getInstanceName()) {
                curserverinfo = (String)curserverinfo + "\\";
                curserverinfo = (String)curserverinfo + currentFOPlaceHolder.getInstanceName();
            }
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPartnerConfiguration"));
            msgArgs = new Object[]{this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.DATABASE_NAME.toString()), curserverinfo};
            this.terminate(6, form.format(msgArgs));
        }
        if (null != this.failoverPartnerServerProvided) {
            String msg;
            if (this.multiSubnetFailover) {
                msg = SQLServerException.getErrString("R_dbMirroringWithMultiSubnetFailover");
                this.terminate(6, msg);
            }
            if (this.applicationIntent != null && this.applicationIntent.equals((Object)ApplicationIntent.READ_ONLY)) {
                msg = SQLServerException.getErrString("R_dbMirroringWithReadOnlyIntent");
                this.terminate(6, msg);
            }
            if (null == tempFailover) {
                tempFailover = new FailoverInfo(this.failoverPartnerServerProvided, false);
            }
            if (null != foActual) {
                foActual.failoverAdd(this, useFailoverHost, this.failoverPartnerServerProvided);
            } else {
                String databaseNameProperty = SQLServerDriverStringProperty.DATABASE_NAME.toString();
                String instanceNameProperty = SQLServerDriverStringProperty.INSTANCE_NAME.toString();
                String serverNameProperty = SQLServerDriverStringProperty.SERVER_NAME.toString();
                if (connectionlogger.isLoggable(Level.FINE)) {
                    connectionlogger.fine(this.toString() + " adding new failover info server: " + this.activeConnectionProperties.getProperty(serverNameProperty) + " instance: " + this.activeConnectionProperties.getProperty(instanceNameProperty) + " database: " + this.activeConnectionProperties.getProperty(databaseNameProperty) + " server provided failover: " + this.failoverPartnerServerProvided);
                }
                tempFailover.failoverAdd(this, useFailoverHost, this.failoverPartnerServerProvided);
                FailoverMapSingleton.putFailoverInfo(this, primary, this.activeConnectionProperties.getProperty(instanceNameProperty), this.activeConnectionProperties.getProperty(databaseNameProperty), tempFailover, useFailoverHost, this.failoverPartnerServerProvided);
            }
        }
    }

    boolean isFatalError(SQLServerException e) {
        return 18456 == e.getErrorCode() || 18488 == e.getErrorCode() || 18486 == e.getErrorCode() || 4 == e.getDriverErrorCode() || 5 == e.getDriverErrorCode() || 7 == e.getDriverErrorCode() || 6 == e.getDriverErrorCode() || 8 == e.getDriverErrorCode();
    }

    void resetNonRoutingEnvchangeValues() {
        this.tdsPacketSize = 4096;
        this.databaseCollation = null;
        this.rolledBackTransaction = false;
        Arrays.fill(this.getTransactionDescriptor(), (byte)0);
        this.sCatalog = this.originalCatalog;
        this.failoverPartnerServerProvided = null;
    }

    ServerPortPlaceHolder primaryPermissionCheck(String primary, String primaryInstanceName, int primaryPortNumber) throws SQLServerException {
        block8: {
            if (0 == primaryPortNumber) {
                if (null != primaryInstanceName) {
                    String instancePort = this.getInstancePort(primary, primaryInstanceName);
                    if (connectionlogger.isLoggable(Level.FINER)) {
                        connectionlogger.fine(this.toString() + " SQL Server port returned by SQL Browser: " + instancePort);
                    }
                    try {
                        if (null != instancePort) {
                            primaryPortNumber = Integer.parseInt(instancePort);
                            if (primaryPortNumber < 0 || primaryPortNumber > 65535) {
                                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPortNumber"));
                                Object[] msgArgs = new Object[]{Integer.toString(primaryPortNumber)};
                                SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
                            }
                            break block8;
                        }
                        primaryPortNumber = DEFAULTPORT;
                    }
                    catch (NumberFormatException e) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidPortNumber"));
                        Object[] msgArgs = new Object[]{primaryPortNumber};
                        SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
                    }
                } else {
                    primaryPortNumber = DEFAULTPORT;
                }
            }
        }
        this.activeConnectionProperties.setProperty(SQLServerDriverIntProperty.PORT_NUMBER.toString(), String.valueOf(primaryPortNumber));
        return new ServerPortPlaceHolder(primary, primaryPortNumber, primaryInstanceName, this.integratedSecurity);
    }

    static boolean timerHasExpired(long timerExpire) {
        return System.currentTimeMillis() > timerExpire;
    }

    static int timerRemaining(long timerExpire) {
        long remaining = timerExpire - System.currentTimeMillis();
        return (int)(remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (remaining <= 0L ? 1L : remaining));
    }

    private InetSocketAddress connectHelper(ServerPortPlaceHolder serverInfo, int timeOutSliceInMillis, int timeOutFullInSeconds, boolean useParallel, boolean useTnir, boolean isTnirFirstAttempt, int timeOutsliceInMillisForFullTimeout) throws SQLServerException {
        if (connectionlogger.isLoggable(Level.FINE)) {
            connectionlogger.fine(this.toString() + " Connecting with server: " + serverInfo.getServerName() + " port: " + serverInfo.getPortNumber() + " Timeout slice: " + timeOutSliceInMillis + " Timeout Full: " + timeOutFullInSeconds);
        }
        this.hostName = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.WORKSTATION_ID.toString());
        if (StringUtils.isEmpty(this.hostName)) {
            this.hostName = Util.lookupHostName();
        }
        this.tdsChannel = new TDSChannel(this);
        String iPAddressPreference = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.IPADDRESS_PREFERENCE.toString());
        InetSocketAddress inetSocketAddress = this.tdsChannel.open(serverInfo.getParsedServerName(), serverInfo.getPortNumber(), 0 == timeOutFullInSeconds ? 0 : timeOutSliceInMillis, useParallel, useTnir, isTnirFirstAttempt, timeOutsliceInMillisForFullTimeout, iPAddressPreference);
        this.setState(State.CONNECTED);
        try {
            this.clientConnectionId = UUID.randomUUID();
        }
        catch (InternalError e) {
            if (connectionlogger.isLoggable(Level.FINER)) {
                connectionlogger.finer(this.toString() + " Generating a random UUID has failed due to : " + e.getMessage() + "Retrying once.");
            }
            this.clientConnectionId = UUID.randomUUID();
        }
        assert (null != this.clientConnectionId);
        if (this.isTDS8) {
            this.tdsChannel.enableSSL(serverInfo.getParsedServerName(), serverInfo.getPortNumber(), this.clientCertificate, this.clientKey, this.clientKeyPassword, this.isTDS8);
            this.clientKeyPassword = "";
        }
        this.prelogin(serverInfo.getServerName(), serverInfo.getPortNumber());
        if (!this.isTDS8 && 2 != this.negotiatedEncryptionLevel) {
            this.tdsChannel.enableSSL(serverInfo.getParsedServerName(), serverInfo.getPortNumber(), this.clientCertificate, this.clientKey, this.clientKeyPassword, false);
            this.clientKeyPassword = "";
        }
        this.activeConnectionProperties.remove(SQLServerDriverStringProperty.CLIENT_KEY_PASSWORD.toString());
        if (this.sessionRecovery.isReconnectRunning()) {
            if (this.negotiatedEncryptionLevel != this.sessionRecovery.getSessionStateTable().getOriginalNegotiatedEncryptionLevel()) {
                if (connectionlogger.isLoggable(Level.WARNING)) {
                    connectionlogger.warning(this.toString() + " The server did not preserve SSL encryption during a recovery attempt, connection recovery is not possible.");
                }
                this.terminate(6, SQLServerException.getErrString("R_crClientSSLStateNotRecoverable"));
            }
            try {
                this.executeReconnect(new LogonCommand());
            }
            catch (SQLServerException e) {
                throw new SQLServerException(SQLServerException.getErrString("R_crServerSessionStateNotRecoverable"), e);
            }
        } else {
            if (this.connectRetryCount > 0 && null == this.sessionRecovery.getSessionStateTable()) {
                this.sessionRecovery.setSessionStateTable(new SessionStateTable());
                this.sessionRecovery.getSessionStateTable().setOriginalNegotiatedEncryptionLevel(this.negotiatedEncryptionLevel);
            }
            this.executeCommand(new LogonCommand());
        }
        return inetSocketAddress;
    }

    private void executeReconnect(LogonCommand logonCommand) throws SQLServerException {
        logonCommand.execute(this.tdsChannel.getWriter(), this.tdsChannel.getReader(logonCommand));
    }

    void prelogin(String serverName, int portNumber) throws SQLServerException {
        int fedAuthOffset;
        byte messageLength;
        if (!this.authenticationString.equalsIgnoreCase(SqlAuthentication.NOT_SPECIFIED.toString()) || null != this.accessTokenInByte || null != this.accessTokenCallback || this.hasAccessTokenCallbackClass) {
            this.fedAuthRequiredByUser = true;
        }
        if (this.fedAuthRequiredByUser) {
            messageLength = 73;
            this.requestedEncryptionLevel = 1;
            fedAuthOffset = 5;
        } else {
            messageLength = 67;
            fedAuthOffset = 0;
        }
        byte[] preloginRequest = new byte[messageLength];
        int preloginRequestOffset = 0;
        byte[] bufferHeader = new byte[]{18, 1, 0, messageLength, 0, 0, 0, 0};
        System.arraycopy(bufferHeader, 0, preloginRequest, preloginRequestOffset, bufferHeader.length);
        byte[] preloginOptionsBeforeFedAuth = new byte[]{0, 0, (byte)(16 + fedAuthOffset), 0, 6, 1, 0, (byte)(22 + fedAuthOffset), 0, 1, 5, 0, (byte)(23 + fedAuthOffset), 0, 36};
        System.arraycopy(preloginOptionsBeforeFedAuth, 0, preloginRequest, preloginRequestOffset += bufferHeader.length, preloginOptionsBeforeFedAuth.length);
        preloginRequestOffset += preloginOptionsBeforeFedAuth.length;
        if (this.fedAuthRequiredByUser) {
            byte[] preloginOptions2 = new byte[]{6, 0, 64, 0, 1};
            System.arraycopy(preloginOptions2, 0, preloginRequest, preloginRequestOffset, preloginOptions2.length);
            preloginRequestOffset += preloginOptions2.length;
        }
        preloginRequest[preloginRequestOffset] = -1;
        byte[] preloginOptionData = new byte[]{12, 4, 0, 2, 0, 0, null == this.clientCertificate ? this.requestedEncryptionLevel : (byte)(this.requestedEncryptionLevel | 0xFFFFFF80), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        System.arraycopy(preloginOptionData, 0, preloginRequest, ++preloginRequestOffset, preloginOptionData.length);
        preloginRequestOffset += preloginOptionData.length;
        if (this.fedAuthRequiredByUser) {
            preloginRequest[preloginRequestOffset] = 1;
            ++preloginRequestOffset;
        }
        byte[] preloginResponse = new byte[4096];
        String preloginErrorLogString = " Prelogin error: host " + serverName + " port " + portNumber;
        byte[] conIdByteArray = Util.asGuidByteArray(this.clientConnectionId);
        int offset = this.fedAuthRequiredByUser ? preloginRequest.length - 36 - 1 : preloginRequest.length - 36;
        System.arraycopy(conIdByteArray, 0, preloginRequest, offset, conIdByteArray.length);
        ActivityId activityId = ActivityCorrelator.getNext();
        byte[] actIdByteArray = Util.asGuidByteArray(activityId.getId());
        System.arraycopy(actIdByteArray, 0, preloginRequest, offset += conIdByteArray.length, actIdByteArray.length);
        long seqNum = activityId.getSequence();
        Util.writeInt((int)seqNum, preloginRequest, offset += actIdByteArray.length);
        offset += 4;
        if (Util.isActivityTraceOn() && connectionlogger.isLoggable(Level.FINER)) {
            connectionlogger.finer(this.toString() + " ActivityId " + activityId);
        }
        if (connectionlogger.isLoggable(Level.FINER)) {
            connectionlogger.finer(this.toString() + " Requesting encryption level:" + TDS.getEncryptionLevel(this.requestedEncryptionLevel));
        }
        if (this.tdsChannel.isLoggingPackets()) {
            this.tdsChannel.logPacket(preloginRequest, 0, preloginRequest.length, this.toString() + " Prelogin request");
        }
        try {
            this.tdsChannel.write(preloginRequest, 0, preloginRequest.length);
            this.tdsChannel.flush();
        }
        catch (SQLServerException e) {
            connectionlogger.warning(this.toString() + preloginErrorLogString + " Error sending prelogin request: " + e.getMessage());
            throw e;
        }
        int responseLength = preloginResponse.length;
        int responseBytesRead = 0;
        boolean processedResponseHeader = false;
        while (responseBytesRead < responseLength) {
            Object[] msgArgs;
            MessageFormat form;
            int bytesRead;
            try {
                bytesRead = this.tdsChannel.read(preloginResponse, responseBytesRead, responseLength - responseBytesRead);
            }
            catch (SQLServerException e) {
                connectionlogger.warning(this.toString() + preloginErrorLogString + " Error reading prelogin response: " + e.getMessage());
                throw e;
            }
            if (-1 == bytesRead) {
                if (connectionlogger.isLoggable(Level.WARNING)) {
                    connectionlogger.warning(this.toString() + preloginErrorLogString + " Unexpected end of prelogin response after " + responseBytesRead + " bytes read");
                }
                form = new MessageFormat(SQLServerException.getErrString("R_tcpipConnectionFailed"));
                msgArgs = new Object[]{this.getServerNameString(serverName), Integer.toString(portNumber), SQLServerException.getErrString("R_notSQLServer")};
                this.terminate(3, form.format(msgArgs));
            }
            assert (bytesRead >= 0);
            assert (bytesRead <= responseLength - responseBytesRead);
            if (this.tdsChannel.isLoggingPackets()) {
                this.tdsChannel.logPacket(preloginResponse, responseBytesRead, bytesRead, this.toString() + " Prelogin response");
            }
            if (processedResponseHeader || (responseBytesRead += bytesRead) < 8) continue;
            if (4 != preloginResponse[0]) {
                if (connectionlogger.isLoggable(Level.WARNING)) {
                    connectionlogger.warning(this.toString() + preloginErrorLogString + " Unexpected response type:" + preloginResponse[0]);
                }
                form = new MessageFormat(SQLServerException.getErrString("R_tcpipConnectionFailed"));
                msgArgs = new Object[]{this.getServerNameString(serverName), Integer.toString(portNumber), SQLServerException.getErrString("R_notSQLServer")};
                this.terminate(3, form.format(msgArgs));
            }
            if (1 != (1 & preloginResponse[1])) {
                if (connectionlogger.isLoggable(Level.WARNING)) {
                    connectionlogger.warning(this.toString() + preloginErrorLogString + " Unexpected response status:" + preloginResponse[1]);
                }
                form = new MessageFormat(SQLServerException.getErrString("R_tcpipConnectionFailed"));
                msgArgs = new Object[]{this.getServerNameString(serverName), Integer.toString(portNumber), SQLServerException.getErrString("R_notSQLServer")};
                this.terminate(3, form.format(msgArgs));
            }
            responseLength = Util.readUnsignedShortBigEndian(preloginResponse, 2);
            assert (responseLength >= 0);
            if (responseLength >= preloginResponse.length) {
                if (connectionlogger.isLoggable(Level.WARNING)) {
                    connectionlogger.warning(this.toString() + preloginErrorLogString + " Response length:" + responseLength + " is greater than allowed length:" + preloginResponse.length);
                }
                form = new MessageFormat(SQLServerException.getErrString("R_tcpipConnectionFailed"));
                msgArgs = new Object[]{this.getServerNameString(serverName), Integer.toString(portNumber), SQLServerException.getErrString("R_notSQLServer")};
                this.terminate(3, form.format(msgArgs));
            }
            processedResponseHeader = true;
        }
        boolean receivedVersionOption = false;
        this.negotiatedEncryptionLevel = (byte)-1;
        int responseIndex = 8;
        block10: while (true) {
            byte optionToken;
            if (responseIndex >= responseLength) {
                if (connectionlogger.isLoggable(Level.WARNING)) {
                    connectionlogger.warning(this.toString() + " Option token not found");
                }
                this.throwInvalidTDS();
            }
            if (-1 == (optionToken = preloginResponse[responseIndex++])) break;
            if (responseIndex + 4 >= responseLength) {
                if (connectionlogger.isLoggable(Level.WARNING)) {
                    connectionlogger.warning(this.toString() + " Offset/Length not found for option:" + optionToken);
                }
                this.throwInvalidTDS();
            }
            int optionOffset = Util.readUnsignedShortBigEndian(preloginResponse, responseIndex) + 8;
            responseIndex += 2;
            assert (optionOffset >= 0);
            int optionLength = Util.readUnsignedShortBigEndian(preloginResponse, responseIndex);
            responseIndex += 2;
            assert (optionLength >= 0);
            if (optionOffset + optionLength > responseLength) {
                if (connectionlogger.isLoggable(Level.WARNING)) {
                    connectionlogger.warning(this.toString() + " Offset:" + optionOffset + " and length:" + optionLength + " exceed response length:" + responseLength);
                }
                this.throwInvalidTDS();
            }
            switch (optionToken) {
                case 0: {
                    MessageFormat form;
                    if (receivedVersionOption) {
                        if (connectionlogger.isLoggable(Level.WARNING)) {
                            connectionlogger.warning(this.toString() + " Version option already received");
                        }
                        this.throwInvalidTDS();
                    }
                    if (6 != optionLength) {
                        if (connectionlogger.isLoggable(Level.WARNING)) {
                            connectionlogger.warning(this.toString() + " Version option length:" + optionLength + " is incorrect.  Correct value is 6.");
                        }
                        this.throwInvalidTDS();
                    }
                    this.serverMajorVersion = preloginResponse[optionOffset];
                    if (this.serverMajorVersion < 9) {
                        if (connectionlogger.isLoggable(Level.WARNING)) {
                            connectionlogger.warning(this.toString() + " Server major version:" + this.serverMajorVersion + " is not supported by this driver.");
                        }
                        form = new MessageFormat(SQLServerException.getErrString("R_unsupportedServerVersion"));
                        Object[] msgArgs = new Object[]{Integer.toString(preloginResponse[optionOffset])};
                        this.terminate(6, form.format(msgArgs));
                    }
                    if (connectionlogger.isLoggable(Level.FINE)) {
                        connectionlogger.fine(this.toString() + " Server returned major version:" + preloginResponse[optionOffset]);
                    }
                    receivedVersionOption = true;
                    continue block10;
                }
                case 1: {
                    if (-1 != this.negotiatedEncryptionLevel) {
                        if (connectionlogger.isLoggable(Level.WARNING)) {
                            connectionlogger.warning(this.toString() + " Encryption option already received");
                        }
                        this.throwInvalidTDS();
                    }
                    if (1 != optionLength) {
                        if (connectionlogger.isLoggable(Level.WARNING)) {
                            connectionlogger.warning(this.toString() + " Encryption option length:" + optionLength + " is incorrect.  Correct value is 1.");
                        }
                        this.throwInvalidTDS();
                    }
                    this.negotiatedEncryptionLevel = preloginResponse[optionOffset];
                    if (0 != this.negotiatedEncryptionLevel && 1 != this.negotiatedEncryptionLevel && 3 != this.negotiatedEncryptionLevel && 2 != this.negotiatedEncryptionLevel) {
                        if (connectionlogger.isLoggable(Level.WARNING)) {
                            connectionlogger.warning(this.toString() + " Server returned " + TDS.getEncryptionLevel(this.negotiatedEncryptionLevel));
                        }
                        this.throwInvalidTDS();
                    }
                    if (connectionlogger.isLoggable(Level.FINER)) {
                        connectionlogger.finer(this.toString() + " Negotiated encryption level:" + TDS.getEncryptionLevel(this.negotiatedEncryptionLevel));
                    }
                    if (1 == this.requestedEncryptionLevel && 1 != this.negotiatedEncryptionLevel && 3 != this.negotiatedEncryptionLevel) {
                        this.terminate(5, SQLServerException.getErrString("R_sslRequiredNoServerSupport"));
                    }
                    if (2 != this.requestedEncryptionLevel || 2 == this.negotiatedEncryptionLevel || this.isTDS8) continue block10;
                    if (3 == this.negotiatedEncryptionLevel) {
                        this.terminate(5, SQLServerException.getErrString("R_sslRequiredByServer"));
                    }
                    if (connectionlogger.isLoggable(Level.WARNING)) {
                        connectionlogger.warning(this.toString() + " Client requested encryption level: " + TDS.getEncryptionLevel(this.requestedEncryptionLevel) + " Server returned unexpected encryption level: " + TDS.getEncryptionLevel(this.negotiatedEncryptionLevel));
                    }
                    this.throwInvalidTDS();
                    continue block10;
                }
                case 6: {
                    MessageFormat form;
                    if (0 != preloginResponse[optionOffset] && 1 != preloginResponse[optionOffset]) {
                        if (connectionlogger.isLoggable(Level.SEVERE)) {
                            connectionlogger.severe(this.toString() + " Server sent an unexpected value for FedAuthRequired PreLogin Option. Value was " + preloginResponse[optionOffset]);
                        }
                        form = new MessageFormat(SQLServerException.getErrString("R_FedAuthRequiredPreLoginResponseInvalidValue"));
                        throw new SQLServerException(form.format(new Object[]{preloginResponse[optionOffset]}), null);
                    }
                    if ((null == this.authenticationString || this.authenticationString.equalsIgnoreCase(SqlAuthentication.NOT_SPECIFIED.toString())) && null == this.accessTokenInByte && null == this.accessTokenCallback && !this.hasAccessTokenCallbackClass) continue block10;
                    this.fedAuthRequiredPreLoginResponse = preloginResponse[optionOffset] == 1;
                    continue block10;
                }
            }
            if (!connectionlogger.isLoggable(Level.FINER)) continue;
            connectionlogger.finer(this.toString() + " Ignoring prelogin response option:" + optionToken);
        }
        if (!receivedVersionOption || -1 == this.negotiatedEncryptionLevel) {
            if (connectionlogger.isLoggable(Level.WARNING)) {
                connectionlogger.warning(this.toString() + " Prelogin response is missing version and/or encryption option.");
            }
            this.throwInvalidTDS();
        }
    }

    final void throwInvalidTDS() throws SQLServerException {
        this.terminate(4, SQLServerException.getErrString("R_invalidTDS"));
    }

    final void throwInvalidTDSToken(String tokenName) throws SQLServerException {
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unexpectedToken"));
        Object[] msgArgs = new Object[]{tokenName};
        String message = SQLServerException.getErrString("R_invalidTDS") + form.format(msgArgs);
        this.terminate(4, message);
    }

    final void terminate(int driverErrorCode, String message) throws SQLServerException {
        this.terminate(driverErrorCode, message, null);
    }

    final void terminate(int driverErrorCode, String message, Throwable throwable) throws SQLServerException {
        String st;
        String string = st = this.state.equals((Object)State.OPENED) ? "08006" : "08001";
        if (!this.xopenStates) {
            st = SQLServerException.mapFromXopen(st);
        }
        SQLServerException ex = new SQLServerException((Object)this, SQLServerException.checkAndAppendClientConnId(message, this), st, 0, true);
        if (null != throwable) {
            ex.initCause(throwable);
        }
        ex.setDriverErrorCode(driverErrorCode);
        this.notifyPooledConnection(ex);
        this.close();
        throw ex;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean executeCommand(TDSCommand newCommand) throws SQLServerException {
        this.schedulerLock.lock();
        try {
            ICounter previousCounter = null;
            if (null != this.currentCommand) {
                try {
                    this.currentCommand.getCounter().resetCounter();
                    this.currentCommand.detach();
                }
                catch (SQLServerException e) {
                    if (connectionlogger.isLoggable(Level.FINE)) {
                        connectionlogger.fine("Failed to detach current command : " + e.getMessage());
                    }
                }
                finally {
                    previousCounter = this.currentCommand.getCounter();
                    this.currentCommand = null;
                }
            }
            newCommand.createCounter(previousCounter, this.activeConnectionProperties);
            if (!(newCommand instanceof LogonCommand) && !this.sessionRecovery.isReconnectRunning() && this.connectRetryCount > 0 && this.sessionRecovery.isConnectionRecoveryNegotiated() && this.isConnectionDead()) {
                if (connectionlogger.isLoggable(Level.FINER)) {
                    connectionlogger.finer(this.toString() + " Connection is detected to be broken.");
                }
                if (!this.sessionRecovery.isConnectionRecoveryPossible() || this.sessionRecovery.getUnprocessedResponseCount() != 0) {
                    SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_crClientUnrecoverable"), null, false);
                }
                if (!this.sessionRecovery.getSessionStateTable().isSessionRecoverable()) {
                    SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_crServerSessionStateNotRecoverable"), null, false);
                }
                try {
                    if (null != this.preparedStatementHandleCache) {
                        this.preparedStatementHandleCache.clear();
                    }
                    this.sessionRecovery.reconnect(newCommand);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    SQLServerException.makeFromDriverError(this, this.sessionRecovery, e.getMessage(), null, false);
                }
                if (this.sessionRecovery.getReconnectException() != null) {
                    if (connectionlogger.isLoggable(Level.FINER)) {
                        connectionlogger.finer(this.toString() + "Connection is broken and recovery is not possible.");
                    }
                    throw this.sessionRecovery.getReconnectException();
                }
            }
            boolean commandComplete = false;
            try {
                commandComplete = newCommand.execute(this.tdsChannel.getWriter(), this.tdsChannel.getReader(newCommand));
            }
            finally {
                if (!commandComplete && !this.isSessionUnAvailable()) {
                    this.currentCommand = newCommand;
                }
            }
            boolean bl = commandComplete;
            return bl;
        }
        finally {
            this.schedulerLock.unlock();
        }
    }

    void resetCurrentCommand() throws SQLServerException {
        if (null != this.currentCommand) {
            this.currentCommand.detach();
            this.currentCommand = null;
        }
    }

    boolean isConnectionDead() throws SQLServerException {
        if (!this.idleNetworkTracker.isIdle()) {
            if (connectionlogger.isLoggable(Level.FINEST)) {
                connectionlogger.finest(this.toString() + " Network not idle. Skipping networkSocketStillConnected check.");
            }
            return false;
        }
        this.lock.lock();
        try {
            if (!this.idleNetworkTracker.isIdle()) {
                if (connectionlogger.isLoggable(Level.FINEST)) {
                    connectionlogger.finest(this.toString() + " Network not idle. Skipping networkSocketStillConnected check.");
                }
                boolean bl = false;
                return bl;
            }
            if (this.isSessionUnAvailable()) {
                SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_connectionIsClosed"), "08006", false);
            }
            boolean bl = this.tdsChannel.networkSocketStillConnected() == false;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean executeReconnectCommand(TDSCommand newCommand) throws SQLServerException {
        this.lock.lock();
        try {
            if (null != this.currentCommand) {
                this.currentCommand.detach();
                this.currentCommand = null;
            }
            boolean commandComplete = false;
            try {
                commandComplete = newCommand.execute(this.tdsChannel.getWriter(), this.tdsChannel.getReader(newCommand));
            }
            finally {
                if (!commandComplete && !this.isSessionUnAvailable()) {
                    this.currentCommand = newCommand;
                }
            }
            boolean bl = commandComplete;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    private void connectionCommand(String sql, String logContext) throws SQLServerException {
        final class ConnectionCommand
        extends UninterruptableTDSCommand {
            private static final long serialVersionUID = 1L;
            final String sql;

            ConnectionCommand(String sql, String logContext) {
                super(logContext);
                this.sql = sql;
            }

            @Override
            final boolean doExecute() throws SQLServerException {
                TDSWriter tdsWriter = this.startRequest((byte)1);
                tdsWriter.sendEnclavePackage(null, null);
                tdsWriter.writeString(this.sql);
                TDSParser.parse(this.startResponse(), this.getLogContext());
                return true;
            }
        }
        if (this.sessionRecovery.isReconnectRunning()) {
            this.executeReconnectCommand(new ConnectionCommand(sql, logContext));
        } else {
            this.executeCommand(new ConnectionCommand(sql, logContext));
        }
    }

    private String sqlStatementToInitialize() {
        String s = null;
        if (this.nLockTimeout > -1) {
            s = " set lock_timeout " + this.nLockTimeout;
        }
        return s;
    }

    void setCatalogName(String sDB) {
        if (sDB != null && sDB.length() > 0) {
            this.sCatalog = sDB;
        }
    }

    void setLanguageName(String language) {
        if (language != null && language.length() > 0) {
            this.sLanguage = language;
        }
    }

    String sqlStatementToSetTransactionIsolationLevel() throws SQLServerException {
        Object sql = "set transaction isolation level ";
        switch (this.transactionIsolationLevel) {
            case 1: {
                sql = (String)sql + " read uncommitted ";
                break;
            }
            case 2: {
                sql = (String)sql + " read committed ";
                break;
            }
            case 4: {
                sql = (String)sql + " repeatable read ";
                break;
            }
            case 8: {
                sql = (String)sql + " serializable ";
                break;
            }
            case 4096: {
                sql = (String)sql + " snapshot ";
                break;
            }
            default: {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidTransactionLevel"));
                Object[] msgArgs = new Object[]{Integer.toString(this.transactionIsolationLevel)};
                SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
            }
        }
        return sql;
    }

    static String sqlStatementToSetCommit(boolean autoCommit) {
        return autoCommit ? "set implicit_transactions off " : "set implicit_transactions on ";
    }

    @Override
    public Statement createStatement() throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, CREATE_STATEMENT);
        Statement st = this.createStatement(1003, 1007);
        loggerExternal.exiting(this.loggingClassName, CREATE_STATEMENT, st);
        return st;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "prepareStatement", sql);
        PreparedStatement pst = this.prepareStatement(sql, 1003, 1007);
        loggerExternal.exiting(this.loggingClassName, "prepareStatement", pst);
        return pst;
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "prepareCall", sql);
        CallableStatement st = this.prepareCall(sql, 1003, 1007);
        loggerExternal.exiting(this.loggingClassName, "prepareCall", st);
        return st;
    }

    @Override
    public String nativeSQL(String sql) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "nativeSQL", sql);
        this.checkClosed();
        loggerExternal.exiting(this.loggingClassName, "nativeSQL", sql);
        return sql;
    }

    @Override
    public void setAutoCommit(boolean newAutoCommitMode) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggingClassName, "setAutoCommit", newAutoCommitMode);
            if (Util.isActivityTraceOn()) {
                loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
            }
        }
        String commitPendingTransaction = "";
        this.checkClosed();
        if (newAutoCommitMode == this.databaseAutoCommitMode) {
            return;
        }
        if (newAutoCommitMode) {
            commitPendingTransaction = "IF @@TRANCOUNT > 0 COMMIT TRAN ";
        }
        if (connectionlogger.isLoggable(Level.FINER)) {
            connectionlogger.finer(this.toString() + " Autocommitmode current :" + this.databaseAutoCommitMode + " new: " + newAutoCommitMode);
        }
        this.rolledBackTransaction = false;
        this.connectionCommand(SQLServerConnection.sqlStatementToSetCommit(newAutoCommitMode) + commitPendingTransaction, "setAutoCommit");
        this.databaseAutoCommitMode = newAutoCommitMode;
        loggerExternal.exiting(this.loggingClassName, "setAutoCommit");
    }

    @Override
    public boolean getAutoCommit() throws SQLServerException {
        boolean res;
        loggerExternal.entering(this.loggingClassName, "getAutoCommit");
        this.checkClosed();
        boolean bl = res = !this.inXATransaction && this.databaseAutoCommitMode;
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.exiting(this.loggingClassName, "getAutoCommit", res);
        }
        return res;
    }

    final byte[] getTransactionDescriptor() {
        return this.transactionDescriptor;
    }

    @Override
    public void commit() throws SQLServerException {
        this.commit(false);
    }

    public void commit(boolean delayedDurability) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "commit");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        this.checkClosed();
        if (!this.databaseAutoCommitMode) {
            if (!delayedDurability) {
                this.connectionCommand("IF @@TRANCOUNT > 0 COMMIT TRAN", "Connection.commit");
            } else {
                this.connectionCommand("IF @@TRANCOUNT > 0 COMMIT TRAN WITH ( DELAYED_DURABILITY =  ON )", "Connection.commit");
            }
        }
        loggerExternal.exiting(this.loggingClassName, "commit");
    }

    @Override
    public void rollback() throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "rollback");
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        this.checkClosed();
        if (this.databaseAutoCommitMode) {
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_cantInvokeRollback"), null, true);
        } else {
            this.connectionCommand("IF @@TRANCOUNT > 0 ROLLBACK TRAN", "Connection.rollback");
        }
        loggerExternal.exiting(this.loggingClassName, "rollback");
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        loggerExternal.entering(this.loggingClassName, "abort", executor);
        if (this.isClosed()) {
            return;
        }
        SecurityManager secMgr = System.getSecurityManager();
        if (secMgr != null) {
            try {
                SQLPermission perm = new SQLPermission(CALL_ABORT_PERM);
                secMgr.checkPermission(perm);
            }
            catch (SecurityException ex) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_permissionDenied"));
                Object[] msgArgs = new Object[]{CALL_ABORT_PERM};
                SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, true);
            }
        }
        if (null == executor) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
            Object[] msgArgs = new Object[]{"executor"};
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
        } else {
            this.setState(State.CLOSED);
            executor.execute(() -> this.clearConnectionResources());
        }
        loggerExternal.exiting(this.loggingClassName, "abort");
    }

    @Override
    public void close() throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "close");
        this.setState(State.CLOSED);
        this.clearConnectionResources();
        loggerExternal.exiting(this.loggingClassName, "close");
    }

    private void clearConnectionResources() {
        if (this.sharedTimer != null) {
            this.sharedTimer.removeRef();
            this.sharedTimer = null;
        }
        if (null != this.tdsChannel) {
            this.tdsChannel.close();
        }
        if (null != this.preparedStatementHandleCache) {
            this.preparedStatementHandleCache.clear();
        }
        if (null != this.parameterMetadataCache) {
            this.parameterMetadataCache.clear();
        }
        this.cleanupPreparedStatementDiscardActions();
    }

    final void poolCloseEventNotify() throws SQLServerException {
        if (this.state.equals((Object)State.OPENED) && null != this.pooledConnectionParent) {
            if (!this.databaseAutoCommitMode && !(this.pooledConnectionParent instanceof XAConnection)) {
                this.connectionCommand("IF @@TRANCOUNT > 0 ROLLBACK TRAN", "close connection");
            }
            this.notifyPooledConnection(null);
            if (connectionlogger.isLoggable(Level.FINER)) {
                connectionlogger.finer(this.toString() + " Connection closed and returned to connection pool");
            }
        }
    }

    @Override
    public boolean isClosed() throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "isClosed");
        loggerExternal.exiting(this.loggingClassName, "isClosed", this.isSessionUnAvailable());
        return this.isSessionUnAvailable();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "getMetaData");
        this.checkClosed();
        if (this.databaseMetaData == null) {
            this.databaseMetaData = new SQLServerDatabaseMetaData(this);
        }
        loggerExternal.exiting(this.loggingClassName, "getMetaData", this.databaseMetaData);
        return this.databaseMetaData;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggingClassName, "setReadOnly", readOnly);
        }
        this.checkClosed();
        loggerExternal.exiting(this.loggingClassName, "setReadOnly");
    }

    @Override
    public boolean isReadOnly() throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "isReadOnly");
        this.checkClosed();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.exiting(this.loggingClassName, "isReadOnly", Boolean.FALSE);
        }
        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "setCatalog", catalog);
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        this.checkClosed();
        if (catalog != null) {
            this.connectionCommand("use " + Util.escapeSQLId(catalog), "setCatalog");
            this.sCatalog = catalog;
        }
        loggerExternal.exiting(this.loggingClassName, "setCatalog");
    }

    @Override
    public String getCatalog() throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "getCatalog");
        this.checkClosed();
        loggerExternal.exiting(this.loggingClassName, "getCatalog", this.sCatalog);
        return this.sCatalog;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggingClassName, "setTransactionIsolation", level);
            if (Util.isActivityTraceOn()) {
                loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
            }
        }
        this.checkClosed();
        if (level == 0) {
            return;
        }
        this.transactionIsolationLevel = level;
        String sql = this.sqlStatementToSetTransactionIsolationLevel();
        this.connectionCommand(sql, "setTransactionIsolation");
        loggerExternal.exiting(this.loggingClassName, "setTransactionIsolation");
    }

    @Override
    public int getTransactionIsolation() throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "getTransactionIsolation");
        this.checkClosed();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.exiting(this.loggingClassName, "getTransactionIsolation", this.transactionIsolationLevel);
        }
        return this.transactionIsolationLevel;
    }

    @Override
    public SQLWarning getWarnings() throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "getWarnings");
        this.checkClosed();
        loggerExternal.exiting(this.loggingClassName, "getWarnings", this.sqlWarnings);
        return this.sqlWarnings;
    }

    void addWarning(String warningString) {
        this.warningSynchronization.lock();
        try {
            SQLWarning warning = new SQLWarning(warningString);
            if (null == this.sqlWarnings) {
                this.sqlWarnings = warning;
            } else {
                this.sqlWarnings.setNextWarning(warning);
            }
        }
        finally {
            this.warningSynchronization.unlock();
        }
    }

    @Override
    public void clearWarnings() throws SQLServerException {
        this.warningSynchronization.lock();
        try {
            loggerExternal.entering(this.loggingClassName, "clearWarnings");
            this.checkClosed();
            this.sqlWarnings = null;
            loggerExternal.exiting(this.loggingClassName, "clearWarnings");
        }
        finally {
            this.warningSynchronization.unlock();
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggingClassName, CREATE_STATEMENT, new Object[]{resultSetType, resultSetConcurrency});
        }
        this.checkClosed();
        SQLServerStatement st = new SQLServerStatement(this, resultSetType, resultSetConcurrency, SQLServerStatementColumnEncryptionSetting.USE_CONNECTION_SETTING);
        if (this.requestStarted) {
            this.addOpenStatement(st);
        }
        loggerExternal.exiting(this.loggingClassName, CREATE_STATEMENT, st);
        return st;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggingClassName, "prepareStatement", new Object[]{sql, resultSetType, resultSetConcurrency});
        }
        this.checkClosed();
        SQLServerPreparedStatement st = new SQLServerPreparedStatement(this, sql, resultSetType, resultSetConcurrency, SQLServerStatementColumnEncryptionSetting.USE_CONNECTION_SETTING);
        if (this.requestStarted) {
            this.addOpenStatement(st);
        }
        loggerExternal.exiting(this.loggingClassName, "prepareStatement", st);
        return st;
    }

    private PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggingClassName, "prepareStatement", new Object[]{sql, resultSetType, resultSetConcurrency, stmtColEncSetting});
        }
        this.checkClosed();
        SQLServerPreparedStatement st = new SQLServerPreparedStatement(this, sql, resultSetType, resultSetConcurrency, stmtColEncSetting);
        if (this.requestStarted) {
            this.addOpenStatement(st);
        }
        loggerExternal.exiting(this.loggingClassName, "prepareStatement", st);
        return st;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggingClassName, "prepareCall", new Object[]{sql, resultSetType, resultSetConcurrency});
        }
        this.checkClosed();
        SQLServerCallableStatement st = new SQLServerCallableStatement(this, sql, resultSetType, resultSetConcurrency, SQLServerStatementColumnEncryptionSetting.USE_CONNECTION_SETTING);
        if (this.requestStarted) {
            this.addOpenStatement(st);
        }
        loggerExternal.exiting(this.loggingClassName, "prepareCall", st);
        return st;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        loggerExternal.entering(this.loggingClassName, "setTypeMap", map);
        this.checkClosed();
        if (map != null && map instanceof HashMap && map.isEmpty()) {
            loggerExternal.exiting(this.loggingClassName, "setTypeMap");
            return;
        }
        SQLServerException.throwNotSupportedException(this, null);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "getTypeMap");
        this.checkClosed();
        HashMap mp = new HashMap();
        loggerExternal.exiting(this.loggingClassName, "getTypeMap", mp);
        return mp;
    }

    int writeAEFeatureRequest(boolean write, TDSWriter tdsWriter) throws SQLServerException {
        int len = 6;
        if (write) {
            tdsWriter.writeByte((byte)4);
            tdsWriter.writeInt(1);
            if (null == this.enclaveAttestationProtocol) {
                tdsWriter.writeByte((byte)1);
            } else {
                tdsWriter.writeByte((byte)2);
            }
        }
        return len;
    }

    int writeFedAuthFeatureRequest(boolean write, TDSWriter tdsWriter, FederatedAuthenticationFeatureExtensionData fedAuthFeatureExtensionData) throws SQLServerException {
        assert (fedAuthFeatureExtensionData.libraryType == 2 || fedAuthFeatureExtensionData.libraryType == 1);
        int dataLen = 0;
        switch (fedAuthFeatureExtensionData.libraryType) {
            case 2: {
                dataLen = 2;
                break;
            }
            case 1: {
                assert (null != fedAuthFeatureExtensionData.accessToken);
                dataLen = 5 + fedAuthFeatureExtensionData.accessToken.length;
                break;
            }
            default: {
                assert (false);
                break;
            }
        }
        int totalLen = dataLen + 5;
        if (write) {
            tdsWriter.writeByte((byte)2);
            byte options = 0;
            switch (fedAuthFeatureExtensionData.libraryType) {
                case 2: {
                    assert (this.federatedAuthenticationInfoRequested);
                    options = (byte)(options | 4);
                    break;
                }
                case 1: {
                    assert (this.federatedAuthenticationRequested);
                    options = (byte)(options | 2);
                    break;
                }
                default: {
                    assert (false);
                    break;
                }
            }
            options = (byte)(options | (byte)(fedAuthFeatureExtensionData.fedAuthRequiredPreLoginResponse ? 1 : 0));
            tdsWriter.writeInt(dataLen);
            tdsWriter.writeByte(options);
            switch (fedAuthFeatureExtensionData.libraryType) {
                case 2: {
                    int workflow = 0;
                    switch (fedAuthFeatureExtensionData.authentication) {
                        case ACTIVE_DIRECTORY_PASSWORD: {
                            workflow = 1;
                            break;
                        }
                        case ACTIVE_DIRECTORY_INTEGRATED: {
                            workflow = 2;
                            break;
                        }
                        case ACTIVE_DIRECTORY_MANAGED_IDENTITY: {
                            workflow = 3;
                            break;
                        }
                        case ACTIVE_DIRECTORY_DEFAULT: {
                            workflow = 3;
                            break;
                        }
                        case ACTIVE_DIRECTORY_INTERACTIVE: {
                            workflow = 3;
                            break;
                        }
                        case ACTIVE_DIRECTORY_SERVICE_PRINCIPAL: {
                            workflow = 1;
                            break;
                        }
                        case ACTIVE_DIRECTORY_SERVICE_PRINCIPAL_CERTIFICATE: {
                            workflow = 1;
                            break;
                        }
                        default: {
                            if (null != this.accessTokenCallback || this.hasAccessTokenCallbackClass) {
                                workflow = 3;
                                break;
                            }
                            assert (false);
                            break;
                        }
                    }
                    tdsWriter.writeByte((byte)workflow);
                    break;
                }
                case 1: {
                    tdsWriter.writeInt(fedAuthFeatureExtensionData.accessToken.length);
                    tdsWriter.writeBytes(fedAuthFeatureExtensionData.accessToken, 0, fedAuthFeatureExtensionData.accessToken.length);
                    break;
                }
                default: {
                    assert (false);
                    break;
                }
            }
        }
        return totalLen;
    }

    int writeDataClassificationFeatureRequest(boolean write, TDSWriter tdsWriter) throws SQLServerException {
        int len = 6;
        if (write) {
            tdsWriter.writeByte((byte)9);
            tdsWriter.writeInt(1);
            tdsWriter.writeByte((byte)2);
        }
        return len;
    }

    int writeUTF8SupportFeatureRequest(boolean write, TDSWriter tdsWriter) throws SQLServerException {
        int len = 5;
        if (write) {
            tdsWriter.writeByte((byte)10);
            tdsWriter.writeInt(0);
        }
        return len;
    }

    int writeDNSCacheFeatureRequest(boolean write, TDSWriter tdsWriter) throws SQLServerException {
        int len = 5;
        if (write) {
            tdsWriter.writeByte((byte)11);
            tdsWriter.writeInt(0);
        }
        return len;
    }

    int writeIdleConnectionResiliencyRequest(boolean write, TDSWriter tdsWriter) throws SQLServerException {
        SessionStateTable ssTable = this.sessionRecovery.getSessionStateTable();
        int len = 1;
        if (write) {
            tdsWriter.writeByte((byte)1);
        }
        if (!this.sessionRecovery.isReconnectRunning()) {
            if (write) {
                tdsWriter.writeInt(0);
            }
            len += 4;
        } else {
            int initialLength = 0;
            initialLength += 1 + 2 * ssTable.getOriginalCatalog().length();
            initialLength += 1 + 2 * ssTable.getOriginalLanguage().length();
            initialLength += 1 + (ssTable.getOriginalCollation() == null ? 0 : SQLCollation.tdsLength());
            initialLength += ssTable.getInitialLength();
            int currentLength = 0;
            currentLength += 1 + 2 * (this.sCatalog.equals(ssTable.getOriginalCatalog()) ? 0 : this.sCatalog.length());
            currentLength += 1 + 2 * (this.sLanguage.equals(ssTable.getOriginalLanguage()) ? 0 : this.sLanguage.length());
            currentLength += 1 + (this.databaseCollation == null || this.databaseCollation.isEqual(ssTable.getOriginalCollation()) ? 0 : SQLCollation.tdsLength());
            currentLength += ssTable.getDeltaLength();
            if (write) {
                int i;
                tdsWriter.writeInt(8 + initialLength + currentLength);
                tdsWriter.writeInt(initialLength);
                tdsWriter.writeByte((byte)ssTable.getOriginalCatalog().length());
                tdsWriter.writeBytes(this.toUCS16(ssTable.getOriginalCatalog()));
                if (ssTable.getOriginalCollation() != null) {
                    tdsWriter.writeByte((byte)SQLCollation.tdsLength());
                    ssTable.getOriginalCollation().writeCollation(tdsWriter);
                } else {
                    tdsWriter.writeByte((byte)0);
                }
                tdsWriter.writeByte((byte)ssTable.getOriginalLanguage().length());
                tdsWriter.writeBytes(this.toUCS16(ssTable.getOriginalLanguage()));
                for (i = 0; i < 256; ++i) {
                    if (ssTable.getSessionStateInitial()[i] == null) continue;
                    tdsWriter.writeByte((byte)i);
                    if (ssTable.getSessionStateInitial()[i].length >= 255) {
                        tdsWriter.writeByte((byte)-1);
                        tdsWriter.writeShort((short)ssTable.getSessionStateInitial()[i].length);
                    } else {
                        tdsWriter.writeByte((byte)ssTable.getSessionStateInitial()[i].length);
                    }
                    tdsWriter.writeBytes(ssTable.getSessionStateInitial()[i]);
                }
                tdsWriter.writeInt(currentLength);
                if (ssTable.spResetCalled()) {
                    this.sCatalog = ssTable.getOriginalCatalog();
                    this.databaseCollation = ssTable.getOriginalCollation();
                    this.sLanguage = ssTable.getOriginalLanguage();
                    ssTable.setspResetCalled(false);
                }
                if (this.sCatalog.equals(ssTable.getOriginalCatalog())) {
                    tdsWriter.writeByte((byte)0);
                } else {
                    tdsWriter.writeByte((byte)this.sCatalog.length());
                    tdsWriter.writeBytes(this.toUCS16(this.sCatalog));
                }
                if (this.databaseCollation == null || this.databaseCollation.isEqual(ssTable.getOriginalCollation())) {
                    tdsWriter.writeByte((byte)0);
                } else {
                    tdsWriter.writeByte((byte)SQLCollation.tdsLength());
                    this.databaseCollation.writeCollation(tdsWriter);
                }
                if (this.sLanguage.equals(ssTable.getOriginalLanguage())) {
                    tdsWriter.writeByte((byte)0);
                } else {
                    tdsWriter.writeByte((byte)this.sLanguage.length());
                    tdsWriter.writeBytes(this.toUCS16(this.sLanguage));
                }
                for (i = 0; i < 256; ++i) {
                    if (ssTable.getSessionStateDelta()[i] == null || ssTable.getSessionStateDelta()[i].getData() == null) continue;
                    tdsWriter.writeByte((byte)i);
                    if (ssTable.getSessionStateDelta()[i].getDataLength() >= 255) {
                        tdsWriter.writeByte((byte)-1);
                        tdsWriter.writeShort((short)ssTable.getSessionStateDelta()[i].getDataLength());
                    } else {
                        tdsWriter.writeByte((byte)ssTable.getSessionStateDelta()[i].getDataLength());
                    }
                    tdsWriter.writeBytes(ssTable.getSessionStateDelta()[i].getData());
                }
            }
            len += initialLength + currentLength + 12;
        }
        return len;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void logon(LogonCommand command) throws SQLServerException {
        SSPIAuthentication authentication = null;
        if (this.integratedSecurity) {
            if (AuthenticationScheme.NATIVE_AUTHENTICATION == this.intAuthScheme) {
                authentication = new AuthenticationJNI(this, this.currentConnectPlaceHolder.getServerName(), this.currentConnectPlaceHolder.getPortNumber());
            } else if (AuthenticationScheme.JAVA_KERBEROS == this.intAuthScheme) {
                authentication = null != this.impersonatedUserCred ? new KerbAuthentication(this, this.currentConnectPlaceHolder.getServerName(), this.currentConnectPlaceHolder.getPortNumber(), this.impersonatedUserCred, this.isUserCreatedCredential) : new KerbAuthentication(this, this.currentConnectPlaceHolder.getServerName(), this.currentConnectPlaceHolder.getPortNumber());
            } else if (this.ntlmAuthentication) {
                if (null == this.ntlmPasswordHash) {
                    this.ntlmPasswordHash = NTLMAuthentication.getNtlmPasswordHash(this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()));
                    this.activeConnectionProperties.remove(SQLServerDriverStringProperty.PASSWORD.toString());
                }
                authentication = new NTLMAuthentication(this, this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.DOMAIN.toString()), this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()), this.ntlmPasswordHash, this.hostName);
            }
        }
        if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_PASSWORD.toString()) || (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_INTEGRATED.toString()) || this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_MANAGED_IDENTITY.toString()) || this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_DEFAULT.toString()) || this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_SERVICE_PRINCIPAL.toString()) || this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_SERVICE_PRINCIPAL_CERTIFICATE.toString()) || this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_INTERACTIVE.toString())) && this.fedAuthRequiredPreLoginResponse || null != this.accessTokenCallback || this.hasAccessTokenCallbackClass) {
            this.federatedAuthenticationInfoRequested = true;
            this.fedAuthFeatureExtensionData = new FederatedAuthenticationFeatureExtensionData(2, this.authenticationString, this.fedAuthRequiredPreLoginResponse);
        }
        if (null != this.accessTokenInByte) {
            this.fedAuthFeatureExtensionData = new FederatedAuthenticationFeatureExtensionData(1, this.fedAuthRequiredPreLoginResponse, this.accessTokenInByte);
            this.federatedAuthenticationRequested = true;
        }
        try {
            this.sendLogon(command, authentication, this.fedAuthFeatureExtensionData);
            if (!this.isRoutedInCurrentAttempt) {
                this.originalCatalog = this.sCatalog;
                String sqlStmt = this.sqlStatementToInitialize();
                if (sqlStmt != null) {
                    this.connectionCommand(sqlStmt, "Change Settings");
                }
            }
        }
        finally {
            if (this.integratedSecurity) {
                if (null != authentication) {
                    authentication.releaseClientContext();
                    authentication = null;
                }
                if (null != this.impersonatedUserCred) {
                    this.impersonatedUserCred = null;
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void processEnvChange(TDSReader tdsReader) throws SQLServerException {
        tdsReader.readUnsignedByte();
        int envValueLength = tdsReader.readUnsignedShort();
        TDSReaderMark mark = tdsReader.mark();
        int envchange = tdsReader.readUnsignedByte();
        switch (envchange) {
            case 4: {
                try {
                    this.tdsPacketSize = Integer.parseInt(tdsReader.readUnicodeString(tdsReader.readUnsignedByte()));
                }
                catch (NumberFormatException e) {
                    tdsReader.throwInvalidTDS();
                }
                if (!connectionlogger.isLoggable(Level.FINER)) break;
                connectionlogger.finer(this.toString() + " Network packet size is " + this.tdsPacketSize + " bytes");
                break;
            }
            case 7: {
                if (SQLCollation.tdsLength() != tdsReader.readUnsignedByte()) {
                    tdsReader.throwInvalidTDS();
                }
                try {
                    this.databaseCollation = new SQLCollation(tdsReader);
                }
                catch (UnsupportedEncodingException e) {
                    this.terminate(4, e.getMessage(), e);
                }
                break;
            }
            case 8: 
            case 11: {
                this.rolledBackTransaction = false;
                byte[] descriptor = this.getTransactionDescriptor();
                if (descriptor.length != tdsReader.readUnsignedByte()) {
                    tdsReader.throwInvalidTDS();
                }
                tdsReader.readBytes(descriptor, 0, descriptor.length);
                if (!connectionlogger.isLoggable(Level.FINER)) break;
                String op = 8 == envchange ? " started" : " enlisted";
                connectionlogger.finer(this.toString() + op);
                break;
            }
            case 10: {
                this.rolledBackTransaction = true;
                if (this.inXATransaction) {
                    if (!connectionlogger.isLoggable(Level.FINER)) break;
                    connectionlogger.finer(this.toString() + " rolled back. (DTC)");
                    break;
                }
                if (connectionlogger.isLoggable(Level.FINER)) {
                    connectionlogger.finer(this.toString() + " rolled back");
                }
                Arrays.fill(this.getTransactionDescriptor(), (byte)0);
                break;
            }
            case 9: {
                if (connectionlogger.isLoggable(Level.FINER)) {
                    connectionlogger.finer(this.toString() + " committed");
                }
                Arrays.fill(this.getTransactionDescriptor(), (byte)0);
                break;
            }
            case 12: {
                if (connectionlogger.isLoggable(Level.FINER)) {
                    connectionlogger.finer(this.toString() + " defected");
                }
                Arrays.fill(this.getTransactionDescriptor(), (byte)0);
                break;
            }
            case 1: {
                this.setCatalogName(tdsReader.readUnicodeString(tdsReader.readUnsignedByte()));
                break;
            }
            case 13: {
                this.setFailoverPartnerServerProvided(tdsReader.readUnicodeString(tdsReader.readUnsignedByte()));
                break;
            }
            case 2: {
                this.setLanguageName(tdsReader.readUnicodeString(tdsReader.readUnsignedByte()));
                break;
            }
            case 18: {
                this.sessionRecovery.getSessionStateTable().reset();
                break;
            }
            case 3: 
            case 5: 
            case 6: 
            case 15: 
            case 16: 
            case 17: 
            case 19: {
                if (!connectionlogger.isLoggable(Level.FINER)) break;
                connectionlogger.finer(this.toString() + " Ignored env change: " + envchange);
                break;
            }
            case 20: {
                int routingServerNameLength = -1;
                int routingPortNumber = -1;
                int routingProtocol = -1;
                int routingDataValueLength = -1;
                String routingServerName = null;
                try {
                    routingDataValueLength = tdsReader.readUnsignedShort();
                    if (routingDataValueLength <= 5) {
                        this.throwInvalidTDS();
                    }
                    if ((routingProtocol = tdsReader.readUnsignedByte()) != 0) {
                        this.throwInvalidTDS();
                    }
                    if ((routingPortNumber = tdsReader.readUnsignedShort()) <= 0 || routingPortNumber > 65535) {
                        this.throwInvalidTDS();
                    }
                    if ((routingServerNameLength = tdsReader.readUnsignedShort()) <= 0 || routingServerNameLength > 1024) {
                        this.throwInvalidTDS();
                    }
                    routingServerName = tdsReader.readUnicodeString(routingServerNameLength);
                    assert (routingServerName != null);
                    if (connectionlogger.isLoggable(Level.FINER)) {
                        connectionlogger.finer(this.toString() + " Received routing ENVCHANGE with the following values. routingDataValueLength:" + routingDataValueLength + " protocol:" + routingProtocol + " portNumber:" + routingPortNumber + " serverNameLength:" + routingServerNameLength + " serverName:" + (routingServerName != null ? routingServerName : "null"));
                    }
                }
                catch (Throwable throwable) {
                    if (connectionlogger.isLoggable(Level.FINER)) {
                        connectionlogger.finer(this.toString() + " Received routing ENVCHANGE with the following values. routingDataValueLength:" + routingDataValueLength + " protocol:" + routingProtocol + " portNumber:" + routingPortNumber + " serverNameLength:" + routingServerNameLength + " serverName:" + (routingServerName != null ? routingServerName : "null"));
                    }
                    throw throwable;
                }
                String currentHostName = this.activeConnectionProperties.getProperty("hostNameInCertificate");
                if (null != currentHostName && currentHostName.startsWith("*") && null != routingServerName && routingServerName.indexOf(46) != -1) {
                    char[] currentHostNameCharArray = currentHostName.toCharArray();
                    char[] routingServerNameCharArray = routingServerName.toCharArray();
                    boolean hostNameNeedsUpdate = true;
                    int i = currentHostName.length() - 1;
                    for (int j = routingServerName.length() - 1; i > 0 && j > 0; --i, --j) {
                        if (routingServerNameCharArray[j] == currentHostNameCharArray[i]) continue;
                        hostNameNeedsUpdate = false;
                        break;
                    }
                    if (hostNameNeedsUpdate) {
                        String newHostName = "*" + routingServerName.substring(routingServerName.indexOf(46));
                        this.activeConnectionProperties.setProperty("hostNameInCertificate", newHostName);
                        if (connectionlogger.isLoggable(Level.FINER)) {
                            connectionlogger.finer(this.toString() + "Using new host to validate the SSL certificate");
                        }
                    }
                }
                this.isRoutedInCurrentAttempt = true;
                this.routingInfo = new ServerPortPlaceHolder(routingServerName, routingPortNumber, null, this.integratedSecurity);
                break;
            }
            default: {
                if (connectionlogger.isLoggable(Level.WARNING)) {
                    connectionlogger.warning(this.toString() + " Unknown environment change: " + envchange);
                }
                this.throwInvalidTDS();
            }
        }
        tdsReader.reset(mark);
        tdsReader.readBytes(new byte[envValueLength], 0, envValueLength);
    }

    final void processFedAuthInfo(TDSReader tdsReader, TDSTokenHandler tdsTokenHandler) throws SQLServerException {
        SqlFedAuthInfo sqlFedAuthInfo = new SqlFedAuthInfo();
        tdsReader.readUnsignedByte();
        int tokenLen = tdsReader.readInt();
        if (connectionlogger.isLoggable(Level.FINER)) {
            connectionlogger.fine(this.toString() + " FEDAUTHINFO token stream length = " + tokenLen);
        }
        if (tokenLen < 4) {
            if (connectionlogger.isLoggable(Level.SEVERE)) {
                connectionlogger.severe(this.toString() + "FEDAUTHINFO token stream length too short for CountOfInfoIDs.");
            }
            throw new SQLServerException(SQLServerException.getErrString("R_FedAuthInfoLengthTooShortForCountOfInfoIds"), null);
        }
        int optionsCount = tdsReader.readInt();
        tokenLen -= 4;
        if (connectionlogger.isLoggable(Level.FINER)) {
            connectionlogger.fine(this.toString() + " CountOfInfoIDs = " + optionsCount);
        }
        if (tokenLen > 0) {
            byte[] tokenData = new byte[tokenLen];
            tdsReader.readBytes(tokenData, 0, tokenLen);
            if (connectionlogger.isLoggable(Level.FINER)) {
                connectionlogger.fine(this.toString() + " Read rest of FEDAUTHINFO token stream: " + Arrays.toString(tokenData));
            }
            int optionSize = 9;
            int totalOptionsSize = optionsCount * 9;
            block6: for (int i = 0; i < optionsCount; ++i) {
                int currentOptionOffset = i * 9;
                byte id = tokenData[currentOptionOffset];
                byte[] buffer = new byte[4];
                buffer[3] = tokenData[currentOptionOffset + 1];
                buffer[2] = tokenData[currentOptionOffset + 2];
                buffer[1] = tokenData[currentOptionOffset + 3];
                buffer[0] = tokenData[currentOptionOffset + 4];
                ByteBuffer wrapped = ByteBuffer.wrap(buffer);
                int dataLen = wrapped.getInt();
                buffer = new byte[4];
                buffer[3] = tokenData[currentOptionOffset + 5];
                buffer[2] = tokenData[currentOptionOffset + 6];
                buffer[1] = tokenData[currentOptionOffset + 7];
                buffer[0] = tokenData[currentOptionOffset + 8];
                wrapped = ByteBuffer.wrap(buffer);
                int dataOffset = wrapped.getInt();
                if (connectionlogger.isLoggable(Level.FINER)) {
                    connectionlogger.fine(this.toString() + " FedAuthInfoOpt: ID=" + id + ", DataLen=" + dataLen + ", Offset=" + dataOffset);
                }
                if ((dataOffset -= 4) < totalOptionsSize || dataOffset >= tokenLen) {
                    if (connectionlogger.isLoggable(Level.SEVERE)) {
                        connectionlogger.severe(this.toString() + "FedAuthInfoDataOffset points to an invalid location.");
                    }
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_FedAuthInfoInvalidOffset"));
                    throw new SQLServerException(form.format(new Object[]{dataOffset}), null);
                }
                String data = null;
                try {
                    byte[] dataArray = new byte[dataLen];
                    System.arraycopy(tokenData, dataOffset, dataArray, 0, dataLen);
                    data = new String(dataArray, StandardCharsets.UTF_16LE);
                }
                catch (Exception e) {
                    connectionlogger.severe(this.toString() + "Failed to read FedAuthInfoData.");
                    throw new SQLServerException(SQLServerException.getErrString("R_FedAuthInfoFailedToReadData"), e);
                }
                if (connectionlogger.isLoggable(Level.FINER)) {
                    connectionlogger.fine(this.toString() + " FedAuthInfoData: " + data);
                }
                switch (id) {
                    case 2: {
                        sqlFedAuthInfo.spn = data;
                        continue block6;
                    }
                    case 1: {
                        sqlFedAuthInfo.stsurl = data;
                        continue block6;
                    }
                    default: {
                        if (!connectionlogger.isLoggable(Level.FINER)) continue block6;
                        connectionlogger.fine(this.toString() + " Ignoring unknown federated authentication info option: " + id);
                    }
                }
            }
        } else {
            if (connectionlogger.isLoggable(Level.SEVERE)) {
                connectionlogger.severe(this.toString() + "FEDAUTHINFO token stream is not long enough to contain the data it claims to.");
            }
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_FedAuthInfoLengthTooShortForData"));
            throw new SQLServerException(form.format(new Object[]{tokenLen}), null);
        }
        if (null == sqlFedAuthInfo.spn || null == sqlFedAuthInfo.stsurl || sqlFedAuthInfo.spn.trim().isEmpty() || sqlFedAuthInfo.stsurl.trim().isEmpty()) {
            if (connectionlogger.isLoggable(Level.SEVERE)) {
                connectionlogger.severe(this.toString() + "FEDAUTHINFO token stream does not contain both STSURL and SPN.");
            }
            throw new SQLServerException(SQLServerException.getErrString("R_FedAuthInfoDoesNotContainStsurlAndSpn"), null);
        }
        this.onFedAuthInfo(sqlFedAuthInfo, tdsTokenHandler);
        this.aadPrincipalSecret = "";
        this.activeConnectionProperties.remove(SQLServerDriverStringProperty.AAD_SECURE_PRINCIPAL_SECRET.toString());
    }

    final void processSessionState(TDSReader tdsReader) throws SQLServerException {
        if (this.sessionRecovery.isConnectionRecoveryNegotiated()) {
            byte status;
            tdsReader.readUnsignedByte();
            long dataLength = tdsReader.readUnsignedInt();
            if (dataLength < 7L) {
                if (connectionlogger.isLoggable(Level.SEVERE)) {
                    connectionlogger.severe(this.toString() + "SESSIONSTATETOKEN token stream is not long enough to contain the data it claims to.");
                }
                this.sessionRecovery.getSessionStateTable().setMasterRecoveryDisabled(true);
                tdsReader.throwInvalidTDS();
            }
            int sequenceNumber = tdsReader.readInt();
            long dataBytesRead = 4L;
            if (-1L == (long)sequenceNumber) {
                this.sessionRecovery.getSessionStateTable().setMasterRecoveryDisabled(true);
            }
            boolean fRecoverable = ((status = (byte)tdsReader.readUnsignedByte()) & 1) > 0;
            ++dataBytesRead;
            while (dataBytesRead < dataLength) {
                short sessionStateId = (short)tdsReader.readUnsignedByte();
                int sessionStateLength = tdsReader.readUnsignedByte();
                dataBytesRead += 2L;
                if (sessionStateLength >= 255) {
                    sessionStateLength = (int)tdsReader.readUnsignedInt();
                    dataBytesRead += 4L;
                }
                if (this.sessionRecovery.getSessionStateTable().getSessionStateDelta()[sessionStateId] == null) {
                    this.sessionRecovery.getSessionStateTable().getSessionStateDelta()[sessionStateId] = new SessionStateValue();
                }
                if (-1L != (long)sequenceNumber && (null == this.sessionRecovery.getSessionStateTable().getSessionStateDelta()[sessionStateId].getData() || this.sessionRecovery.getSessionStateTable().getSessionStateDelta()[sessionStateId].isSequenceNumberGreater(sequenceNumber))) {
                    this.sessionRecovery.getSessionStateTable().updateSessionState(tdsReader, sessionStateId, sessionStateLength, sequenceNumber, fRecoverable);
                } else {
                    tdsReader.readSkipBytes(sessionStateLength);
                }
                dataBytesRead += (long)sessionStateLength;
            }
            if (dataBytesRead != dataLength) {
                if (connectionlogger.isLoggable(Level.SEVERE)) {
                    connectionlogger.severe(this.toString() + " Session State data length is corrupt.");
                }
                this.sessionRecovery.getSessionStateTable().setMasterRecoveryDisabled(true);
                tdsReader.throwInvalidTDS();
            }
        } else {
            if (connectionlogger.isLoggable(Level.SEVERE)) {
                connectionlogger.severe(this.toString() + " Session state received when session recovery was not negotiated.");
            }
            tdsReader.throwInvalidTDSToken(TDS.getTokenName(tdsReader.peekTokenType()));
        }
    }

    void onFedAuthInfo(SqlFedAuthInfo fedAuthInfo, TDSTokenHandler tdsTokenHandler) throws SQLServerException {
        assert (null != this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()) && null != this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()) || this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_INTEGRATED.toString()) || this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_MANAGED_IDENTITY.toString()) || this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_INTERACTIVE.toString()) && this.fedAuthRequiredPreLoginResponse);
        assert (null != fedAuthInfo);
        this.attemptRefreshTokenLocked = true;
        if (this.authenticationString.equals(SqlAuthentication.NOT_SPECIFIED.toString()) && null != this.accessTokenCallbackClass && !this.accessTokenCallbackClass.isEmpty()) {
            try {
                Object[] msgArgs = new Object[]{"accessTokenCallbackClass", "com.microsoft.sqlserver.jdbc.SQLServerAccessTokenCallback"};
                SQLServerAccessTokenCallback callbackInstance = (SQLServerAccessTokenCallback)Util.newInstance(SQLServerAccessTokenCallback.class, this.accessTokenCallbackClass, null, msgArgs);
                this.fedAuthToken = callbackInstance.getAccessToken(fedAuthInfo.spn, fedAuthInfo.stsurl);
            }
            catch (Exception e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidAccessTokenCallbackClass"));
                throw new SQLServerException(form.format(new Object[]{this.accessTokenCallbackClass}), e.getCause());
            }
        } else {
            this.fedAuthToken = this.authenticationString.equals(SqlAuthentication.NOT_SPECIFIED.toString()) && null != this.accessTokenCallback ? this.accessTokenCallback.getAccessToken(fedAuthInfo.spn, fedAuthInfo.stsurl) : this.getFedAuthToken(fedAuthInfo);
        }
        this.attemptRefreshTokenLocked = false;
        assert (null != this.fedAuthToken);
        FedAuthTokenCommand fedAuthCommand = new FedAuthTokenCommand(this.fedAuthToken, tdsTokenHandler);
        fedAuthCommand.execute(this.tdsChannel.getWriter(), this.tdsChannel.getReader(fedAuthCommand));
    }

    private SqlAuthenticationToken getFedAuthToken(SqlFedAuthInfo fedAuthInfo) throws SQLServerException {
        block20: {
            assert (null != fedAuthInfo);
            String user = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString());
            int sleepInterval = 100;
            if (!this.msalContextExists() && !this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_INTEGRATED.toString())) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_MSALMissing"));
                throw new SQLServerException(form.format(new Object[]{this.authenticationString}), null, 0, null);
            }
            if (loggerExternal.isLoggable(Level.FINEST)) {
                loggerExternal.finest("Getting FedAuth token " + fedAuthInfo.toString());
            }
            do {
                if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_PASSWORD.toString())) {
                    this.fedAuthToken = SQLServerMSAL4JUtils.getSqlFedAuthToken(fedAuthInfo, user, this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()), this.authenticationString);
                    break block20;
                }
                if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_MANAGED_IDENTITY.toString())) {
                    String managedIdentityClientId = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString());
                    this.fedAuthToken = null != managedIdentityClientId && !managedIdentityClientId.isEmpty() ? SQLServerSecurityUtility.getManagedIdentityCredAuthToken(fedAuthInfo.spn, managedIdentityClientId) : SQLServerSecurityUtility.getManagedIdentityCredAuthToken(fedAuthInfo.spn, this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.MSI_CLIENT_ID.toString()));
                    break block20;
                }
                if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_SERVICE_PRINCIPAL.toString())) {
                    this.fedAuthToken = this.aadPrincipalID != null && !this.aadPrincipalID.isEmpty() && this.aadPrincipalSecret != null && !this.aadPrincipalSecret.isEmpty() ? SQLServerMSAL4JUtils.getSqlFedAuthTokenPrincipal(fedAuthInfo, this.aadPrincipalID, this.aadPrincipalSecret, this.authenticationString) : SQLServerMSAL4JUtils.getSqlFedAuthTokenPrincipal(fedAuthInfo, this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()), this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()), this.authenticationString);
                    break block20;
                }
                if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_SERVICE_PRINCIPAL_CERTIFICATE.toString())) {
                    this.fedAuthToken = SQLServerMSAL4JUtils.getSqlFedAuthTokenPrincipalCertificate(fedAuthInfo, this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString()), this.servicePrincipalCertificate, this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString()), this.servicePrincipalCertificateKey, this.servicePrincipalCertificatePassword, this.authenticationString);
                    break block20;
                }
                if (this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_INTEGRATED.toString())) {
                    if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("windows") && AuthenticationJNI.isDllLoaded()) {
                        try {
                            FedAuthDllInfo dllInfo = AuthenticationJNI.getAccessTokenForWindowsIntegrated(fedAuthInfo.stsurl, fedAuthInfo.spn, this.clientConnectionId.toString(), "7f98cb04-cd1e-40df-9140-3bf7e2cea4db", 0L);
                            assert (null != dllInfo.accessTokenBytes);
                            byte[] accessTokenFromDLL = dllInfo.accessTokenBytes;
                            String accessToken = new String(accessTokenFromDLL, StandardCharsets.UTF_16LE);
                            Date now = new Date();
                            now.setTime(now.getTime() + dllInfo.expiresIn * 1000L);
                            this.fedAuthToken = new SqlAuthenticationToken(accessToken, now);
                        }
                        catch (DLLException adalException) {
                            int errorCategory = adalException.getCategory();
                            if (-1 == errorCategory) {
                                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnableLoadADALSqlDll"));
                                Object[] msgArgs = new Object[]{Integer.toHexString(adalException.getState())};
                                throw new SQLServerException(form.format(msgArgs), null);
                            }
                            int millisecondsRemaining = SQLServerConnection.timerRemaining(this.timerExpire);
                            if (2 != errorCategory || SQLServerConnection.timerHasExpired(this.timerExpire) || sleepInterval >= millisecondsRemaining) {
                                String errorStatus = Integer.toHexString(adalException.getStatus());
                                if (connectionlogger.isLoggable(Level.FINER)) {
                                    connectionlogger.fine(this.toString() + " SQLServerConnection.getFedAuthToken.AdalException category:" + errorCategory + " error: " + errorStatus);
                                }
                                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ADALAuthenticationMiddleErrorMessage"));
                                String errorCode = Integer.toHexString(adalException.getStatus()).toUpperCase();
                                Object[] msgArgs1 = new Object[]{errorCode, adalException.getState()};
                                SQLServerException middleException = new SQLServerException(form.format(msgArgs1), adalException);
                                form = new MessageFormat(SQLServerException.getErrString("R_MSALExecution"));
                                Object[] msgArgs = new Object[]{user, this.authenticationString};
                                throw new SQLServerException(form.format(msgArgs), null, 0, (Throwable)middleException);
                            }
                            if (connectionlogger.isLoggable(Level.FINER)) {
                                connectionlogger.fine(this.toString() + " SQLServerConnection.getFedAuthToken sleeping: " + sleepInterval + " milliseconds.");
                                connectionlogger.fine(this.toString() + " SQLServerConnection.getFedAuthToken remaining: " + millisecondsRemaining + " milliseconds.");
                            }
                            try {
                                Thread.sleep(sleepInterval);
                            }
                            catch (InterruptedException e1) {
                                Thread.currentThread().interrupt();
                            }
                            sleepInterval *= 2;
                        }
                        break block20;
                    }
                    if (!this.msalContextExists()) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_DLLandMSALMissing"));
                        Object[] msgArgs = new Object[]{SQLServerDriver.AUTH_DLL_NAME, this.authenticationString};
                        throw new SQLServerException(form.format(msgArgs), null, 0, null);
                    }
                    this.fedAuthToken = SQLServerMSAL4JUtils.getSqlFedAuthTokenIntegrated(fedAuthInfo, this.authenticationString);
                    break block20;
                }
                if (!this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_INTERACTIVE.toString())) continue;
                this.fedAuthToken = SQLServerMSAL4JUtils.getSqlFedAuthTokenInteractive(fedAuthInfo, user, this.authenticationString);
                break block20;
            } while (!this.authenticationString.equalsIgnoreCase(SqlAuthentication.ACTIVE_DIRECTORY_DEFAULT.toString()));
            String managedIdentityClientId = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString());
            this.fedAuthToken = null != managedIdentityClientId && !managedIdentityClientId.isEmpty() ? SQLServerSecurityUtility.getDefaultAzureCredAuthToken(fedAuthInfo.spn, managedIdentityClientId) : SQLServerSecurityUtility.getDefaultAzureCredAuthToken(fedAuthInfo.spn, this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.MSI_CLIENT_ID.toString()));
        }
        return this.fedAuthToken;
    }

    private boolean msalContextExists() {
        try {
            Class.forName("com.microsoft.aad.msal4j.PublicClientApplication");
        }
        catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    final void processFeatureExtAck(TDSReader tdsReader) throws SQLServerException {
        byte featureId;
        tdsReader.readUnsignedByte();
        do {
            if ((featureId = (byte)tdsReader.readUnsignedByte()) == -1) continue;
            int dataLen = tdsReader.readInt();
            byte[] data = new byte[dataLen];
            if (dataLen > 0) {
                tdsReader.readBytes(data, 0, dataLen);
            }
            this.onFeatureExtAck(featureId, data);
        } while (featureId != -1);
    }

    private void onFeatureExtAck(byte featureId, byte[] data) throws SQLServerException {
        if (null != this.routingInfo && 11 != featureId) {
            return;
        }
        block0 : switch (featureId) {
            case 2: {
                if (connectionlogger.isLoggable(Level.FINER)) {
                    connectionlogger.fine(this.toString() + " Received feature extension acknowledgement for federated authentication.");
                }
                if (!this.federatedAuthenticationRequested) {
                    if (connectionlogger.isLoggable(Level.SEVERE)) {
                        connectionlogger.severe(this.toString() + " Did not request federated authentication.");
                    }
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnrequestedFeatureAckReceived"));
                    Object[] msgArgs = new Object[]{featureId};
                    throw new SQLServerException(form.format(msgArgs), null);
                }
                assert (null != this.fedAuthFeatureExtensionData);
                switch (this.fedAuthFeatureExtensionData.libraryType) {
                    case 1: 
                    case 2: {
                        if (0 == data.length) break block0;
                        if (connectionlogger.isLoggable(Level.SEVERE)) {
                            connectionlogger.severe(this.toString() + " Federated authentication feature extension ack for ADAL and Security Token includes extra data.");
                        }
                        throw new SQLServerException(SQLServerException.getErrString("R_FedAuthFeatureAckContainsExtraData"), null);
                    }
                    default: {
                        assert (false);
                        if (connectionlogger.isLoggable(Level.SEVERE)) {
                            connectionlogger.severe(this.toString() + " Attempting to use unknown federated authentication library.");
                        }
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_FedAuthFeatureAckUnknownLibraryType"));
                        Object[] msgArgs = new Object[]{this.fedAuthFeatureExtensionData.libraryType};
                        throw new SQLServerException(form.format(msgArgs), null);
                    }
                }
            }
            case 4: {
                if (connectionlogger.isLoggable(Level.FINER)) {
                    connectionlogger.fine(this.toString() + " Received feature extension acknowledgement for AE.");
                }
                if (1 > data.length) {
                    throw new SQLServerException(SQLServerException.getErrString("R_InvalidAEVersionNumber"), null);
                }
                this.aeVersion = data[0];
                if (0 == this.aeVersion || this.aeVersion > 3) {
                    throw new SQLServerException(SQLServerException.getErrString("R_InvalidAEVersionNumber"), null);
                }
                this.serverColumnEncryptionVersion = ColumnEncryptionVersion.AE_V1;
                String enclaveType = null;
                if (null == this.enclaveAttestationUrl && (this.enclaveAttestationProtocol == null || !this.enclaveAttestationProtocol.equalsIgnoreCase(AttestationProtocol.NONE.toString()))) break;
                if (this.aeVersion < 2) {
                    throw new SQLServerException(SQLServerException.getErrString("R_enclaveNotSupported"), null);
                }
                this.serverColumnEncryptionVersion = this.aeVersion == 3 ? ColumnEncryptionVersion.AE_V3 : ColumnEncryptionVersion.AE_V2;
                enclaveType = new String(data, 2, data.length - 2, StandardCharsets.UTF_16LE);
                boolean bl = this.serverSupportsEnclaveRetry = this.aeVersion == 3;
                if (EnclaveType.isValidEnclaveType(enclaveType)) break;
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_enclaveTypeInvalid"));
                Object[] msgArgs = new Object[]{enclaveType};
                throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
            }
            case 9: {
                if (connectionlogger.isLoggable(Level.FINER)) {
                    connectionlogger.fine(this.toString() + " Received feature extension acknowledgement for Data Classification.");
                }
                if (2 != data.length) {
                    throw new SQLServerException(SQLServerException.getErrString("R_UnknownDataClsTokenNumber"), null);
                }
                this.serverSupportedDataClassificationVersion = data[0];
                if (0 == this.serverSupportedDataClassificationVersion || this.serverSupportedDataClassificationVersion > 2) {
                    throw new SQLServerException(SQLServerException.getErrString("R_InvalidDataClsVersionNumber"), null);
                }
                byte enabled = data[1];
                this.serverSupportsDataClassification = enabled != 0;
                break;
            }
            case 10: {
                if (connectionlogger.isLoggable(Level.FINER)) {
                    connectionlogger.fine(this.toString() + " Received feature extension acknowledgement for UTF8 support.");
                }
                if (1 <= data.length) break;
                throw new SQLServerException(SQLServerException.getErrString("R_unknownUTF8SupportValue"), null);
            }
            case 11: {
                if (connectionlogger.isLoggable(Level.FINER)) {
                    connectionlogger.fine(this.toString() + " Received feature extension acknowledgement for Azure SQL DNS Caching.");
                }
                if (1 > data.length) {
                    throw new SQLServerException(SQLServerException.getErrString("R_unknownAzureSQLDNSCachingValue"), null);
                }
                if (1 == data[0]) {
                    this.serverSupportsDNSCaching = true;
                    if (null != dnsCache) break;
                    dnsCache = new ConcurrentHashMap();
                    break;
                }
                this.serverSupportsDNSCaching = false;
                if (null == dnsCache) break;
                dnsCache.remove(this.currentConnectPlaceHolder.getServerName());
                break;
            }
            case 1: {
                if (connectionlogger.isLoggable(Level.FINER)) {
                    connectionlogger.fine(this.toString() + " Received feature extension acknowledgement for Idle Connection Resiliency.");
                }
                this.sessionRecovery.parseInitialSessionStateData(data, this.sessionRecovery.getSessionStateTable().getSessionStateInitial());
                this.sessionRecovery.setConnectionRecoveryNegotiated(true);
                this.sessionRecovery.setConnectionRecoveryPossible(true);
                break;
            }
            default: {
                throw new SQLServerException(SQLServerException.getErrString("R_UnknownFeatureAck"), null);
            }
        }
    }

    private void executeDTCCommand(int requestType, byte[] payload, String logContext) throws SQLServerException {
        final class DTCCommand
        extends UninterruptableTDSCommand {
            private static final long serialVersionUID = 1L;
            private final int requestType;
            private final byte[] payload;

            DTCCommand(int requestType, byte[] payload, String logContext) {
                super(logContext);
                this.requestType = requestType;
                this.payload = payload;
            }

            @Override
            final boolean doExecute() throws SQLServerException {
                TDSWriter tdsWriter = this.startRequest((byte)14);
                tdsWriter.sendEnclavePackage(null, null);
                tdsWriter.writeShort((short)this.requestType);
                if (null == this.payload) {
                    tdsWriter.writeShort((short)0);
                } else {
                    assert (this.payload.length <= Short.MAX_VALUE);
                    tdsWriter.writeShort((short)this.payload.length);
                    tdsWriter.writeBytes(this.payload);
                }
                TDSParser.parse(this.startResponse(), this.getLogContext());
                return true;
            }
        }
        this.executeCommand(new DTCCommand(requestType, payload, logContext));
    }

    final void jtaUnenlistConnection() throws SQLServerException {
        this.executeDTCCommand(1, null, "MS_DTC delist connection");
        this.inXATransaction = false;
    }

    final void jtaEnlistConnection(byte[] cookie) throws SQLServerException {
        this.executeDTCCommand(1, cookie, "MS_DTC enlist connection");
        this.connectionCommand(this.sqlStatementToSetTransactionIsolationLevel(), "jtaEnlistConnection");
        this.inXATransaction = true;
    }

    private byte[] toUCS16(String s) {
        if (s == null) {
            return new byte[0];
        }
        int l = s.length();
        byte[] data = new byte[l * 2];
        int offset = 0;
        for (int i = 0; i < l; ++i) {
            char c = s.charAt(i);
            byte b1 = (byte)(c & 0xFF);
            data[offset++] = b1;
            data[offset++] = (byte)(c >> 8 & 0xFF);
        }
        return data;
    }

    private byte[] encryptPassword(String pwd) {
        if (pwd == null) {
            pwd = "";
        }
        int len = pwd.length();
        byte[] data = new byte[len * 2];
        for (int i1 = 0; i1 < len; ++i1) {
            byte b2;
            byte b1;
            int j1 = pwd.charAt(i1) ^ 0x5A5A;
            j1 = (j1 & 0xF) << 4 | (j1 & 0xF0) >> 4 | (j1 & 0xF00) << 4 | (j1 & 0xF000) >> 4;
            data[i1 * 2 + 1] = b1 = (byte)((j1 & 0xFF00) >> 8);
            data[i1 * 2 + 0] = b2 = (byte)(j1 & 0xFF);
        }
        return data;
    }

    private void sendLogon(LogonCommand logonCommand, SSPIAuthentication authentication, FederatedAuthenticationFeatureExtensionData fedAuthFeatureExtensionData) throws SQLServerException {
        TDSReader tdsReader;
        Object serverName;
        assert (!this.integratedSecurity || !this.fedAuthRequiredPreLoginResponse);
        assert (!this.integratedSecurity || !this.federatedAuthenticationInfoRequested && !this.federatedAuthenticationRequested);
        assert (null == fedAuthFeatureExtensionData || this.federatedAuthenticationInfoRequested || this.federatedAuthenticationRequested);
        assert (null != fedAuthFeatureExtensionData || !this.federatedAuthenticationInfoRequested && !this.federatedAuthenticationRequested);
        String sUser = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.USER.toString());
        String sPwd = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.PASSWORD.toString());
        String appName = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.APPLICATION_NAME.toString());
        String interfaceLibName = "Microsoft JDBC Driver 12.4";
        String databaseName = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.DATABASE_NAME.toString());
        if (null != this.currentConnectPlaceHolder) {
            serverName = this.currentConnectPlaceHolder.getFullServerName();
        } else {
            serverName = this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.SERVER_NAME.toString());
            if (null != this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.INSTANCE_NAME.toString())) {
                serverName = (String)serverName + "\\" + this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.INSTANCE_NAME.toString());
            }
        }
        if (null != serverName && ((String)serverName).length() > 128) {
            serverName = ((String)serverName).substring(0, 128);
        }
        byte[] secBlob = new byte[]{};
        boolean[] done = new boolean[]{false};
        if (null != authentication) {
            secBlob = authentication.generateClientContext(secBlob, done);
            sUser = null;
            sPwd = null;
        }
        byte[] hostnameBytes = this.toUCS16(this.hostName);
        byte[] userBytes = this.toUCS16(sUser);
        byte[] passwordBytes = this.encryptPassword(sPwd);
        int passwordLen = null != passwordBytes ? passwordBytes.length : 0;
        byte[] appNameBytes = this.toUCS16(appName);
        byte[] serverNameBytes = this.toUCS16((String)serverName);
        byte[] interfaceLibNameBytes = this.toUCS16(interfaceLibName);
        byte[] interfaceLibVersionBytes = new byte[]{0, 2, 4, 12};
        byte[] databaseNameBytes = this.toUCS16(databaseName);
        int dataLen = 0;
        if (this.encryptOption.compareToIgnoreCase(EncryptOption.STRICT.toString()) == 0) {
            this.tdsVersion = 0x8000000;
        } else if (this.serverMajorVersion >= 11) {
            this.tdsVersion = 0x74000004;
        } else if (this.serverMajorVersion >= 10) {
            this.tdsVersion = 1930100739;
        } else if (this.serverMajorVersion >= 9) {
            this.tdsVersion = 1913192450;
        } else assert (false) : "prelogin did not disconnect for the old version: " + this.serverMajorVersion;
        int tdsLoginRequestBaseLength = 94;
        TDSWriter tdsWriter = logonCommand.startRequest((byte)16);
        int len = 94 + hostnameBytes.length + appNameBytes.length + serverNameBytes.length + interfaceLibNameBytes.length + databaseNameBytes.length + (secBlob != null ? secBlob.length : 0) + 4;
        if (!(this.integratedSecurity || this.federatedAuthenticationInfoRequested || this.federatedAuthenticationRequested || null != this.clientCertificate)) {
            len = len + passwordLen + userBytes.length;
        }
        int aeOffset = len;
        len += this.writeAEFeatureRequest(false, tdsWriter);
        if (this.federatedAuthenticationInfoRequested || this.federatedAuthenticationRequested) {
            len += this.writeFedAuthFeatureRequest(false, tdsWriter, fedAuthFeatureExtensionData);
        }
        len += this.writeDataClassificationFeatureRequest(false, tdsWriter);
        len += this.writeUTF8SupportFeatureRequest(false, tdsWriter);
        len += this.writeDNSCacheFeatureRequest(false, tdsWriter);
        ++len;
        if (this.connectRetryCount > 0) {
            len += this.writeIdleConnectionResiliencyRequest(false, tdsWriter);
        }
        tdsWriter.writeInt(len);
        tdsWriter.writeInt(this.tdsVersion);
        tdsWriter.writeInt(this.requestedPacketSize);
        tdsWriter.writeBytes(interfaceLibVersionBytes);
        tdsWriter.writeInt(DriverJDBCVersion.getProcessId());
        tdsWriter.writeInt(0);
        tdsWriter.writeByte((byte)-32);
        tdsWriter.writeByte((byte)(3 | (this.replication ? 48 : 0) | (this.integratedSecurity ? -128 : 0)));
        tdsWriter.writeByte((byte)(0 | (this.applicationIntent != null && this.applicationIntent.equals((Object)ApplicationIntent.READ_ONLY) ? 32 : 0)));
        int colEncSetting = 16;
        tdsWriter.writeByte((byte)(0 | colEncSetting | (this.serverMajorVersion >= 10 ? 8 : 0)));
        tdsWriter.writeInt(0);
        tdsWriter.writeInt(0);
        tdsWriter.writeShort((short)94);
        tdsWriter.writeShort((short)(this.hostName != null && !this.hostName.isEmpty() ? this.hostName.length() : 0));
        dataLen += hostnameBytes.length;
        if (this.ntlmAuthentication) {
            tdsWriter.writeShort((short)(94 + dataLen));
            tdsWriter.writeShort((short)0);
            tdsWriter.writeShort((short)(94 + dataLen));
            tdsWriter.writeShort((short)0);
        } else if (!(this.integratedSecurity || this.federatedAuthenticationInfoRequested || this.federatedAuthenticationRequested || null != this.clientCertificate)) {
            tdsWriter.writeShort((short)(94 + dataLen));
            tdsWriter.writeShort((short)(sUser == null ? 0 : sUser.length()));
            tdsWriter.writeShort((short)(94 + (dataLen += userBytes.length)));
            tdsWriter.writeShort((short)(sPwd == null ? 0 : sPwd.length()));
            dataLen += passwordLen;
        } else {
            tdsWriter.writeShort((short)0);
            tdsWriter.writeShort((short)0);
            tdsWriter.writeShort((short)0);
            tdsWriter.writeShort((short)0);
        }
        tdsWriter.writeShort((short)(94 + dataLen));
        tdsWriter.writeShort((short)(appName == null ? 0 : appName.length()));
        tdsWriter.writeShort((short)(94 + (dataLen += appNameBytes.length)));
        tdsWriter.writeShort((short)(serverName == null ? 0 : ((String)serverName).length()));
        tdsWriter.writeShort((short)(94 + (dataLen += serverNameBytes.length)));
        tdsWriter.writeShort((short)4);
        dataLen += 4;
        assert (null != interfaceLibName);
        tdsWriter.writeShort((short)(94 + dataLen));
        tdsWriter.writeShort((short)interfaceLibName.length());
        tdsWriter.writeShort((short)0);
        tdsWriter.writeShort((short)0);
        tdsWriter.writeShort((short)(94 + (dataLen += interfaceLibNameBytes.length)));
        tdsWriter.writeShort((short)(databaseName == null ? 0 : databaseName.length()));
        dataLen += databaseNameBytes.length;
        tdsWriter.writeBytes(netAddress);
        int uShortMax = 65535;
        if (!this.integratedSecurity) {
            tdsWriter.writeShort((short)0);
            tdsWriter.writeShort((short)0);
        } else {
            tdsWriter.writeShort((short)(94 + dataLen));
            if (65535 <= secBlob.length) {
                tdsWriter.writeShort((short)-1);
            } else {
                tdsWriter.writeShort((short)secBlob.length);
            }
        }
        tdsWriter.writeShort((short)0);
        tdsWriter.writeShort((short)0);
        if (this.tdsVersion >= 1913192450 || this.tdsVersion == 0x8000000) {
            tdsWriter.writeShort((short)0);
            tdsWriter.writeShort((short)0);
            if (null != secBlob && 65535 <= secBlob.length) {
                tdsWriter.writeInt(secBlob.length);
            } else {
                tdsWriter.writeInt(0);
            }
        }
        tdsWriter.writeBytes(hostnameBytes);
        tdsWriter.setDataLoggable(false);
        if (!(this.integratedSecurity || this.federatedAuthenticationInfoRequested || this.federatedAuthenticationRequested || null != this.clientCertificate)) {
            tdsWriter.writeBytes(userBytes);
            tdsWriter.writeBytes(passwordBytes);
        }
        tdsWriter.setDataLoggable(true);
        tdsWriter.writeBytes(appNameBytes);
        tdsWriter.writeBytes(serverNameBytes);
        tdsWriter.writeInt(aeOffset);
        tdsWriter.writeBytes(interfaceLibNameBytes);
        tdsWriter.writeBytes(databaseNameBytes);
        tdsWriter.setDataLoggable(false);
        if (this.integratedSecurity) {
            tdsWriter.writeBytes(secBlob, 0, secBlob.length);
        }
        this.writeAEFeatureRequest(true, tdsWriter);
        if (this.federatedAuthenticationInfoRequested || this.federatedAuthenticationRequested) {
            this.writeFedAuthFeatureRequest(true, tdsWriter, fedAuthFeatureExtensionData);
        }
        this.writeDataClassificationFeatureRequest(true, tdsWriter);
        this.writeUTF8SupportFeatureRequest(true, tdsWriter);
        this.writeDNSCacheFeatureRequest(true, tdsWriter);
        if (this.connectRetryCount > 0) {
            this.writeIdleConnectionResiliencyRequest(true, tdsWriter);
        }
        tdsWriter.writeByte((byte)-1);
        tdsWriter.setDataLoggable(true);
        final class LogonProcessor
        extends TDSTokenHandler {
            private final SSPIAuthentication auth;
            private byte[] secBlobOut;
            StreamLoginAck loginAckToken;

            LogonProcessor(SSPIAuthentication auth) {
                super("logon");
                this.secBlobOut = null;
                this.auth = auth;
                this.loginAckToken = null;
            }

            @Override
            boolean onSSPI(TDSReader tdsReader) throws SQLServerException {
                StreamSSPI ack = new StreamSSPI();
                ack.setFromTDS(tdsReader);
                boolean[] done = new boolean[]{false};
                this.secBlobOut = this.auth.generateClientContext(ack.sspiBlob, done);
                return true;
            }

            @Override
            boolean onLoginAck(TDSReader tdsReader) throws SQLServerException {
                this.loginAckToken = new StreamLoginAck();
                this.loginAckToken.setFromTDS(tdsReader);
                SQLServerConnection.this.sqlServerVersion = this.loginAckToken.sSQLServerVersion;
                SQLServerConnection.this.tdsVersion = this.loginAckToken.tdsVersion;
                return true;
            }

            final boolean complete(LogonCommand logonCommand, TDSReader tdsReader) throws SQLServerException {
                if (null != this.loginAckToken) {
                    return true;
                }
                if (null != this.secBlobOut && 0 != this.secBlobOut.length) {
                    logonCommand.startRequest((byte)17).writeBytes(this.secBlobOut, 0, this.secBlobOut.length);
                    return false;
                }
                logonCommand.startRequest((byte)17);
                logonCommand.onRequestComplete();
                ++SQLServerConnection.this.tdsChannel.numMsgsSent;
                TDSParser.parse(tdsReader, this);
                return true;
            }
        }
        LogonProcessor logonProcessor = new LogonProcessor(authentication);
        do {
            tdsReader = logonCommand.startResponse();
            this.sessionRecovery.setConnectionRecoveryPossible(false);
            TDSParser.parse(tdsReader, logonProcessor);
        } while (!logonProcessor.complete(logonCommand, tdsReader));
        if (this.sessionRecovery.isReconnectRunning() && !this.sessionRecovery.isConnectionRecoveryPossible()) {
            if (connectionlogger.isLoggable(Level.WARNING)) {
                connectionlogger.warning(this.toString() + "SessionRecovery feature extension ack was not sent by the server during reconnection.");
            }
            this.terminate(4, SQLServerException.getErrString("R_crClientNoRecoveryAckFromLogin"));
        }
        if (this.connectRetryCount > 0 && !this.sessionRecovery.isReconnectRunning()) {
            this.sessionRecovery.getSessionStateTable().setOriginalCatalog(this.sCatalog);
            this.sessionRecovery.getSessionStateTable().setOriginalCollation(this.databaseCollation);
            this.sessionRecovery.getSessionStateTable().setOriginalLanguage(this.sLanguage);
        }
    }

    private void checkValidHoldability(int holdability) throws SQLServerException {
        if (holdability != 1 && holdability != 2) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidHoldability"));
            SQLServerException.makeFromDriverError(this, this, form.format(new Object[]{holdability}), null, true);
        }
    }

    private void checkMatchesCurrentHoldability(int resultSetHoldability) throws SQLServerException {
        if (resultSetHoldability != this.holdability) {
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_sqlServerHoldability"), null, false);
        }
    }

    @Override
    public Statement createStatement(int nType, int nConcur, int resultSetHoldability) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, CREATE_STATEMENT, new Object[]{nType, nConcur, resultSetHoldability});
        Statement st = this.createStatement(nType, nConcur, resultSetHoldability, SQLServerStatementColumnEncryptionSetting.USE_CONNECTION_SETTING);
        loggerExternal.exiting(this.loggingClassName, CREATE_STATEMENT, st);
        return st;
    }

    @Override
    public Statement createStatement(int nType, int nConcur, int resultSetHoldability, SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, CREATE_STATEMENT, new Object[]{nType, nConcur, resultSetHoldability, stmtColEncSetting});
        this.checkClosed();
        this.checkValidHoldability(resultSetHoldability);
        this.checkMatchesCurrentHoldability(resultSetHoldability);
        SQLServerStatement st = new SQLServerStatement(this, nType, nConcur, stmtColEncSetting);
        if (this.requestStarted) {
            this.addOpenStatement(st);
        }
        loggerExternal.exiting(this.loggingClassName, CREATE_STATEMENT, st);
        return st;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int nType, int nConcur, int resultSetHoldability) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "prepareStatement", new Object[]{nType, nConcur, resultSetHoldability});
        PreparedStatement st = this.prepareStatement(sql, nType, nConcur, resultSetHoldability, SQLServerStatementColumnEncryptionSetting.USE_CONNECTION_SETTING);
        loggerExternal.exiting(this.loggingClassName, "prepareStatement", st);
        return st;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int nType, int nConcur, int resultSetHoldability, SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "prepareStatement", new Object[]{nType, nConcur, resultSetHoldability, stmtColEncSetting});
        this.checkClosed();
        this.checkValidHoldability(resultSetHoldability);
        this.checkMatchesCurrentHoldability(resultSetHoldability);
        SQLServerPreparedStatement st = new SQLServerPreparedStatement(this, sql, nType, nConcur, stmtColEncSetting);
        if (this.requestStarted) {
            this.addOpenStatement(st);
        }
        loggerExternal.exiting(this.loggingClassName, "prepareStatement", st);
        return st;
    }

    @Override
    public CallableStatement prepareCall(String sql, int nType, int nConcur, int resultSetHoldability) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "prepareStatement", new Object[]{nType, nConcur, resultSetHoldability});
        CallableStatement st = this.prepareCall(sql, nType, nConcur, resultSetHoldability, SQLServerStatementColumnEncryptionSetting.USE_CONNECTION_SETTING);
        loggerExternal.exiting(this.loggingClassName, "prepareCall", st);
        return st;
    }

    @Override
    public CallableStatement prepareCall(String sql, int nType, int nConcur, int resultSetHoldability, SQLServerStatementColumnEncryptionSetting stmtColEncSetiing) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "prepareStatement", new Object[]{nType, nConcur, resultSetHoldability, stmtColEncSetiing});
        this.checkClosed();
        this.checkValidHoldability(resultSetHoldability);
        this.checkMatchesCurrentHoldability(resultSetHoldability);
        SQLServerCallableStatement st = new SQLServerCallableStatement(this, sql, nType, nConcur, stmtColEncSetiing);
        if (this.requestStarted) {
            this.addOpenStatement(st);
        }
        loggerExternal.exiting(this.loggingClassName, "prepareCall", st);
        return st;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int flag) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggingClassName, "prepareStatement", new Object[]{sql, flag});
        }
        SQLServerPreparedStatement ps = (SQLServerPreparedStatement)this.prepareStatement(sql, flag, SQLServerStatementColumnEncryptionSetting.USE_CONNECTION_SETTING);
        loggerExternal.exiting(this.loggingClassName, "prepareStatement", ps);
        return ps;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int flag, SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggingClassName, "prepareStatement", new Object[]{sql, flag, stmtColEncSetting});
        }
        this.checkClosed();
        SQLServerPreparedStatement ps = (SQLServerPreparedStatement)this.prepareStatement(sql, 1003, 1007, stmtColEncSetting);
        ps.bRequestedGeneratedKeys = flag == 1;
        loggerExternal.exiting(this.loggingClassName, "prepareStatement", ps);
        return ps;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggingClassName, "prepareStatement", new Object[]{sql, columnIndexes});
        }
        SQLServerPreparedStatement ps = (SQLServerPreparedStatement)this.prepareStatement(sql, columnIndexes, SQLServerStatementColumnEncryptionSetting.USE_CONNECTION_SETTING);
        loggerExternal.exiting(this.loggingClassName, "prepareStatement", ps);
        return ps;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes, SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "prepareStatement", new Object[]{sql, columnIndexes, stmtColEncSetting});
        this.checkClosed();
        if (columnIndexes == null || columnIndexes.length != 1) {
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_invalidColumnArrayLength"), null, false);
        }
        SQLServerPreparedStatement ps = (SQLServerPreparedStatement)this.prepareStatement(sql, 1003, 1007, stmtColEncSetting);
        ps.bRequestedGeneratedKeys = true;
        loggerExternal.exiting(this.loggingClassName, "prepareStatement", ps);
        return ps;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggingClassName, "prepareStatement", new Object[]{sql, columnNames});
        }
        SQLServerPreparedStatement ps = (SQLServerPreparedStatement)this.prepareStatement(sql, columnNames, SQLServerStatementColumnEncryptionSetting.USE_CONNECTION_SETTING);
        loggerExternal.exiting(this.loggingClassName, "prepareStatement", ps);
        return ps;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames, SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "prepareStatement", new Object[]{sql, columnNames, stmtColEncSetting});
        this.checkClosed();
        if (columnNames == null || columnNames.length != 1) {
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_invalidColumnArrayLength"), null, false);
        }
        SQLServerPreparedStatement ps = (SQLServerPreparedStatement)this.prepareStatement(sql, 1003, 1007, stmtColEncSetting);
        ps.bRequestedGeneratedKeys = true;
        loggerExternal.exiting(this.loggingClassName, "prepareStatement", ps);
        return ps;
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        loggerExternal.entering(this.loggingClassName, "releaseSavepoint", savepoint);
        SQLServerException.throwNotSupportedException(this, null);
    }

    private final Savepoint setNamedSavepoint(String sName) throws SQLServerException {
        if (this.databaseAutoCommitMode) {
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_cantSetSavepoint"), null, false);
        }
        SQLServerSavepoint s = new SQLServerSavepoint(this, sName);
        this.connectionCommand("IF @@TRANCOUNT = 0 BEGIN BEGIN TRAN IF @@TRANCOUNT = 2 COMMIT TRAN END SAVE TRAN " + Util.escapeSQLId(s.getLabel()), SET_SAVE_POINT);
        return s;
    }

    @Override
    public Savepoint setSavepoint(String sName) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, SET_SAVE_POINT, sName);
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        this.checkClosed();
        Savepoint pt = this.setNamedSavepoint(sName);
        loggerExternal.exiting(this.loggingClassName, SET_SAVE_POINT, pt);
        return pt;
    }

    @Override
    public Savepoint setSavepoint() throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, SET_SAVE_POINT);
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        this.checkClosed();
        Savepoint pt = this.setNamedSavepoint(null);
        loggerExternal.exiting(this.loggingClassName, SET_SAVE_POINT, pt);
        return pt;
    }

    @Override
    public void rollback(Savepoint s) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "rollback", s);
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        this.checkClosed();
        if (this.databaseAutoCommitMode) {
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_cantInvokeRollback"), null, false);
        }
        this.connectionCommand("IF @@TRANCOUNT > 0 ROLLBACK TRAN " + Util.escapeSQLId(((SQLServerSavepoint)s).getLabel()), "rollbackSavepoint");
        loggerExternal.exiting(this.loggingClassName, "rollback");
    }

    @Override
    public int getHoldability() throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "getHoldability");
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.exiting(this.loggingClassName, "getHoldability", this.holdability);
        }
        return this.holdability;
    }

    @Override
    public void setHoldability(int holdability) throws SQLServerException {
        loggerExternal.entering(this.loggingClassName, "setHoldability", holdability);
        if (loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            loggerExternal.finer(this.toString() + ACTIVITY_ID + ActivityCorrelator.getCurrent().toString());
        }
        this.checkValidHoldability(holdability);
        this.checkClosed();
        if (this.holdability != holdability) {
            this.connectionCommand(holdability == 2 ? "SET CURSOR_CLOSE_ON_COMMIT ON" : "SET CURSOR_CLOSE_ON_COMMIT OFF", "setHoldability");
            this.holdability = holdability;
        }
        loggerExternal.exiting(this.loggingClassName, "setHoldability");
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        loggerExternal.entering(this.loggingClassName, "getNetworkTimeout");
        this.checkClosed();
        int timeout = 0;
        try {
            timeout = this.tdsChannel.getNetworkTimeout();
        }
        catch (IOException ioe) {
            this.terminate(3, ioe.getMessage(), ioe);
        }
        loggerExternal.exiting(this.loggingClassName, "getNetworkTimeout");
        return timeout;
    }

    @Override
    public void setNetworkTimeout(Executor executor, int timeout) throws SQLException {
        loggerExternal.entering(this.loggingClassName, SET_NETWORK_TIMEOUT_PERM, timeout);
        if (timeout < 0) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidSocketTimeout"));
            Object[] msgArgs = new Object[]{timeout};
            SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, false);
        }
        this.checkClosed();
        SecurityManager secMgr = System.getSecurityManager();
        if (secMgr != null) {
            try {
                SQLPermission perm = new SQLPermission(SET_NETWORK_TIMEOUT_PERM);
                secMgr.checkPermission(perm);
            }
            catch (SecurityException ex) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_permissionDenied"));
                Object[] msgArgs = new Object[]{SET_NETWORK_TIMEOUT_PERM};
                SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, true);
            }
        }
        try {
            this.tdsChannel.setNetworkTimeout(timeout);
        }
        catch (IOException ioe) {
            this.terminate(3, ioe.getMessage(), ioe);
        }
        loggerExternal.exiting(this.loggingClassName, SET_NETWORK_TIMEOUT_PERM);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String getSchema() throws SQLException {
        loggerExternal.entering(this.loggingClassName, "getSchema");
        this.checkClosed();
        try (SQLServerStatement stmt = (SQLServerStatement)this.createStatement();
             SQLServerResultSet resultSet = stmt.executeQueryInternal("SELECT SCHEMA_NAME()");){
            if (resultSet != null) {
                resultSet.next();
                String string = resultSet.getString(1);
                return string;
            }
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_getSchemaError"), null, true);
        }
        catch (SQLException e) {
            if (this.isSessionUnAvailable()) {
                throw e;
            }
            SQLServerException.makeFromDriverError(this, this, SQLServerException.getErrString("R_getSchemaError"), null, true);
        }
        loggerExternal.exiting(this.loggingClassName, "getSchema");
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        loggerExternal.entering(this.loggingClassName, "setSchema", schema);
        this.checkClosed();
        this.addWarning(SQLServerException.getErrString("R_setSchemaWarning"));
        loggerExternal.exiting(this.loggingClassName, "setSchema");
    }

    @Override
    public void setSendTimeAsDatetime(boolean sendTimeAsDateTimeValue) {
        this.sendTimeAsDatetime = sendTimeAsDateTimeValue;
    }

    @Override
    public void setDatetimeParameterType(String datetimeParameterTypeValue) throws SQLServerException {
        if (datetimeParameterTypeValue != null) {
            datetimeParameterTypeValue = datetimeParameterTypeValue.toLowerCase();
        }
        this.datetimeParameterType = DatetimeType.valueOfString(datetimeParameterTypeValue);
    }

    @Override
    public void setUseFmtOnly(boolean useFmtOnly) {
        this.useFmtOnly = useFmtOnly;
    }

    @Override
    public final boolean getUseFmtOnly() {
        return this.useFmtOnly;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        SQLServerException.throwNotSupportedException(this, null);
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        this.checkClosed();
        return new SQLServerBlob(this);
    }

    @Override
    public Clob createClob() throws SQLException {
        this.checkClosed();
        return new SQLServerClob(this);
    }

    @Override
    public NClob createNClob() throws SQLException {
        this.checkClosed();
        return new SQLServerNClob(this);
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        loggerExternal.entering(this.loggingClassName, "createSQLXML");
        SQLServerSQLXML sqlxml = new SQLServerSQLXML(this);
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.exiting(this.loggingClassName, "createSQLXML", sqlxml);
        }
        return sqlxml;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        SQLServerException.throwNotSupportedException(this, null);
        return null;
    }

    String getTrustedServerNameAE() {
        return this.trustedServerNameAE.toUpperCase();
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        loggerExternal.entering(this.loggingClassName, "getClientInfo");
        this.checkClosed();
        Properties p = new Properties();
        loggerExternal.exiting(this.loggingClassName, "getClientInfo", p);
        return p;
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        loggerExternal.entering(this.loggingClassName, "getClientInfo", name);
        this.checkClosed();
        loggerExternal.exiting(this.loggingClassName, "getClientInfo", null);
        return null;
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        loggerExternal.entering(this.loggingClassName, "setClientInfo", properties);
        try {
            this.checkClosed();
        }
        catch (SQLServerException ex) {
            SQLClientInfoException info = new SQLClientInfoException();
            info.initCause(ex);
            throw info;
        }
        if (!properties.isEmpty()) {
            Enumeration<Object> e = properties.keys();
            while (e.hasMoreElements()) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidProperty"));
                Object[] msgArgs = new Object[]{e.nextElement()};
                this.addWarning(form.format(msgArgs));
            }
        }
        loggerExternal.exiting(this.loggingClassName, "setClientInfo");
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.loggingClassName, "setClientInfo", new Object[]{name, value});
        }
        try {
            this.checkClosed();
        }
        catch (SQLServerException ex) {
            SQLClientInfoException info = new SQLClientInfoException();
            info.initCause(ex);
            throw info;
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidProperty"));
        Object[] msgArgs = new Object[]{name};
        this.addWarning(form.format(msgArgs));
        loggerExternal.exiting(this.loggingClassName, "setClientInfo");
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        loggerExternal.entering(this.loggingClassName, "isValid", timeout);
        if (timeout < 0) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidQueryTimeOutValue"));
            Object[] msgArgs = new Object[]{timeout};
            SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), null, true);
        }
        if (this.isSessionUnAvailable()) {
            return false;
        }
        boolean isValid = true;
        try (SQLServerStatement stmt = new SQLServerStatement(this, 1003, 1007, SQLServerStatementColumnEncryptionSetting.USE_CONNECTION_SETTING);){
            if (0 != timeout) {
                stmt.setQueryTimeout(timeout);
            }
            stmt.executeQueryInternal("SELECT 1");
        }
        catch (SQLException e) {
            isValid = false;
            connectionlogger.fine(this.toString() + " Exception checking connection validity: " + e.getMessage());
        }
        loggerExternal.exiting(this.loggingClassName, "isValid", isValid);
        return isValid;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        loggerExternal.entering(this.loggingClassName, "isWrapperFor", iface);
        boolean f = iface.isInstance(this);
        loggerExternal.exiting(this.loggingClassName, "isWrapperFor", f);
        return f;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        T t;
        loggerExternal.entering(this.loggingClassName, "unwrap", iface);
        try {
            t = iface.cast(this);
        }
        catch (ClassCastException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
        loggerExternal.exiting(this.loggingClassName, "unwrap", t);
        return t;
    }

    void beginRequestInternal() throws SQLException {
        loggerExternal.entering(this.loggingClassName, "beginRequest", this);
        this.lock.lock();
        try {
            if (!this.requestStarted) {
                this.originalDatabaseAutoCommitMode = this.databaseAutoCommitMode;
                this.originalTransactionIsolationLevel = this.transactionIsolationLevel;
                this.originalNetworkTimeout = this.getNetworkTimeout();
                this.originalHoldability = this.holdability;
                this.originalSendTimeAsDatetime = this.sendTimeAsDatetime;
                this.originalDatetimeParameterType = this.datetimeParameterType;
                this.originalStatementPoolingCacheSize = this.statementPoolingCacheSize;
                this.originalDisableStatementPooling = this.disableStatementPooling;
                this.originalServerPreparedStatementDiscardThreshold = this.getServerPreparedStatementDiscardThreshold();
                this.originalEnablePrepareOnFirstPreparedStatementCall = this.getEnablePrepareOnFirstPreparedStatementCall();
                this.originalSCatalog = this.sCatalog;
                this.originalUseBulkCopyForBatchInsert = this.getUseBulkCopyForBatchInsert();
                this.originalSqlWarnings = this.sqlWarnings;
                this.openStatements = new LinkedList<ISQLServerStatement>();
                this.originalUseFmtOnly = this.useFmtOnly;
                this.originalDelayLoadingLobs = this.delayLoadingLobs;
                this.requestStarted = true;
            }
        }
        finally {
            this.lock.unlock();
        }
        loggerExternal.exiting(this.loggingClassName, "beginRequest", this);
    }

    void endRequestInternal() throws SQLException {
        loggerExternal.entering(this.loggingClassName, "endRequest", this);
        this.lock.lock();
        try {
            if (this.requestStarted) {
                if (!this.databaseAutoCommitMode) {
                    this.rollback();
                }
                if (this.databaseAutoCommitMode != this.originalDatabaseAutoCommitMode) {
                    this.setAutoCommit(this.originalDatabaseAutoCommitMode);
                }
                if (this.transactionIsolationLevel != this.originalTransactionIsolationLevel) {
                    this.setTransactionIsolation(this.originalTransactionIsolationLevel);
                }
                if (this.getNetworkTimeout() != this.originalNetworkTimeout) {
                    this.setNetworkTimeout(null, this.originalNetworkTimeout);
                }
                if (this.holdability != this.originalHoldability) {
                    this.setHoldability(this.originalHoldability);
                }
                if (this.sendTimeAsDatetime != this.originalSendTimeAsDatetime) {
                    this.setSendTimeAsDatetime(this.originalSendTimeAsDatetime);
                }
                if (this.datetimeParameterType != this.originalDatetimeParameterType) {
                    this.setDatetimeParameterType(this.originalDatetimeParameterType.toString());
                }
                if (this.useFmtOnly != this.originalUseFmtOnly) {
                    this.setUseFmtOnly(this.originalUseFmtOnly);
                }
                if (this.statementPoolingCacheSize != this.originalStatementPoolingCacheSize) {
                    this.setStatementPoolingCacheSize(this.originalStatementPoolingCacheSize);
                }
                if (this.disableStatementPooling != this.originalDisableStatementPooling) {
                    this.setDisableStatementPooling(this.originalDisableStatementPooling);
                }
                if (this.getServerPreparedStatementDiscardThreshold() != this.originalServerPreparedStatementDiscardThreshold) {
                    this.setServerPreparedStatementDiscardThreshold(this.originalServerPreparedStatementDiscardThreshold);
                }
                if (this.getEnablePrepareOnFirstPreparedStatementCall() != this.originalEnablePrepareOnFirstPreparedStatementCall.booleanValue()) {
                    this.setEnablePrepareOnFirstPreparedStatementCall(this.originalEnablePrepareOnFirstPreparedStatementCall);
                }
                if (!this.sCatalog.equals(this.originalSCatalog)) {
                    this.setCatalog(this.originalSCatalog);
                }
                if (this.getUseBulkCopyForBatchInsert() != this.originalUseBulkCopyForBatchInsert) {
                    this.setUseBulkCopyForBatchInsert(this.originalUseBulkCopyForBatchInsert);
                }
                if (this.delayLoadingLobs != this.originalDelayLoadingLobs) {
                    this.setDelayLoadingLobs(this.originalDelayLoadingLobs);
                }
                this.sqlWarnings = this.originalSqlWarnings;
                if (null != this.openStatements) {
                    while (!this.openStatements.isEmpty()) {
                        Statement st = this.openStatements.get(0);
                        if (st == null) continue;
                        st.close();
                    }
                    this.openStatements.clear();
                }
                this.requestStarted = false;
            }
        }
        finally {
            this.lock.unlock();
        }
        loggerExternal.exiting(this.loggingClassName, "endRequest", this);
    }

    String replaceParameterMarkers(String sqlSrc, int[] paramPositions, Parameter[] params, boolean isReturnValueSyntax) {
        int MAX_PARAM_NAME_LEN = 6;
        char[] sqlDst = new char[sqlSrc.length() + params.length * (6 + OUT.length) + params.length * 2];
        int dstBegin = 0;
        int srcBegin = 0;
        int nParam = 0;
        int paramIndex = 0;
        while (true) {
            int srcEnd = paramIndex >= paramPositions.length ? sqlSrc.length() : paramPositions[paramIndex];
            sqlSrc.getChars(srcBegin, srcEnd, sqlDst, dstBegin);
            dstBegin += srcEnd - srcBegin;
            if (sqlSrc.length() == srcEnd) break;
            dstBegin += SQLServerConnection.makeParamName(nParam++, sqlDst, dstBegin, true);
            int n = srcBegin = srcEnd + 1 <= sqlSrc.length() - 1 && sqlSrc.charAt(srcEnd + 1) == ' ' ? srcEnd + 2 : srcEnd + 1;
            if (!params[paramIndex++].isOutput() || isReturnValueSyntax && paramIndex <= 1) continue;
            System.arraycopy(OUT, 0, sqlDst, dstBegin, OUT.length);
            dstBegin += OUT.length;
        }
        return new String(sqlDst, 0, dstBegin);
    }

    static int makeParamName(int nParam, char[] name, int offset, boolean isPreparedSQL) {
        SQLServerConnection.buildParamInitial(name, offset, isPreparedSQL);
        if (nParam < 10) {
            return SQLServerConnection.buildParamLt10(nParam, name, offset, isPreparedSQL);
        }
        if (nParam < 100) {
            return SQLServerConnection.buildParamLt100(nParam, name, offset, isPreparedSQL);
        }
        return SQLServerConnection.buildParamMt100(nParam, name, offset, isPreparedSQL);
    }

    private static void buildParamInitial(char[] name, int offset, boolean isPreparedSQL) {
        int preparedSQLOffset = 0;
        if (isPreparedSQL) {
            name[offset + 0] = 32;
            ++preparedSQLOffset;
        }
        name[offset + preparedSQLOffset + 0] = 64;
        name[offset + preparedSQLOffset + 1] = 80;
    }

    private static int buildParamLt10(int nParam, char[] name, int offset, boolean isPreparedSQL) {
        int preparedSQLOffset = 0;
        if (isPreparedSQL) {
            ++preparedSQLOffset;
        }
        name[offset + preparedSQLOffset + 2] = (char)(48 + nParam);
        if (isPreparedSQL) {
            name[offset + 4] = 32;
            return 5;
        }
        return 3;
    }

    private static int buildParamLt100(int nParam, char[] name, int offset, boolean isPreparedSQL) {
        int nBase = 2;
        int preparedSQLOffset = 0;
        if (isPreparedSQL) {
            preparedSQLOffset = 1;
        }
        while (true) {
            if (nParam < nBase * 10) {
                name[offset + preparedSQLOffset + 2] = (char)(48 + (nBase - 1));
                name[offset + preparedSQLOffset + 3] = (char)(48 + (nParam - (nBase - 1) * 10));
                if (isPreparedSQL) {
                    name[offset + 5] = 32;
                    ++preparedSQLOffset;
                }
                return 4 + preparedSQLOffset;
            }
            ++nBase;
        }
    }

    private static int buildParamMt100(int nParam, char[] name, int offset, boolean isPreparedSQL) {
        int preparedSQLOffset = 0;
        Object sParam = Integer.toString(nParam);
        if (isPreparedSQL) {
            ++preparedSQLOffset;
            sParam = nParam + " ";
        }
        ((String)sParam).getChars(0, ((String)sParam).length(), name, offset + preparedSQLOffset + 2);
        return 2 + ((String)sParam).length() + preparedSQLOffset;
    }

    void notifyPooledConnection(SQLServerException e) {
        this.lock.lock();
        try {
            if (null != this.pooledConnectionParent) {
                this.pooledConnectionParent.notifyEvent(e);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    void detachFromPool() {
        this.lock.lock();
        try {
            this.pooledConnectionParent = null;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    String getInstancePort(String server, String instanceName) throws SQLServerException {
        MessageFormat form;
        String browserResult = null;
        DatagramSocket datagramSocket = null;
        Object lastErrorMessage = null;
        try {
            block24: {
                lastErrorMessage = "Failed to determine instance for the : " + server + " instance:" + instanceName;
                try {
                    datagramSocket = new DatagramSocket();
                    datagramSocket.setSoTimeout(1000);
                }
                catch (SocketException socketException) {
                    lastErrorMessage = "Unable to create local datagram socket";
                    throw socketException;
                }
                assert (null != datagramSocket);
                try {
                    if (this.multiSubnetFailover) {
                        InetAddress[] inetAddrs = InetAddress.getAllByName(server);
                        assert (null != inetAddrs);
                        for (InetAddress inetAddr : inetAddrs) {
                            try {
                                byte[] sendBuffer = (" " + instanceName).getBytes();
                                sendBuffer[0] = 4;
                                DatagramPacket udpRequest = new DatagramPacket(sendBuffer, sendBuffer.length, inetAddr, 1434);
                                datagramSocket.send(udpRequest);
                            }
                            catch (IOException ioException) {
                                lastErrorMessage = "Error sending SQL Server Browser Service UDP request to address: " + inetAddr + ", port: 1434";
                                throw ioException;
                            }
                        }
                        break block24;
                    }
                    InetAddress inetAddr = InetAddress.getByName(server);
                    assert (null != inetAddr);
                    try {
                        byte[] sendBuffer = (" " + instanceName).getBytes();
                        sendBuffer[0] = 4;
                        DatagramPacket udpRequest = new DatagramPacket(sendBuffer, sendBuffer.length, inetAddr, 1434);
                        datagramSocket.send(udpRequest);
                    }
                    catch (IOException ioException) {
                        lastErrorMessage = "Error sending SQL Server Browser Service UDP request to address: " + inetAddr + ", port: 1434";
                        throw ioException;
                    }
                }
                catch (UnknownHostException unknownHostException) {
                    lastErrorMessage = "Unable to determine IP address of host: " + server;
                    throw unknownHostException;
                }
            }
            try {
                byte[] receiveBuffer = new byte[4096];
                DatagramPacket udpResponse = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                datagramSocket.receive(udpResponse);
                browserResult = new String(receiveBuffer, 3, receiveBuffer.length - 3);
                if (connectionlogger.isLoggable(Level.FINER)) {
                    connectionlogger.fine(this.toString() + " Received SSRP UDP response from IP address: " + udpResponse.getAddress().getHostAddress());
                }
            }
            catch (IOException ioException) {
                lastErrorMessage = "Error receiving SQL Server Browser Service UDP response from server: " + server;
                throw ioException;
            }
        }
        catch (IOException ioException) {
            form = new MessageFormat(SQLServerException.getErrString("R_sqlBrowserFailed"));
            Object[] msgArgs = new Object[]{server, instanceName, ioException.toString()};
            connectionlogger.log(Level.FINE, this.toString() + " " + (String)lastErrorMessage, ioException);
            SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), "08001", false);
        }
        finally {
            if (null != datagramSocket) {
                datagramSocket.close();
            }
        }
        assert (null != browserResult);
        int p = browserResult.indexOf("tcp;");
        if (-1 == p) {
            form = new MessageFormat(SQLServerException.getErrString("R_notConfiguredToListentcpip"));
            Object[] msgArgs = new Object[]{instanceName};
            SQLServerException.makeFromDriverError(this, this, form.format(msgArgs), "08001", false);
        }
        int p1 = p + 4;
        int p2 = browserResult.indexOf(59, p1);
        return browserResult.substring(p1, p2);
    }

    int getNextSavepointId() {
        ++this.nNextSavePointId;
        return this.nNextSavePointId;
    }

    void doSecurityCheck() {
        assert (null != this.currentConnectPlaceHolder);
        this.currentConnectPlaceHolder.doSecurityCheck();
    }

    public static void setColumnEncryptionKeyCacheTtl(int columnEncryptionKeyCacheTTL, TimeUnit unit) throws SQLServerException {
        sLock.lock();
        try {
            if (columnEncryptionKeyCacheTTL < 0 || unit.equals((Object)TimeUnit.MILLISECONDS) || unit.equals((Object)TimeUnit.MICROSECONDS) || unit.equals((Object)TimeUnit.NANOSECONDS)) {
                throw new SQLServerException(null, SQLServerException.getErrString("R_invalidCEKCacheTtl"), null, 0, false);
            }
            columnEncryptionKeyCacheTtl = TimeUnit.SECONDS.convert(columnEncryptionKeyCacheTTL, unit);
        }
        finally {
            sLock.unlock();
        }
    }

    static long getColumnEncryptionKeyCacheTtl() {
        sLock.lock();
        try {
            long l = columnEncryptionKeyCacheTtl;
            return l;
        }
        finally {
            sLock.unlock();
        }
    }

    final void enqueueUnprepareStatementHandle(PreparedStatementHandle statementHandle) {
        if (null == statementHandle) {
            return;
        }
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.finer(this + ": Adding PreparedHandle to queue for un-prepare:" + statementHandle.getHandle());
        }
        this.discardedPreparedStatementHandles.add(statementHandle);
        this.discardedPreparedStatementHandleCount.incrementAndGet();
    }

    @Override
    public int getDiscardedServerPreparedStatementCount() {
        return this.discardedPreparedStatementHandleCount.get();
    }

    @Override
    public void closeUnreferencedPreparedStatementHandles() {
        this.unprepareUnreferencedPreparedStatementHandles(true);
    }

    private final void cleanupPreparedStatementDiscardActions() {
        this.discardedPreparedStatementHandles.clear();
        this.discardedPreparedStatementHandleCount.set(0);
    }

    @Override
    public boolean getEnablePrepareOnFirstPreparedStatementCall() {
        if (null == this.enablePrepareOnFirstPreparedStatementCall) {
            return false;
        }
        return this.enablePrepareOnFirstPreparedStatementCall;
    }

    @Override
    public void setEnablePrepareOnFirstPreparedStatementCall(boolean value) {
        this.enablePrepareOnFirstPreparedStatementCall = value;
    }

    @Override
    public String getPrepareMethod() {
        if (null == this.prepareMethod) {
            return SQLServerDriverStringProperty.PREPARE_METHOD.getDefaultValue();
        }
        return this.prepareMethod;
    }

    @Override
    public void setPrepareMethod(String prepareMethod) {
        this.prepareMethod = prepareMethod;
    }

    @Override
    public int getServerPreparedStatementDiscardThreshold() {
        if (0 > this.serverPreparedStatementDiscardThreshold) {
            return 10;
        }
        return this.serverPreparedStatementDiscardThreshold;
    }

    @Override
    public void setServerPreparedStatementDiscardThreshold(int value) {
        this.serverPreparedStatementDiscardThreshold = Math.max(0, value);
    }

    final boolean isPreparedStatementUnprepareBatchingEnabled() {
        return 1 < this.getServerPreparedStatementDiscardThreshold();
    }

    @Override
    public String getAccessTokenCallbackClass() {
        if (null == this.accessTokenCallbackClass) {
            return SQLServerDriverStringProperty.ACCESS_TOKEN_CALLBACK_CLASS.getDefaultValue();
        }
        return this.accessTokenCallbackClass;
    }

    @Override
    public void setAccessTokenCallbackClass(String accessTokenCallbackClass) {
        this.accessTokenCallbackClass = accessTokenCallbackClass;
    }

    final void unprepareUnreferencedPreparedStatementHandles(boolean force) {
        if (this.isSessionUnAvailable()) {
            return;
        }
        int threshold = this.getServerPreparedStatementDiscardThreshold();
        if (force || threshold < this.getDiscardedServerPreparedStatementCount()) {
            int handlesRemoved;
            block13: {
                StringBuilder sql = new StringBuilder(threshold * 32);
                handlesRemoved = 0;
                PreparedStatementHandle statementHandle = null;
                while (null != (statementHandle = this.discardedPreparedStatementHandles.poll())) {
                    ++handlesRemoved;
                    sql.append(statementHandle.isDirectSql() ? "EXEC sp_unprepare " : "EXEC sp_cursorunprepare ").append(statementHandle.getHandle()).append(';');
                }
                try {
                    try (SQLServerStatement stmt = (SQLServerStatement)this.createStatement();){
                        stmt.isInternalEncryptionQuery = true;
                        stmt.execute(sql.toString());
                    }
                    if (loggerExternal.isLoggable(Level.FINER)) {
                        loggerExternal.finer(this + ": Finished un-preparing handle count:" + handlesRemoved);
                    }
                }
                catch (SQLException e) {
                    if (!loggerExternal.isLoggable(Level.FINER)) break block13;
                    loggerExternal.log(Level.FINER, this + ": Error batch-closing at least one prepared handle", e);
                }
            }
            this.discardedPreparedStatementHandleCount.addAndGet(-handlesRemoved);
        }
    }

    @Override
    public boolean getDisableStatementPooling() {
        return this.disableStatementPooling;
    }

    @Override
    public void setDisableStatementPooling(boolean value) {
        this.disableStatementPooling = value;
        if (!value && 0 < this.getStatementPoolingCacheSize()) {
            this.prepareCache();
        }
    }

    @Override
    public int getStatementPoolingCacheSize() {
        return this.statementPoolingCacheSize;
    }

    @Override
    public int getStatementHandleCacheEntryCount() {
        if (!this.isStatementPoolingEnabled()) {
            return 0;
        }
        return this.preparedStatementHandleCache.size();
    }

    @Override
    public boolean isStatementPoolingEnabled() {
        return null != this.preparedStatementHandleCache && 0 < this.getStatementPoolingCacheSize() && !this.getDisableStatementPooling();
    }

    @Override
    public void setStatementPoolingCacheSize(int value) {
        this.statementPoolingCacheSize = value = Math.max(0, value);
        if (!this.disableStatementPooling && value > 0) {
            this.prepareCache();
        }
        if (null != this.preparedStatementHandleCache) {
            this.preparedStatementHandleCache.setCapacity(value);
        }
        if (null != this.parameterMetadataCache) {
            this.parameterMetadataCache.setCapacity(value);
        }
    }

    @Override
    @Deprecated(since="12.1.0", forRemoval=true)
    public int getMsiTokenCacheTtl() {
        return 0;
    }

    @Override
    @Deprecated(since="12.1.0", forRemoval=true)
    public void setMsiTokenCacheTtl(int timeToLive) {
    }

    private void prepareCache() {
        this.preparedStatementHandleCache = new ConcurrentLinkedHashMap.Builder().maximumWeightedCapacity(this.getStatementPoolingCacheSize()).listener(new PreparedStatementCacheEvictionListener()).build();
        this.parameterMetadataCache = new ConcurrentLinkedHashMap.Builder().maximumWeightedCapacity(this.getStatementPoolingCacheSize()).build();
    }

    final SQLServerParameterMetaData getCachedParameterMetadata(CityHash128Key key) {
        if (!this.isStatementPoolingEnabled()) {
            return null;
        }
        return this.parameterMetadataCache.get(key);
    }

    final void registerCachedParameterMetadata(CityHash128Key key, SQLServerParameterMetaData pmd) {
        if (!this.isStatementPoolingEnabled() || null == pmd) {
            return;
        }
        this.parameterMetadataCache.put(key, pmd);
    }

    final PreparedStatementHandle getCachedPreparedStatementHandle(CityHash128Key key) {
        if (!this.isStatementPoolingEnabled()) {
            return null;
        }
        return this.preparedStatementHandleCache.get(key);
    }

    final PreparedStatementHandle registerCachedPreparedStatementHandle(CityHash128Key key, int handle, boolean isDirectSql) {
        if (!this.isStatementPoolingEnabled() || null == key) {
            return null;
        }
        PreparedStatementHandle cacheItem = new PreparedStatementHandle(key, handle, isDirectSql, false);
        this.preparedStatementHandleCache.putIfAbsent(key, cacheItem);
        return cacheItem;
    }

    final void returnCachedPreparedStatementHandle(PreparedStatementHandle handle) {
        handle.removeReference();
        if (handle.isEvictedFromCache() && handle.tryDiscardHandle()) {
            this.enqueueUnprepareStatementHandle(handle);
        }
    }

    final void evictCachedPreparedStatementHandle(PreparedStatementHandle handle) {
        if (null == handle || null == handle.getKey()) {
            return;
        }
        this.preparedStatementHandleCache.remove(handle.getKey());
    }

    boolean isAzure() {
        if (null == this.isAzure) {
            try (Statement stmt = this.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT CAST(SERVERPROPERTY('EngineEdition') as INT)");){
                rs.next();
                int engineEdition = rs.getInt(1);
                this.isAzure = engineEdition == 5 || engineEdition == 6 || engineEdition == 8 || engineEdition == 9 || engineEdition == 11;
                this.isAzureDW = engineEdition == 6 || engineEdition == 11;
                this.isAzureMI = engineEdition == 8;
            }
            catch (SQLException e) {
                if (loggerExternal.isLoggable(Level.FINER)) {
                    loggerExternal.log(Level.FINER, this + ": Error retrieving server type", e);
                }
                this.isAzure = false;
                this.isAzureDW = false;
                this.isAzureMI = false;
            }
            return this.isAzure;
        }
        return this.isAzure;
    }

    boolean isAzureDW() {
        this.isAzure();
        return this.isAzureDW;
    }

    boolean isAzureMI() {
        this.isAzure();
        return this.isAzureMI;
    }

    boolean supportsTransactions() throws SQLServerException {
        if (this.supportsTransactions != null) {
            return this.supportsTransactions;
        }
        this.supportsTransactions = true;
        try {
            this.connectionCommand("SELECT @@TRANCOUNT", "SQLServerConnection.supportsTransactions");
        }
        catch (SQLServerException e) {
            if (e.getMessage().equals(SQLServerException.getErrString("R_transactionsNotSupported"))) {
                this.supportsTransactions = false;
                return false;
            }
            throw e;
        }
        return this.supportsTransactions;
    }

    final void addOpenStatement(ISQLServerStatement st) {
        this.lock.lock();
        try {
            if (null != this.openStatements) {
                this.openStatements.add(st);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    final void removeOpenStatement(ISQLServerStatement st) {
        this.lock.lock();
        try {
            if (null != this.openStatements) {
                this.openStatements.remove(st);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    boolean isAEv2() {
        return this.aeVersion >= 2;
    }

    boolean doesServerSupportEnclaveRetry() {
        return this.serverSupportsEnclaveRetry;
    }

    boolean setLockTimeout() {
        this.nLockTimeout = SQLServerDriverIntProperty.LOCK_TIMEOUT.getDefaultValue();
        String lockTimeoutKey = SQLServerDriverIntProperty.LOCK_TIMEOUT.toString();
        if (null != this.activeConnectionProperties && null != this.activeConnectionProperties.getProperty(lockTimeoutKey) && this.activeConnectionProperties.getProperty(lockTimeoutKey).length() > 0) {
            int newLockTimeout = Integer.parseInt(this.activeConnectionProperties.getProperty(lockTimeoutKey));
            if (newLockTimeout >= this.nLockTimeout) {
                this.nLockTimeout = newLockTimeout;
                return true;
            }
            return false;
        }
        return this.nLockTimeout == SQLServerDriverIntProperty.LOCK_TIMEOUT.getDefaultValue();
    }

    ArrayList<byte[]> initEnclaveParameters(SQLServerStatement statement, String userSql, String preparedTypeDefinitions, Parameter[] params, ArrayList<String> parameterNames) throws SQLServerException {
        if (!this.enclaveEstablished()) {
            this.enclaveProvider.getAttestationParameters(this.enclaveAttestationUrl);
        }
        return this.enclaveProvider.createEnclaveSession(this, statement, userSql, preparedTypeDefinitions, params, parameterNames);
    }

    boolean enclaveEstablished() {
        return null != this.enclaveProvider.getEnclaveSession();
    }

    byte[] generateEnclavePackage(String userSQL, ArrayList<byte[]> enclaveCEKs) throws SQLServerException {
        return !enclaveCEKs.isEmpty() ? this.enclaveProvider.getEnclavePackage(userSQL, enclaveCEKs) : null;
    }

    String getServerName() {
        return this.trustedServerNameAE;
    }

    @Override
    public void setIPAddressPreference(String iPAddressPreference) {
        this.activeConnectionProperties.setProperty(SQLServerDriverStringProperty.IPADDRESS_PREFERENCE.toString(), iPAddressPreference);
    }

    @Override
    public String getIPAddressPreference() {
        return this.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.IPADDRESS_PREFERENCE.toString());
    }

    static {
        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("windows")) {
            SQLServerColumnEncryptionCertificateStoreProvider provider = new SQLServerColumnEncryptionCertificateStoreProvider();
            globalSystemColumnEncryptionKeyStoreProviders.put(provider.getName(), provider);
        }
        globalCustomColumnEncryptionKeyStoreProviders = null;
        columnEncryptionTrustedMasterKeyPaths = new HashMap<String, List<String>>();
        baseConnectionID = new AtomicInteger(0);
        connectionlogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerConnection");
        loggerExternal = Logger.getLogger("com.microsoft.sqlserver.jdbc.Connection");
        loggingClassNameBase = "com.microsoft.sqlserver.jdbc.SQLServerConnection";
        DEFAULTPORT = SQLServerDriverIntProperty.PORT_NUMBER.getDefaultValue();
        OUT = new char[]{' ', 'O', 'U', 'T'};
        columnEncryptionKeyCacheTtl = TimeUnit.SECONDS.convert(2L, TimeUnit.HOURS);
    }

    final class PreparedStatementCacheEvictionListener
    implements EvictionListener<CityHash128Key, PreparedStatementHandle> {
        PreparedStatementCacheEvictionListener() {
        }

        @Override
        public void onEviction(CityHash128Key key, PreparedStatementHandle handle) {
            if (null != handle) {
                handle.setIsEvictedFromCache(true);
                if (handle.tryDiscardHandle()) {
                    SQLServerConnection.this.enqueueUnprepareStatementHandle(handle);
                }
            }
        }
    }

    final class FedAuthTokenCommand
    extends UninterruptableTDSCommand {
        private static final long serialVersionUID = 1L;
        transient TDSTokenHandler tdsTokenHandler;
        SqlAuthenticationToken sqlFedAuthToken;

        FedAuthTokenCommand(SqlAuthenticationToken sqlFedAuthToken, TDSTokenHandler tdsTokenHandler) {
            super("FedAuth");
            this.tdsTokenHandler = null;
            this.sqlFedAuthToken = null;
            this.tdsTokenHandler = tdsTokenHandler;
            this.sqlFedAuthToken = sqlFedAuthToken;
        }

        @Override
        final boolean doExecute() throws SQLServerException {
            this.sendFedAuthToken(this, this.sqlFedAuthToken, this.tdsTokenHandler);
            return true;
        }

        private void sendFedAuthToken(FedAuthTokenCommand fedAuthCommand, SqlAuthenticationToken fedAuthToken, TDSTokenHandler tdsTokenHandler) throws SQLServerException {
            assert (null != fedAuthToken);
            assert (null != fedAuthToken.getAccessToken());
            if (connectionlogger.isLoggable(Level.FINER)) {
                connectionlogger.fine(super.toString() + " Sending federated authentication token.");
            }
            TDSWriter tdsWriter = fedAuthCommand.startRequest((byte)8);
            byte[] accessToken = fedAuthToken.getAccessToken().getBytes(StandardCharsets.UTF_16LE);
            tdsWriter.writeInt(accessToken.length + 4);
            tdsWriter.writeInt(accessToken.length);
            tdsWriter.writeBytes(accessToken, 0, accessToken.length);
            TDSReader tdsReader = fedAuthCommand.startResponse();
            SQLServerConnection.this.federatedAuthenticationRequested = true;
            TDSParser.parse(tdsReader, tdsTokenHandler);
        }
    }

    private final class LogonCommand
    extends UninterruptableTDSCommand {
        private static final long serialVersionUID = 1L;

        LogonCommand() {
            super("logon");
        }

        @Override
        final boolean doExecute() throws SQLServerException {
            SQLServerConnection.this.logon(this);
            return true;
        }
    }

    private static enum State {
        INITIALIZED,
        CONNECTED,
        OPENED,
        CLOSED;

    }

    class ActiveDirectoryAuthentication {
        static final String JDBC_FEDAUTH_CLIENT_ID = "7f98cb04-cd1e-40df-9140-3bf7e2cea4db";
        static final String AZURE_REST_MSI_URL = "http://169.254.169.254/metadata/identity/oauth2/token?api-version=2018-02-01";
        static final String ACCESS_TOKEN_IDENTIFIER = "\"access_token\":\"";
        static final String ACCESS_TOKEN_EXPIRES_IN_IDENTIFIER = "\"expires_in\":\"";
        static final String ACCESS_TOKEN_EXPIRES_ON_IDENTIFIER = "\"expires_on\":\"";
        static final String ACCESS_TOKEN_EXPIRES_ON_DATE_FORMAT = "M/d/yyyy h:mm:ss a X";
        static final int GET_ACCESS_TOKEN_SUCCESS = 0;
        static final int GET_ACCESS_TOKEN_INVALID_GRANT = 1;
        static final int GET_ACCESS_TOKEN_TRANSIENT_ERROR = 2;
        static final int GET_ACCESS_TOKEN_OTHER_ERROR = 3;

        private ActiveDirectoryAuthentication() {
            throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
        }
    }

    class SqlFedAuthInfo {
        String spn;
        String stsurl;

        SqlFedAuthInfo() {
        }

        public String toString() {
            return "STSURL: " + this.stsurl + ", SPN: " + this.spn;
        }
    }

    class FederatedAuthenticationFeatureExtensionData
    implements Serializable {
        private static final long serialVersionUID = -6709861741957202475L;
        boolean fedAuthRequiredPreLoginResponse;
        int libraryType = -1;
        byte[] accessToken = null;
        SqlAuthentication authentication = null;

        FederatedAuthenticationFeatureExtensionData(int libraryType, String authenticationString, boolean fedAuthRequiredPreLoginResponse) throws SQLServerException {
            this.libraryType = libraryType;
            this.fedAuthRequiredPreLoginResponse = fedAuthRequiredPreLoginResponse;
            switch (authenticationString.toUpperCase(Locale.ENGLISH)) {
                case "ACTIVEDIRECTORYPASSWORD": {
                    this.authentication = SqlAuthentication.ACTIVE_DIRECTORY_PASSWORD;
                    break;
                }
                case "ACTIVEDIRECTORYINTEGRATED": {
                    this.authentication = SqlAuthentication.ACTIVE_DIRECTORY_INTEGRATED;
                    break;
                }
                case "ACTIVEDIRECTORYMANAGEDIDENTITY": {
                    this.authentication = SqlAuthentication.ACTIVE_DIRECTORY_MANAGED_IDENTITY;
                    break;
                }
                case "ACTIVEDIRECTORYDEFAULT": {
                    this.authentication = SqlAuthentication.ACTIVE_DIRECTORY_DEFAULT;
                    break;
                }
                case "ACTIVEDIRECTORYSERVICEPRINCIPAL": {
                    this.authentication = SqlAuthentication.ACTIVE_DIRECTORY_SERVICE_PRINCIPAL;
                    break;
                }
                case "ACTIVEDIRECTORYSERVICEPRINCIPALCERTIFICATE": {
                    this.authentication = SqlAuthentication.ACTIVE_DIRECTORY_SERVICE_PRINCIPAL_CERTIFICATE;
                    break;
                }
                case "ACTIVEDIRECTORYINTERACTIVE": {
                    this.authentication = SqlAuthentication.ACTIVE_DIRECTORY_INTERACTIVE;
                    break;
                }
                default: {
                    if (null != SQLServerConnection.this.accessTokenCallback || SQLServerConnection.this.hasAccessTokenCallbackClass) {
                        this.authentication = SqlAuthentication.NOT_SPECIFIED;
                        break;
                    }
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidConnectionSetting"));
                    Object[] msgArgs = new Object[]{"authentication", authenticationString};
                    throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
                }
            }
        }

        FederatedAuthenticationFeatureExtensionData(int libraryType, boolean fedAuthRequiredPreLoginResponse, byte[] accessToken) {
            this.libraryType = libraryType;
            this.fedAuthRequiredPreLoginResponse = fedAuthRequiredPreLoginResponse;
            this.accessToken = accessToken;
        }
    }

    class IdleNetworkTracker {
        private Instant lastNetworkActivity = Instant.now();
        private int maxIdleMillis = 15000;

        IdleNetworkTracker() {
        }

        private boolean isIdle() {
            return Instant.now().minusMillis(this.maxIdleMillis).isAfter(this.lastNetworkActivity);
        }

        protected void markNetworkActivity() {
            this.lastNetworkActivity = Instant.now();
        }

        protected void setMaxIdleMillis(int millis) {
            this.maxIdleMillis = millis;
        }
    }

    class PreparedStatementHandle {
        private int handle = 0;
        private final AtomicInteger handleRefCount = new AtomicInteger();
        private boolean isDirectSql;
        private volatile boolean evictedFromCache;
        private volatile boolean explicitlyDiscarded;
        private CityHash128Key key;

        PreparedStatementHandle(CityHash128Key key, int handle, boolean isDirectSql, boolean isEvictedFromCache) {
            this.key = key;
            this.handle = handle;
            this.isDirectSql = isDirectSql;
            this.setIsEvictedFromCache(isEvictedFromCache);
            this.handleRefCount.set(1);
        }

        private boolean isEvictedFromCache() {
            return this.evictedFromCache;
        }

        private void setIsEvictedFromCache(boolean isEvictedFromCache) {
            this.evictedFromCache = isEvictedFromCache;
        }

        void setIsExplicitlyDiscarded() {
            this.explicitlyDiscarded = true;
            SQLServerConnection.this.evictCachedPreparedStatementHandle(this);
        }

        private boolean isExplicitlyDiscarded() {
            return this.explicitlyDiscarded;
        }

        int getHandle() {
            return this.handle;
        }

        CityHash128Key getKey() {
            return this.key;
        }

        boolean isDirectSql() {
            return this.isDirectSql;
        }

        private boolean tryDiscardHandle() {
            return this.handleRefCount.compareAndSet(0, -999);
        }

        private boolean isDiscarded() {
            return 0 > this.handleRefCount.intValue();
        }

        boolean tryAddReference() {
            return this.isDiscarded() || this.isExplicitlyDiscarded() ? false : this.handleRefCount.incrementAndGet() > 0;
        }

        void removeReference() {
            this.handleRefCount.decrementAndGet();
        }
    }

    static class CityHash128Key
    implements Serializable {
        private static final long serialVersionUID = 166788428640603097L;
        String unhashedString;
        private long[] segments;
        private int hashCode;

        CityHash128Key(String sql, String parametersDefinition) {
            this(sql + parametersDefinition);
        }

        CityHash128Key(String s) {
            this.unhashedString = s;
            byte[] bytes = new byte[s.length()];
            s.getBytes(0, s.length(), bytes, 0);
            this.segments = CityHash.cityHash128(bytes, 0, bytes.length);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof CityHash128Key)) {
                return false;
            }
            return Arrays.equals(this.segments, ((CityHash128Key)obj).segments) && this.unhashedString.equals(((CityHash128Key)obj).unhashedString);
        }

        public int hashCode() {
            if (0 == this.hashCode) {
                this.hashCode = Arrays.hashCode(this.segments);
            }
            return this.hashCode;
        }
    }
}

