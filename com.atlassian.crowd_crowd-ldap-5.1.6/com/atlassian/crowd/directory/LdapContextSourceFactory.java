/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.ldap.core.ContextSource
 *  org.springframework.ldap.core.support.DirContextAuthenticationStrategy
 *  org.springframework.ldap.core.support.LdapContextSource
 *  org.springframework.ldap.pool2.DirContextType
 *  org.springframework.ldap.pool2.factory.MutablePooledContextSource
 *  org.springframework.ldap.pool2.factory.PoolConfig
 *  org.springframework.ldap.pool2.factory.PooledContextSource
 *  org.springframework.ldap.pool2.validation.DefaultDirContextValidator
 *  org.springframework.ldap.pool2.validation.DirContextValidator
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.SpringLDAPConnector;
import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.LdapSecureMode;
import com.atlassian.crowd.directory.ldap.connectionpool.SpringLdapPoolConfigService;
import com.atlassian.crowd.directory.ldap.connectionpool.data.LdapPoolConfig;
import com.atlassian.crowd.directory.ldap.connectionpool.mapper.LdapPoolConfigMapper;
import com.atlassian.crowd.directory.ssl.CrowdTlsDirContextAuthenticationStrategy;
import com.google.common.annotations.VisibleForTesting;
import java.util.Map;
import java.util.function.Supplier;
import javax.naming.directory.DirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool2.DirContextType;
import org.springframework.ldap.pool2.factory.MutablePooledContextSource;
import org.springframework.ldap.pool2.factory.PoolConfig;
import org.springframework.ldap.pool2.factory.PooledContextSource;
import org.springframework.ldap.pool2.validation.DefaultDirContextValidator;
import org.springframework.ldap.pool2.validation.DirContextValidator;

public class LdapContextSourceFactory {
    private static final Logger logger = LoggerFactory.getLogger(LdapContextSourceFactory.class);
    private final Supplier<LdapContextSource> ldapContextSourceSupplier;
    private final SpringLdapPoolConfigService springLdapPoolConfigService;
    private final LdapPoolConfigMapper ldapPoolConfigMapper;

    public LdapContextSourceFactory(SpringLdapPoolConfigService springLdapPoolConfigService) {
        this.springLdapPoolConfigService = springLdapPoolConfigService;
        this.ldapContextSourceSupplier = LdapContextSource::new;
        this.ldapPoolConfigMapper = new LdapPoolConfigMapper();
    }

    @VisibleForTesting
    LdapContextSourceFactory(SpringLdapPoolConfigService springLdapPoolConfigService, Supplier<LdapContextSource> ldapContextSourceSupplier) {
        this.springLdapPoolConfigService = springLdapPoolConfigService;
        this.ldapContextSourceSupplier = ldapContextSourceSupplier;
        this.ldapPoolConfigMapper = new LdapPoolConfigMapper();
    }

    ContextSource createMinimalContextSource(String username, String password, LDAPPropertiesMapper ldapPropertiesMapper, Map<String, Object> envProperties) {
        LdapContextSource contextSource = this.ldapContextSourceSupplier.get();
        contextSource.setUrl(ldapPropertiesMapper.getConnectionURL());
        contextSource.setUserDn(username);
        contextSource.setPassword(password);
        contextSource.setBaseEnvironmentProperties(envProperties);
        contextSource.setPooled(false);
        this.maybeApplyTls(ldapPropertiesMapper, contextSource);
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    ContextSource createContextSource(LDAPPropertiesMapper ldapPropertiesMapper, Map<String, Object> envProperties, boolean useLegacyPooling) {
        LdapContextSource contextSource = this.ldapContextSourceSupplier.get();
        String initialContextFactoryClassName = (String)envProperties.get("java.naming.factory.initial");
        if (initialContextFactoryClassName != null) {
            try {
                contextSource.setContextFactory(Class.forName(initialContextFactoryClassName, false, SpringLDAPConnector.class.getClassLoader()));
            }
            catch (ClassNotFoundException e) {
                NoClassDefFoundError err = new NoClassDefFoundError(initialContextFactoryClassName);
                err.initCause(e);
                throw err;
            }
        }
        contextSource.setUrl(ldapPropertiesMapper.getConnectionURL());
        contextSource.setUserDn(ldapPropertiesMapper.getUsername());
        contextSource.setPassword(ldapPropertiesMapper.getPassword());
        contextSource.setBaseEnvironmentProperties(envProperties);
        boolean tlsApplied = this.maybeApplyTls(ldapPropertiesMapper, contextSource);
        contextSource.setPooled(useLegacyPooling && !tlsApplied);
        try {
            contextSource.afterPropertiesSet();
        }
        catch (Exception e) {
            logger.error("Failed to configure context source", (Throwable)e);
        }
        return contextSource;
    }

    private boolean maybeApplyTls(LDAPPropertiesMapper ldapPropertiesMapper, LdapContextSource ldapContextSource) {
        if (ldapPropertiesMapper.getSecureMode() == LdapSecureMode.START_TLS) {
            ldapContextSource.setAuthenticationStrategy((DirContextAuthenticationStrategy)new CrowdTlsDirContextAuthenticationStrategy());
            return true;
        }
        return false;
    }

    PooledContextSource createPooledContextSource(long directoryId, LDAPPropertiesMapper ldapPropertiesMapper, Map<String, Object> envProperties) {
        ContextSource contextSource = this.createContextSource(ldapPropertiesMapper, envProperties, false);
        LdapPoolConfig ldapPoolConfig = this.springLdapPoolConfigService.toLdapPoolConfigDto(ldapPropertiesMapper.getLdapPoolConfig());
        CrowdPooledContextSource poolingContextSource = new CrowdPooledContextSource(this.ldapPoolConfigMapper.convertToPoolConfig(ldapPoolConfig, directoryId));
        poolingContextSource.setContextSource(contextSource);
        poolingContextSource.setDirContextValidator((DirContextValidator)new DefaultDirContextValidator());
        return poolingContextSource;
    }

    @VisibleForTesting
    static class CrowdPooledContextSource
    extends MutablePooledContextSource {
        CrowdPooledContextSource(PoolConfig poolConfig) {
            super(poolConfig);
        }

        protected DirContext getContext(DirContextType dirContextType) {
            try {
                return super.getContext(dirContextType);
            }
            catch (DataAccessResourceFailureException e) {
                this.logger.error("Error when creating ContextSource", (Throwable)e);
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException)e.getCause();
                }
                throw e;
            }
        }
    }
}

