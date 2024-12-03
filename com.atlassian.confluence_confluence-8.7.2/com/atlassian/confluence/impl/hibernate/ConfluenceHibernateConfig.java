/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.confluence.impl.hibernate.ConfluenceHibernateSchemaManagementTool
 *  com.atlassian.secrets.api.SecretStoreProvider
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  io.atlassian.fugue.Pair
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.confluence.impl.hibernate;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.impl.hibernate.ConfluenceHibernateSchemaManagementTool;
import com.atlassian.confluence.impl.hibernate.EmbeddedDatabaseManager;
import com.atlassian.confluence.internal.cipher.SecretStoreDataSourcePasswordDecrypter;
import com.atlassian.secrets.api.SecretStoreProvider;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.atlassian.fugue.Pair;
import java.util.Properties;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ConfluenceHibernateConfig
extends HibernateConfig
implements HibernateDatabaseCapabilities,
ApplicationContextAware {
    public static final String CIPHER_TYPE_PROP = "jdbc.password.decrypter.classname";
    private SecretStoreProvider secretStoreProvider;
    private BeanFactory applicationContext;
    private String h2DatabaseManagerBeanName;
    private final LoadingCache<Pair<String, String>, String> passwordCache = CacheBuilder.newBuilder().maximumSize(1L).build((CacheLoader)new CacheLoader<Pair<String, String>, String>(){

        public String load(Pair<String, String> key) {
            return ConfluenceHibernateConfig.this.tryDecrypt((String)key.left(), (String)key.right());
        }
    });

    private String tryDecrypt(String password, String cipherProviderClassName) {
        return this.secretStoreProvider.getInstance(cipherProviderClassName).map(SecretStoreDataSourcePasswordDecrypter::new).map(decrypter -> decrypter.decrypt(password)).orElse(password);
    }

    public Properties getHibernateProperties() {
        Properties prop = super.getHibernateProperties();
        String dialect = prop.getProperty("hibernate.dialect");
        Object cipherType = this.getApplicationConfig().getProperty((Object)CIPHER_TYPE_PROP);
        if (cipherType != null && StringUtils.isNotBlank((CharSequence)prop.getProperty("hibernate.connection.password"))) {
            String password = prop.getProperty("hibernate.connection.password");
            password = this.decryptPassword(password, cipherType.toString());
            prop.setProperty("hibernate.connection.password", password);
        }
        if (ConfluenceHibernateConfig.isOracleDialect((String)dialect) || ConfluenceHibernateConfig.isSqlServerDialect((String)dialect)) {
            prop.setProperty("hibernate.query.substitutions", "true 1, false 0");
        }
        if (this.shouldRunH2Server(prop)) {
            prop.setProperty("hibernate.connection.url", this.getH2DatabaseManager().ensureDatabaseStarted());
        }
        prop.setProperty("hibernate.atlassian.parse.hbm.hibernate5.only", "true");
        prop.setProperty("hibernate.schema_management_tool", ConfluenceHibernateSchemaManagementTool.class.getName());
        return prop;
    }

    private EmbeddedDatabaseManager getH2DatabaseManager() {
        return (EmbeddedDatabaseManager)this.applicationContext.getBean(this.h2DatabaseManagerBeanName, EmbeddedDatabaseManager.class);
    }

    protected boolean shouldRunH2Server(Properties prop) {
        return this.isH2() && prop.getProperty("hibernate.connection.url") == null;
    }

    @Override
    public boolean uniqueAllowsMultipleNullValues() {
        return !this.isOracle() && !this.isSqlServer();
    }

    @Override
    public boolean uniqueAllowsAnyNullValues() {
        return true;
    }

    public void setSecretStoreProvider(SecretStoreProvider secretStoreProvider) {
        this.secretStoreProvider = secretStoreProvider;
    }

    public SecretStoreProvider getSecretStoreProvider() {
        return this.secretStoreProvider;
    }

    public String decryptPassword(@Nonnull String password, @Nullable String decrypterClassName) {
        if (StringUtils.isBlank((CharSequence)password) || StringUtils.isBlank((CharSequence)decrypterClassName)) {
            return password;
        }
        return (String)this.passwordCache.getUnchecked((Object)Pair.pair((Object)password, (Object)decrypterClassName));
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setH2DatabaseManagerBeanName(String h2DatabaseManagerBeanName) {
        this.h2DatabaseManagerBeanName = h2DatabaseManagerBeanName;
    }
}

