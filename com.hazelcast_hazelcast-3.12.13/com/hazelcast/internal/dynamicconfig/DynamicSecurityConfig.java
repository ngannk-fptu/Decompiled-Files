/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.dynamicconfig;

import com.hazelcast.config.CredentialsFactoryConfig;
import com.hazelcast.config.LoginModuleConfig;
import com.hazelcast.config.OnJoinPermissionOperationName;
import com.hazelcast.config.PermissionConfig;
import com.hazelcast.config.PermissionPolicyConfig;
import com.hazelcast.config.SecurityConfig;
import com.hazelcast.config.SecurityInterceptorConfig;
import com.hazelcast.security.SecurityService;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DynamicSecurityConfig
extends SecurityConfig {
    private final SecurityConfig staticSecurityConfig;
    private final SecurityService securityService;

    public DynamicSecurityConfig(SecurityConfig staticSecurityConfig, SecurityService securityService) {
        this.staticSecurityConfig = staticSecurityConfig;
        this.securityService = securityService;
    }

    @Override
    public SecurityConfig addSecurityInterceptorConfig(SecurityInterceptorConfig interceptorConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public List<SecurityInterceptorConfig> getSecurityInterceptorConfigs() {
        return this.staticSecurityConfig.getSecurityInterceptorConfigs();
    }

    @Override
    public void setSecurityInterceptorConfigs(List<SecurityInterceptorConfig> securityInterceptorConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public boolean isEnabled() {
        return this.staticSecurityConfig.isEnabled();
    }

    @Override
    public SecurityConfig setEnabled(boolean enabled) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public SecurityConfig addMemberLoginModuleConfig(LoginModuleConfig loginModuleConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public SecurityConfig addClientLoginModuleConfig(LoginModuleConfig loginModuleConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public SecurityConfig addClientPermissionConfig(PermissionConfig permissionConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public List<LoginModuleConfig> getClientLoginModuleConfigs() {
        return this.staticSecurityConfig.getClientLoginModuleConfigs();
    }

    @Override
    public SecurityConfig setClientLoginModuleConfigs(List<LoginModuleConfig> loginModuleConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public List<LoginModuleConfig> getMemberLoginModuleConfigs() {
        return this.staticSecurityConfig.getMemberLoginModuleConfigs();
    }

    @Override
    public SecurityConfig setMemberLoginModuleConfigs(List<LoginModuleConfig> memberLoginModuleConfigs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public PermissionPolicyConfig getClientPolicyConfig() {
        return this.staticSecurityConfig.getClientPolicyConfig();
    }

    @Override
    public SecurityConfig setClientPolicyConfig(PermissionPolicyConfig policyConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public SecurityConfig setClientBlockUnmappedActions(boolean clientBlockUnmappedActions) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public Set<PermissionConfig> getClientPermissionConfigs() {
        Set<PermissionConfig> permissionConfigs = this.securityService != null ? this.securityService.getClientPermissionConfigs() : this.staticSecurityConfig.getClientPermissionConfigs();
        return Collections.unmodifiableSet(permissionConfigs);
    }

    @Override
    public boolean getClientBlockUnmappedActions() {
        return this.staticSecurityConfig.getClientBlockUnmappedActions();
    }

    @Override
    public SecurityConfig setClientPermissionConfigs(Set<PermissionConfig> permissions) {
        if (this.securityService == null) {
            throw new UnsupportedOperationException("Unsupported operation");
        }
        this.securityService.refreshClientPermissions(permissions);
        return this;
    }

    @Override
    public CredentialsFactoryConfig getMemberCredentialsConfig() {
        return this.staticSecurityConfig.getMemberCredentialsConfig();
    }

    @Override
    public SecurityConfig setMemberCredentialsConfig(CredentialsFactoryConfig credentialsFactoryConfig) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public OnJoinPermissionOperationName getOnJoinPermissionOperation() {
        return this.staticSecurityConfig.getOnJoinPermissionOperation();
    }

    @Override
    public SecurityConfig setOnJoinPermissionOperation(OnJoinPermissionOperationName onJoinPermissionOperation) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DynamicSecurityConfig that = (DynamicSecurityConfig)o;
        return this.staticSecurityConfig != null ? this.staticSecurityConfig.equals(that.staticSecurityConfig) : that.staticSecurityConfig == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.staticSecurityConfig != null ? this.staticSecurityConfig.hashCode() : 0);
        return result;
    }
}

