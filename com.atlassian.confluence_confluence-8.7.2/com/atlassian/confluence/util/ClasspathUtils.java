/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  io.github.classgraph.ClassGraph
 *  org.apache.commons.io.FilenameUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.classpath.ClasspathClasses;
import com.google.common.annotations.VisibleForTesting;
import io.github.classgraph.ClassGraph;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasspathUtils {
    private static final Logger logger = LoggerFactory.getLogger(ClasspathUtils.class);

    @Deprecated
    public static URL[] getSystemClasspath() {
        return new ClassGraph().enableClassInfo().getClasspathURLs().toArray(new URL[0]);
    }

    public static URL[] getThreadContextClasspath() {
        ClassGraph classGraph = new ClassGraph().enableClassInfo();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            classGraph = classGraph.overrideClassLoaders(new ClassLoader[]{classLoader});
        }
        return classGraph.getClasspathURLs().toArray(new URL[0]);
    }

    public static List<ClassLoader> getThreadContentClassLoaderHierarchy() {
        LinkedList<ClassLoader> classLoaders = new LinkedList<ClassLoader>();
        for (ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); classLoader != null; classLoader = classLoader.getParent()) {
            classLoaders.add(classLoader);
        }
        return classLoaders;
    }

    public static List<URL> getClassLoaderClasspath(ClassLoader classloader) {
        if (!(classloader instanceof URLClassLoader)) {
            return null;
        }
        return Arrays.asList(((URLClassLoader)classloader).getURLs());
    }

    public static List<String> getClassesFromJar(URL jarUrl) {
        return ClasspathUtils.getFilesFromJar(jarUrl, true);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static List<String> getFilesFromJar(URL jarUrl, boolean onlyAddClasses) {
        try (InputStream urlStream = jarUrl.openStream();){
            LinkedList<String> linkedList;
            try (JarInputStream jar = new JarInputStream(urlStream);){
                LinkedList<String> result = new LinkedList<String>();
                ZipEntry entry = jar.getNextEntry();
                while (entry != null) {
                    String name = entry.getName();
                    if (onlyAddClasses) {
                        if (name.endsWith(".class")) {
                            result.add(name);
                        }
                    } else {
                        result.add(name);
                    }
                    entry = jar.getNextEntry();
                }
                linkedList = result;
            }
            return linkedList;
        }
        catch (IOException e) {
            return null;
        }
    }

    public static ClasspathClasses getClassesInClasspathJars() {
        URL[] jarUrls = ClasspathUtils.getThreadContextClasspath();
        if (jarUrls == null) {
            return null;
        }
        ClasspathClasses result = new ClasspathClasses();
        for (URL jarUrl : jarUrls) {
            List<String> jarClassFileNames = ClasspathUtils.getClassesFromJar(jarUrl);
            if (jarClassFileNames == null) continue;
            result.addAll(jarUrl, jarClassFileNames);
        }
        return result;
    }

    public static ClasspathClasses getFilesInClasspathJars() {
        URL[] jarUrls = ClasspathUtils.getThreadContextClasspath();
        if (jarUrls == null) {
            return null;
        }
        ClasspathClasses result = new ClasspathClasses();
        for (URL jarUrl : jarUrls) {
            List<String> jarClassFileNames = ClasspathUtils.getFilesFromJar(jarUrl, false);
            if (jarClassFileNames == null) continue;
            result.addAll(jarUrl, jarClassFileNames);
        }
        return result;
    }

    public static Optional<File> getJarFileFromClass(Class clazz) {
        if (clazz == null) {
            return Optional.empty();
        }
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return Optional.empty();
        }
        return ClasspathUtils.computePathFromCodeSource(codeSource, clazz.getName());
    }

    @VisibleForTesting
    static Optional<File> computePathFromCodeSource(CodeSource codeSource, String clazzName) {
        String badSuffix;
        String filePath = codeSource.getLocation().getPath();
        if (filePath.endsWith(badSuffix = "!/" + clazzName.replace(".", "/") + ".class")) {
            filePath = filePath.substring(0, filePath.indexOf(badSuffix));
        }
        filePath = FilenameUtils.normalize((String)filePath);
        logger.debug("Computed [{}] as the path to the driver", (Object)filePath);
        return StringUtils.isNotBlank((CharSequence)filePath) ? Optional.of(new File(filePath)) : Optional.empty();
    }
}

