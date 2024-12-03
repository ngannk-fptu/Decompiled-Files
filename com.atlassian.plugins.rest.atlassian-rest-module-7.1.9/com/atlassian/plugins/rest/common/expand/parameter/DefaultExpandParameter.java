/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.rest.common.expand.parameter;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.parameter.ChainingExpandParameter;
import com.atlassian.plugins.rest.common.expand.parameter.ExpandParameter;
import com.atlassian.plugins.rest.common.expand.parameter.IndexParser;
import com.atlassian.plugins.rest.common.expand.parameter.Indexes;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public final class DefaultExpandParameter
implements ExpandParameter {
    private static final String DOT = ".";
    private static final String COMMA = ",";
    private static final String WILDCARD = "*";
    private static final ExpandParameter EMPTY_EXPAND_PARAMETER = new DefaultExpandParameter((String)null);
    @TenantAware(value=TenancyScope.SUPPRESS)
    private final Map<String, ExpandInformation> parameters;

    private DefaultExpandParameter(String expand) {
        this(StringUtils.isNotBlank((CharSequence)expand) ? Collections.singleton(expand) : Collections.emptyList());
    }

    private DefaultExpandParameter() {
        this.parameters = Maps.newHashMap();
    }

    public DefaultExpandParameter(Collection<String> expands) {
        this.parameters = DefaultExpandParameter.parse(expands != null ? expands : Collections.emptyList());
    }

    @Override
    public boolean shouldExpand(Expandable expandable) {
        return this.parameters.containsKey(WILDCARD) || this.parameters.containsKey(Objects.requireNonNull(expandable).value());
    }

    @Override
    public Indexes getIndexes(Expandable expandable) {
        ExpandInformation expandInformation = this.parameters.get(Objects.requireNonNull(expandable).value());
        return expandInformation != null ? expandInformation.getIndexes() : IndexParser.EMPTY;
    }

    @Override
    public ExpandParameter getExpandParameter(Expandable expandable) {
        ExpandInformation wildcardExpandInformation = this.parameters.get(WILDCARD);
        ExpandInformation valueExpandInformation = this.parameters.get(Objects.requireNonNull(expandable).value());
        return new ChainingExpandParameter(wildcardExpandInformation != null ? wildcardExpandInformation.getExpandParameter() : EMPTY_EXPAND_PARAMETER, valueExpandInformation != null ? valueExpandInformation.getExpandParameter() : EMPTY_EXPAND_PARAMETER);
    }

    @Override
    public boolean isEmpty() {
        return this.parameters.isEmpty();
    }

    private static void appendParam(Map<String, ExpandInformation> parameters, String expand) {
        ExpandKey key = ExpandKey.from(StringUtils.substringBefore((String)expand, (String)DOT));
        String newParameter = StringUtils.substringAfter((String)expand, (String)DOT);
        DefaultExpandParameter existingParameter = null;
        if (parameters.containsKey(key.getName())) {
            existingParameter = parameters.get(key.getName()).getExpandParameter();
        } else {
            existingParameter = new DefaultExpandParameter();
            parameters.put(key.getName(), new ExpandInformation(key.getIndexes(), existingParameter));
        }
        if (StringUtils.isNotBlank((CharSequence)newParameter)) {
            DefaultExpandParameter.appendParam(existingParameter.parameters, newParameter);
        }
    }

    private static Map<String, ExpandInformation> parse(Collection<String> expands) {
        HashMap parameters = Maps.newHashMap();
        for (String expand : DefaultExpandParameter.preProcess(expands)) {
            if (!StringUtils.isNotEmpty((CharSequence)expand)) continue;
            DefaultExpandParameter.appendParam(parameters, expand);
        }
        return parameters;
    }

    private static Collection<String> preProcess(Collection<String> expands) {
        HashSet<String> preProcessed = new HashSet<String>();
        for (String expand : expands) {
            preProcessed.addAll(Sets.newHashSet((Object[])expand.split(COMMA)));
        }
        return preProcessed;
    }

    private static class ExpandInformation {
        private final Indexes indexes;
        private final DefaultExpandParameter expandParameter;

        public ExpandInformation(Indexes indexes, DefaultExpandParameter expandParameter) {
            this.indexes = Objects.requireNonNull(indexes);
            this.expandParameter = Objects.requireNonNull(expandParameter);
        }

        public Indexes getIndexes() {
            return this.indexes;
        }

        public DefaultExpandParameter getExpandParameter() {
            return this.expandParameter;
        }
    }

    private static class ExpandKey {
        private static final Pattern KEY_PATTERN = Pattern.compile("(\\w+|\\*)(?:\\[([\\d:\\-\\|]+)\\])?");
        private final String name;
        private final Indexes indexes;

        ExpandKey(String name, Indexes indexes) {
            this.name = name;
            this.indexes = indexes;
        }

        public String getName() {
            return this.name;
        }

        public Indexes getIndexes() {
            return this.indexes;
        }

        private static ExpandKey from(String key) {
            Matcher keyMatcher = KEY_PATTERN.matcher(key);
            if (!keyMatcher.matches()) {
                throw new RuntimeException("key <" + key + "> doesn't match pattern");
            }
            String name = keyMatcher.group(1);
            String indexesString = keyMatcher.group(2);
            return new ExpandKey(name, IndexParser.parse(indexesString));
        }
    }
}

