/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.BaseApiEnum
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.cql.rest.model;

import com.atlassian.confluence.api.model.BaseApiEnum;
import com.atlassian.confluence.plugins.cql.rest.model.QueryOperator;
import com.atlassian.confluence.plugins.cql.rest.model.RestUiSupport;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class QueryField {
    @JsonProperty
    private final String fieldName;
    @JsonProperty
    private final FieldType type;
    @JsonProperty
    private final List<QueryOperator> supportedOps;
    @JsonProperty
    private final RestUiSupport uiSupport;

    @JsonCreator
    private QueryField() {
        this(QueryField.builder());
    }

    private QueryField(QueryFieldBuilder builder) {
        this.fieldName = builder.fieldName;
        this.type = builder.type;
        this.supportedOps = ImmutableList.copyOf(builder.operators);
        this.uiSupport = builder.uiSupport;
    }

    public static QueryFieldBuilder builder() {
        return new QueryFieldBuilder();
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public FieldType getType() {
        return this.type;
    }

    public List<QueryOperator> getSupportedOps() {
        return this.supportedOps;
    }

    public RestUiSupport getUiSupport() {
        return this.uiSupport;
    }

    public boolean equals(Object other) {
        if (!(other instanceof QueryField)) {
            return false;
        }
        QueryField otherField = (QueryField)other;
        return Objects.equals(this.fieldName, otherField.fieldName) && Objects.equals((Object)this.type, (Object)otherField.type) && Objects.equals(this.supportedOps, otherField.supportedOps) && Objects.equals(this.uiSupport, otherField.uiSupport);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.fieldName, this.type, this.supportedOps, this.uiSupport});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("fieldName", (Object)this.fieldName).add("type", (Object)this.type).add("supportedOps", this.supportedOps).add("uiSupport", (Object)this.uiSupport).toString();
    }

    public static class QueryFieldBuilder {
        private String fieldName;
        private FieldType type;
        private Iterable<QueryOperator> operators = ImmutableList.of();
        private RestUiSupport uiSupport;

        private QueryFieldBuilder() {
        }

        public QueryFieldBuilder name(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public QueryFieldBuilder type(FieldType type) {
            this.type = type;
            return this;
        }

        public QueryFieldBuilder supportedOps(Iterable<QueryOperator> operators) {
            this.operators = operators;
            return this;
        }

        public QueryFieldBuilder uiSupport(RestUiSupport uiSupport) {
            this.uiSupport = uiSupport;
            return this;
        }

        public QueryField build() {
            return new QueryField(this);
        }
    }

    public static final class FieldType
    extends BaseApiEnum {
        public static final FieldType TEXT = new FieldType("text");
        public static final FieldType EQUALITY = new FieldType("equality");
        public static final FieldType DATE = new FieldType("date");
        public static final FieldType NUMBER = new FieldType("number");
        private static final Iterable<FieldType> BUILT_IN = ImmutableList.of((Object)((Object)TEXT), (Object)((Object)EQUALITY), (Object)((Object)DATE), (Object)((Object)NUMBER));

        private FieldType(String value) {
            super(value);
        }

        @JsonCreator
        public static FieldType valueOf(String value) {
            for (FieldType fieldType : BUILT_IN) {
                if (!fieldType.getValue().equals(value)) continue;
                return fieldType;
            }
            return new FieldType(value);
        }
    }
}

