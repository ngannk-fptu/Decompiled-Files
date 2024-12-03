/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.cql.rest.model;

import com.atlassian.confluence.plugins.cql.rest.model.FunctionValue;
import com.atlassian.confluence.plugins.cql.rest.model.QueryField;
import com.atlassian.confluence.plugins.cql.rest.model.QueryOperator;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class QueryExpression {
    @JsonProperty
    private final QueryField field;
    @JsonProperty
    private final QueryOperator operator;
    @JsonProperty
    private final List<String> values;
    @JsonProperty
    private final List<FunctionValue> functionValues;
    @JsonProperty
    private final boolean negate;

    public QueryExpression(QueryField field, QueryOperator op, Iterable<String> values, Iterable<FunctionValue> functionValues) {
        this(field, op, values, functionValues, false);
    }

    @JsonCreator
    private QueryExpression() {
        this(null, null, Collections.emptyList(), Collections.emptyList());
    }

    public QueryExpression(QueryField field, QueryOperator op, Iterable<String> values, Iterable<FunctionValue> functionValues, boolean negate) {
        this.field = field;
        this.operator = op;
        this.values = ImmutableList.copyOf((Iterable)Iterables.filter(values, (Predicate)Predicates.notNull()));
        this.functionValues = ImmutableList.copyOf((Iterable)Iterables.filter(functionValues, (Predicate)Predicates.notNull()));
        this.negate = negate;
    }

    public QueryField getField() {
        return this.field;
    }

    public QueryOperator getOperator() {
        return this.operator;
    }

    public Iterable<String> getValues() {
        return this.values;
    }

    public Iterable<FunctionValue> getFunctionValues() {
        return this.functionValues;
    }

    public boolean isNot() {
        return this.negate;
    }

    public QueryExpression negate() {
        return new QueryExpression(this.field, this.operator, this.values, this.functionValues, !this.negate);
    }
}

