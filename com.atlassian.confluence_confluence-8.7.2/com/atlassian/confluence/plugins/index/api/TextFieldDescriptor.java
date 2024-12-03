/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldVisitor;
import com.atlassian.confluence.plugins.index.api.mapping.TextFieldMapping;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public final class TextFieldDescriptor
extends FieldDescriptor {
    private final TextFieldMapping mapping;

    public TextFieldDescriptor(TextFieldMapping mapping, String value) {
        super(mapping, value);
        this.mapping = mapping;
    }

    public TextFieldDescriptor(String name, String value, FieldDescriptor.Store store, AnalyzerDescriptorProvider analyzerProvider) {
        this(TextFieldDescriptor.createMapping(name, store, analyzerProvider), value);
    }

    private static TextFieldMapping createMapping(String name, FieldDescriptor.Store store, AnalyzerDescriptorProvider analyzerProvider) {
        if (StringUtils.isBlank((CharSequence)name)) {
            throw new IllegalArgumentException("name is required.");
        }
        if (store == null) {
            throw new IllegalArgumentException("store is required.");
        }
        return TextFieldMapping.builder(name).store(store.isStored()).analyzer(analyzerProvider).build();
    }

    @Override
    public FieldDescriptor.Index getIndex() {
        return FieldDescriptor.Index.ANALYZED;
    }

    public TextFieldDescriptor(String name, String value, FieldDescriptor.Store store) {
        this(name, value, store, AnalyzerDescriptorProvider.EMPTY);
    }

    @Override
    public <T> T accept(FieldVisitor<T> fieldVisitor) {
        return fieldVisitor.visit(this);
    }

    @Nonnull
    public AnalyzerDescriptorProvider getAnalyzerProvider() {
        return this.mapping.getAnalyzer();
    }
}

