/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.confluence.plugins.cql.rest.model;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonValue;

@JsonIgnoreProperties(ignoreUnknown=true)
public class QueryOperator {
    @JsonProperty
    private final String value;
    @Deprecated
    public static final Function<String, QueryOperator> create = new Function<String, QueryOperator>(){

        public QueryOperator apply(String input) {
            return new QueryOperator(input);
        }
    };

    @JsonCreator
    private QueryOperator() {
        this.value = null;
    }

    public QueryOperator(String op) {
        this.value = (String)Preconditions.checkNotNull((Object)op);
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    public boolean equals(Object other) {
        return other instanceof QueryOperator && this.value.equals(((QueryOperator)other).value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }
}

