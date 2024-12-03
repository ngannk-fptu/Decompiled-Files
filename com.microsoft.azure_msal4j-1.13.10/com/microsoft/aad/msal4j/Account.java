/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.ITenantProfile;
import java.util.Map;

class Account
implements IAccount {
    String homeAccountId;
    String environment;
    String username;
    Map<String, ITenantProfile> tenantProfiles;

    @Override
    public Map<String, ITenantProfile> getTenantProfiles() {
        return this.tenantProfiles;
    }

    @Override
    public String homeAccountId() {
        return this.homeAccountId;
    }

    @Override
    public String environment() {
        return this.environment;
    }

    @Override
    public String username() {
        return this.username;
    }

    public Map<String, ITenantProfile> tenantProfiles() {
        return this.tenantProfiles;
    }

    public Account homeAccountId(String homeAccountId) {
        this.homeAccountId = homeAccountId;
        return this;
    }

    public Account environment(String environment) {
        this.environment = environment;
        return this;
    }

    public Account username(String username) {
        this.username = username;
        return this;
    }

    public Account tenantProfiles(Map<String, ITenantProfile> tenantProfiles) {
        this.tenantProfiles = tenantProfiles;
        return this;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Account)) {
            return false;
        }
        Account other = (Account)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$homeAccountId = this.homeAccountId();
        String other$homeAccountId = other.homeAccountId();
        return !(this$homeAccountId == null ? other$homeAccountId != null : !this$homeAccountId.equals(other$homeAccountId));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Account;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $homeAccountId = this.homeAccountId();
        result = result * 59 + ($homeAccountId == null ? 43 : $homeAccountId.hashCode());
        return result;
    }

    public Account(String homeAccountId, String environment, String username, Map<String, ITenantProfile> tenantProfiles) {
        this.homeAccountId = homeAccountId;
        this.environment = environment;
        this.username = username;
        this.tenantProfiles = tenantProfiles;
    }
}

