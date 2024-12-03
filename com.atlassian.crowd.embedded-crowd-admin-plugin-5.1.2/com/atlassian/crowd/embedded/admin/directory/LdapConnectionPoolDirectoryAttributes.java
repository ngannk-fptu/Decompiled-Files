/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.ldap.LdapPoolType
 *  com.atlassian.crowd.directory.ldap.connectionpool.SpringLdapPoolConfigService
 *  com.atlassian.crowd.directory.ldap.connectionpool.data.LdapPoolConfig
 *  com.atlassian.crowd.directory.ldap.connectionpool.data.LdapPoolConfig$Builder
 *  com.atlassian.crowd.embedded.impl.ConnectionPoolPropertyUtil
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.crowd.embedded.admin.directory;

import com.atlassian.crowd.directory.ldap.LdapPoolType;
import com.atlassian.crowd.directory.ldap.connectionpool.SpringLdapPoolConfigService;
import com.atlassian.crowd.directory.ldap.connectionpool.data.LdapPoolConfig;
import com.atlassian.crowd.embedded.impl.ConnectionPoolPropertyUtil;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.lang3.math.NumberUtils;

public class LdapConnectionPoolDirectoryAttributes {
    public static final LdapPoolType DEFAULT_POOL_TYPE = LdapPoolType.COMMONS_POOL2;
    private String ldapPoolType = DEFAULT_POOL_TYPE.name();
    private String maxTotal;
    private String maxTotalPerKey;
    private String maxIdlePerKey;
    private String minIdlePerKey;
    private boolean blockWhenExhausted;
    private String maxWaitSeconds;
    private boolean testOnCreate;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean testWhileIdle;
    private String timeBetweenEvictionRunsSeconds;
    private String minEvictableIdleTimeSeconds;

    public String getLdapPoolType() {
        return this.ldapPoolType;
    }

    public void setLdapPoolType(String ldapPoolType) {
        this.ldapPoolType = ldapPoolType;
    }

    public String getMaxTotal() {
        return this.maxTotal;
    }

    public void setMaxTotal(String maxTotal) {
        this.maxTotal = maxTotal;
    }

    public String getMaxTotalPerKey() {
        return this.maxTotalPerKey;
    }

    public void setMaxTotalPerKey(String maxTotalPerKey) {
        this.maxTotalPerKey = maxTotalPerKey;
    }

    public String getMaxIdlePerKey() {
        return this.maxIdlePerKey;
    }

    public void setMaxIdlePerKey(String maxIdlePerKey) {
        this.maxIdlePerKey = maxIdlePerKey;
    }

    public String getMinIdlePerKey() {
        return this.minIdlePerKey;
    }

    public void setMinIdlePerKey(String minIdlePerKey) {
        this.minIdlePerKey = minIdlePerKey;
    }

    public boolean isBlockWhenExhausted() {
        return this.blockWhenExhausted;
    }

    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    public String getMaxWaitSeconds() {
        return this.maxWaitSeconds;
    }

    public void setMaxWaitSeconds(String maxWaitSeconds) {
        this.maxWaitSeconds = maxWaitSeconds;
    }

    public boolean isTestOnCreate() {
        return this.testOnCreate;
    }

    public void setTestOnCreate(boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
    }

    public boolean isTestOnBorrow() {
        return this.testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return this.testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return this.testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public String getTimeBetweenEvictionRunsSeconds() {
        return this.timeBetweenEvictionRunsSeconds;
    }

    public void setTimeBetweenEvictionRunsSeconds(String timeBetweenEvictionRunsSeconds) {
        this.timeBetweenEvictionRunsSeconds = timeBetweenEvictionRunsSeconds;
    }

    public String getMinEvictableIdleTimeSeconds() {
        return this.minEvictableIdleTimeSeconds;
    }

    public void setMinEvictableIdleTimeSeconds(String minEvictableIdleTimeSeconds) {
        this.minEvictableIdleTimeSeconds = minEvictableIdleTimeSeconds;
    }

    public Function<SpringLdapPoolConfigService, Map<String, String>> toAttributesMap() {
        return ldapPoolConfigService -> {
            LdapPoolConfig.Builder ldapPoolConfigBuilder = LdapPoolConfig.builder();
            ldapPoolConfigService.enrichByDefaultValues(ldapPoolConfigBuilder);
            ldapPoolConfigBuilder.setMaxTotal(NumberUtils.toInt((String)this.maxTotal)).setMaxTotalPerKey(NumberUtils.toInt((String)this.maxTotalPerKey)).setMaxIdlePerKey(NumberUtils.toInt((String)this.maxIdlePerKey)).setMinIdlePerKey(NumberUtils.toInt((String)this.minIdlePerKey)).setBlockWhenExhausted(this.blockWhenExhausted).setMaxWaitMillis(NumberUtils.toLong((String)ConnectionPoolPropertyUtil.secondsToMillis((String)this.maxWaitSeconds))).setTestOnCreate(this.testOnCreate).setTestOnBorrow(this.testOnBorrow).setTestOnReturn(this.testOnReturn).setTestWhileIdle(this.testWhileIdle).setTimeBetweenEvictionRunsMillis(NumberUtils.toLong((String)ConnectionPoolPropertyUtil.secondsToMillis((String)this.timeBetweenEvictionRunsSeconds))).setMinEvictableIdleTimeMillis(NumberUtils.toLong((String)ConnectionPoolPropertyUtil.secondsToMillis((String)this.minEvictableIdleTimeSeconds)));
            return ImmutableMap.of((Object)"ldap.pool.type", (Object)this.ldapPoolType, (Object)"ldap.pool.config", (Object)ldapPoolConfigService.toJsonLdapPoolConfig(ldapPoolConfigBuilder.build()));
        };
    }

    public static Function<SpringLdapPoolConfigService, LdapConnectionPoolDirectoryAttributes> fromAttributesMap(Map<String, String> serializedAttributes) {
        return ldapPoolConfigService -> {
            LdapConnectionPoolDirectoryAttributes attributes = new LdapConnectionPoolDirectoryAttributes();
            attributes.setLdapPoolType(serializedAttributes.getOrDefault("ldap.pool.type", LdapPoolType.JNDI.name()));
            String poolConfigJson = (String)serializedAttributes.get("ldap.pool.config");
            LdapPoolConfig ldapPoolConfig = ldapPoolConfigService.toLdapPoolConfigDto(poolConfigJson);
            attributes.setMaxTotal(String.valueOf(ldapPoolConfig.getMaxTotal()));
            attributes.setMaxTotalPerKey(String.valueOf(ldapPoolConfig.getMaxTotalPerKey()));
            attributes.setMaxIdlePerKey(String.valueOf(ldapPoolConfig.getMaxIdlePerKey()));
            attributes.setMinIdlePerKey(String.valueOf(ldapPoolConfig.getMinIdlePerKey()));
            attributes.setBlockWhenExhausted(ldapPoolConfig.isBlockWhenExhausted());
            attributes.setMaxWaitSeconds(ConnectionPoolPropertyUtil.millisToSeconds((String)String.valueOf(ldapPoolConfig.getMaxWaitMillis())));
            attributes.setTestOnCreate(ldapPoolConfig.isTestOnCreate());
            attributes.setTestOnBorrow(ldapPoolConfig.isTestOnBorrow());
            attributes.setTestOnReturn(ldapPoolConfig.isTestOnReturn());
            attributes.setTestWhileIdle(ldapPoolConfig.isTestWhileIdle());
            attributes.setTimeBetweenEvictionRunsSeconds(ConnectionPoolPropertyUtil.millisToSeconds((String)String.valueOf(ldapPoolConfig.getTimeBetweenEvictionRunsMillis())));
            attributes.setMinEvictableIdleTimeSeconds(ConnectionPoolPropertyUtil.millisToSeconds((String)String.valueOf(ldapPoolConfig.getMinEvictableIdleTimeMillis())));
            return attributes;
        };
    }
}

