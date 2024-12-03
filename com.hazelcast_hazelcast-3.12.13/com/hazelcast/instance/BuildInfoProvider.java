/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.instance.BuildInfo;
import com.hazelcast.instance.GeneratedBuildProperties;
import com.hazelcast.instance.JetBuildInfo;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.EmptyStatement;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public final class BuildInfoProvider {
    public static final String HAZELCAST_INTERNAL_OVERRIDE_VERSION = "hazelcast.internal.override.version";
    public static final String HAZELCAST_INTERNAL_OVERRIDE_ENTERPRISE = "hazelcast.internal.override.enterprise";
    private static final String HAZELCAST_INTERNAL_OVERRIDE_BUILD = "hazelcast.build";
    private static final ILogger LOGGER = Logger.getLogger(BuildInfoProvider.class);
    private static final BuildInfo BUILD_INFO_CACHE = BuildInfoProvider.populateBuildInfoCache();

    private BuildInfoProvider() {
    }

    private static BuildInfo populateBuildInfoCache() {
        return BuildInfoProvider.getBuildInfoInternalVersion(Overrides.DISABLED);
    }

    public static BuildInfo getBuildInfo() {
        if (Overrides.isEnabled()) {
            Overrides overrides = Overrides.fromProperties();
            return BuildInfoProvider.getBuildInfoInternalVersion(overrides);
        }
        return BUILD_INFO_CACHE;
    }

    private static BuildInfo getBuildInfoInternalVersion(Overrides overrides) {
        BuildInfo buildInfo = BuildInfoProvider.readBuildPropertiesClass(GeneratedBuildProperties.class, null, overrides);
        try {
            Class<?> enterpriseClass = BuildInfoProvider.class.getClassLoader().loadClass("com.hazelcast.instance.GeneratedEnterpriseBuildProperties");
            if (enterpriseClass.getClassLoader() == BuildInfoProvider.class.getClassLoader()) {
                buildInfo = BuildInfoProvider.readBuildPropertiesClass(enterpriseClass, buildInfo, overrides);
            }
        }
        catch (ClassNotFoundException e) {
            EmptyStatement.ignore(e);
        }
        Properties jetProperties = BuildInfoProvider.loadPropertiesFromResource("jet-runtime.properties");
        return BuildInfoProvider.withJetProperties(jetProperties, buildInfo);
    }

    private static BuildInfo withJetProperties(Properties properties, BuildInfo buildInfo) {
        if (properties.isEmpty()) {
            return buildInfo;
        }
        String version = properties.getProperty("jet.version");
        String build = properties.getProperty("jet.build");
        String revision = properties.getProperty("jet.git.revision");
        JetBuildInfo jetBuildInfo = new JetBuildInfo(version, build, revision);
        return buildInfo.withJetBuildInfo(jetBuildInfo);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Properties loadPropertiesFromResource(String resourceName) {
        InputStream properties = BuildInfoProvider.class.getClassLoader().getResourceAsStream(resourceName);
        Properties runtimeProperties = new Properties();
        try {
            if (properties != null) {
                runtimeProperties.load(properties);
            }
        }
        catch (Exception ignored) {
            EmptyStatement.ignore(ignored);
        }
        finally {
            IOUtil.closeResource(properties);
        }
        return runtimeProperties;
    }

    private static BuildInfo readBuildPropertiesClass(Class<?> clazz, BuildInfo upstreamBuildInfo, Overrides overrides) {
        String version = BuildInfoProvider.readStaticStringField(clazz, "VERSION");
        String build = BuildInfoProvider.readStaticStringField(clazz, "BUILD");
        String revision = BuildInfoProvider.readStaticStringField(clazz, "REVISION");
        String distribution = BuildInfoProvider.readStaticStringField(clazz, "DISTRIBUTION");
        if (!revision.isEmpty() && revision.equals("${git.commit.id.abbrev}")) {
            revision = "";
        }
        int buildNumber = Integer.parseInt(build);
        boolean enterprise = !"Hazelcast".equals(distribution);
        String serialVersionString = BuildInfoProvider.readStaticStringField(clazz, "SERIALIZATION_VERSION");
        byte serialVersion = Byte.parseByte(serialVersionString);
        return overrides.apply(version, build, revision, buildNumber, enterprise, serialVersion, upstreamBuildInfo);
    }

    private static String readStaticStringField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getField(fieldName);
            return (String)field.get(null);
        }
        catch (NoSuchFieldException e) {
            throw new HazelcastException(e);
        }
        catch (IllegalAccessException e) {
            throw new HazelcastException(e);
        }
    }

    private static final class Overrides {
        private static final Overrides DISABLED = new Overrides(null, -1, null);
        private String version;
        private int buildNo;
        private Boolean enterprise;

        private Overrides(String version, int build, Boolean enterprise) {
            this.version = version;
            this.buildNo = build;
            this.enterprise = enterprise;
        }

        private BuildInfo apply(String version, String build, String revision, int buildNumber, boolean enterprise, byte serialVersion, BuildInfo upstreamBuildInfo) {
            if (this.buildNo != -1) {
                build = String.valueOf(this.buildNo);
                buildNumber = this.buildNo;
            }
            if (this.version != null) {
                LOGGER.info("Overriding hazelcast version with system property value " + this.version);
                version = this.version;
            }
            if (this.enterprise != null) {
                LOGGER.info("Overriding hazelcast enterprise flag with system property value " + this.enterprise);
                enterprise = this.enterprise;
            }
            return new BuildInfo(version, build, revision, buildNumber, enterprise, serialVersion, upstreamBuildInfo);
        }

        private static boolean isEnabled() {
            return System.getProperty(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_BUILD) != null || System.getProperty(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_VERSION) != null || System.getProperty(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_ENTERPRISE) != null;
        }

        private static Overrides fromProperties() {
            String version = System.getProperty(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_VERSION);
            int build = Integer.getInteger(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_BUILD, -1);
            Boolean enterprise = null;
            String enterpriseOverride = System.getProperty(BuildInfoProvider.HAZELCAST_INTERNAL_OVERRIDE_ENTERPRISE);
            if (enterpriseOverride != null) {
                enterprise = Boolean.valueOf(enterpriseOverride);
            }
            return new Overrides(version, build, enterprise);
        }
    }
}

