/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Mutator
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.AutoIncrement
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 *  net.java.ao.schema.Unique
 */
package com.atlassian.plugins.authentication.impl.config;

import java.util.Date;
import net.java.ao.Accessor;
import net.java.ao.Mutator;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;
import net.java.ao.schema.Unique;

@Table(value="IDP_CONFIG")
public interface IdpConfigEntity
extends RawEntity<Long> {
    public static final String TABLE_NAME = "IDP_CONFIG";
    public static final String USER_PROVISIONING_ENABLED = "USER_PROVISIONING_ENABLED";
    public static final String SSO_TYPE = "SSO_TYPE";
    public static final String NAME = "NAME";
    public static final String ENABLED = "ENABLED";
    public static final String INCLUDE_CUSTOMER_LOGINS = "INCLUDE_CUSTOMER_LOGINS";
    public static final String ENABLE_REMEMBER_ME = "ENABLE_REMEMBER_ME";
    public static final String LAST_UPDATED = "LAST_UPDATED";
    public static final String BUTTON_TEXT = "BUTTON_TEXT";
    public static final String ISSUER = "ISSUER";

    @AutoIncrement
    @NotNull
    @PrimaryKey(value="ID")
    public long getID();

    @Accessor(value="SSO_TYPE")
    public String getSsoType();

    @Mutator(value="SSO_TYPE")
    public void setSsoType(String var1);

    @Accessor(value="NAME")
    @NotNull
    @Unique
    public String getName();

    @Mutator(value="NAME")
    public void setName(String var1);

    @Accessor(value="ENABLED")
    @NotNull
    public Boolean isEnabled();

    @Mutator(value="ENABLED")
    public void setEnabled(Boolean var1);

    @Accessor(value="USER_PROVISIONING_ENABLED")
    public Boolean isUserProvisioningEnabled();

    @Mutator(value="USER_PROVISIONING_ENABLED")
    public void setUserProvisioning(boolean var1);

    @Accessor(value="LAST_UPDATED")
    public Date getLastUpdated();

    @Mutator(value="LAST_UPDATED")
    public void setLastUpdated(Date var1);

    @Accessor(value="BUTTON_TEXT")
    @NotNull
    @Unique
    public String getButtonText();

    @Mutator(value="BUTTON_TEXT")
    public void setButtonText(String var1);

    @Accessor(value="ISSUER")
    @NotNull
    @Unique
    public String getIssuer();

    @Mutator(value="ISSUER")
    public void setIssuer(String var1);

    @Accessor(value="SAML_IDP_TYPE")
    public String getIdpType();

    @Mutator(value="SAML_IDP_TYPE")
    public void setIdpType(String var1);

    @Accessor(value="SSO_URL")
    public String getSsoUrl();

    @Mutator(value="SSO_URL")
    public void setSsoUrl(String var1);

    @Accessor(value="SIGNING_CERT")
    @StringLength(value=-1)
    public String getCertificate();

    @Mutator(value="SIGNING_CERT")
    @StringLength(value=-1)
    public void setCertificate(String var1);

    @Accessor(value="USERNAME_ATTRIBUTE")
    public String getUserAttribute();

    @Mutator(value="USERNAME_ATTRIBUTE")
    public void setUserAttribute(String var1);

    @Accessor(value="CLIENT_ID")
    public String getClientId();

    @Mutator(value="CLIENT_ID")
    public void setClientId(String var1);

    @Accessor(value="CLIENT_SECRET")
    public String getClientSecret();

    @Mutator(value="CLIENT_SECRET")
    public void setClientSecret(String var1);

    @Accessor(value="AUTHORIZATION_ENDPOINT")
    public String getAuthorizationEndpoint();

    @Mutator(value="AUTHORIZATION_ENDPOINT")
    public void setAuthorizationEndpoint(String var1);

    @Accessor(value="TOKEN_ENDPOINT")
    public String getTokenEndpoint();

    @Mutator(value="TOKEN_ENDPOINT")
    public void setTokenEndpoint(String var1);

    @Accessor(value="USER_INFO_ENDPOINT")
    public String getUserInfoEndpoint();

    @Mutator(value="USER_INFO_ENDPOINT")
    public void setUserInfoEndpoint(String var1);

    @Accessor(value="USE_DISCOVERY")
    public boolean isUseDiscovery();

    @Mutator(value="USE_DISCOVERY")
    public void setUseDiscovery(boolean var1);

    @Accessor(value="INCLUDE_CUSTOMER_LOGINS")
    public boolean isIncludeCustomerLogins();

    @Mutator(value="INCLUDE_CUSTOMER_LOGINS")
    public void setIncludeCustomerLogins(boolean var1);

    @Accessor(value="ENABLE_REMEMBER_ME")
    public boolean isEnableRememberMe();

    @Mutator(value="ENABLE_REMEMBER_ME")
    public void setEnableRememberMe(boolean var1);

    @Accessor(value="ADDITIONAL_SCOPES")
    @StringLength(value=-1)
    public String getAdditionalScopes();

    @Mutator(value="ADDITIONAL_SCOPES")
    @StringLength(value=-1)
    public void setAdditionalScopes(String var1);

    @Accessor(value="USERNAME_CLAIM")
    public String getUsernameClaim();

    @Mutator(value="USERNAME_CLAIM")
    public void setUsernameClaim(String var1);

    @Accessor(value="MAPPING_DISPLAYNAME")
    public String getDisplayNameMapping();

    @Mutator(value="MAPPING_DISPLAYNAME")
    public void setDisplayNameMapping(String var1);

    @Accessor(value="MAPPING_EMAIL")
    public String getEmailMapping();

    @Mutator(value="MAPPING_EMAIL")
    public void setEmailMapping(String var1);

    @Accessor(value="MAPPING_GROUPS")
    public String getGroupsMapping();

    @Mutator(value="MAPPING_GROUPS")
    public void setGroupsMapping(String var1);

    @Accessor(value="ADDITIONAL_JIT_SCOPES")
    @StringLength(value=-1)
    public String getAdditionalJitScopes();

    @Mutator(value="ADDITIONAL_JIT_SCOPES")
    @StringLength(value=-1)
    public void setAdditionalJitScopes(String var1);

    public static interface Oidc {
        public static final String CLIENT_ID = "CLIENT_ID";
        public static final String CLIENT_SECRET = "CLIENT_SECRET";
        public static final String AUTHORIZATION_ENDPOINT = "AUTHORIZATION_ENDPOINT";
        public static final String TOKEN_ENDPOINT = "TOKEN_ENDPOINT";
        public static final String USER_INFO_ENDPOINT = "USER_INFO_ENDPOINT";
        public static final String USE_DISCOVERY = "USE_DISCOVERY";
        public static final String ADDITIONAL_SCOPES = "ADDITIONAL_SCOPES";
        public static final String USERNAME_CLAIM = "USERNAME_CLAIM";
    }

    public static interface Saml {
        public static final String IDP_TYPE = "SAML_IDP_TYPE";
        public static final String SSO_URL = "SSO_URL";
        public static final String CERTIFICATE = "SIGNING_CERT";
        public static final String USERNAME_ATTRIBUTE = "USERNAME_ATTRIBUTE";
    }

    public static interface Jit {
        public static final String ADDITIONAL_JIT_SCOPES = "ADDITIONAL_JIT_SCOPES";
        public static final String DISPLAY_NAME_MAPPING = "MAPPING_DISPLAYNAME";
        public static final String EMAIL_MAPPING = "MAPPING_EMAIL";
        public static final String GROUPS_MAPPING = "MAPPING_GROUPS";
    }
}

