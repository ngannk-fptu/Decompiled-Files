/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.properties.SpringLdapPoolDefaults
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.ldap.connectionpool;

import com.atlassian.crowd.common.properties.SpringLdapPoolDefaults;
import com.atlassian.crowd.directory.ldap.connectionpool.SpringLdapPoolConfigService;
import com.atlassian.crowd.directory.ldap.connectionpool.data.LdapPoolConfig;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSpringLdapPoolConfigService
implements SpringLdapPoolConfigService {
    private static final Logger log = LoggerFactory.getLogger(DefaultSpringLdapPoolConfigService.class);
    private static final String JMX_NAME_PREFIX_PATTERN = "%s-directory-";
    private static final String ERROR_MESSAGE_JSON_SERIALIZATION_EXCEPTION = "Can't serialize LDAP pool configuration to string";
    private static final String ERROR_MESSAGE_JSON_DESERIALIZATION_EXCEPTION = "Can't deserialize string configuration to LDAP pool object";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void enrichByDefaultValues(LdapPoolConfig.Builder ldapPoolConfigBuilder) {
        ldapPoolConfigBuilder.setBlockWhenExhausted(true).setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy").setFairness(false).setJmxEnabled(true).setJmxNamePrefix(String.format(JMX_NAME_PREFIX_PATTERN, "pool")).setLifo(true).setMaxIdlePerKey(-1).setMaxTotal(-1).setMaxTotalPerKey(-1).setMaxWaitMillis(-1000L).setMinEvictableIdleTimeMillis(SpringLdapPoolDefaults.MIN_EVICTABLE_TIME_MILLIS).setSoftMinEvictableIdleTimeMillis(-1L).setNumTestsPerEvictionRun(3).setTimeBetweenEvictionRunsMillis(SpringLdapPoolDefaults.EVICTION_RUN_INTERVAL_MILLIS).setTestOnBorrow(true).setTestOnCreate(false).setTestOnReturn(false).setTestWhileIdle(false).setMinIdlePerKey(0);
    }

    @Override
    public String toJsonLdapPoolConfig(LdapPoolConfig ldapPoolConfig) {
        try {
            return this.objectMapper.writeValueAsString((Object)ldapPoolConfig);
        }
        catch (IOException e) {
            log.warn(ERROR_MESSAGE_JSON_SERIALIZATION_EXCEPTION, (Throwable)e);
            throw new OperationFailedException((Throwable)e);
        }
    }

    @Override
    public LdapPoolConfig toLdapPoolConfigDto(String ldapPoolConfigJson) {
        LdapPoolConfig ldapPoolConfig;
        LdapPoolConfig fallbackConfig = this.getDefaultLdapPoolConfig();
        try {
            ldapPoolConfig = this.toPoolConfigOrElse(ldapPoolConfigJson, fallbackConfig);
        }
        catch (IOException e) {
            log.warn(ERROR_MESSAGE_JSON_DESERIALIZATION_EXCEPTION, (Throwable)e);
            ldapPoolConfig = fallbackConfig;
        }
        return ldapPoolConfig;
    }

    private LdapPoolConfig toPoolConfigOrElse(String ldapPoolConfigJson, LdapPoolConfig defaultValue) throws IOException {
        return StringUtils.isBlank((CharSequence)ldapPoolConfigJson) ? defaultValue : (LdapPoolConfig)this.objectMapper.readValue(ldapPoolConfigJson, LdapPoolConfig.class);
    }

    private LdapPoolConfig getDefaultLdapPoolConfig() {
        LdapPoolConfig.Builder ldapPoolConfigBuilder = LdapPoolConfig.builder();
        this.enrichByDefaultValues(ldapPoolConfigBuilder);
        return ldapPoolConfigBuilder.build();
    }
}

