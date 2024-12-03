/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.plugin.osgi.factory.transform.stage;

import com.atlassian.plugin.osgi.factory.transform.JarUtils;
import com.atlassian.plugin.osgi.factory.transform.PluginTransformationException;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.apache.commons.io.IOUtils;

final class TransformStageUtils {
    private static final String CLASS_EXTENSION = ".class";

    private TransformStageUtils() {
    }

    static Set<String> scanJarForItems(JarInputStream inputStream, Set<String> expectedItems, Function<JarEntry, String> mapper) throws IOException {
        JarEntry entry;
        LinkedHashSet<String> matches = new LinkedHashSet<String>();
        while ((entry = inputStream.getNextJarEntry()) != null) {
            String item = (String)mapper.apply((Object)entry);
            if (item == null || !expectedItems.contains(item)) continue;
            matches.add(item);
            if (matches.size() != expectedItems.size()) continue;
            break;
        }
        return Collections.unmodifiableSet(matches);
    }

    static Set<String> scanInnerJars(File pluginFile, Set<String> innerJars, Set<String> expectedClasses) {
        return (Set)JarUtils.withJar(pluginFile, pluginJarFile -> {
            LinkedHashSet<String> matches = new LinkedHashSet<String>();
            for (String innerJar : innerJars) {
                try (JarInputStream innerJarStream = new JarInputStream(pluginJarFile.getInputStream(pluginJarFile.getEntry(innerJar)));){
                    Set<String> innerMatches = TransformStageUtils.scanJarForItems(innerJarStream, expectedClasses, JarEntryToClassName.INSTANCE);
                    matches.addAll(innerMatches);
                }
                catch (IOException ioe) {
                    throw new PluginTransformationException("Error reading inner jar:" + innerJar + " in file: " + pluginFile, ioe);
                }
                if (matches.size() != expectedClasses.size()) continue;
                break;
            }
            return Collections.unmodifiableSet(matches);
        });
    }

    static void closeNestedStreamQuietly(InputStream ... streams) {
        for (InputStream stream : streams) {
            if (stream == null) continue;
            IOUtils.closeQuietly((InputStream)stream);
            break;
        }
    }

    static String getPackageName(String fullClassName) {
        return PackageName.INSTANCE.apply(fullClassName);
    }

    static Set<String> getPackageNames(Iterable<String> classes) {
        return ImmutableSet.copyOf((Iterable)Iterables.transform(classes, (Function)PackageName.INSTANCE));
    }

    static String jarPathToClassName(String jarPath) {
        if (jarPath == null || !jarPath.contains(CLASS_EXTENSION)) {
            return null;
        }
        return jarPath.replaceAll("/", ".").substring(0, jarPath.length() - CLASS_EXTENSION.length());
    }

    static enum JarEntryToClassName implements Function<JarEntry, String>
    {
        INSTANCE;


        public String apply(JarEntry entry) {
            String jarPath = entry.getName();
            if (jarPath == null || !jarPath.contains(TransformStageUtils.CLASS_EXTENSION)) {
                return null;
            }
            return jarPath.replaceAll("/", ".").substring(0, jarPath.length() - TransformStageUtils.CLASS_EXTENSION.length());
        }
    }

    static enum PackageName implements Function<String, String>
    {
        INSTANCE;


        public String apply(String fullClassName) {
            return fullClassName.substring(0, fullClassName.lastIndexOf(46));
        }
    }
}

