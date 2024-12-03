/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ImportInfo {
    static final String defaultFile = "META-INF/imports/script.txt";
    static String importFile = "META-INF/imports/script.txt";
    static ImportInfo defaultImports;
    protected Set classes = new HashSet();
    protected Set packages = new HashSet();
    static final String classStr = "class";
    static final String packageStr = "package";

    public static ImportInfo getImports() {
        if (defaultImports == null) {
            defaultImports = ImportInfo.readImports();
        }
        return defaultImports;
    }

    static ImportInfo readImports() {
        Enumeration<URL> e;
        ImportInfo ret = new ImportInfo();
        ClassLoader cl = ImportInfo.class.getClassLoader();
        if (cl == null) {
            return ret;
        }
        try {
            e = cl.getResources(importFile);
        }
        catch (IOException ioe) {
            return ret;
        }
        while (e.hasMoreElements()) {
            try {
                URL url = e.nextElement();
                ret.addImports(url);
            }
            catch (Exception exception) {}
        }
        return ret;
    }

    public Iterator getClasses() {
        return Collections.unmodifiableSet(this.classes).iterator();
    }

    public Iterator getPackages() {
        return Collections.unmodifiableSet(this.packages).iterator();
    }

    public void addClass(String cls) {
        this.classes.add(cls);
    }

    public void addPackage(String pkg) {
        this.packages.add(pkg);
    }

    public boolean removeClass(String cls) {
        return this.classes.remove(cls);
    }

    public boolean removePackage(String pkg) {
        return this.packages.remove(pkg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addImports(URL src) throws IOException {
        InputStream is = null;
        InputStreamReader r = null;
        BufferedReader br = null;
        try {
            String line;
            is = src.openStream();
            r = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(r);
            while ((line = br.readLine()) != null) {
                int idx = line.indexOf(35);
                if (idx != -1) {
                    line = line.substring(0, idx);
                }
                if ((line = line.trim()).length() == 0 || (idx = line.indexOf(32)) == -1) continue;
                String prefix = line.substring(0, idx);
                line = line.substring(idx + 1);
                boolean isPackage = packageStr.equals(prefix);
                boolean isClass = classStr.equals(prefix);
                if (!isPackage && !isClass) continue;
                while (line.length() != 0) {
                    String id;
                    idx = line.indexOf(32);
                    if (idx == -1) {
                        id = line;
                        line = "";
                    } else {
                        id = line.substring(0, idx);
                        line = line.substring(idx + 1);
                    }
                    if (id.length() == 0) continue;
                    if (isClass) {
                        this.addClass(id);
                        continue;
                    }
                    this.addPackage(id);
                }
            }
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
                    ((Reader)r).close();
                }
                catch (IOException iOException) {}
                r = null;
            }
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException iOException) {}
                br = null;
            }
        }
    }

    static {
        try {
            importFile = System.getProperty("org.apache.batik.script.imports", defaultFile);
        }
        catch (SecurityException securityException) {
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        defaultImports = null;
    }
}

