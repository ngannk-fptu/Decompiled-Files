/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.StreamsFilterType
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.spi.StreamsFilterOption;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

final class StreamsFilterOptionImpl
implements StreamsFilterOption {
    private final String key;
    private final String displayName;
    private final String helpTextI18nKey;
    private final String i18nKey;
    private final StreamsFilterType type;
    private final boolean unique;
    private final boolean providerAlias;
    private final Map<String, String> values;

    StreamsFilterOptionImpl(StreamsFilterOption.Builder builder) {
        this.key = builder.getKey();
        this.displayName = builder.getDisplayName();
        this.helpTextI18nKey = builder.getHelpTextI18nKey();
        this.i18nKey = builder.getI18nKey();
        this.type = builder.getType();
        this.unique = builder.isUnique();
        this.providerAlias = builder.isProviderAlias();
        this.values = builder.getValues();
    }

    public static StreamsFilterOption.Builder builder(String key, StreamsFilterType type) {
        return new StreamsFilterOption.Builder(key, type);
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getDisplayName() {
        return StringUtils.isNotBlank((CharSequence)this.displayName) ? this.displayName : (StringUtils.isNotBlank((CharSequence)this.i18nKey) ? this.i18nKey : this.key);
    }

    @Override
    public String getHelpTextI18nKey() {
        return this.helpTextI18nKey;
    }

    @Override
    public String getI18nKey() {
        return this.i18nKey;
    }

    @Override
    public StreamsFilterType getFilterType() {
        return this.type;
    }

    @Override
    public boolean isUnique() {
        return this.unique;
    }

    @Override
    public boolean isProviderAlias() {
        return this.providerAlias;
    }

    @Override
    public Map<String, String> getValues() {
        return this.values;
    }
}

