/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.ProductFilter
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.spring.scanner.runtime.impl.util;

import com.atlassian.plugin.spring.scanner.ProductFilter;
import com.atlassian.plugin.spring.scanner.runtime.impl.util.ProductFilterUtil;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public final class AnnotationIndexReader {
    private static final Logger log = LoggerFactory.getLogger(AnnotationIndexReader.class);

    public static List<String> readIndexFile(String resourceFile, Bundle bundle) {
        URL url = bundle.getEntry(resourceFile);
        return AnnotationIndexReader.readIndexFile(url);
    }

    public static List<String> readAllIndexFilesForProduct(String resourceFile, BundleContext bundleContext) {
        Bundle bundle = bundleContext.getBundle();
        URL resourceFileUrl = bundle.getEntry(resourceFile);
        ArrayList<String> entries = new ArrayList<String>(AnnotationIndexReader.readIndexFile(resourceFileUrl));
        ProductFilter filter = ProductFilterUtil.getFilterForCurrentProduct(bundleContext);
        if (filter != null) {
            entries.addAll(AnnotationIndexReader.readIndexFile(filter.getPerProductFile(resourceFile), bundle));
        }
        return entries;
    }

    public static List<String> readIndexFile(@Nullable URL indexFileUrl) {
        if (indexFileUrl == null) {
            log.debug("Could not find annotation index file (null url).");
            return Collections.emptyList();
        }
        List<String> entries = AnnotationIndexReader.readIndexFileLines(indexFileUrl);
        if (log.isDebugEnabled()) {
            log.debug("Successfully read annotation index file: {}", (Object)indexFileUrl);
            log.debug("Annotated beans: {}", entries);
        }
        return entries;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Properties readPropertiesFile(@Nullable URL url) {
        if (url == null) {
            return new Properties();
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));){
            Properties properties2 = new Properties();
            properties2.load(reader);
            Properties properties = properties2;
            return properties;
        }
        catch (FileNotFoundException e) {
            return new Properties();
        }
        catch (IOException e) {
            throw new RuntimeIOException("Cannot read properties file [" + url + "]", e);
        }
    }

    public static String[] splitProfiles(@Nullable String profiles) {
        return StringUtils.split((String)StringUtils.trimToEmpty((String)profiles), (char)',');
    }

    public static List<String> getIndexFilesForProfiles(@Nullable String[] profileNames, String indexFileName) {
        if (ArrayUtils.isEmpty((Object[])profileNames)) {
            return Collections.singletonList("META-INF/plugin-components/" + indexFileName);
        }
        return Arrays.stream(profileNames).map(StringUtils::trimToEmpty).filter(StringUtils::isNotEmpty).map(profile -> AnnotationIndexReader.getProfiledIndexFileName(profile, indexFileName)).collect(Collectors.toList());
    }

    private static String getProfiledIndexFileName(String profile, String indexFileName) {
        return "META-INF/plugin-components/profile-" + profile + "/" + indexFileName;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static List<String> readIndexFileLines(URL indexFileUrl) {
        try (InputStreamReader reader = new InputStreamReader(indexFileUrl.openStream(), StandardCharsets.UTF_8);){
            List list = IOUtils.readLines((Reader)reader);
            return list;
        }
        catch (FileNotFoundException e) {
            log.debug("Could not find annotation index file {}", (Object)indexFileUrl);
            return Collections.emptyList();
        }
        catch (IOException e) {
            throw new RuntimeIOException("Cannot read index file [" + indexFileUrl + "]", e);
        }
    }

    private AnnotationIndexReader() {
        throw new UnsupportedOperationException("Not for instantiation");
    }

    private static class RuntimeIOException
    extends RuntimeException {
        private RuntimeIOException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

