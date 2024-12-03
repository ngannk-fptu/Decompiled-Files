/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SearchPrimitive
public class PhraseQuery
implements SearchQuery {
    public static final String KEY = "phrase";
    private final String fieldName;
    private final String text;
    private final int slop;
    private final AnalyzerDescriptorProvider analyzerDescriptorProvider;
    private final float boost;

    public PhraseQuery(String fieldName, String text, int slop, AnalyzerDescriptorProvider analyzerDescriptorProvider, float boost) {
        Preconditions.checkNotNull((Object)analyzerDescriptorProvider);
        this.fieldName = fieldName;
        this.analyzerDescriptorProvider = analyzerDescriptorProvider;
        this.slop = slop;
        this.text = text;
        this.boost = boost;
    }

    public PhraseQuery(String fieldName, String text, int slop, AnalyzerDescriptorProvider analyzerDescriptorProvider) {
        this(fieldName, text, slop, analyzerDescriptorProvider, 1.0f);
    }

    public PhraseQuery(String fieldName, String text, int slop) {
        this(fieldName, text, slop, AnalyzerDescriptorProvider.EMPTY);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public int getSlop() {
        return this.slop;
    }

    public AnalyzerDescriptorProvider getAnalyzerDescriptorProvider() {
        return this.analyzerDescriptorProvider;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<?> getParameters() {
        return Arrays.asList(this.fieldName, this.text, this.slop, this.analyzerDescriptorProvider);
    }

    public String getText() {
        return this.text;
    }

    @Override
    public float getBoost() {
        return this.boost;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PhraseQuery)) {
            return false;
        }
        PhraseQuery that = (PhraseQuery)o;
        return this.getSlop() == that.getSlop() && Objects.equals(this.getFieldName(), that.getFieldName()) && Objects.equals(this.getText(), that.getText()) && Objects.equals(this.getAnalyzerDescriptorProvider(), that.getAnalyzerDescriptorProvider()) && this.getBoost() == that.getBoost();
    }

    public int hashCode() {
        return Objects.hash(this.getFieldName(), this.getText(), this.getSlop(), this.getAnalyzerDescriptorProvider(), Float.valueOf(this.getBoost()));
    }
}

