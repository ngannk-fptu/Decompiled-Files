/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.healthcheck.impl;

import com.atlassian.troubleshooting.healthcheck.model.DbType;
import com.atlassian.troubleshooting.stp.spi.Version;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbVersionExtractor {
    private static final Map<String, String> MSSQL_VERSION_ALIASES = ImmutableMap.builder().put((Object)"15\\..*", (Object)"2019").put((Object)"14\\..*", (Object)"2017").put((Object)"13\\..*", (Object)"2016").put((Object)"12\\..*", (Object)"2014").put((Object)"11\\..*", (Object)"2012").put((Object)"10\\.5\\d\\..*", (Object)"2008 R2").put((Object)"10\\.[0-4]\\..*", (Object)"2008").put((Object)"9\\..*", (Object)"2005").put((Object)"8\\..*", (Object)"2000").build();
    private static final Logger LOG = LoggerFactory.getLogger(DbVersionExtractor.class);
    private static final Pattern ORACLE_MAJOR_MINOR_PATTERN = Pattern.compile("^.*?(\\d+\\.\\d+)\\.\\d+\\.\\d+.*?", 32);
    private static final Map<String, String> ORACLE_VERSION_ALIASES = ImmutableMap.builder().put((Object)"19.0", (Object)"19C").put((Object)"18.0", (Object)"18C").put((Object)"12.2", (Object)"12C R2").put((Object)"12.1", (Object)"12C R1").put((Object)"11.2", (Object)"11G R2").put((Object)"11.1", (Object)"11G R1").build();
    private static final int POSTGRES_NEW_VERSIONING_SCHEME_MAJOR = 10;

    private static String extractOracleVersion(String version) {
        Matcher m = ORACLE_MAJOR_MINOR_PATTERN.matcher(version);
        if (m.matches()) {
            return m.group(1);
        }
        LOG.warn("Failed to find version number in Oracle version string '" + version + "'");
        return version;
    }

    private static String getIfExists(Map<String, String> aliasMap, String version, BiFunction<String, String, Boolean> filterVersion) {
        return aliasMap.entrySet().stream().filter(entry -> (Boolean)filterVersion.apply(version, (String)entry.getKey())).map(Map.Entry::getValue).findFirst().orElse(version);
    }

    public String getSupportedPlatformVersionComparisonString(DbType dbType, String version) {
        switch (dbType) {
            case postgres: {
                return this.extractPostgresVersion(version);
            }
            case mysql: 
            case h2: {
                return this.simpleVersionNumber(version);
            }
            case oracle: {
                return DbVersionExtractor.getIfExists(ORACLE_VERSION_ALIASES, version, (v, k) -> DbVersionExtractor.extractOracleVersion(v).equals(k));
            }
            case sqlServer: {
                return DbVersionExtractor.getIfExists(MSSQL_VERSION_ALIASES, version, (v, k) -> v.matches((String)k));
            }
        }
        throw new RuntimeException("Unknown DbType: " + (Object)((Object)dbType));
    }

    private String extractPostgresVersion(String version) {
        try {
            int majorVersion = Version.of(version).getMajor();
            return majorVersion < 10 ? this.simpleVersionNumber(version) : String.valueOf(majorVersion);
        }
        catch (IllegalArgumentException e) {
            LOG.warn("Unexpected db version number format '" + version + "'", (Throwable)e);
            return version;
        }
    }

    private String simpleVersionNumber(String version) {
        try {
            return Version.of(version).getMajorAndMinor();
        }
        catch (IllegalArgumentException e) {
            LOG.warn("Unexpected db version number format '" + version + "'", (Throwable)e);
            return version;
        }
    }
}

