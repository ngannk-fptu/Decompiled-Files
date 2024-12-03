/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.HomeLocator
 *  com.atlassian.config.bootstrap.BootstrapException
 *  com.atlassian.config.bootstrap.DefaultAtlassianBootstrapManager
 *  com.atlassian.config.db.DatabaseDetails
 *  com.atlassian.config.db.HibernateConfigurator
 *  com.atlassian.config.setup.SetupPersister
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  com.atlassian.confluence.impl.hibernate.dialect.H2V4200Dialect
 *  com.atlassian.confluence.impl.hibernate.dialect.MySQLDialect
 *  com.atlassian.confluence.impl.hibernate.dialect.OracleDialect
 *  com.atlassian.confluence.impl.hibernate.dialect.PostgreSQLDialect
 *  com.atlassian.confluence.impl.hibernate.dialect.SQLServerDialect
 *  com.atlassian.confluence.upgrade.BuildNumber
 *  com.atlassian.security.random.DefaultSecureTokenGenerator
 *  com.atlassian.security.serialblocklist.BlocklistConfigurator
 *  com.atlassian.security.serialfilter.DeserializationFilterConfigurator
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.dialect.Dialect
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.HomeLocator;
import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.config.bootstrap.DefaultAtlassianBootstrapManager;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.config.db.HibernateConfigurator;
import com.atlassian.config.setup.SetupPersister;
import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.cluster.ClusterException;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.impl.health.HealthCheckRunner;
import com.atlassian.confluence.impl.hibernate.dialect.H2V4200Dialect;
import com.atlassian.confluence.impl.hibernate.dialect.MySQLDialect;
import com.atlassian.confluence.impl.hibernate.dialect.OracleDialect;
import com.atlassian.confluence.impl.hibernate.dialect.PostgreSQLDialect;
import com.atlassian.confluence.impl.hibernate.dialect.SQLServerDialect;
import com.atlassian.confluence.impl.setup.BootstrapDatabaseAccessor;
import com.atlassian.confluence.impl.setup.DefaultBootstrapDatabaseAccessor;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import com.atlassian.confluence.setup.BootstrapManagerInternal;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.setup.BuildNumberChecker;
import com.atlassian.confluence.setup.ConfluenceDatabaseDetails;
import com.atlassian.confluence.setup.ConfluenceDatabaseDetailsBuilder;
import com.atlassian.confluence.setup.SharedConfigurationMap;
import com.atlassian.confluence.setup.dbcheck.MySQLChecker;
import com.atlassian.confluence.upgrade.BuildNumber;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.security.random.DefaultSecureTokenGenerator;
import com.atlassian.security.serialblocklist.BlocklistConfigurator;
import com.atlassian.security.serialfilter.DeserializationFilterConfigurator;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.dialect.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBootstrapManager
extends DefaultAtlassianBootstrapManager
implements BootstrapManagerInternal {
    public static final String CONFIG_DIR_NAME = "config";
    private static final Logger log = LoggerFactory.getLogger(DefaultBootstrapManager.class);
    @VisibleForTesting
    static final AtomicBoolean isDeserializationFilterSet = new AtomicBoolean();
    public static final String LOCK_FILE_NAME = "lock";
    @Deprecated
    public static final String SYNCHRONY_BTF = "synchrony.btf";
    public static final String SYNCHRONY_PROXY_ENABLED = "synchrony.proxy.enabled";
    public static final String SYNCHRONY_ENCRYPTION_DISABLED = "synchrony.encryption.disabled";
    public static final String SYNCHRONY_BTF_OFF = "synchrony.btf.off";
    public static final String CONFLUENCE_DATABASE_CHOICE = "confluence.database.choice";
    @VisibleForTesting
    static final String FINALIZED_BUILD_NUMBER_CONFIG_KEY = "finalizedBuildNumber";
    private static final Function<String, Class<? extends Dialect>> databaseDialectConversions = dialect -> {
        switch (dialect) {
            case "net.sf.hibernate.dialect.H2Dialect": {
                return H2V4200Dialect.class;
            }
            case "net.sf.hibernate.dialect.PostgreSQLDialect": {
                return PostgreSQLDialect.class;
            }
            case "com.atlassian.hibernate.dialect.MySQLDialect": {
                return MySQLDialect.class;
            }
            case "net.sf.hibernate.dialect.SQLServerIntlDialect": 
            case "net.sf.hibernate.dialect.SQLServerDialect": 
            case "com.atlassian.confluence.core.persistence.hibernate.SQLServerVarBinaryCapableDiale;ct": {
                return SQLServerDialect.class;
            }
            case "net.sf.hibernate.dialect.Oracle9Dialect": 
            case "net.sf.hibernate.dialect.OracleIntlDialect": {
                return OracleDialect.class;
            }
        }
        return null;
    };
    private static final String JTDS_DRIVER_UPGRADE_DOC_LINK = "https://confluence.atlassian.com/x/4rX-Nw";
    private static final String MSSQL_DRIVER_UPGRADE_DOC_LINK = "TODO";
    static final String JTDS_DRIVER = "net.sourceforge.jtds.jdbc.Driver";
    static final String MSSQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    static final String MYSQL_PROTOCOL = "jdbc:mysql";
    public static final String JTDS_PROTOCOL = "jdbc:jtds:sqlserver";
    static final String MSSQL_PROTOCOL = "jdbc:sqlserver";
    static final String ATLASSIAN_REST_ANNOTATION_SCANNER_JAR_INDEXER_THRESHOLD_LIMIT_KEY = "rest.annotation.scanner.jar.indexer.threshold.limit";
    static final Pattern JTDS_URL = Pattern.compile("jdbc:jtds:sqlserver://(?<hostName>[^:;/]++)(?::(?<port>[\\d]{1,5}))?(?:/(?<databaseName>[^;]++))?(?:$|(?<parameters>[^:/].*))");
    static final Pattern MSSQL_URL = Pattern.compile("jdbc:sqlserver://(?<hostName>[^:;/]++)(?::(?<port>[\\d]{1,5}))?(?:$|(?<parameters>[^:/].*))");
    private final ClusterConfigurationHelperInternal clusterConfigurationHelper;
    private final SingleConnectionProvider databaseHelper;
    private final HealthCheckRunner healthCheckRunner;
    private final DeserializationFilterConfigurator deserializationFilterConfigurator;
    private BootstrapDatabaseAccessor.BootstrapDatabaseData bootstrapDatabaseData;
    private FileLock fileLock;
    private RandomAccessFile lockFile;

    @Deprecated
    public DefaultBootstrapManager(ApplicationConfiguration applicationConfiguration, ClusterConfigurationHelperInternal clusterConfigurationHelper, SingleConnectionProvider databaseHelper, HealthCheckRunner healthCheckRunner, HibernateConfigurator hibernateConfigurator, HomeLocator homeLocator, SetupPersister setupPersister, List<String> tables) {
        this.setApplicationConfig(Objects.requireNonNull(applicationConfiguration));
        this.setHibernateConfigurator(Objects.requireNonNull(hibernateConfigurator));
        this.setHomeLocator(Objects.requireNonNull(homeLocator));
        this.setSetupPersister(Objects.requireNonNull(setupPersister));
        this.setTables(Objects.requireNonNull(tables));
        this.clusterConfigurationHelper = Objects.requireNonNull(clusterConfigurationHelper);
        this.databaseHelper = Objects.requireNonNull(databaseHelper);
        this.healthCheckRunner = Objects.requireNonNull(healthCheckRunner);
        this.deserializationFilterConfigurator = new DeserializationFilterConfigurator();
    }

    public DefaultBootstrapManager(ApplicationConfiguration applicationConfiguration, ClusterConfigurationHelperInternal clusterConfigurationHelper, SingleConnectionProvider databaseHelper, HealthCheckRunner healthCheckRunner, HibernateConfigurator hibernateConfigurator, HomeLocator homeLocator, SetupPersister setupPersister, List<String> tables, DeserializationFilterConfigurator deserializationFilterConfigurator) {
        this.setApplicationConfig(Objects.requireNonNull(applicationConfiguration));
        this.setHibernateConfigurator(Objects.requireNonNull(hibernateConfigurator));
        this.setHomeLocator(Objects.requireNonNull(homeLocator));
        this.setSetupPersister(Objects.requireNonNull(setupPersister));
        this.setTables(Objects.requireNonNull(tables));
        this.clusterConfigurationHelper = Objects.requireNonNull(clusterConfigurationHelper);
        this.databaseHelper = Objects.requireNonNull(databaseHelper);
        this.healthCheckRunner = Objects.requireNonNull(healthCheckRunner);
        this.deserializationFilterConfigurator = deserializationFilterConfigurator;
    }

    public void init() throws BootstrapException {
        System.setProperty(ATLASSIAN_REST_ANNOTATION_SCANNER_JAR_INDEXER_THRESHOLD_LIMIT_KEY, System.getProperty(ATLASSIAN_REST_ANNOTATION_SCANNER_JAR_INDEXER_THRESHOLD_LIMIT_KEY, "60000"));
        this.configureDeserializationFilter();
        this.healthCheckRunner.runHealthChecks(LifecyclePhase.SETUP);
        super.init();
    }

    private void configureDeserializationFilter() {
        Predicate predicate;
        boolean isFilterSet;
        if (isDeserializationFilterSet.compareAndSet(false, true) && !(isFilterSet = this.deserializationFilterConfigurator.configure(predicate = new BlocklistConfigurator().configure()))) {
            throw new RuntimeException("Cannot set any global DeserializationFilter filter.");
        }
    }

    @Deprecated
    public void publishConfiguration() {
    }

    @Override
    public Optional<DatabaseDetails> getDatabaseDetail(String database) {
        ConfluenceDatabaseDetails dbDetails = new ConfluenceDatabaseDetailsBuilder().databaseType(database).build();
        log.info("Loading any preconfigured db configuration from the configuration file.");
        Properties hibernateProps = this.getHibernateProperties();
        String driverClass = hibernateProps.getProperty("hibernate.connection.driver_class");
        String dialect = hibernateProps.getProperty("hibernate.dialect");
        String dbUrl = hibernateProps.getProperty("hibernate.connection.url");
        String dbUserName = hibernateProps.getProperty("hibernate.connection.username");
        String dbPassword = hibernateProps.getProperty("hibernate.connection.password");
        if (StringUtils.isEmpty((CharSequence)driverClass) || StringUtils.isEmpty((CharSequence)dialect) || StringUtils.isEmpty((CharSequence)dbUrl) || StringUtils.isEmpty((CharSequence)dbUserName) || StringUtils.isEmpty((CharSequence)dbPassword)) {
            log.info("Could not found reconfiguration for DB Info");
            return Optional.empty();
        }
        dbDetails.setDriverClassName(driverClass);
        dbDetails.setDialect(dialect);
        dbDetails.setDatabaseUrl(dbUrl);
        dbDetails.setUserName(dbUserName);
        dbDetails.setPassword(dbPassword);
        return Optional.of(dbDetails);
    }

    public String getString(String key) {
        String tmp = super.getString(key);
        if (StringUtils.isBlank((CharSequence)tmp) && key.equals("struts.multipart.saveDir")) {
            tmp = super.getString("webwork.multipart.saveDir");
            if (StringUtils.isBlank((CharSequence)tmp)) {
                return tmp;
            }
            log.info("Migrating LEGACY_TEMP_DIR_PROP:{} to {}", (Object)"webwork.multipart.saveDir", (Object)"struts.multipart.saveDir");
            try {
                this.applicationConfig.setProperty((Object)"struts.multipart.saveDir", (Object)tmp);
                this.applicationConfig.removeProperty((Object)"webwork.multipart.saveDir");
                this.applicationConfig.save();
            }
            catch (Exception e) {
                log.error("Error saving configuration with corrected TEMP_DIR_PROP", (Throwable)e);
            }
        }
        return tmp;
    }

    @Override
    public String getFilePathProperty(String key) {
        String path = this.getString(key);
        return GeneralUtil.replaceConfluenceConstants(path, this.getLegacyHome(), this.getLocalHome());
    }

    protected String getDbUrl(DatabaseDetails dbDetails) {
        return GeneralUtil.replaceConfluenceConstants(super.getDbUrl(dbDetails), this.getLegacyHome(), this.getLocalHome());
    }

    @Override
    @Deprecated
    public String getConfluenceHome() {
        return this.getLegacyHome().getPath();
    }

    @Override
    public String getApplicationHome() {
        return this.getLegacyHome().getPath();
    }

    @Deprecated
    private File getLegacyHome() {
        if (this.clusterConfigurationHelper.isClusterHomeConfigured()) {
            return this.clusterConfigurationHelper.sharedHome().get();
        }
        return this.getLocalHome();
    }

    @Override
    public File getSharedHome() {
        return this.clusterConfigurationHelper.sharedHome().get();
    }

    @Override
    public File getLocalHome() {
        String confHome = this.applicationConfig.getApplicationHome();
        if (confHome == null) {
            throw new RuntimeException("confluence.home has not been configured or is returning a null. Please check your confluence home configuration.");
        }
        return new File(confHome);
    }

    @Override
    @VisibleForTesting
    public void setConfluenceHome(String confluenceHome) throws ConfigurationException {
        this.applicationConfig.setApplicationHome(confluenceHome);
    }

    @Override
    @Deprecated
    public void bootstrapSharedConfiguration(SharedConfigurationMap sharedConfig) {
    }

    @Override
    public File getConfiguredLocalHome() {
        String configuredLocalHome = this.getConfiguredApplicationHome();
        return configuredLocalHome != null ? new File(configuredLocalHome) : null;
    }

    @Override
    public boolean performPersistenceUpgrade() {
        return this.updateDatabaseDialects() && this.updateDatabaseUrl() && this.checkRequiredDriverIsPresent(this.getHibernateProperties());
    }

    @VisibleForTesting
    boolean updateDatabaseUrl() {
        String databaseUrl = (String)this.applicationConfig.getProperty((Object)"hibernate.connection.url");
        if (StringUtils.startsWithIgnoreCase((CharSequence)databaseUrl, (CharSequence)MYSQL_PROTOCOL) && StringUtils.containsIgnoreCase((CharSequence)databaseUrl, (CharSequence)"storage_engine")) {
            return this.updateMysqlDatabaseUrl(databaseUrl).length() > 0;
        }
        if (StringUtils.startsWithIgnoreCase((CharSequence)databaseUrl, (CharSequence)MSSQL_PROTOCOL)) {
            return this.updateMssqlDatabaseUrlEncryption(databaseUrl);
        }
        if (StringUtils.startsWithIgnoreCase((CharSequence)databaseUrl, (CharSequence)JTDS_PROTOCOL)) {
            return this.updateMssqlDatabaseUrl(databaseUrl);
        }
        return true;
    }

    boolean updateMssqlDatabaseUrlEncryption(String oldMssqlJdbcUrl) {
        Optional<String> mssqlJdbcUrl = DefaultBootstrapManager.computeMssqlEncryptionUrlFromMSSQL(oldMssqlJdbcUrl);
        if (mssqlJdbcUrl.isPresent()) {
            Optional<DatabaseDetails> dbdetails = this.getDatabaseDetail("mssql");
            if (dbdetails.isEmpty()) {
                log.error("Automatic upgrade of mssql-jdbc driver failed. Unable to obtain connection details from confluence.cfg.xml. Please follow {} to update manually.", (Object)MSSQL_DRIVER_UPGRADE_DOC_LINK);
                return true;
            }
            DatabaseDetails testDetails = dbdetails.get();
            testDetails.setDatabaseUrl(mssqlJdbcUrl.get());
            testDetails.setDriverClassName(MSSQL_DRIVER);
            ((ConfluenceDatabaseDetails)testDetails).setSimple(false);
            log.info("Testing SQLServer connection with {}", (Object)mssqlJdbcUrl.get());
            try {
                Connection conn = this.getTestDatabaseConnection(testDetails);
                conn.close();
                log.info("Connection test successful. Updating SQL Server Url from {} to {}", (Object)oldMssqlJdbcUrl, (Object)mssqlJdbcUrl.get());
                this.applicationConfig.setProperty((Object)"hibernate.connection.url", (Object)mssqlJdbcUrl.get());
                this.applicationConfig.setProperty((Object)"hibernate.connection.driver_class", (Object)MSSQL_DRIVER);
                this.applicationConfig.save();
            }
            catch (ConfigurationException e) {
                log.error("Automatic upgrade of mssql-jdbc driver failed. Error updating the database URL {} in configuration: {}. Turn on debug logging to see the full stack trace. Please follow {} to update manually.", new Object[]{mssqlJdbcUrl.get(), e.getMessage(), MSSQL_DRIVER_UPGRADE_DOC_LINK});
                log.debug("", (Throwable)e);
            }
            catch (BootstrapException e) {
                log.error("Automatic upgrade of mssql-jdbc driver failed. Unable to connect to the database using {}, connection error message : {}. Turn on debug logging to see the full stack trace. Please follow {} to update manually.", new Object[]{mssqlJdbcUrl.get(), e.getMessage(), MSSQL_DRIVER_UPGRADE_DOC_LINK});
                log.debug("", (Throwable)e);
            }
            catch (SQLException e) {
                log.warn("Failed to close test connection. This may result in a connection leak");
            }
        } else {
            log.error("Automatic upgrade of mssql-jdbc driver failed. Failed to parse the database URL {} automatically. Please follow {} to update manually.", (Object)oldMssqlJdbcUrl, (Object)MSSQL_DRIVER_UPGRADE_DOC_LINK);
        }
        return true;
    }

    @VisibleForTesting
    boolean updateMssqlDatabaseUrl(String jTDSUrl) {
        Optional<String> mssqlJdbcUrl = DefaultBootstrapManager.computeMssqlUrlFromJTDS(jTDSUrl);
        if (mssqlJdbcUrl.isPresent()) {
            Optional<DatabaseDetails> dbdetails = this.getDatabaseDetail("mssql");
            if (dbdetails.isEmpty()) {
                log.error("Automatic upgrade of jTDS driver failed. Unable to obtain connection details from confluence.cfg.xml. Please follow {} to update manually.", (Object)JTDS_DRIVER_UPGRADE_DOC_LINK);
                return true;
            }
            DatabaseDetails testDetails = dbdetails.get();
            testDetails.setDatabaseUrl(mssqlJdbcUrl.get());
            testDetails.setDriverClassName(MSSQL_DRIVER);
            ((ConfluenceDatabaseDetails)testDetails).setSimple(false);
            log.info("Testing SQLServer connection with {}", (Object)mssqlJdbcUrl.get());
            try {
                Connection conn = this.getTestDatabaseConnection(testDetails);
                conn.close();
                log.info("Connection test successful. Updating jTDS Url {} to SQLServer url {}", (Object)jTDSUrl, (Object)mssqlJdbcUrl.get());
                this.applicationConfig.setProperty((Object)"hibernate.connection.url", (Object)mssqlJdbcUrl.get());
                this.applicationConfig.setProperty((Object)"hibernate.connection.driver_class", (Object)MSSQL_DRIVER);
                this.applicationConfig.save();
            }
            catch (ConfigurationException e) {
                log.error("Automatic upgrade of jTDS driver failed. Error updating the database URL {} in configuration: {}. Turn on debug logging to see the full stack trace. Please follow {} to update manually.", new Object[]{mssqlJdbcUrl.get(), e.getMessage(), JTDS_DRIVER_UPGRADE_DOC_LINK});
                log.debug("", (Throwable)e);
            }
            catch (BootstrapException e) {
                log.error("Automatic upgrade of jTDS driver failed. Unable to connect to the database using {}, connection error message : {}. Turn on debug logging to see the full stack trace. Please follow {} to update manually.", new Object[]{mssqlJdbcUrl.get(), e.getMessage(), JTDS_DRIVER_UPGRADE_DOC_LINK});
                log.debug("", (Throwable)e);
            }
            catch (SQLException e) {
                log.warn("Failed to close test connection. This may result in a connection leak");
            }
        } else {
            log.error("Automatic upgrade of jTDS driver failed. Failed to parse the jTDS URL {} automatically. Please follow {} to update manually.", (Object)jTDSUrl, (Object)JTDS_DRIVER_UPGRADE_DOC_LINK);
        }
        return true;
    }

    private static Map<String, String> parseJdbcParameters(String value) {
        if (StringUtils.isBlank((CharSequence)value)) {
            return Collections.emptyMap();
        }
        String[] pieces = value.split(";");
        HashMap<String, String> parameters = new HashMap<String, String>(pieces.length);
        for (String piece : pieces) {
            if (StringUtils.isBlank((CharSequence)piece)) continue;
            String[] keyValue = piece.split("=");
            if (keyValue.length == 2 && !parameters.containsKey(keyValue[0])) {
                parameters.put(keyValue[0], keyValue[1]);
                continue;
            }
            log.debug("'{}' is not in parameter format, or is duplicated.", (Object)piece);
        }
        return parameters;
    }

    private static int parseSqlServerPort(String value) {
        try {
            return value == null ? 1433 : Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            log.error("unable to parse port '{}' in url. Defaulting to 1433", (Object)value, (Object)e);
            return 1433;
        }
    }

    @VisibleForTesting
    static Optional<String> computeMssqlUrlFromJTDS(String oldJTDSUrl) {
        return DefaultBootstrapManager.computeMssqlUrlMatchingPattern(JTDS_URL, oldJTDSUrl);
    }

    @VisibleForTesting
    static Optional<String> computeMssqlEncryptionUrlFromMSSQL(String oldMssqlUrl) {
        return DefaultBootstrapManager.computeMssqlUrlMatchingPattern(MSSQL_URL, oldMssqlUrl);
    }

    static Optional<String> computeMssqlUrlMatchingPattern(Pattern urlPattern, String oldUrl) {
        Matcher matcher = urlPattern.matcher(oldUrl);
        if (matcher.matches()) {
            Optional<String> instance;
            TreeMap<String, String> parameters = new TreeMap<String, String>(DefaultBootstrapManager.parseJdbcParameters(matcher.group("parameters")));
            String databaseName = null;
            try {
                databaseName = matcher.group("databaseName");
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
            if (databaseName == null) {
                databaseName = (String)parameters.remove("databaseName");
            }
            Optional<Object> optional = instance = parameters.containsKey("instance") ? Optional.of("\\" + (String)parameters.remove("instance")) : Optional.empty();
            if (databaseName != null) {
                parameters.put("databaseName", databaseName);
                parameters.putIfAbsent("encrypt", "false");
                int port = DefaultBootstrapManager.parseSqlServerPort(matcher.group("port"));
                return Optional.of(DefaultBootstrapManager.generateMssqlJdbcUrl(matcher.group("hostName"), instance, port, parameters));
            }
        }
        return Optional.empty();
    }

    private static String generateMssqlJdbcUrl(@NonNull String hostName, @NonNull Optional<String> instance, int port, Map<String, String> parameters) {
        String paramString = parameters.entrySet().stream().map(e -> (String)e.getKey() + "=" + (String)e.getValue()).collect(Collectors.joining(";"));
        return String.format("%1$s://%2$s%3$s:%4$d;%5$s", MSSQL_PROTOCOL, hostName, instance.orElse(""), port, paramString);
    }

    @VisibleForTesting
    String updateMysqlDatabaseUrl(String url) {
        String originalUrl = url;
        int index = url.indexOf("?");
        if (index != -1) {
            String paramString = url.substring(index + 1, url.length());
            StringBuilder fixedParamBuilder = new StringBuilder();
            url = url.substring(0, index);
            StringTokenizer queryParams = new StringTokenizer(paramString, "&");
            while (queryParams.hasMoreTokens()) {
                String parameterValuePair = queryParams.nextToken();
                int indexOfEquals = StringUtils.indexOfIgnoreCase((CharSequence)parameterValuePair, (CharSequence)"=", (int)0);
                if (indexOfEquals == -1) continue;
                String parameter = parameterValuePair.substring(0, indexOfEquals);
                String value = null;
                if (indexOfEquals + 1 < parameterValuePair.length()) {
                    value = parameterValuePair.substring(indexOfEquals + 1);
                }
                if (value == null || value.length() <= 0 || parameter.length() <= 0) continue;
                if (StringUtils.equalsIgnoreCase((CharSequence)parameter, (CharSequence)"sessionVariables")) {
                    String string = parameterValuePair = (value = this.removeStorageEngineFromSessionVariables(value)).length() > 0 ? parameter + "=" + value : "";
                }
                if (parameterValuePair.isEmpty()) continue;
                if (fixedParamBuilder.length() > 0) {
                    fixedParamBuilder.append("&");
                }
                fixedParamBuilder.append(parameterValuePair);
            }
            try {
                String newUrl;
                if (fixedParamBuilder.length() > 0) {
                    fixedParamBuilder.insert(0, "?");
                }
                if (!originalUrl.equals(newUrl = url + fixedParamBuilder.toString())) {
                    this.applicationConfig.setProperty((Object)"hibernate.connection.url", (Object)newUrl);
                    this.applicationConfig.save();
                }
                return newUrl;
            }
            catch (ConfigurationException e) {
                log.error("Error updating the database URL: " + e.getMessage(), (Throwable)e);
                return "";
            }
        }
        return "";
    }

    private String removeStorageEngineFromSessionVariables(String sessionVariablesValue) {
        String delimiter = sessionVariablesValue.contains(",") ? "," : ";";
        StringTokenizer variables = new StringTokenizer(sessionVariablesValue, delimiter);
        StringBuilder fixedValueBuilder = new StringBuilder();
        while (variables.hasMoreTokens()) {
            String variableValuePair = variables.nextToken();
            int indexOfEquals = StringUtils.indexOfIgnoreCase((CharSequence)variableValuePair, (CharSequence)"%3D", (int)0);
            if (indexOfEquals == -1) continue;
            String variable = variableValuePair.substring(0, indexOfEquals);
            String value = null;
            if (indexOfEquals + 3 < variableValuePair.length()) {
                value = variableValuePair.substring(indexOfEquals + 3);
            }
            if (value == null || value.length() <= 0 || variable.length() <= 0 || StringUtils.equalsIgnoreCase((CharSequence)variable, (CharSequence)"storage_engine")) continue;
            if (fixedValueBuilder.length() > 0) {
                fixedValueBuilder.append(",");
            }
            fixedValueBuilder.append(variableValuePair);
        }
        return fixedValueBuilder.toString();
    }

    private boolean updateDatabaseDialects() {
        String currentDatabaseDialect = (String)this.applicationConfig.getProperty((Object)"hibernate.dialect");
        Class<? extends Dialect> dialect = databaseDialectConversions.apply(currentDatabaseDialect);
        return dialect == null || this.updateDatabaseDialect(dialect);
    }

    private void populateSynchronyConfiguration() throws ConfigurationException {
        boolean isClusteredInstance = this.clusterConfigurationHelper.isClusteredInstance();
        if (isClusteredInstance) {
            if (StringUtils.isBlank((CharSequence)System.getProperty("synchrony.service.url"))) {
                this.applicationConfig.setProperty((Object)SYNCHRONY_PROXY_ENABLED, true);
            }
        } else {
            this.applicationConfig.setProperty((Object)SYNCHRONY_PROXY_ENABLED, !ConfluenceSystemProperties.isDevMode());
        }
        if (this.applicationConfig.getProperty((Object)SYNCHRONY_ENCRYPTION_DISABLED) == null) {
            this.applicationConfig.setProperty((Object)SYNCHRONY_ENCRYPTION_DISABLED, true);
        }
        if (!StringUtils.isBlank((CharSequence)System.getProperty("synchrony.service.authtoken"))) {
            this.applicationConfig.setProperty((Object)"synchrony.service.authtoken", (Object)System.getProperty("synchrony.service.authtoken"));
        } else {
            String authToken = (String)this.applicationConfig.getProperty((Object)"synchrony.service.authtoken");
            if (StringUtils.isBlank((CharSequence)authToken)) {
                authToken = DefaultSecureTokenGenerator.getInstance().generateToken().substring(0, 32);
            }
            this.applicationConfig.setProperty((Object)"synchrony.service.authtoken", (Object)authToken);
            System.setProperty("synchrony.service.authtoken", authToken);
        }
        this.applicationConfig.save();
        if (this.applicationConfig.getProperty((Object)"jwt.public.key") == null || this.applicationConfig.getProperty((Object)"jwt.private.key") == null) {
            try {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                int keyLength = Integer.getInteger("jwt.key.length", 3072);
                keyPairGenerator.initialize(keyLength);
                KeyPair keyPair = keyPairGenerator.genKeyPair();
                byte[] publicKey = keyPair.getPublic().getEncoded();
                byte[] privateKey = keyPair.getPrivate().getEncoded();
                this.applicationConfig.setProperty((Object)"jwt.private.key", (Object)Base64.encodeBase64String((byte[])privateKey));
                this.applicationConfig.setProperty((Object)"jwt.public.key", (Object)Base64.encodeBase64String((byte[])publicKey));
                this.applicationConfig.save();
            }
            catch (NoSuchAlgorithmException e) {
                throw new ConfigurationException("Error occurred while generating the RSA key pair for collaborative editing: " + e.getMessage(), (Throwable)e);
            }
        }
    }

    private boolean updateDatabaseDialect(Class<? extends Dialect> newDialect) {
        return this.updateProperty("hibernate.dialect", newDialect.getName(), e -> "Error updating the database dialect: " + e.getMessage());
    }

    private boolean updateProperty(String propertyName, String propertyVal, Function<ConfigurationException, String> errorMessage) {
        try {
            this.applicationConfig.setProperty((Object)propertyName, (Object)propertyVal);
            this.applicationConfig.save();
            return true;
        }
        catch (ConfigurationException e) {
            log.error(errorMessage.apply(e), (Throwable)e);
            return false;
        }
    }

    public Properties getHibernateProperties() {
        return this.hibernateConfig.getHibernateProperties();
    }

    public Connection getTestDatabaseConnection(DatabaseDetails databaseDetails) throws BootstrapException {
        Properties hibernateProperties;
        String databaseUrl;
        if (databaseDetails.getDatabaseUrl() == null && (databaseUrl = (hibernateProperties = this.hibernateConfig.getHibernateProperties()).getProperty("hibernate.connection.url")) != null) {
            databaseDetails.setDatabaseUrl(databaseUrl);
        }
        return super.getTestDatabaseConnection(databaseDetails);
    }

    @Override
    public void checkConfigurationOnStartup() throws BootstrapException {
        this.healthCheckRunner.runHealthChecks(LifecyclePhase.BOOTSTRAP_END);
        if (this.fileLock == null) {
            this.acquireConfluenceHomeLock();
        }
        Properties databaseProperties = this.getHibernateProperties();
        this.checkBuildNumber();
        if (this.hibernateConfig.isMySql()) {
            new MySQLChecker(this.databaseHelper).verifyDatabaseSetup(databaseProperties);
        }
    }

    @Override
    public void cleanupOnShutdown() {
        this.closeLocalHomeLock();
    }

    @Override
    public Optional<String> getDataSourceName() {
        return Optional.ofNullable(StringUtils.trimToNull((String)this.getString("hibernate.connection.datasource")));
    }

    @Override
    public String getHibernateDialect() {
        return this.getString("hibernate.dialect");
    }

    private void acquireConfluenceHomeLock() throws BootstrapException {
        File homeDir = this.getLocalHome();
        File lFile = new File(homeDir, LOCK_FILE_NAME);
        String homeDirPath = this.applicationConfig.getApplicationHome();
        String errorMsg = "Couldn't lock file 'lock' in directory " + homeDirPath + "\nMake sure the directory is not being used by another Confluence instance.";
        try {
            this.lockFile = new RandomAccessFile(lFile, "rw");
        }
        catch (Exception e) {
            throw new BootstrapException(errorMsg);
        }
        FileChannel channel = this.lockFile.getChannel();
        try {
            this.fileLock = channel.tryLock();
        }
        catch (Exception e) {
            throw new BootstrapException(errorMsg);
        }
        if (this.fileLock == null) {
            throw new BootstrapException(errorMsg);
        }
        log.info("Locked file 'lock' in confluence.home directory " + homeDirPath);
    }

    private void closeLocalHomeLock() {
        if (this.fileLock != null && this.lockFile != null) {
            String homeDirPath = this.applicationConfig.getApplicationHome();
            log.info("Unlock file 'lock' in confluence.home directory " + homeDirPath);
        }
        if (this.fileLock != null) {
            try {
                this.fileLock.close();
            }
            catch (IOException e) {
                log.error("Couldn't close fileLock", (Throwable)e);
            }
            this.fileLock = null;
        }
        if (this.lockFile != null) {
            try {
                this.lockFile.close();
            }
            catch (IOException e) {
                log.error("Couldn't close lockFile", (Throwable)e);
            }
            this.lockFile = null;
        }
    }

    private void checkBuildNumber() throws BootstrapException {
        BuildNumber homeDirectoryFinalizedBuildNumber = new BuildNumber(this.getFinalizedBuildNumber());
        BuildNumber applicationBuildNumber = new BuildNumber(BuildInformation.INSTANCE.getBuildNumber());
        BuildNumber finalizedBuildNumber = this.bootstrapDatabaseData.getFinalizedBuildNumber();
        new BuildNumberChecker(this.clusterConfigurationHelper.isClusteredInstance()).checkBuildNumbers(homeDirectoryFinalizedBuildNumber, applicationBuildNumber, finalizedBuildNumber);
    }

    private String getFinalizedBuildNumber() {
        Object finalizedBuildNumberProperty = this.applicationConfig.getProperty((Object)FINALIZED_BUILD_NUMBER_CONFIG_KEY);
        return finalizedBuildNumberProperty != null ? String.valueOf(finalizedBuildNumberProperty) : this.applicationConfig.getBuildNumber();
    }

    private boolean checkRequiredDriverIsPresent(Properties databaseProperties) {
        String requiredDriverClassName = (String)databaseProperties.get("hibernate.connection.driver_class");
        if (this.isDatasourceConfigured(databaseProperties) || this.isDriverPresent(requiredDriverClassName)) {
            return true;
        }
        String driverDocumentationLink = this.hibernateConfig.isMySql() ? "https://confluence.atlassian.com/display/DOC/Database+Setup+For+MySQL" : "https://confluence.atlassian.com/display/DOC/Database+Configuration";
        this.bootstrapFailureReason = "The database driver (" + requiredDriverClassName + ") was not found in the class path.<br/>This Confluence installation cannot be upgraded automatically. Please see Confluence documentation:<br/>" + driverDocumentationLink;
        return false;
    }

    private boolean isDatasourceConfigured(Properties databaseProperties) {
        return StringUtils.isNotBlank((CharSequence)((String)databaseProperties.get("hibernate.connection.datasource")));
    }

    private boolean isDriverPresent(String driverClass) {
        log.debug("Check if the {} database driver is in the classpath.", (Object)driverClass);
        try {
            Class.forName(driverClass);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    protected void afterConfigurationLoaded() throws ConfigurationException {
        this.bootstrapCluster();
    }

    private void bootstrapCluster() throws ConfigurationException {
        log.debug("Trying to populate setup configuration if running with Cluster mode");
        this.clusterConfigurationHelper.createClusterConfig();
        this.clusterConfigurationHelper.populateExistingClusterSetupConfig();
        this.populateAccessModeConfiguration();
        this.populateSynchronyConfiguration();
        this.updateSharedHomeToLocalHome("lucene.index.dir");
        this.updateSharedHomeToLocalHome("struts.multipart.saveDir");
        DefaultBootstrapDatabaseAccessor accessor = new DefaultBootstrapDatabaseAccessor(this.databaseHelper, this.hibernateConfig);
        this.bootstrapDatabaseData = accessor.getBootstrapData();
        try {
            this.clusterConfigurationHelper.bootstrapCluster(this.bootstrapDatabaseData);
        }
        catch (ClusterException ex) {
            throw new ConfigurationException("Exception bootstrapping cluster:" + ex.getMessage(), (Throwable)ex);
        }
    }

    private void populateAccessModeConfiguration() throws ConfigurationException {
        String localAccessMode = (String)this.applicationConfig.getProperty((Object)"access.mode");
        if (localAccessMode == null) {
            localAccessMode = AccessMode.READ_WRITE.name();
            this.applicationConfig.setProperty((Object)"access.mode", (Object)localAccessMode);
            this.applicationConfig.save();
        }
        if (this.clusterConfigurationHelper.isClusteredInstance()) {
            if (this.clusterConfigurationHelper.getSharedProperty("access.mode").isPresent()) {
                this.applicationConfig.setProperty((Object)"access.mode", this.clusterConfigurationHelper.getSharedProperty("access.mode").get());
                this.applicationConfig.save();
            } else {
                this.clusterConfigurationHelper.saveSharedProperty("access.mode", localAccessMode);
            }
        }
    }

    private void updateSharedHomeToLocalHome(String propertyKey) {
        String originalValue = (String)this.getProperty(propertyKey);
        if (originalValue != null && originalValue.contains("${confluenceHome}")) {
            String newValue = originalValue.replace("${confluenceHome}", "${localHome}");
            log.info("Updating {} to use local home instead of shared home", (Object)propertyKey);
            this.setProperty(propertyKey, newValue);
        }
    }

    protected void postBootstrapDatabase() throws BootstrapException {
        this.checkConfigurationOnStartup();
    }

    @Override
    public String getWebAppContextPath() {
        return this.getString("confluence.webapp.context.path");
    }

    @Override
    public void setWebAppContextPath(String webAppContextPath) {
        this.setProperty("confluence.webapp.context.path", webAppContextPath);
    }

    @Override
    public boolean isWebAppContextPathSet() {
        return this.getWebAppContextPath() != null;
    }
}

