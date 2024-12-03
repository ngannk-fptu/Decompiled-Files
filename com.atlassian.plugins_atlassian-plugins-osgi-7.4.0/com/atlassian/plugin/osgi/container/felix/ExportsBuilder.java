/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.PluginFrameworkUtils
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  io.github.classgraph.ClassGraph
 *  javax.servlet.ServletContext
 *  org.apache.commons.io.FileUtils
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.Element
 *  org.dom4j.io.SAXReader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.twdata.pkgscanner.DefaultOsgiVersionConverter
 *  org.twdata.pkgscanner.ExportPackage
 *  org.twdata.pkgscanner.PackageScanner
 */
package com.atlassian.plugin.osgi.container.felix;

import com.atlassian.plugin.osgi.container.PackageScannerConfiguration;
import com.atlassian.plugin.osgi.container.felix.ExportBuilderUtils;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentRegistration;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.atlassian.plugin.util.PluginFrameworkUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.github.classgraph.ClassGraph;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twdata.pkgscanner.DefaultOsgiVersionConverter;
import org.twdata.pkgscanner.ExportPackage;
import org.twdata.pkgscanner.PackageScanner;

class ExportsBuilder {
    static final String JDK8_PACKAGES_PATH = "jdk8-packages.txt";
    static final String JDK9_PACKAGES_PATH = "jdk9-packages.txt";
    static final String JDK11_PACKAGES_PATH = "jdk11-packages.txt";
    private static final List<String> FRAMEWORK_PACKAGES = ImmutableList.of((Object)"com.atlassian.plugin.remotable", (Object)"com.atlassian.plugin.cache.filecache", (Object)"com.atlassian.plugin.webresource", (Object)"com.atlassian.plugin.web");
    private static final String OSGI_PACKAGES_PATH = "osgi-packages.txt";
    private static final Pattern PATTERN_JAR_FILE_URL1 = Pattern.compile("jar:(file:.+\\.jar)!/");
    private static final Pattern PATTERN_JAR_FILE_URL2 = Pattern.compile("jar:(file:.+\\.jar)!/.+\\.jar");
    private static final Logger log = LoggerFactory.getLogger(ExportsBuilder.class);
    private static String exportStringCache;
    private final CachedExportPackageLoader cachedExportPackageLoader;

    static String getLegacyScanModeProperty() {
        return "com.atlassian.plugin.export.legacy.scan.mode";
    }

    ExportsBuilder() {
        this(new PackageScannerExportsFileLoader("package-scanner-exports.xml"));
    }

    ExportsBuilder(CachedExportPackageLoader loader) {
        this.cachedExportPackageLoader = loader;
    }

    @VisibleForTesting
    static URL maybeUnwrapJarFileUrl(URL url) {
        Matcher matcher1 = PATTERN_JAR_FILE_URL1.matcher(url.toString());
        Matcher matcher2 = PATTERN_JAR_FILE_URL2.matcher(url.toString());
        if (matcher1.matches()) {
            String fileUrl = matcher1.group(1);
            log.debug("Unwrapped Spring Boot URL: {} -> {}", (Object)url, (Object)fileUrl);
            try {
                return new URL(fileUrl);
            }
            catch (MalformedURLException e) {
                log.warn("Could not create URL from apparent Spring Boot jar:file: {}->{}", (Object)url, (Object)fileUrl);
            }
        } else if (matcher2.matches()) {
            String fileUrl = matcher2.group(1);
            log.debug("Unwrapped Spring Boot URL: {} -> {}", (Object)url, (Object)fileUrl);
            return null;
        }
        return url;
    }

    @VisibleForTesting
    static boolean isPluginFrameworkPackage(String pkg) {
        return pkg.startsWith("com.atlassian.plugin.") && FRAMEWORK_PACKAGES.stream().noneMatch(frameworkPackage -> pkg.equals(frameworkPackage) || pkg.startsWith(frameworkPackage + "."));
    }

    String getExports(List<HostComponentRegistration> regs, PackageScannerConfiguration packageScannerConfig) {
        if (exportStringCache == null) {
            exportStringCache = this.determineExports(regs, packageScannerConfig);
        }
        return exportStringCache;
    }

    void clearExportCache() {
        exportStringCache = null;
    }

    String determineExports(List<HostComponentRegistration> regs, PackageScannerConfiguration packageScannerConfig) {
        HashMap<String, String> exportPackages = new HashMap<String, String>();
        ExportBuilderUtils.copyUnlessExist(exportPackages, ExportBuilderUtils.parseExportFile(OSGI_PACKAGES_PATH));
        ExportBuilderUtils.copyUnlessExist(exportPackages, ExportBuilderUtils.parseExportFile(this.getJdkPackagesPath()));
        Collection<ExportPackage> scannedPackages = this.generateExports(packageScannerConfig);
        ExportBuilderUtils.copyUnlessExist(exportPackages, ExportBuilderUtils.toMap(scannedPackages));
        try {
            Map<String, String> referredPackages = OsgiHeaderUtil.findReferredPackageVersions(regs, packageScannerConfig.getPackageVersions());
            ExportBuilderUtils.copyUnlessExist(exportPackages, referredPackages);
        }
        catch (IOException ex) {
            log.error("Unable to calculate necessary exports based on host components", (Throwable)ex);
        }
        this.enforceFrameworkVersion(exportPackages);
        String exports = OsgiHeaderUtil.generatePackageVersionString(exportPackages);
        if (log.isDebugEnabled()) {
            log.debug("Exports:\n{}", (Object)exports.replaceAll(",", "\r\n"));
        }
        return exports;
    }

    private void enforceFrameworkVersion(Map<String, String> exportPackages) {
        String frameworkVersion = PluginFrameworkUtils.getPluginFrameworkVersion();
        DefaultOsgiVersionConverter converter = new DefaultOsgiVersionConverter();
        String frameworkVersionOsgi = converter.getVersion(frameworkVersion);
        exportPackages.keySet().stream().filter(ExportsBuilder::isPluginFrameworkPackage).forEach(pkg -> exportPackages.put((String)pkg, frameworkVersionOsgi));
    }

    Collection<ExportPackage> generateExports(PackageScannerConfiguration packageScannerConfig) {
        Collection exports;
        String[] arrType = new String[]{};
        HashMap<String, String> pkgVersions = new HashMap<String, String>(packageScannerConfig.getPackageVersions());
        String javaxServletPattern = "javax.servlet*";
        ServletContext servletContext = packageScannerConfig.getServletContext();
        if (null == pkgVersions.get("javax.servlet*") && null != servletContext) {
            String servletVersion = servletContext.getMajorVersion() + "." + servletContext.getMinorVersion();
            pkgVersions.put("javax.servlet*", servletVersion);
        }
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        PackageScanner scanner = new PackageScanner().useClassLoader(contextClassLoader).select(PackageScanner.jars((String[])PackageScanner.include((String[])packageScannerConfig.getJarIncludes().toArray(arrType)), (String[])PackageScanner.exclude((String[])packageScannerConfig.getJarExcludes().toArray(arrType))), PackageScanner.packages((String[])PackageScanner.include((String[])packageScannerConfig.getPackageIncludes().toArray(arrType)), (String[])PackageScanner.exclude((String[])packageScannerConfig.getPackageExcludes().toArray(arrType)))).withMappings(pkgVersions);
        if (log.isDebugEnabled()) {
            scanner.enableDebug();
        }
        if ((exports = this.cachedExportPackageLoader.load()) == null) {
            boolean legacyMode = Boolean.getBoolean(ExportsBuilder.getLegacyScanModeProperty());
            if (legacyMode) {
                exports = scanner.scan();
            } else {
                URL[] urls = this.getClassPathUrls(contextClassLoader);
                exports = scanner.scan(urls);
            }
        }
        log.info("Package scan completed. Found {} packages to export.", (Object)exports.size());
        if (ExportsBuilder.packageScanFailed(exports) && servletContext != null) {
            log.warn("Unable to find expected packages via classloader scanning. Trying ServletContext scanning...");
            try {
                exports = scanner.scan(new URL[]{servletContext.getResource("/WEB-INF/lib"), servletContext.getResource("/WEB-INF/classes")});
            }
            catch (MalformedURLException e) {
                log.warn("Unable to scan webapp for packages", (Throwable)e);
            }
        }
        if (ExportsBuilder.packageScanFailed(exports)) {
            throw new IllegalStateException("Unable to find required packages via classloader or servlet context scanning, most likely due to an application server bug.");
        }
        return exports;
    }

    private URL[] getClassPathUrls(ClassLoader contextClassLoader) {
        LinkedList<URL> loaderUrls = new LinkedList<URL>();
        new ClassGraph().addClassLoader(contextClassLoader).acceptPathsNonRecursive(new String[]{""}).getClasspathURLs().forEach(loaderUrls::push);
        ArrayList<URL> allUrls = new ArrayList<URL>();
        while (!loaderUrls.isEmpty()) {
            URL url = ExportsBuilder.maybeUnwrapJarFileUrl((URL)loaderUrls.pop());
            try {
                if (null == url) continue;
                File file = FileUtils.toFile((URL)url);
                if (null == file) {
                    log.warn("Cannot deep scan non file '{}'", (Object)url);
                    continue;
                }
                if (!file.exists()) {
                    log.debug("Cannot deep scan missing file '{}'", (Object)url);
                    continue;
                }
                if (file.isDirectory()) {
                    allUrls.add(url);
                    continue;
                }
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    allUrls.add(url);
                    JarFile jar = new JarFile(file);
                    this.collectClassPath(loaderUrls, url, jar);
                    continue;
                }
                log.debug("Skipping deep scan of non jar-file ");
            }
            catch (Exception exception) {
                log.warn("Failed to deep scan '{}'", (Object)url, (Object)exception);
            }
        }
        return allUrls.toArray(new URL[0]);
    }

    private void collectClassPath(Deque<URL> loaderUrls, URL url, JarFile jar) throws IOException {
        Manifest manifest = jar.getManifest();
        if (null == manifest) {
            log.debug("Missing manifest prevents deep scan of '{}'", (Object)url);
            return;
        }
        String classPath = manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH);
        if (null != classPath) {
            StringTokenizer tokenizer = new StringTokenizer(classPath);
            while (tokenizer.hasMoreTokens()) {
                String classPathEntry = tokenizer.nextToken();
                try {
                    loaderUrls.push(new URL(url, classPathEntry));
                    log.debug("Deep scan found url '{}'", (Object)loaderUrls.peekFirst());
                }
                catch (MalformedURLException emu) {
                    log.warn("Cannot deep scan unparseable Class-Path entry '{}' in '{}'", (Object)url, (Object)classPath);
                }
            }
        }
    }

    private String getJdkPackagesPath() {
        String versionString = System.getProperty("java.specification.version");
        if (versionString == null) {
            versionString = System.getProperty("java.version", "11");
        }
        if (versionString.startsWith("1.")) {
            versionString = versionString.substring(2);
        }
        int version = 0;
        for (char c : versionString.toCharArray()) {
            if (!Character.isDigit(c)) continue;
            version = 10 * version + Character.digit(c, 10);
        }
        if (version >= 11) {
            return JDK11_PACKAGES_PATH;
        }
        if (version >= 9) {
            return JDK9_PACKAGES_PATH;
        }
        return JDK8_PACKAGES_PATH;
    }

    private static boolean packageScanFailed(Collection<ExportPackage> exports) {
        return exports.stream().noneMatch(export -> export.getPackageName().equals("org.slf4j"));
    }

    static class PackageScannerExportsFileLoader
    implements CachedExportPackageLoader {
        private final String path;

        PackageScannerExportsFileLoader(String path) {
            this.path = path;
        }

        @Override
        public Collection<ExportPackage> load() {
            URL exportsUrl = this.getClass().getClassLoader().getResource(this.path);
            if (exportsUrl != null) {
                log.debug("Precalculated exports found, loading...");
                ArrayList result = Lists.newArrayList();
                try {
                    Document doc = new SAXReader().read(exportsUrl);
                    for (Element export : doc.getRootElement().elements()) {
                        String packageName = export.attributeValue("package");
                        String version = export.attributeValue("version");
                        String location = export.attributeValue("location");
                        if (packageName == null || location == null) {
                            log.warn("Invalid configuration: package({}) and location({}) are required, aborting precalculated exports and reverting to normal scanning", (Object)packageName, (Object)location);
                            return Collections.emptyList();
                        }
                        result.add(new ExportPackage(packageName, version, new File(location)));
                    }
                    log.debug("Loaded {} precalculated exports", (Object)result.size());
                    return result;
                }
                catch (DocumentException e) {
                    log.warn("Unable to load exports from " + this.path + " due to malformed XML", (Throwable)e);
                }
            }
            log.debug("No precalculated exports found");
            return null;
        }
    }

    public static interface CachedExportPackageLoader {
        public Collection<ExportPackage> load();
    }
}

