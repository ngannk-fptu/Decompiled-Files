/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.StreamsFilterType
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.spi.StreamsFilterOptionImpl;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public interface StreamsFilterOption {
    public String getKey();

    public String getDisplayName();

    public String getHelpTextI18nKey();

    public String getI18nKey();

    public StreamsFilterType getFilterType();

    public boolean isUnique();

    public boolean isProviderAlias();

    public Map<String, String> getValues();

    public static class Builder {
        private String key;
        private String displayName;
        private String helpTextI18nKey;
        private String i18nKey;
        private StreamsFilterType type;
        private boolean unique = true;
        private boolean providerAlias = false;
        private Map<String, String> values;

        public Builder(String key, StreamsFilterType type) {
            this.key = (String)Preconditions.checkNotNull((Object)key, (Object)"key");
            this.type = (StreamsFilterType)Preconditions.checkNotNull((Object)type, (Object)"type");
            this.values = ImmutableMap.of();
        }

        public Builder helpTextI18nKey(String helpTextI18nKey) {
            this.helpTextI18nKey = helpTextI18nKey;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder i18nKey(String i18nKey) {
            this.i18nKey = i18nKey;
            return this;
        }

        public Builder unique(boolean unique) {
            this.unique = unique;
            return this;
        }

        public Builder providerAlias(boolean providerAlias) {
            this.providerAlias = providerAlias;
            return this;
        }

        public Builder values(Map<String, String> values) {
            this.values = ImmutableMap.copyOf((Map)((Map)Preconditions.checkNotNull(values, (Object)("Values are null for key: " + this.key + " and name: " + this.displayName))));
            return this;
        }

        public StreamsFilterOption build() {
            return new StreamsFilterOptionImpl(this);
        }

        String getKey() {
            return this.key;
        }

        String getHelpTextI18nKey() {
            return this.helpTextI18nKey;
        }

        String getDisplayName() {
            return this.displayName;
        }

        String getI18nKey() {
            return this.i18nKey;
        }

        StreamsFilterType getType() {
            return this.type;
        }

        boolean isUnique() {
            return this.unique;
        }

        boolean isProviderAlias() {
            return this.providerAlias;
        }

        Map<String, String> getValues() {
            return this.values;
        }
    }
}

