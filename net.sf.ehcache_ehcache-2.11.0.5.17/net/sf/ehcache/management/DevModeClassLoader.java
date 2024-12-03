/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.management;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.ehcache.util.MergedEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevModeClassLoader
extends ClassLoader {
    private static final String DEVMODE_OS_DEPENDENCIES_RESOURCE = "/META-INF/devmode/net.sf.ehcache.internal/ehcache-rest-agent/dependencies.txt";
    private static final String DEVMODE_EE_DEPENDENCIES_RESOURCE = "/META-INF/devmode/net.sf.ehcache.internal/ehcache-ee-rest-agent/dependencies.txt";
    private static final Pattern ARTIFACT_PATTERN = Pattern.compile("^\\s*([^:]+):([^:]+):([^:]+):([^:]+):(.+)$");
    private static final Logger LOG = LoggerFactory.getLogger(DevModeClassLoader.class);
    private final URLClassLoader urlClassLoader;

    public DevModeClassLoader(URL depsReource, ClassLoader parent) {
        super(parent);
        this.urlClassLoader = this.initUrlClassLoader(depsReource);
    }

    public static URL devModeResource() {
        URL url = DevModeClassLoader.class.getResource(DEVMODE_EE_DEPENDENCIES_RESOURCE);
        if (url != null) {
            return url;
        }
        url = DevModeClassLoader.class.getResource(DEVMODE_OS_DEPENDENCIES_RESOURCE);
        if (url != null) {
            return url;
        }
        return null;
    }

    private URLClassLoader initUrlClassLoader(URL depsReource) {
        ArrayList<URL> urlList = new ArrayList<URL>();
        InputStream in = null;
        try {
            in = depsReource.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String scope;
                Matcher m = ARTIFACT_PATTERN.matcher(line);
                if (!m.matches() || !"compile".equals(scope = m.group(5)) && !"runtime".equals(scope)) continue;
                URL url = this.constructMavenLocalFile(m.group(1), m.group(2), m.group(3), m.group(4));
                LOG.debug("devmode jar: " + url);
                urlList.add(url);
            }
            URLClassLoader uRLClassLoader = new URLClassLoader(urlList.toArray(new URL[0]), this.getParent());
            return uRLClassLoader;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    private URL constructMavenLocalFile(String groupId, String artifactId, String type, String version) {
        File artifact;
        Object base = System.getProperty("localMavenRepository");
        if (base == null) {
            base = System.getProperty("user.home") + "/.m2/repository";
        }
        if (!(artifact = new File((String)base, groupId.replace('.', '/') + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + "." + type)).exists()) {
            throw new AssertionError((Object)("Can't find Maven artifact: " + groupId + ":" + artifactId + ":" + type + ":" + version));
        }
        try {
            return artifact.toURI().toURL();
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.startsWith("java.")) {
            return this.getParent().loadClass(name);
        }
        Class<?> c = this.findLoadedClass(name);
        if (c == null) {
            try {
                c = this.findClass(name);
            }
            catch (ClassNotFoundException e) {
                c = this.getParent().loadClass(name);
            }
        }
        if (resolve) {
            this.resolveClass(c);
        }
        return c;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return this.urlClassLoader.loadClass(name);
    }

    @Override
    public URL getResource(String name) {
        URL url = this.findResource(name);
        if (url == null) {
            return super.getResource(name);
        }
        return url;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        if (name.startsWith("META-INF/services")) {
            return this.findResources(name);
        }
        Enumeration[] tmp = new Enumeration[]{this.findResources(name), this.getParent().getResources(name)};
        return new MergedEnumeration<URL>(tmp[0], tmp[1]);
    }

    @Override
    protected URL findResource(String name) {
        return this.urlClassLoader.findResource(name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        return this.urlClassLoader.findResources(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        URL resource = this.getResource(name);
        if (resource != null) {
            try {
                return resource.openStream();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}

