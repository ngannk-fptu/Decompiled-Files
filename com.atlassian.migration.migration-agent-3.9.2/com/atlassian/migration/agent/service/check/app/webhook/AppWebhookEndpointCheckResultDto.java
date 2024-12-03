/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.app.webhook;

import java.io.Serializable;
import lombok.Generated;

public class AppWebhookEndpointCheckResultDto
implements Serializable {
    private static final long serialVersionUID = 2153828124093242683L;
    public final String name;
    public final String key;
    public final String contactVendorUrl;

    @Generated
    public static AppWebhookEndpointCheckResultDtoBuilder builder() {
        return new AppWebhookEndpointCheckResultDtoBuilder();
    }

    @Generated
    public AppWebhookEndpointCheckResultDto(String name, String key, String contactVendorUrl) {
        this.name = name;
        this.key = key;
        this.contactVendorUrl = contactVendorUrl;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AppWebhookEndpointCheckResultDto)) {
            return false;
        }
        AppWebhookEndpointCheckResultDto other = (AppWebhookEndpointCheckResultDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$name = this.name;
        String other$name = other.name;
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$key = this.key;
        String other$key = other.key;
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) {
            return false;
        }
        String this$contactVendorUrl = this.contactVendorUrl;
        String other$contactVendorUrl = other.contactVendorUrl;
        return !(this$contactVendorUrl == null ? other$contactVendorUrl != null : !this$contactVendorUrl.equals(other$contactVendorUrl));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AppWebhookEndpointCheckResultDto;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.name;
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $key = this.key;
        result = result * 59 + ($key == null ? 43 : $key.hashCode());
        String $contactVendorUrl = this.contactVendorUrl;
        result = result * 59 + ($contactVendorUrl == null ? 43 : $contactVendorUrl.hashCode());
        return result;
    }

    @Generated
    public static class AppWebhookEndpointCheckResultDtoBuilder {
        @Generated
        private String name;
        @Generated
        private String key;
        @Generated
        private String contactVendorUrl;

        @Generated
        AppWebhookEndpointCheckResultDtoBuilder() {
        }

        @Generated
        public AppWebhookEndpointCheckResultDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public AppWebhookEndpointCheckResultDtoBuilder key(String key) {
            this.key = key;
            return this;
        }

        @Generated
        public AppWebhookEndpointCheckResultDtoBuilder contactVendorUrl(String contactVendorUrl) {
            this.contactVendorUrl = contactVendorUrl;
            return this;
        }

        @Generated
        public AppWebhookEndpointCheckResultDto build() {
            return new AppWebhookEndpointCheckResultDto(this.name, this.key, this.contactVendorUrl);
        }

        @Generated
        public String toString() {
            return "AppWebhookEndpointCheckResultDto.AppWebhookEndpointCheckResultDtoBuilder(name=" + this.name + ", key=" + this.key + ", contactVendorUrl=" + this.contactVendorUrl + ")";
        }
    }
}

