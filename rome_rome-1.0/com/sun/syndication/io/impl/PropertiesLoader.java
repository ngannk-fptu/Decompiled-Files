/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

public class PropertiesLoader {
    private static final String MASTER_PLUGIN_FILE = "com/sun/syndication/rome.properties";
    private static final String EXTRA_PLUGIN_FILE = "rome.properties";
    private static Map clMap = new WeakHashMap();
    private Properties[] _properties;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PropertiesLoader getPropertiesLoader() {
        Class clazz = PropertiesLoader.class;
        synchronized (clazz) {
            PropertiesLoader loader = (PropertiesLoader)clMap.get(Thread.currentThread().getContextClassLoader());
            if (loader == null) {
                try {
                    loader = new PropertiesLoader(MASTER_PLUGIN_FILE, EXTRA_PLUGIN_FILE);
                    clMap.put(Thread.currentThread().getContextClassLoader(), loader);
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return loader;
        }
    }

    private PropertiesLoader(String masterFileLocation, String extraFileLocation) throws IOException {
        ArrayList<Properties> propertiesList = new ArrayList<Properties>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            InputStream is = classLoader.getResourceAsStream(masterFileLocation);
            Properties p = new Properties();
            p.load(is);
            is.close();
            propertiesList.add(p);
        }
        catch (IOException ioex) {
            IOException ex = new IOException("could not load ROME master plugins file [" + masterFileLocation + "], " + ioex.getMessage());
            ex.setStackTrace(ioex.getStackTrace());
            throw ex;
        }
        Enumeration<URL> urls = classLoader.getResources(extraFileLocation);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            Properties p = new Properties();
            try {
                InputStream is = url.openStream();
                p.load(is);
                is.close();
            }
            catch (IOException ioex) {
                IOException ex = new IOException("could not load ROME extensions plugins file [" + url.toString() + "], " + ioex.getMessage());
                ex.setStackTrace(ioex.getStackTrace());
                throw ex;
            }
            propertiesList.add(p);
        }
        this._properties = new Properties[propertiesList.size()];
        propertiesList.toArray(this._properties);
    }

    public String[] getTokenizedProperty(String key, String separator) {
        ArrayList<String> entriesList = new ArrayList<String>();
        for (int i = 0; i < this._properties.length; ++i) {
            String values = this._properties[i].getProperty(key);
            if (values == null) continue;
            StringTokenizer st = new StringTokenizer(values, separator);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                entriesList.add(token);
            }
        }
        String[] entries = new String[entriesList.size()];
        entriesList.toArray(entries);
        return entries;
    }

    public String[] getProperty(String key) {
        ArrayList<String> entriesList = new ArrayList<String>();
        for (int i = 0; i < this._properties.length; ++i) {
            String values = this._properties[i].getProperty(key);
            if (values == null) continue;
            entriesList.add(values);
        }
        String[] entries = new String[entriesList.size()];
        entriesList.toArray(entries);
        return entries;
    }
}

