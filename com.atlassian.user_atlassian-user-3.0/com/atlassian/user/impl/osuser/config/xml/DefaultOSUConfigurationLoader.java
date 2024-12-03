/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.opensymphony.user.provider.AccessProvider
 *  com.opensymphony.user.provider.CredentialsProvider
 *  com.opensymphony.user.provider.ProfileProvider
 *  com.opensymphony.user.provider.UserProvider
 *  com.opensymphony.util.ClassLoaderUtil
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.osuser.config.xml;

import com.atlassian.cache.CacheFactory;
import com.atlassian.user.cache.CacheFactoryAware;
import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.impl.osuser.DefaultOSUAccessor;
import com.atlassian.user.impl.osuser.OSUAccessor;
import com.atlassian.user.impl.osuser.config.xml.DefaultOSUConfigurationHandler;
import com.atlassian.user.impl.osuser.config.xml.OSUConfigurationLoader;
import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.CredentialsProvider;
import com.opensymphony.user.provider.ProfileProvider;
import com.opensymphony.user.provider.UserProvider;
import com.opensymphony.util.ClassLoaderUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultOSUConfigurationLoader
implements OSUConfigurationLoader {
    private static final Logger log = Logger.getLogger(DefaultOSUConfigurationLoader.class);
    private DefaultOSUConfigurationHandler configHandler;
    private OSUAccessor accessor = new DefaultOSUAccessor();
    private ProfileProvider profileProvider;
    private AccessProvider accessProvider;
    private CredentialsProvider credentialsProvider;
    private List<CredentialsProvider> credentialProviders = new ArrayList<CredentialsProvider>();
    private CacheFactory cacheFactory;

    public synchronized void load(InputStream in) throws ConfigurationException {
        log.debug((Object)"Loading config");
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(in, this.getOSUserConfigurationHandler());
        }
        catch (SAXException e) {
            log.error((Object)"Could not parse config XML", (Throwable)e);
            throw new ConfigurationException(e.getMessage());
        }
        catch (IOException e) {
            log.error((Object)"Could not read config from stream", (Throwable)e);
            throw new ConfigurationException(e.getMessage());
        }
        catch (ParserConfigurationException e) {
            log.fatal((Object)"Could not obtain SAX parser", (Throwable)e);
            throw new ConfigurationException(e.getMessage());
        }
        catch (RuntimeException e) {
            log.fatal((Object)"RuntimeException", (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.fatal((Object)"Exception", e);
            throw new ConfigurationException(e.getMessage());
        }
    }

    public DefaultHandler getOSUserConfigurationHandler() {
        if (this.configHandler == null) {
            this.configHandler = new DefaultOSUConfigurationHandler(this);
        }
        return this.configHandler;
    }

    public void setOSUserConfigurationHandler(DefaultOSUConfigurationHandler configHandler) {
        this.configHandler = configHandler;
    }

    public void addProvider(String providerClassName, Properties providerProperties) throws ConfigurationException {
        block13: {
            if (log.isDebugEnabled()) {
                log.debug((Object)("UserProvider class = " + providerClassName + " " + providerProperties));
            }
            try {
                UserProvider provider = (UserProvider)ClassLoaderUtil.loadClass((String)providerClassName, this.getClass()).newInstance();
                if (provider == null) {
                    throw new ConfigurationException("OSUser provider class was unable to be instantiated: [" + providerClassName + "]");
                }
                if (provider instanceof CacheFactoryAware) {
                    ((CacheFactoryAware)provider).setCacheFactory(this.cacheFactory);
                }
                if (provider.init(providerProperties)) {
                    if (provider instanceof AccessProvider) {
                        if (this.accessor != null) {
                            this.accessor.setAccessProvider((AccessProvider)provider);
                        }
                        this.accessProvider = (AccessProvider)provider;
                    }
                    if (provider instanceof CredentialsProvider) {
                        CredentialsProvider credentialsProvider = (CredentialsProvider)provider;
                        if (this.accessor != null) {
                            this.accessor.setCredentialsProvider(credentialsProvider);
                        }
                        if (!this.credentialProviders.contains(credentialsProvider)) {
                            this.credentialProviders.add(credentialsProvider);
                        }
                        this.credentialsProvider = (CredentialsProvider)provider;
                    }
                    if (provider instanceof ProfileProvider) {
                        if (this.accessor != null) {
                            this.accessor.setProfileProvider((ProfileProvider)provider);
                        }
                        this.profileProvider = (ProfileProvider)provider;
                    }
                    break block13;
                }
                log.error((Object)("Could not getConnectionPoolProperties provider " + providerClassName));
                throw new ConfigurationException("Could not getConnectionPoolProperties provider " + providerClassName);
            }
            catch (Exception e) {
                log.error((Object)("Could not create instance of provider [" + providerClassName + "]: "), (Throwable)e);
                throw new ConfigurationException(e.getMessage());
            }
        }
    }

    public void setAccessor(DefaultOSUAccessor accessor) {
        this.accessor = accessor;
    }

    public OSUAccessor getOSUAccessor() {
        return this.accessor;
    }

    public List getCredentialProviders() {
        return this.credentialProviders;
    }

    public ProfileProvider getProfileProvider() {
        return this.profileProvider;
    }

    public AccessProvider getAccessProvider() {
        return this.accessProvider;
    }

    public CredentialsProvider getCredentialsProvider() {
        return this.credentialsProvider;
    }

    public void setCacheFactory(CacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
    }
}

