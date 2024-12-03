/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.Severity;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class AlertCriteria {
    private static final int MAX_VALUES_IN_FIELD = 150;
    private static final Function<String, String> TO_UPPER = val -> StringUtils.upperCase((String)val, (Locale)Locale.ROOT);
    private final Set<String> componentIds;
    private final Set<String> issueIds;
    private final Set<String> nodeNames;
    private final Set<String> pluginKeys;
    private final Set<Severity> severities;
    private final Instant since;
    private final Instant until;

    private AlertCriteria(Builder builder) {
        this.componentIds = AlertCriteria.checkMaxSizeNotExceeded(builder.componentIds.build(), "component IDs");
        this.issueIds = AlertCriteria.checkMaxSizeNotExceeded(builder.issueIds.build(), "issue IDs");
        this.nodeNames = AlertCriteria.checkMaxSizeNotExceeded(builder.nodes.build(), "node names");
        this.pluginKeys = AlertCriteria.checkMaxSizeNotExceeded(builder.pluginKeys.build(), "plugin keys");
        this.severities = AlertCriteria.checkMaxSizeNotExceeded(builder.severities.build(), "severities");
        this.since = builder.since;
        this.until = builder.until;
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public Set<String> getComponentIds() {
        return this.componentIds;
    }

    @Nonnull
    public Set<String> getIssueIds() {
        return this.issueIds;
    }

    @Nonnull
    public Set<String> getNodeNames() {
        return this.nodeNames;
    }

    @Nonnull
    public Set<String> getPluginKeys() {
        return this.pluginKeys;
    }

    @Nonnull
    public Set<Severity> getSeverities() {
        return this.severities;
    }

    @Nonnull
    public Optional<Instant> getSince() {
        return Optional.ofNullable(this.since);
    }

    @Nonnull
    public Optional<Instant> getUntil() {
        return Optional.ofNullable(this.until);
    }

    private static <T> Set<T> checkMaxSizeNotExceeded(Set<T> values, String name) {
        if (values.size() > 150) {
            throw new IllegalArgumentException("No more than 150 " + name + " can be provided (was " + values.size() + ")");
        }
        return values;
    }

    public static class Builder {
        private final ImmutableSet.Builder<String> componentIds = ImmutableSet.builder();
        private final ImmutableSet.Builder<String> issueIds = ImmutableSet.builder();
        private final ImmutableSet.Builder<String> nodes = ImmutableSet.builder();
        private final ImmutableSet.Builder<String> pluginKeys = ImmutableSet.builder();
        private final ImmutableSet.Builder<Severity> severities = ImmutableSet.builder();
        private Instant since;
        private Instant until;

        public Builder() {
        }

        public Builder(@Nonnull AlertCriteria other) {
            this();
            Objects.requireNonNull(other, "other");
            this.componentIds.addAll((Iterable)other.componentIds);
            this.issueIds.addAll((Iterable)other.issueIds);
            this.nodes.addAll((Iterable)other.nodeNames);
            this.pluginKeys.addAll((Iterable)other.pluginKeys);
            this.severities.addAll((Iterable)other.severities);
            this.since = other.since;
            this.until = other.until;
        }

        @Nonnull
        public AlertCriteria build() {
            return new AlertCriteria(this);
        }

        @Nonnull
        public Builder componentIds(String value, String ... moreValues) {
            Builder.addIf(StringUtils::isNotBlank, TO_UPPER, this.componentIds, value, moreValues);
            return this;
        }

        @Nonnull
        public Builder componentIds(Iterable<String> values) {
            Builder.addIf(StringUtils::isNotBlank, TO_UPPER, this.componentIds, values);
            return this;
        }

        @Nonnull
        public Builder issueIds(String value, String ... moreValues) {
            Builder.addIf(StringUtils::isNotBlank, TO_UPPER, this.issueIds, value, moreValues);
            return this;
        }

        @Nonnull
        public Builder issueIds(Iterable<String> values) {
            Builder.addIf(StringUtils::isNotBlank, TO_UPPER, this.issueIds, values);
            return this;
        }

        @Nonnull
        public Builder nodeNames(String value, String ... moreValues) {
            Builder.addIf(StringUtils::isNotBlank, this.nodes, value, moreValues);
            return this;
        }

        @Nonnull
        public Builder nodeNames(Iterable<String> values) {
            Builder.addIf(StringUtils::isNotBlank, this.nodes, values);
            return this;
        }

        @Nonnull
        public Builder pluginKeys(String value, String ... moreValues) {
            Builder.addIf(StringUtils::isNotBlank, this.pluginKeys, value, moreValues);
            return this;
        }

        @Nonnull
        public Builder pluginKeys(Iterable<String> values) {
            Builder.addIf(StringUtils::isNotBlank, this.pluginKeys, values);
            return this;
        }

        @Nonnull
        public Builder severities(Severity value, Severity ... moreValues) {
            Builder.addIf(Objects::nonNull, this.severities, value, moreValues);
            return this;
        }

        @Nonnull
        public Builder severities(Iterable<Severity> values) {
            Builder.addIf(Objects::nonNull, this.severities, values);
            return this;
        }

        @Nonnull
        public Builder since(Instant value) {
            this.since = value;
            return this;
        }

        @Nonnull
        public Builder until(Instant value) {
            this.until = value;
            return this;
        }

        @SafeVarargs
        private static <T> void addIf(Predicate<T> filter, ImmutableSet.Builder<T> builder, T value, T ... moreValues) {
            Builder.addIf(filter, Function.identity(), builder, value, moreValues);
        }

        @SafeVarargs
        private static <T> void addIf(Predicate<T> filter, Function<T, T> transform, ImmutableSet.Builder<T> builder, T value, T ... moreValues) {
            if (filter.test(value)) {
                builder.add(transform.apply(value));
            }
            for (T val : moreValues) {
                if (!filter.test(val)) continue;
                builder.add(transform.apply(val));
            }
        }

        private static <T> void addIf(Predicate<T> filter, ImmutableSet.Builder<T> builder, Iterable<T> values) {
            Builder.addIf(filter, Function.identity(), builder, values);
        }

        private static <T> void addIf(Predicate<T> filter, Function<T, T> transform, ImmutableSet.Builder<T> builder, Iterable<T> values) {
            if (values != null) {
                for (T value : values) {
                    if (!filter.test(value)) continue;
                    builder.add(transform.apply(value));
                }
            }
        }
    }
}

