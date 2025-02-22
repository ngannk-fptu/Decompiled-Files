/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.instance.JetBuildInfo;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.StringUtil;

public class BuildInfo {
    public static final int UNKNOWN_HAZELCAST_VERSION = -1;
    private static final int MAJOR_VERSION_MULTIPLIER = 10000;
    private static final int MINOR_VERSION_MULTIPLIER = 100;
    private static final int PATCH_TOKEN_INDEX = 3;
    private final String version;
    private final String build;
    private final String revision;
    private final int buildNumber;
    private final boolean enterprise;
    private final byte serializationVersion;
    private final BuildInfo upstreamBuildInfo;
    private final JetBuildInfo jetBuildInfo;

    public BuildInfo(String version, String build, String revision, int buildNumber, boolean enterprise, byte serializationVersion) {
        this(version, build, revision, buildNumber, enterprise, serializationVersion, null);
    }

    public BuildInfo(String version, String build, String revision, int buildNumber, boolean enterprise, byte serializationVersion, BuildInfo upstreamBuildInfo) {
        this(version, build, revision, buildNumber, enterprise, serializationVersion, upstreamBuildInfo, null);
    }

    private BuildInfo(String version, String build, String revision, int buildNumber, boolean enterprise, byte serializationVersion, BuildInfo upstreamBuildInfo, JetBuildInfo jetBuildInfo) {
        this.version = version;
        this.build = build;
        this.revision = revision;
        this.buildNumber = buildNumber;
        this.enterprise = enterprise;
        this.serializationVersion = serializationVersion;
        this.upstreamBuildInfo = upstreamBuildInfo;
        this.jetBuildInfo = jetBuildInfo;
    }

    private BuildInfo(BuildInfo buildInfo, JetBuildInfo jetBuildInfo) {
        this(buildInfo.getVersion(), buildInfo.getBuild(), buildInfo.getRevision(), buildInfo.getBuildNumber(), buildInfo.isEnterprise(), buildInfo.getSerializationVersion(), buildInfo.getUpstreamBuildInfo(), jetBuildInfo);
    }

    public String getRevision() {
        return this.revision;
    }

    public String getVersion() {
        return this.version;
    }

    public String getBuild() {
        return this.build;
    }

    public int getBuildNumber() {
        return this.buildNumber;
    }

    public boolean isEnterprise() {
        return this.enterprise;
    }

    public byte getSerializationVersion() {
        return this.serializationVersion;
    }

    public BuildInfo getUpstreamBuildInfo() {
        return this.upstreamBuildInfo;
    }

    public JetBuildInfo getJetBuildInfo() {
        return this.jetBuildInfo;
    }

    BuildInfo withJetBuildInfo(JetBuildInfo jetBuildInfo) {
        return new BuildInfo(this, jetBuildInfo);
    }

    public String toString() {
        return "BuildInfo{version='" + this.version + '\'' + ", build='" + this.build + '\'' + ", buildNumber=" + this.buildNumber + ", revision=" + this.revision + ", enterprise=" + this.enterprise + ", serializationVersion=" + this.serializationVersion + (this.jetBuildInfo == null ? "" : ", jet=" + this.jetBuildInfo) + (this.upstreamBuildInfo == null ? "" : ", upstream=" + this.upstreamBuildInfo) + '}';
    }

    public static int calculateVersion(String version) {
        if (null == version) {
            return -1;
        }
        String[] versionTokens = StringUtil.tokenizeVersionString(version);
        if (versionTokens != null) {
            try {
                String patchVersionString;
                int calculatedVersion = 10000 * Integer.parseInt(versionTokens[0]) + 100 * Integer.parseInt(versionTokens[1]);
                int groupCount = versionTokens.length;
                if (groupCount >= 3 && null != (patchVersionString = versionTokens[3]) && !patchVersionString.startsWith("-")) {
                    calculatedVersion += Integer.parseInt(patchVersionString);
                }
                return calculatedVersion;
            }
            catch (Exception e) {
                Logger.getLogger(BuildInfo.class).warning("Failed to calculate version using version string " + version, e);
            }
        }
        return -1;
    }
}

