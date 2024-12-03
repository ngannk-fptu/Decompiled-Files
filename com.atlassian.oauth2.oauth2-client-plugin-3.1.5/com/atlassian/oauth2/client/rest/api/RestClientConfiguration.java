/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.oauth2.client.rest.api;

import com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
public class RestClientConfiguration {
    @XmlElement
    private String id;
    @XmlElement
    private String name;
    @XmlElement
    private String description;
    @XmlElement
    private String type;
    @XmlElement
    private String clientId;
    @XmlElement
    private String clientSecret;
    @XmlElement
    private String authorizationEndpoint;
    @XmlElement
    private String tokenEndpoint;
    @XmlElement
    private List<String> scopes;
    @XmlElement
    private String redirectUriSuffix;

    public RestClientConfiguration() {
    }

    public RestClientConfiguration(String id, String name, String description, String type, String clientId, String clientSecret, String authorizationEndpoint, String tokenEndpoint, List<String> scopes, String redirectUriSuffix) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authorizationEndpoint = authorizationEndpoint;
        this.tokenEndpoint = tokenEndpoint;
        this.scopes = scopes;
        this.redirectUriSuffix = redirectUriSuffix;
    }

    public RestClientConfiguration(RestClientConfiguration configuration) {
        this.id = configuration.getId();
        this.name = configuration.getName();
        this.description = configuration.getDescription();
        this.type = configuration.getType();
        this.clientId = configuration.getClientId();
        this.clientSecret = configuration.getClientSecret();
        this.authorizationEndpoint = configuration.getAuthorizationEndpoint();
        this.tokenEndpoint = configuration.getTokenEndpoint();
        this.scopes = configuration.getScopes();
        this.redirectUriSuffix = configuration.getRedirectUriSuffix();
    }

    private RestClientConfiguration(ClientConfigurationEntity clientConfiguration, String redirectUriSuffix) {
        this.id = clientConfiguration.getId();
        this.name = clientConfiguration.getName();
        this.description = clientConfiguration.getDescription();
        this.type = clientConfiguration.getProviderType().toString();
        this.clientId = clientConfiguration.getClientId();
        this.clientSecret = null;
        this.authorizationEndpoint = clientConfiguration.getAuthorizationEndpoint();
        this.tokenEndpoint = clientConfiguration.getTokenEndpoint();
        this.scopes = ImmutableList.copyOf((Collection)clientConfiguration.getScopes());
        this.redirectUriSuffix = redirectUriSuffix;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAuthorizationEndpoint() {
        return this.authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return this.tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public List<String> getScopes() {
        return this.scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public String getRedirectUriSuffix() {
        return this.redirectUriSuffix;
    }

    public void setRedirectUriSuffix(String redirectUriSuffix) {
        this.redirectUriSuffix = redirectUriSuffix;
    }

    public static RestClientConfiguration valueOf(ClientConfigurationEntity clientConfiguration, String redirectUriSuffix) {
        return new RestClientConfiguration(clientConfiguration, redirectUriSuffix);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RestClientConfiguration that = (RestClientConfiguration)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getName(), that.getName()) && Objects.equals(this.getDescription(), that.getDescription()) && Objects.equals(this.getType(), that.getType()) && Objects.equals(this.getClientId(), that.getClientId()) && Objects.equals(this.getClientSecret(), that.getClientSecret()) && Objects.equals(this.getAuthorizationEndpoint(), that.getAuthorizationEndpoint()) && Objects.equals(this.getTokenEndpoint(), that.getTokenEndpoint()) && Objects.equals(this.getScopes(), that.getScopes()) && Objects.equals(this.getRedirectUriSuffix(), that.getRedirectUriSuffix());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getName(), this.getDescription(), this.getType(), this.getClientId(), this.getClientSecret(), this.getAuthorizationEndpoint(), this.getTokenEndpoint(), this.getScopes(), this.getRedirectUriSuffix());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("name", (Object)this.getName()).add("description", (Object)this.getDescription()).add("type", (Object)this.getType()).add("clientId", (Object)this.getClientId()).add("clientSecret", (Object)"*****").add("authorizationEndpoint", (Object)this.getAuthorizationEndpoint()).add("tokenEndpoint", (Object)this.getTokenEndpoint()).add("scopes", this.getScopes()).add("redirectUriSuffix", (Object)this.getRedirectUriSuffix()).toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(RestClientConfiguration data) {
        return new Builder(data);
    }

    public static final class Builder {
        private String id;
        private String name;
        private String description;
        private String type;
        private String clientId;
        private String clientSecret;
        private String authorizationEndpoint;
        private String tokenEndpoint;
        private List<String> scopes = new ArrayList<String>();
        private String redirectUriSuffix;

        private Builder() {
        }

        private Builder(RestClientConfiguration initialData) {
            this.id = initialData.getId();
            this.name = initialData.getName();
            this.description = initialData.getDescription();
            this.type = initialData.getType();
            this.clientId = initialData.getClientId();
            this.clientSecret = initialData.getClientSecret();
            this.authorizationEndpoint = initialData.getAuthorizationEndpoint();
            this.tokenEndpoint = initialData.getTokenEndpoint();
            this.scopes = new ArrayList<String>(initialData.getScopes());
            this.redirectUriSuffix = initialData.getRedirectUriSuffix();
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder authorizationEndpoint(String authorizationEndpoint) {
            this.authorizationEndpoint = authorizationEndpoint;
            return this;
        }

        public Builder tokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
            return this;
        }

        public Builder scopes(List<String> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder addScope(String scope) {
            this.scopes.add(scope);
            return this;
        }

        public Builder addScopes(Iterable<String> scopes) {
            for (String scope : scopes) {
                this.addScope(scope);
            }
            return this;
        }

        public Builder redirectUriSuffix(String redirectUriSuffix) {
            this.redirectUriSuffix = redirectUriSuffix;
            return this;
        }

        public RestClientConfiguration build() {
            return new RestClientConfiguration(this.id, this.name, this.description, this.type, this.clientId, this.clientSecret, this.authorizationEndpoint, this.tokenEndpoint, this.scopes, this.redirectUriSuffix);
        }
    }
}

