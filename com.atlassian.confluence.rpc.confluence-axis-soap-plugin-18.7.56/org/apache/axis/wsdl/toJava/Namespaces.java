/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.toJava;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.toJava.Utils;

public class Namespaces
extends HashMap {
    private String root;
    private String defaultPackage = null;
    private static final char[] pkgSeparators = new char[]{'.', ':'};
    private static final char javaPkgSeparator = pkgSeparators[0];
    private Map pkg2NamespacesMap = new HashMap();

    private static String normalizePackageName(String pkg, char separator) {
        for (int i = 0; i < pkgSeparators.length; ++i) {
            pkg = pkg.replace(pkgSeparators[i], separator);
        }
        return pkg;
    }

    public Namespaces(String root) {
        this.root = root;
    }

    private Namespaces(Namespaces clone) {
        super(clone);
        this.root = clone.root;
        this.defaultPackage = clone.defaultPackage;
    }

    public Object clone() {
        return new Namespaces(this);
    }

    public String getCreate(String key) {
        return this.getCreate(key, true);
    }

    String getCreate(String key, boolean create) {
        if (this.defaultPackage != null) {
            this.put(key, this.defaultPackage);
            return this.defaultPackage;
        }
        String value = (String)super.get(key);
        if (value == null && create) {
            value = Namespaces.normalizePackageName(Utils.makePackageName(key), javaPkgSeparator);
            this.put(key, value);
        }
        return value;
    }

    public String getAsDir(String key) {
        if (this.defaultPackage != null) {
            return this.toDir(this.defaultPackage);
        }
        String pkg = (String)this.get(key);
        return this.toDir(pkg);
    }

    public String toDir(String pkg) {
        String dir = null;
        if (pkg != null) {
            pkg = Namespaces.normalizePackageName(pkg, File.separatorChar);
        }
        dir = this.root == null ? pkg : this.root + File.separatorChar + pkg;
        return dir == null ? "" : dir + File.separatorChar;
    }

    public void putAll(Map map) {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = i.next();
            Object key = entry.getKey();
            String pkg = (String)entry.getValue();
            pkg = this.javify(pkg);
            this.put(key, pkg);
        }
    }

    private String javify(String pkg) {
        StringTokenizer st = new StringTokenizer(pkg, ".");
        pkg = "";
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (JavaUtils.isJavaKeyword(token)) {
                token = JavaUtils.makeNonJavaKeyword(token);
            }
            pkg = pkg + token;
            if (!st.hasMoreTokens()) continue;
            pkg = pkg + '.';
        }
        return pkg;
    }

    public void mkdir(String pkg) {
        String pkgDirString = this.toDir(pkg);
        File packageDir = new File(pkgDirString);
        packageDir.mkdirs();
    }

    public void setDefaultPackage(String defaultPackage) {
        this.defaultPackage = defaultPackage;
    }

    public Object put(Object key, Object value) {
        Vector v = null;
        v = !this.pkg2NamespacesMap.containsKey(value) ? new Vector() : (Vector)this.pkg2NamespacesMap.get(value);
        if (!v.contains(key)) {
            v.add(key);
        }
        this.pkg2NamespacesMap.put(value, v);
        return super.put(key, value);
    }

    public Map getPkg2NamespacesMap() {
        return this.pkg2NamespacesMap;
    }
}

