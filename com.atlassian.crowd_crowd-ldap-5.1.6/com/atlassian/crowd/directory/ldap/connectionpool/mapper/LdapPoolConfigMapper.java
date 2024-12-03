/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.pool2.factory.PoolConfig
 */
package com.atlassian.crowd.directory.ldap.connectionpool.mapper;

import com.atlassian.crowd.directory.ldap.connectionpool.data.LdapPoolConfig;
import org.springframework.ldap.pool2.factory.PoolConfig;

public class LdapPoolConfigMapper {
    public PoolConfig convertToPoolConfig(LdapPoolConfig ldapPoolConfig, long directoryId) {
        PoolConfig poolConfig = new PoolConfig();
        poolConfig.setBlockWhenExhausted(ldapPoolConfig.isBlockWhenExhausted());
        poolConfig.setEvictionPolicyClassName(ldapPoolConfig.getEvictionPolicyClassName());
        poolConfig.setFairness(ldapPoolConfig.isFairness());
        poolConfig.setJmxEnabled(ldapPoolConfig.isJmxEnabled());
        poolConfig.setJmxNamePrefix(ldapPoolConfig.getJmxNamePrefix() + directoryId);
        poolConfig.setLifo(ldapPoolConfig.isLifo());
        poolConfig.setMaxIdlePerKey(ldapPoolConfig.getMaxIdlePerKey());
        poolConfig.setMaxTotal(ldapPoolConfig.getMaxTotal());
        poolConfig.setMaxTotalPerKey(ldapPoolConfig.getMaxTotalPerKey());
        poolConfig.setMaxWaitMillis(ldapPoolConfig.getMaxWaitMillis());
        poolConfig.setMinEvictableIdleTimeMillis(ldapPoolConfig.getMinEvictableIdleTimeMillis());
        poolConfig.setSoftMinEvictableIdleTimeMillis(ldapPoolConfig.getSoftMinEvictableIdleTimeMillis());
        poolConfig.setNumTestsPerEvictionRun(ldapPoolConfig.getNumTestsPerEvictionRun());
        poolConfig.setTimeBetweenEvictionRunsMillis(ldapPoolConfig.getTimeBetweenEvictionRunsMillis());
        poolConfig.setTestOnBorrow(ldapPoolConfig.isTestOnBorrow());
        poolConfig.setTestOnCreate(ldapPoolConfig.isTestOnCreate());
        poolConfig.setTestOnReturn(ldapPoolConfig.isTestOnReturn());
        poolConfig.setTestWhileIdle(ldapPoolConfig.isTestWhileIdle());
        poolConfig.setMinIdlePerKey(ldapPoolConfig.getMinIdlePerKey());
        return poolConfig;
    }
}

