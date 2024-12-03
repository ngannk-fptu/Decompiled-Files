/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.header.OSGiHeader
 *  aQute.bnd.osgi.Analyzer
 *  aQute.bnd.osgi.Clazz
 *  aQute.bnd.osgi.Resource
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.PluginPermission
 *  com.atlassian.plugin.util.ClassLoaderUtils
 *  com.atlassian.plugin.util.ClassUtils
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.io.IOUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.Version
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.util;

import aQute.bnd.header.OSGiHeader;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Resource;
import com.atlassian.annotations.Internal;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.PluginPermission;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentRegistration;
import com.atlassian.plugin.osgi.util.ClassBinaryScanner;
import com.atlassian.plugin.util.ClassLoaderUtils;
import com.atlassian.plugin.util.ClassUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgiHeaderUtil {
    static Logger log = LoggerFactory.getLogger(OsgiHeaderUtil.class);
    private static final String EMPTY_OSGI_VERSION = Version.emptyVersion.toString();
    private static final String STAR_PACKAGE = "*";
    private static final String DUPLICATE_PACKAGE_SUFFIX = "~";
    private static final Predicate<String> JAVA_PACKAGE_FILTER = pkg -> !pkg.startsWith("java.");
    private static final Predicate<String> JAVA_CLASS_FILTER = classEntry -> !classEntry.startsWith("java/");

    public static Set<String> findReferredPackageNames(Collection<Class<?>> classes) throws IOException {
        if (classes == null || classes.isEmpty()) {
            return Collections.emptySet();
        }
        HashSet classesToScan = new HashSet();
        for (Class<?> clazz : classes) {
            ClassUtils.findAllTypes(clazz, classesToScan);
        }
        HashSet<String> referredClasses = new HashSet<String>();
        HashSet<String> referredPackages = new HashSet<String>();
        for (Class inf : classesToScan) {
            String clsName = inf.getName().replace('.', '/') + ".class";
            OsgiHeaderUtil.crawlReferenceTree(clsName, referredClasses, referredPackages, 1);
        }
        return ImmutableSet.copyOf(referredPackages);
    }

    public static Map<String, String> findReferredPackageVersions(List<HostComponentRegistration> registrations, Map<String, String> packageVersions) throws IOException {
        if (registrations == null || registrations.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Class<?>> declaredInterfaces = registrations.stream().flatMap(reg -> Arrays.stream(reg.getMainInterfaceClasses())).collect(Collectors.toSet());
        return OsgiHeaderUtil.matchPackageVersions(OsgiHeaderUtil.findReferredPackageNames(declaredInterfaces), packageVersions);
    }

    static Map<String, String> matchPackageVersions(Set<String> packageNames, Map<String, String> packageVersions) {
        HashMap<String, String> output = new HashMap<String, String>();
        for (String pkg : packageNames) {
            String version = packageVersions.get(pkg);
            String effectiveValue = EMPTY_OSGI_VERSION;
            if (version != null) {
                try {
                    Version.parseVersion((String)version);
                    effectiveValue = version;
                }
                catch (IllegalArgumentException ex) {
                    log.info("Unable to parse version: {}", (Object)version);
                }
            }
            output.put(pkg, effectiveValue);
        }
        return ImmutableMap.copyOf(output);
    }

    static void crawlReferenceTree(String className, Set<String> scannedClasses, Set<String> packageImports, int level) throws IOException {
        InputStream in;
        if (level <= 0) {
            return;
        }
        if (className.startsWith("java/")) {
            return;
        }
        if (scannedClasses.contains(className)) {
            return;
        }
        scannedClasses.add(className);
        if (log.isDebugEnabled()) {
            log.debug("Crawling {}", (Object)className);
        }
        if ((in = ClassLoaderUtils.getResourceAsStream((String)className, OsgiHeaderUtil.class)) == null) {
            log.error("Cannot find class: [{}]", (Object)className);
            return;
        }
        try (ClassBinaryScanner.InputStreamResource classBinaryResource = new ClassBinaryScanner.InputStreamResource(in);){
            ClassBinaryScanner.ScanResult scanResult = ClassBinaryScanner.scanClassBinary(new Clazz(new Analyzer(), className, (Resource)classBinaryResource));
            scanResult.getReferredPackages().stream().filter(JAVA_PACKAGE_FILTER).forEach(packageImports::add);
            Set referredClasses = scanResult.getReferredClasses().stream().filter(JAVA_CLASS_FILTER).collect(Collectors.toSet());
            for (String ref : referredClasses) {
                OsgiHeaderUtil.crawlReferenceTree(ref + ".class", scannedClasses, packageImports, level - 1);
            }
        }
    }

    public static Map<String, Map<String, String>> parseHeader(String header) {
        return new LinkedHashMap<String, Map<String, String>>(OSGiHeader.parseHeader((String)header).asMapMap());
    }

    public static String buildHeader(Map<String, Map<String, String>> values) {
        StringBuilder header = new StringBuilder();
        Iterator<Map.Entry<String, Map<String, String>>> i = values.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, Map<String, String>> entry = i.next();
            OsgiHeaderUtil.buildHeader(entry.getKey(), entry.getValue(), header);
            if (!i.hasNext()) continue;
            header.append(",");
        }
        return header.toString();
    }

    public static String buildHeader(String key, Map<String, String> attrs) {
        StringBuilder fullPkg = new StringBuilder();
        OsgiHeaderUtil.buildHeader(key, attrs, fullPkg);
        return fullPkg.toString();
    }

    private static void buildHeader(String key, Map<String, String> attrs, StringBuilder builder) {
        builder.append(key);
        if (attrs != null && !attrs.isEmpty()) {
            for (Map.Entry<String, String> entry : attrs.entrySet()) {
                builder.append(";");
                builder.append(entry.getKey());
                builder.append("=\"");
                builder.append(entry.getValue());
                builder.append("\"");
            }
        }
    }

    public static String getPluginKey(Bundle bundle) {
        return OsgiHeaderUtil.getPluginKey(bundle.getSymbolicName(), bundle.getHeaders().get("Atlassian-Plugin-Key"), bundle.getHeaders().get("Bundle-Version"));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static String getPluginKey(File file) {
        try (JarFile jar = new JarFile(file);){
            Manifest manifest = jar.getManifest();
            if (manifest == null) return null;
            String string = OsgiHeaderUtil.getPluginKey(manifest);
            return string;
        }
        catch (IOException eio) {
            log.warn("Cannot read jar file '{}': {}", (Object)file, (Object)eio.getMessage());
        }
        return null;
    }

    public static String getPluginKey(Manifest mf) {
        return OsgiHeaderUtil.getPluginKey(OsgiHeaderUtil.getAttributeWithoutValidation(mf, "Bundle-SymbolicName"), OsgiHeaderUtil.getAttributeWithoutValidation(mf, "Atlassian-Plugin-Key"), OsgiHeaderUtil.getAttributeWithoutValidation(mf, "Bundle-Version"));
    }

    private static String getPluginKey(Object bundleName, Object atlKey, Object version) {
        Object key = atlKey;
        if (key == null) {
            String bName = bundleName.toString();
            int scPos = bName.indexOf(59);
            if (scPos > -1) {
                bName = bName.substring(0, scPos);
            }
            key = bName + "-" + version;
        }
        return key.toString();
    }

    public static String generatePackageVersionString(Map<String, String> packages) {
        if (packages == null || packages.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        ArrayList<String> packageNames = new ArrayList<String>(packages.keySet());
        Collections.sort(packageNames);
        for (String packageName : packageNames) {
            sb.append(",");
            sb.append(packageName);
            String version = packages.get(packageName);
            if (version == null || version.equals(EMPTY_OSGI_VERSION)) continue;
            sb.append(";version=").append(version);
        }
        sb.delete(0, 1);
        return sb.toString();
    }

    public static String getValidatedAttribute(Manifest manifest, String key) {
        String value = OsgiHeaderUtil.getAttributeWithoutValidation(manifest, key);
        Preconditions.checkNotNull((Object)value);
        Preconditions.checkArgument((!value.isEmpty() ? 1 : 0) != 0);
        return value;
    }

    public static String getNonEmptyAttribute(Manifest manifest, String key) {
        String attributeWithoutValidation = OsgiHeaderUtil.getAttributeWithoutValidation(manifest, key);
        Preconditions.checkArgument((!attributeWithoutValidation.isEmpty() ? 1 : 0) != 0);
        return attributeWithoutValidation;
    }

    public static String getAttributeWithoutValidation(Manifest manifest, String key) {
        return manifest.getMainAttributes().getValue(key);
    }

    @Internal
    public static PluginInformation extractOsgiPluginInformation(Manifest manifest, boolean requireVersion) {
        String bundleVersion = requireVersion ? OsgiHeaderUtil.getValidatedAttribute(manifest, "Bundle-Version") : OsgiHeaderUtil.getAttributeWithoutValidation(manifest, "Bundle-Version");
        String bundleVendor = OsgiHeaderUtil.getAttributeWithoutValidation(manifest, "Bundle-Vendor");
        String bundleDescription = OsgiHeaderUtil.getAttributeWithoutValidation(manifest, "Bundle-Description");
        PluginInformation pluginInformation = new PluginInformation();
        pluginInformation.setVersion(bundleVersion);
        pluginInformation.setDescription(bundleDescription);
        pluginInformation.setVendorName(bundleVendor);
        pluginInformation.setPermissions((Set)ImmutableSet.of((Object)PluginPermission.EXECUTE_JAVA));
        return pluginInformation;
    }

    public static Manifest getManifest(PluginArtifact pluginArtifact) {
        block8: {
            Manifest manifest;
            InputStream manifestStream = pluginArtifact.getResourceAsStream("META-INF/MANIFEST.MF");
            if (manifestStream == null) break block8;
            try {
                manifest = new Manifest(manifestStream);
            }
            catch (IOException eio) {
                try {
                    try {
                        log.error("Cannot read manifest from plugin artifact '{}': {}", (Object)pluginArtifact.getName(), (Object)eio.getMessage());
                    }
                    catch (Throwable throwable) {
                        throw throwable;
                    }
                    finally {
                        IOUtils.closeQuietly((InputStream)manifestStream);
                    }
                }
                catch (PluginParseException epp) {
                    log.error("Cannot get manifest resource from plugin artifact '{}': {}", (Object)pluginArtifact.getName(), (Object)epp.getMessage());
                }
            }
            IOUtils.closeQuietly((InputStream)manifestStream);
            return manifest;
        }
        return null;
    }

    public static Map<String, Map<String, String>> moveStarPackageToEnd(Map<String, Map<String, String>> packages, String pluginKey) {
        LinkedHashMap<String, Map<String, String>> orderedPkgs = new LinkedHashMap<String, Map<String, String>>();
        LinkedHashMap<String, Map<String, String>> starPkgs = new LinkedHashMap<String, Map<String, String>>();
        for (Map.Entry<String, Map<String, String>> entry : packages.entrySet()) {
            if (entry.getKey().contains(STAR_PACKAGE)) {
                starPkgs.put(entry.getKey(), entry.getValue());
                continue;
            }
            orderedPkgs.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Map<String, String>> entry : starPkgs.entrySet()) {
            log.debug("moving {} package to end for plugin {}", (Object)entry.getKey(), (Object)pluginKey);
            orderedPkgs.put(entry.getKey(), entry.getValue());
        }
        return orderedPkgs;
    }

    public static Map<String, Map<String, String>> stripDuplicatePackages(Map<String, Map<String, String>> packages, String pluginKey, String action) {
        LinkedHashMap<String, Map<String, String>> deduplicatedPackages = new LinkedHashMap<String, Map<String, String>>();
        for (Map.Entry<String, Map<String, String>> pkg : packages.entrySet()) {
            if (pkg.getKey().endsWith(DUPLICATE_PACKAGE_SUFFIX)) {
                log.warn("removing duplicate {} package {} for plugin {} - it is likely that a duplicate package was supplied in the OSGi instructions in the plugin's MANIFEST.MF", new Object[]{action, pkg.getKey(), pluginKey});
                continue;
            }
            deduplicatedPackages.put(pkg.getKey(), pkg.getValue());
        }
        return deduplicatedPackages;
    }
}

