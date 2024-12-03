/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.StreamsFilterType
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.spi.StreamsFilterOption;
import java.util.Map;

public enum StandardStreamsFilterOption implements StreamsFilterOption
{
    UPDATE_DATE("update-date", StreamsFilterType.DATE, "streams.filter.option.update.date", "Update Date", true),
    USER("user", StreamsFilterType.USER, "streams.filter.option.author", "Username", true, "streams.filter.option.help.author"),
    ISSUE_KEY("issue-key", StreamsFilterType.LIST, "streams.filter.option.issueKey", "Jira Issue Key", true, "streams.filter.option.help.issueKey");

    public static final String STANDARD_FILTERS_PROVIDER_KEY = "streams";
    public static final String ACTIVITY_KEY = "activity";
    public static final String ACTIVITY_OBJECT_VERB_SEPARATOR = ":";
    public static final String PROJECT_KEY = "key";
    public static final StreamsFilterType PROJECT_TYPE;
    private final StreamsFilterOption streamsFilterOption;

    private StandardStreamsFilterOption(String key, StreamsFilterType type, String i18nKey, String displayName, boolean unique) {
        this.streamsFilterOption = new StreamsFilterOption.Builder(key, type).displayName(displayName).i18nKey(i18nKey).unique(unique).build();
    }

    private StandardStreamsFilterOption(String key, StreamsFilterType type, String i18nKey, String displayName, boolean unique, String helpTextI18nKey) {
        this.streamsFilterOption = new StreamsFilterOption.Builder(key, type).helpTextI18nKey(helpTextI18nKey).displayName(displayName).i18nKey(i18nKey).unique(unique).build();
    }

    public static StreamsFilterOption projectKeys(Map<String, String> values, String product) {
        return new StreamsFilterOption.Builder(PROJECT_KEY, PROJECT_TYPE).displayName("Project").helpTextI18nKey("streams.filter.option.help.project." + product.toLowerCase()).i18nKey("streams.filter.option.project." + product.toLowerCase()).unique(true).values(values).build();
    }

    @Override
    public String getKey() {
        return this.streamsFilterOption.getKey();
    }

    @Override
    public String getDisplayName() {
        return this.streamsFilterOption.getDisplayName();
    }

    @Override
    public String getHelpTextI18nKey() {
        return this.streamsFilterOption.getHelpTextI18nKey();
    }

    @Override
    public String getI18nKey() {
        return this.streamsFilterOption.getI18nKey();
    }

    @Override
    public StreamsFilterType getFilterType() {
        return this.streamsFilterOption.getFilterType();
    }

    @Override
    public boolean isUnique() {
        return this.streamsFilterOption.isUnique();
    }

    @Override
    public boolean isProviderAlias() {
        return this.streamsFilterOption.isProviderAlias();
    }

    @Override
    public Map<String, String> getValues() {
        return this.streamsFilterOption.getValues();
    }

    static {
        PROJECT_TYPE = StreamsFilterType.SELECT;
    }
}

