/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.fromJava;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class Namespaces
extends HashMap {
    private int prefixCount = 1;
    private HashMap namespacePrefixMap = new HashMap();

    public String getCreate(String key) {
        Object value = super.get(key);
        if (value == null) {
            value = Namespaces.makeNamespaceFromPackageName(key);
            this.put(key, value, null);
        }
        return (String)value;
    }

    public String getCreate(String key, String prefix) {
        Object value = super.get(key);
        if (value == null) {
            value = Namespaces.makeNamespaceFromPackageName(key);
            this.put(key, value, prefix);
        }
        return (String)value;
    }

    public Object put(Object key, Object value, String prefix) {
        if (prefix != null) {
            this.namespacePrefixMap.put(value, prefix);
        } else {
            this.getCreatePrefix((String)value);
        }
        return super.put(key, value);
    }

    public void putAll(Map map) {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = i.next();
            this.put(entry.getKey(), entry.getValue(), null);
        }
    }

    public String getCreatePrefix(String namespace) {
        if (this.namespacePrefixMap.get(namespace) == null) {
            this.namespacePrefixMap.put(namespace, "tns" + this.prefixCount++);
        }
        return (String)this.namespacePrefixMap.get(namespace);
    }

    public void putPrefix(String namespace, String prefix) {
        this.namespacePrefixMap.put(namespace, prefix);
    }

    public void putAllPrefix(Map map) {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = i.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public static String makeNamespace(String clsName) {
        return Namespaces.makeNamespace(clsName, "http");
    }

    public static String makeNamespace(String clsName, String protocol) {
        if (clsName.startsWith("[L")) {
            clsName = clsName.substring(2, clsName.length() - 1);
        }
        if (clsName.lastIndexOf(46) == -1) {
            return protocol + "://" + "DefaultNamespace";
        }
        String packageName = clsName.substring(0, clsName.lastIndexOf(46));
        return Namespaces.makeNamespaceFromPackageName(packageName, protocol);
    }

    public static String getPackage(String namespace) {
        try {
            URL url = new URL(namespace);
            StringTokenizer st = new StringTokenizer(url.getHost(), ".");
            String[] words = new String[st.countTokens()];
            for (int i = 0; i < words.length; ++i) {
                words[i] = st.nextToken();
            }
            StringBuffer sb = new StringBuffer(80);
            for (int i = words.length - 1; i >= 0; --i) {
                String word = words[i];
                if (i != words.length - 1) {
                    sb.append('.');
                }
                sb.append(word);
            }
            String pkg = sb.toString();
            if (pkg.equals("DefaultNamespace")) {
                return "";
            }
            return pkg;
        }
        catch (MalformedURLException malformedURLException) {
            return null;
        }
    }

    private static String makeNamespaceFromPackageName(String packageName) {
        return Namespaces.makeNamespaceFromPackageName(packageName, "http");
    }

    private static String makeNamespaceFromPackageName(String packageName, String protocol) {
        if (packageName == null || packageName.equals("")) {
            return protocol + "://" + "DefaultNamespace";
        }
        StringTokenizer st = new StringTokenizer(packageName, ".");
        String[] words = new String[st.countTokens()];
        for (int i = 0; i < words.length; ++i) {
            words[i] = st.nextToken();
        }
        StringBuffer sb = new StringBuffer(80);
        for (int i = words.length - 1; i >= 0; --i) {
            String word = words[i];
            if (i != words.length - 1) {
                sb.append('.');
            }
            sb.append(word);
        }
        return protocol + "://" + sb.toString();
    }

    public Iterator getNamespaces() {
        return this.namespacePrefixMap.keySet().iterator();
    }
}

