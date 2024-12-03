/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl.util;

import com.sun.media.jai.codecimpl.util.JaiI18N;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PropertyUtil {
    private static Hashtable bundles = new Hashtable();
    private static String propertiesDir = "com/sun/media/jai/codec";
    static /* synthetic */ Class class$com$sun$media$jai$codecimpl$util$PropertyUtil;

    public static InputStream getFileFromClasspath(String path) throws IOException, FileNotFoundException {
        InputStream is;
        String urlHeader;
        final String pathFinal = path;
        final String sep = File.separator;
        String tmpHome = null;
        try {
            tmpHome = System.getProperty("java.home");
        }
        catch (Exception e) {
            tmpHome = null;
        }
        final String home = tmpHome;
        String string = urlHeader = tmpHome == null ? null : home + sep + "lib" + sep;
        if (home != null) {
            String libExtPath = urlHeader + "ext" + sep + path;
            File libExtFile = new File(libExtPath);
            try {
                if (libExtFile.exists() && (is = new FileInputStream(libExtFile)) != null) {
                    return is;
                }
            }
            catch (AccessControlException e) {
                // empty catch block
            }
        }
        if ((is = (class$com$sun$media$jai$codecimpl$util$PropertyUtil == null ? (class$com$sun$media$jai$codecimpl$util$PropertyUtil = PropertyUtil.class$("com.sun.media.jai.codecimpl.util.PropertyUtil")) : class$com$sun$media$jai$codecimpl$util$PropertyUtil).getResourceAsStream("/" + path)) != null) {
            return is;
        }
        PrivilegedAction p = new PrivilegedAction(){

            public Object run() {
                String localHome = null;
                String localUrlHeader = null;
                if (home != null) {
                    localHome = home;
                    localUrlHeader = urlHeader;
                } else {
                    localHome = System.getProperty("java.home");
                    localUrlHeader = localHome + sep + "lib" + sep;
                }
                String[] filenames = new String[]{localUrlHeader + "ext" + sep + "jai_core.jar", localUrlHeader + "ext" + sep + "jai_codec.jar", localUrlHeader + "jai_core.jar", localUrlHeader + "jai_codec.jar"};
                for (int i = 0; i < filenames.length; ++i) {
                    try {
                        InputStream tmpIS = PropertyUtil.getFileFromJar(filenames[i], pathFinal);
                        if (tmpIS == null) continue;
                        return tmpIS;
                    }
                    catch (Exception e) {
                        // empty catch block
                    }
                }
                return null;
            }
        };
        return (InputStream)AccessController.doPrivileged(p);
    }

    private static InputStream getFileFromJar(String jarFilename, String path) throws Exception {
        JarFile f = null;
        try {
            f = new JarFile(jarFilename);
        }
        catch (Exception e) {
            // empty catch block
        }
        JarEntry ent = f.getJarEntry(path);
        if (ent != null) {
            return f.getInputStream(ent);
        }
        return null;
    }

    private static ResourceBundle getBundle(String packageName) {
        PropertyResourceBundle bundle = null;
        InputStream in = null;
        try {
            in = PropertyUtil.getFileFromClasspath(propertiesDir + "/" + packageName + ".properties");
            if (in != null) {
                bundle = new PropertyResourceBundle(in);
                bundles.put(packageName, bundle);
                return bundle;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getString(String packageName, String key) {
        ResourceBundle b = (ResourceBundle)bundles.get(packageName);
        if (b == null) {
            b = PropertyUtil.getBundle(packageName);
        }
        return b.getString(key);
    }

    public static String[] getPropertyNames(String[] propertyNames, String prefix) {
        if (propertyNames == null) {
            return null;
        }
        if (prefix == null) {
            throw new IllegalArgumentException(JaiI18N.getString("PropertyUtil0"));
        }
        prefix = prefix.toLowerCase();
        Vector<String> names = new Vector<String>();
        for (int i = 0; i < propertyNames.length; ++i) {
            if (!propertyNames[i].toLowerCase().startsWith(prefix)) continue;
            names.addElement(propertyNames[i]);
        }
        if (names.size() == 0) {
            return null;
        }
        String[] prefixNames = new String[names.size()];
        int count = 0;
        Iterator it = names.iterator();
        while (it.hasNext()) {
            prefixNames[count++] = (String)it.next();
        }
        return prefixNames;
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

