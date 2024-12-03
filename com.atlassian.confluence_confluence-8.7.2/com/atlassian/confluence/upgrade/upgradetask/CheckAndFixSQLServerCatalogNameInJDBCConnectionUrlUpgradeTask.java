/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfig
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.UpgradeError
 *  com.atlassian.confluence.util.tomcat.TomcatConfigHelper
 *  com.atlassian.fugue.Pair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.config.ApplicationConfig;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.UpgradeError;
import com.atlassian.confluence.util.tomcat.TomcatConfigHelper;
import com.atlassian.fugue.Pair;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckAndFixSQLServerCatalogNameInJDBCConnectionUrlUpgradeTask
extends AbstractUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(CheckAndFixSQLServerCatalogNameInJDBCConnectionUrlUpgradeTask.class);
    static final String CONNECTION_URL_KEY = "hibernate.connection.url";
    static final String DATASOURCE_KEY = "hibernate.connection.datasource";
    private final BootstrapManager bootstrapManager;
    private final String configFilename;
    private final SingleConnectionProvider databaseHelper;
    private final ApplicationConfig applicationConfig;
    private final TomcatConfigHelper tomcatConfigHelper;

    public CheckAndFixSQLServerCatalogNameInJDBCConnectionUrlUpgradeTask(BootstrapManager bootstrapManager, String configFilename, SingleConnectionProvider databaseHelper, ApplicationConfig applicationConfig, TomcatConfigHelper tomcatConfigHelper) {
        this.bootstrapManager = bootstrapManager;
        this.configFilename = configFilename;
        this.databaseHelper = databaseHelper;
        this.applicationConfig = applicationConfig;
        this.tomcatConfigHelper = tomcatConfigHelper;
    }

    public String getBuildNumber() {
        return "7201";
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public void doUpgrade() {
    }

    public void validate() throws Exception {
        super.validate();
        if (this.bootstrapManager.getHibernateConfig().isSqlServer()) {
            File configFile = new File(this.bootstrapManager.getLocalHome(), this.configFilename);
            String jdbcUrl = (String)this.applicationConfig.getProperty((Object)CONNECTION_URL_KEY);
            String datasourceName = (String)this.applicationConfig.getProperty((Object)DATASOURCE_KEY);
            if (jdbcUrl != null) {
                log.info("JDBC connection property '{}'->'{}' found in [{}].", new Object[]{CONNECTION_URL_KEY, jdbcUrl, configFile.getAbsolutePath()});
                try (Connection c = this.databaseHelper.getConnection(this.bootstrapManager.getHibernateProperties());){
                    this.fixCatalog(c, jdbcUrl);
                }
                log.info("Upgrade JDBC url with case sensitive catalog finished.");
            } else if (datasourceName != null) {
                log.info("JNDI Datasource property '{}'->'{}' found in [{}].", new Object[]{DATASOURCE_KEY, datasourceName, configFile.getAbsolutePath()});
                try (Connection c = this.bootstrapManager.getTestDatasourceConnection(datasourceName);){
                    String configuredCatalog = c.getCatalog();
                    String fixedCatalog = this.getCatalogIgnoreCase(c, configuredCatalog);
                    if (!fixedCatalog.equals(configuredCatalog)) {
                        Optional<Pair> locations = this.tomcatConfigHelper.getPotentialDatasourceLocations().stream().map(location -> Pair.pair((Object)location, (Object)this.tomcatConfigHelper.getDatasourceUrl(location))).filter(p -> ((Optional)p.right()).isPresent()).map(p -> Pair.pair((Object)((File)p.left()), (Object)((String)((Optional)p.right()).get()))).findFirst();
                        String serverConfigXmlPath = ((File)locations.get().left()).getAbsolutePath();
                        String connectionUrl = (String)locations.get().right();
                        String unformatted = "Detected incorrect database catalog in the JNDI Datasource configuration with name %s, which causes the error %s. %sConfluence is unable to automatically correct this problem. Follow these steps to correct the issue: %s%s Shut down Confluence %s%s Find the JNDI Datasource configuration in the file %s %s%s Change the database catalog %s to %s in the connection url %s %sSee %s for detailed instructions.";
                        String kbarticleLink = "https://confluence.atlassian.com/x/75bENQ";
                        String sqlExceptionString = "java.sql.SQLException: The database name component of the object qualifier must be the name of the current database";
                        String uiMessage = String.format(unformatted, "<code>" + datasourceName + "</code>", "<code>" + sqlExceptionString + "</code>", "<br>", "<br>", "<ol><li>", "</li>", "<li>", "<code>" + serverConfigXmlPath + "</code>", "</li>", "<li>", "<code>" + configuredCatalog + "</code>", "<code>" + fixedCatalog + "</code>", "<code>" + connectionUrl + "</code>", "</li></ol><br>", "<a href='" + kbarticleLink + "'>this knowledge base article</a>");
                        String loggedMsg = String.format(unformatted, "<" + datasourceName + ">", "<" + sqlExceptionString + ">", "", "\n", "\t1.", "\n", "\t2.", "<" + serverConfigXmlPath + ">", "\n", "\t3.", "<" + configuredCatalog + ">", "<" + fixedCatalog + ">", "<" + connectionUrl + ">", "\n", kbarticleLink);
                        this.addError(new UpgradeError(uiMessage, (Throwable)new Exception(loggedMsg)));
                    }
                }
            }
        }
    }

    private void fixCatalog(Connection c, String jdbcUrl) throws SQLException, ConfigurationException {
        String oldCatalog = c.getCatalog();
        String fixedCatalog = this.getCatalogIgnoreCase(c, oldCatalog);
        if (!fixedCatalog.equals(oldCatalog)) {
            int catalogStartIndex = jdbcUrl.indexOf(oldCatalog);
            String replacementJdbc = jdbcUrl.substring(0, catalogStartIndex) + fixedCatalog + jdbcUrl.substring(catalogStartIndex + oldCatalog.length(), jdbcUrl.length());
            log.info("Replacing existing JDBC url '{}' with '{}'", (Object)jdbcUrl, (Object)replacementJdbc);
            this.applicationConfig.setProperty((Object)CONNECTION_URL_KEY, (Object)replacementJdbc);
            this.applicationConfig.save();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getCatalogIgnoreCase(Connection c, String existingCatalog) throws SQLException {
        try (ResultSet catalogs = c.getMetaData().getCatalogs();){
            while (catalogs.next()) {
                String caseSensitiveCatalog = catalogs.getString("TABLE_CAT");
                if (!caseSensitiveCatalog.equalsIgnoreCase(existingCatalog)) continue;
                existingCatalog = caseSensitiveCatalog;
                break;
            }
        }
        return existingCatalog;
    }
}

