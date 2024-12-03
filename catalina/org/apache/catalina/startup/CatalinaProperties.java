/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.catalina.startup.Bootstrap;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class CatalinaProperties {
    private static final Log log = LogFactory.getLog(CatalinaProperties.class);
    private static Properties properties = null;

    public static String getProperty(String name) {
        return properties.getProperty(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void loadProperties() {
        InputStream is = null;
        String fileName = "catalina.properties";
        try {
            String configUrl = System.getProperty("catalina.config");
            if (configUrl != null) {
                if (configUrl.indexOf(47) == -1) {
                    fileName = configUrl;
                } else {
                    is = new URI(configUrl).toURL().openStream();
                }
            }
        }
        catch (Throwable t) {
            CatalinaProperties.handleThrowable(t);
        }
        if (is == null) {
            try {
                File home = new File(Bootstrap.getCatalinaBase());
                File conf = new File(home, "conf");
                File propsFile = new File(conf, fileName);
                is = new FileInputStream(propsFile);
            }
            catch (Throwable t) {
                CatalinaProperties.handleThrowable(t);
            }
        }
        if (is == null) {
            try {
                is = CatalinaProperties.class.getResourceAsStream("/org/apache/catalina/startup/catalina.properties");
            }
            catch (Throwable t) {
                CatalinaProperties.handleThrowable(t);
            }
        }
        if (is != null) {
            try {
                properties = new Properties();
                properties.load(is);
            }
            catch (Throwable t) {
                CatalinaProperties.handleThrowable(t);
                log.warn((Object)t);
            }
            finally {
                try {
                    is.close();
                }
                catch (IOException ioe) {
                    log.warn((Object)"Could not close catalina properties file", (Throwable)ioe);
                }
            }
        }
        if (is == null) {
            log.warn((Object)"Failed to load catalina properties file");
            properties = new Properties();
        }
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String name = (String)enumeration.nextElement();
            String value = properties.getProperty(name);
            if (value == null) continue;
            System.setProperty(name, value);
        }
    }

    private static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError)t;
        }
    }

    static {
        CatalinaProperties.loadProperties();
    }
}

