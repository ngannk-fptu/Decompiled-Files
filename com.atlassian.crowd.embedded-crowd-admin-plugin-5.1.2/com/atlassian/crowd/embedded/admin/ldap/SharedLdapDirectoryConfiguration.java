/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.embedded.admin.ldap;

import com.atlassian.crowd.embedded.admin.directory.LdapConnectionPoolDirectoryAttributes;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SharedLdapDirectoryConfiguration {
    private static final Logger log = LoggerFactory.getLogger(SharedLdapDirectoryConfiguration.class);
    private String hostname;
    private int port = 389;
    private boolean useSSL;
    private String ldapConnectionPoolingTypeOption = LdapConnectionPoolDirectoryAttributes.DEFAULT_POOL_TYPE.name();
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

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isUseSSL() {
        return this.useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getLdapUrl() {
        return (this.useSSL ? "ldaps" : "ldap") + "://" + this.hostname + ":" + this.port;
    }

    public void setLdapUrl(String url) {
        if (url == null || !url.contains("://")) {
            return;
        }
        this.useSSL = url.startsWith("ldaps");
        try {
            URI uri = new URI(url);
            this.hostname = uri.getHost();
            if (uri.getPort() != -1) {
                this.port = uri.getPort();
            }
        }
        catch (URISyntaxException e) {
            log.warn("Got an invalid ldap url: {}", (Object)url, (Object)e);
        }
    }

    public String getLdapPoolType() {
        return this.ldapConnectionPoolingTypeOption;
    }

    public void setLdapPoolType(String option) {
        this.ldapConnectionPoolingTypeOption = option;
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
}

