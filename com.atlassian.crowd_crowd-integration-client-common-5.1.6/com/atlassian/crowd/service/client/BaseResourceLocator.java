/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.service.client;

import com.atlassian.crowd.service.client.ResourceLocator;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseResourceLocator
implements ResourceLocator {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String resourceName;
    protected String propertyFileLocation;

    protected BaseResourceLocator(String resourceName) {
        Preconditions.checkNotNull((Object)resourceName);
        this.resourceName = resourceName;
    }

    protected String getResourceLocationFromSystemProperty() {
        String fileLocation = System.getProperty(this.resourceName);
        return this.formatFileLocation(fileLocation, false);
    }

    protected String formatFileLocation(String fileLocation, boolean skipValidation) {
        String url = null;
        if (fileLocation != null) {
            File file = new File(fileLocation);
            if (skipValidation || file.exists() && file.canRead()) {
                try {
                    url = file.toURI().toURL().toExternalForm();
                }
                catch (MalformedURLException e) {
                    this.logger.error(e.getMessage(), (Throwable)e);
                }
            } else {
                this.logger.debug("The file cannot be read or does not exist: " + fileLocation);
            }
        }
        return url;
    }

    protected String getResourceLocationFromClassPath() {
        URL resource = this.getClassLoaderResource();
        return resource != null ? resource.toExternalForm() : null;
    }

    protected URL getClassLoaderResource() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(this.resourceName);
        if (url == null) {
            url = BaseResourceLocator.class.getClassLoader().getResource(this.resourceName);
        }
        return url;
    }

    @Override
    public String getResourceName() {
        return this.resourceName;
    }

    @Override
    public Properties getProperties() {
        Properties properties = null;
        String resourceLocation = this.getResourceLocation();
        try {
            URL url = new URL(resourceLocation);
            properties = this.getPropertiesFromStream(url.openStream());
        }
        catch (FileNotFoundException e) {
            this.logger.info("No crowd.properties file found at {}", (Object)resourceLocation);
        }
        catch (IOException e) {
            this.logger.error(e.getMessage(), (Throwable)e);
        }
        return properties;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Properties getPropertiesFromStream(InputStream is) {
        if (is == null) {
            return null;
        }
        Properties props = new Properties();
        try {
            props.load(is);
        }
        catch (IOException e) {
            this.logger.error("Error loading properties from stream.", (Throwable)e);
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                this.logger.error("Failed to close the stream: " + e.getMessage(), (Throwable)e);
            }
        }
        return props;
    }

    @Override
    public String getResourceLocation() {
        return this.propertyFileLocation;
    }
}

