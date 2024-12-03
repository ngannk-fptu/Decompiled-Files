/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.StreamsFilterType$Operator
 *  com.atlassian.streams.spi.StreamsFilterOption
 *  com.google.common.base.Function
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.streams.internal.rest.representations;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.StreamsFilterType;
import com.atlassian.streams.spi.StreamsFilterOption;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class FilterOptionRepresentation {
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String helpText;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final Collection<FilterOptionOperatorRepresentation> operators;
    @JsonProperty
    private final String type;
    @JsonProperty
    private final boolean unique;
    @JsonProperty
    private final Map<String, String> values;

    @JsonCreator
    public FilterOptionRepresentation(@JsonProperty(value="key") String key, @JsonProperty(value="helpText") String helpText, @JsonProperty(value="name") String name, @JsonProperty(value="operators") Collection<FilterOptionOperatorRepresentation> operators, @JsonProperty(value="type") String type, @JsonProperty(value="unique") boolean unique, @JsonProperty(value="values") Map<String, String> values) {
        this.key = key;
        this.helpText = helpText;
        this.name = name;
        this.operators = ImmutableList.copyOf(operators);
        this.type = type;
        this.unique = unique;
        this.values = ImmutableMap.copyOf(values);
    }

    public FilterOptionRepresentation(I18nResolver i18nResolver, StreamsFilterOption filterOption) {
        this.key = filterOption.getKey();
        this.helpText = StringUtils.isNotBlank((CharSequence)filterOption.getHelpTextI18nKey()) ? i18nResolver.getText(filterOption.getHelpTextI18nKey()) : null;
        this.name = StringUtils.isNotBlank((CharSequence)filterOption.getI18nKey()) ? i18nResolver.getText(filterOption.getI18nKey()) : filterOption.getDisplayName();
        this.operators = Collections2.transform((Collection)ImmutableList.copyOf((Iterable)filterOption.getFilterType().getOperators()), FilterOptionRepresentation.toOperatorRepresentation(i18nResolver));
        this.type = filterOption.getFilterType().getType();
        this.unique = filterOption.isUnique();
        this.values = filterOption.getValues() != null ? ImmutableMap.copyOf((Map)filterOption.getValues()) : null;
    }

    public String getKey() {
        return this.key;
    }

    public String getHelpText() {
        return this.helpText;
    }

    public String getName() {
        return this.name;
    }

    public Collection<FilterOptionOperatorRepresentation> getOperators() {
        return this.operators;
    }

    public String getType() {
        return this.type;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public Map<String, String> getValues() {
        return this.values;
    }

    public String toString() {
        return this.getKey();
    }

    static Function<StreamsFilterType.Operator, FilterOptionOperatorRepresentation> toOperatorRepresentation(final I18nResolver i18nResolver) {
        return new Function<StreamsFilterType.Operator, FilterOptionOperatorRepresentation>(){

            public FilterOptionOperatorRepresentation apply(StreamsFilterType.Operator operator) {
                return new FilterOptionOperatorRepresentation(operator.getKey(), i18nResolver.getText(operator.getI18nKey()));
            }
        };
    }

    public static Function<StreamsFilterOption, FilterOptionRepresentation> toFilterOptionEntry(final I18nResolver i18nResolver) {
        return new Function<StreamsFilterOption, FilterOptionRepresentation>(){

            public FilterOptionRepresentation apply(StreamsFilterOption filterOption) {
                return new FilterOptionRepresentation(i18nResolver, filterOption);
            }
        };
    }

    public static class FilterOptionOperatorRepresentation {
        @JsonProperty
        private final String key;
        @JsonProperty
        private final String name;

        @JsonCreator
        public FilterOptionOperatorRepresentation(@JsonProperty(value="key") String key, @JsonProperty(value="name") String name) {
            this.key = key;
            this.name = name;
        }

        public String getKey() {
            return this.key;
        }

        public String getName() {
            return this.name;
        }
    }

    static enum ToKey implements Function<StreamsFilterType.Operator, String>
    {
        INSTANCE;


        public String apply(StreamsFilterType.Operator operator) {
            return operator.getKey();
        }
    }
}

