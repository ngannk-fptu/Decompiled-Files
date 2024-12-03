/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public final class ClasspathResource {
    private final Map contentMappings = new HashMap();
    private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static ClasspathResource classpathResource;

    private ClasspathResource() {
        this.loadManifests();
    }

    public static synchronized ClasspathResource getInstance() {
        if (classpathResource == null) {
            classpathResource = new ClasspathResource();
        }
        return classpathResource;
    }

    private Set getClassLoadersForResources() {
        ClassLoader l2;
        HashSet<ClassLoader> v = new HashSet<ClassLoader>();
        try {
            l2 = ClassLoader.getSystemClassLoader();
            if (l2 != null) {
                v.add(l2);
            }
        }
        catch (SecurityException l2) {
            // empty catch block
        }
        try {
            l2 = Thread.currentThread().getContextClassLoader();
            if (l2 != null) {
                v.add(l2);
            }
        }
        catch (SecurityException l3) {
            // empty catch block
        }
        try {
            l2 = ClasspathResource.class.getClassLoader();
            if (l2 != null) {
                v.add(l2);
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        return v;
    }

    private void loadManifests() {
        try {
            for (Object o1 : this.getClassLoadersForResources()) {
                ClassLoader classLoader = (ClassLoader)o1;
                Enumeration<URL> e = classLoader.getResources(MANIFEST_PATH);
                while (e.hasMoreElements()) {
                    URL u = e.nextElement();
                    try {
                        Manifest manifest = new Manifest(u.openStream());
                        Map<String, Attributes> entries = manifest.getEntries();
                        Iterator<Map.Entry<String, Attributes>> iterator = entries.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<String, Attributes> o;
                            Map.Entry<String, Attributes> entry = o = iterator.next();
                            String name = entry.getKey();
                            Attributes attributes = entry.getValue();
                            String contentType = attributes.getValue(CONTENT_TYPE_KEY);
                            if (contentType == null) continue;
                            this.addToMapping(contentType, name, classLoader);
                        }
                    }
                    catch (IOException iOException) {
                    }
                }
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private void addToMapping(String contentType, String name, ClassLoader classLoader) {
        URL url;
        Vector<URL> existingFiles = (Vector<URL>)this.contentMappings.get(contentType);
        if (existingFiles == null) {
            existingFiles = new Vector<URL>();
            this.contentMappings.put(contentType, existingFiles);
        }
        if ((url = classLoader.getResource(name)) != null) {
            existingFiles.add(url);
        }
    }

    public List listResourcesOfMimeType(String mimeType) {
        List content = (List)this.contentMappings.get(mimeType);
        if (content == null) {
            return Collections.EMPTY_LIST;
        }
        return content;
    }
}

