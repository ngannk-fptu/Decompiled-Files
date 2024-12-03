/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.vault.authentication;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.AppRoleTokens;
import org.springframework.vault.authentication.UnwrappingEndpoints;
import org.springframework.vault.support.VaultToken;

public class AppRoleAuthenticationOptions {
    public static final String DEFAULT_APPROLE_AUTHENTICATION_PATH = "approle";
    private final String path;
    private final RoleId roleId;
    private final SecretId secretId;
    @Nullable
    private final String appRole;
    private final UnwrappingEndpoints unwrappingEndpoints;
    @Nullable
    @Deprecated
    private final VaultToken initialToken;

    private AppRoleAuthenticationOptions(String path, RoleId roleId, SecretId secretId, @Nullable String appRole, UnwrappingEndpoints unwrappingEndpoints, @Nullable VaultToken initialToken) {
        this.path = path;
        this.roleId = roleId;
        this.secretId = secretId;
        this.appRole = appRole;
        this.unwrappingEndpoints = unwrappingEndpoints;
        this.initialToken = initialToken;
    }

    public static AppRoleAuthenticationOptionsBuilder builder() {
        return new AppRoleAuthenticationOptionsBuilder();
    }

    public String getPath() {
        return this.path;
    }

    public RoleId getRoleId() {
        return this.roleId;
    }

    public SecretId getSecretId() {
        return this.secretId;
    }

    @Nullable
    public String getAppRole() {
        return this.appRole;
    }

    public UnwrappingEndpoints getUnwrappingEndpoints() {
        return this.unwrappingEndpoints;
    }

    @Nullable
    @Deprecated
    public VaultToken getInitialToken() {
        return this.initialToken;
    }

    public static interface SecretId {
        public static SecretId wrapped(VaultToken initialToken) {
            Assert.notNull((Object)initialToken, (String)"Initial token must not be null");
            return new AppRoleTokens.Wrapped(initialToken);
        }

        public static SecretId pull(VaultToken initialToken) {
            Assert.notNull((Object)initialToken, (String)"Initial token must not be null");
            return new AppRoleTokens.Pull(initialToken);
        }

        public static SecretId provided(String secretId) {
            Assert.hasText((String)secretId, (String)"SecretId must not be null or empty");
            return new AppRoleTokens.Provided(secretId);
        }

        public static SecretId absent() {
            return AppRoleTokens.AbsentSecretId.ABSENT_SECRET_ID;
        }
    }

    public static interface RoleId {
        public static RoleId wrapped(VaultToken initialToken) {
            Assert.notNull((Object)initialToken, (String)"Initial token must not be null");
            return new AppRoleTokens.Wrapped(initialToken);
        }

        public static RoleId pull(VaultToken initialToken) {
            Assert.notNull((Object)initialToken, (String)"Initial token must not be null");
            return new AppRoleTokens.Pull(initialToken);
        }

        public static RoleId provided(String roleId) {
            Assert.hasText((String)roleId, (String)"RoleId must not be null or empty");
            return new AppRoleTokens.Provided(roleId);
        }
    }

    public static class AppRoleAuthenticationOptionsBuilder {
        private String path = "approle";
        @Nullable
        private String providedRoleId;
        @Nullable
        private RoleId roleId;
        @Nullable
        private String providedSecretId;
        @Nullable
        private SecretId secretId;
        @Nullable
        private String appRole;
        private UnwrappingEndpoints unwrappingEndpoints = UnwrappingEndpoints.SysWrapping;
        @Nullable
        @Deprecated
        private VaultToken initialToken;

        AppRoleAuthenticationOptionsBuilder() {
        }

        public AppRoleAuthenticationOptionsBuilder path(String path) {
            Assert.hasText((String)path, (String)"Path must not be empty");
            this.path = path;
            return this;
        }

        public AppRoleAuthenticationOptionsBuilder roleId(RoleId roleId) {
            Assert.notNull((Object)roleId, (String)"RoleId must not be null");
            this.roleId = roleId;
            return this;
        }

        @Deprecated
        public AppRoleAuthenticationOptionsBuilder roleId(String roleId) {
            Assert.hasText((String)roleId, (String)"RoleId must not be empty");
            this.providedRoleId = roleId;
            return this;
        }

        public AppRoleAuthenticationOptionsBuilder secretId(SecretId secretId) {
            Assert.notNull((Object)secretId, (String)"SecretId must not be null");
            this.secretId = secretId;
            return this;
        }

        @Deprecated
        public AppRoleAuthenticationOptionsBuilder secretId(String secretId) {
            Assert.hasText((String)secretId, (String)"SecretId must not be empty");
            this.providedSecretId = secretId;
            return this;
        }

        public AppRoleAuthenticationOptionsBuilder appRole(String appRole) {
            Assert.hasText((String)appRole, (String)"AppRole must not be empty");
            this.appRole = appRole;
            return this;
        }

        public AppRoleAuthenticationOptionsBuilder unwrappingEndpoints(UnwrappingEndpoints endpoints) {
            Assert.notNull((Object)((Object)endpoints), (String)"UnwrappingEndpoints must not be empty");
            this.unwrappingEndpoints = endpoints;
            return this;
        }

        @Deprecated
        public AppRoleAuthenticationOptionsBuilder initialToken(VaultToken initialToken) {
            Assert.notNull((Object)initialToken, (String)"InitialToken must not be null");
            this.initialToken = initialToken;
            return this;
        }

        public AppRoleAuthenticationOptions build() {
            Assert.hasText((String)this.path, (String)"Path must not be empty");
            if (this.secretId == null) {
                if (this.providedSecretId != null) {
                    this.secretId(SecretId.provided(this.providedSecretId));
                } else if (this.initialToken != null) {
                    this.secretId(SecretId.pull(this.initialToken));
                } else {
                    this.secretId(SecretId.absent());
                }
            }
            if (this.roleId == null) {
                if (this.providedRoleId != null) {
                    this.roleId(RoleId.provided(this.providedRoleId));
                } else {
                    Assert.notNull((Object)this.initialToken, (String)"AppRole authentication configured for pull mode. InitialToken must not be null (pull mode)");
                    this.roleId(RoleId.pull(this.initialToken));
                }
            }
            if (this.roleId instanceof AppRoleTokens.Pull || this.secretId instanceof AppRoleTokens.Pull) {
                Assert.notNull((Object)this.appRole, (String)"AppRole authentication configured for pull mode. AppRole must not be null.");
            }
            return new AppRoleAuthenticationOptions(this.path, this.roleId, this.secretId, this.appRole, this.unwrappingEndpoints, this.initialToken);
        }
    }
}

