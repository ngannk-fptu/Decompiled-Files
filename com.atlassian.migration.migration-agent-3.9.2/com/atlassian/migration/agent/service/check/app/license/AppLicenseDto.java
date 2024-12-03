/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.app.license;

import java.io.Serializable;
import lombok.Generated;

public class AppLicenseDto
implements Serializable {
    private static final long serialVersionUID = -3189912796923274202L;
    public final String name;
    public final String url;
    public final String key;

    @Generated
    AppLicenseDto(String name, String url, String key) {
        this.name = name;
        this.url = url;
        this.key = key;
    }

    @Generated
    public static AppLicenseDtoBuilder builder() {
        return new AppLicenseDtoBuilder();
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
    public static class AppLicenseDtoBuilder {
        @Generated
        private String name;
        @Generated
        private String url;
        @Generated
        private String key;

        @Generated
        AppLicenseDtoBuilder() {
        }

        @Generated
        public AppLicenseDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public AppLicenseDtoBuilder url(String url) {
            this.url = url;
            return this;
        }

        @Generated
        public AppLicenseDtoBuilder key(String key) {
            this.key = key;
            return this;
        }

        @Generated
        public AppLicenseDto build() {
            return new AppLicenseDto(this.name, this.url, this.key);
        }

        @Generated
        public String toString() {
            return "AppLicenseDto.AppLicenseDtoBuilder(name=" + this.name + ", url=" + this.url + ", key=" + this.key + ")";
        }
    }
}

