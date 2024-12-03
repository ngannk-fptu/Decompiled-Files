/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.DatabaseDetails
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.confluence.setup.DatabaseEnum;
import com.atlassian.confluence.setup.JDBCUrlBuilderFactory;
import com.atlassian.confluence.setup.actions.DatabaseList;
import java.util.List;

public class ConfluenceDatabaseDetails
extends DatabaseDetails {
    private static final List DATABASE_LIST = new DatabaseList().getDatabases();
    private String databaseType;
    private boolean simple = true;
    private String hostname;
    private String port;
    private String databaseName;
    private String serviceName;
    private String instanceName;

    public List getDatabases() {
        return DATABASE_LIST;
    }

    public String getDatabaseType() {
        return this.databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public boolean isSimple() {
        return this.simple;
    }

    public void setSimple(boolean simple) {
        this.simple = simple;
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getDatabaseUrl() {
        if (this.simple && this.databaseType != null) {
            return JDBCUrlBuilderFactory.getInstance(this.databaseType).getDatabaseUrl(this);
        }
        return super.getDatabaseUrl();
    }

    public void setupForDatabase(String databaseType) {
        super.setupForDatabase(databaseType);
        if (!DatabaseEnum.OTHER.getType().equals(databaseType)) {
            this.setDriverClassName(this.getConfigProps().getProperty("driverClassName"));
        }
    }
}

