/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.osuser.configuration;

import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.configuration.DefaultRepositoryProcessor;
import com.atlassian.user.configuration.RepositoryAccessor;
import com.atlassian.user.configuration.RepositoryConfiguration;
import com.atlassian.user.impl.osuser.OSUAccessor;
import com.atlassian.user.impl.osuser.config.xml.OSUConfigurationLoader;
import com.atlassian.user.util.ClassLoaderUtils;
import java.io.InputStream;

public class OSURepositoryProcessor
extends DefaultRepositoryProcessor {
    public RepositoryAccessor process(RepositoryConfiguration config) throws ConfigurationException {
        OSUConfigurationLoader configLoader = (OSUConfigurationLoader)this.createBean("configLoader", config, false);
        config.addComponent("configLoader", configLoader);
        InputStream configFile = this.getConfigurationFile(config.getComponentClassName("configFile"));
        configLoader.load(configFile);
        OSUAccessor accessor = configLoader.getOSUAccessor();
        config.addComponent("accessor", accessor);
        config.addComponent("accessProvider", accessor.getAccessProvider());
        config.addComponent("credentialsProvider", accessor.getCredentialsProvider());
        config.addComponent("profileProvider", accessor.getProfileProvider());
        config.addComponent("credentialsProviderList", configLoader.getCredentialProviders());
        return super.process(config);
    }

    private InputStream getConfigurationFile(String configXMLFileName) throws ConfigurationException {
        InputStream is = ClassLoaderUtils.getResourceAsStream(configXMLFileName, this.getClass());
        if (is == null) {
            throw new ConfigurationException("Could not open InputStream on specified configuration file: [" + configXMLFileName + "]. Please ensure that the file is available on the classpath.");
        }
        return is;
    }
}

