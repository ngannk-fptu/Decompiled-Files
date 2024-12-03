/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.startup;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import org.apache.catalina.startup.Bootstrap;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public final class ClassLoaderFactory {
    private static final Log log = LogFactory.getLog(ClassLoaderFactory.class);

    public static ClassLoader createClassLoader(File[] unpacked, File[] packed, ClassLoader parent) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Creating new class loader");
        }
        LinkedHashSet<URL> set = new LinkedHashSet<URL>();
        if (unpacked != null) {
            for (File file : unpacked) {
                if (!file.canRead()) continue;
                file = new File(file.getCanonicalPath());
                URL url = file.toURI().toURL();
                if (log.isDebugEnabled()) {
                    log.debug((Object)("  Including directory " + url));
                }
                set.add(url);
            }
        }
        if (packed != null) {
            for (File directory : packed) {
                String[] filenames;
                if (!directory.isDirectory() || !directory.canRead() || (filenames = directory.list()) == null) continue;
                for (String s : filenames) {
                    String filename = s.toLowerCase(Locale.ENGLISH);
                    if (!filename.endsWith(".jar")) continue;
                    File file = new File(directory, s);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("  Including jar file " + file.getAbsolutePath()));
                    }
                    URL url = file.toURI().toURL();
                    set.add(url);
                }
            }
        }
        URL[] array = set.toArray(new URL[0]);
        return AccessController.doPrivileged(() -> {
            if (parent == null) {
                return new URLClassLoader(array);
            }
            return new URLClassLoader(array, parent);
        });
    }

    public static ClassLoader createClassLoader(List<Repository> repositories, ClassLoader parent) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Creating new class loader");
        }
        LinkedHashSet<URL> set = new LinkedHashSet<URL>();
        if (repositories != null) {
            for (Repository repository : repositories) {
                String[] filenames;
                URL url;
                File directory;
                if (repository.getType() == RepositoryType.URL) {
                    URL url2 = ClassLoaderFactory.buildClassLoaderUrl(repository.getLocation());
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("  Including URL " + url2));
                    }
                    set.add(url2);
                    continue;
                }
                if (repository.getType() == RepositoryType.DIR) {
                    directory = new File(repository.getLocation());
                    if (!ClassLoaderFactory.validateFile(directory = directory.getCanonicalFile(), RepositoryType.DIR)) continue;
                    url = ClassLoaderFactory.buildClassLoaderUrl(directory);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("  Including directory " + url));
                    }
                    set.add(url);
                    continue;
                }
                if (repository.getType() == RepositoryType.JAR) {
                    File file = new File(repository.getLocation());
                    if (!ClassLoaderFactory.validateFile(file = file.getCanonicalFile(), RepositoryType.JAR)) continue;
                    url = ClassLoaderFactory.buildClassLoaderUrl(file);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("  Including jar file " + url));
                    }
                    set.add(url);
                    continue;
                }
                if (repository.getType() != RepositoryType.GLOB) continue;
                directory = new File(repository.getLocation());
                if (!ClassLoaderFactory.validateFile(directory = directory.getCanonicalFile(), RepositoryType.GLOB)) continue;
                if (log.isDebugEnabled()) {
                    log.debug((Object)("  Including directory glob " + directory.getAbsolutePath()));
                }
                if ((filenames = directory.list()) == null) continue;
                for (String s : filenames) {
                    String filename = s.toLowerCase(Locale.ENGLISH);
                    if (!filename.endsWith(".jar")) continue;
                    File file = new File(directory, s);
                    if (!ClassLoaderFactory.validateFile(file = file.getCanonicalFile(), RepositoryType.JAR)) continue;
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("    Including glob jar file " + file.getAbsolutePath()));
                    }
                    URL url3 = ClassLoaderFactory.buildClassLoaderUrl(file);
                    set.add(url3);
                }
            }
        }
        URL[] array = set.toArray(new URL[0]);
        if (log.isDebugEnabled()) {
            for (int i = 0; i < array.length; ++i) {
                log.debug((Object)("  location " + i + " is " + array[i]));
            }
        }
        return AccessController.doPrivileged(() -> {
            if (parent == null) {
                return new URLClassLoader(array);
            }
            return new URLClassLoader(array, parent);
        });
    }

    private static boolean validateFile(File file, RepositoryType type) throws IOException {
        if (RepositoryType.DIR == type || RepositoryType.GLOB == type) {
            if (!file.isDirectory() || !file.canRead()) {
                String msg = "Problem with directory [" + file + "], exists: [" + file.exists() + "], isDirectory: [" + file.isDirectory() + "], canRead: [" + file.canRead() + "]";
                File home = new File(Bootstrap.getCatalinaHome());
                home = home.getCanonicalFile();
                File base = new File(Bootstrap.getCatalinaBase());
                base = base.getCanonicalFile();
                File defaultValue = new File(base, "lib");
                if (!home.getPath().equals(base.getPath()) && file.getPath().equals(defaultValue.getPath()) && !file.exists()) {
                    log.debug((Object)msg);
                } else {
                    log.warn((Object)msg);
                }
                return false;
            }
        } else if (RepositoryType.JAR == type && !file.canRead()) {
            log.warn((Object)("Problem with JAR file [" + file + "], exists: [" + file.exists() + "], canRead: [" + file.canRead() + "]"));
            return false;
        }
        return true;
    }

    private static URL buildClassLoaderUrl(String urlString) throws MalformedURLException, URISyntaxException {
        String result = urlString.replace("!/", "%21/");
        return new URI(result).toURL();
    }

    private static URL buildClassLoaderUrl(File file) throws MalformedURLException, URISyntaxException {
        String fileUrlString = file.toURI().toString();
        fileUrlString = fileUrlString.replace("!/", "%21/");
        return new URI(fileUrlString).toURL();
    }

    public static class Repository {
        private final String location;
        private final RepositoryType type;

        public Repository(String location, RepositoryType type) {
            this.location = location;
            this.type = type;
        }

        public String getLocation() {
            return this.location;
        }

        public RepositoryType getType() {
            return this.type;
        }
    }

    public static enum RepositoryType {
        DIR,
        GLOB,
        JAR,
        URL;

    }
}

