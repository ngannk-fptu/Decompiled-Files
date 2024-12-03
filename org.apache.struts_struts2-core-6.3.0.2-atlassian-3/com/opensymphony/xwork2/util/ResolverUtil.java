/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResolverUtil<T> {
    private static final Logger LOG = LogManager.getLogger(ResolverUtil.class);
    private Set<Class<? extends T>> classMatches = new HashSet<Class<? extends T>>();
    private Set<URL> resourceMatches = new HashSet<URL>();
    private ClassLoader classloader;

    public Set<Class<? extends T>> getClasses() {
        return this.classMatches;
    }

    public Set<URL> getResources() {
        return this.resourceMatches;
    }

    public ClassLoader getClassLoader() {
        return this.classloader == null ? Thread.currentThread().getContextClassLoader() : this.classloader;
    }

    public void setClassLoader(ClassLoader classloader) {
        this.classloader = classloader;
    }

    public void findImplementations(Class parent, String ... packageNames) {
        if (packageNames == null) {
            return;
        }
        IsA test = new IsA(parent);
        for (String pkg : packageNames) {
            this.findInPackage(test, pkg);
        }
    }

    public void findSuffix(String suffix, String ... packageNames) {
        if (packageNames == null) {
            return;
        }
        NameEndsWith test = new NameEndsWith(suffix);
        for (String pkg : packageNames) {
            this.findInPackage(test, pkg);
        }
    }

    public void findAnnotated(Class<? extends Annotation> annotation, String ... packageNames) {
        if (packageNames == null) {
            return;
        }
        AnnotatedWith test = new AnnotatedWith(annotation);
        for (String pkg : packageNames) {
            this.findInPackage(test, pkg);
        }
    }

    public void findNamedResource(String name, String ... pathNames) {
        if (pathNames == null) {
            return;
        }
        NameIs test = new NameIs(name);
        for (String pkg : pathNames) {
            this.findInPackage(test, pkg);
        }
    }

    public void find(Test test, String ... packageNames) {
        if (packageNames == null) {
            return;
        }
        for (String pkg : packageNames) {
            this.findInPackage(test, pkg);
        }
    }

    public void findInPackage(Test test, String packageName) {
        Enumeration<URL> urls;
        packageName = packageName.replace('.', '/');
        ClassLoader loader = this.getClassLoader();
        try {
            urls = loader.getResources(packageName);
        }
        catch (IOException ioe) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Could not read package: " + packageName, (Throwable)ioe);
            }
            return;
        }
        while (urls.hasMoreElements()) {
            try {
                File file;
                String urlPath = urls.nextElement().getFile();
                urlPath = URLDecoder.decode(urlPath, "UTF-8");
                if (urlPath.startsWith("file:")) {
                    urlPath = urlPath.substring(5);
                }
                if (urlPath.indexOf(33) > 0) {
                    urlPath = urlPath.substring(0, urlPath.indexOf(33));
                }
                if (LOG.isInfoEnabled()) {
                    LOG.info("Scanning for classes in [" + urlPath + "] matching criteria: " + test);
                }
                if ((file = new File(urlPath)).isDirectory()) {
                    this.loadImplementationsInDirectory(test, packageName, file);
                    continue;
                }
                this.loadImplementationsInJar(test, packageName, file);
            }
            catch (IOException ioe) {
                if (!LOG.isWarnEnabled()) continue;
                LOG.warn("could not read entries", (Throwable)ioe);
            }
        }
    }

    private void loadImplementationsInDirectory(Test test, String parent, File location) {
        File[] files = location.listFiles();
        StringBuilder builder = null;
        for (File file : files) {
            String packageOrClass;
            builder = new StringBuilder(100);
            builder.append(parent).append("/").append(file.getName());
            String string = packageOrClass = parent == null ? file.getName() : builder.toString();
            if (file.isDirectory()) {
                this.loadImplementationsInDirectory(test, packageOrClass, file);
                continue;
            }
            if (!this.isTestApplicable(test, file.getName())) continue;
            this.addIfMatching(test, packageOrClass);
        }
    }

    private boolean isTestApplicable(Test test, String path) {
        return test.doesMatchResource() || path.endsWith(".class") && test.doesMatchClass();
    }

    private void loadImplementationsInJar(Test test, String parent, File jarfile) {
        try (JarInputStream jarStream = new JarInputStream(new FileInputStream(jarfile));){
            JarEntry entry;
            while ((entry = jarStream.getNextJarEntry()) != null) {
                String name = entry.getName();
                if (entry.isDirectory() || !name.startsWith(parent) || !this.isTestApplicable(test, name)) continue;
                this.addIfMatching(test, name);
            }
        }
        catch (IOException ioe) {
            LOG.error("Could not search jar file '" + jarfile + "' for classes matching criteria: " + test + " due to an IOException", (Throwable)ioe);
        }
    }

    protected void addIfMatching(Test test, String fqn) {
        block8: {
            try {
                ClassLoader loader = this.getClassLoader();
                if (test.doesMatchClass()) {
                    Class<?> type;
                    String externalName = fqn.substring(0, fqn.indexOf(46)).replace('/', '.');
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Checking to see if class " + externalName + " matches criteria [" + test + "]");
                    }
                    if (test.matches(type = loader.loadClass(externalName))) {
                        this.classMatches.add(type);
                    }
                }
                if (test.doesMatchResource()) {
                    URL url = loader.getResource(fqn);
                    if (url == null) {
                        url = loader.getResource(fqn.substring(1));
                    }
                    if (url != null && test.matches(url)) {
                        this.resourceMatches.add(url);
                    }
                }
            }
            catch (Throwable t) {
                if (!LOG.isWarnEnabled()) break block8;
                LOG.warn("Could not examine class '" + fqn + "' due to a " + t.getClass().getName() + " with message: " + t.getMessage());
            }
        }
    }

    public static class NameIs
    extends ResourceTest {
        private String name;

        public NameIs(String name) {
            this.name = "/" + name;
        }

        @Override
        public boolean matches(URL resource) {
            return resource.getPath().endsWith(this.name);
        }

        public String toString() {
            return "named " + this.name;
        }
    }

    public static class AnnotatedWith
    extends ClassTest {
        private Class<? extends Annotation> annotation;

        public AnnotatedWith(Class<? extends Annotation> annotation) {
            this.annotation = annotation;
        }

        @Override
        public boolean matches(Class type) {
            return type != null && type.isAnnotationPresent(this.annotation);
        }

        public String toString() {
            return "annotated with @" + this.annotation.getSimpleName();
        }
    }

    public static class NameEndsWith
    extends ClassTest {
        private String suffix;

        public NameEndsWith(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public boolean matches(Class type) {
            return type != null && type.getName().endsWith(this.suffix);
        }

        public String toString() {
            return "ends with the suffix " + this.suffix;
        }
    }

    public static class IsA
    extends ClassTest {
        private Class parent;

        public IsA(Class parentType) {
            this.parent = parentType;
        }

        @Override
        public boolean matches(Class type) {
            return type != null && this.parent.isAssignableFrom(type);
        }

        public String toString() {
            return "is assignable to " + this.parent.getSimpleName();
        }
    }

    public static abstract class ResourceTest
    implements Test {
        @Override
        public boolean matches(Class cls) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean doesMatchClass() {
            return false;
        }

        @Override
        public boolean doesMatchResource() {
            return true;
        }
    }

    public static abstract class ClassTest
    implements Test {
        @Override
        public boolean matches(URL resource) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean doesMatchClass() {
            return true;
        }

        @Override
        public boolean doesMatchResource() {
            return false;
        }
    }

    public static interface Test {
        public boolean matches(Class var1);

        public boolean matches(URL var1);

        public boolean doesMatchClass();

        public boolean doesMatchResource();
    }
}

