/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.troubleshooting.healthcheck.impl;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.atlassian.troubleshooting.healthcheck.accessors.DbPlatform;
import com.atlassian.troubleshooting.healthcheck.accessors.DbPlatformFactory;
import com.atlassian.troubleshooting.healthcheck.model.DbType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultDbPlatformFactory
implements DbPlatformFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDbPlatformFactory.class);
    private final TransactionalExecutorFactory transactionalExecutorFactory;
    private final ApplicationProperties applicationProperties;
    private DbPlatform dbPlatform;

    @Autowired
    public DefaultDbPlatformFactory(TransactionalExecutorFactory transactionalExecutorFactory, ApplicationProperties applicationProperties) {
        this.transactionalExecutorFactory = Objects.requireNonNull(transactionalExecutorFactory);
        this.applicationProperties = applicationProperties;
    }

    @Override
    public DbPlatform create(DbType dbType, String dbVersion) {
        if (this.dbPlatform == null) {
            this.dbPlatform = this.calculateDbPlatform(dbType, dbVersion);
        }
        return this.dbPlatform;
    }

    private DbPlatform calculateDbPlatform(DbType dbType, String dbVersion) {
        boolean isAzure;
        if (!this.applicationProperties.getPlatformId().equals("bitbucket") && dbType.equals((Object)DbType.sqlServer) && (isAzure = ((Boolean)this.transactionalExecutorFactory.createReadOnly().execute(connection -> {
            try (ResultSet rs = connection.createStatement().executeQuery("SELECT CAST(SERVERPROPERTY('Edition') as NVARCHAR(128))");){
                String mssqlEdition = rs.next() ? rs.getString(1) : null;
                Boolean bl = !StringUtils.isEmpty((CharSequence)mssqlEdition) && mssqlEdition.toLowerCase().contains("azure");
                return bl;
            }
            catch (SQLException e) {
                LOGGER.warn("Could not determine the SQL Server edition", (Throwable)e);
                return false;
            }
        })).booleanValue())) {
            return new DbPlatformImpl(DbType.sqlServer, dbVersion + "Azure");
        }
        return new DbPlatformImpl(dbType, dbVersion);
    }

    static class DbPlatformImpl
    implements DbPlatform {
        private final DbType dbType;
        private final String dbVersion;

        DbPlatformImpl(DbType dbType, String dbVersion) {
            this.dbType = Objects.requireNonNull(dbType);
            this.dbVersion = Objects.requireNonNull(dbVersion);
        }

        @Override
        public DbType getDbType() {
            return this.dbType;
        }

        @Override
        public String getDbVersion() {
            return this.dbVersion;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            DbPlatformImpl that = (DbPlatformImpl)o;
            return new EqualsBuilder().append((Object)this.dbType, (Object)that.dbType).append((Object)this.dbVersion, (Object)that.dbVersion).isEquals();
        }

        public int hashCode() {
            return new HashCodeBuilder(17, 37).append((Object)this.dbType).append((Object)this.dbVersion).toHashCode();
        }

        public String toString() {
            return "DbPlatform{dbType=" + (Object)((Object)this.dbType) + ", dbVersion='" + this.dbVersion + '\'' + '}';
        }

        @Override
        public boolean versionEquals(String otherVersion) {
            return this.dbVersion.equals(otherVersion);
        }
    }
}

