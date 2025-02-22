/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.CredentialsFactoryConfig;
import com.hazelcast.config.LoginModuleConfig;
import com.hazelcast.config.OnJoinPermissionOperationName;
import com.hazelcast.config.PermissionConfig;
import com.hazelcast.config.PermissionPolicyConfig;
import com.hazelcast.config.SecurityInterceptorConfig;
import com.hazelcast.util.Preconditions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SecurityConfig {
    private static final boolean DEFAULT_CLIENT_BLOCK_UNMAPPED_ACTIONS = true;
    private boolean enabled;
    private CredentialsFactoryConfig memberCredentialsConfig = new CredentialsFactoryConfig();
    private List<LoginModuleConfig> memberLoginModuleConfigs = new ArrayList<LoginModuleConfig>();
    private List<SecurityInterceptorConfig> securityInterceptorConfigs = new ArrayList<SecurityInterceptorConfig>();
    private List<LoginModuleConfig> clientLoginModuleConfigs = new ArrayList<LoginModuleConfig>();
    private PermissionPolicyConfig clientPolicyConfig = new PermissionPolicyConfig();
    private Set<PermissionConfig> clientPermissionConfigs = new HashSet<PermissionConfig>();
    private boolean clientBlockUnmappedActions = true;
    private OnJoinPermissionOperationName onJoinPermissionOperation = OnJoinPermissionOperationName.RECEIVE;

    public SecurityConfig addSecurityInterceptorConfig(SecurityInterceptorConfig interceptorConfig) {
        this.securityInterceptorConfigs.add(interceptorConfig);
        return this;
    }

    public List<SecurityInterceptorConfig> getSecurityInterceptorConfigs() {
        return this.securityInterceptorConfigs;
    }

    public void setSecurityInterceptorConfigs(List<SecurityInterceptorConfig> securityInterceptorConfigs) {
        this.securityInterceptorConfigs = securityInterceptorConfigs;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public SecurityConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public SecurityConfig addMemberLoginModuleConfig(LoginModuleConfig loginModuleConfig) {
        this.memberLoginModuleConfigs.add(loginModuleConfig);
        return this;
    }

    public SecurityConfig addClientLoginModuleConfig(LoginModuleConfig loginModuleConfig) {
        this.clientLoginModuleConfigs.add(loginModuleConfig);
        return this;
    }

    public SecurityConfig addClientPermissionConfig(PermissionConfig permissionConfig) {
        this.clientPermissionConfigs.add(permissionConfig);
        return this;
    }

    public List<LoginModuleConfig> getClientLoginModuleConfigs() {
        return this.clientLoginModuleConfigs;
    }

    public SecurityConfig setClientLoginModuleConfigs(List<LoginModuleConfig> loginModuleConfigs) {
        this.clientLoginModuleConfigs = loginModuleConfigs;
        return this;
    }

    public List<LoginModuleConfig> getMemberLoginModuleConfigs() {
        return this.memberLoginModuleConfigs;
    }

    public SecurityConfig setMemberLoginModuleConfigs(List<LoginModuleConfig> memberLoginModuleConfigs) {
        this.memberLoginModuleConfigs = memberLoginModuleConfigs;
        return this;
    }

    public PermissionPolicyConfig getClientPolicyConfig() {
        return this.clientPolicyConfig;
    }

    public SecurityConfig setClientPolicyConfig(PermissionPolicyConfig policyConfig) {
        this.clientPolicyConfig = policyConfig;
        return this;
    }

    public Set<PermissionConfig> getClientPermissionConfigs() {
        return this.clientPermissionConfigs;
    }

    public SecurityConfig setClientPermissionConfigs(Set<PermissionConfig> permissions) {
        this.clientPermissionConfigs = permissions;
        return this;
    }

    public CredentialsFactoryConfig getMemberCredentialsConfig() {
        return this.memberCredentialsConfig;
    }

    public SecurityConfig setMemberCredentialsConfig(CredentialsFactoryConfig credentialsFactoryConfig) {
        this.memberCredentialsConfig = credentialsFactoryConfig;
        return this;
    }

    public OnJoinPermissionOperationName getOnJoinPermissionOperation() {
        return this.onJoinPermissionOperation;
    }

    public SecurityConfig setOnJoinPermissionOperation(OnJoinPermissionOperationName onJoinPermissionOperation) {
        this.onJoinPermissionOperation = Preconditions.checkNotNull(onJoinPermissionOperation, "Existing " + OnJoinPermissionOperationName.class.getSimpleName() + " value has to be provided.");
        return this;
    }

    public boolean getClientBlockUnmappedActions() {
        return this.clientBlockUnmappedActions;
    }

    public SecurityConfig setClientBlockUnmappedActions(boolean clientBlockUnmappedActions) {
        this.clientBlockUnmappedActions = clientBlockUnmappedActions;
        return this;
    }

    public String toString() {
        return "SecurityConfig{enabled=" + this.enabled + ", memberCredentialsConfig=" + this.memberCredentialsConfig + ", memberLoginModuleConfigs=" + this.memberLoginModuleConfigs + ", clientLoginModuleConfigs=" + this.clientLoginModuleConfigs + ", clientPolicyConfig=" + this.clientPolicyConfig + ", clientPermissionConfigs=" + this.clientPermissionConfigs + ", clientBlockUnmappedActions=" + this.clientBlockUnmappedActions + ", onJoinPermissionOperation=" + (Object)((Object)this.onJoinPermissionOperation) + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SecurityConfig that = (SecurityConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.clientBlockUnmappedActions != that.clientBlockUnmappedActions) {
            return false;
        }
        if (this.memberCredentialsConfig != null ? !this.memberCredentialsConfig.equals(that.memberCredentialsConfig) : that.memberCredentialsConfig != null) {
            return false;
        }
        if (this.memberLoginModuleConfigs != null ? !this.memberLoginModuleConfigs.equals(that.memberLoginModuleConfigs) : that.memberLoginModuleConfigs != null) {
            return false;
        }
        if (this.securityInterceptorConfigs != null ? !this.securityInterceptorConfigs.equals(that.securityInterceptorConfigs) : that.securityInterceptorConfigs != null) {
            return false;
        }
        if (this.clientLoginModuleConfigs != null ? !this.clientLoginModuleConfigs.equals(that.clientLoginModuleConfigs) : that.clientLoginModuleConfigs != null) {
            return false;
        }
        if (this.clientPolicyConfig != null ? !this.clientPolicyConfig.equals(that.clientPolicyConfig) : that.clientPolicyConfig != null) {
            return false;
        }
        if (this.onJoinPermissionOperation != that.onJoinPermissionOperation) {
            return false;
        }
        return this.clientPermissionConfigs != null ? this.clientPermissionConfigs.equals(that.clientPermissionConfigs) : that.clientPermissionConfigs == null;
    }

    public int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.memberCredentialsConfig != null ? this.memberCredentialsConfig.hashCode() : 0);
        result = 31 * result + (this.memberLoginModuleConfigs != null ? this.memberLoginModuleConfigs.hashCode() : 0);
        result = 31 * result + (this.securityInterceptorConfigs != null ? this.securityInterceptorConfigs.hashCode() : 0);
        result = 31 * result + (this.clientLoginModuleConfigs != null ? this.clientLoginModuleConfigs.hashCode() : 0);
        result = 31 * result + (this.clientPolicyConfig != null ? this.clientPolicyConfig.hashCode() : 0);
        result = 31 * result + (this.clientPermissionConfigs != null ? this.clientPermissionConfigs.hashCode() : 0);
        result = 31 * result + (this.clientBlockUnmappedActions ? 1 : 0);
        result = 31 * result + this.onJoinPermissionOperation.ordinal();
        return result;
    }
}

