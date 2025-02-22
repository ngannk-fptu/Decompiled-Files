/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

public class JetBuildInfo {
    private final String version;
    private final String build;
    private final String revision;

    public JetBuildInfo(String version, String build, String revision) {
        this.version = version;
        this.build = build;
        this.revision = revision;
    }

    public String getVersion() {
        return this.version;
    }

    public String getBuild() {
        return this.build;
    }

    public String getRevision() {
        return this.revision;
    }

    public String toString() {
        return "JetBuildInfo{version='" + this.version + '\'' + ", build='" + this.build + '\'' + ", revision='" + this.revision + '\'' + '}';
    }
}

