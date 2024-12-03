/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.PatternMatchUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.support;

import java.sql.DatabaseMetaData;
import java.util.Collections;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.support.CustomSQLExceptionTranslatorRegistry;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodes;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

public class SQLErrorCodesFactory {
    public static final String SQL_ERROR_CODE_OVERRIDE_PATH = "sql-error-codes.xml";
    public static final String SQL_ERROR_CODE_DEFAULT_PATH = "org/springframework/jdbc/support/sql-error-codes.xml";
    private static final Log logger = LogFactory.getLog(SQLErrorCodesFactory.class);
    private static final SQLErrorCodesFactory instance = new SQLErrorCodesFactory();
    private final Map<String, SQLErrorCodes> errorCodesMap;
    private final Map<DataSource, SQLErrorCodes> dataSourceCache = new ConcurrentReferenceHashMap(16);

    public static SQLErrorCodesFactory getInstance() {
        return instance;
    }

    protected SQLErrorCodesFactory() {
        Map errorCodes;
        try {
            DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
            lbf.setBeanClassLoader(this.getClass().getClassLoader());
            XmlBeanDefinitionReader bdr = new XmlBeanDefinitionReader((BeanDefinitionRegistry)lbf);
            Resource resource = this.loadResource(SQL_ERROR_CODE_DEFAULT_PATH);
            if (resource != null && resource.exists()) {
                bdr.loadBeanDefinitions(resource);
            } else {
                logger.info((Object)"Default sql-error-codes.xml not found (should be included in spring-jdbc jar)");
            }
            resource = this.loadResource(SQL_ERROR_CODE_OVERRIDE_PATH);
            if (resource != null && resource.exists()) {
                bdr.loadBeanDefinitions(resource);
                logger.debug((Object)"Found custom sql-error-codes.xml file at the root of the classpath");
            }
            errorCodes = lbf.getBeansOfType(SQLErrorCodes.class, true, false);
            if (logger.isTraceEnabled()) {
                logger.trace((Object)("SQLErrorCodes loaded: " + errorCodes.keySet()));
            }
        }
        catch (BeansException ex) {
            logger.warn((Object)"Error loading SQL error codes from config file", (Throwable)ex);
            errorCodes = Collections.emptyMap();
        }
        this.errorCodesMap = errorCodes;
    }

    @Nullable
    protected Resource loadResource(String path) {
        return new ClassPathResource(path, this.getClass().getClassLoader());
    }

    public SQLErrorCodes getErrorCodes(String databaseName) {
        Assert.notNull((Object)databaseName, (String)"Database product name must not be null");
        SQLErrorCodes sec = this.errorCodesMap.get(databaseName);
        if (sec == null) {
            for (SQLErrorCodes candidate : this.errorCodesMap.values()) {
                if (!PatternMatchUtils.simpleMatch((String[])candidate.getDatabaseProductNames(), (String)databaseName)) continue;
                sec = candidate;
                break;
            }
        }
        if (sec != null) {
            this.checkCustomTranslatorRegistry(databaseName, sec);
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("SQL error codes for '" + databaseName + "' found"));
            }
            return sec;
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("SQL error codes for '" + databaseName + "' not found"));
        }
        return new SQLErrorCodes();
    }

    public SQLErrorCodes getErrorCodes(DataSource dataSource) {
        SQLErrorCodes sec = this.resolveErrorCodes(dataSource);
        return sec != null ? sec : new SQLErrorCodes();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public SQLErrorCodes resolveErrorCodes(DataSource dataSource) {
        SQLErrorCodes sec;
        Assert.notNull((Object)dataSource, (String)"DataSource must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Looking up default SQLErrorCodes for DataSource [" + this.identify(dataSource) + "]"));
        }
        if ((sec = this.dataSourceCache.get(dataSource)) == null) {
            Map<DataSource, SQLErrorCodes> map = this.dataSourceCache;
            synchronized (map) {
                sec = this.dataSourceCache.get(dataSource);
                if (sec == null) {
                    try {
                        String name = JdbcUtils.extractDatabaseMetaData(dataSource, DatabaseMetaData::getDatabaseProductName);
                        if (StringUtils.hasLength((String)name)) {
                            return this.registerDatabase(dataSource, name);
                        }
                    }
                    catch (MetaDataAccessException ex) {
                        logger.warn((Object)"Error while extracting database name", (Throwable)((Object)ex));
                    }
                    return null;
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("SQLErrorCodes found in cache for DataSource [" + this.identify(dataSource) + "]"));
        }
        return sec;
    }

    public SQLErrorCodes registerDatabase(DataSource dataSource, String databaseName) {
        SQLErrorCodes sec = this.getErrorCodes(databaseName);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Caching SQL error codes for DataSource [" + this.identify(dataSource) + "]: database product name is '" + databaseName + "'"));
        }
        this.dataSourceCache.put(dataSource, sec);
        return sec;
    }

    @Nullable
    public SQLErrorCodes unregisterDatabase(DataSource dataSource) {
        return this.dataSourceCache.remove(dataSource);
    }

    private String identify(DataSource dataSource) {
        return dataSource.getClass().getName() + '@' + Integer.toHexString(dataSource.hashCode());
    }

    private void checkCustomTranslatorRegistry(String databaseName, SQLErrorCodes errorCodes) {
        SQLExceptionTranslator customTranslator = CustomSQLExceptionTranslatorRegistry.getInstance().findTranslatorForDatabase(databaseName);
        if (customTranslator != null) {
            if (errorCodes.getCustomSqlExceptionTranslator() != null && logger.isDebugEnabled()) {
                logger.debug((Object)("Overriding already defined custom translator '" + errorCodes.getCustomSqlExceptionTranslator().getClass().getSimpleName() + " with '" + customTranslator.getClass().getSimpleName() + "' found in the CustomSQLExceptionTranslatorRegistry for database '" + databaseName + "'"));
            } else if (logger.isTraceEnabled()) {
                logger.trace((Object)("Using custom translator '" + customTranslator.getClass().getSimpleName() + "' found in the CustomSQLExceptionTranslatorRegistry for database '" + databaseName + "'"));
            }
            errorCodes.setCustomSqlExceptionTranslator(customTranslator);
        }
    }
}

