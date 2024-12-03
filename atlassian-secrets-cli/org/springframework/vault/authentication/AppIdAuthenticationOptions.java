/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import org.springframework.util.Assert;
import org.springframework.vault.authentication.AppIdUserIdMechanism;

@Deprecated
public class AppIdAuthenticationOptions {
    public static final String DEFAULT_APPID_AUTHENTICATION_PATH = "app-id";
    private final String path;
    private final String appId;
    private final AppIdUserIdMechanism userIdMechanism;

    private AppIdAuthenticationOptions(String path, String appId, AppIdUserIdMechanism userIdMechanism) {
        this.path = path;
        this.appId = appId;
        this.userIdMechanism = userIdMechanism;
    }

    public static AppIdAuthenticationOptionsBuilder builder() {
        return new AppIdAuthenticationOptionsBuilder();
    }

    public String getPath() {
        return this.path;
    }

    public String getAppId() {
        return this.appId;
    }

    public AppIdUserIdMechanism getUserIdMechanism() {
        return this.userIdMechanism;
    }

    public static class AppIdAuthenticationOptionsBuilder {
        private String path = "app-id";
        private String appId;
        private AppIdUserIdMechanism userIdMechanism;

        AppIdAuthenticationOptionsBuilder() {
        }

        public AppIdAuthenticationOptionsBuilder path(String path) {
            Assert.hasText(path, "Path must not be empty");
            this.path = path;
            return this;
        }

        public AppIdAuthenticationOptionsBuilder appId(String appId) {
            Assert.hasText(appId, "AppId must not be empty");
            this.appId = appId;
            return this;
        }

        public AppIdAuthenticationOptionsBuilder userIdMechanism(AppIdUserIdMechanism userIdMechanism) {
            Assert.notNull((Object)userIdMechanism, "AppIdUserIdMechanism must not be null");
            this.userIdMechanism = userIdMechanism;
            return this;
        }

        public AppIdAuthenticationOptions build() {
            Assert.hasText(this.appId, "AppId must not be empty");
            Assert.notNull((Object)this.userIdMechanism, "AppIdUserIdMechanism must not be null");
            return new AppIdAuthenticationOptions(this.path, this.appId, this.userIdMechanism);
        }
    }
}

