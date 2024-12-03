/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.config;

import com.atlassian.config.HomeLocator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHomeLocator
implements HomeLocator {
    private static final Logger log = LoggerFactory.getLogger(DefaultHomeLocator.class);
    private String initPropertyName;
    private String propertiesFile;
    private String configFileName;
    private String servletHomeProperty;

    @Override
    public String getHomePath() {
        String home = this.getHomeFromSystemProperty();
        if (home == null) {
            home = this.getHomeFromConfigFile();
        }
        if (home == null) {
            home = this.servletHomeProperty;
        }
        if (log.isDebugEnabled()) {
            log.debug("Found " + this.initPropertyName + "  property with value: " + home);
        }
        return StringUtils.trim((String)home);
    }

    @Override
    public String getConfigFileName() {
        return this.configFileName;
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    private String getHomeFromSystemProperty() {
        log.debug("Trying to load " + this.initPropertyName + " from System property parameter... ");
        String sysProperty = System.getProperty(this.initPropertyName);
        if (sysProperty == null) {
            log.debug("Could not find " + this.initPropertyName + " property as a System property.");
        }
        return sysProperty;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getHomeFromConfigFile() {
        String confHome;
        block9: {
            log.debug("Trying to load " + this.initPropertyName + " from properties file... ");
            confHome = null;
            try {
                Properties props = new Properties();
                URL url = DefaultHomeLocator.class.getClassLoader().getResource(this.getPropertiesFile());
                if (url != null) {
                    InputStream inputStream = null;
                    try {
                        inputStream = url.openStream();
                        props.load(inputStream);
                    }
                    finally {
                        IOUtils.closeQuietly((InputStream)inputStream);
                    }
                }
                if (props.getProperty(this.initPropertyName) != null) {
                    confHome = props.getProperty(this.initPropertyName);
                } else {
                    log.debug("Could not find " + this.initPropertyName + " property in the " + this.getPropertiesFile() + " file. trying other methods.");
                }
            }
            catch (IOException e) {
                if (!log.isDebugEnabled()) break block9;
                log.debug("Could not find " + this.getPropertiesFile() + " in the classpath, trying other methods.");
            }
        }
        return confHome;
    }

    public String getPropertiesFile() {
        return this.propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public void setInitPropertyName(String initPropertyName) {
        this.initPropertyName = initPropertyName;
    }

    @Override
    public void lookupServletHomeProperty(ServletContext context) {
        log.debug("Trying to load " + this.initPropertyName + " from servlet context parameter... ");
        if (context != null && context.getInitParameter(this.initPropertyName) != null) {
            this.servletHomeProperty = context.getInitParameter(this.initPropertyName);
        } else {
            log.debug("Could not find " + this.initPropertyName + " property in the servlet context. Trying other methods.");
        }
    }
}

