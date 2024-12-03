/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentStatus
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ContentEntityMetadataExtractor
implements Extractor2 {
    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ArrayList<FieldDescriptor> fields = new ArrayList<FieldDescriptor>();
        if (searchable instanceof ConfluenceEntityObject) {
            this.extractCreator((ConfluenceEntityObject)searchable, fields);
        }
        if (searchable instanceof ContentEntityObject) {
            ContentEntityObject contentEntityObject = (ContentEntityObject)searchable;
            this.extractExcerpt(contentEntityObject, fields);
            this.extractContentStatus(contentEntityObject, fields);
            if (contentEntityObject.getTypeEnum() == ContentTypeEnum.PAGE || contentEntityObject.getTypeEnum() == ContentTypeEnum.BLOG) {
                fields.add(SearchFieldMappings.CONTENT_NAME_UNSTEMMED.createField(contentEntityObject.getTitle()));
            }
            if (contentEntityObject.getTypeEnum() == ContentTypeEnum.PAGE && contentEntityObject.getVersionComment() != null) {
                fields.add(SearchFieldMappings.LAST_UPDATE_DESCRIPTION.createField(contentEntityObject.getVersionComment()));
            }
        }
        return fields;
    }

    private void extractContentStatus(ContentEntityObject ceo, Collection<FieldDescriptor> fields) {
        ContentStatus contentStatus = ceo.getContentStatusObject();
        if (contentStatus != null) {
            fields.add(SearchFieldMappings.CONTENT_STATUS.createField(contentStatus.serialise()));
        }
    }

    private void extractExcerpt(ContentEntityObject ceo, Collection<FieldDescriptor> fields) {
        String excerpt = ceo.getExcerpt();
        if (excerpt != null) {
            fields.add(SearchFieldMappings.EXCERPT.createField(excerpt));
        }
    }

    private void extractCreator(ConfluenceEntityObject ceo, Collection<FieldDescriptor> fields) {
        Optional.ofNullable(ceo.getCreator()).ifPresent(creator -> Optional.ofNullable(creator.getKey()).ifPresent(key -> fields.add(SearchFieldMappings.CREATOR.createField(key.getStringValue()))));
    }

    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }
}

