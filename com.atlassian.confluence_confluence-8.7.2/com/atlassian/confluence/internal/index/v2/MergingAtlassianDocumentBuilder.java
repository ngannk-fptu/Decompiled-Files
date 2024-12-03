/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.index.v2;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.AtlassianDocumentBuilder;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class MergingAtlassianDocumentBuilder<T>
implements AtlassianDocumentBuilder<T> {
    private final Collection<AtlassianDocumentBuilder<T>> delegates;

    public MergingAtlassianDocumentBuilder(Collection<AtlassianDocumentBuilder<T>> delegates) {
        this.delegates = delegates;
    }

    @Override
    public AtlassianDocument build(T searchable) {
        return new AtlassianDocument(this.getFields(searchable));
    }

    private List<FieldDescriptor> getFields(T searchable) {
        return this.delegates.stream().flatMap(delegate -> delegate.build(searchable).getFields().stream()).collect(Collectors.toList());
    }
}

