/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  javax.annotation.Nonnull
 *  lombok.NonNull
 */
package com.atlassian.oauth2.provider.api.client.dao;

import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.scopes.api.Scope;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.NonNull;

public class ClientEntity
implements Client {
    @Nonnull
    private final String id;
    @NonNull
    private final String clientId;
    @NonNull
    private final String clientSecret;
    @NonNull
    private final String name;
    private final List<String> redirects;
    @NonNull
    private final String userKey;
    @NonNull
    private final Scope scope;

    @Override
    @Nonnull
    public List<String> getRedirects() {
        return this.redirects != null ? this.redirects : Collections.emptyList();
    }

    public static ClientEntityBuilder builder() {
        return new ClientEntityBuilder();
    }

    public ClientEntityBuilder toBuilder() {
        return new ClientEntityBuilder().id(this.id).clientId(this.clientId).clientSecret(this.clientSecret).name(this.name).redirects(this.redirects).userKey(this.userKey).scope(this.scope);
    }

    public String toString() {
        return "ClientEntity(id=" + this.getId() + ", clientId=" + this.getClientId() + ", clientSecret=" + this.getClientSecret() + ", name=" + this.getName() + ", redirects=" + this.getRedirects() + ", userKey=" + this.getUserKey() + ", scope=" + this.getScope() + ")";
    }

    @Override
    @Nonnull
    public String getId() {
        return this.id;
    }

    @Override
    @NonNull
    public String getClientId() {
        return this.clientId;
    }

    @Override
    @NonNull
    public String getClientSecret() {
        return this.clientSecret;
    }

    @Override
    @NonNull
    public String getName() {
        return this.name;
    }

    @Override
    @NonNull
    public String getUserKey() {
        return this.userKey;
    }

    @Override
    @NonNull
    public Scope getScope() {
        return this.scope;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ClientEntity)) {
            return false;
        }
        ClientEntity other = (ClientEntity)o;
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
        Scope this$scope = this.getScope();
        Scope other$scope = other.getScope();
        return !(this$scope == null ? other$scope != null : !this$scope.equals(other$scope));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ClientEntity;
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
        Scope $scope = this.getScope();
        result = result * 59 + ($scope == null ? 43 : $scope.hashCode());
        return result;
    }

    public ClientEntity(@Nonnull String id, @NonNull String clientId, @NonNull String clientSecret, @NonNull String name, List<String> redirects, @NonNull String userKey, @NonNull Scope scope) {
        if (id == null) {
            throw new NullPointerException("id is marked non-null but is null");
        }
        if (clientId == null) {
            throw new NullPointerException("clientId is marked non-null but is null");
        }
        if (clientSecret == null) {
            throw new NullPointerException("clientSecret is marked non-null but is null");
        }
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        }
        if (userKey == null) {
            throw new NullPointerException("userKey is marked non-null but is null");
        }
        if (scope == null) {
            throw new NullPointerException("scope is marked non-null but is null");
        }
        this.id = id;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.name = name;
        this.redirects = redirects;
        this.userKey = userKey;
        this.scope = scope;
    }

    public static class ClientEntityBuilder {
        private String id;
        private String clientId;
        private String clientSecret;
        private String name;
        private List<String> redirects;
        private String userKey;
        private Scope scope;

        ClientEntityBuilder() {
        }

        public ClientEntityBuilder id(@Nonnull String id) {
            this.id = id;
            return this;
        }

        public ClientEntityBuilder clientId(@NonNull String clientId) {
            if (clientId == null) {
                throw new NullPointerException("clientId is marked non-null but is null");
            }
            this.clientId = clientId;
            return this;
        }

        public ClientEntityBuilder clientSecret(@NonNull String clientSecret) {
            if (clientSecret == null) {
                throw new NullPointerException("clientSecret is marked non-null but is null");
            }
            this.clientSecret = clientSecret;
            return this;
        }

        public ClientEntityBuilder name(@NonNull String name) {
            if (name == null) {
                throw new NullPointerException("name is marked non-null but is null");
            }
            this.name = name;
            return this;
        }

        public ClientEntityBuilder redirects(List<String> redirects) {
            this.redirects = redirects;
            return this;
        }

        public ClientEntityBuilder userKey(@NonNull String userKey) {
            if (userKey == null) {
                throw new NullPointerException("userKey is marked non-null but is null");
            }
            this.userKey = userKey;
            return this;
        }

        public ClientEntityBuilder scope(@NonNull Scope scope) {
            if (scope == null) {
                throw new NullPointerException("scope is marked non-null but is null");
            }
            this.scope = scope;
            return this;
        }

        public ClientEntity build() {
            return new ClientEntity(this.id, this.clientId, this.clientSecret, this.name, this.redirects, this.userKey, this.scope);
        }

        public String toString() {
            return "ClientEntity.ClientEntityBuilder(id=" + this.id + ", clientId=" + this.clientId + ", clientSecret=" + this.clientSecret + ", name=" + this.name + ", redirects=" + this.redirects + ", userKey=" + this.userKey + ", scope=" + this.scope + ")";
        }
    }
}

