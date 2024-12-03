/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.index.api.LanguageDescriptor;
import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.QueryUtil;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class TextFieldQuery
implements SearchQuery {
    public static final String KEY = "textField";
    private final String fieldName;
    private final AnalyzerDescriptorProvider analyzerProvider;
    private final String rawQuery;
    private final BooleanOperator operator;

    public TextFieldQuery(String fieldName, String query, BooleanOperator operator) {
        this(fieldName, lang -> Optional.empty(), operator, query);
    }

    public TextFieldQuery(String fieldName, AnalyzerDescriptorProvider analyzerProvider, BooleanOperator operator, String query) {
        if (StringUtils.isBlank((CharSequence)fieldName)) {
            throw new IllegalArgumentException("Fieldname is required.");
        }
        if (StringUtils.isBlank((CharSequence)query)) {
            throw new IllegalArgumentException("Raw query is required.");
        }
        if (operator == null) {
            throw new IllegalArgumentException("Operator is required.");
        }
        this.rawQuery = query;
        this.fieldName = fieldName;
        this.operator = operator;
        this.analyzerProvider = analyzerProvider;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Arrays.asList(this.fieldName, this.rawQuery);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getRawQuery() {
        return QueryUtil.escape(this.rawQuery);
    }

    public String getUnescapedQuery() {
        return this.rawQuery;
    }

    public BooleanOperator getOperator() {
        return this.operator;
    }

    public Optional<MappingAnalyzerDescriptor> getAnalyzer(LanguageDescriptor language) {
        return Optional.ofNullable(this.analyzerProvider).map(provider -> provider.getAnalyzer(language)).orElse(Optional.empty());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TextFieldQuery)) {
            return false;
        }
        TextFieldQuery that = (TextFieldQuery)o;
        return Objects.equals(this.getFieldName(), that.getFieldName()) && Objects.equals(this.analyzerProvider, that.analyzerProvider) && Objects.equals(this.getRawQuery(), that.getRawQuery()) && this.getOperator() == that.getOperator();
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getFieldName(), this.analyzerProvider, this.getRawQuery(), this.getOperator()});
    }
}

