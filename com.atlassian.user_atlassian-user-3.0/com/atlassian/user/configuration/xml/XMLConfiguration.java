/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.configuration.xml;

import com.atlassian.user.configuration.Configuration;
import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.configuration.DefaultDelegationAccessor;
import com.atlassian.user.configuration.DelegationAccessor;
import com.atlassian.user.configuration.RepositoryAccessor;
import com.atlassian.user.configuration.RepositoryConfiguration;
import com.atlassian.user.configuration.xml.XMLConfigurationParser;
import com.atlassian.user.util.ClassLoaderUtils;
import com.atlassian.user.util.FileUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.log4j.Logger;

public class XMLConfiguration
implements Configuration {
    private static final Logger log = Logger.getLogger(XMLConfiguration.class);
    private static final String SYSTEM_ATLASSIAN_USER_CONFIGURATION = "atlassian.user.configuration";
    protected String fileName = "atlassian-user.xml";
    protected XMLConfigurationParser xmlParser;
    protected boolean initialized;
    protected boolean initializing;
    protected DelegationAccessor delegationAccessor;
    protected List repositoryConfigs;
    protected InputStream xmlIS;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static XMLConfiguration configureFromXMLString(String xmlString) throws ConfigurationException {
        ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes());
        try {
            XMLConfiguration config = new XMLConfiguration(in);
            config.init();
            XMLConfiguration xMLConfiguration = config;
            return xMLConfiguration;
        }
        finally {
            FileUtils.shutdownStream(in);
        }
    }

    public XMLConfiguration() throws ConfigurationException {
        this.xmlParser = new XMLConfigurationParser();
        this.delegationAccessor = new DefaultDelegationAccessor();
    }

    public XMLConfiguration(String fileName) throws ConfigurationException {
        this.fileName = fileName;
        this.xmlParser = new XMLConfigurationParser();
        this.delegationAccessor = new DefaultDelegationAccessor();
    }

    public XMLConfiguration(InputStream is) throws ConfigurationException {
        this.xmlIS = is;
        this.xmlParser = new XMLConfigurationParser();
        this.delegationAccessor = new DefaultDelegationAccessor();
    }

    public DelegationAccessor getDelegationAccessor() {
        if (!this.initialized && !this.initializing) {
            try {
                this.init();
            }
            catch (ConfigurationException e) {
                throw new RuntimeException("Atlassian User failed to initialize: " + e.getMessage(), e);
            }
        }
        return this.delegationAccessor;
    }

    public void init() throws ConfigurationException {
        if (!this.initialized && !this.initializing) {
            this.initializing = true;
            if (this.xmlIS == null) {
                this.xmlIS = this.getXmlConfigurationFileAsInputStream();
            }
            this.xmlParser.parse(this.xmlIS);
            try {
                this.xmlIS.close();
            }
            catch (IOException e) {
                throw new ConfigurationException("Could not close inputstream on [" + this.fileName + "]: " + e.getMessage(), e);
            }
            this.repositoryConfigs = this.xmlParser.getRepositoryConfigurations();
            for (RepositoryConfiguration repositoryConfiguration : this.repositoryConfigs) {
                RepositoryAccessor accessor = this.configureRepository(repositoryConfiguration);
                this.delegationAccessor.addRepositoryAccessor(accessor);
            }
            this.initialized = true;
        }
        this.initializing = false;
    }

    private InputStream getXmlConfigurationFileAsInputStream() {
        InputStream is = null;
        String configurationFilePath = System.getProperty(SYSTEM_ATLASSIAN_USER_CONFIGURATION);
        if (configurationFilePath != null) {
            File configurationFile = new File(configurationFilePath);
            if (configurationFile.exists()) {
                try {
                    is = new BufferedInputStream(new FileInputStream(configurationFile));
                    if (log.isInfoEnabled()) {
                        log.info((Object)("Using configuration file at: [" + configurationFile.getAbsolutePath() + "]"));
                    }
                }
                catch (IOException e) {
                    is = null;
                    log.warn((Object)("Couldn't load file at: [" + configurationFile.getAbsolutePath() + "], falling back on classpath resource."), (Throwable)e);
                }
            } else if (log.isDebugEnabled()) {
                log.debug((Object)("Couldn't find file at [" + configurationFile.getAbsolutePath() + "], falling back on classpath resource."));
            }
        } else if (log.isDebugEnabled()) {
            log.debug((Object)"System property atlassian.user.configuration not specified, using classpath resource.");
        }
        if (is == null) {
            is = ClassLoaderUtils.getResourceAsStream(this.fileName, this.getClass());
        }
        return is;
    }

    protected RepositoryAccessor configureRepository(RepositoryConfiguration repositoryConfiguration) throws ConfigurationException {
        return repositoryConfiguration.configure();
    }

    public boolean isInitialized() {
        return this.initialized;
    }
}

