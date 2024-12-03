/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.twdata.pkgscanner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twdata.pkgscanner.DefaultOsgiVersionConverter;
import org.twdata.pkgscanner.ExportPackage;
import org.twdata.pkgscanner.ExportPackageListBuilder;
import org.twdata.pkgscanner.OsgiVersionConverter;
import org.twdata.pkgscanner.PackageScanner;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class InternalScanner {
    private final Logger log = LoggerFactory.getLogger(InternalScanner.class);
    private Map<String, Set<String>> jarContentCache = new HashMap<String, Set<String>>();
    private ClassLoader classloader;
    private PackageScanner.VersionMapping[] versionMappings;
    private OsgiVersionConverter versionConverter = new DefaultOsgiVersionConverter();
    private final boolean debug;

    InternalScanner(ClassLoader cl, PackageScanner.VersionMapping[] versionMappings, boolean debug) {
        this.classloader = cl;
        for (PackageScanner.VersionMapping mapping : versionMappings) {
            mapping.toVersion(this.versionConverter.getVersion(mapping.getVersion()));
        }
        this.versionMappings = versionMappings;
        this.debug = debug;
    }

    void setOsgiVersionConverter(OsgiVersionConverter converter) {
        this.versionConverter = converter;
    }

    Collection<ExportPackage> findInPackages(Test test, String ... roots) {
        ExportPackageListBuilder exportPackageListBuilder = new ExportPackageListBuilder();
        for (String pkg : roots) {
            for (ExportPackage export : this.findInPackage(test, pkg)) {
                exportPackageListBuilder.add(export);
            }
        }
        return exportPackageListBuilder.getPackageList();
    }

    Collection<ExportPackage> findInUrls(Test test, URL ... urls) {
        ExportPackageListBuilder exportPackageListBuilder = new ExportPackageListBuilder();
        Vector<URL> list = new Vector<URL>(Arrays.asList(urls));
        for (ExportPackage export : this.findInPackageWithUrls(test, "", list.elements())) {
            exportPackageListBuilder.add(export);
        }
        return exportPackageListBuilder.getPackageList();
    }

    List<ExportPackage> findInPackage(Test test, String packageName) {
        Enumeration<URL> urls;
        ArrayList<ExportPackage> localExports = new ArrayList<ExportPackage>();
        packageName = packageName.replace('.', '/');
        try {
            urls = this.classloader.getResources(packageName);
            if (!urls.hasMoreElements()) {
                this.log.warn("Unable to find any resources for package '" + packageName + "'");
            }
        }
        catch (IOException ioe) {
            this.log.warn("Could not read package: " + packageName);
            return localExports;
        }
        return this.findInPackageWithUrls(test, packageName, urls);
    }

    List<ExportPackage> findInPackageWithUrls(Test test, String packageName, Enumeration<URL> urls) {
        ArrayList<ExportPackage> localExports = new ArrayList<ExportPackage>();
        while (urls.hasMoreElements()) {
            try {
                URL url = urls.nextElement();
                String urlPath = url.getPath();
                if (urlPath.lastIndexOf(33) > 0) {
                    if ((urlPath = urlPath.substring(0, urlPath.lastIndexOf(33))).startsWith("/")) {
                        urlPath = "file:" + urlPath;
                    }
                } else if (!urlPath.startsWith("file:")) {
                    urlPath = "file:" + urlPath;
                }
                this.log.debug("Scanning for packages in [" + urlPath + "].");
                File file = null;
                try {
                    URL fileURL = new URL(urlPath);
                    if ("file".equals(fileURL.getProtocol().toLowerCase())) {
                        file = new File(fileURL.toURI());
                    } else {
                        this.log.info("Skipping non file classpath element [ " + urlPath + " ]");
                    }
                }
                catch (URISyntaxException e) {
                    file = new File(urlPath.substring("file:".length()));
                }
                if (file != null && file.isDirectory()) {
                    localExports.addAll(this.loadImplementationsInDirectory(test, packageName, file));
                    continue;
                }
                if (file == null || !test.matchesJar(file.getName())) continue;
                localExports.addAll(this.loadImplementationsInJar(test, file));
            }
            catch (IOException ioe) {
                this.log.error("could not read entries: " + ioe);
            }
        }
        return localExports;
    }

    List<ExportPackage> loadImplementationsInDirectory(Test test, String parent, File location) {
        this.log.debug("Scanning directory " + location.getAbsolutePath() + " parent: '" + parent + "'.");
        File[] files = location.listFiles();
        ArrayList<ExportPackage> localExports = new ArrayList<ExportPackage>();
        HashSet<String> scanned = new HashSet<String>();
        for (File file : files) {
            String packageOrClass = parent == null || parent.length() == 0 ? file.getName() : parent + "/" + file.getName();
            if (file.isDirectory()) {
                localExports.addAll(this.loadImplementationsInDirectory(test, packageOrClass, file));
                continue;
            }
            if ("".equals(parent) && file.getName().endsWith(".jar") && test.matchesJar(file.getName())) {
                localExports.addAll(this.loadImplementationsInJar(test, file));
                continue;
            }
            String pkg = packageOrClass;
            int lastSlash = pkg.lastIndexOf(47);
            if (lastSlash > 0) {
                pkg = pkg.substring(0, lastSlash);
            }
            if (scanned.contains(pkg = pkg.replace('/', '.'))) continue;
            if (test.matchesPackage(pkg)) {
                this.log.debug(String.format("loadImplementationsInDirectory: [%s] %s", pkg, file));
                localExports.add(new ExportPackage(pkg, this.determinePackageVersion(null, pkg), location));
            }
            scanned.add(pkg);
        }
        return localExports;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    List<ExportPackage> loadImplementationsInJar(Test test, File file) {
        ArrayList<ExportPackage> localExports = new ArrayList<ExportPackage>();
        Set<String> packages = this.jarContentCache.get(file.getPath());
        if (packages == null) {
            packages = new HashSet<String>();
            try {
                JarFile jarFile = new JarFile(file);
                Enumeration<JarEntry> e = jarFile.entries();
                while (e.hasMoreElements()) {
                    boolean newlyAdded;
                    JarEntry entry = e.nextElement();
                    String name = entry.getName();
                    if (entry.isDirectory()) continue;
                    String pkg = name;
                    int pos = pkg.lastIndexOf(47);
                    if (pos > -1) {
                        pkg = pkg.substring(0, pos);
                    }
                    if (!(newlyAdded = packages.add(pkg = pkg.replace('/', '.'))) || !this.log.isDebugEnabled()) continue;
                    this.log.debug(String.format("Found package '%s' in jar file [%s]", pkg, file));
                }
            }
            catch (IOException ioe) {
                this.log.error("Could not search jar file '" + file + "' for classes matching criteria: " + test + " due to an IOException" + ioe);
                List<ExportPackage> e = Collections.emptyList();
                return e;
            }
            finally {
                this.jarContentCache.put(file.getPath(), packages);
            }
        }
        HashSet<String> scanned = new HashSet<String>();
        for (String pkg : packages) {
            if (scanned.contains(pkg)) continue;
            if (test.matchesPackage(pkg)) {
                localExports.add(new ExportPackage(pkg, this.determinePackageVersion(file, pkg), file));
            }
            scanned.add(pkg);
        }
        return localExports;
    }

    String determinePackageVersion(File jar, String pkg) {
        String version = null;
        for (PackageScanner.VersionMapping mapping : this.versionMappings) {
            if (!mapping.matches(pkg)) continue;
            version = mapping.getVersion();
        }
        if (version == null && jar != null) {
            String name = jar.getName();
            version = this.extractVersion(name);
        }
        if (version == null && this.debug) {
            if (jar != null) {
                this.log.warn("Unable to determine version for '" + pkg + "' in jar '" + jar.getPath() + "'");
            } else {
                this.log.warn("Unable to determine version for '" + pkg + "'");
            }
        }
        return version;
    }

    String extractVersion(String filename) {
        StringBuilder version = null;
        boolean lastWasSeparator = false;
        for (int x = 0; x < filename.length(); ++x) {
            char c = filename.charAt(x);
            if (c == '-' || c == '_') {
                lastWasSeparator = true;
            } else {
                if (Character.isDigit(c) && lastWasSeparator && version == null) {
                    version = new StringBuilder();
                }
                lastWasSeparator = false;
            }
            if (version == null) continue;
            version.append(c);
        }
        if (version != null) {
            if (".jar".equals(version.substring(version.length() - 4))) {
                version.delete(version.length() - 4, version.length());
            }
            return this.versionConverter.getVersion(version.toString());
        }
        return null;
    }

    static interface Test {
        public boolean matchesPackage(String var1);

        public boolean matchesJar(String var1);
    }
}

