/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

import java.io.File;

public class DatabaseInfo {
    private String url;
    private String dialect;
    private String isolationLevel;
    private String driverName;
    private String driverVersion;
    private String databaseVersion;
    private String databaseName;
    private String catalogName;
    private Long exampleLatency;
    private File driverFile;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDialect() {
        return this.dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getIsolationLevel() {
        return this.isolationLevel;
    }

    public void setIsolationLevel(String isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    public String getDriverName() {
        return this.driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverVersion() {
        return this.driverVersion;
    }

    public void setDriverVersion(String driverVersion) {
        this.driverVersion = driverVersion;
    }

    public String getVersion() {
        return this.databaseVersion;
    }

    public void setVersion(String databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public String getName() {
        return this.databaseName;
    }

    public void setName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getCatalogName() {
        return this.catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public Long getExampleLatency() {
        return this.exampleLatency;
    }

    public void setExampleLatency(Long exampleLatency) {
        this.exampleLatency = exampleLatency;
    }

    public File getDriverFile() {
        return this.driverFile;
    }

    public void setDriverFile(File driverFile) {
        this.driverFile = driverFile;
    }
}

