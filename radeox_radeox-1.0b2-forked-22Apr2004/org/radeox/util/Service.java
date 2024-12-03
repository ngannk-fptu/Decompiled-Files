/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Service {
    static HashMap services = new HashMap();

    public static synchronized Iterator providerClasses(Class cls) {
        return Service.providers(cls, false);
    }

    public static synchronized Iterator providers(Class cls) {
        return Service.providers(cls, true);
    }

    public static synchronized Iterator providers(Class cls, boolean instantiate) {
        ClassLoader classLoader = cls.getClassLoader();
        String providerFile = "META-INF/services/" + cls.getName();
        ArrayList providers = (ArrayList)services.get(providerFile);
        if (providers != null) {
            return providers.iterator();
        }
        providers = new ArrayList();
        services.put(providerFile, providers);
        try {
            Enumeration<URL> providerFiles = classLoader.getResources(providerFile);
            if (providerFiles.hasMoreElements()) {
                while (providerFiles.hasMoreElements()) {
                    try {
                        URL url = providerFiles.nextElement();
                        InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
                        if (instantiate) {
                            Service.loadResource(reader, classLoader, providers);
                            continue;
                        }
                        Service.loadClasses(reader, classLoader, providers);
                    }
                    catch (Exception ex) {}
                }
            } else {
                InputStream is = classLoader.getResourceAsStream(providerFile);
                if (is == null) {
                    providerFile = providerFile.substring(providerFile.lastIndexOf(46) + 1);
                    is = classLoader.getResourceAsStream(providerFile);
                }
                if (is != null) {
                    InputStreamReader reader = new InputStreamReader(is, "UTF-8");
                    Service.loadResource(reader, classLoader, providers);
                }
            }
        }
        catch (IOException ioe) {
            // empty catch block
        }
        return providers.iterator();
    }

    private static List loadClasses(Reader input, ClassLoader classLoader, List classes) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        String line = reader.readLine();
        while (line != null) {
            try {
                int idx = line.indexOf(35);
                if (idx != -1) {
                    line = line.substring(0, idx);
                }
                if ((line = line.trim()).length() > 0) {
                    classes.add(classLoader.loadClass(line));
                }
            }
            catch (Exception ex) {
                // empty catch block
            }
            line = reader.readLine();
        }
        return classes;
    }

    private static void loadResource(Reader input, ClassLoader classLoader, List providers) throws IOException {
        ArrayList classes = new ArrayList();
        Service.loadClasses(input, classLoader, classes);
        Iterator iterator = classes.iterator();
        while (iterator.hasNext()) {
            Class klass = (Class)iterator.next();
            try {
                Object obj = klass.newInstance();
                providers.add(obj);
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

