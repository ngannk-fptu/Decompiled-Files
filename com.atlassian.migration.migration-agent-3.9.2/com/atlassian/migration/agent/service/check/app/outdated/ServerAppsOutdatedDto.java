/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.app.outdated;

import java.io.Serializable;
import lombok.Generated;

public class ServerAppsOutdatedDto
implements Serializable {
    private static final long serialVersionUID = 5219076943216902977L;
    public final String name;
    public final String key;
    public final String version;
    public final String url;
    public final String versionWithMigration;

    @Generated
    ServerAppsOutdatedDto(String name, String key, String version, String url, String versionWithMigration) {
        this.name = name;
        this.key = key;
        this.version = version;
        this.url = url;
        this.versionWithMigration = versionWithMigration;
    }

    @Generated
    public static ServerAppsOutdatedDtoBuilder builder() {
        return new ServerAppsOutdatedDtoBuilder();
    }

    @Generated
    public static class ServerAppsOutdatedDtoBuilder {
        @Generated
        private String name;
        @Generated
        private String key;
        @Generated
        private String version;
        @Generated
        private String url;
        @Generated
        private String versionWithMigration;

        @Generated
        ServerAppsOutdatedDtoBuilder() {
        }

        @Generated
        public ServerAppsOutdatedDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public ServerAppsOutdatedDtoBuilder key(String key) {
            this.key = key;
            return this;
        }

        @Generated
        public ServerAppsOutdatedDtoBuilder version(String version) {
            this.version = version;
            return this;
        }

        @Generated
        public ServerAppsOutdatedDtoBuilder url(String url) {
            this.url = url;
            return this;
        }

        @Generated
        public ServerAppsOutdatedDtoBuilder versionWithMigration(String versionWithMigration) {
            this.versionWithMigration = versionWithMigration;
            return this;
        }

        @Generated
        public ServerAppsOutdatedDto build() {
            return new ServerAppsOutdatedDto(this.name, this.key, this.version, this.url, this.versionWithMigration);
        }

        @Generated
        public String toString() {
            return "ServerAppsOutdatedDto.ServerAppsOutdatedDtoBuilder(name=" + this.name + ", key=" + this.key + ", version=" + this.version + ", url=" + this.url + ", versionWithMigration=" + this.versionWithMigration + ")";
        }
    }
}

