/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.config.db;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.db.PropertyUtils;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseDetails {
    private static final Logger log = LoggerFactory.getLogger(DatabaseDetails.class);
    private String driverClassName;
    private String databaseUrl;
    private String userName;
    private String password;
    private int poolSize;
    private String dialect;
    private Properties configProps;
    private List dbNotes = new ArrayList();
    private Properties extraHibernateProperties = new Properties();

    static Properties getConfigProperties(String databaseName) {
        return PropertyUtils.getProperties("database-defaults/" + databaseName.toLowerCase() + ".properties", DatabaseDetails.class);
    }

    public static DatabaseDetails getDefaults(String databaseName) throws ConfigurationException {
        if ("other".equals(databaseName.toLowerCase())) {
            return new DatabaseDetails();
        }
        Properties props = DatabaseDetails.getConfigProperties(databaseName);
        if (props == null) {
            throw new ConfigurationException("The default values for '" + databaseName + "' not found. Check that properties file exists in your database-defaults directory");
        }
        return DatabaseDetails.buildDetailsFromProperties(props, databaseName);
    }

    public static DatabaseDetails buildDetailsFromProperties(Properties props, String databaseName) throws ConfigurationException {
        DatabaseDetails defaults = new DatabaseDetails();
        defaults.setDriverClassName(props.getProperty("driverClassName"));
        defaults.setDatabaseUrl(props.getProperty("databaseUrl"));
        defaults.setUserName(props.getProperty("userName"));
        defaults.setPassword(props.getProperty("password"));
        defaults.storeHibernateProperties(props);
        ArrayList<String> dbNotes = new ArrayList<String>();
        int i = 1;
        while (StringUtils.isNotEmpty((CharSequence)props.getProperty("note" + i))) {
            dbNotes.add(props.getProperty("note" + i));
            ++i;
        }
        defaults.setDbNotes(dbNotes);
        try {
            defaults.setPoolSize(Integer.parseInt(props.getProperty("poolSize")));
        }
        catch (NumberFormatException e) {
            log.error("Bad number within poolSize field in " + databaseName + ".");
            throw new ConfigurationException(e.getMessage(), e);
        }
        return defaults;
    }

    private static String nullSafeTrim(String str) {
        return str != null ? str.trim() : null;
    }

    public String getDatabaseUrl() {
        return this.databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = DatabaseDetails.nullSafeTrim(databaseUrl);
    }

    public int getPoolSize() {
        return this.poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = DatabaseDetails.nullSafeTrim(driverClassName);
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = DatabaseDetails.nullSafeTrim(userName);
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDialect() {
        return this.dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = DatabaseDetails.nullSafeTrim(dialect);
    }

    public List getDbNotes() {
        return this.dbNotes;
    }

    public void setDbNotes(List dbNotes) {
        this.dbNotes = dbNotes;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str = str.append(this.getDriverClassName()).append("\n");
        str = str.append(this.getDatabaseUrl()).append("\n");
        str = str.append(this.getDialect()).append("\n");
        str = str.append(this.getUserName()).append("\n");
        str = str.append(this.getPassword()).append("\n");
        return str.toString();
    }

    public Properties getConfigProps() {
        return this.configProps;
    }

    public void setupForDatabase(String database) {
        int poolSizeToSet = 10;
        if (database.equals("other")) {
            this.setPoolSize(poolSizeToSet);
            return;
        }
        Properties props = DatabaseDetails.getConfigProperties(database);
        this.setDialect(props.getProperty("dialect"));
        try {
            poolSizeToSet = Integer.parseInt(props.getProperty("poolSize"));
        }
        catch (NumberFormatException e) {
            log.error("Could find a property for poolSize; nonetheless, defaulting to 10.");
        }
        this.setPoolSize(poolSizeToSet);
        this.configProps = props;
        this.storeHibernateProperties(props);
    }

    private void storeHibernateProperties(Properties props) {
        Enumeration<Object> enu = props.keys();
        while (enu.hasMoreElements()) {
            String key = (String)enu.nextElement();
            if (key.matches("hibernate.*") && props.getProperty(key) != null) {
                this.extraHibernateProperties.put(key, props.getProperty(key));
                continue;
            }
            if (props.getProperty(key) != null) continue;
            log.warn("database hibernate property present but set to null: [" + key + "] = [" + props.getProperty(key) + "]. Setting this property anyway.");
            this.extraHibernateProperties.put(key, props.getProperty(key));
        }
    }

    public Properties getExtraHibernateProperties() {
        return this.extraHibernateProperties;
    }

    public boolean checkDriver() {
        try {
            Class.forName(this.getDriverClassName());
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}

