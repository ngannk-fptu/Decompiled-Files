/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.oauth2.provider.rest.model;

import java.util.Set;
import org.codehaus.jackson.annotate.JsonProperty;

public class RestOAuth2AuthorizationServerMetadata {
    @JsonProperty(value="issuer")
    private String issuer;
    @JsonProperty(value="token_endpoint")
    private String tokenEndpoint;
    @JsonProperty(value="authorization_endpoint")
    private String authorizationEndpoint;
    @JsonProperty(value="revocation_endpoint")
    private String revocationEndpoint;
    @JsonProperty(value="scopes_supported")
    private Set<String> scopesSupported;
    @JsonProperty(value="response_types_supported")
    private Set<String> responseTypesSupported;
    @JsonProperty(value="response_modes_supported")
    private Set<String> responseModesSupported;
    @JsonProperty(value="grant_types_supported")
    private Set<String> grantTypesSupported;
    @JsonProperty(value="token_endpoint_auth_methods_supported")
    private Set<String> tokenEndpointAuthMethodsSupported;
    @JsonProperty(value="revocation_endpoint_auth_methods_supported")
    private Set<String> revocationEndpointAuthMethodsSupported;

    public static RestOAuth2AuthorizationServerMetadataBuilder builder() {
        return new RestOAuth2AuthorizationServerMetadataBuilder();
    }

    public String getIssuer() {
        return this.issuer;
    }

    public String getTokenEndpoint() {
        return this.tokenEndpoint;
    }

    public String getAuthorizationEndpoint() {
        return this.authorizationEndpoint;
    }

    public String getRevocationEndpoint() {
        return this.revocationEndpoint;
    }

    public Set<String> getScopesSupported() {
        return this.scopesSupported;
    }

    public Set<String> getResponseTypesSupported() {
        return this.responseTypesSupported;
    }

    public Set<String> getResponseModesSupported() {
        return this.responseModesSupported;
    }

    public Set<String> getGrantTypesSupported() {
        return this.grantTypesSupported;
    }

    public Set<String> getTokenEndpointAuthMethodsSupported() {
        return this.tokenEndpointAuthMethodsSupported;
    }

    public Set<String> getRevocationEndpointAuthMethodsSupported() {
        return this.revocationEndpointAuthMethodsSupported;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public void setRevocationEndpoint(String revocationEndpoint) {
        this.revocationEndpoint = revocationEndpoint;
    }

    public void setScopesSupported(Set<String> scopesSupported) {
        this.scopesSupported = scopesSupported;
    }

    public void setResponseTypesSupported(Set<String> responseTypesSupported) {
        this.responseTypesSupported = responseTypesSupported;
    }

    public void setResponseModesSupported(Set<String> responseModesSupported) {
        this.responseModesSupported = responseModesSupported;
    }

    public void setGrantTypesSupported(Set<String> grantTypesSupported) {
        this.grantTypesSupported = grantTypesSupported;
    }

    public void setTokenEndpointAuthMethodsSupported(Set<String> tokenEndpointAuthMethodsSupported) {
        this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
    }

    public void setRevocationEndpointAuthMethodsSupported(Set<String> revocationEndpointAuthMethodsSupported) {
        this.revocationEndpointAuthMethodsSupported = revocationEndpointAuthMethodsSupported;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestOAuth2AuthorizationServerMetadata)) {
            return false;
        }
        RestOAuth2AuthorizationServerMetadata other = (RestOAuth2AuthorizationServerMetadata)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$issuer = this.getIssuer();
        String other$issuer = other.getIssuer();
        if (this$issuer == null ? other$issuer != null : !this$issuer.equals(other$issuer)) {
            return false;
        }
        String this$tokenEndpoint = this.getTokenEndpoint();
        String other$tokenEndpoint = other.getTokenEndpoint();
        if (this$tokenEndpoint == null ? other$tokenEndpoint != null : !this$tokenEndpoint.equals(other$tokenEndpoint)) {
            return false;
        }
        String this$authorizationEndpoint = this.getAuthorizationEndpoint();
        String other$authorizationEndpoint = other.getAuthorizationEndpoint();
        if (this$authorizationEndpoint == null ? other$authorizationEndpoint != null : !this$authorizationEndpoint.equals(other$authorizationEndpoint)) {
            return false;
        }
        String this$revocationEndpoint = this.getRevocationEndpoint();
        String other$revocationEndpoint = other.getRevocationEndpoint();
        if (this$revocationEndpoint == null ? other$revocationEndpoint != null : !this$revocationEndpoint.equals(other$revocationEndpoint)) {
            return false;
        }
        Set<String> this$scopesSupported = this.getScopesSupported();
        Set<String> other$scopesSupported = other.getScopesSupported();
        if (this$scopesSupported == null ? other$scopesSupported != null : !((Object)this$scopesSupported).equals(other$scopesSupported)) {
            return false;
        }
        Set<String> this$responseTypesSupported = this.getResponseTypesSupported();
        Set<String> other$responseTypesSupported = other.getResponseTypesSupported();
        if (this$responseTypesSupported == null ? other$responseTypesSupported != null : !((Object)this$responseTypesSupported).equals(other$responseTypesSupported)) {
            return false;
        }
        Set<String> this$responseModesSupported = this.getResponseModesSupported();
        Set<String> other$responseModesSupported = other.getResponseModesSupported();
        if (this$responseModesSupported == null ? other$responseModesSupported != null : !((Object)this$responseModesSupported).equals(other$responseModesSupported)) {
            return false;
        }
        Set<String> this$grantTypesSupported = this.getGrantTypesSupported();
        Set<String> other$grantTypesSupported = other.getGrantTypesSupported();
        if (this$grantTypesSupported == null ? other$grantTypesSupported != null : !((Object)this$grantTypesSupported).equals(other$grantTypesSupported)) {
            return false;
        }
        Set<String> this$tokenEndpointAuthMethodsSupported = this.getTokenEndpointAuthMethodsSupported();
        Set<String> other$tokenEndpointAuthMethodsSupported = other.getTokenEndpointAuthMethodsSupported();
        if (this$tokenEndpointAuthMethodsSupported == null ? other$tokenEndpointAuthMethodsSupported != null : !((Object)this$tokenEndpointAuthMethodsSupported).equals(other$tokenEndpointAuthMethodsSupported)) {
            return false;
        }
        Set<String> this$revocationEndpointAuthMethodsSupported = this.getRevocationEndpointAuthMethodsSupported();
        Set<String> other$revocationEndpointAuthMethodsSupported = other.getRevocationEndpointAuthMethodsSupported();
        return !(this$revocationEndpointAuthMethodsSupported == null ? other$revocationEndpointAuthMethodsSupported != null : !((Object)this$revocationEndpointAuthMethodsSupported).equals(other$revocationEndpointAuthMethodsSupported));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestOAuth2AuthorizationServerMetadata;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $issuer = this.getIssuer();
        result = result * 59 + ($issuer == null ? 43 : $issuer.hashCode());
        String $tokenEndpoint = this.getTokenEndpoint();
        result = result * 59 + ($tokenEndpoint == null ? 43 : $tokenEndpoint.hashCode());
        String $authorizationEndpoint = this.getAuthorizationEndpoint();
        result = result * 59 + ($authorizationEndpoint == null ? 43 : $authorizationEndpoint.hashCode());
        String $revocationEndpoint = this.getRevocationEndpoint();
        result = result * 59 + ($revocationEndpoint == null ? 43 : $revocationEndpoint.hashCode());
        Set<String> $scopesSupported = this.getScopesSupported();
        result = result * 59 + ($scopesSupported == null ? 43 : ((Object)$scopesSupported).hashCode());
        Set<String> $responseTypesSupported = this.getResponseTypesSupported();
        result = result * 59 + ($responseTypesSupported == null ? 43 : ((Object)$responseTypesSupported).hashCode());
        Set<String> $responseModesSupported = this.getResponseModesSupported();
        result = result * 59 + ($responseModesSupported == null ? 43 : ((Object)$responseModesSupported).hashCode());
        Set<String> $grantTypesSupported = this.getGrantTypesSupported();
        result = result * 59 + ($grantTypesSupported == null ? 43 : ((Object)$grantTypesSupported).hashCode());
        Set<String> $tokenEndpointAuthMethodsSupported = this.getTokenEndpointAuthMethodsSupported();
        result = result * 59 + ($tokenEndpointAuthMethodsSupported == null ? 43 : ((Object)$tokenEndpointAuthMethodsSupported).hashCode());
        Set<String> $revocationEndpointAuthMethodsSupported = this.getRevocationEndpointAuthMethodsSupported();
        result = result * 59 + ($revocationEndpointAuthMethodsSupported == null ? 43 : ((Object)$revocationEndpointAuthMethodsSupported).hashCode());
        return result;
    }

    public String toString() {
        return "RestOAuth2AuthorizationServerMetadata(issuer=" + this.getIssuer() + ", tokenEndpoint=" + this.getTokenEndpoint() + ", authorizationEndpoint=" + this.getAuthorizationEndpoint() + ", revocationEndpoint=" + this.getRevocationEndpoint() + ", scopesSupported=" + this.getScopesSupported() + ", responseTypesSupported=" + this.getResponseTypesSupported() + ", responseModesSupported=" + this.getResponseModesSupported() + ", grantTypesSupported=" + this.getGrantTypesSupported() + ", tokenEndpointAuthMethodsSupported=" + this.getTokenEndpointAuthMethodsSupported() + ", revocationEndpointAuthMethodsSupported=" + this.getRevocationEndpointAuthMethodsSupported() + ")";
    }

    public RestOAuth2AuthorizationServerMetadata(String issuer, String tokenEndpoint, String authorizationEndpoint, String revocationEndpoint, Set<String> scopesSupported, Set<String> responseTypesSupported, Set<String> responseModesSupported, Set<String> grantTypesSupported, Set<String> tokenEndpointAuthMethodsSupported, Set<String> revocationEndpointAuthMethodsSupported) {
        this.issuer = issuer;
        this.tokenEndpoint = tokenEndpoint;
        this.authorizationEndpoint = authorizationEndpoint;
        this.revocationEndpoint = revocationEndpoint;
        this.scopesSupported = scopesSupported;
        this.responseTypesSupported = responseTypesSupported;
        this.responseModesSupported = responseModesSupported;
        this.grantTypesSupported = grantTypesSupported;
        this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
        this.revocationEndpointAuthMethodsSupported = revocationEndpointAuthMethodsSupported;
    }

    public RestOAuth2AuthorizationServerMetadata() {
    }

    public static class RestOAuth2AuthorizationServerMetadataBuilder {
        private String issuer;
        private String tokenEndpoint;
        private String authorizationEndpoint;
        private String revocationEndpoint;
        private Set<String> scopesSupported;
        private Set<String> responseTypesSupported;
        private Set<String> responseModesSupported;
        private Set<String> grantTypesSupported;
        private Set<String> tokenEndpointAuthMethodsSupported;
        private Set<String> revocationEndpointAuthMethodsSupported;

        RestOAuth2AuthorizationServerMetadataBuilder() {
        }

        public RestOAuth2AuthorizationServerMetadataBuilder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public RestOAuth2AuthorizationServerMetadataBuilder tokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
            return this;
        }

        public RestOAuth2AuthorizationServerMetadataBuilder authorizationEndpoint(String authorizationEndpoint) {
            this.authorizationEndpoint = authorizationEndpoint;
            return this;
        }

        public RestOAuth2AuthorizationServerMetadataBuilder revocationEndpoint(String revocationEndpoint) {
            this.revocationEndpoint = revocationEndpoint;
            return this;
        }

        public RestOAuth2AuthorizationServerMetadataBuilder scopesSupported(Set<String> scopesSupported) {
            this.scopesSupported = scopesSupported;
            return this;
        }

        public RestOAuth2AuthorizationServerMetadataBuilder responseTypesSupported(Set<String> responseTypesSupported) {
            this.responseTypesSupported = responseTypesSupported;
            return this;
        }

        public RestOAuth2AuthorizationServerMetadataBuilder responseModesSupported(Set<String> responseModesSupported) {
            this.responseModesSupported = responseModesSupported;
            return this;
        }

        public RestOAuth2AuthorizationServerMetadataBuilder grantTypesSupported(Set<String> grantTypesSupported) {
            this.grantTypesSupported = grantTypesSupported;
            return this;
        }

        public RestOAuth2AuthorizationServerMetadataBuilder tokenEndpointAuthMethodsSupported(Set<String> tokenEndpointAuthMethodsSupported) {
            this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
            return this;
        }

        public RestOAuth2AuthorizationServerMetadataBuilder revocationEndpointAuthMethodsSupported(Set<String> revocationEndpointAuthMethodsSupported) {
            this.revocationEndpointAuthMethodsSupported = revocationEndpointAuthMethodsSupported;
            return this;
        }

        public RestOAuth2AuthorizationServerMetadata build() {
            return new RestOAuth2AuthorizationServerMetadata(this.issuer, this.tokenEndpoint, this.authorizationEndpoint, this.revocationEndpoint, this.scopesSupported, this.responseTypesSupported, this.responseModesSupported, this.grantTypesSupported, this.tokenEndpointAuthMethodsSupported, this.revocationEndpointAuthMethodsSupported);
        }

        public String toString() {
            return "RestOAuth2AuthorizationServerMetadata.RestOAuth2AuthorizationServerMetadataBuilder(issuer=" + this.issuer + ", tokenEndpoint=" + this.tokenEndpoint + ", authorizationEndpoint=" + this.authorizationEndpoint + ", revocationEndpoint=" + this.revocationEndpoint + ", scopesSupported=" + this.scopesSupported + ", responseTypesSupported=" + this.responseTypesSupported + ", responseModesSupported=" + this.responseModesSupported + ", grantTypesSupported=" + this.grantTypesSupported + ", tokenEndpointAuthMethodsSupported=" + this.tokenEndpointAuthMethodsSupported + ", revocationEndpointAuthMethodsSupported=" + this.revocationEndpointAuthMethodsSupported + ")";
        }
    }
}

