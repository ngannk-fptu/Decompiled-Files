/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.tools.GrapeUtil;
import org.codehaus.groovy.tools.LoaderConfiguration;

public class RootLoader
extends URLClassLoader {
    private static final URL[] EMPTY_URL_ARRAY = new URL[0];
    private Map<String, Class> customClasses = new HashMap<String, Class>();

    private RootLoader(ClassLoader parent) {
        this(EMPTY_URL_ARRAY, parent);
    }

    public RootLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        try {
            this.customClasses.put("org.w3c.dom.Node", super.loadClass("org.w3c.dom.Node", false));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static ClassLoader chooseParent() {
        ClassLoader cl = RootLoader.class.getClassLoader();
        if (cl != null) {
            return cl;
        }
        return ClassLoader.getSystemClassLoader();
    }

    public RootLoader(LoaderConfiguration lc) {
        this(RootLoader.chooseParent());
        URL[] urls;
        Thread.currentThread().setContextClassLoader(this);
        for (URL url : urls = lc.getClassPathUrls()) {
            this.addURL(url);
        }
        String groovyHome = System.getProperty("groovy.home");
        List<String> grabUrls = lc.getGrabUrls();
        for (String grabUrl : grabUrls) {
            Map<String, Object> grabParts = GrapeUtil.getIvyParts(grabUrl);
            String group = grabParts.get("group").toString();
            String module = grabParts.get("module").toString();
            String name = grabParts.get("module").toString() + "-" + grabParts.get("version") + ".jar";
            File jar = new File(groovyHome + "/repo/" + group + "/" + module + "/jars/" + name);
            try {
                this.addURL(jar.toURI().toURL());
            }
            catch (MalformedURLException malformedURLException) {}
        }
    }

    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c = this.findLoadedClass(name);
        if (c != null) {
            return c;
        }
        c = this.customClasses.get(name);
        if (c != null) {
            return c;
        }
        try {
            c = this.oldFindClass(name);
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        if (c == null) {
            c = super.loadClass(name, resolve);
        }
        if (resolve) {
            this.resolveClass(c);
        }
        return c;
    }

    @Override
    public URL getResource(String name) {
        URL url = this.findResource(name);
        if (url == null) {
            url = super.getResource(name);
        }
        return url;
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    private Class oldFindClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    protected Class findClass(String name) throws ClassNotFoundException {
        throw new ClassNotFoundException(name);
    }
}

