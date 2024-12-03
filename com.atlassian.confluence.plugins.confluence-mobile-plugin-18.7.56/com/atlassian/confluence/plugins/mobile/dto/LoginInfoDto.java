/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.google.common.base.Objects;
import io.swagger.v3.oas.annotations.media.Schema;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class LoginInfoDto {
    @Schema(name="instance-name")
    @JsonProperty(value="instance-name")
    private String instanceName;
    @Schema(name="base-url")
    @JsonProperty(value="base-url")
    private String baseUrl;

    @JsonCreator
    private LoginInfoDto() {
    }

    public LoginInfoDto(String instanceName, String baseUrl) {
        this.instanceName = instanceName;
        this.baseUrl = baseUrl;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LoginInfoDto that = (LoginInfoDto)o;
        return Objects.equal((Object)this.instanceName, (Object)that.instanceName) && Objects.equal((Object)this.baseUrl, (Object)that.baseUrl);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.instanceName, this.baseUrl});
    }
}

