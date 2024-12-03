/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.plugin.classloader;

import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.classworlds.uberjar.protocol.jar.NonLockingJarHandler;

public final class PluginClassLoader
extends ClassLoader {
    private static final String PLUGIN_INNER_JAR_PREFIX = "atlassian-plugins-innerjar";
    private final List<File> pluginInnerJars;
    private final Map<String, URL> entryMappings = new HashMap<String, URL>();
    private final File tempDirectory;

    public PluginClassLoader(File pluginFile) {
        this(pluginFile, null);
    }

    public PluginClassLoader(File pluginFile, ClassLoader parent) {
        this(pluginFile, parent, new File(System.getProperty("java.io.tmpdir")));
    }

    public PluginClassLoader(File pluginFile, ClassLoader parent, File tempDirectory) {
        super(parent);
        this.tempDirectory = Objects.requireNonNull(tempDirectory);
        if (!tempDirectory.exists()) {
            throw new IllegalStateException(String.format("Temp directory should exist, %s", tempDirectory));
        }
        try {
            if (pluginFile == null || !pluginFile.exists()) {
                throw new IllegalArgumentException("Plugin jar file must not be null and must exist.");
            }
            this.pluginInnerJars = new ArrayList<File>();
            this.initialiseOuterJar(pluginFile);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initialiseOuterJar(File file) throws IOException {
        try (JarFile jarFile = new JarFile(file);){
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (this.isInnerJarPath(jarEntry.getName())) {
                    this.initialiseInnerJar(jarFile, jarEntry);
                    continue;
                }
                this.addEntryMapping(jarEntry, file, true);
            }
        }
    }

    private boolean isInnerJarPath(String name) {
        return name.startsWith("META-INF/lib/") && name.endsWith(".jar");
    }

    private void initialiseInnerJar(JarFile jarFile, JarEntry jarEntry) throws IOException {
        File innerJarFile = File.createTempFile(PLUGIN_INNER_JAR_PREFIX, ".jar", this.tempDirectory);
        try (InputStream inputStream = jarFile.getInputStream(jarEntry);
             FileOutputStream fileOutputStream = new FileOutputStream(innerJarFile);){
            IOUtils.copy((InputStream)inputStream, (OutputStream)fileOutputStream);
            try (JarFile innerJarJarFile = new JarFile(innerJarFile);){
                Enumeration<JarEntry> entries = innerJarJarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry innerJarEntry = entries.nextElement();
                    this.addEntryMapping(innerJarEntry, innerJarFile, false);
                }
            }
            this.pluginInnerJars.add(innerJarFile);
        }
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c = this.findLoadedClass(name);
        if (c != null) {
            return c;
        }
        String path = name.replace('.', '/').concat(".class");
        if (this.isEntryInPlugin(path)) {
            try {
                return this.loadClassFromPlugin(name, path);
            }
            catch (IOException e) {
                throw new ClassNotFoundException("Unable to load class [ " + name + " ] from PluginClassLoader", e);
            }
        }
        return super.loadClass(name, resolve);
    }

    @Override
    public URL getResource(String name) {
        if (this.isEntryInPlugin(name)) {
            return this.entryMappings.get(name);
        }
        return super.getResource(name);
    }

    public URL getLocalResource(String name) {
        if (this.isEntryInPlugin(name)) {
            return this.getResource(name);
        }
        return null;
    }

    public void close() {
        for (File pluginInnerJar : this.pluginInnerJars) {
            FileUtils.deleteQuietly((File)pluginInnerJar);
        }
    }

    @VisibleForTesting
    public List<File> getPluginInnerJars() {
        return new ArrayList<File>(this.pluginInnerJars);
    }

    private void initializePackage(String className) {
        String pkgname;
        Package pkg;
        int i = className.lastIndexOf(46);
        if (i != -1 && (pkg = this.getPackage(pkgname = className.substring(0, i))) == null) {
            this.definePackage(pkgname, null, null, null, null, null, null, null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Class<?> loadClassFromPlugin(String className, String path) throws IOException {
        Class<?> clazz;
        InputStream inputStream = null;
        try {
            URL resourceURL = this.entryMappings.get(path);
            inputStream = resourceURL.openStream();
            byte[] bytez = IOUtils.toByteArray((InputStream)inputStream);
            this.initializePackage(className);
            clazz = this.defineClass(className, bytez, 0, bytez.length);
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(inputStream);
            throw throwable;
        }
        IOUtils.closeQuietly((InputStream)inputStream);
        return clazz;
    }

    private URL getUrlOfResourceInJar(String name, File jarFile) {
        try {
            return new URL(new URL("jar:file:" + jarFile.getAbsolutePath() + "!/"), name, NonLockingJarHandler.getInstance());
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isEntryInPlugin(String name) {
        return this.entryMappings.containsKey(name);
    }

    private void addEntryMapping(JarEntry jarEntry, File jarFile, boolean overrideExistingEntries) {
        if (overrideExistingEntries) {
            this.addEntryUrl(jarEntry, jarFile);
        } else if (!this.entryMappings.containsKey(jarEntry.getName())) {
            this.addEntryUrl(jarEntry, jarFile);
        }
    }

    private void addEntryUrl(JarEntry jarEntry, File jarFile) {
        this.entryMappings.put(jarEntry.getName(), this.getUrlOfResourceInJar(jarEntry.getName(), jarFile));
    }
}

