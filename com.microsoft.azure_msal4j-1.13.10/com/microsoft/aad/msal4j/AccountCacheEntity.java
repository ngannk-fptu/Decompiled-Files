/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.aad.msal4j.Account;
import com.microsoft.aad.msal4j.Authority;
import com.microsoft.aad.msal4j.ClientInfo;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IdToken;
import com.microsoft.aad.msal4j.StringHelper;
import java.io.Serializable;
import java.util.ArrayList;

class AccountCacheEntity
implements Serializable {
    static final String MSSTS_ACCOUNT_TYPE = "MSSTS";
    static final String ADFS_ACCOUNT_TYPE = "ADFS";
    @JsonProperty(value="home_account_id")
    protected String homeAccountId;
    @JsonProperty(value="environment")
    protected String environment;
    @JsonProperty(value="realm")
    protected String realm;
    @JsonProperty(value="local_account_id")
    protected String localAccountId;
    @JsonProperty(value="username")
    protected String username;
    @JsonProperty(value="name")
    protected String name;
    @JsonProperty(value="client_info")
    protected String clientInfoStr;
    @JsonProperty(value="user_assertion_hash")
    protected String userAssertionHash;
    @JsonProperty(value="authority_type")
    protected String authorityType;

    AccountCacheEntity() {
    }

    ClientInfo clientInfo() {
        return ClientInfo.createFromJson(this.clientInfoStr);
    }

    String getKey() {
        ArrayList<String> keyParts = new ArrayList<String>();
        keyParts.add(this.homeAccountId);
        keyParts.add(this.environment);
        keyParts.add(StringHelper.isBlank(this.realm) ? "" : this.realm);
        return String.join((CharSequence)"-", keyParts).toLowerCase();
    }

    static AccountCacheEntity create(String clientInfoStr, Authority requestAuthority, IdToken idToken, String policy) {
        AccountCacheEntity account = new AccountCacheEntity();
        account.authorityType(MSSTS_ACCOUNT_TYPE);
        account.clientInfoStr = clientInfoStr;
        account.homeAccountId(policy != null ? account.clientInfo().toAccountIdentifier() + "-" + policy : account.clientInfo().toAccountIdentifier());
        account.environment(requestAuthority.host());
        account.realm(requestAuthority.tenant());
        if (idToken != null) {
            String localAccountId = !StringHelper.isBlank(idToken.objectIdentifier) ? idToken.objectIdentifier : idToken.subject;
            account.localAccountId(localAccountId);
            account.username(idToken.preferredUsername);
            account.name(idToken.name);
        }
        return account;
    }

    static AccountCacheEntity createADFSAccount(Authority requestAuthority, IdToken idToken) {
        AccountCacheEntity account = new AccountCacheEntity();
        account.authorityType(ADFS_ACCOUNT_TYPE);
        account.homeAccountId(idToken.subject);
        account.environment(requestAuthority.host());
        account.username(idToken.upn);
        account.name(idToken.uniqueName);
        return account;
    }

    static AccountCacheEntity create(String clientInfoStr, Authority requestAuthority, IdToken idToken) {
        return AccountCacheEntity.create(clientInfoStr, requestAuthority, idToken, null);
    }

    IAccount toAccount() {
        return new Account(this.homeAccountId, this.environment, this.username, null);
    }

    public String homeAccountId() {
        return this.homeAccountId;
    }

    public String environment() {
        return this.environment;
    }

    public String realm() {
        return this.realm;
    }

    public String localAccountId() {
        return this.localAccountId;
    }

    public String username() {
        return this.username;
    }

    public String name() {
        return this.name;
    }

    public String clientInfoStr() {
        return this.clientInfoStr;
    }

    public String userAssertionHash() {
        return this.userAssertionHash;
    }

    public String authorityType() {
        return this.authorityType;
    }

    public AccountCacheEntity homeAccountId(String homeAccountId) {
        this.homeAccountId = homeAccountId;
        return this;
    }

    public AccountCacheEntity environment(String environment) {
        this.environment = environment;
        return this;
    }

    public AccountCacheEntity realm(String realm) {
        this.realm = realm;
        return this;
    }

    public AccountCacheEntity localAccountId(String localAccountId) {
        this.localAccountId = localAccountId;
        return this;
    }

    public AccountCacheEntity username(String username) {
        this.username = username;
        return this;
    }

    public AccountCacheEntity name(String name) {
        this.name = name;
        return this;
    }

    public AccountCacheEntity clientInfoStr(String clientInfoStr) {
        this.clientInfoStr = clientInfoStr;
        return this;
    }

    public AccountCacheEntity userAssertionHash(String userAssertionHash) {
        this.userAssertionHash = userAssertionHash;
        return this;
    }

    public AccountCacheEntity authorityType(String authorityType) {
        this.authorityType = authorityType;
        return this;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AccountCacheEntity)) {
            return false;
        }
        AccountCacheEntity other = (AccountCacheEntity)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$homeAccountId = this.homeAccountId();
        String other$homeAccountId = other.homeAccountId();
        if (this$homeAccountId == null ? other$homeAccountId != null : !this$homeAccountId.equals(other$homeAccountId)) {
            return false;
        }
        String this$environment = this.environment();
        String other$environment = other.environment();
        if (this$environment == null ? other$environment != null : !this$environment.equals(other$environment)) {
            return false;
        }
        String this$realm = this.realm();
        String other$realm = other.realm();
        if (this$realm == null ? other$realm != null : !this$realm.equals(other$realm)) {
            return false;
        }
        String this$localAccountId = this.localAccountId();
        String other$localAccountId = other.localAccountId();
        if (this$localAccountId == null ? other$localAccountId != null : !this$localAccountId.equals(other$localAccountId)) {
            return false;
        }
        String this$username = this.username();
        String other$username = other.username();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$name = this.name();
        String other$name = other.name();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$clientInfoStr = this.clientInfoStr();
        String other$clientInfoStr = other.clientInfoStr();
        if (this$clientInfoStr == null ? other$clientInfoStr != null : !this$clientInfoStr.equals(other$clientInfoStr)) {
            return false;
        }
        String this$userAssertionHash = this.userAssertionHash();
        String other$userAssertionHash = other.userAssertionHash();
        if (this$userAssertionHash == null ? other$userAssertionHash != null : !this$userAssertionHash.equals(other$userAssertionHash)) {
            return false;
        }
        String this$authorityType = this.authorityType();
        String other$authorityType = other.authorityType();
        return !(this$authorityType == null ? other$authorityType != null : !this$authorityType.equals(other$authorityType));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AccountCacheEntity;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $homeAccountId = this.homeAccountId();
        result = result * 59 + ($homeAccountId == null ? 43 : $homeAccountId.hashCode());
        String $environment = this.environment();
        result = result * 59 + ($environment == null ? 43 : $environment.hashCode());
        String $realm = this.realm();
        result = result * 59 + ($realm == null ? 43 : $realm.hashCode());
        String $localAccountId = this.localAccountId();
        result = result * 59 + ($localAccountId == null ? 43 : $localAccountId.hashCode());
        String $username = this.username();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $name = this.name();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $clientInfoStr = this.clientInfoStr();
        result = result * 59 + ($clientInfoStr == null ? 43 : $clientInfoStr.hashCode());
        String $userAssertionHash = this.userAssertionHash();
        result = result * 59 + ($userAssertionHash == null ? 43 : $userAssertionHash.hashCode());
        String $authorityType = this.authorityType();
        result = result * 59 + ($authorityType == null ? 43 : $authorityType.hashCode());
        return result;
    }
}

