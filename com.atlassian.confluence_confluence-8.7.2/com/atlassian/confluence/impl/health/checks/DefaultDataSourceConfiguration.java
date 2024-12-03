/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.health.checks.DataSourceConfiguration;
import com.atlassian.confluence.setup.BootstrapManager;
import java.util.Objects;
import java.util.Optional;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDataSourceConfiguration
implements DataSourceConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataSourceConfiguration.class);
    private static final String DATA_SOURCE_JMX_PATTERN = "*:type=DataSource,host=*,context=%s,class=javax.sql.DataSource,name=\"%s\"";
    private final HibernateConfig hibernateConfig;
    private final MBeanServer mBeanServer;
    private final BootstrapManager bootstrapManager;

    public DefaultDataSourceConfiguration(HibernateConfig hibernateConfig, MBeanServer mBeanServer, BootstrapManager bootstrapManager) {
        this.hibernateConfig = Objects.requireNonNull(hibernateConfig);
        this.mBeanServer = Objects.requireNonNull(mBeanServer);
        this.bootstrapManager = Objects.requireNonNull(bootstrapManager);
    }

    @Override
    public Optional<String> getJdbcUrl() {
        return this.getDataSourceAttribute("url");
    }

    @Override
    public Optional<Integer> getPoolSize() {
        return this.getDataSourceAttribute("maxTotal");
    }

    private Optional<String> getDataSourceName() {
        return Optional.ofNullable(this.hibernateConfig.getHibernateProperties().getProperty("hibernate.connection.datasource"));
    }

    private <T> Optional<T> getDataSourceAttribute(String attributeName) {
        return this.getDataSourceName().flatMap(dataSourceName -> this.mBeanServer.queryNames(this.getDataSourceObjectName((String)dataSourceName), null).stream().findFirst().flatMap(name -> this.getDataSourceAttribute((ObjectName)name, attributeName)));
    }

    private ObjectName getDataSourceObjectName(String dataSourceName) {
        String shortDataSourceName = StringUtils.removeStart((String)dataSourceName, (String)"java:comp/env/");
        String dataSourceJmxPattern = String.format(DATA_SOURCE_JMX_PATTERN, this.bootstrapManager.getWebAppContextPath(), shortDataSourceName);
        try {
            return new ObjectName(dataSourceJmxPattern);
        }
        catch (MalformedObjectNameException e) {
            throw new IllegalStateException(e);
        }
    }

    private <T> Optional<T> getDataSourceAttribute(ObjectName objectName, String attributeName) {
        try {
            return Optional.ofNullable(this.mBeanServer.getAttribute(objectName, attributeName));
        }
        catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e) {
            LOGGER.debug("Error getting attribute '" + attributeName + "' from object '" + objectName + "'", (Throwable)e);
            return Optional.empty();
        }
    }
}

