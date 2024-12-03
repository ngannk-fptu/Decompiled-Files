/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.config.bootstrap;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.HomeLocator;
import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.config.db.HibernateConfigurator;
import com.atlassian.config.setup.SetupPersister;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAtlassianBootstrapManager
implements AtlassianBootstrapManager {
    private static final Logger privateLog = LoggerFactory.getLogger(DefaultAtlassianBootstrapManager.class);
    protected boolean bootstrapped;
    protected String bootstrapFailureReason;
    protected ApplicationConfiguration applicationConfig;
    protected SetupPersister setupPersister;
    protected HomeLocator homeLocator;
    protected HibernateConfigurator hibernateConfigurator;
    protected HibernateConfig hibernateConfig;
    private String operation;
    private List tables;

    @Override
    public void init() throws BootstrapException {
        try {
            if (StringUtils.isNotEmpty((CharSequence)this.homeLocator.getHomePath())) {
                this.applicationConfig.setApplicationHome(this.homeLocator.getHomePath());
                this.applicationConfig.setConfigurationFileName(this.homeLocator.getConfigFileName());
                if (this.applicationConfig.configFileExists()) {
                    this.applicationConfig.load();
                }
                this.afterConfigurationLoaded();
                this.setupPersister.setSetupType(this.applicationConfig.getSetupType());
                if ("complete".equals(this.setupPersister.getCurrentStep())) {
                    if (!this.performPersistenceUpgrade()) {
                        return;
                    }
                    this.applicationConfig.setSetupComplete(true);
                    this.publishConfiguration();
                }
            } else {
                privateLog.warn("Unable to set up application config: no home set");
            }
            this.finishBootstrapInitialisation();
            this.bootstrapped = true;
        }
        catch (ConfigurationException e) {
            privateLog.error("Home is not configured properly: ", (Throwable)e);
            this.bootstrapped = false;
            this.bootstrapFailureReason = e.getMessage();
        }
    }

    @Override
    public void publishConfiguration() {
    }

    @Override
    public Object getProperty(String key) {
        Object o = null;
        try {
            o = this.applicationConfig.getProperty(key);
        }
        catch (NullPointerException e) {
            privateLog.error("BootstrapManager was asked to fetch property ({}) and found a NullPointer", (Object)key);
        }
        return o;
    }

    @Override
    public void setProperty(String key, Object value) {
        if (value == null) {
            this.applicationConfig.removeProperty(key);
        } else {
            this.applicationConfig.setProperty((Object)key, value);
        }
        if (this.isSetupComplete()) {
            this.publishConfiguration();
        }
    }

    @Override
    public boolean isPropertyTrue(String prop) {
        return "true".equals(this.getString(prop));
    }

    @Override
    public void removeProperty(String key) {
        this.applicationConfig.removeProperty(key);
    }

    @Override
    public String getString(String key) {
        return (String)this.applicationConfig.getProperty(key);
    }

    @Override
    public String getFilePathProperty(String key) {
        return this.getString(key);
    }

    @Override
    public Collection getPropertyKeys() {
        return this.applicationConfig.getProperties().keySet();
    }

    @Override
    public Map getPropertiesWithPrefix(String prefix) {
        return this.applicationConfig.getPropertiesWithPrefix(prefix);
    }

    @Override
    public void save() throws ConfigurationException {
        this.applicationConfig.save();
    }

    @Override
    public String getConfiguredApplicationHome() {
        return this.homeLocator.getHomePath();
    }

    @Override
    public boolean isSetupComplete() {
        return this.isBootstrapped() && this.applicationConfig.isSetupComplete();
    }

    @Override
    public void setSetupComplete(boolean complete) {
        this.applicationConfig.setSetupComplete(complete);
    }

    @Override
    public String getBuildNumber() {
        return this.applicationConfig.getBuildNumber();
    }

    @Override
    public void setBuildNumber(String buildNumber) {
        this.applicationConfig.setBuildNumber(buildNumber);
    }

    @Override
    public Properties getHibernateProperties() {
        Properties props = new Properties();
        props.putAll((Map<?, ?>)this.applicationConfig.getPropertiesWithPrefix("hibernate."));
        return props;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void bootstrapDatabase(DatabaseDetails dbDetails, boolean embedded) throws BootstrapException {
        try {
            this.hibernateConfigurator.configureDatabase(dbDetails, embedded);
        }
        catch (ConfigurationException e) {
            privateLog.error("Could not successfully configure database:\n db: {}\n embedded = {}", (Object)dbDetails, (Object)embedded);
            privateLog.error("ConfigurationException reads thus: ", (Throwable)e);
            this.hibernateConfigurator.unconfigureDatabase();
            throw new BootstrapException(e);
        }
        Connection conn = null;
        try {
            conn = this.getTestDatabaseConnection(dbDetails);
            if (!this.databaseContainsExistingData(conn)) {
                throw new BootstrapException("Schema creation complete, but database tables don't seem to exist.");
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException sQLException) {}
        }
        this.postBootstrapDatabase();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void bootstrapDatasource(String datasourceName, String hibernateDialect) throws BootstrapException {
        try {
            this.hibernateConfigurator.configureDatasource(datasourceName, hibernateDialect);
        }
        catch (ConfigurationException e) {
            privateLog.error("Could not successfully configure datasource:\n db: {}\n dialect = {}", (Object)datasourceName, (Object)hibernateDialect);
            privateLog.error("ConfigurationException reads thus: ", (Throwable)e);
            this.hibernateConfigurator.unconfigureDatabase();
            throw new BootstrapException(e);
        }
        Connection connection = null;
        try {
            connection = this.getTestDatasourceConnection(datasourceName);
            if (!this.databaseContainsExistingData(connection)) {
                throw new BootstrapException("Schema creation complete, but tables could not be found.");
            }
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (SQLException sQLException) {}
        }
        this.postBootstrapDatabase();
    }

    @Override
    public Connection getTestDatabaseConnection(DatabaseDetails databaseDetails) throws BootstrapException {
        Connection conn = null;
        try {
            Class.forName(databaseDetails.getDriverClassName());
            conn = DriverManager.getConnection(this.getDbUrl(databaseDetails), databaseDetails.getUserName(), databaseDetails.getPassword());
            if (conn == null) {
                throw new BootstrapException("Connection was null. We could not successfully connect to the specified database!");
            }
            return conn;
        }
        catch (SQLException e) {
            privateLog.error("Could not successfully test your database: ", (Throwable)e);
            throw new BootstrapException(e);
        }
        catch (ClassNotFoundException e) {
            privateLog.error("Could not successfully test your database: ", (Throwable)e);
            throw new BootstrapException(e);
        }
    }

    @Override
    public Connection getTestDatasourceConnection(String datasourceName) throws BootstrapException {
        DataSource dsrc;
        privateLog.debug("datasource is {}", (Object)datasourceName);
        try {
            InitialContext ctx = new InitialContext();
            dsrc = (DataSource)ctx.lookup(datasourceName);
            if (dsrc == null) {
                throw new NamingException("Could not locate " + datasourceName);
            }
        }
        catch (NamingException e) {
            privateLog.error("Could not locate datasource: " + datasourceName, (Throwable)e);
            throw new BootstrapException("Could not locate datasource: " + datasourceName, e);
        }
        catch (ClassCastException e) {
            privateLog.error("Couldn't locate Datasource (" + datasourceName + ") in the initial context. An object was bound to this name but whatever we found, it wasn't a Datasource: ", (Throwable)e);
            throw new BootstrapException("Couldn't locate Datasource (" + datasourceName + ") in the initial context. An object was bound to this name but whatever we found, it wasn't a Datasource: ", e);
        }
        try {
            Connection conn = dsrc.getConnection();
            conn.createStatement();
            return conn;
        }
        catch (SQLException e) {
            privateLog.error("Couldn't open a connection on Datasource (" + datasourceName + "): ", (Throwable)e);
            throw new BootstrapException("Couldn't open a connection on Datasource (" + datasourceName + "): ", e);
        }
        catch (NullPointerException e) {
            privateLog.error("Couldn't open a connection on Datasource (" + datasourceName + "): ", (Throwable)e);
            throw new BootstrapException("Couldn't open a connection on Datasource (" + datasourceName + "): ", e);
        }
    }

    @Override
    public boolean databaseContainsExistingData(Connection connection) {
        for (String table : this.getTables()) {
            if (!this.tableExists(connection, table)) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean tableExists(Connection conn, String table) {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeQuery("select count(*) from " + table);
            boolean bl = true;
            return bl;
        }
        catch (SQLException e) {
            boolean bl = false;
            return bl;
        }
        finally {
            if (st != null) {
                try {
                    st.close();
                }
                catch (SQLException sQLException) {}
            }
        }
    }

    @Override
    public boolean isApplicationHomeValid() {
        return this.applicationConfig.isApplicationHomeValid();
    }

    protected boolean performPersistenceUpgrade() {
        return true;
    }

    protected void finishBootstrapInitialisation() throws ConfigurationException {
    }

    protected String getDbUrl(DatabaseDetails dbDetails) {
        return dbDetails.getDatabaseUrl();
    }

    protected void postBootstrapDatabase() throws BootstrapException {
    }

    protected void afterConfigurationLoaded() throws ConfigurationException {
    }

    public HomeLocator getHomeLocator() {
        return this.homeLocator;
    }

    public void setHomeLocator(HomeLocator homeLocator) {
        this.homeLocator = homeLocator;
    }

    @Override
    public ApplicationConfiguration getApplicationConfig() {
        return this.applicationConfig;
    }

    public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Override
    public String getApplicationHome() {
        return this.applicationConfig.getApplicationHome();
    }

    @Override
    public SetupPersister getSetupPersister() {
        return this.setupPersister;
    }

    public void setSetupPersister(SetupPersister setupPersister) {
        this.setupPersister = setupPersister;
    }

    @Override
    public boolean isBootstrapped() {
        return this.bootstrapped;
    }

    @Override
    public String getOperation() {
        return this.operation;
    }

    @Override
    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public HibernateConfigurator getHibernateConfigurator() {
        return this.hibernateConfigurator;
    }

    @Override
    public void setHibernateConfigurator(HibernateConfigurator hibernateConfigurator) {
        this.hibernateConfigurator = hibernateConfigurator;
    }

    @Override
    public HibernateConfig getHibernateConfig() {
        return this.hibernateConfig;
    }

    public void setHibernateConfig(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    @Override
    public String getBootstrapFailureReason() {
        return this.bootstrapFailureReason;
    }

    public List getTables() {
        return this.tables;
    }

    public void setTables(List tables) {
        this.tables = tables;
    }
}

