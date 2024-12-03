/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.app.notinstalled;

import java.io.Serializable;
import lombok.Generated;

public class AppsNotInstalledOnCloudDto
implements Serializable {
    private static final long serialVersionUID = -1732941836559700753L;
    public final String name;
    public final String url;
    public final String key;

    @Generated
    AppsNotInstalledOnCloudDto(String name, String url, String key) {
        this.name = name;
        this.url = url;
        this.key = key;
    }

    @Generated
    public static AppsNotInstalledOnCloudDtoBuilder builder() {
        return new AppsNotInstalledOnCloudDtoBuilder();
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getUrl() {
        return this.url;
    }

    @Generated
    public String getKey() {
        return this.key;
    }

    @Generated
    public static class AppsNotInstalledOnCloudDtoBuilder {
        @Generated
        private String name;
        @Generated
        private String url;
        @Generated
        private String key;

        @Generated
        AppsNotInstalledOnCloudDtoBuilder() {
        }

        @Generated
        public AppsNotInstalledOnCloudDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public AppsNotInstalledOnCloudDtoBuilder url(String url) {
            this.url = url;
            return this;
        }

        @Generated
        public AppsNotInstalledOnCloudDtoBuilder key(String key) {
            this.key = key;
            return this;
        }

        @Generated
        public AppsNotInstalledOnCloudDto build() {
            return new AppsNotInstalledOnCloudDto(this.name, this.url, this.key);
        }

        @Generated
        public String toString() {
            return "AppsNotInstalledOnCloudDto.AppsNotInstalledOnCloudDtoBuilder(name=" + this.name + ", url=" + this.url + ", key=" + this.key + ")";
        }
    }
}

