/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.bonnie.HandleResolver
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.core.util.ObjectUtils
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Handle;
import com.atlassian.bonnie.HandleResolver;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.ContentTypeAware;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.internal.search.ChangeDocumentIdBuilder;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.StringFieldMapping;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.SearchResultType;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.core.util.ObjectUtils;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Internal
public class LuceneChangeExtractor
implements Extractor2 {
    private final HandleResolver handleResolver;
    private final ChangeDocumentIdBuilder changeDocumentIdBuilder;

    public LuceneChangeExtractor(HandleResolver handleResolver) {
        this.handleResolver = Objects.requireNonNull(handleResolver);
        this.changeDocumentIdBuilder = new ChangeDocumentIdBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object object) {
        if (!(object instanceof Searchable)) {
            return Collections.emptyList();
        }
        ImmutableList.Builder fieldDescriptors = ImmutableList.builder();
        fieldDescriptors.add((Object)SearchFieldMappings.DOCUMENT_TYPE.createField(SearchResultType.CHANGE.name()));
        Searchable searchable = (Searchable)object;
        Handle handle = this.getHandle(searchable);
        if (handle == null) {
            throw new IllegalArgumentException("Cannot convert " + searchable + " into a valid handle.");
        }
        fieldDescriptors.add((Object)SearchFieldMappings.HANDLE.createField(handle.toString()));
        fieldDescriptors.add((Object)SearchFieldMappings.CONTENT_ID.createField(searchable.getId()));
        fieldDescriptors.add((Object)SearchFieldMappings.CLASS_NAME.createField(ObjectUtils.getTrueClass((Object)searchable).getName()));
        fieldDescriptors.add((Object)SearchFieldMappings.TYPE.createField(((ContentTypeAware)searchable).getType()));
        if (searchable instanceof Versioned) {
            Versioned latestVersion = ((Versioned)searchable).getLatestVersion();
            Searchable latestSearchable = (Searchable)latestVersion;
            if (latestVersion instanceof EntityObject) {
                fieldDescriptors.add((Object)SearchFieldMappings.LATEST_VERSION_ID.createField(String.valueOf(((EntityObject)latestVersion).getId())));
            }
            fieldDescriptors.add((Object)Mappings.CHANGE_DOCUMENT_AND_AUTHOR_ID.createField(this.changeDocumentIdBuilder.getChangeDocumentAndAuthorId(searchable)));
            fieldDescriptors.add((Object)Mappings.CHANGE_DOCUMENT_GROUP_ID.createField(this.changeDocumentIdBuilder.getGroupId(latestSearchable)));
        }
        return fieldDescriptors.build();
    }

    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    private Handle getHandle(Object obj) {
        return this.handleResolver.getHandle(obj);
    }

    public static class Mappings {
        public static final StringFieldMapping CHANGE_DOCUMENT_GROUP_ID = StringFieldMapping.builder("change-document-group-id").build();
        public static final StringFieldMapping CHANGE_DOCUMENT_AND_AUTHOR_ID = StringFieldMapping.builder("change-document-and-author-id").build();
    }
}

