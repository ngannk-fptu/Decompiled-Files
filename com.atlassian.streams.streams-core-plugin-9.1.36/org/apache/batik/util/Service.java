/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

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

public class Service {
    static HashMap providerMap = new HashMap();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static synchronized Iterator providers(Class cls) {
        Enumeration<URL> e;
        String serviceFile = "META-INF/services/" + cls.getName();
        ArrayList l = (ArrayList)providerMap.get(serviceFile);
        if (l != null) {
            return l.iterator();
        }
        l = new ArrayList();
        providerMap.put(serviceFile, l);
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
            return l.iterator();
        }
        try {
            e = cl.getResources(serviceFile);
        }
        catch (IOException ioe) {
            return l.iterator();
        }
        block37: while (e.hasMoreElements()) {
            InputStream is = null;
            Reader r = null;
            BufferedReader br = null;
            try {
                URL u = e.nextElement();
                is = u.openStream();
                r = new InputStreamReader(is, "UTF-8");
                br = new BufferedReader(r);
                String line = br.readLine();
                while (true) {
                    if (line == null) continue block37;
                    try {
                        int idx = line.indexOf(35);
                        if (idx != -1) {
                            line = line.substring(0, idx);
                        }
                        if ((line = line.trim()).length() == 0) {
                            line = br.readLine();
                            continue;
                        }
                        Object obj = cl.loadClass(line).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                        l.add(obj);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    line = br.readLine();
                }
            }
            catch (Exception exception) {
                continue;
            }
            catch (LinkageError linkageError) {
                continue;
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (IOException iOException) {}
                    is = null;
                }
                if (r != null) {
                    try {
                        r.close();
                    }
                    catch (IOException iOException) {}
                    r = null;
                }
                if (br == null) continue;
                try {
                    br.close();
                }
                catch (IOException iOException) {}
                br = null;
                continue;
            }
            break;
        }
        return l.iterator();
    }
}

