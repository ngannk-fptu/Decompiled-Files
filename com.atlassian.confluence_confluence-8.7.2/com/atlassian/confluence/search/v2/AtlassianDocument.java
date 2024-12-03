/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.google.common.collect.ArrayListMultimap;
import java.util.Collection;
import java.util.Optional;

public class AtlassianDocument {
    private final ArrayListMultimap<String, FieldDescriptor> fields = ArrayListMultimap.create();

    public AtlassianDocument() {
    }

    public AtlassianDocument(Collection<FieldDescriptor> fields) {
        fields.forEach(field -> this.fields.put((Object)field.getName(), field));
    }

    public AtlassianDocument addField(FieldDescriptor field) {
        this.fields.put((Object)field.getName(), (Object)field);
        return this;
    }

    public AtlassianDocument addFields(Collection<FieldDescriptor> fields) {
        fields.forEach(field -> this.fields.put((Object)field.getName(), field));
        return this;
    }

    public Collection<FieldDescriptor> getFields() {
        return this.fields.values();
    }

    public Optional<String> getValue(String fieldName) {
        return this.fields.get((Object)fieldName).stream().findFirst().map(descriptor -> descriptor.getValue());
    }
}

