/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.osgi.framework.Bundle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.module.scanner;

import com.atlassian.plugins.rest.module.scanner.AnnotatedScannerException;
import com.atlassian.plugins.rest.module.scanner.JarIndexer;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AnnotatedClassScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatedClassScanner.class);
    private static final String REFERENCE_PROTOCOL = "reference:";
    private static final String FILE_PROTOCOL = "file:";
    private final Bundle bundle;
    private final Set<String> annotations;
    private boolean indexBundledJars;

    public AnnotatedClassScanner(Bundle bundle2, boolean indexBundledJars, Class<?> ... annotations) {
        Validate.notNull((Object)bundle2);
        Validate.notEmpty((Object[])annotations, (String)"You gotta scan for something!", (Object[])new Object[0]);
        this.indexBundledJars = indexBundledJars;
        this.bundle = bundle2;
        this.annotations = this.getAnnotationSet(annotations);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Set<Class<?>> scan(String ... basePackages) {
        File bundleFile = this.getBundleFile(this.bundle);
        if (!bundleFile.isFile()) throw new AnnotatedScannerException("Could not identify Bundle at location <" + this.bundle.getLocation() + ">");
        if (!bundleFile.exists()) {
            throw new AnnotatedScannerException("Could not identify Bundle at location <" + this.bundle.getLocation() + ">");
        }
        try (JarFile jarFile = new JarFile(bundleFile);){
            Set<Class<?>> set = new JarIndexer(jarFile, this.preparePackages(basePackages), this.indexBundledJars, this.annotations, this.bundle).scanJar();
            return set;
        }
        catch (IOException e) {
            throw new AnnotatedScannerException(e.getMessage());
        }
    }

    File getBundleFile(Bundle bundle2) {
        File bundleFile;
        String bundleLocation = bundle2.getLocation();
        if (bundleLocation.startsWith(REFERENCE_PROTOCOL)) {
            bundleLocation = bundleLocation.substring(REFERENCE_PROTOCOL.length());
        }
        if (bundleLocation.startsWith(FILE_PROTOCOL)) {
            try {
                bundleFile = new File(URLDecoder.decode(new URL(bundleLocation).getFile(), "UTF-8"));
            }
            catch (MalformedURLException e) {
                throw new AnnotatedScannerException("Could not parse Bundle location as URL <" + bundleLocation + ">", e);
            }
            catch (UnsupportedEncodingException e) {
                throw new IllegalStateException("Obviously something is wrong with your JVM... It doesn't support UTF-8 !?!", e);
            }
        } else {
            bundleFile = new File(bundleLocation);
        }
        return bundleFile;
    }

    private Set<String> preparePackages(String ... packages) {
        HashSet<String> packageNames = new HashSet<String>();
        for (String packageName : packages) {
            String newPackageName = StringUtils.replaceChars((String)packageName, (char)'.', (char)'/');
            if (!newPackageName.endsWith("/")) {
                packageNames.add(newPackageName + '/');
                continue;
            }
            packageNames.add(newPackageName);
        }
        return packageNames;
    }

    Set<String> getAnnotationSet(Class ... annotations) {
        HashSet<String> formatedAnnotations = new HashSet<String>();
        for (Class cls : annotations) {
            formatedAnnotations.add("L" + cls.getName().replaceAll("\\.", "/") + ";");
        }
        return formatedAnnotations;
    }

    public void setIndexBundledJars(boolean indexBundledJars) {
        this.indexBundledJars = indexBundledJars;
    }
}

