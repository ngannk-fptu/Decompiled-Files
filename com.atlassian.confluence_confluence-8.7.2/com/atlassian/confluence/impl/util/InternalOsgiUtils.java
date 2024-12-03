/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.core.io.ClassPathResource
 */
package com.atlassian.confluence.impl.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

public class InternalOsgiUtils {
    private static final String EXPECTED_SYSTEM_BUNDLE_EXPORTS_FILE = "OsgiAcceptanceTest-expected-system-bundle-exports.txt";
    private static final String EXPECTED_PUBLIC_API_EXPORTS_FILE = "OsgiAcceptanceTest-expected-public-api-exports.txt";
    private static final String EXPECTED_DEPRECATED_EXPORTS_FILE = "OsgiAcceptanceTest-expected-deprecated-exports.txt";
    private static final Predicate<String> FILTER = Stream.of(StringUtils::isNotBlank, line -> !StringUtils.startsWith((CharSequence)line, (CharSequence)"#"), line -> !line.contains("java11") || InternalOsgiUtils.getJavaMajorVersion() == 11, line -> !line.contains("java17") || InternalOsgiUtils.getJavaMajorVersion() == 17).reduce(Predicate::and).get();

    private static int getJavaMajorVersion() {
        return Integer.parseInt(System.getProperty("java.specification.version"));
    }

    public static Set<String> readExposedOsgiSystemPackagesFromFile() {
        return InternalOsgiUtils.readExposedOsgiPackagesFromFile(EXPECTED_SYSTEM_BUNDLE_EXPORTS_FILE);
    }

    public static TreeSet<String> readExposedOsgiPackagesFromFile(String exportsFile) {
        TreeSet treeSet;
        block8: {
            ClassPathResource resource = new ClassPathResource(exportsFile, InternalOsgiUtils.class);
            InputStream stream = resource.getInputStream();
            try {
                treeSet = IOUtils.readLines((InputStream)stream, (Charset)StandardCharsets.UTF_8).stream().filter(FILTER).map(line -> StringUtils.split((String)line, (String)",")[0]).collect(Collectors.toCollection(TreeSet::new));
                if (stream == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to read expected system bundle exports file", e);
                }
            }
            stream.close();
        }
        return treeSet;
    }

    public static String systemPackagesFilename() {
        return EXPECTED_SYSTEM_BUNDLE_EXPORTS_FILE;
    }

    public static String publicApiPackagesFilename() {
        return EXPECTED_PUBLIC_API_EXPORTS_FILE;
    }

    public static String deprecatedPackagesFilename() {
        return EXPECTED_DEPRECATED_EXPORTS_FILE;
    }
}

