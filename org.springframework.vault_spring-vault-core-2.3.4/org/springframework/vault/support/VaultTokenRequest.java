/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.vault.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class VaultTokenRequest {
    @Nullable
    private final String id;
    private final List<String> policies;
    private final Map<String, String> meta;
    @JsonProperty(value="no_parent")
    private final boolean noParent;
    @JsonProperty(value="no_default_policy")
    private final boolean noDefaultPolicy;
    private final boolean renewable;
    @Nullable
    private final String ttl;
    @JsonProperty(value="explicit_max_ttl")
    @Nullable
    private final String explicitMaxTtl;
    @JsonProperty(value="display_name")
    private final String displayName;
    @JsonProperty(value="num_uses")
    private final int numUses;

    VaultTokenRequest(@Nullable String id, List<String> policies, Map<String, String> meta, boolean noParent, boolean noDefaultPolicy, boolean renewable, @Nullable String ttl, @Nullable String explicitMaxTtl, String displayName, int numUses) {
        this.id = id;
        this.policies = policies;
        this.meta = meta;
        this.noParent = noParent;
        this.noDefaultPolicy = noDefaultPolicy;
        this.renewable = renewable;
        this.ttl = ttl;
        this.explicitMaxTtl = explicitMaxTtl;
        this.displayName = displayName;
        this.numUses = numUses;
    }

    public static VaultTokenRequestBuilder builder() {
        return new VaultTokenRequestBuilder();
    }

    @Nullable
    public String getId() {
        return this.id;
    }

    public List<String> getPolicies() {
        return this.policies;
    }

    public Map<String, String> getMeta() {
        return this.meta;
    }

    public boolean getNoParent() {
        return this.noParent;
    }

    public boolean getNoDefaultPolicy() {
        return this.noDefaultPolicy;
    }

    public boolean getRenewable() {
        return this.renewable;
    }

    @Nullable
    public String getTtl() {
        return this.ttl;
    }

    @Nullable
    public String getExplicitMaxTtl() {
        return this.explicitMaxTtl;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getNumUses() {
        return this.numUses;
    }

    public static class VaultTokenRequestBuilder {
        @Nullable
        private String id;
        private List<String> policies = new ArrayList<String>();
        private Map<String, String> meta = new LinkedHashMap<String, String>();
        private boolean noParent;
        private boolean noDefaultPolicy;
        private boolean renewable;
        @Nullable
        private String ttl;
        @Nullable
        private String explicitMaxTtl;
        private String displayName = "";
        private int numUses;

        VaultTokenRequestBuilder() {
        }

        public VaultTokenRequestBuilder id(String id) {
            this.id = id;
            return this;
        }

        public VaultTokenRequestBuilder policies(Iterable<String> policies) {
            Assert.notNull(policies, (String)"Policies must not be null");
            this.policies = VaultTokenRequestBuilder.toList(policies);
            return this;
        }

        public VaultTokenRequestBuilder withPolicy(String policy) {
            Assert.hasText((String)policy, (String)"Policy must not be empty");
            this.policies.add(policy);
            return this;
        }

        public VaultTokenRequestBuilder meta(Map<String, String> meta) {
            Assert.notNull(meta, (String)"Meta must not be null");
            this.meta = meta;
            return this;
        }

        public VaultTokenRequestBuilder noParent() {
            return this.noParent(true);
        }

        public VaultTokenRequestBuilder noParent(boolean noParent) {
            this.noParent = noParent;
            return this;
        }

        public VaultTokenRequestBuilder noDefaultPolicy() {
            return this.noDefaultPolicy(true);
        }

        public VaultTokenRequestBuilder noDefaultPolicy(boolean noDefaultPolicy) {
            this.noDefaultPolicy = noDefaultPolicy;
            return this;
        }

        public VaultTokenRequestBuilder renewable() {
            return this.renewable(true);
        }

        public VaultTokenRequestBuilder renewable(boolean renewable) {
            this.renewable = renewable;
            return this;
        }

        @Deprecated
        public VaultTokenRequestBuilder ttl(long ttl) {
            return this.ttl(ttl, TimeUnit.SECONDS);
        }

        public VaultTokenRequestBuilder ttl(long ttl, TimeUnit timeUnit) {
            Assert.isTrue((ttl >= 0L ? 1 : 0) != 0, (String)"TTL must not be negative");
            Assert.notNull((Object)((Object)timeUnit), (String)"TimeUnit must not be null");
            this.ttl = String.format("%ss", timeUnit.toSeconds(ttl));
            return this;
        }

        public VaultTokenRequestBuilder ttl(Duration ttl) {
            Assert.notNull((Object)ttl, (String)"TTL must not be null");
            Assert.isTrue((!ttl.isNegative() ? 1 : 0) != 0, (String)"TTL must not be negative");
            this.ttl = String.format("%ss", ttl.getSeconds());
            return this;
        }

        @Deprecated
        public VaultTokenRequestBuilder explicitMaxTtl(long explicitMaxTtl) {
            return this.explicitMaxTtl(explicitMaxTtl, TimeUnit.SECONDS);
        }

        public VaultTokenRequestBuilder explicitMaxTtl(long explicitMaxTtl, TimeUnit timeUnit) {
            Assert.isTrue((explicitMaxTtl >= 0L ? 1 : 0) != 0, (String)"TTL must not be negative");
            Assert.notNull((Object)((Object)timeUnit), (String)"TimeUnit must not be null");
            this.explicitMaxTtl = String.format("%ss", timeUnit.toSeconds(explicitMaxTtl));
            return this;
        }

        public VaultTokenRequestBuilder explicitMaxTtl(Duration explicitMaxTtl) {
            Assert.notNull((Object)explicitMaxTtl, (String)"Explicit max TTL must not be null");
            Assert.isTrue((!explicitMaxTtl.isNegative() ? 1 : 0) != 0, (String)"TTL must not be negative");
            this.explicitMaxTtl = String.format("%ss", explicitMaxTtl.getSeconds());
            return this;
        }

        public VaultTokenRequestBuilder numUses(int numUses) {
            Assert.isTrue((numUses >= 0 ? 1 : 0) != 0, (String)"Number of uses must not be negative");
            this.numUses = numUses;
            return this;
        }

        public VaultTokenRequestBuilder displayName(String displayName) {
            Assert.hasText((String)displayName, (String)"Display name must not be empty");
            this.displayName = displayName;
            return this;
        }

        public VaultTokenRequest build() {
            Map<String, String> meta;
            List<String> policies;
            switch (this.policies.size()) {
                case 0: {
                    policies = Collections.emptyList();
                    break;
                }
                case 1: {
                    policies = Collections.singletonList(this.policies.get(0));
                    break;
                }
                default: {
                    policies = Collections.unmodifiableList(new ArrayList<String>(this.policies));
                }
            }
            switch (this.meta.size()) {
                case 0: {
                    meta = Collections.emptyMap();
                    break;
                }
                default: {
                    meta = Collections.unmodifiableMap(new LinkedHashMap<String, String>(this.meta));
                }
            }
            return new VaultTokenRequest(this.id, policies, meta, this.noParent, this.noDefaultPolicy, this.renewable, this.ttl, this.explicitMaxTtl, this.displayName, this.numUses);
        }

        private static <E> List<E> toList(Iterable<E> iter) {
            ArrayList<E> list = new ArrayList<E>();
            for (E item : iter) {
                list.add(item);
            }
            return list;
        }
    }
}

