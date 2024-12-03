/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.config.db.HibernateConfig
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.util.db;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.health.checks.DataSourceConfiguration;
import com.atlassian.confluence.impl.util.NumberUtil;
import com.atlassian.confluence.impl.util.OptionalUtils;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.setup.DatabaseEnum;
import com.atlassian.confluence.util.db.DatabaseConfigHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReturnValuesAreNonnullByDefault
@Internal
public class DefaultDatabaseConfigHelper
implements DatabaseConfigHelper {
    private static final Logger log = LoggerFactory.getLogger(DefaultDatabaseConfigHelper.class);
    private static final String HIBERNATE_SETUP_KEY = "hibernate.setup";
    private static final String HIBERNATE_SETUP_FLAG = "true";
    private final HibernateConfig hibernateConfig;
    private final DataSourceConfiguration dataSourceConfiguration;
    private final SingleConnectionProvider databaseHelper;

    public DefaultDatabaseConfigHelper(HibernateConfig hibernateConfig, DataSourceConfiguration dataSourceConfiguration, SingleConnectionProvider databaseHelper) {
        this.hibernateConfig = Objects.requireNonNull(hibernateConfig);
        this.dataSourceConfiguration = Objects.requireNonNull(dataSourceConfiguration);
        this.databaseHelper = Objects.requireNonNull(databaseHelper);
    }

    @Override
    public Optional<Integer> getConnectionPoolSize() {
        Supplier[] supplierArray = new Supplier[2];
        supplierArray[0] = this.dataSourceConfiguration::getPoolSize;
        supplierArray[1] = this::getJdbcPoolSize;
        return OptionalUtils.firstNonEmpty(supplierArray);
    }

    @Override
    public Optional<String> getProductName() {
        if (this.isHibernateSetUp()) {
            Optional<String> optional;
            block9: {
                Connection connection = this.databaseHelper.getConnection(this.hibernateConfig.getHibernateProperties());
                try {
                    String productName = connection.getMetaData().getDatabaseProductName();
                    int majorVersion = connection.getMetaData().getDatabaseMajorVersion();
                    optional = Optional.of(DatabaseEnum.getDatabaseType(productName, majorVersion));
                    if (connection == null) break block9;
                }
                catch (Throwable throwable) {
                    try {
                        if (connection != null) {
                            try {
                                connection.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (SQLException e) {
                        log.info(String.format("error getting the database product name from the connection: [%s]", e.getMessage()));
                    }
                }
                connection.close();
            }
            return optional;
        }
        return Optional.empty();
    }

    private Optional<Integer> getJdbcPoolSize() {
        String hikariMaxPoolSize = this.hibernateConfig.getHibernateProperties().getProperty("hibernate.hikari.maximumPoolSize");
        String maxPoolSize = StringUtils.defaultString((String)hikariMaxPoolSize, (String)this.hibernateConfig.getHibernateProperties().getProperty("hibernate.c3p0.max_size"));
        return NumberUtil.parseInteger(maxPoolSize);
    }

    private boolean isHibernateSetUp() {
        return Optional.ofNullable((String)this.hibernateConfig.getHibernateProperties().get(HIBERNATE_SETUP_KEY)).map(HIBERNATE_SETUP_FLAG::equals).orElse(false);
    }
}

