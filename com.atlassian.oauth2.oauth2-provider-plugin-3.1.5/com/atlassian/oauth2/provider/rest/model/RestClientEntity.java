/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.oauth2.provider.rest.model;

import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class RestClientEntity {
    @JsonProperty
    private String id;
    @JsonProperty
    private String clientId;
    @JsonProperty
    private String clientSecret;
    @JsonProperty
    private String name;
    @JsonProperty
    private List<String> redirects;
    @JsonProperty
    private String userKey;
    @JsonProperty
    private String scope;

    public RestClientEntity(String name, List<String> redirects, String scope) {
        this.name = name;
        this.redirects = redirects;
        this.scope = scope;
    }

    public static RestClientEntityBuilder builder() {
        return new RestClientEntityBuilder();
    }

    public RestClientEntityBuilder toBuilder() {
        return new RestClientEntityBuilder().id(this.id).clientId(this.clientId).clientSecret(this.clientSecret).name(this.name).redirects(this.redirects).userKey(this.userKey).scope(this.scope);
    }

    public String getId() {
        return this.id;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getRedirects() {
        return this.redirects;
    }

    public String getUserKey() {
        return this.userKey;
    }

    public String getScope() {
        return this.scope;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRedirects(List<String> redirects) {
        this.redirects = redirects;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestClientEntity)) {
            return false;
        }
        RestClientEntity other = (RestClientEntity)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$clientId = this.getClientId();
        String other$clientId = other.getClientId();
        if (this$clientId == null ? other$clientId != null : !this$clientId.equals(other$clientId)) {
            return false;
        }
        String this$clientSecret = this.getClientSecret();
        String other$clientSecret = other.getClientSecret();
        if (this$clientSecret == null ? other$clientSecret != null : !this$clientSecret.equals(other$clientSecret)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        List<String> this$redirects = this.getRedirects();
        List<String> other$redirects = other.getRedirects();
        if (this$redirects == null ? other$redirects != null : !((Object)this$redirects).equals(other$redirects)) {
            return false;
        }
        String this$userKey = this.getUserKey();
        String other$userKey = other.getUserKey();
        if (this$userKey == null ? other$userKey != null : !this$userKey.equals(other$userKey)) {
            return false;
        }
        String this$scope = this.getScope();
        String other$scope = other.getScope();
        return !(this$scope == null ? other$scope != null : !this$scope.equals(other$scope));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestClientEntity;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $clientId = this.getClientId();
        result = result * 59 + ($clientId == null ? 43 : $clientId.hashCode());
        String $clientSecret = this.getClientSecret();
        result = result * 59 + ($clientSecret == null ? 43 : $clientSecret.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        List<String> $redirects = this.getRedirects();
        result = result * 59 + ($redirects == null ? 43 : ((Object)$redirects).hashCode());
        String $userKey = this.getUserKey();
        result = result * 59 + ($userKey == null ? 43 : $userKey.hashCode());
        String $scope = this.getScope();
        result = result * 59 + ($scope == null ? 43 : $scope.hashCode());
        return result;
    }

    public String toString() {
        return "RestClientEntity(id=" + this.getId() + ", clientId=" + this.getClientId() + ", clientSecret=" + this.getClientSecret() + ", name=" + this.getName() + ", redirects=" + this.getRedirects() + ", userKey=" + this.getUserKey() + ", scope=" + this.getScope() + ")";
    }

    public RestClientEntity(String id, String clientId, String clientSecret, String name, List<String> redirects, String userKey, String scope) {
        this.id = id;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.name = name;
        this.redirects = redirects;
        this.userKey = userKey;
        this.scope = scope;
    }

    public RestClientEntity() {
    }

    public static class RestClientEntityBuilder {
        private String id;
        private String clientId;
        private String clientSecret;
        private String name;
        private List<String> redirects;
        private String userKey;
        private String scope;

        RestClientEntityBuilder() {
        }

        public RestClientEntityBuilder id(String id) {
            this.id = id;
            return this;
        }

        public RestClientEntityBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public RestClientEntityBuilder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public RestClientEntityBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RestClientEntityBuilder redirects(List<String> redirects) {
            this.redirects = redirects;
            return this;
        }

        public RestClientEntityBuilder userKey(String userKey) {
            this.userKey = userKey;
            return this;
        }

        public RestClientEntityBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public RestClientEntity build() {
            return new RestClientEntity(this.id, this.clientId, this.clientSecret, this.name, this.redirects, this.userKey, this.scope);
        }

        public String toString() {
            return "RestClientEntity.RestClientEntityBuilder(id=" + this.id + ", clientId=" + this.clientId + ", clientSecret=" + this.clientSecret + ", name=" + this.name + ", redirects=" + this.redirects + ", userKey=" + this.userKey + ", scope=" + this.scope + ")";
        }
    }
}

