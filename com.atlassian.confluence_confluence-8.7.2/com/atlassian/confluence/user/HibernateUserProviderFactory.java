/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.user.cache.CacheFactoryAware
 *  com.opensymphony.user.provider.AccessProvider
 *  com.opensymphony.user.provider.CredentialsProvider
 *  com.opensymphony.user.provider.ProfileProvider
 *  com.opensymphony.user.provider.UserProvider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user;

import com.atlassian.cache.CacheFactory;
import com.atlassian.user.cache.CacheFactoryAware;
import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.ProfileProvider;
import com.opensymphony.user.provider.UserProvider;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUserProviderFactory {
    private static final Logger log = LoggerFactory.getLogger(HibernateUserProviderFactory.class);
    private Properties accessProviderProperties;
    private Properties credentialsProviderProperties;
    private Properties profileProviderProperties;
    private CacheFactory cacheFactory;

    public AccessProvider createAccessProviderInstance() {
        return (AccessProvider)this.createUserProviderInstance(this.accessProviderProperties);
    }

    public CredentialsProvider createCredentialsProviderInstance() {
        return (CredentialsProvider)this.createUserProviderInstance(this.credentialsProviderProperties);
    }

    public ProfileProvider createProfileProviderInstance() {
        return (ProfileProvider)this.createUserProviderInstance(this.profileProviderProperties);
    }

    private UserProvider createUserProviderInstance(Properties providerProperties) {
        UserProvider userProvider = null;
        String baseProviderClassName = providerProperties.getProperty("base");
        try {
            userProvider = (UserProvider)Class.forName(baseProviderClassName).newInstance();
            ((CacheFactoryAware)userProvider).setCacheFactory(this.cacheFactory);
            userProvider.init(providerProperties);
        }
        catch (Exception e) {
            log.error("Couldn't create provider instance - " + baseProviderClassName, (Throwable)e);
        }
        return userProvider;
    }

    public void setCredentialsProviderProperties(Properties credentialsProviderProperties) {
        this.credentialsProviderProperties = credentialsProviderProperties;
    }

    public void setAccessProviderProperties(Properties accessProviderProperties) {
        this.accessProviderProperties = accessProviderProperties;
    }

    public void setProfileProviderProperties(Properties profileProviderProperties) {
        this.profileProviderProperties = profileProviderProperties;
    }

    public void setCacheFactory(CacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
    }
}

