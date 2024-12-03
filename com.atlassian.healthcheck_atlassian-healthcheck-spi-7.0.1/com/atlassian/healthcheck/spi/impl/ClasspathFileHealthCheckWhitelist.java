/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 */
package com.atlassian.healthcheck.spi.impl;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.healthcheck.spi.HealthCheckWhitelist;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ClasspathFileHealthCheckWhitelist
implements HealthCheckWhitelist {
    private static final String WHITELIST_FILENAME = "atlassian-healthcheck-whitelist.txt";
    @TenantAware(value=TenancyScope.TENANTLESS, comment="Whitelist provided by product core, same across all tenants")
    private final Map<String, Set<String>> whitelistsByHealthcheckKey;

    public ClasspathFileHealthCheckWhitelist() {
        this(WHITELIST_FILENAME, ClasspathFileHealthCheckWhitelist.class.getClassLoader());
    }

    ClasspathFileHealthCheckWhitelist(String filename, ClassLoader classLoader) {
        this.whitelistsByHealthcheckKey = ClasspathFileHealthCheckWhitelist.loadFile(filename, classLoader);
    }

    Map<String, Set<String>> getWhitelistsByHealthcheckKey() {
        return Collections.unmodifiableMap(this.whitelistsByHealthcheckKey);
    }

    @Override
    public Set<String> getWhitelistedItemsForHealthCheck(String whitelistKey) {
        return this.whitelistsByHealthcheckKey.getOrDefault(whitelistKey, Collections.emptySet());
    }

    private static Map<String, Set<String>> loadFile(String fileName, ClassLoader classLoader) {
        Collection<InputStream> fileInputStreams = ClasspathFileHealthCheckWhitelist.getInputStreamsForFilename(fileName, classLoader);
        try {
            Map<String, Set<String>> map = fileInputStreams.stream().flatMap(inputStream -> ClasspathFileHealthCheckWhitelist.readLines(inputStream).stream().map(String::trim).filter(line -> !line.isEmpty()).filter(line -> !line.startsWith("#")).map(WhitelistEntry::new)).collect(Collectors.groupingBy(WhitelistEntry::getWhitelistKey, Collectors.mapping(WhitelistEntry::getWhitelistedItem, Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet))));
            return map;
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid whitelist file '" + fileName + "'", e);
        }
        finally {
            ClasspathFileHealthCheckWhitelist.closeAll(fileInputStreams);
        }
    }

    private static List<String> readLines(InputStream inputStream) {
        ArrayList<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        try {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    private static Collection<InputStream> getInputStreamsForFilename(String fileName, ClassLoader classLoader) {
        ArrayList<InputStream> inputStreams = new ArrayList<InputStream>();
        Class<ClasspathFileHealthCheckWhitelist> clazz = ClasspathFileHealthCheckWhitelist.class;
        String resourcePath = clazz.getPackage().getName().replace(".", "/") + "/" + fileName;
        try {
            Enumeration<URL> urlEnumeration = classLoader.getResources(resourcePath);
            while (urlEnumeration.hasMoreElements()) {
                inputStreams.add(urlEnumeration.nextElement().openStream());
            }
            if (inputStreams.isEmpty()) {
                throw new IllegalArgumentException("No whitelist file found at resource path '" + resourcePath + "'");
            }
        }
        catch (IOException e) {
            ClasspathFileHealthCheckWhitelist.closeAll(inputStreams);
            throw new RuntimeException(e);
        }
        return inputStreams;
    }

    private static void closeAll(Collection<InputStream> fileInputStreams) {
        for (InputStream fileInputStream : fileInputStreams) {
            try {
                fileInputStream.close();
            }
            catch (IOException iOException) {}
        }
    }

    private static class WhitelistEntry {
        private final String whitelistKey;
        private final String whitelistedItem;

        public WhitelistEntry(String line) {
            String[] parts = line.split("[\\s]+", 3);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Unsupported whitelist line format: expected 'HealthCheckKey item', got '" + line + "'");
            }
            this.whitelistKey = parts[0].trim();
            this.whitelistedItem = parts[1].trim();
            if (this.whitelistKey.isEmpty() || this.whitelistedItem.isEmpty()) {
                throw new IllegalArgumentException("Unsupported whitelist line format: expected 'HealthCheckKey item', got '" + line + "'");
            }
        }

        public String getWhitelistKey() {
            return this.whitelistKey;
        }

        public String getWhitelistedItem() {
            return this.whitelistedItem;
        }
    }
}

