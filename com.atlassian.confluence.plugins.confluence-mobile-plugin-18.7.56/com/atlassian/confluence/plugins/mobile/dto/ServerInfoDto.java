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

public class ServerInfoDto {
    @Schema(name="push-notification-enabled")
    @JsonProperty(value="push-notification-enabled")
    private boolean pushNotificationEnabled;
    @Schema(name="session-timeout-fix-enabled")
    @JsonProperty(value="session-timeout-fix-enabled")
    private boolean sessionTimeoutFixEnabled;

    public static Builder builder() {
        return new Builder();
    }

    public boolean isPushNotificationEnabled() {
        return this.pushNotificationEnabled;
    }

    public boolean isSessionTimeoutFixEnabled() {
        return this.sessionTimeoutFixEnabled;
    }

    @JsonCreator
    private ServerInfoDto() {
        this(ServerInfoDto.builder());
    }

    private ServerInfoDto(Builder builder) {
        this.pushNotificationEnabled = builder.pushNotificationEnabled;
        this.sessionTimeoutFixEnabled = builder.sessionTimeoutFixEnabled;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ServerInfoDto that = (ServerInfoDto)o;
        return Objects.equal((Object)this.pushNotificationEnabled, (Object)that.pushNotificationEnabled) && Objects.equal((Object)this.sessionTimeoutFixEnabled, (Object)that.sessionTimeoutFixEnabled);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.pushNotificationEnabled, this.sessionTimeoutFixEnabled});
    }

    public static final class Builder {
        private boolean pushNotificationEnabled;
        private boolean sessionTimeoutFixEnabled;

        public ServerInfoDto build() {
            return new ServerInfoDto(this);
        }

        public Builder pushNotificationEnabled(boolean pushNotificationEnabled) {
            this.pushNotificationEnabled = pushNotificationEnabled;
            return this;
        }

        public Builder sessionTimeoutFixEnabled(boolean sessionTimeoutFixEnabled) {
            this.sessionTimeoutFixEnabled = sessionTimeoutFixEnabled;
            return this;
        }
    }
}

