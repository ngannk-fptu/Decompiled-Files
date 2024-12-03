/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.macros.advanced.contentbylabel;

import com.atlassian.confluence.plugins.macros.advanced.contentbylabel.QueryExpression;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;

public class SimpleQueryExpression
implements QueryExpression {
    private final String key;
    private final InclusionOperator inclusionOperator;
    private final List<String> values;
    static Function<String, String> wrapWithQuotes = new Function<String, String>(){

        public String apply(String input) {
            if (input.equals("currentSpace()")) {
                return input;
            }
            return "\"" + input.replaceAll("\"", "\\\\\"") + "\"";
        }
    };

    private SimpleQueryExpression(String key, InclusionOperator inclusionOperator, List<String> values) {
        this.key = key;
        this.inclusionOperator = inclusionOperator;
        this.values = ImmutableList.copyOf(values);
    }

    private SimpleQueryExpression(String key, InclusionOperator inclusionOperator, String ... values) {
        this(key, inclusionOperator, Arrays.asList(values));
    }

    public String getKey() {
        return this.key;
    }

    public InclusionOperator getInclusionOperator() {
        return this.inclusionOperator;
    }

    public List<String> getValues() {
        return this.values;
    }

    @Override
    public String toQueryString() {
        boolean multiValue;
        boolean bl = multiValue = this.values.size() > 1;
        String operator = this.inclusionOperator == InclusionOperator.INCLUDES ? (multiValue ? "in" : "=") : (multiValue ? "not in" : "!=");
        String prefix = this.key + " " + operator + " ";
        if (!multiValue) {
            return prefix + (String)wrapWithQuotes.apply((Object)this.values.get(0));
        }
        List wrapped = Lists.transform(this.values, wrapWithQuotes);
        return prefix + "(" + Joiner.on((char)',').join((Iterable)wrapped) + ")";
    }

    public static SimpleQueryExpression of(String key, String ... values) {
        return new SimpleQueryExpression(key, InclusionOperator.INCLUDES, values);
    }

    public static SimpleQueryExpression of(String key, List<String> values) {
        return new SimpleQueryExpression(key, InclusionOperator.INCLUDES, values);
    }

    public static SimpleQueryExpression of(String key, InclusionOperator inclusionOperator, String ... values) {
        return new SimpleQueryExpression(key, inclusionOperator, values);
    }

    public static SimpleQueryExpression of(String key, InclusionOperator inclusionOperator, List<String> values) {
        return new SimpleQueryExpression(key, inclusionOperator, values);
    }

    public static enum InclusionOperator {
        INCLUDES,
        EXCLUDES;

    }
}

