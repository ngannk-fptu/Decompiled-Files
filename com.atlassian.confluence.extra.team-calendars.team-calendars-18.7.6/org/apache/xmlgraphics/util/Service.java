/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package org.apache.xmlgraphics.util;

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
import java.util.Map;
import org.apache.commons.io.IOUtils;

public final class Service {
    static Map<String, List<String>> classMap = new HashMap<String, List<String>>();
    static Map<String, List<Object>> instanceMap = new HashMap<String, List<Object>>();

    private Service() {
    }

    public static synchronized Iterator<Object> providers(Class<?> cls) {
        String serviceFile = Service.getServiceFilename(cls);
        List<Object> l = instanceMap.get(serviceFile);
        if (l != null) {
            return l.iterator();
        }
        l = new ArrayList<Object>();
        instanceMap.put(serviceFile, l);
        ClassLoader cl = Service.getClassLoader(cls);
        if (cl != null) {
            List<String> names = Service.getProviderNames(cls, cl);
            for (String name : names) {
                try {
                    Object obj = cl.loadClass(name).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                    l.add(obj);
                }
                catch (Exception exception) {}
            }
        }
        return l.iterator();
    }

    public static synchronized Iterator<String> providerNames(Class<?> cls) {
        String serviceFile = Service.getServiceFilename(cls);
        List<String> l = classMap.get(serviceFile);
        if (l != null) {
            return l.iterator();
        }
        l = new ArrayList<String>();
        classMap.put(serviceFile, l);
        l.addAll(Service.getProviderNames(cls));
        return l.iterator();
    }

    public static Iterator<?> providers(Class<?> cls, boolean returnInstances) {
        return returnInstances ? Service.providers(cls) : Service.providerNames(cls);
    }

    private static List<String> getProviderNames(Class<?> cls) {
        return Service.getProviderNames(cls, Service.getClassLoader(cls));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static List<String> getProviderNames(Class<?> cls, ClassLoader cl) {
        Enumeration<URL> e;
        ArrayList<String> l = new ArrayList<String>();
        if (cl == null) {
            return l;
        }
        try {
            e = cl.getResources(Service.getServiceFilename(cls));
        }
        catch (IOException ioe) {
            return l;
        }
        while (e.hasMoreElements()) {
            try {
                URL u = e.nextElement();
                InputStream is = u.openStream();
                InputStreamReader r = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(r);
                try {
                    String line = br.readLine();
                    while (line != null) {
                        int idx = line.indexOf(35);
                        if (idx != -1) {
                            line = line.substring(0, idx);
                        }
                        if ((line = line.trim()).length() != 0) {
                            l.add(line);
                        }
                        line = br.readLine();
                    }
                }
                finally {
                    IOUtils.closeQuietly((Reader)br);
                    IOUtils.closeQuietly((InputStream)is);
                }
            }
            catch (Exception exception) {}
        }
        return l;
    }

    private static ClassLoader getClassLoader(Class<?> cls) {
        ClassLoader cl = null;
        try {
            cl = cls.getClassLoader();
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        if (cl == null) {
            cl = Service.class.getClassLoader();
        }
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        return cl;
    }

    private static String getServiceFilename(Class<?> cls) {
        return "META-INF/services/" + cls.getName();
    }
}

