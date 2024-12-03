/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.impl.ConfigurableClassLoader;
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
    private static final String MASTER_PLUGIN_FILE = "com/rometools/rome/rome.properties";
    private static final String EXTRA_PLUGIN_FILE = "rome.properties";
    private static Map<ClassLoader, PropertiesLoader> clMap = new WeakHashMap<ClassLoader, PropertiesLoader>();
    private final Properties[] properties;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PropertiesLoader getPropertiesLoader() {
        Class<PropertiesLoader> clazz = PropertiesLoader.class;
        synchronized (PropertiesLoader.class) {
            ClassLoader classLoader = ConfigurableClassLoader.INSTANCE.getClassLoader();
            PropertiesLoader loader = clMap.get(classLoader);
            if (loader == null) {
                try {
                    loader = new PropertiesLoader(MASTER_PLUGIN_FILE, EXTRA_PLUGIN_FILE);
                    clMap.put(classLoader, loader);
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return loader;
        }
    }

    private PropertiesLoader(String masterFileLocation, String extraFileLocation) throws IOException {
        ArrayList<Properties> propertiesList = new ArrayList<Properties>();
        ClassLoader classLoader = ConfigurableClassLoader.INSTANCE.getClassLoader();
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
        this.properties = new Properties[propertiesList.size()];
        propertiesList.toArray(this.properties);
    }

    public String[] getTokenizedProperty(String key, String separator) {
        ArrayList<String> entriesList = new ArrayList<String>();
        for (Properties property : this.properties) {
            String values = property.getProperty(key);
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
        for (Properties property : this.properties) {
            String values = property.getProperty(key);
            if (values == null) continue;
            entriesList.add(values);
        }
        String[] entries = new String[entriesList.size()];
        entriesList.toArray(entries);
        return entries;
    }
}

