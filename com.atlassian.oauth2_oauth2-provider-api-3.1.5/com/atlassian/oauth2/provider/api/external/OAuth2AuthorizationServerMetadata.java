/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.atlassian.oauth2.provider.api.external;

import java.util.Set;
import lombok.NonNull;

public class OAuth2AuthorizationServerMetadata {
    @NonNull
    private final String issuer;
    @NonNull
    private final String tokenEndpoint;
    @NonNull
    private final String authorizationEndpoint;
    @NonNull
    private final String revocationEndpoint;
    @NonNull
    private final Set<String> scopesSupported;
    @NonNull
    private final Set<String> responseTypesSupported;
    @NonNull
    private final Set<String> responseModesSupported;
    @NonNull
    private final Set<String> grantTypesSupported;
    @NonNull
    private final Set<String> tokenEndpointAuthMethodsSupported;
    @NonNull
    private final Set<String> revocationEndpointAuthMethodsSupported;

    OAuth2AuthorizationServerMetadata(@NonNull String issuer, @NonNull String tokenEndpoint, @NonNull String authorizationEndpoint, @NonNull String revocationEndpoint, @NonNull Set<String> scopesSupported, @NonNull Set<String> responseTypesSupported, @NonNull Set<String> responseModesSupported, @NonNull Set<String> grantTypesSupported, @NonNull Set<String> tokenEndpointAuthMethodsSupported, @NonNull Set<String> revocationEndpointAuthMethodsSupported) {
        if (issuer == null) {
            throw new NullPointerException("issuer is marked non-null but is null");
        }
        if (tokenEndpoint == null) {
            throw new NullPointerException("tokenEndpoint is marked non-null but is null");
        }
        if (authorizationEndpoint == null) {
            throw new NullPointerException("authorizationEndpoint is marked non-null but is null");
        }
        if (revocationEndpoint == null) {
            throw new NullPointerException("revocationEndpoint is marked non-null but is null");
        }
        if (scopesSupported == null) {
            throw new NullPointerException("scopesSupported is marked non-null but is null");
        }
        if (responseTypesSupported == null) {
            throw new NullPointerException("responseTypesSupported is marked non-null but is null");
        }
        if (responseModesSupported == null) {
            throw new NullPointerException("responseModesSupported is marked non-null but is null");
        }
        if (grantTypesSupported == null) {
            throw new NullPointerException("grantTypesSupported is marked non-null but is null");
        }
        if (tokenEndpointAuthMethodsSupported == null) {
            throw new NullPointerException("tokenEndpointAuthMethodsSupported is marked non-null but is null");
        }
        if (revocationEndpointAuthMethodsSupported == null) {
            throw new NullPointerException("revocationEndpointAuthMethodsSupported is marked non-null but is null");
        }
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

    public static OAuth2AuthorizationServerMetadataBuilder builder() {
        return new OAuth2AuthorizationServerMetadataBuilder();
    }

    @NonNull
    public String getIssuer() {
        return this.issuer;
    }

    @NonNull
    public String getTokenEndpoint() {
        return this.tokenEndpoint;
    }

    @NonNull
    public String getAuthorizationEndpoint() {
        return this.authorizationEndpoint;
    }

    @NonNull
    public String getRevocationEndpoint() {
        return this.revocationEndpoint;
    }

    @NonNull
    public Set<String> getScopesSupported() {
        return this.scopesSupported;
    }

    @NonNull
    public Set<String> getResponseTypesSupported() {
        return this.responseTypesSupported;
    }

    @NonNull
    public Set<String> getResponseModesSupported() {
        return this.responseModesSupported;
    }

    @NonNull
    public Set<String> getGrantTypesSupported() {
        return this.grantTypesSupported;
    }

    @NonNull
    public Set<String> getTokenEndpointAuthMethodsSupported() {
        return this.tokenEndpointAuthMethodsSupported;
    }

    @NonNull
    public Set<String> getRevocationEndpointAuthMethodsSupported() {
        return this.revocationEndpointAuthMethodsSupported;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OAuth2AuthorizationServerMetadata)) {
            return false;
        }
        OAuth2AuthorizationServerMetadata other = (OAuth2AuthorizationServerMetadata)o;
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
        return other instanceof OAuth2AuthorizationServerMetadata;
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
        return "OAuth2AuthorizationServerMetadata(issuer=" + this.getIssuer() + ", tokenEndpoint=" + this.getTokenEndpoint() + ", authorizationEndpoint=" + this.getAuthorizationEndpoint() + ", revocationEndpoint=" + this.getRevocationEndpoint() + ", scopesSupported=" + this.getScopesSupported() + ", responseTypesSupported=" + this.getResponseTypesSupported() + ", responseModesSupported=" + this.getResponseModesSupported() + ", grantTypesSupported=" + this.getGrantTypesSupported() + ", tokenEndpointAuthMethodsSupported=" + this.getTokenEndpointAuthMethodsSupported() + ", revocationEndpointAuthMethodsSupported=" + this.getRevocationEndpointAuthMethodsSupported() + ")";
    }

    public static class OAuth2AuthorizationServerMetadataBuilder {
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

        OAuth2AuthorizationServerMetadataBuilder() {
        }

        public OAuth2AuthorizationServerMetadataBuilder issuer(@NonNull String issuer) {
            if (issuer == null) {
                throw new NullPointerException("issuer is marked non-null but is null");
            }
            this.issuer = issuer;
            return this;
        }

        public OAuth2AuthorizationServerMetadataBuilder tokenEndpoint(@NonNull String tokenEndpoint) {
            if (tokenEndpoint == null) {
                throw new NullPointerException("tokenEndpoint is marked non-null but is null");
            }
            this.tokenEndpoint = tokenEndpoint;
            return this;
        }

        public OAuth2AuthorizationServerMetadataBuilder authorizationEndpoint(@NonNull String authorizationEndpoint) {
            if (authorizationEndpoint == null) {
                throw new NullPointerException("authorizationEndpoint is marked non-null but is null");
            }
            this.authorizationEndpoint = authorizationEndpoint;
            return this;
        }

        public OAuth2AuthorizationServerMetadataBuilder revocationEndpoint(@NonNull String revocationEndpoint) {
            if (revocationEndpoint == null) {
                throw new NullPointerException("revocationEndpoint is marked non-null but is null");
            }
            this.revocationEndpoint = revocationEndpoint;
            return this;
        }

        public OAuth2AuthorizationServerMetadataBuilder scopesSupported(@NonNull Set<String> scopesSupported) {
            if (scopesSupported == null) {
                throw new NullPointerException("scopesSupported is marked non-null but is null");
            }
            this.scopesSupported = scopesSupported;
            return this;
        }

        public OAuth2AuthorizationServerMetadataBuilder responseTypesSupported(@NonNull Set<String> responseTypesSupported) {
            if (responseTypesSupported == null) {
                throw new NullPointerException("responseTypesSupported is marked non-null but is null");
            }
            this.responseTypesSupported = responseTypesSupported;
            return this;
        }

        public OAuth2AuthorizationServerMetadataBuilder responseModesSupported(@NonNull Set<String> responseModesSupported) {
            if (responseModesSupported == null) {
                throw new NullPointerException("responseModesSupported is marked non-null but is null");
            }
            this.responseModesSupported = responseModesSupported;
            return this;
        }

        public OAuth2AuthorizationServerMetadataBuilder grantTypesSupported(@NonNull Set<String> grantTypesSupported) {
            if (grantTypesSupported == null) {
                throw new NullPointerException("grantTypesSupported is marked non-null but is null");
            }
            this.grantTypesSupported = grantTypesSupported;
            return this;
        }

        public OAuth2AuthorizationServerMetadataBuilder tokenEndpointAuthMethodsSupported(@NonNull Set<String> tokenEndpointAuthMethodsSupported) {
            if (tokenEndpointAuthMethodsSupported == null) {
                throw new NullPointerException("tokenEndpointAuthMethodsSupported is marked non-null but is null");
            }
            this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
            return this;
        }

        public OAuth2AuthorizationServerMetadataBuilder revocationEndpointAuthMethodsSupported(@NonNull Set<String> revocationEndpointAuthMethodsSupported) {
            if (revocationEndpointAuthMethodsSupported == null) {
                throw new NullPointerException("revocationEndpointAuthMethodsSupported is marked non-null but is null");
            }
            this.revocationEndpointAuthMethodsSupported = revocationEndpointAuthMethodsSupported;
            return this;
        }

        public OAuth2AuthorizationServerMetadata build() {
            return new OAuth2AuthorizationServerMetadata(this.issuer, this.tokenEndpoint, this.authorizationEndpoint, this.revocationEndpoint, this.scopesSupported, this.responseTypesSupported, this.responseModesSupported, this.grantTypesSupported, this.tokenEndpointAuthMethodsSupported, this.revocationEndpointAuthMethodsSupported);
        }

        public String toString() {
            return "OAuth2AuthorizationServerMetadata.OAuth2AuthorizationServerMetadataBuilder(issuer=" + this.issuer + ", tokenEndpoint=" + this.tokenEndpoint + ", authorizationEndpoint=" + this.authorizationEndpoint + ", revocationEndpoint=" + this.revocationEndpoint + ", scopesSupported=" + this.scopesSupported + ", responseTypesSupported=" + this.responseTypesSupported + ", responseModesSupported=" + this.responseModesSupported + ", grantTypesSupported=" + this.grantTypesSupported + ", tokenEndpointAuthMethodsSupported=" + this.tokenEndpointAuthMethodsSupported + ", revocationEndpointAuthMethodsSupported=" + this.revocationEndpointAuthMethodsSupported + ")";
        }
    }
}

