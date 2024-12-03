/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.api.healthcheck;

import com.atlassian.troubleshooting.api.healthcheck.exception.InvalidHealthCheckFilterException;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonProperty;

public class HealthCheckFilter {
    public static final HealthCheckFilter ALL = HealthCheckFilter.builder().build();
    @JsonProperty
    private final Set<String> keys;
    @JsonProperty
    private final Set<String> tags;

    private HealthCheckFilter(Builder builder) {
        if (HealthCheckFilter.bothKeysAndTagsDefined(builder.keys, builder.tags)) {
            throw new InvalidHealthCheckFilterException("Providing both key/s and tag/s is not valid. Please provide one or the other.");
        }
        this.keys = ImmutableSet.copyOf((Collection)builder.keys);
        this.tags = ImmutableSet.copyOf((Collection)builder.tags);
    }

    public static HealthCheckFilter withKeys(String ... keys) {
        return HealthCheckFilter.builder().keys(Sets.newHashSet((Object[])keys)).build();
    }

    public static HealthCheckFilter withTags(String ... tags) {
        return HealthCheckFilter.builder().tags(Sets.newHashSet((Object[])tags)).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    private static boolean bothKeysAndTagsDefined(Set<String> keys, Set<String> tags) {
        return keys != null && !keys.isEmpty() && tags != null && !tags.isEmpty();
    }

    public Set<String> getKeys() {
        return this.keys;
    }

    public Set<String> getTags() {
        return this.tags;
    }

    public static class Builder {
        private Set<String> keys = ImmutableSet.of();
        private Set<String> tags = ImmutableSet.of();

        private Builder() {
        }

        public Builder keys(Set<String> keys) {
            this.keys = keys;
            return this;
        }

        public Builder tags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public HealthCheckFilter build() {
            return new HealthCheckFilter(this);
        }
    }
}

