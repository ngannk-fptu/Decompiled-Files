/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.ClassLoaderUtil
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.module.propertyset.config;

import com.opensymphony.util.ClassLoaderUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class PropertySetConfig {
    private static PropertySetConfig config;
    private static final Object lock;
    private static final Log log;
    private static final String[] CONFIG_LOCATIONS;
    private HashMap propertySetArgs;
    private HashMap propertySets;
    static /* synthetic */ Class class$com$opensymphony$module$propertyset$config$PropertySetConfig;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    private PropertySetConfig() {
        block18: {
            super();
            this.propertySetArgs = new HashMap<K, V>();
            this.propertySets = new HashMap<K, V>();
            is = this.load();
            dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            db = null;
            try {
                db = dbf.newDocumentBuilder();
            }
            catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            doc = null;
            try {
                doc = db.parse(is);
                var7_6 = null;
                ** if (is == null) goto lbl-1000
            }
            catch (Throwable var6_14) {
                var7_7 = null;
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (IOException e) {
                        // empty catch block
                    }
                }
                throw var6_14;
            }
lbl-1000:
            // 1 sources

            {
                try {
                    is.close();
                }
                catch (IOException e) {}
            }
lbl-1000:
            // 2 sources

            {
                break block18;
                catch (SAXException e) {
                    e.printStackTrace();
                    var7_6 = null;
                    if (is != null) {
                        try {
                            is.close();
                        }
                        catch (IOException e) {}
                    }
                    break block18;
                }
                catch (IOException e) {
                    e.printStackTrace();
                    var7_6 = null;
                    if (is != null) {
                        try {
                            is.close();
                        }
                        catch (IOException e) {}
                    }
                }
            }
        }
        root = (Element)doc.getElementsByTagName("propertysets").item(0);
        propertySets = root.getElementsByTagName("propertyset");
        for (i = 0; i < propertySets.getLength(); ++i) {
            propertySet = (Element)propertySets.item(i);
            name = propertySet.getAttribute("name");
            clazz = propertySet.getAttribute("class");
            this.propertySets.put(name, clazz);
            args = propertySet.getElementsByTagName("arg");
            argsMap = new HashMap<String, String>();
            for (j = 0; j < args.getLength(); ++j) {
                arg = (Element)args.item(j);
                argName = arg.getAttribute("name");
                argValue = arg.getAttribute("value");
                argsMap.put(argName, argValue);
            }
            this.propertySetArgs.put(name, argsMap);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PropertySetConfig getConfig() {
        Object object = lock;
        synchronized (object) {
            if (config == null) {
                config = new PropertySetConfig();
            }
            return config;
        }
    }

    public Map getArgs(String name) {
        return (Map)this.propertySetArgs.get(name);
    }

    public String getClassName(String name) {
        return (String)this.propertySets.get(name);
    }

    private InputStream load() throws IllegalArgumentException {
        InputStream is = null;
        for (int i = 0; i < CONFIG_LOCATIONS.length; ++i) {
            String location = CONFIG_LOCATIONS[i];
            try {
                is = ClassLoaderUtil.getResourceAsStream((String)location, this.getClass());
                if (is == null) continue;
                return is;
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if (is == null) {
            String exceptionMessage = "Could not load propertyset config using '" + CONFIG_LOCATIONS + "'.  Please check your classpath.";
            log.fatal((Object)exceptionMessage);
            throw new IllegalArgumentException(exceptionMessage);
        }
        return is;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        lock = new Object();
        log = LogFactory.getLog((Class)(class$com$opensymphony$module$propertyset$config$PropertySetConfig == null ? (class$com$opensymphony$module$propertyset$config$PropertySetConfig = PropertySetConfig.class$("com.opensymphony.module.propertyset.config.PropertySetConfig")) : class$com$opensymphony$module$propertyset$config$PropertySetConfig));
        CONFIG_LOCATIONS = new String[]{"propertyset.xml", "/propertyset.xml", "META-INF/propertyset.xml", "/META-INF/propertyset.xml", "META-INF/propertyset-default.xml", "/META-INF/propertyset-default.xml"};
    }
}

