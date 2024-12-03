/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.DocumentException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.config.xml;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.xml.AbstractDom4jXmlConfigurationPersister;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDom4jXmlConfigurationPersister
extends AbstractDom4jXmlConfigurationPersister {
    private static final Logger log = LoggerFactory.getLogger(DefaultDom4jXmlConfigurationPersister.class);

    @Override
    public String getRootName() {
        return "application-configuration";
    }

    @Override
    public synchronized void save(String configPath, String configFile) throws ConfigurationException {
        this.saveDocument(configPath, configFile);
    }

    @Override
    public Object load(InputStream is) throws ConfigurationException {
        try {
            this.loadDocument(is);
        }
        catch (DocumentException e) {
            throw new ConfigurationException("Failed to parse config file: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String getStringConfigElement(String elementName) {
        String val = null;
        try {
            val = (String)this.getConfigElement(String.class, elementName);
        }
        catch (ConfigurationException e) {
            log.error("Could not load text from " + elementName + " element: " + e.getMessage());
        }
        return val;
    }

    @Override
    public Object load(String configPath, String configFile) throws ConfigurationException {
        if (configPath == null) {
            configPath = ".";
        }
        try {
            return this.load(new FileInputStream(new File(configPath + "/" + configFile)));
        }
        catch (FileNotFoundException e) {
            throw new ConfigurationException("failed to find config at: " + configPath + "/" + configFile, e);
        }
    }
}

