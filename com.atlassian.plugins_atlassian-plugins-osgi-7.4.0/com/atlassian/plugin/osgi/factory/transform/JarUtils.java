/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterators
 */
package com.atlassian.plugin.osgi.factory.transform;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class JarUtils {
    private JarUtils() {
    }

    public static Manifest getManifest(File file) {
        Manifest result = JarUtils.withJar(file, ManifestExtractor.INSTANCE);
        return result == null ? new Manifest() : result;
    }

    public static boolean hasManifestEntry(Manifest manifest, String entryName) {
        return manifest != null && manifest.getMainAttributes().getValue(entryName) != null;
    }

    static Iterable<JarEntry> getEntries(File file) {
        return JarUtils.withJar(file, JarEntryExtractor.INSTANCE);
    }

    static JarEntry getEntry(File file, String path) {
        return (JarEntry)JarUtils.withJar(file, jarFile -> jarFile.getJarEntry(path));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static <T> T withJar(File file, Extractor<T> extractor) {
        try (JarFile jarFile = new JarFile(file);){
            Object r = extractor.apply(jarFile);
            return (T)r;
        }
        catch (IOException e) {
            throw new IllegalArgumentException("File must be a jar: " + file, e);
        }
    }

    public static void closeQuietly(JarFile jarFile) {
        if (jarFile != null) {
            try {
                jarFile.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    static enum JarEntryExtractor implements Extractor<Iterable<JarEntry>>
    {
        INSTANCE;


        @Override
        public Iterable<JarEntry> apply(JarFile jarFile) {
            return ImmutableList.copyOf((Iterator)Iterators.forEnumeration(jarFile.entries()));
        }
    }

    static enum ManifestExtractor implements Extractor<Manifest>
    {
        INSTANCE;


        @Override
        public Manifest apply(JarFile input) {
            try {
                return input.getManifest();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static interface Extractor<T>
    extends Function<JarFile, T> {
    }
}

