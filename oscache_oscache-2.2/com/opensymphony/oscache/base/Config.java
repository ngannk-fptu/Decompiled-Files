/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.base;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Config
implements Serializable {
    private static final transient Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$base$Config == null ? (class$com$opensymphony$oscache$base$Config = Config.class$("com.opensymphony.oscache.base.Config")) : class$com$opensymphony$oscache$base$Config));
    private static final String PROPERTIES_FILENAME = "/oscache.properties";
    private Properties properties = null;
    static /* synthetic */ Class class$com$opensymphony$oscache$base$Config;

    public Config() {
        this(null);
    }

    public Config(Properties p) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Config() called");
        }
        if (p == null) {
            this.loadProps();
        } else {
            this.properties = p;
        }
    }

    public String getProperty(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (this.properties == null) {
            return null;
        }
        String value = this.properties.getProperty(key);
        return value;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public Object get(Object key) {
        return this.properties.get(key);
    }

    public void set(Object key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (value == null) {
            return;
        }
        if (this.properties == null) {
            this.properties = new Properties();
        }
        this.properties.put(key, value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadProps() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Getting Config");
        }
        this.properties = new Properties();
        InputStream in = null;
        try {
            in = (class$com$opensymphony$oscache$base$Config == null ? (class$com$opensymphony$oscache$base$Config = Config.class$("com.opensymphony.oscache.base.Config")) : class$com$opensymphony$oscache$base$Config).getResourceAsStream(PROPERTIES_FILENAME);
            this.properties.load(in);
            log.info((Object)("Properties " + this.properties));
        }
        catch (Exception e) {
            log.error((Object)("Error reading /oscache.properties in CacheAdministrator.loadProps() " + e));
            log.error((Object)"Ensure the /oscache.properties file is readable and in your classpath.");
        }
        finally {
            try {
                in.close();
            }
            catch (Exception exception) {}
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

