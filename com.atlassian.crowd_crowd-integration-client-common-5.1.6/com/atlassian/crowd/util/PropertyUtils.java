/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtils {
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

    public Properties getProperties(String propertyResourceLocation) {
        Properties properties = null;
        if (propertyResourceLocation != null) {
            try {
                URL url = new URL(propertyResourceLocation);
                properties = this.getPropertiesFromStream(url.openStream());
            }
            catch (IOException e) {
                logger.error(e.getMessage(), (Throwable)e);
            }
        }
        return properties;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Properties getPropertiesFromStream(InputStream is) {
        if (is == null) {
            return null;
        }
        Properties props = new Properties();
        try {
            props.load(is);
        }
        catch (IOException e) {
            logger.error("Error loading properties from stream.", (Throwable)e);
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                logger.error("Failed to close the stream: " + e.getMessage(), (Throwable)e);
            }
        }
        return props;
    }

    public boolean removeProperty(String propertyResourceLocation, String key) {
        boolean success = false;
        Properties properties = this.getProperties(propertyResourceLocation);
        Object currentValue = properties.remove(key);
        logger.info("Updating properties resource: " + propertyResourceLocation + " removing property with key: " + key);
        if (currentValue != null) {
            success = true;
        }
        this.storeProperties(propertyResourceLocation, properties);
        return success;
    }

    public void updateProperty(String propertyResourceLocation, String key, String value) {
        Properties properties = this.getProperties(propertyResourceLocation);
        if (properties != null) {
            properties.setProperty(key, value);
            logger.info("Updating properties resource: " + propertyResourceLocation + " adding property: " + key + "|" + value);
            this.storeProperties(propertyResourceLocation, properties);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SuppressFBWarnings(value={"PATH_TRAVERSAL_OUT"}, justification="Updating the given path is the entire point")
    private void storeProperties(String propertyResourceLocation, Properties properties) {
        OutputStream ostream = null;
        try {
            String crowdFile = URLDecoder.decode(new URL(propertyResourceLocation).getFile(), "UTF-8");
            ostream = new FileOutputStream(crowdFile);
            properties.store(ostream, null);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), (Throwable)e);
        }
        finally {
            if (ostream != null) {
                try {
                    ostream.close();
                }
                catch (IOException e) {
                    logger.error(e.getMessage(), (Throwable)e);
                }
            }
        }
    }
}

